package com.algo.trading.auth.controller;

import com.algo.trading.auth.service.KiteService;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KiteService kiteService;

    /** Returns the URL where the user should be redirected to log in. */
    @GetMapping("/login")
    public ResponseEntity<String> loginUrl() {
        return ResponseEntity.ok(kiteService.getLoginUrl());
    }

    /**
     * Callback endpoint Zerodha will redirect to with a request_token query param.
     * We exchange it for a session here.
     */
    @GetMapping("/session")
    public ResponseEntity<String> createSession(@RequestParam("request_token") String requestToken)
            throws KiteException, IOException {
        kiteService.generateSession(requestToken);
        return ResponseEntity.ok("OK");
    }
}
