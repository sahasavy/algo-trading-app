package com.algo.trading.gui.controller;

import com.algo.trading.gui.autologin.AutoLoginManager;
import com.algo.trading.gui.config.GuiConfig;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class MainController {

    private final GuiConfig guiConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    @FXML
    private Button loginButton;
    @FXML
    private WebView webView;

    public MainController(GuiConfig guiConfig) {
        this.guiConfig = guiConfig;
    }

    @FXML
    public void initialize() {
        webView.setVisible(false);
        webView.getEngine().locationProperty()
                .addListener((obs, o, n) -> log.debug("Navigated to {}", n));
        webView.getEngine().locationProperty()
                .addListener(buildRedirectListener());
    }

    /* ---------- UI action ----------------------------------- */

    @FXML
    void onLogin(ActionEvent ignored) {
        String loginUrl = restTemplate.getForObject(guiConfig.getAuthBaseUrl() + "/auth/login", String.class);

        if (loginUrl == null || loginUrl.isBlank()) {
            showError("Auth-service did not return login URL");
            return;
        }
        log.info("Opening {}", loginUrl);

        webView.setVisible(true);
        WebEngine eng = webView.getEngine();
        eng.load(loginUrl);

        new AutoLoginManager(eng, System.getenv("KITE_UID"), System.getenv("KITE_PWD"),
                System.getenv("KITE_TOTP_SEED"))
                .register();
    }

    /* -------------------------------------------------------- */

    private ChangeListener<String> buildRedirectListener() {
        final String cbPrefix = guiConfig.getAuthBaseUrl() + "/auth/session";

        return new ChangeListener<>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs,
                                String oldLoc, String newLoc) {

                if (!newLoc.startsWith(cbPrefix)) return;

                String token = extractRequestToken(newLoc);
                if (token == null) {
                    showError("Request token missing");
                    return;
                }
                log.info("request_token={}", token);

                try {
                    restTemplate.getForObject(cbPrefix + "?request_token=" + token, String.class);

                    /* detach listener so duplicate navigation is ignored */
                    webView.getEngine().locationProperty().removeListener(this);

                    Platform.runLater(() -> {
                        webView.setVisible(false);
                        loginButton.setText("Logged In");
                        loginButton.setDisable(true);
                    });
                } catch (HttpStatusCodeException ex) {
                    showError("Auth-service error: " + ex.getStatusText());
                } catch (RestClientException ex) {
                    showError("Network error contacting auth-service");
                }
            }
        };
    }

    private static String extractRequestToken(String callbackUrl) {
        for (String reqTokenStr : callbackUrl.split("\\?")[1].split("&"))
            if (reqTokenStr.startsWith("request_token="))
                return URLDecoder.decode(reqTokenStr.split("=", 2)[1],
                        StandardCharsets.UTF_8);
        return null;
    }

    private void showError(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, msg);
            alert.setTitle("Login error");
            alert.showAndWait();
        });
    }
}
