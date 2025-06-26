package com.algo.trading.auth.exception;

import java.time.Instant;

/**
 * JSON structure returned on errors.
 */
public record AuthErrorResponse(Instant timestamp, String message, String details) {
}
