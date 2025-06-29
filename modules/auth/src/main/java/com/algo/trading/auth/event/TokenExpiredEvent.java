package com.algo.trading.auth.event;

import org.springframework.context.ApplicationEvent;

public class TokenExpiredEvent extends ApplicationEvent {
    public TokenExpiredEvent(Object src) {
        super(src);
    }
}
