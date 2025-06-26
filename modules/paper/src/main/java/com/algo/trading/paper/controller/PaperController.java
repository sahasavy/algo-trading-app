package com.algo.trading.paper.controller;

import com.algo.trading.paper.service.PaperTradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paper")
@RequiredArgsConstructor
public class PaperController {

    private final PaperTradingService paperTradingService;

    @PostMapping("/start")
    public ResponseEntity<String> start() {
        paperTradingService.start();
        return ResponseEntity.accepted().body("Paper trading started");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stop() {
        paperTradingService.stop();
        return ResponseEntity.ok("Paper trading stopped");
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok(paperTradingService.isRunning() ? "RUNNING" : "STOPPED");
    }
}
