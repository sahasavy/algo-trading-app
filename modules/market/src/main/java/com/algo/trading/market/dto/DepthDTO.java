package com.algo.trading.market.dto;

import com.algo.trading.common.model.OrderSide;
import lombok.Builder;
import lombok.Value;

/**
 * One level of market depth (L1-L5).
 */
@Value
@Builder
public class DepthDTO {

    OrderSide side;        // BUY or SELL
    double price;       // ₹
    long quantity;    // total quantity at this level
    int orderCount;  // number of orders composing that quantity
}
