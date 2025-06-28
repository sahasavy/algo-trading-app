package com.algo.trading.market.cache;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bars",
        indexes = @Index(columnList = "instrumentToken,startEpochMs", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarEntity {

    @Id
    @GeneratedValue
    private Long id;

    private long instrumentToken;
    private long startEpochMs;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
}
