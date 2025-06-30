package com.algo.trading.backtest.spec;

import com.algo.trading.indicators.Op;
import lombok.Data;

@Data
public class RuleSpec {
    private String indicator;  // lhs indicator name or "const"
    private int length;        // period for lhs (if indicator)
    private String operator;   // ">", "<", ">=", "<=", "crossOver", …
    private String other;      // either another indicator key or numeric constant

    public Op op() {
        return Op.of(operator);
    }
}
