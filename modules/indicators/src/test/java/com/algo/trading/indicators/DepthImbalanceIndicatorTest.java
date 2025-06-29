package com.algo.trading.indicators;

import org.junit.jupiter.api.Test;
import org.ta4j.core.*;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DepthImbalanceIndicatorTest {

    @Test
    void defaultReturnsZeroWhenNoDepthInfo() {

        // --- build minimal BarSeries with two bars ----
        BarSeries series = new BaseBarSeriesBuilder().withName("demo").build();
        series.addBar(makeBar(100));
        series.addBar(makeBar(101));

        DepthImbalanceIndicator ind = new DepthImbalanceIndicator(series);

        Num zero = series.numFactory().zero();

        assertEquals(zero, ind.getValue(0));
        assertEquals(zero, ind.getValue(1));
    }

    /* helper: one-minute bar with given close price */
    private static Bar makeBar(double close) {
        Num c = DoubleNum.valueOf(close);
        return new BaseBar(Duration.ofMinutes(1), ZonedDateTime.now().toInstant(),
                c, c, c, c, DoubleNum.valueOf(1));
    }
}
