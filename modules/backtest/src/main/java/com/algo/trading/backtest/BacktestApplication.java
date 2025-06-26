package com.algo.trading.backtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.algo.trading.common",
        "com.algo.trading.indicators",
        "com.algo.trading.backtest"
})
@EnableJpaRepositories(basePackages = "com.algo.trading.common.repository")
@EntityScan(basePackages = "com.algo.trading.common.model")
public class BacktestApplication {
    public static void main(String[] args) {
        SpringApplication.run(BacktestApplication.class, args);
    }
}
