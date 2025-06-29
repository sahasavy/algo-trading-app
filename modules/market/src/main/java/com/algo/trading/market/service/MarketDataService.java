package com.algo.trading.market.service;

import com.algo.trading.market.cache.LocalBarCache;
import com.algo.trading.market.model.OhlcBar;
import com.algo.trading.market.provider.CandleInterval;
import com.algo.trading.market.provider.KiteHistoricalClient;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Public façade combining cache look-ups and remote fetches.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MarketDataService {

    private final LocalBarCache        cache;
    private final KiteHistoricalClient kiteClient;

    /* ------------------------------------------------------------------ */

    public List<OhlcBar> getBars(long instrumentToken,
                                 CandleInterval interval,
                                 LocalDate from,
                                 LocalDate to) throws Exception, KiteException {

        long fromMs = startOfDayMillis(from);
        long toMs   = startOfDayMillis(to.plusDays(1));   // exclusive

        List<OhlcBar> cached = cache.query(instrumentToken, fromMs, toMs - 1);

        if (coversRange(cached, from, to, interval)) {
            log.debug("Cache hit for {} ({} – {})", instrumentToken, from, to);
            return cached;
        }

        log.info("Cache miss → pulling from Zerodha for {}", instrumentToken);
        List<OhlcBar> fetched = kiteClient.fetchHistorical(
                instrumentToken, interval, from, to);
        cache.saveAll(fetched);

        /* merge lists */
        Map<Long, OhlcBar> byStart = new TreeMap<>();
        for (OhlcBar bar : cached)  byStart.put(bar.getStartEpochMs(), bar);
        for (OhlcBar bar : fetched) byStart.put(bar.getStartEpochMs(), bar);

        return new ArrayList<>(byStart.values());
    }

    /* ---------------- helpers --------------------------------------- */

    private static long startOfDayMillis(LocalDate d) {
        Instant instant = d.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();
        return instant.toEpochMilli();
    }

    private static boolean coversRange(List<OhlcBar> bars,
                                       LocalDate from,
                                       LocalDate to,
                                       CandleInterval interval) {

        if (bars.isEmpty()) return false;

        long expectedBars = switch (interval) {
            case DAY   -> to.toEpochDay() - from.toEpochDay() + 1;
            case MIN_1 -> (to.toEpochDay() - from.toEpochDay() + 1) * 375;
            case MIN_5 -> (to.toEpochDay() - from.toEpochDay() + 1) * 75;
        };
        return bars.size() >= expectedBars;
    }
}
