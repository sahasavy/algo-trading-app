package com.algo.trading.backtest.spec;

import lombok.Data;
import java.util.List;

/** Deserialized from YAML/JSON by ObjectMapper. */
@Data
public class StrategySpec {
    private String description;
    private List<RuleSpec> entry;
    private List<RuleSpec> exit;
    private boolean mlConfirm;     // reserved for later
    // Later we can add stopLossPercent, takeProfitPercent, etc.
}
