package com.algo.trading.indicators;

/** Binary comparison operator for two numeric series. */
public enum Op {
    GT,   // >
    GTE,  // >=
    LT,   // <
    LTE,  // <=
    EQ,   // ==
    CROSS_OVER,
    CROSS_UNDER;

    public static Op of(String s) {
        return switch (s) {
            case ">"  -> GT;
            case ">=" -> GTE;
            case "<"  -> LT;
            case "<=" -> LTE;
            case "==" -> EQ;
            case "crossOver",  "cross_over"  -> CROSS_OVER;
            case "crossUnder", "cross_under" -> CROSS_UNDER;
            default -> throw new IllegalArgumentException("Unknown op " + s);
        };
    }
}
