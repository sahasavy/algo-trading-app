//package com.algo.trading.gui.controller;
//
//import com.algo.trading.gui.config.GuiConfig;
//import javafx.application.Platform;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpStatusCodeException;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//
//@Slf4j
//@Component
//public class MainController {
//
//    private final GuiConfig cfg;
//    private final RestTemplate rest = new RestTemplate();
//
//    @FXML
//    private Button loginButton;
//    @FXML
//    private WebView webView;
//
//    public MainController(GuiConfig cfg) {
//        this.cfg = cfg;
//    }
//
//    @FXML
//    public void initialize() {
//        log.info("GUI initialized");
//        webView.setVisible(false);
//        WebEngine engine = webView.getEngine();
//
//        engine.locationProperty()
//                .addListener((obs, oldLoc, newLoc) -> {
//                    log.debug("Browser navigated to {}", newLoc);
//                    String callbackPrefix = cfg.getAuthBaseUrl() + "/auth/session";
//                    if (newLoc.startsWith(callbackPrefix)) {
//                        // 1) extract request_token
//                        String requestToken = null;
//                        String[] parts = newLoc.split("\\?");
//                        if (parts.length > 1) {
//                            for (String param : parts[1].split("&")) {
//                                if (param.startsWith("request_token=")) {
//                                    requestToken = URLDecoder.decode(
//                                            param.split("=", 2)[1], StandardCharsets.UTF_8);
//                                    break;
//                                }
//                            }
//                        }
//                        if (requestToken == null) {
//                            log.error("No request_token in callback URL");
//                            showError("Login failed: missing token");
//                            return;
//                        }
//                        log.info("Captured request_token={}", requestToken);
//
//                        // 2) exchange for session
//                        try {
//                            rest.getForObject(
//                                    cfg.getAuthBaseUrl() + "/auth/session?request_token=" + requestToken,
//                                    String.class);
//                            Platform.runLater(() -> {
//                                webView.setVisible(false);
//                                loginButton.setText("Logged In");
//                                loginButton.setDisable(true);
//                            });
//                        } catch (HttpStatusCodeException e) {
//                            String body = e.getResponseBodyAsString(); // ErrorResponse JSON
//                            log.error("Session exchange failed: status={} body={}", e.getStatusCode(), body, e);
//                            // extract the "message" field from the JSON, or just show body
//                            showError("Login failed: " + body);
//                        } catch (RestClientException e) {
//                            log.error("Session exchange failed", e);
//                            showError("Login failed: network error");
//                        }
//                    }
//                });
//    }
//
//    @FXML
//    void onLogin(ActionEvent event) {
//        log.info("Login button clicked");
//        try {
//            String loginUrl = rest.getForObject(cfg.getAuthBaseUrl() + "/auth/login", String.class);
//            if (loginUrl == null || loginUrl.isBlank()) {
//                log.error("Empty login URL");
//                showError("Cannot fetch login URL");
//                return;
//            }
//
//            log.debug("Opening login URL: {}", loginUrl);
//            webView.setVisible(true);
//            webView.getEngine().load(loginUrl);
//        } catch (RestClientException e) {
//            log.error("Error fetching login URL", e);
//            showError("Failed to contact auth service:\n" + e.getMessage());
//        }
//    }
//
//    private void showError(String message) {
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Login Error");
//            alert.setHeaderText("Authentication Problem");
//            alert.setContentText(message);
//            alert.showAndWait();
//        });
//    }
//}

package com.algo.trading.gui.controller;

import com.algo.trading.gui.config.GuiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
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

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class MainController {

    private final GuiConfig cfg;
    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private Button loginButton;
    @FXML
    private WebView webView;

    public MainController(GuiConfig cfg) {
        this.cfg = cfg;
    }

    @FXML
    public void initialize() {
        log.info("GUI initialized");
        webView.setVisible(false);
        WebEngine engine = webView.getEngine();

        engine.locationProperty()
                .addListener((obs, oldLoc, newLoc) -> {
                    log.debug("Browser navigated to {}", newLoc);
                    String callbackPrefix = cfg.getAuthBaseUrl() + "/auth/session";
                    if (newLoc.startsWith(callbackPrefix)) {
                        String requestToken = extractRequestToken(newLoc);
                        if (requestToken == null) {
                            log.error("No request_token in callback URL");
                            showError("Login failed: missing token");
                            return;
                        }
                        log.info("Captured request_token={}", requestToken);

                        try {
                            rest.getForObject(
                                    cfg.getAuthBaseUrl() + "/auth/session?request_token=" + requestToken,
                                    String.class);
                            Platform.runLater(() -> {
                                webView.setVisible(false);
                                loginButton.setText("Logged In");
                                loginButton.setDisable(true);
                            });
                        } catch (HttpStatusCodeException ex) {
                            handleHttpError(ex);
                        } catch (RestClientException ex) {
                            log.error("Session exchange failed (network)", ex);
                            showError("Login failed: network error");
                        }
                    }
                });
    }

    @FXML
    void onLogin(ActionEvent event) {
        log.info("Login button clicked");

        try {
            String loginUrl = rest.getForObject(cfg.getAuthBaseUrl() + "/auth/login", String.class);
            if (loginUrl == null || loginUrl.isBlank()) {
                log.error("Empty login URL");
                showError("Cannot fetch login URL");
                return;
            }

            log.debug("Opening login URL: {}", loginUrl);
            webView.setVisible(true);
            webView.getEngine().load(loginUrl);
        } catch (RestClientException ex) {
            log.error("Error fetching login URL", ex);
            showError("Failed to contact auth service:\n" + ex.getMessage());
        }
    }

    private String extractRequestToken(String url) {
        var parts = url.split("\\?", 2);

        if (parts.length < 2) {
            return null;
        }

        for (String param : parts[1].split("&")) {
            if (param.startsWith("request_token=")) {
                return URLDecoder.decode(param.split("=", 2)[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private void handleHttpError(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        log.error("Session exchange failed: status={} body={}", ex.getStatusCode(), body, ex);
        String userMsg = "Login failed";

        try {
            JsonNode node = mapper.readTree(body);
            if (node.has("message")) {
                userMsg = node.get("message").asText();
            }
        } catch (IOException io) {
            log.warn("Could not parse error JSON", io);
            userMsg += ": " + ex.getStatusText();
        }

        showError(userMsg);
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Authentication Problem");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
