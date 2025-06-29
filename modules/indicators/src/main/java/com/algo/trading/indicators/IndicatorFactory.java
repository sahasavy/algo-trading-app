package com.algo.trading.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates or caches TA4J Indicator instances for a given BarSeries.
 * Custom metrics can be added by extending the switch statement.
 */
@Service
public class IndicatorFactory {

    private final Map<CacheKey, Indicator<?>> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Number> Indicator<T> get(BarSeries series, IndicatorKey key) {

        CacheKey ck = new CacheKey(series, key);
        return (Indicator<T>) cache.computeIfAbsent(ck, k -> create(series, key));
    }

    /* ------------------------------------------------------------------ */

    private <T extends Number> Indicator<T> create(BarSeries s, IndicatorKey key) {
        return switch (key.getName().toLowerCase()) {

            /* ----- TA4J wrapped indicators ----------------------------- */
            case "sma" -> (Indicator<T>) new SMAIndicator(new ClosePriceIndicator(s), key.getLength());
            case "rsi" -> (Indicator<T>) new RSIIndicator(new ClosePriceIndicator(s), key.getLength());

            /* ----- Custom depth-aware metric --------------------------- */
//            case "depthimbalance" -> (Indicator<T>) new DepthImbalanceIndicator(s);
            case "depthimbalance", "depth_imb" -> (Indicator<T>) new DepthImbalanceIndicator(s);

            default -> throw new IllegalArgumentException("Unknown indicator: " + key);
        };
    }

    /* tiny value object for cache map */
    private record CacheKey(BarSeries series, IndicatorKey key) {
    }
}
