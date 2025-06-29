package com.algo.trading.market.tick;

import com.algo.trading.common.model.dto.DepthDTO;
import com.algo.trading.common.model.dto.TickDTO;
import com.algo.trading.common.model.enums.OrderSide;
import com.algo.trading.market.config.TickStreamProperties;
import com.algo.trading.market.event.WsConnected;
import com.algo.trading.market.event.WsDisconnected;
import com.algo.trading.market.event.WsRetryLimitReached;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Depth;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class KiteTickerClient {

    private final KiteTicker websocket;
    private final TickProducer producer;
    private final TickStreamProperties tickStreamConfig;

    private final ApplicationEventPublisher publisher;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Counter reconnects = Metrics.counter("ws.reconnects");

    @Value("${tick.max-retries:5}")
    private int maxRetries;

    private final AtomicInteger retryCount = new AtomicInteger(0);

    @PostConstruct
    void start() {
        connect();
    }

    /**
     * Called by TokenExpiryListener when a new session is generated.
     */
    public void forceReconnect() {
        try {
            log.info("Closing WS so it reconnects with fresh token");

            if (websocket.isConnectionOpen()) {
                websocket.disconnect();
            }
        } catch (Exception ignored) {
            // next scheduleReconnect() will handle retry
        }
    }

    private void connect() {
        ArrayList<Long> tokens = new ArrayList<>(tickStreamConfig.getTokens());

        websocket.setOnConnectedListener(handleOnConnectConfigs(tokens));
        websocket.setOnDisconnectedListener(handleDisconnection());
        websocket.setOnErrorListener(handleErrors());
        websocket.setOnTickerArrivalListener(handleTicks());
        websocket.setOnOrderUpdateListener(handleOrderUpdate());

        try {
            websocket.connect();

            boolean isConnected = websocket.isConnectionOpen();

            if (isConnected) {
                log.info("Websocket connection open: {}", true);
            } else {
                log.error("Websocket connection FAILED to open: {}", false);
                scheduleReconnect();
            }
        } catch (Exception ex) {
            log.error("Initial Websocket connection FAILED – maybe token not ready yet, will retry", ex);
            scheduleReconnect();
        }
    }

    /**
     * Subscribe ticks for token. By default, all tokens are subscribed for modeQuote.
     */
    private OnConnect handleOnConnectConfigs(ArrayList<Long> instrumentTokens) {
        return () -> {
            retryCount.set(0);
            websocket.subscribe(instrumentTokens);
            websocket.setMode(instrumentTokens, KiteTicker.modeFull);

            publisher.publishEvent(new WsConnected(this));

            log.info("WebSocket connected & subscribed: {}", instrumentTokens);
        };
    }

    private OnDisconnect handleDisconnection() {
        log.warn("WS disconnected; reconnecting in {}", tickStreamConfig.getReconnectDelay());

        publisher.publishEvent(new WsDisconnected(this));

        scheduleReconnect();
        return null;
    }

    private void scheduleReconnect() {
        int attempt = retryCount.incrementAndGet();
        if (attempt > maxRetries) {
            log.error("Max reconnect attempts ({}) reached – giving up.", maxRetries);
            stopRetries();
            return;
        }

        reconnects.increment();

        log.info("Scheduling reconnect attempt {}/{} in {}", attempt, maxRetries, tickStreamConfig.getReconnectDelay());
        scheduler.schedule(this::connect, tickStreamConfig.getReconnectDelay().toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Cancels further reconnect attempts and detaches listeners.
     */
    private void stopRetries() {
        try {
            scheduler.shutdownNow();
        } catch (Exception ignored) {
            //handle here.
        }

        log.info("Websocket retries exhausted: Removing listeners so KiteTicker stops calling it repeatedly");
        websocket.setOnDisconnectedListener(null);
        websocket.setOnErrorListener(null);

        publisher.publishEvent(new WsRetryLimitReached(this));
    }

    private OnError handleErrors() {
        return new OnError() {
            @Override
            public void onError(Exception e) {
                log.error("Exception occurred : ");
                //handle here.
            }

            @Override
            public void onError(KiteException e) {
                log.error("KiteException occurred : ");
                //handle here.
            }

            @Override
            public void onError(String error) {
                log.error("String error occurred : {}", error);
                //handle here.
            }
        };
    }

    /**
     * Process live market data
     */
    private OnTicks handleTicks() {
        {
            return ticks -> {
                log.info("Ticks size: {}", ticks.size());

                ticks.forEach(tick -> {
                    List<DepthDTO> depth = buildDepth(tick);

                    // TODO - Recheck this logic
                    double bestBid = depth.stream()
                            .filter(depthDTO -> depthDTO.getSide() == OrderSide.BUY)
                            .findFirst()
                            .map(DepthDTO::getPrice)
                            .orElse(0.0);

                    double bestAsk = depth.stream()
                            .filter(depthDTO -> depthDTO.getSide() == OrderSide.SELL)
                            .findFirst()
                            .map(DepthDTO::getPrice)
                            .orElse(0.0);

                    TickDTO tickDTO = TickDTO.builder()
                            .instrumentToken(tick.getInstrumentToken())
                            .epochMs(tick.getTickTimestamp().getTime())
                            .lastPrice(tick.getLastTradedPrice())
                            .volume(tick.getVolumeTradedToday())
                            .bestBid(bestBid)
                            .bestAsk(bestAsk)
                            .depthDTO(depth)
                            .build();

                    producer.publish(tickDTO);
                });
            };
        }
    }

    private static List<DepthDTO> buildDepth(Tick tick) {
        List<DepthDTO> list = new ArrayList<>(10);

        List<Depth> buys = tick.getMarketDepth().get("buy");
        List<Depth> sells = tick.getMarketDepth().get("sell");

        if (buys != null) {
            buys.forEach(depth -> list.add(toDto(depth, OrderSide.BUY)));
        }
        if (sells != null) {
            sells.forEach(depth -> list.add(toDto(depth, OrderSide.SELL)));
        }

        return list;
    }

    private static DepthDTO toDto(Depth depth, OrderSide side) {
        return DepthDTO.builder()
                .side(side)
                .price(depth.getPrice())
                .quantity(depth.getQuantity())
                .orderCount(depth.getOrders())
                .build();
    }

    private OnOrderUpdate handleOrderUpdate() {
        // TODO - need to think about this part
        return null;
    }
}
