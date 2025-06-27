package com.algo.trading.common.brokerage;

public interface BrokerageCalculator {

    Breakdown calculate(OrderParams orderParams);

    record Breakdown(
            double brokerage,
            double stt,
            double transaction,
            double sebi,
            double gst,
            double stamp) {

        public double total() {
            return Math.round((brokerage + stt + transaction + sebi + gst + stamp) * 100.0) / 100.0;
        }
    }
}
