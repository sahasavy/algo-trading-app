package com.algo.trading.gui;

import fi.iki.elonen.NanoHTTPD;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.web.client.RestTemplate;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.Executors;

/**
 * Controller for the JavaFX GUI.
 */
public class MainController {

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab loginTab;
    @FXML
    private Tab backtestTab;
    @FXML
    private Tab tradingTab;
    @FXML
    private TextField apiKeyField;
    @FXML
    private TextField apiSecretField;
    @FXML
    private Button loginButton;
    @FXML
    private Button backtestButton;
    @FXML
    private Button paperButton;
    @FXML
    private Button liveButton;
    @FXML
    private TextArea logArea;

    private final RestTemplate rest = new RestTemplate();
    private NanoHTTPD httpServer;

    @FXML
    public void initialize() {
        // disable other tabs until login
        backtestTab.setDisable(true);
        tradingTab.setDisable(true);

        loginButton.setOnAction(e -> doLogin());
        backtestButton.setOnAction(e -> runBacktest());
        paperButton.setOnAction(e -> togglePaperTrading());
        liveButton.setOnAction(e -> toggleLiveTrading());
    }

    private void doLogin() {
        try {
            // start HTTP server to capture request_token
            httpServer = new NanoHTTPD(8085) {
                @Override
                public Response serve(IHTTPSession session) {
                    String token = session.getParms().get("request_token");
                    // call auth microservice to generate session
                    rest.postForObject(
                            "http://localhost:8081/auth/session?requestToken=" + token,
                            null, Void.class
                    );
                    Platform.runLater(() -> {
                        logArea.appendText("Login successful, token=" + token + "\n");
                        backtestTab.setDisable(false);
                        tradingTab.setDisable(false);
                        tabPane.getSelectionModel().select(backtestTab);
                    });
                    stop();
                    return newFixedLengthResponse(
                            "<html><body>Logged in! You can close this window.</body></html>"
                    );
                }
            };
            httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

            // open system browser for login
            String loginUrl = "http://localhost:8081/auth/login?redirectUri=http://localhost:8085/";
            Desktop.getDesktop().browse(new URI(loginUrl));
            logArea.appendText("Waiting for login callback...\n");
        } catch (Exception ex) {
            logArea.appendText("Login error: " + ex.getMessage() + "\n");
        }
    }

    private void runBacktest() {
        Executors.newSingleThreadExecutor().submit(() -> {
            Platform.runLater(() -> logArea.appendText("Running backtest...\n"));
            String resp = rest.postForObject(
                    "http://localhost:8080/backtest/run", null, String.class
            );
            Platform.runLater(() -> logArea.appendText(resp + "\n"));
        });
    }

    private void togglePaperTrading() {
        Executors.newSingleThreadExecutor().submit(() -> {
            String status = rest.getForObject(
                    "http://localhost:8082/paper/status", String.class
            );
            if ("RUNNING".equals(status)) {
                rest.postForObject("http://localhost:8082/paper/stop", null, String.class);
                Platform.runLater(() -> logArea.appendText("Paper trading stopped\n"));
            } else {
                rest.postForObject("http://localhost:8082/paper/start", null, String.class);
                Platform.runLater(() -> logArea.appendText("Paper trading started\n"));
            }
        });
    }

    private void toggleLiveTrading() {
        Executors.newSingleThreadExecutor().submit(() -> {
            String status = rest.getForObject(
                    "http://localhost:8083/live/status", String.class
            );
            if ("RUNNING".equals(status)) {
                rest.postForObject("http://localhost:8083/live/stop", null, String.class);
                Platform.runLater(() -> logArea.appendText("Live trading stopped\n"));
            } else {
                rest.postForObject("http://localhost:8083/live/start", null, String.class);
                Platform.runLater(() -> logArea.appendText("Live trading started\n"));
            }
        });
    }
}
