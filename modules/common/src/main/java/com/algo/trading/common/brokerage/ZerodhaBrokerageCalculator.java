package com.algo.trading.common.brokerage;

import com.algo.trading.common.model.OrderSide;

public final class ZerodhaBrokerageCalculator implements BrokerageCalculator {

    private final BrokerageConfig brokerageConfig = BrokerageConfig.get();

    @Override
    public Breakdown calculate(OrderParams orderParams) {

        FeeStructure feeStructure = brokerageConfig.getFeeTable().get(orderParams.segment());
        double turnover = orderParams.turnover();

        /* brokerage */
        double brokerage = switch (orderParams.segment()) {
            case FNO_OPTION -> feeStructure.brokerageCap();     // flat ₹20
            default -> Math.min(turnover * feeStructure.brokeragePercent(), feeStructure.brokerageCap());
        };

        /* STT */
        double stt = (orderParams.side() == OrderSide.BUY ? feeStructure.sttPercentBuy()
                : feeStructure.sttPercentSell()) * turnover;

        /* exchange txn fee */
        double txnRate = orderParams.exchange().equalsIgnoreCase("BSE")
                ? feeStructure.txnPercentBse() : feeStructure.txnPercentNse();
        double txn = txnRate * turnover;

        /* SEBI */
        double sebi = feeStructure.sebiPerCrore() / 1_00_00_000 * turnover;

        /* GST */
        double gst = brokerageConfig.getGstPercent() * (brokerage + txn + sebi);

        /* stamp duty (buy side only) */
        double stamp = orderParams.side() == OrderSide.BUY ? feeStructure.stampPercentBuy() * turnover : 0.0;

        return new Breakdown(brokerage, stt, txn, sebi, gst, stamp);
    }
}
