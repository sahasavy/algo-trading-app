package com.algo.trading.market.cache;

import com.algo.trading.market.dto.OhlcBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Thin wrapper around {@link BarRepository}. Converts between
 * JPA entity and DTO.
 */
@Component
@RequiredArgsConstructor
public class LocalBarCache {

    private final BarRepository repo;

    public List<OhlcBar> query(long token, long fromMs, long toMs) {
        return repo.findByInstrumentTokenAndStartEpochMsBetween(token, fromMs, toMs)
                .stream()
                .map(LocalBarCache::toDto)
                .toList();
    }

    public void saveAll(List<OhlcBar> bars) {
        List<BarEntity> entities = bars.stream()
                .map(LocalBarCache::toEntity)
                .collect(Collectors.toList());
        repo.saveAll(entities);
    }

    /* ---------- mappers ----------------------------------- */

    private static OhlcBar toDto(BarEntity e) {
        return OhlcBar.builder()
                .instrumentToken(e.getInstrumentToken())
                .startEpochMs(e.getStartEpochMs())
                .open(e.getOpen()).high(e.getHigh())
                .low(e.getLow()).close(e.getClose())
                .volume(e.getVolume())
                .build();
    }

    private static BarEntity toEntity(OhlcBar b) {
        return BarEntity.builder()
                .instrumentToken(b.getInstrumentToken())
                .startEpochMs(b.getStartEpochMs())
                .open(b.getOpen()).high(b.getHigh())
                .low(b.getLow()).close(b.getClose())
                .volume(b.getVolume())
                .build();
    }
}
