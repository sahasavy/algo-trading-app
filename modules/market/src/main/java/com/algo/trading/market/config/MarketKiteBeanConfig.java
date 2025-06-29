package com.algo.trading.market.config;

import com.algo.trading.market.dto.AuthTokenDTO;
import com.algo.trading.market.remote.TokenClient;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.ticker.KiteTicker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class MarketKiteBeanConfig {

    private final TokenClient tokenClient;

    private volatile AuthTokenDTO latest;

    /* fetch once at startup */
    @PostConstruct
    void init() {
        refreshToken();
    }

    /* refresh every day at 6:05 AM IST */
    @Scheduled(cron = "0 5 6 * * *", zone = "Asia/Kolkata")
    void refreshToken() {
        latest = tokenClient.fetch();
    }

    @Bean
    public KiteConnect kiteConnect() {
        if (latest == null)
            throw new IllegalStateException("No Zerodha token available yet");
        KiteConnect kc = new KiteConnect(latest.getApiKey());
        kc.setAccessToken(latest.getAccessToken());
        kc.setPublicToken(latest.getPublicToken());
        return kc;
    }

    @Bean
    public KiteTicker kiteTicker(KiteConnect kc) {
        return new KiteTicker(kc.getApiKey(), kc.getAccessToken());
    }
}
