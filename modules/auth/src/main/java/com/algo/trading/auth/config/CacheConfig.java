package com.algo.trading.auth.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeine() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(24))
                .maximumSize(10_000);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(
                "kiteSession",          // holds SessionToken (1 per day)
                "usedRequestTokens"     // de-dup set for request_token
        );
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
