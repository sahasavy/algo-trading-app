package com.algo.trading.auth.service;

import com.algo.trading.auth.AuthProperties;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.User;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KiteService {

    private final AuthProperties props;

    /** Builds the Zerodha login URL for the user to authenticate. */
    public String getLoginUrl() {
        KiteConnect kite = new KiteConnect(props.getApiKey());
        return kite.getLoginURL();
    }

    /**
     * Exchanges the short-lived request token for an access token & public token,
     * then stores them in props for later REST/WebSocket calls.
     */
    public void generateSession(String requestToken) throws KiteException, IOException {
        KiteConnect kite = new KiteConnect(props.getApiKey());
        User session = kite.generateSession(requestToken, props.getApiSecret());
        props.setAccessToken(session.accessToken);
        props.setPublicToken(session.publicToken);
    }

    /** Returns a KiteConnect client pre-configured with your stored access token. */
    public KiteConnect getAuthenticatedClient() {
        KiteConnect kite = new KiteConnect(props.getApiKey());
        kite.setAccessToken(props.getAccessToken());
        kite.setPublicToken(props.getPublicToken());
        return kite;
    }
}
