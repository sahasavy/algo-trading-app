package com.algo.trading.indicators;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.algo.trading.common",
        "com.algo.trading.indicators"
})
public class IndicatorsApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndicatorsApplication.class, args);
    }
}
