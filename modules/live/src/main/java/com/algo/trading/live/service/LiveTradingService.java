package com.algo.trading.live.service;

import com.algo.trading.auth.service.KiteService;
import com.algo.trading.live.config.LiveProperties;
import com.algo.trading.indicators.service.IndicatorService;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.KiteTicker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveTradingService {

    private final LiveProperties props;
    private final IndicatorService indicatorService;
    private final KiteService kiteService;

    private KiteConnect kiteConnect;
    private final BarSeries series = new BaseBarSeriesBuilder()
            .withName("live_series")
            .build();
    private final AtomicBoolean running = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        this.kiteConnect = kiteService.getAuthenticatedClient();
        log.info("Live trading initialized for user {}", kiteConnect.getUserId());
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            log.warn("Live trading already running");
            return;
        }
        log.info("Starting live trading on {}", props.getInstruments());

        // TODO: subscribe to WebSocket, build bars, compute signals, place orders...

        // 1) Copy instrument tokens into a mutable list
        ArrayList<Long> tokens = new ArrayList<>(props.getInstruments());

        // 2) Initialize WebSocket ticker
        KiteTicker ticker = new KiteTicker(
                kiteConnect.getApiKey(),
                kiteConnect.getAccessToken()
        );
        ticker.setTryReconnection(true);
        try {
            ticker.setMaximumRetries(-1);                        // infinite retries
            ticker.setMaximumRetryInterval(props.getReconnectIntervalMs());
        } catch (KiteException e) {
            throw new RuntimeException(e);
        }

        // 3) Subscribe + set full mode on connect
        ticker.setOnConnectedListener(() -> {
            ticker.subscribe(tokens);
            ticker.setMode(tokens, KiteTicker.modeFull);
            log.info("WebSocket connected & subscribed: {}", tokens);
        });

        // 4) Build up 1-minute bars and check SMA(10,30) crossover
        AtomicReference<Instant> barTime = new AtomicReference<>();
        AtomicReference<Double> open = new AtomicReference<>();
        AtomicReference<Double> high = new AtomicReference<>();
        AtomicReference<Double> low = new AtomicReference<>();
        AtomicReference<Double> close = new AtomicReference<>();
        AtomicReference<Double> volume = new AtomicReference<>(0.0);

        ticker.setOnTickerArrivalListener(ticks -> {
            if (ticks.isEmpty()) return;

            // just take the first tick in this batch
            Tick tick = ticks.getFirst();

            Instant now = tick.getTickTimestamp().toInstant();
            Instant minute = now.truncatedTo(ChronoUnit.MINUTES);

            boolean isNewBar = barTime.get() == null || !barTime.get().equals(minute);
            if (isNewBar) {
                // close previous bar
                if (barTime.get() != null) {
                    Bar finishedBar = new BaseBar(
                            Duration.ofMinutes(1),
                            barTime.get().plus(1, ChronoUnit.MINUTES),
                            DoubleNum.valueOf(open.get()),
                            DoubleNum.valueOf(high.get()),
                            DoubleNum.valueOf(low.get()),
                            DoubleNum.valueOf(close.get()),
                            DoubleNum.valueOf(volume.get()),
                            DoubleNum.valueOf(close.get() * volume.get()),
                            0L
                    );
                    series.addBar(finishedBar);

                    // calculate SMA crossover
                    var sma10 = indicatorService.sma(series, 10);
                    var sma30 = indicatorService.sma(series, 30);
                    Strategy strategy = new BaseStrategy(
                            new CrossedUpIndicatorRule(sma10, sma30),
                            new CrossedUpIndicatorRule(sma30, sma10)
                    );
                    if (strategy.shouldEnter(series.getEndIndex())) {
                        placeMarketOrder(tick.getInstrumentToken(), props.getOrderQuantity());
                    }
                }
                // start new bar
                barTime.set(minute);
                open.set(tick.getLastTradedPrice());
                high.set(tick.getLastTradedPrice());
                low.set(tick.getLastTradedPrice());
                close.set(tick.getLastTradedPrice());
                volume.set((double) tick.getVolumeTradedToday());
            } else {
                // update ongoing bar
                high.set(Math.max(high.get(), tick.getLastTradedPrice()));
                low.set(Math.min(low.get(), tick.getLastTradedPrice()));
                close.set(tick.getLastTradedPrice());
                volume.set(volume.get() + tick.getVolumeTradedToday());
            }
        });

        // 5) Connect!
        ticker.connect();
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            log.info("Stopped live trading");
            // TODO: unsubscribe from WS
        } else {
            log.warn("Live trading not running");
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    private void placeMarketOrder(long instrumentToken, int qty) {
        try {
            OrderParams params = new OrderParams();
            params.tradingsymbol = String.valueOf(instrumentToken);
            params.quantity = qty;
            params.product = props.getProduct();
            params.orderType = props.getOrderType();
            // TODO: set params.variety, params.exchange, params.transactionType
            Order resp = kiteConnect.placeOrder(params, null);
            log.info("Order placed: {}", resp);
        } catch (Exception | KiteException e) {
            log.error("Failed to place market order", e);
        }
    }
}
