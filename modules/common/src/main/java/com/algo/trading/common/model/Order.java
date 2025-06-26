package com.algo.trading.common.model;

import lombok.*;
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
    private Side side;
}