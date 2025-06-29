# indicator-service

Indicator service is responsible for supporting TA4J indicators and custom depth metrics (spread, imbalance)

---

## Responsibilities/Goals

1. Single source of truth for every numeric series a strategy might need : (price‐based TA4J indicators, depth metrics,
   spreads, ML features, etc.).

2. Consistent keys / caching so we don’t recompute the same SMA-50 a hundred times for different consumers.

3. Works on both historical bars and live ticks : (indicators update incrementally when new data arrives).

4. Extensible : Strategy devs can register their own indicator classes without touching core code.

---

## Core concepts

| Concept                | Plain-English meaning                                                                      |
|------------------------|--------------------------------------------------------------------------------------------|
| **BarSeries**          | A time-ordered list of OHLC bars (comes from Market module).                               |
| **Indicator**          | A function that, given an index *i*, returns a number (SMA, RSI, etc.).                    |
| **Indicator Key**      | (`name`, `length`, maybe extra params) → uniquely identifies one series.                   |
| **Indicator Registry** | Maps `(BarSeries, Key)` → *the* singleton indicator object.                                |
| **Update bus**         | When live ticks update the latest bar, registry tells indicators “bar X changed – recalc”. |
| **RuleSpec**           | Human/writable YAML chunk: “SMA-20 crossover SMA-50” or “RSI < 30”.                        |

---

## Data flow

```
Historical loader         Live tick listener
        │                         │
        │ create / update bars    │ update last bar, push depth
        ▼                         ▼
-------------------  Update  ----------------------
       BarSeries  ─────────────▶ IndicatorRegistry
-------------------           (cache & notifier)
        │                                 │
 TA4J Strategy builder         Live Strategy engine
        │                                 │
 uses Indicator<K> objects      uses same objects
```

## Custom indicators

| Name             | Formula idea                        | Data source     |
|------------------|-------------------------------------|-----------------|
| `depthImbalance` | (BidQty – AskQty)/(BidQty + AskQty) | live tick depth |
| `vwap`           | Σ(price·volume)/Σ(volume) intraday  | bar vol + price |
| `spread`         | bestAsk – bestBid                   | live tick       |
| `returns`        | log(close\_t / close\_{t-1})        | bars            |
| `supertrend`     | ATR-based trend indicator           | bars            |

---
