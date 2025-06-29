//package com.algo.trading.market.service;
//
//import com.algo.trading.market.cache.LocalBarCache;
//import com.algo.trading.market.model.OhlcBar;
//import com.algo.trading.market.provider.CandleInterval;
//import com.algo.trading.market.provider.KiteHistoricalClient;
//import com.zerodhatech.kiteconnect.KiteConnect;
//import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
//import com.zerodhatech.models.HistoricalData;
//import org.junit.jupiter.api.Test;
//
//import java.sql.Date;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * Pure unit test: mocks KiteConnect, uses a tiny in-mem cache implementation,
// * verifies conversion + caching behaviour. No Spring context needed.
// */
//class MarketDataServiceTest {
//
//    /**
//     * Simple in-memory cache for the test only.
//     */
//    static class MemoryCache extends LocalBarCache {
//        private final List<OhlcBar> store = new ArrayList<>();
//
//        MemoryCache() {
//            super(null);
//        }
//
//        @Override
//        public List<OhlcBar> query(long token, long from, long to) {
//            return store.stream()
//                    .filter(b -> b.getInstrumentToken() == token &&
//                            b.getStartEpochMs() >= from &&
//                            b.getStartEpochMs() <= to)
//                    .toList();
//        }
//
//        @Override
//        public void saveAll(List<OhlcBar> bars) {
//            store.addAll(bars);
//        }
//    }
//
//    @Test
//    void serviceFetchesFromSdkAndCaches() throws Exception, KiteException {
//
//        /* ---------- 1. Mock Kite SDK ------------------------ */
//        KiteConnect kite = mock(KiteConnect.class);
//
//        HistoricalData candle1 = new HistoricalData();
//        candle1.timeStamp = String.valueOf(Date.valueOf("2025-01-01").getTime());
//        candle1.open = 100;
//        candle1.high = 110;
//        candle1.low = 95;
//        candle1.close = 105;
//        candle1.volume = 1000;
//
//        HistoricalData candle2 = new HistoricalData();
//        candle2.timeStamp = String.valueOf(Date.valueOf("2025-01-02").getTime());
//        candle2.open = 106;
//        candle2.high = 115;
//        candle2.low = 100;
//        candle2.close = 112;
//        candle2.volume = 2000;
//
//        HistoricalData wrapper = new HistoricalData();
//        wrapper.dataArrayList = new ArrayList<>(List.of(candle1, candle2));
//
//        when(kite.getHistoricalData(any(), any(), anyString(), anyString(), anyBoolean(), anyBoolean()))
//                .thenReturn(wrapper);
//
//        /* ---------- 2. Wire service ------------------------- */
//        KiteHistoricalClient client = new KiteHistoricalClient(kite);
//        MemoryCache cache = new MemoryCache();
//        MarketDataService svc = new MarketDataService(cache, client);
//
//        /* ---------- 3. First call hits SDK ------------------ */
//        List<OhlcBar> bars = svc.getBars(
//                738561L,
//                CandleInterval.DAY,
//                LocalDate.of(2025, 1, 1),
//                LocalDate.of(2025, 1, 2));
//
//        assertEquals(2, bars.size());
//        assertEquals(105, bars.getFirst().getClose(), 0.001);
//        verify(kite, times(1))
//                .getHistoricalData(any(), any(), anyString(), anyString(), anyBoolean(), anyBoolean());
//
//        /* ---------- 4. Second call hits cache only ---------- */
//        bars = svc.getBars(
//                738561L,
//                CandleInterval.DAY,
//                LocalDate.of(2025, 1, 1),
//                LocalDate.of(2025, 1, 2));
//
//        assertEquals(2, bars.size());
//        verifyNoMoreInteractions(kite);
//    }
//}
