package com.algo.trading.market.tick;

import com.algo.trading.auth.event.TokenExpiredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenExpiryListener {

    private final KiteTickerClient client;

    @EventListener
    public void onTokenExpired(TokenExpiredEvent event) {
        client.forceReconnect();
    }
}
