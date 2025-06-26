package com.algo.trading.gui.controller;

import com.algo.trading.gui.config.GuiConfig;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class MainController {

    private final GuiConfig cfg;
    private final RestTemplate rest;

    @FXML private Button loginButton;
    @FXML private WebView webView;

    public MainController(GuiConfig cfg) {
        this.cfg = cfg;
        this.rest = new RestTemplate();
    }

    @FXML
    public void initialize() {
        log.info("Initializing GUI controller");
        webView.setVisible(false);
        WebEngine engine = webView.getEngine();
        engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
            log.debug("WebView navigating to {}", newLoc);
            String sessionPrefix = cfg.getAuthBaseUrl() + "/auth/session";
            if (newLoc.startsWith(sessionPrefix)) {
                log.info("Detected successful login redirect");
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
        log.info("Login button clicked");
        try {
            String loginUrl = rest.getForObject(cfg.getAuthBaseUrl() + "/auth/login", String.class);
            if (loginUrl == null || loginUrl.isBlank()) {
                log.error("Auth service returned empty login URL");
                showError("Cannot retrieve login URL.");
                return;
            }
            log.debug("Opening login URL: {}", loginUrl);
            webView.setVisible(true);
            webView.getEngine().load(loginUrl);
        } catch (RestClientException e) {
            log.error("Error calling auth service", e);
            showError("Failed to contact auth service:\n" + e.getMessage());
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Operation Failed");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
