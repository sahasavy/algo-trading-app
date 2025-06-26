package com.algo.trading.ml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.algo.trading.common",      // common module (entities, repos)
        "com.algo.trading.indicators",  // indicators module (services)
        "com.algo.trading.ml"           // this module
})
@EnableJpaRepositories(basePackages = {
        "com.algo.trading.common.repository"
})
@EntityScan(basePackages = {
        "com.algo.trading.common.model"
})
public class MlApplication {
    public static void main(String[] args) {
        SpringApplication.run(MlApplication.class, args);
    }
}
