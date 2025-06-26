# algo-trading-app
Algorithmic trading application

---

### Complete Directory Structure

```bash
algo-trading-app/
├── settings.gradle
├── build.gradle                # root build, common plugins & dependency management
├── docker-compose.yml          # MySQL, Redis, Kafka, Zookeeper
├── application.yml             # profile‐aware (dev/paper/live) defaults
│
└── modules/
    ├── common/                 # shared models, configs, utils
    │   ├── build.gradle
    │   └── src/
    │       ├── main/
    │       │   ├── java/
    │       │   │   └── com/algo/trading/common/
    │       │   │       ├── config/
    │       │   │       │   ├── AppProperties.java
    │       │   │       │   └── SharedConfig.java
    │       │   │       └── model/
    │       │   │           ├── Order.java
    │       │   │           ├── InstrumentType.java
    │       │   │           └── Side.java
    │       │   └── resources/
    │       │       └── application.yml   # module‐specific overrides if any
    │       └── test/
    │           └── java/...             # unit tests for common code
    │
    ├── auth/                   # Zerodha login & data retrieval
    │   ├── build.gradle
    │   └── src/
    │       ├── main/
    │       │   ├── java/
    │       │   │   └── com/algo/trading/auth/
    │       │   │       ├── AuthApplication.java
    │       │   │       ├── AuthProperties.java
    │       │   │       └── KiteService.java
    │       │   └── resources/
    │       │       ├── application-dev.yml
    │       │       ├── application-paper.yml
    │       │       └── application-live.yml
    │       └── test/…                # tests for auth flows
    │
    ├── indicators/             # TA4J indicator registry & calculations
    │   ├── build.gradle
    │   └── src/
    │       ├── main/java/com/algo/trading/indicators/
    │       │   ├── IndicatorFactory.java
    │       │   ├── CachedSuperTrendIndicator.java
    │       │   └── …  
    │       └── test/…            # indicator unit tests
    │
    ├── ml/                     # feature pipeline & Weka/DL4J training
    │   ├── build.gradle
    │   └── src/
    │       ├── main/java/com/algo/trading/ml/
    │       │   ├── BatchConfig.java
    │       │   ├── FeatureExtractor.java
    │       │   └── ModelService.java
    │       └── test/…
    │
    ├── backtest/               # Spring Batch jobs + TA4J backtesting
    │   ├── build.gradle
    │   └── src/
    │       ├── main/java/com/algo/trading/backtest/
    │       │   ├── BacktestJobConfig.java
    │       │   └── BacktestService.java
    │       └── test/…
    │
    ├── paper/                  # real-time simulation over Kafka
    │   ├── build.gradle
    │   └── src/
    │       ├── main/java/com/algo/trading/paper/
    │       │   ├── PaperTradingService.java
    │       │   └── …  
    │       └── test/…
    │
    ├── live/                   # actual order execution with Resilience4j
    │   ├── build.gradle
    │   └── src/
    │       ├── main/java/com/algo/trading/live/
    │       │   ├── LiveTradingService.java
    │       │   └── …  
    │       └── test/…
    │
    └── gui/                    # JavaFX desktop app
        ├── build.gradle
        └── src/
            ├── main/java/com/algo/trading/gui/
            │   ├── GuiApplication.java
            │   └── controllers/…
            └── main/resources/
                ├── fxml/…    # .fxml view files
                └── static/…  # charts, icons, CSS, etc.
```

---

### Docker Compose commands:

The `docker-compose.yml` defines MySQL, Redis, ZooKeeper, and Kafka—all on a shared backend network. Each service has its own named container, volume, and exposed port, so we can start or stop them independently:

1. Start all:
```bash
docker-compose up -d
```

2. Start just MySQL (for example):
```bash
docker-compose up -d mysql
```

3. Stop Redis:
```bash
docker-compose stop redis
```

4. Remove Kafka:
```bash
docker-compose rm -f kafka
```

---

### 🚀 Run & Build

```bash
./gradlew clean build
./gradlew test
```

---

### 🚀 Test Coverage

```bash
./gradlew jacocoTestReport
```

---

## Maintainers

1. Sourav Saha (sahasavy@gmail.com)
