package com.algo.trading.auth.service;

import com.algo.trading.auth.AuthProperties;
import com.algo.trading.auth.exception.AuthException;
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
        log.debug("Generating login URL (apiKey={}, redirectUri={})", props.getApiKey(), props.getRedirectUri());

        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            throw new AuthException("API key is not configured");
        }
        if (props.getRedirectUri() == null || props.getRedirectUri().isBlank()) {
            throw new AuthException("Redirect URI is not configured");
        }

        KiteConnect kite = new KiteConnect(props.getApiKey());
//        kite.setRedirectUri(props.getRedirectUri());

        String url = kite.getLoginURL();
        log.info("Login URL generated");
        return url;
    }

    /**
     * Exchanges a one-time request token for an access token & public token.
     */
    public void generateSession(String requestToken) {
        log.info("Generating session for requestToken={}", requestToken);

        if (requestToken == null || requestToken.isBlank()) {
            throw new AuthException("Missing or empty request token");
        }
        if (props.getApiKey() == null || props.getApiSecret() == null) {
            throw new AuthException("API credentials not configured");
        }

        KiteConnect kite = new KiteConnect(props.getApiKey());
//        kite.setRedirectUri(props.getRedirectUri());
        try {
            User session = kite.generateSession(requestToken, props.getApiSecret());

            // Sanity check
            if (session == null || session.accessToken == null || session.publicToken == null) {
                throw new AuthException("Invalid session response from Kite Connect");
            }

            log.debug("Session acquired: accessToken={}, publicToken={}", session.accessToken, session.publicToken);
            props.setAccessToken(session.accessToken);
            props.setPublicToken(session.publicToken);

            log.info("Session stored in AuthProperties"); // TODO - Can save to some db, or cache, or temporary file
        } catch (KiteException e) {
            log.error("KiteException during session generation", e);
            throw new AuthException("Failed to generate session: request token may have expired or is invalid", e);
        } catch (IOException e) {
            log.error("IOException during session generation", e);
            throw new AuthException("Network error while generating session", e);
        }
    }

    /**
     * Returns a pre-configured client ready for authenticated calls.
     */
    public KiteConnect getAuthenticatedClient() {
        log.debug("Creating authenticated KiteConnect client");

        if (props.getAccessToken() == null || props.getAccessToken().isBlank()) {
            throw new AuthException("No access token available; user is not logged in");
        }

        KiteConnect kite = new KiteConnect(props.getApiKey());
        kite.setAccessToken(props.getAccessToken());
        kite.setPublicToken(props.getPublicToken());

        log.info("Authenticated client ready (publicToken={})", props.getPublicToken());
        return kite;
    }
}
