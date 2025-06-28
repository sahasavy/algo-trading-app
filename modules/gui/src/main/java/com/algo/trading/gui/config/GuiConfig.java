package com.algo.trading.gui.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class GuiConfig {

    @Value("${services.auth.baseUrl}")
    private String authBaseUrl;

    @Value("${services.market.baseUrl}")
    private String marketBaseUrl;

    @Value("${services.ml.baseUrl}")
    private String mlBaseUrl;

    @Value("${services.backtest.baseUrl}")
    private String backtestBaseUrl;

    @Value("${services.paper.baseUrl}")
    private String paperBaseUrl;

    @Value("${services.live.baseUrl}")
    private String liveBaseUrl;
}
