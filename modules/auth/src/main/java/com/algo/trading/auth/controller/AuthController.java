package com.algo.trading.auth.controller;

import com.algo.trading.auth.exception.AuthException;
import com.algo.trading.auth.service.KiteService;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KiteService kiteService;

    /**
     * Endpoint to fetch the Zerodha login URL.
     */
    @GetMapping("/login")
    public ResponseEntity<String> loginUrl() {
        try {
            log.info("Started user login process");
            String url = kiteService.getLoginUrl();
            log.info("Provided login URL to client");
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("Failed to generate login URL", e);
            throw new AuthException("Failed to generate login URL", e);
        }
    }

    /**
     * Callback endpoint Zerodha will redirect to with a request_token query param.
     * We exchange it for a session here.
     */
    @GetMapping("/session")
    public ResponseEntity<String> createSession(@RequestParam("request_token") String requestToken) {
        try {
            log.info("Received callback with request_token={}", requestToken);
            kiteService.generateSession(requestToken);
            log.info("Session successfully created");
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error exchanging request token for session", e);
            throw new AuthException("Failed to create session", e);
        }
    }
}
