package com.algo.trading.paper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.paper")
@Data
public class PaperProperties {
    private String apiKey;
    private String apiSecret;
    private List<String> instruments;
    private Websocket websocket = new Websocket();

    @Data
    public static class Websocket {
        private long reconnectIntervalMs;
    }
}
