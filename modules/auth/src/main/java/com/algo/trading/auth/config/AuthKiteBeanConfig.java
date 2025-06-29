package com.algo.trading.auth.config;

import com.algo.trading.auth.event.TokenExpiredEvent;
import com.algo.trading.auth.service.KiteService;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.ticker.KiteTicker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AuthKiteBeanConfig {

    private final KiteService kiteService;
    private final ApplicationEventPublisher publisher;

    /**
     * Gives any other module an authenticated KiteConnect instance.
     */
    @Bean
    public KiteConnect kiteConnect() {
        return kiteService.getAuthenticatedClient();
    }

    /**
     * Live WS connection that Market or Live-trade can reuse.
     */
    @Bean
    public KiteTicker kiteTicker() {
        KiteConnect kiteConnect = kiteService.getAuthenticatedClient();
        return new KiteTicker(kiteConnect.getApiKey(), kiteConnect.getAccessToken());
    }

    /**
     * called once per day by KiteService when new session generated
     */
    public void publishExpiryEvent() {
        publisher.publishEvent(new TokenExpiredEvent(this));
    }
}
