package com.algo.trading.auth.config;

import com.algo.trading.auth.service.KiteService;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.ticker.KiteTicker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@RequiredArgsConstructor
public class AuthKiteBeanConfig {

    private final KiteService kiteService;

    /**
     * Gives any other module an authenticated KiteConnect instance.
     * Instantiated only when first requested *after* login succeeds (Lazy loading).
     */
    @Bean
    @Lazy
    public KiteConnect kiteConnect() {
        return kiteService.getAuthenticatedClient();
    }

    /**
     * Live WS connection (Lazy loading) that Market or Live-trade can reuse.
     */
    @Bean
    @Lazy
    public KiteTicker kiteTicker() {
        KiteConnect kiteConnect = kiteService.getAuthenticatedClient();
        return new KiteTicker(kiteConnect.getApiKey(), kiteConnect.getAccessToken());
    }
}
