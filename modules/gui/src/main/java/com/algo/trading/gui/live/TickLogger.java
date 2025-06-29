package com.algo.trading.gui.live;

import com.algo.trading.common.model.dto.TickDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TickLogger {

    private final ObjectMapper json;

    @KafkaListener(topics = "tick.raw", containerFactory = "kFactory")
    public void onMsg(String payload) throws Exception {
        TickDTO dto = json.readValue(payload, TickDTO.class);
        log.info("TICK {}  LTP={}  Spread={}", dto.getInstrumentToken(),
                dto.getLastPrice(), dto.getBestAsk() - dto.getBestBid());
    }
}
