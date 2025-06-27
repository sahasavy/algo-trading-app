package com.algo.trading.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private String symbol;
    private InstrumentType instrumentType;
    private String exchange;
    private BigDecimal price;
    private int qty;
    private OrderSide side;
}