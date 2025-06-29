package com.algo.trading.backtest;

import com.algo.trading.backtest.spec.RuleSpec;
import com.algo.trading.indicators.IndicatorFactory;
import com.algo.trading.indicators.IndicatorKey;
import com.algo.trading.indicators.Op;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.ta4j.core.*;
import org.ta4j.core.indicators.ConstantIndicator;
import org.ta4j.core.rules.*;

@Component
@RequiredArgsConstructor
public class RuleFactory {

    private final IndicatorFactory indicators;

    public Rule build(BarSeries series, RuleSpec spec) {

        Indicator<Double> left = resolveIndicator(series, spec.getIndicator(), spec.getLength());
        Indicator<Double> right = resolveRight(series, spec.getOther());

        return switch (spec.op()) {
            case GT -> new OverIndicatorRule(wrap(left), wrap(right));
            case GTE -> new OverIndicatorRule(wrap(left), wrap(right)).or(
                    new EqualIndicatorRule(wrap(left), wrap(right)));
            case LT -> new UnderIndicatorRule(wrap(left), wrap(right));
            case LTE -> new UnderIndicatorRule(wrap(left), wrap(right)).or(
                    new EqualIndicatorRule(wrap(left), wrap(right)));
            case EQ -> new EqualIndicatorRule(wrap(left), wrap(right));
            case CROSS_OVER -> new CrossedUpIndicatorRule(wrap(left), wrap(right));
            case CROSS_UNDER -> new CrossedDownIndicatorRule(wrap(left), wrap(right));
        };
    }

    /* ------------------------------------------------------------------ */

    private Indicator<Double> resolveIndicator(BarSeries s, String name, int len) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Indicator name missing");
        if ("const".equalsIgnoreCase(name))
            throw new IllegalArgumentException("Use 'other' for constants");
        return indicators.get(s, new IndicatorKey(name, len));
    }

    private Indicator<Double> resolveRight(BarSeries s, String token) {
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

    /**
     * TA4J Rule API expects Num-based indicators.
     */
    @SuppressWarnings("unchecked")
    private static Indicator<Num> wrap(Indicator<Double> in) {
        return i -> Num.valueOf(in.getValue(i));
    }
}
