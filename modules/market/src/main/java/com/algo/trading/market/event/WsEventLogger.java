package com.algo.trading.market.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * TODO - Later relay these events to Kafka by simply create another listener that converts them to messages.
 */
@Component
@Slf4j
public class WsEventLogger {

    @EventListener
    public void onConnect(WsConnected e) {
        log.info("📡 WebSocket connected to Zerodha");
    }

    @EventListener
    public void onDisconnect(WsDisconnected e) {
        log.warn("📡 WebSocket disconnected");
    }

    @EventListener
    public void onGiveUp(WsRetryLimitReached e) {
        log.error("🚨 Gave up reconnecting after max attempts");
        // Optionally: System.exit(2) or publish a Slack alert.
    }
}
