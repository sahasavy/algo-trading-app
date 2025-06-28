package com.algo.trading.market.provider;

import java.time.Duration;

/** Supported Zerodha candle intervals. */
public enum CandleInterval {
    MIN_1("minute", Duration.ofMinutes(1)),
    MIN_5("5minute", Duration.ofMinutes(5)),
    DAY("day", Duration.ofDays(1));

    public final String kiteToken;
    public final Duration duration;

    CandleInterval(String kiteToken, Duration duration) {
        this.kiteToken = kiteToken;
        this.duration  = duration;
    }
}
