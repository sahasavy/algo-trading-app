package com.algo.trading.backtest.api;

import com.algo.trading.backtest.BacktestEngine;
import com.algo.trading.backtest.BacktestReport;
import com.algo.trading.market.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/backtest")
@RequiredArgsConstructor
public class BacktestController {

    private final MarketDataService market;
    private final BacktestEngine engine;

    /**
     * Accepts YAML or JSON request, returns JSON report.
     */
    @PostMapping(consumes = {"application/x-yaml", MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BacktestReport run(@RequestBody BacktestRequest req) {

        var series = market.loadSeries(
                req.getSymbol(),
                req.getFrom(), req.getTo());

        return engine.run(req.getSymbol(), series,
                req.getStrategy(), req.getQuantity());
    }
}
