package com.algo.trading.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionToken {
    private String username;
    private String accessToken;
    private String publicToken;
    private LocalDate tradeDate;
}
