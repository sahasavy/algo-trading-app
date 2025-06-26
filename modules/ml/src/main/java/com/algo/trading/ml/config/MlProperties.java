package com.algo.trading.ml.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.ml")
@Data
public class MlProperties {
    private int trainingPeriodDays;
    private int testingPeriodDays;
    private String featurePath;
    private String modelOutputPath;
}
