package com.algo.trading.paper.service;

import com.algo.trading.auth.service.KiteService;
import com.algo.trading.paper.config.PaperProperties;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.algo.trading.indicators.service.IndicatorService;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaperTradingService {

    private final PaperProperties props;
    private final IndicatorService indicatorService;
    private final KiteService kiteService;
    private KiteConnect kiteConnect;

    private final BarSeries series = new BaseBarSeriesBuilder()
            .withName("paper_series")
            .build();
    private final AtomicBoolean running = new AtomicBoolean(false);

    @PostConstruct
    public void init() throws KiteException, IOException {
        // Exchange the requestToken you’ve obtained via login
        String requestToken = "passed_from_GUI";/* passed from your GUI after user login */
        ;
        kiteService.generateSession(requestToken);
        kiteConnect = kiteService.getKiteConnect();
        log.info("Paper-trading Kite session initialized for user {}", kiteConnect.getUserId());
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            log.warn("Paper trading already running");
            return;
        }
        log.info("Starting paper trading for instruments {}", props.getInstruments());
        // TODO: use kiteService.getAuthenticatedClient() inside your tick handlers
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            log.info("Stopped paper trading");
            // TODO: unsubscribe and cleanup
        } else {
            log.warn("Paper trading not running");
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
