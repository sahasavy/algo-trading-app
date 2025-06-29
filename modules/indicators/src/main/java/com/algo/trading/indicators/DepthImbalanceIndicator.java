package com.algo.trading.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;

/**
 * Depth-imbalance = (buyQty – sellQty) / (buyQty + sellQty).
 *
 * While running on historical bars the indicator falls back to
 * bar-volume as a neutral proxy (0 imbalance).  In live mode the Market
 * module will enrich the Bar’s metaData with tick depth so the same
 * class returns real values.
 */
public class DepthImbalanceIndicator implements Indicator<Double> {

    private static final String BUY_QTY  = "depth.buyQty";
    private static final String SELL_QTY = "depth.sellQty";

    private final BarSeries series;

    public DepthImbalanceIndicator(BarSeries series) {
        this.series = series;
    }

    @Override
    public Double getValue(int i) {

        double buy  = getMeta(series, i, BUY_QTY);
        double sell = getMeta(series, i, SELL_QTY);

        if (buy + sell == 0) return 0.0;              // neutral
        return (buy - sell) / (buy + sell);
    }

    @Override public BarSeries getBarSeries() { return series; }

    /* helper – meta values default to 0 */
    private static double getMeta(BarSeries s, int i, String key) {
        Object o = s.getBar(i).getAdditionalData().get(key);
        return o instanceof Number n ? n.doubleValue() : 0.0;
    }
}
