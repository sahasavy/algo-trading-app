package com.algo.trading.paper.service;

import com.algo.trading.auth.service.KiteService;
import com.algo.trading.indicators.service.IndicatorService;
import com.algo.trading.paper.config.PaperProperties;
import com.zerodhatech.kiteconnect.KiteConnect;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaperTradingService {

    private final PaperProperties props;
    private final IndicatorService indicatorService;
    private final KiteService kiteService;
    private final BarSeries series = new BaseBarSeriesBuilder()
            .withName("paper_series")
            .build();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private KiteConnect kiteConnect;

    @PostConstruct
    public void init() {
        this.kiteConnect = kiteService.getAuthenticatedClient();
        log.info("Paper trading authenticated for user {}", kiteConnect.getUserId());
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            log.warn("Paper trading already running");
            return;
        }
        log.info("Starting paper trading for instruments {}", props.getInstruments());
        // TODO: WebSocket subscription + bar building + signal logic...
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            log.info("Stopped paper trading");
            // TODO: unsubscribe & cleanup
        } else {
            log.warn("Paper trading not running");
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
