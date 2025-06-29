package com.algo.trading.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API-key is public anyway; token fields are for trusted internal consumers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenDTO {
    private String apiKey;
    private String accessToken;
    private String publicToken;
}
