package com.algo.trading.common.brokerage;

import com.algo.trading.common.model.enums.InstrumentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Loads fee structures from YAML exactly once (thread-safe lazy singleton).
 */
@Getter
public final class BrokerageConfig {

    private static final BrokerageConfig INSTANCE = new BrokerageConfig("/brokerage-schedule.yml");

    private final Map<InstrumentType, FeeStructure> feeTable = new EnumMap<>(InstrumentType.class);
    private final double gstPercent;

    public static BrokerageConfig get() {
        return INSTANCE;
    }

    private BrokerageConfig(String classpathResource) {
        try (InputStream in = getClass().getResourceAsStream(classpathResource)) {
            ObjectMapper yamlObjectMapper = new ObjectMapper(new YAMLFactory());
            YamlRoot root = yamlObjectMapper.readValue(in, YamlRoot.class);
            this.gstPercent = root.gst_percent;

            root.segments.forEach((k, v) ->
                    feeTable.put(InstrumentType.valueOf(k), v.toFeeStructure()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load brokerage-schedule.yml", e);
        }
    }

    private record YamlRoot(double gst_percent, Map<String, YamlSegment> segments) {
    }

    private record YamlSegment(
            double brokerage_percent,
            double brokerage_cap,
            double stt_percent_buy,
            double stt_percent_sell,
            double txn_percent_nse,
            double txn_percent_bse,
            double sebi_per_crore,
            double stamp_percent_buy) {

        FeeStructure toFeeStructure() {
            return FeeStructure.builder()
                    .brokeragePercent(brokerage_percent)
                    .brokerageCap(brokerage_cap)
                    .sttPercentBuy(stt_percent_buy)
                    .sttPercentSell(stt_percent_sell)
                    .txnPercentNse(txn_percent_nse)
                    .txnPercentBse(txn_percent_bse)
                    .sebiPerCrore(sebi_per_crore)
                    .stampPercentBuy(stamp_percent_buy)
                    .build();
        }
    }
}
