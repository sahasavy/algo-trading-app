package com.algo.trading.backtest;

import com.algo.trading.backtest.spec.RuleSpec;
import com.algo.trading.backtest.spec.StrategySpec;
import com.algo.trading.common.brokerage.BrokerageCalculator;
import com.algo.trading.common.brokerage.TradingSegment;
import com.algo.trading.indicators.Op;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.indicators.ConstantIndicator;
import org.ta4j.core.num.DoubleNum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BacktestEngine {

    private final RuleFactory ruleFactory;
    private final BrokerageCalculator brokerage;

    /* ------------------------------------------------------------------ */

    public BacktestReport run(String symbol,
                              BarSeries series,
                              StrategySpec spec,
                              int qty) {

        Strategy strategy = buildStrategy(series, spec);

        BarSeriesManager mgr = new BarSeriesManager(series);
        TradingRecord rec = mgr.run(strategy, Order.OrderType.BUY, qty);

        return analyse(series, rec, spec.getDescription(), qty);
    }

    /* ====== strategy builder ========================================= */

    private Strategy buildStrategy(BarSeries s, StrategySpec spec) {

        Rule entry = null;
        for (RuleSpec rs : spec.getEntry()) {
            Rule r = ruleFactory.build(s, rs);
            entry = (entry == null) ? r : entry.and(r);
        }
        Rule exit = null;
        for (RuleSpec rs : spec.getExit()) {
            Rule r = ruleFactory.build(s, rs);
            exit = (exit == null) ? r : exit.and(r);
        }
        return new BaseStrategy(spec.getDescription(), entry, exit);
    }

    /* ====== analyse TradingRecord ==================================== */

    private BacktestReport analyse(BarSeries s,
                                   TradingRecord rec,
                                   String name,
                                   int qty) {

        double grossP = 0, grossL = 0, net = 0;
        int win = 0, lose = 0;

        List<Double> equity = new ArrayList<>();
        double eq = 0;

        for (Trade t : rec.getTrades()) {

            double buy = price(t.getEntry());
            double sell = price(t.getExit());
            double pl = (sell - buy) * qty;

            double cost = brokerage.roundTrip(TradingSegment.EQUITY_INTRADAY,
                    qty, buy, sell);

            net += pl - cost;
            if (pl > 0) {
                grossP += pl;
                win++;
            } else {
                grossL += pl;
                lose++;
            }

            eq += pl - cost;
            equity.add(eq);
        }

        double winRate = rec.getTradeCount() == 0 ? 0 :
                100.0 * win / rec.getTradeCount();

        // Max DD via TA4J criterion
        MaximumDrawdownCriterion mdd = new MaximumDrawdownCriterion();
        Num dd = mdd.calculate(s, rec).multipliedBy(DoubleNum.valueOf(100));

        return BacktestReport.builder()
                .strategyName(name)
                .tradeCount(rec.getTradeCount())
                .winners(win)
                .losers(lose)
                .grossProfit(grossP)
                .grossLoss(grossL)
                .netProfit(net)
                .winRate(winRate)
                .maxDrawdown(dd.doubleValue())
                .equity(equity)
                .build();
    }

    /* helper */
    private static double price(Order o) {
        return o.getNetPrice().doubleValue();
    }
}
