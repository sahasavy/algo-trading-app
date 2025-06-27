package com.algo.trading.common.brokerage;

import lombok.Builder;

/**
 * Immutable container for all fees relevant to one trading segment.
 * Percentages are expressed as decimals (0.0003 = 0.03 %).
 */
@Builder(toBuilder = true)
public record FeeStructure(
        double brokeragePercent,          //   0.0003  (= 0.03 %)
        double brokerageCap,              //  20.0     (flat cap)
        double sttPercentBuy,
        double sttPercentSell,
        double txnPercentNse,
        double txnPercentBse,
        double sebiPerCrore,              // ₹ / cr  (10 ⇒ 0.000001)
        double stampPercentBuy) {
}
