package com.algo.trading.gui.live;

import com.algo.trading.common.model.dto.TickDTO;
import com.algo.trading.gui.controller.MainController;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TickLogger {

    private final ObjectMapper mapper;
    private final MainController mainController;

    /**
     * Listens to ticks published by the Market module (topic: tick.raw).
     */
    @KafkaListener(topics = "tick.raw", groupId = "gui-preview")
    public void onMessage(String payload) {
        try {
            TickDTO tickDTO = mapper.readValue(payload, TickDTO.class);

            String line = String.format("%d  %.2f  spread=%.2f%n",
                    tickDTO.getInstrumentToken(),
                    tickDTO.getLastPrice(),
                    tickDTO.getBestAsk() - tickDTO.getBestBid());

            /* Push text into JavaFX UI thread */
            Platform.runLater(() -> mainController.appendLiveTick(line));

        } catch (Exception e) {
            log.warn("Cannot parse TickDTO", e);
        }
    }
}
