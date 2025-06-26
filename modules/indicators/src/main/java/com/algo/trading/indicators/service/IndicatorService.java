package com.algo.trading.indicators.service;

import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.*;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.volume.VWAPIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.indicators.volume.MoneyFlowIndexIndicator;
import org.ta4j.core.indicators.supertrend.SuperTrendIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

@Service
public class IndicatorService {

    private ClosePriceIndicator closePrice(BarSeries series) {
        return new ClosePriceIndicator(series);
    }

    /**
     * Simple Moving Average
     */
    public SMAIndicator sma(BarSeries series, int timeFrame) {
        return new SMAIndicator(closePrice(series), timeFrame);
    }

    /**
     * Exponential Moving Average
     */
    public EMAIndicator ema(BarSeries series, int timeFrame) {
        return new EMAIndicator(closePrice(series), timeFrame);
    }

    /**
     * Relative Strength Index
     */
    public RSIIndicator rsi(BarSeries series, int timeFrame) {
        return new RSIIndicator(closePrice(series), timeFrame);
    }

    /**
     * MACD: fast vs. slow EMA
     */
    public MACDIndicator macd(BarSeries series, int shortPeriod, int longPeriod) {
        return new MACDIndicator(closePrice(series), shortPeriod, longPeriod);
    }

    /**
     * Average True Range (volatility)
     */
    public ATRIndicator atr(BarSeries series, int timeFrame) {
        return new ATRIndicator(series, timeFrame);
    }

    /**
     * Average Directional Index (trend strength)
     */
    public ADXIndicator adx(BarSeries series, int timeFrame) {
        return new ADXIndicator(series, timeFrame);
    }

    /**
     * Volume-Weighted Average Price
     */
    public VWAPIndicator vwap(BarSeries series, int timeFrame) {
        return new VWAPIndicator(series, timeFrame);
    }

    /**
     * On-Balance Volume
     */
    public OnBalanceVolumeIndicator obv(BarSeries series) {
        return new OnBalanceVolumeIndicator(series);
    }

    /**
     * Money Flow Index
     */
    public MoneyFlowIndexIndicator mfi(BarSeries series, int timeFrame) {
        return new MoneyFlowIndexIndicator(series, timeFrame);
    }

    /**
     * SuperTrend (ATR-based trend indicator)
     */
    public SuperTrendIndicator superTrend(BarSeries series, int atrPeriod, double multiplier) {
        return new SuperTrendIndicator(series, atrPeriod, multiplier);
    }

    /**
     * Bollinger Bands Middle
     */
    public BollingerBandsMiddleIndicator bbm(BarSeries series) {
        return new BollingerBandsMiddleIndicator(closePrice(series));
    }

    /**
     * Bollinger Bands Upper
     */
    public BollingerBandsUpperIndicator bbu(BarSeries series, int timeFrame, double multiplier) {
        BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(closePrice(series));
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice(series), timeFrame);
        return new BollingerBandsUpperIndicator(middle, sd, DoubleNum.valueOf(multiplier));
    }

    /**
     * Bollinger Bands Lower
     */
    public BollingerBandsLowerIndicator bbl(BarSeries series, int timeFrame, double multiplier) {
        BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(closePrice(series));
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice(series), timeFrame);
        return new BollingerBandsLowerIndicator(middle, sd, DoubleNum.valueOf(multiplier));
    }

    /**
     * Stochastic Oscillator %K
     */
    public StochasticOscillatorKIndicator stochasticK(BarSeries series, int barCount) {
        return new StochasticOscillatorKIndicator(series, barCount);
    }

    /**
     * Stochastic Oscillator %D
     */
    public StochasticOscillatorDIndicator stochasticD(BarSeries series, int barCount) {
        StochasticOscillatorKIndicator k = new StochasticOscillatorKIndicator(series, barCount);
        return new StochasticOscillatorDIndicator(k);
    }
}
