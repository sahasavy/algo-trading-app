package com.algo.trading.auth.service;

import com.algo.trading.auth.AuthProperties;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KiteService {

    private final AuthProperties props;
    private KiteConnect kiteConnect;

    /**
     * Initializes the KiteConnect instance with your API key.
     * Must be called before generateSession().
     */
    private void initClient() {
        if (kiteConnect == null) {
            kiteConnect = new KiteConnect(props.getApiKey());
        }
    }

    /**
     * Returns the login URL for your front end to redirect users.
     */
    public String getLoginUrl() {
        initClient();
        return kiteConnect.getLoginURL();
    }

    /**
     * Exchanges the short-lived request token for a full session, and
     * populates the KiteConnect client with accessToken, publicToken, and userId.
     *
     * @param requestToken the one-time token returned from the login redirect
     */
    public void generateSession(String requestToken) throws KiteException, IOException {
        initClient();
        User session = kiteConnect.generateSession(requestToken, props.getApiSecret());
        // com.zerodhatech.models.User has public fields, not getters
        kiteConnect.setAccessToken(session.accessToken);
        kiteConnect.setPublicToken(session.publicToken);
        kiteConnect.setUserId(session.userId);
    }

    /**
     * @return the fully authenticated KiteConnect client
     */
    public KiteConnect getKiteConnect() {
        initClient();
        return kiteConnect;
    }
}
