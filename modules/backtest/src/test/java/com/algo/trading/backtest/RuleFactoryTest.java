package com.algo.trading.backtest;

import com.algo.trading.backtest.spec.RuleSpec;
import com.algo.trading.indicators.IndicatorFactory;
import org.junit.jupiter.api.Test;
import org.ta4j.core.*;
import org.ta4j.core.num.DoubleNum;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RuleFactoryTest {

    private final RuleFactory factory = new RuleFactory(new IndicatorFactory());

    @Test
    void smaCrossRuleBuilds() {

        BarSeries series = new BaseBarSeriesBuilder().withName("dummy").build();
        series.addBar(bar(100));
        series.addBar(bar(101));

        RuleSpec spec = new RuleSpec();
        spec.setIndicator("sma");
        spec.setLength(1);
        spec.setOperator("crossOver");
        spec.setOther("sma1");

        Rule r = factory.build(series, spec);
        assertNotNull(r);
    }

    private static Bar bar(double close) {
        return new BaseBar(Duration.ofMinutes(1),
                ZonedDateTime.now().toInstant(), DoubleNum.valueOf(close),
                DoubleNum.valueOf(close), DoubleNum.valueOf(close),
                DoubleNum.valueOf(close), DoubleNum.valueOf(1), DoubleNum.valueOf(0), 0);
    }
}
