package com.algo.trading.backtest.step;

import com.algo.trading.common.model.TradeData;
import com.algo.trading.common.repository.TradeDataRepository;
import com.algo.trading.indicators.service.IndicatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.ta4j.core.*;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BacktestStep implements Tasklet {

    private final IndicatorService indicatorService;
    private final TradeDataRepository repo;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        // 1. Build a BarSeries from TradeData
        BarSeries series = new BaseBarSeriesBuilder()
                .withName("backtest_series")
                .build();

        List<TradeData> data = repo.findByTimestampBetween(
                LocalDateTime.now().minusDays(365),
                LocalDateTime.now()
        );
        for (TradeData td : data) {
            Instant endTime = td.getTimestamp()
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
            Bar bar = new BaseBar(
                    Duration.ofMinutes(1),
                    endTime,
                    DoubleNum.valueOf(td.getOpen()),
                    DoubleNum.valueOf(td.getHigh()),
                    DoubleNum.valueOf(td.getLow()),
                    DoubleNum.valueOf(td.getClose()),
                    DoubleNum.valueOf(td.getVolume()),
                    DoubleNum.valueOf(td.getClose())
                            .multipliedBy(DoubleNum.valueOf(td.getVolume())),
                    0L
            );
            series.addBar(bar);
        }

        // 2. Define a simple SMA crossover strategy
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        var shortSma = indicatorService.sma(series, 10);
        var longSma = indicatorService.sma(series, 30);
        Strategy strategy = new BaseStrategy(
                new CrossedUpIndicatorRule(shortSma, longSma),
                new CrossedUpIndicatorRule(longSma, shortSma)
        );

        // 3. Run the backtest via BarSeriesManager
        BarSeriesManager manager = new BarSeriesManager(series);
        TradingRecord record = manager.run(strategy);

        // 4. Sum each position’s profit
        double totalProfit = record.getPositions().stream()
                .filter(Position::isClosed)
                .mapToDouble(p -> p.getProfit().doubleValue())
                .sum();

        log.info("Backtest completed — total profit: {}", totalProfit);

        return RepeatStatus.FINISHED;
    }
}
