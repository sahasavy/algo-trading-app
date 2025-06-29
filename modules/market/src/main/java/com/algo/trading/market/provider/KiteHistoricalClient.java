package com.algo.trading.market.provider;

import com.algo.trading.market.model.OhlcBar;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves Zerodha historical candles, handling 3-month slice limits and
 * transient {@link KiteException}s via Spring-Retry.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KiteHistoricalClient implements MarketDataProvider {

    private final KiteConnect kiteConnect;
    private static final int MAX_SLICE_DAYS = 89;   // Zerodha ≤ 3-month rule

    /* ------------------------------------------------------------------ */

    @Override
    public List<OhlcBar> fetchHistorical(long instrumentToken,
                                         CandleInterval interval,
                                         LocalDate fromDate,
                                         LocalDate toDate)
            throws IOException, KiteException, JSONException {

        List<OhlcBar> aggregatedBars = new ArrayList<>();
        LocalDate sliceStart = fromDate;

        while (!sliceStart.isAfter(toDate)) {
            LocalDate sliceEnd = sliceStart.plusDays(MAX_SLICE_DAYS);
            if (sliceEnd.isAfter(toDate)) {
                sliceEnd = toDate;
            }

            log.debug("Fetching slice {} – {}", sliceStart, sliceEnd);
            aggregatedBars.addAll(
                    fetchSlice(instrumentToken, interval, sliceStart, sliceEnd));

            sliceStart = sliceEnd.plusDays(1);
        }
        return aggregatedBars;
    }

    /* -------- single slice with retry/back-off ---------------------- */

    @Retryable(
            include = KiteException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1_000, multiplier = 2.0))
    protected List<OhlcBar> fetchSlice(long instrumentToken,
                                       CandleInterval interval,
                                       LocalDate sliceStart,
                                       LocalDate sliceEnd)
            throws IOException, KiteException, JSONException {

        Date sqlFrom = Date.valueOf(sliceStart);
        Date sqlTo = Date.valueOf(sliceEnd);

        HistoricalData candleList = kiteConnect.getHistoricalData(
                sqlFrom,
                sqlTo,
                String.valueOf(instrumentToken),
                interval.kiteToken,
                false,
                false);

        List<OhlcBar> barsForSlice = new ArrayList<>(candleList.dataArrayList.size());
        for (HistoricalData candle : candleList.dataArrayList) {
            OhlcBar bar = OhlcBar.builder()
                    .instrumentToken(instrumentToken)
                    .startEpochMs(Long.parseLong(candle.timeStamp)) // TODO - Check this formatting later when testing
                    .open(candle.open)
                    .high(candle.high)
                    .low(candle.low)
                    .close(candle.close)
                    .volume(candle.volume)
                    .build();
            barsForSlice.add(bar);
        }
        return barsForSlice;
    }
}
