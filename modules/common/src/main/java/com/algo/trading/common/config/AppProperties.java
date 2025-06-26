package com.algo.trading.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    /** Application name (e.g. algo-trading-app) */
    private String name;
    /** Short description */
    private String description;
}