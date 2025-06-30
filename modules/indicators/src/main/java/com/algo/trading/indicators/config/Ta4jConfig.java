package com.algo.trading.indicators.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ta4j.core.num.DecimalNumFactory;
import org.ta4j.core.num.NumFactory;

@Configuration
@Slf4j
public class Ta4jConfig {

    @Bean
    public NumFactory numFactory() {
        NumFactory factory = DecimalNumFactory.getInstance();   // 32-digit DecimalNum
        log.info("TA4J NumFactory initialised: {}", factory.one().getClass().getSimpleName());
        return factory;
    }
}
