package com.algo.trading.auth.service;

import com.algo.trading.auth.AuthProperties;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.User;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KiteService {

    private final AuthProperties props;

    /**
     * Builds a new KiteConnect client and auto-logs in using the stored request token.
     */
    public KiteConnect getAuthenticatedClient() {
        // 1) Construct using API key
        KiteConnect kite = new KiteConnect(props.getApiKey());

        // 2) Exchange the request-token for a session (contains accessToken & userId)
        User session;
        try {
            session = kite.generateSession(props.getRequestToken(), props.getApiSecret());
        } catch (KiteException | IOException e) {
            log.warn("Exception occurred while getting authenticated KiteConnect client", e);
            throw new RuntimeException(e);
        }

        // 3) Apply the new tokens to the client
        kite.setAccessToken(session.accessToken);
        kite.setUserId(session.userId);

        return kite;
    }
}
