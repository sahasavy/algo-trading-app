package com.algo.trading.gui.controller;

import com.algo.trading.backtest.BacktestReport;
import com.algo.trading.backtest.api.BacktestRequest;
import com.algo.trading.gui.config.GuiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@Slf4j
public class BacktestTabController {

    private final GuiConfig guiConfig;

    @FXML
    private TextArea yamlArea;
    @FXML
    private TextArea resultArea;

    private final RestTemplate http = new RestTemplate();
    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
    private final ObjectMapper json = new ObjectMapper();

    public BacktestTabController(GuiConfig guiConfig) {
        this.guiConfig = guiConfig;
    }

    /* -------------------- UI actions ------------------------------ */

    @FXML
    void onLoadFile() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open strategy YAML");
        File f = fc.showOpenDialog(yamlArea.getScene().getWindow());
        if (f != null)
            yamlArea.setText(Files.readString(f.toPath()));
    }

    @FXML
    void onRun() {
        try {
            // parse YAML into BacktestRequest
            BacktestRequest req = yaml.readValue(yamlArea.getText(), BacktestRequest.class);

            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.valueOf("application/x-yaml"));
            HttpEntity<String> entity = new HttpEntity<>(yamlArea.getText(), h);

            ResponseEntity<BacktestReport> resp = http.postForEntity(
                    guiConfig.getBacktestBaseUrl() + "/backtest", entity, BacktestReport.class);

            // pretty print JSON
            String pretty = json.writerWithDefaultPrettyPrinter().writeValueAsString(resp.getBody());
            resultArea.setText(pretty);

        } catch (Exception ex) {
            resultArea.setText("ERROR: " + ex.getMessage());
            log.error("Backtest failed", ex);
        }
    }
}
