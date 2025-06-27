package com.algo.trading.ml.config;

import com.algo.trading.ml.step.FeatureExtractor;
import com.algo.trading.ml.step.ModelTrainer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Step extractFeaturesStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    FeatureExtractor featureExtractor) {
        return new StepBuilder("extractFeatures", jobRepository)
                .tasklet(featureExtractor, transactionManager)
                .build();
    }

    @Bean
    public Step trainModelStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               ModelTrainer modelTrainer) {
        return new StepBuilder("trainModel", jobRepository)
                .tasklet(modelTrainer, transactionManager)
                .build();
    }

    @Bean
    public Job mlJob(JobRepository jobRepository,
                     Step extractFeaturesStep,
                     Step trainModelStep) {
        return new JobBuilder("mlPipeline", jobRepository)
                .start(extractFeaturesStep)
                .next(trainModelStep)
                .build();
    }
}
