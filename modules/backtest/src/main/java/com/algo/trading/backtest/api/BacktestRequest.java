package com.algo.trading.backtest.api;

import com.algo.trading.backtest.spec.StrategySpec;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BacktestRequest {
    private String symbol;           // “RELIANCE”
    private int    quantity = 1;
    private LocalDate from;          // optional
    private LocalDate to;            // optional
    private StrategySpec strategy;   // parsed from YAML/JSON body
}
