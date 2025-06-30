package com.algo.trading.backtest;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class BacktestReport {

    int    tradeCount;
    int    winners;
    int    losers;

    double grossProfit;   // ₹ before brokerage
    double grossLoss;     // negative number
    double netProfit;     // after brokerage

    double winRate;       // winners / tradeCount (0-100 %)
    double maxDrawdown;   // % based
    List<Double> equity;  // running equity curve (start at 0)

    String strategyName;  // optional description
}
