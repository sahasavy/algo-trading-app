package com.algo.trading.paper.config;

import com.zerodhatech.kiteconnect.KiteConnect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KiteConfig {

    @Bean
    public KiteConnect kiteConnect(PaperProperties props) {
        return new KiteConnect(props.getApiKey());
    }
}
