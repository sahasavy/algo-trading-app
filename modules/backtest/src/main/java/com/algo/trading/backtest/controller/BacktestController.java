package com.algo.trading.backtest.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/backtest")
public class BacktestController {

    private final JobLauncher jobLauncher;
    private final Job backtestJob;

    public BacktestController(JobLauncher jobLauncher, Job backtestJob) {
        this.jobLauncher = jobLauncher;
        this.backtestJob = backtestJob;
    }

    @PostMapping("/run")
    public ResponseEntity<String> runBacktest() throws Exception {
        var params = new JobParametersBuilder()
                .addLong("runId", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(backtestJob, params);
        return ResponseEntity.accepted()
                .body("Backtest job started");
    }
}
