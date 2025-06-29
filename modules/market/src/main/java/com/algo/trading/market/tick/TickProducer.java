package com.algo.trading.market.tick;

import com.algo.trading.common.model.dto.TickDTO;
import com.algo.trading.market.config.TickStreamProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TickProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper jsonObjectMapper;
    private final TickStreamProperties props;

    private final Counter tickCounter = Metrics.counter("tick.published");

    public void publish(TickDTO tickDTO) {
        try {
            String payload = jsonObjectMapper.writeValueAsString(tickDTO);
            kafkaTemplate.send(props.getKafkaTopic(), String.valueOf(tickDTO.getInstrumentToken()), payload);

            tickCounter.increment();
            log.info("Tick data sent to topic: {}", props.getKafkaTopic());
        } catch (JsonProcessingException e) {
            log.warn("Tick serialisation failed", e);
        }
    }
}

