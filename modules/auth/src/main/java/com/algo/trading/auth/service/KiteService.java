package com.algo.trading.auth.service;

import com.algo.trading.auth.AuthProperties;
import com.algo.trading.auth.event.TokenExpiredEvent;
import com.algo.trading.auth.exception.AuthException;
import com.algo.trading.auth.model.SessionToken;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class KiteService {

    private final AuthProperties props;
    private final TokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * (Step - 1) Build the Zerodha login URL.
     */
    public String getLoginUrl() {
        if (props.getApiKey() == null || props.getApiKey().isBlank())
            throw new AuthException("API key not configured");
        return new KiteConnect(props.getApiKey()).getLoginURL();
    }

    /**
     * (Step - 2) Exchange <code>request_token</code> for access/public tokens and cache them.
     */
    public void generateSession(String requestToken) {
        log.info("Started generating user session for {}", props.getUserName());

        if (requestToken == null || requestToken.isBlank())
            throw new AuthException("Missing request token");
        if (props.getApiKey() == null || props.getApiSecret() == null)
            throw new AuthException("API credentials not configured");

        try {
            KiteConnect kite = new KiteConnect(props.getApiKey());
            User session = kite.generateSession(requestToken, props.getApiSecret());

            if (session == null || session.accessToken == null)
                throw new AuthException("Invalid session response from Kite Connect");

            log.debug("Fetched User's accessToken: {}", session.accessToken);
            log.debug("Fetched User's publicToken: {}", session.publicToken);

            tokenService.save(new SessionToken(props.getUserName(), session.accessToken, session.publicToken,
                    LocalDate.now(ZoneId.systemDefault())));

            log.info("User session generated successfully for {}", props.getUserName());

            /* notify other modules that today’s token is ready */
            eventPublisher.publishEvent(new TokenExpiredEvent(this));
        } catch (KiteException e) {
            throw new AuthException("Failed to generate session: request token may have expired", e);
        } catch (IOException e) {
            throw new AuthException("Network error while generating session", e);
        }
    }

    /**
     * (Step - 3) Get a ready-to-use client for any downstream module.
     */
    public KiteConnect getAuthenticatedClient() {
        SessionToken sessionToken = tokenService.current();
        if (sessionToken == null || sessionToken.getAccessToken() == null)
            throw new AuthException("User not logged in - no access token in cache");

        KiteConnect kite = new KiteConnect(props.getApiKey());
        kite.setAccessToken(sessionToken.getAccessToken());
        kite.setPublicToken(sessionToken.getPublicToken());
        return kite;
    }
}
