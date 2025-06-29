package com.algo.trading.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

import java.util.stream.Stream;

/**
 * (buyQty - sellQty) / (buyQty + sellQty)
 * For now we fall back to 0 because ta4j 0.18 bars no longer carry extra data.
 * When Market enriches bars you can replace the stub logic.
 */
public class DepthImbalanceIndicator implements Indicator<Num> {

    private final BarSeries series;

    public DepthImbalanceIndicator(BarSeries s) {
        this.series = s;
    }

    @Override
    public Num getValue(int i) {
        // TODO replace with real depth once bars are enriched
        return DoubleNum.valueOf(0.0);
    }

    @Override
    public boolean isStable() {
        return Indicator.super.isStable();
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }

    @Override
    public BarSeries getBarSeries() {
        return series;
    }

    @Override
    public Stream<Num> stream() {
        return Indicator.super.stream();
    }
}
