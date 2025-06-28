package com.algo.trading.market.cache;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarRepository
        extends JpaRepository<BarEntity, Long> {

    List<BarEntity> findByInstrumentTokenAndStartEpochMsBetween(
            long instrumentToken, long startInclusive, long endExclusive);
}
