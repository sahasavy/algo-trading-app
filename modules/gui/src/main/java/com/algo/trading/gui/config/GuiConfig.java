package com.algo.trading.gui.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class GuiConfig {

    @Value("${auth.baseUrl}")
    private String authBaseUrl;

    @Value("${backtest.baseUrl}")
    private String backtestBaseUrl;

    @Value("${ml.baseUrl}")
    private String mlBaseUrl;

    @Value("${paper.baseUrl}")
    private String paperBaseUrl;

    @Value("${live.baseUrl}")
    private String liveBaseUrl;
}
