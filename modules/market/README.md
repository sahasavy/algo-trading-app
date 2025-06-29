# market-service

Market service is responsible for historical & live ticks with simple rate-limit
handling

---

## Responsibilities

1. Provide historical OHLC bars to any caller (back-tester, ML trainer,
   indicator engine, etc.).

2. Stream live ticks to interested components (paper/live trader, GUI,
   Kafka).

3. Cache data locally so we don’t hammer the Zerodha endpoints.

4. Shield the rest of the system from Zerodha SDK quirks, rate limits,
   retry logic, and slice limits.

---

## Actors and data flow (historical path)

### Historical path

```
Caller ──► MarketDataService ──► LocalBarCache
└─► KiteHistoricalClient ──► Zerodha REST
```

1. Caller (e.g., back-tester) asks for bars:
   instrumentToken = 738561, interval = 1 minute, from = 2025-01-01, to = 2025-01-31

2. MarketDataService checks LocalBarCache (H2 DB) for that date range.

3. If any portion is missing, KiteHistoricalClient fetches the gap(s) while:
    1. slicing into ≤ 3-month chunks,
    2. retrying on KiteException (HTTP 429, 5xx) with exponential back-off.

4. Client receives one HistoricalData object per REST call.
   Inside that object there is a List<HistoricalData.Candle> (field name
   data in the SDK). We convert each Candle to our DTO OhlcBar.

5. Newly fetched bars are stored in LocalBarCache and then merged with any
   previously cached bars before returning to the caller.

### Live tick path

```
KiteTickerClient ─► Tick DTO ─► Kafka topic "tick.raw" ─► consumers (paper/live)
```

TBD

---

## Metrics (Micrometer counters)

Below are the list of metrics exposed at `/actuator/metrics`.

1. tick.published
2. ws.reconnects

---
