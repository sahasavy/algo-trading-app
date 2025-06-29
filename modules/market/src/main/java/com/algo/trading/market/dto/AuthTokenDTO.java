package com.algo.trading.market.dto;

import lombok.Data;

@Data
public class AuthTokenDTO {
    private String apiKey;
    private String accessToken;
    private String publicToken;
}
