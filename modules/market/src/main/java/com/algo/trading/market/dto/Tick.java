package com.algo.trading.market.dto;

import lombok.Builder;
import lombok.Value;

/** Real-time tick derived from Kite Ticker stream. */
@Value
@Builder
public class Tick {
    long   instrumentToken;
    long   epochMs;
    double lastPrice;
    long   volume;
    double bestBid;
    double bestAsk;
}
