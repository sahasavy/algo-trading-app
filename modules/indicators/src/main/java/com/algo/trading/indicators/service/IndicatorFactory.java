package com.algo.trading.indicators.service;

import com.algo.trading.indicators.domain.IndicatorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.supertrend.SuperTrendIndicator;
import org.ta4j.core.indicators.volume.MoneyFlowIndexIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.indicators.volume.VWAPIndicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

@Component
@RequiredArgsConstructor
public class IndicatorFactory {

    public Indicator<Num> build(IndicatorType type, BarSeries series, int timeFrame) {
        return build(type, series, timeFrame, 2.0);
    }

    public Indicator<Num> build(IndicatorType type, BarSeries series, int timeFrame, double multiplier) {
        return switch (type) {
            case SMA -> new SMAIndicator(closePrice(series), timeFrame);
            case EMA -> new EMAIndicator(closePrice(series), timeFrame);
            case RSI -> new RSIIndicator(closePrice(series), timeFrame);
            case MACD -> new MACDIndicator(closePrice(series));
            case ATR -> new ATRIndicator(series, timeFrame);
            case ADX -> new ADXIndicator(series, timeFrame);
            case VWAP -> new VWAPIndicator(series, timeFrame);
            case OBV -> new OnBalanceVolumeIndicator(series);
            case MFI -> new MoneyFlowIndexIndicator(series, timeFrame);
            case SUPER_TREND -> new SuperTrendIndicator(series, timeFrame, multiplier);
            case BBM -> new BollingerBandsMiddleIndicator(closePrice(series));
            case BBU -> {
                BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(closePrice(series));
                StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice(series), timeFrame);
                yield new BollingerBandsUpperIndicator(middle, sd, DoubleNum.valueOf(multiplier));
            }
            case BBL -> {
                BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(closePrice(series));
                StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice(series), timeFrame);
                yield new BollingerBandsLowerIndicator(middle, sd, DoubleNum.valueOf(multiplier));
            }
            case STOCHASTIC_K -> new StochasticOscillatorKIndicator(series, timeFrame);
            case STOCHASTIC_D -> {
                StochasticOscillatorKIndicator k = new StochasticOscillatorKIndicator(series, timeFrame);
                yield new StochasticOscillatorDIndicator(k);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    private ClosePriceIndicator closePrice(BarSeries series) {
        return new ClosePriceIndicator(series);
    }
}
