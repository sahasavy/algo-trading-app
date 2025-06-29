package com.algo.trading.market.tick;

import com.algo.trading.market.config.TickStreamProperties;
import com.algo.trading.market.dto.TickDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TickProducer {

    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper json;
    private final TickStreamProperties props;

    public void publish(TickDTO tickDTO) {
        try {
            String payload = json.writeValueAsString(tickDTO);
            kafka.send(props.getKafkaTopic(), String.valueOf(tickDTO.getInstrumentToken()), payload);
        } catch (JsonProcessingException e) {
            log.warn("Tick serialisation failed", e);
        }
    }
}

