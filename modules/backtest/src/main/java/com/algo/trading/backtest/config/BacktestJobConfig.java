package com.algo.trading.backtest.config;

import com.algo.trading.backtest.step.BacktestStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BacktestJobConfig {

    @Bean
    public Step backtestRunStep(JobRepository jobRepository,
                                PlatformTransactionManager txManager,
                                BacktestStep backtestStep) {
        return new StepBuilder("backtestRun", jobRepository)
                .tasklet(backtestStep, txManager)
                .build();
    }

    @Bean
    public Job backtestJob(JobRepository jobRepository, Step backtestRunStep) {
        return new JobBuilder("backtestJob", jobRepository)
                .start(backtestRunStep)
                .build();
    }
}
