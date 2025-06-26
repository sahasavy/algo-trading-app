package com.algo.trading.paper;

import com.algo.trading.paper.config.PaperProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {
        "com.algo.trading.common",
        "com.algo.trading.indicators",
        "com.algo.trading.paper"
})
@EnableConfigurationProperties(PaperProperties.class)
public class PaperApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaperApplication.class, args);
    }
}
