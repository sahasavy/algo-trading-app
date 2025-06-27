package com.algo.trading.ml.step;

import com.algo.trading.common.model.TradeData;
import com.algo.trading.common.repository.TradeDataRepository;
import com.algo.trading.indicators.service.IndicatorService;
import com.algo.trading.ml.config.MlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.CSVSaver;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeatureExtractor implements Tasklet {

    private final TradeDataRepository repo;
    private final MlProperties props;
    private final IndicatorService indicatorService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        var end = java.time.LocalDateTime.now();
        var start = end.minusDays(props.getTrainingPeriodDays());
        List<TradeData> list = repo.findByTimestampBetween(start, end);

        BarSeries series = new BaseBarSeriesBuilder().withName("ml_series").build();
        for (TradeData td : list) {
            Instant endTime = td.getTimestamp()
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

            Num open = DoubleNum.valueOf(td.getOpen());
            Num high = DoubleNum.valueOf(td.getHigh());
            Num low = DoubleNum.valueOf(td.getLow());
            Num close = DoubleNum.valueOf(td.getClose());
            Num volume = DoubleNum.valueOf(td.getVolume());

//            Num amount = open.multipliedBy(volume);
            Num amount = close.multipliedBy(volume);

            // no. of trades unknown → set 0
            long trades = 0L;

            // build a 1‐minute bar
            Bar bar = new BaseBar(Duration.ofMinutes(1), endTime, open, high, low, close, volume, amount, trades);
            series.addBar(bar);
        }

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("timestamp", "yyyy-MM-dd'T'HH:mm:ss"));
        attributes.add(new Attribute("open"));
        attributes.add(new Attribute("high"));
        attributes.add(new Attribute("low"));
        attributes.add(new Attribute("close"));
        attributes.add(new Attribute("volume"));
        attributes.add(new Attribute("sma10"));
        attributes.add(new Attribute("label"));

        Instances dataset = new Instances("features", attributes, series.getBarCount());
        dataset.setClassIndex(dataset.numAttributes() - 1);

        for (int i = 0; i < series.getBarCount(); i++) {
            var bar = series.getBar(i);
            double sma10 = new SMAIndicator(new ClosePriceIndicator(series), 10)
                    .getValue(i).doubleValue();
            var inst = new DenseInstance(attributes.size());
            inst.setValue(attributes.get(0), bar.getEndTime().toString());
            inst.setValue(attributes.get(1), bar.getOpenPrice().doubleValue());
            inst.setValue(attributes.get(2), bar.getHighPrice().doubleValue());
            inst.setValue(attributes.get(3), bar.getLowPrice().doubleValue());
            inst.setValue(attributes.get(4), bar.getClosePrice().doubleValue());
            inst.setValue(attributes.get(5), bar.getVolume().doubleValue());
            inst.setValue(attributes.get(6), sma10);
            inst.setValue(attributes.get(7), 1);  // placeholder label
            dataset.add(inst);
        }

        CSVSaver saver = new CSVSaver();
        saver.setInstances(dataset);
        File file = new File(props.getFeaturePath(), "features.csv");
        file.getParentFile().mkdirs();
        saver.setFile(file);
        saver.writeBatch();

        return RepeatStatus.FINISHED;
    }
}
