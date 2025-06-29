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

    /* helper: create a one-minute bar with the same OHLC price */
    private static Bar makeBar(double price) {

        Num p = DoubleNum.valueOf(price);
        Num one = DoubleNum.valueOf(1);
        Num z = DoubleNum.valueOf(0);

        return new BaseBar(
                Duration.ofMinutes(1),
                ZonedDateTime.now().toInstant(),
                p,   // open
                p,   // high
                p,   // low
                p,   // close
                one, // volume
                z,   // amount
                1L   // trades
        );
    }
}
