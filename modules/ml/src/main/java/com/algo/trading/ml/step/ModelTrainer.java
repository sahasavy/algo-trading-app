package com.algo.trading.ml.step;

import com.algo.trading.ml.config.MlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;

@Component
@RequiredArgsConstructor
public class ModelTrainer implements Tasklet {

    private final MlProperties props;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // 1. Load features CSV
        CSVLoader loader = new CSVLoader();
//        loader.setSource(new File("data/features.csv"));
        loader.setSource(new File(props.getFeaturePath(), "features.csv"));
        Instances data = loader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1); // assume last column = label

        // 2. Train a simple RandomForest
        Classifier rf = new RandomForest();
        rf.buildClassifier(data);

        // 3. Serialize model to disk
        weka.core.SerializationHelper.write(props.getModelOutputPath(), rf);
        return RepeatStatus.FINISHED;
    }
}
