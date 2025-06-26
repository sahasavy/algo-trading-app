package com.algo.trading.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}