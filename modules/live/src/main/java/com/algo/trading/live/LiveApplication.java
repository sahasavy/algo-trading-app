package com.algo.trading.live;

import com.algo.trading.live.config.LiveProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {
        "com.algo.trading.common",
        "com.algo.trading.indicators",
        "com.algo.trading.auth",
        "com.algo.trading.live"
})
@EnableConfigurationProperties(LiveProperties.class)
public class LiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveApplication.class, args);
    }
}
