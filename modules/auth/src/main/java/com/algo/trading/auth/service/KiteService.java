package com.algo.trading.auth.service;

import com.algo.trading.auth.AuthProperties;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KiteService {

    private final AuthProperties props;

    /**
     * Builds the Zerodha login URL.
     */
    public String getLoginUrl() {
        log.debug("Generating login URL using apiKey={}", props.getApiKey());
        KiteConnect kite = new KiteConnect(props.getApiKey());
        String url = kite.getLoginURL();
        log.info("Login URL generated");
        return url;
    }

    /**
     * Exchanges the short-lived request token for an access token & public token,
     * then stores them in props for later REST/WebSocket calls.
     */
    public void generateSession(String requestToken) throws KiteException, IOException {
        log.info("Exchanging requestToken={} for session", requestToken);
        KiteConnect kite = new KiteConnect(props.getApiKey());
        User session = kite.generateSession(requestToken, props.getApiSecret());
        log.debug("Session acquired: accessToken={}, publicToken={}",
                session.accessToken, session.publicToken);
        props.setAccessToken(session.accessToken);
        props.setPublicToken(session.publicToken);
        log.info("Session stored in AuthProperties");
    }

    /** Returns a KiteConnect client pre-configured with your stored access token. */
    public KiteConnect getAuthenticatedClient() {
        log.debug("Creating authenticated KiteConnect client");
        KiteConnect kite = new KiteConnect(props.getApiKey());
        kite.setAccessToken(props.getAccessToken());
        kite.setPublicToken(props.getPublicToken());
        log.info("Authenticated client ready (publicToken={})", props.getPublicToken());
        return kite;
    }
}
