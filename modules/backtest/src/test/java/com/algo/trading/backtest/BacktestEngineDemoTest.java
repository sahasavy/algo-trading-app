package com.algo.trading.backtest;

import com.algo.trading.backtest.spec.StrategySpec;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;
import org.ta4j.core.*;
import org.ta4j.core.num.DoubleNum;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.*;

class BacktestEngineDemoTest {

    private final BacktestEngine engine =
            new BacktestEngine(new RuleFactory(new com.algo.trading.indicators.IndicatorFactory()),
                    new com.algo.trading.common.brokerage.ZerodhaBrokerageCalculator());

    @Test
    void runDemo() throws Exception {

        // ---- load SMA crossover spec ---------
        String yaml = """
            description: demo
            entry:
              - { indicator: sma, length: 3, operator: crossOver, other: sma8 }
            exit:
              - { indicator: sma, length: 3, operator: crossUnder, other: sma8 }
            """;
        StrategySpec spec = new YAMLMapper().readValue(yaml, StrategySpec.class);

        // ---- build dumb price series ---------
        BarSeries s = new BaseBarSeriesBuilder().withName("demo").build();
        double[] prices = DoubleStream.concat(
                DoubleStream.iterate(100, p -> p + 1).limit(20), // up-trend
                DoubleStream.iterate(120, p -> p - 1).limit(20)  // down-trend
        ).toArray();

        for (double p : prices)
            s.addBar(makeBar(p));

        BacktestReport rpt = engine.run("DEMO", s, spec, 1);

        assertTrue(rpt.getTradeCount() > 0);
        System.out.println(rpt);
    }

    private static Bar makeBar(double close) {
        return new BaseBar(Duration.ofMinutes(1), ZonedDateTime.now(),
                DoubleNum.valueOf(close), DoubleNum.valueOf(close),
                DoubleNum.valueOf(close), DoubleNum.valueOf(close),
                DoubleNum.valueOf(1));
    }
}
