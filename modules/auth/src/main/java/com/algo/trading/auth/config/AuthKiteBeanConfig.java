package com.algo.trading.auth.config;

import com.algo.trading.auth.service.KiteService;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.ticker.KiteTicker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthKiteBeanConfig {

    private final KiteService kiteService;

    public AuthKiteBeanConfig(KiteService ks) {
        this.kiteService = ks;
    }

    /**
     * Gives any other module an authenticated KiteConnect instance.
     */
    @Bean
    public KiteConnect authenticatedKite() {
        return kiteService.getAuthenticatedClient();
    }

    /**
     * Live WS connection that Market or Live-trade can reuse.
     */
    @Bean
    public KiteTicker kiteTicker() {
        KiteConnect kite = kiteService.getAuthenticatedClient();
        return new KiteTicker(kite.getApiKey(), kite.getAccessToken());
    }
}
