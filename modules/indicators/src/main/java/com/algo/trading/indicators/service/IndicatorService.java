package com.algo.trading.indicators.service;

import com.algo.trading.indicators.domain.IndicatorType;
import com.algo.trading.indicators.domain.dto.IndicatorPointDto;
import com.algo.trading.market.service.MarketDataService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorService {

    private final MarketDataService marketData;         // to be added later
    private final IndicatorFactory factory;

    @Cacheable(value = "seriesCache", key = "#symbol + ':' + #timeframe")
    public BarSeries getSeries(String symbol, String timeframe) {
//        return marketData.series(symbol, timeframe);    // placeholder
        return null;
    }

    public List<IndicatorPointDto> calculate(
            @Nonnull String symbol,
            @Nonnull String timeframe,
            @Nonnull IndicatorType type,
            int period,
            int lookback,
            double multiplier) {

        BarSeries series = getSeries(symbol, timeframe);
        Indicator<Num> indicator = factory.build(type, series, period);
//        Indicator<Num> indicator = factory.build(type, series, period, multiplier);

        int start = Math.max(0, series.getEndIndex() - lookback + 1);
        return IntStream.rangeClosed(start, series.getEndIndex())
                .mapToObj(i -> IndicatorPointDto.builder()
                        .epochMillis(series.getBar(i).getEndTime().toEpochMilli())
                        .value(indicator.getValue(i).doubleValue())
                        .build())
                .toList();
    }
}
