package com.algo.trading.market.event;

import org.springframework.context.ApplicationEvent;

public abstract class WsEvent extends ApplicationEvent {
    protected WsEvent(Object src) {
        super(src);
    }
}
