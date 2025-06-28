package com.algo.trading.market.service;

import com.algo.trading.market.cache.BarEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * No longer implements JpaRepository; acts only as a POJO store
 * for unit tests that extend LocalBarCache.
 */
class InMemoryBarRepository {

    private final List<BarEntity> store = new ArrayList<>();

    public List<BarEntity> findByInstrumentTokenAndStartEpochMsBetween(long token, long from, long to) {
        return store.stream()
                .filter(b -> b.getInstrumentToken() == token &&
                        b.getStartEpochMs() >= from &&
                        b.getStartEpochMs() <= to)
                .toList();
    }

    public void saveAll(List<BarEntity> entities) {
        store.addAll(entities);
    }
}
