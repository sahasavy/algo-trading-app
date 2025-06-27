package com.algo.trading.ml.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ml")
public class MlController {

    private final JobLauncher jobLauncher;
    private final Job mlJob;

    public MlController(JobLauncher jobLauncher, Job mlJob) {
        this.jobLauncher = jobLauncher;
        this.mlJob = mlJob;
    }

    @PostMapping("/train")
    public ResponseEntity<String> trainModel() throws Exception {
        var params = new JobParametersBuilder()
                .addLong("runId", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(mlJob, params);
        return ResponseEntity.accepted().body("ML training job started");
    }
}
