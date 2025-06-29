package com.algo.trading.indicators;

import org.junit.jupiter.api.Test;
import org.ta4j.core.*;
import org.ta4j.core.num.DecimalNum;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DepthImbalanceIndicatorTest {

    @Test
    void computesFromMetaData() {

        BarSeries series = new BaseBarSeriesBuilder().withName("demo").build();

        // -- bar-0
        Bar b0 = new BaseBar(Duration.ofMinutes(1), ZonedDateTime.now().toInstant(),
                DecimalNum.valueOf(100), DecimalNum.valueOf(101),
                DecimalNum.valueOf(99), DecimalNum.valueOf(100), DecimalNum.valueOf(1000),
                DecimalNum.valueOf(0), 0);
        b0.getAdditionalData().put("depth.buyQty", 300);
        b0.getAdditionalData().put("depth.sellQty", 100);
        series.addBar(b0);

        // -- bar-1 (balanced depth)
        Bar b1 = b0.copy();
        b1.getAdditionalData().put("depth.buyQty", 500);
        b1.getAdditionalData().put("depth.sellQty", 500);
        series.addBar(b1);

        DepthImbalanceIndicator ind = new DepthImbalanceIndicator(series);

        assertEquals(0.5, ind.getValue(0), 1e-6);
        assertEquals(0.0, ind.getValue(1), 1e-6);
    }
}
