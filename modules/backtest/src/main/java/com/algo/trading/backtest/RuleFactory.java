package com.algo.trading.backtest;

import com.algo.trading.backtest.spec.RuleSpec;
import com.algo.trading.indicators.IndicatorFactory;
import com.algo.trading.indicators.IndicatorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.ta4j.core.*;
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.*;

@Component
@RequiredArgsConstructor
public class RuleFactory {

    private final IndicatorFactory indicators;

    public Rule build(BarSeries series, RuleSpec spec) {

        Indicator<Num> left = resolveIndicator(series, spec.getIndicator(), spec.getLength());
        Indicator<Num> right = resolveRight(series, spec.getOther());

        return switch (spec.op()) {
            case GT -> new OverIndicatorRule(left, right);
            case GTE -> new OverIndicatorRule(left, right).or(
                    new EqualIndicatorRule(left, right));
            case LT -> new UnderIndicatorRule(left, right);
            case LTE -> new UnderIndicatorRule(left, right).or(
                    new EqualIndicatorRule(left, right));
            case EQ -> new EqualIndicatorRule(left, right);
            case CROSS_OVER -> new CrossedUpIndicatorRule(left, right);
            case CROSS_UNDER -> new CrossedDownIndicatorRule(left, right);
        };
    }

    /* ------------------------------------------------------------------ */

    private Indicator<Num> resolveIndicator(BarSeries s, String name, int len) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Indicator name missing");
        if ("const".equalsIgnoreCase(name))
            throw new IllegalArgumentException("Use 'other' for constants");
        return indicators.get(s, new IndicatorKey(name, len));
    }

    private Indicator<Num> resolveRight(BarSeries s, String token) {
        try {
            double c = Double.parseDouble(token);
            return new ConstantIndicator<>(s, c);
        } catch (NumberFormatException nfe) {
            // treat as indicator key, e.g. "sma50"
            String name = token.replaceAll("\\d", "");
            int len = Integer.parseInt(token.replaceAll("\\D", ""));
            return indicators.get(s, new IndicatorKey(name, len));
        }
    }

    private Indicator<Num> constant(BarSeries s, double v) {
        return i -> s.numOf(v);
    }
}
