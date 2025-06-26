//package com.algo.trading.gui.controller;
//
//import javafx.application.Platform;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
//import org.springframework.web.client.RestTemplate;
//
//public class MainController {
//
//    private static final String AUTH_LOGIN_URL   = "http://localhost:8081/auth/login";
//    private static final String AUTH_SESSION_URL = "http://localhost:8081/auth/session";
//
//    private final RestTemplate rest = new RestTemplate();
//
//    @FXML
//    private Button loginButton;
//
//    @FXML
//    private WebView webView;
//
//    @FXML
//    public void initialize() {
//        webView.setVisible(false);
//        WebEngine engine = webView.getEngine();
//        engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
//            if (newLoc.startsWith(AUTH_SESSION_URL)) {
//                // Login completed successfully
//                Platform.runLater(() -> {
//                    webView.setVisible(false);
//                    loginButton.setText("Logged In");
//                    loginButton.setDisable(true);
//                    // TODO: enable other tabs/buttons
//                });
//            }
//        });
//    }
//
//    @FXML
//    void onLogin(ActionEvent event) {
//        // 1) Get the redirect URL from backend
//        String loginUrl = rest.getForObject(AUTH_LOGIN_URL, String.class);
//        if (loginUrl != null) {
//            // 2) Show embedded browser
//            webView.setVisible(true);
//            webView.getEngine().load(loginUrl);
//        }
//    }
//}

package com.algo.trading.gui.controller;

import com.algo.trading.gui.config.GuiConfig;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MainController {

    private final GuiConfig cfg;
    private final RestTemplate rest = new RestTemplate();

    @FXML private Button loginButton;
    @FXML private WebView webView;

    public MainController(GuiConfig cfg) {
        this.cfg = cfg;
    }

    @FXML
    public void initialize() {
        webView.setVisible(false);
        WebEngine engine = webView.getEngine();
        engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
            // Zerodha will redirect to /session?request_token=...
            if (newLoc.startsWith(cfg.getAuthBaseUrl() + "/auth/session")) {
                Platform.runLater(() -> {
                    webView.setVisible(false);
                    loginButton.setText("Logged In");
                    loginButton.setDisable(true);
                });
            }
        });
    }

    @FXML
    void onLogin(ActionEvent event) {
        // 1) fetch the login URL from auth service
        String loginUrl = rest.getForObject(cfg.getAuthBaseUrl() + "/auth/login", String.class);
        if (loginUrl != null) {
            // 2) show embedded browser
            webView.setVisible(true);
            webView.getEngine().load(loginUrl);
        }
    }
}
