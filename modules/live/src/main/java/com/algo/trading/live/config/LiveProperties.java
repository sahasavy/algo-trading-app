package com.algo.trading.live.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.live")
@Data
public class LiveProperties {
    /**
     * List of instrument tokens (as longs) to subscribe
     */
    private List<Long> instruments;

    /**
     * Default order size
     */
    private int orderQuantity;

    /**
     * MARKET or LIMIT
     */
    private String orderType;

    /**
     * e.g. MIS or CNC
     */
    private String product;

    /**
     * Reconnection interval in milliseconds
     */
    private int reconnectIntervalMs;
}
