package com.algo.trading.live.controller;

import com.algo.trading.live.service.LiveTradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/live")
@RequiredArgsConstructor
public class LiveController {

    private final LiveTradingService liveTradingService;

    @PostMapping("/start")
    public ResponseEntity<String> start() {
        liveTradingService.start();
        return ResponseEntity.accepted().body("Live trading started");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stop() {
        liveTradingService.stop();
        return ResponseEntity.ok("Live trading stopped");
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok(liveTradingService.isRunning() ? "RUNNING" : "STOPPED");
    }
}
