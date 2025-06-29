package com.algo.trading.market.model;

import lombok.Builder;
import lombok.Value;

/**
 * Immutable OHLC bar (typically 1-minute or 1-day).
 */
@Value
@Builder
public class OhlcBar {
    long instrumentToken;
    long startEpochMs;
    double open;
    double high;
    double low;
    double close;
    long volume;
}
