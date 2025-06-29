package com.algo.trading.market.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@ConfigurationProperties("tick-stream")
@Data
public class TickStreamProperties {
    private List<Long> tokens;
    private String kafkaTopic = "tick.raw";
    private String kafkaBrokers = "localhost:9092";
    private Duration reconnectDelay = Duration.ofSeconds(30);
}
