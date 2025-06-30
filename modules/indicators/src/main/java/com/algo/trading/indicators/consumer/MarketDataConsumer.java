package com.algo.trading.indicators.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MarketDataConsumer {

    @KafkaListener(topics = "market-bars")
    public void onBarEvent(String json) {
        // TODO: parse Tick/Bar, push into BarSeriesProvider
        log.debug("Bar received {}", json);
    }
}
