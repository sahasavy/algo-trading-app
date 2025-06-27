package com.algo.trading.auth.controller;

import com.algo.trading.auth.exception.AuthException;
import com.algo.trading.auth.service.KiteService;
import com.algo.trading.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KiteService kiteService;
    private final TokenService tokenService;

    /**
     * ➜  step-1: give GUI the login URL
     */
    @GetMapping("/login")
    public ResponseEntity<String> loginUrl() {
        try {
            log.info("Building login URL");
            return ResponseEntity.ok(kiteService.getLoginUrl());
        } catch (Exception e) {
            log.error("Failed to generate login URL", e);
            throw new AuthException("Failed to generate login URL", e);
        }
    }

    /**
     * ➜  step-2: Zerodha redirects back with request_token
     */
    @GetMapping("/session")
    public ResponseEntity<String> createSession(@RequestParam("request_token") String requestToken) {
        log.info("Received callback with request_token={}", requestToken);

        // Idempotency guard — silently ignore duplicates
        if (tokenService.isRequestTokenSeen(requestToken)) {
            log.info("Request token already processed — ignoring duplicate");
            return ResponseEntity.ok("ALREADY_DONE");
        }

        try {
            kiteService.generateSession(requestToken);
            tokenService.markRequestTokenSeen(requestToken);
            log.info("Session successfully created");
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error exchanging request token for session", e);
            throw new AuthException("Failed to create session", e);
        }
    }
}
