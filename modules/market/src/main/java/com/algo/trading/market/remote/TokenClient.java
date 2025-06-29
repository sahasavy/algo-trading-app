package com.algo.trading.market.remote;

import com.algo.trading.market.dto.AuthTokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenClient {

    @Value("${auth.base-url:http://localhost:8081/auth-service}")
    private String authBase;

    private final RestTemplate rest = new RestTemplate();

    public AuthTokenDTO fetch() {
        try {
            return rest.getForObject(authBase + "/auth/token", AuthTokenDTO.class);
        } catch (RestClientException ex) {
            log.warn("Cannot reach auth-service {}", ex.getMessage());
            return null;
        }
    }
}
