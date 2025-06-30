package com.algo.trading.indicators.controller;

import com.algo.trading.indicators.domain.IndicatorType;
import com.algo.trading.indicators.domain.dto.IndicatorPointDto;
import com.algo.trading.indicators.service.IndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/indicators")
@RequiredArgsConstructor
public class IndicatorController {

    private final IndicatorService indicatorService;

    @GetMapping("/{symbol}/{timeframe}/{type}")
    public Mono<List<IndicatorPointDto>> getIndicator(
            @PathVariable String symbol,
            @PathVariable String timeframe,
            @PathVariable IndicatorType type,
            @RequestParam(defaultValue = "14") int period,
            @RequestParam(defaultValue = "200") int lookback,
            @RequestParam(defaultValue = "2.0") double multiplier) {

        return Mono.fromCallable(() ->
                indicatorService.calculate(symbol, timeframe, type, period, lookback, multiplier));
    }
}
