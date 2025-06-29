package com.algo.trading.common.brokerage;

import com.algo.trading.common.model.enums.InstrumentType;
import com.algo.trading.common.model.enums.OrderSide;
import lombok.Builder;
import lombok.NonNull;

/**
 * Immutable value object describing one executed order.
 */
@Builder
public record OrderParams(
        @NonNull InstrumentType segment,
        @NonNull OrderSide side,
        double price,
        long quantity,
        String exchange) {       // "NSE" default

    public double turnover() {
        return price * quantity;
    }

    public String exchange() {
        return (exchange == null || exchange.isBlank()) ? "NSE" : exchange;
    }
}
