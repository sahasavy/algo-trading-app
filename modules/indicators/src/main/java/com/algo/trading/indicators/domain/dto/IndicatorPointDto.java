package com.algo.trading.indicators.domain.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IndicatorPointDto {
    long epochMillis;     // x-axis
    double value;         // indicator value
}
