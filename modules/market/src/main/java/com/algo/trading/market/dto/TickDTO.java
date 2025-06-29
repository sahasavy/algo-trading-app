package com.algo.trading.market.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * A single Zerodha tick, enriched with best-bid/ask and full 5×5 depth.
 * <p>Kafka serialises this as JSON (handled by {@link com.fasterxml.jackson.databind.ObjectMapper}).</p>
 */
@Value
@Builder
public class TickDTO {

    long instrumentToken;
    long epochMs;

    /* last traded price & day volume */
    double lastPrice;
    long volume;

    /* top-of-book */
    double bestBid;
    double bestAsk;

    /* full depth snapshot (5 BUY + 5 SELL), may be empty if modeQuote */
    List<DepthDTO> depthDTO;
}
