package com.algo.trading.common.brokerage;

import com.algo.trading.common.model.InstrumentType;
import com.algo.trading.common.model.OrderSide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZerodhaBrokerageCalculatorTest {

    private final BrokerageCalculator calculator = new ZerodhaBrokerageCalculator();

    @Test
    @DisplayName("Equity-delivery BUY 100 × ₹100")
    void equityDeliveryBuy() {
        OrderParams order = OrderParams.builder()
                .segment(InstrumentType.EQUITY_DELIVERY)
                .side(OrderSide.BUY)
                .price(100).quantity(100).build();

        var cost = calculator.calculate(order);

        assertEquals(11.86, cost.total(), 0.05);
        assertEquals(0.0, cost.brokerage(), 0.001);
        assertEquals(10.0, cost.stt(), 0.01);
    }

    @Test
    @DisplayName("Intraday SELL hits ₹20 brokerage cap (turnover ₹1 Cr)")
    void intradaySellCapped() {
        OrderParams order = OrderParams.builder()
                .segment(InstrumentType.EQUITY_INTRADAY)
                .side(OrderSide.SELL)
                .price(200).quantity(50_000).build();   // 200 × 50 000 = 1 Cr

        var cost = calculator.calculate(order);

        assertEquals(2885.86, cost.total(), 0.10);
        assertEquals(20.0, cost.brokerage(), 0.01);
        assertEquals(2500.0, cost.stt(), 0.1);
    }

    @Test
    @DisplayName("Options BUY premium ₹50 × 100")
    void optionsBuy() {
        OrderParams order = OrderParams.builder()
                .segment(InstrumentType.FNO_OPTION)
                .side(OrderSide.BUY)
                .price(50).quantity(100).build();

        var cost = calculator.calculate(order);

        assertEquals(25.82, cost.total(), 0.05);
        assertEquals(20.0, cost.brokerage(), 0.01);
        assertTrue(cost.stt() < 0.01);            // no STT on buy
    }

    @Test
    @DisplayName("Futures BUY turnover ₹2 L – brokerage capped at ₹20")
    void futuresBuyCapped() {
        OrderParams order = OrderParams.builder()
                .segment(InstrumentType.FNO_FUTURE)
                .side(OrderSide.BUY)
                .price(200).quantity(1000).build();   // 2 L turnover

        var cost = calculator.calculate(order);

        // hand-calc: brokerage 20 + txn 3.46 + sebi 0.20 + gst 4.26 + stamp 4 = 31.92
        assertEquals(31.92, cost.total(), 0.05);
        assertEquals(20.0, cost.brokerage(), 0.01);
        assertEquals(0.0, cost.stt(), 0.001);       // no STT on buy
    }

    @Test
    @DisplayName("Zero quantity → zero cost")
    void zeroQuantityIsZeroCost() {
        OrderParams order = OrderParams.builder()
                .segment(InstrumentType.EQUITY_DELIVERY)
                .side(OrderSide.BUY)
                .price(100).quantity(0).build();

        var cost = calculator.calculate(order);
        assertEquals(0.0, cost.total(), 0.0001);
    }
}
