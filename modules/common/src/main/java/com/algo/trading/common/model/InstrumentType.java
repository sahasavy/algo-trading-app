package com.algo.trading.common.model;

public enum InstrumentType {
    EQUITY_DELIVERY,   // equity delivery (CNC)
    EQUITY_INTRADAY,   // equity intraday (MIS)
    FNO_FUTURE,       // index / stock futures
    FNO_OPTION        // index / stock options
}