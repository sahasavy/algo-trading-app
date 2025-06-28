package com.algo.trading.market.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/** Enables scanning of market beans and Spring-Retry proxies. */
@Configuration
@EnableRetry
@ComponentScan(basePackages = "com.algo.trading.market")
public class MarketDataAutoConfig { }
