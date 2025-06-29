package com.algo.trading.market.provider;

import com.algo.trading.market.model.OhlcBar;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

import java.time.LocalDate;
import java.util.List;

public interface MarketDataProvider {

    /**
     * Fetches historical candles from the remote provider.
     * Dates are inclusive.
     */
    List<OhlcBar> fetchHistorical(
            long instrumentToken,
            CandleInterval interval,
            LocalDate fromDate,
            LocalDate toDate) throws Exception, KiteException;
}
