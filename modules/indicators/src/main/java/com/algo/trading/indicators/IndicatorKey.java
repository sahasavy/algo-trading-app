package com.algo.trading.indicators;

import lombok.Value;

/**
 * Uniquely identifies an indicator instance (name + parameters).
 */
@Value
public class IndicatorKey {
    String name;          // "sma", "rsi", "depthImbalance", ...
    int length;        // e.g. SMA period, RSI length (use -1 if N/A)
}
