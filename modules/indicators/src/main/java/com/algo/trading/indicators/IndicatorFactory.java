package com.algo.trading.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.springframework.stereotype.Service;
import org.ta4j.core.num.Num;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IndicatorFactory {

    /**
     * (series identity, key) → singleton indicator
     */
    private final Map<CacheKey, Indicator<Num>> cache = new ConcurrentHashMap<>();

    public Indicator<Num> get(BarSeries series, IndicatorKey key) {
        return cache.computeIfAbsent(new CacheKey(series, key),
                ck -> create(series, key));
    }

    /* ------------------------------------------------------------------ */

    private Indicator<Num> create(BarSeries s, IndicatorKey k) {
        return switch (k.getName().toLowerCase()) {
            case "sma" -> new SMAIndicator(new ClosePriceIndicator(s), k.getLength());
            case "rsi" -> new RSIIndicator(new ClosePriceIndicator(s), k.getLength());
            case "depthimbalance", "depth_imb" -> new DepthImbalanceIndicator(s);
            default -> throw new IllegalArgumentException("Unknown indicator: " + k);
        };
    }

    /* tiny key */
    private record CacheKey(BarSeries s, IndicatorKey key) {
    }
}
