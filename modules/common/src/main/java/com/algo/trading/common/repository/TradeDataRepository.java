package com.algo.trading.common.repository;

import com.algo.trading.common.model.TradeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeDataRepository extends JpaRepository<TradeData, Long> {
    List<TradeData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
