# algo-trading-app
Algorithmic trading application

---

### Complete Directory Structure

```bash
./gradlew printTree
```

```bash
algo-trading-app/
├── data/
│   └── features.csv
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── modules/
│   ├── auth/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/algo/trading/auth/
│   │   │   │   │       ├── service/KiteService.java
│   │   │   │   │       ├── AuthApplication.java
│   │   │   │   │       └── AuthProperties.java
│   │   │   │   └── resources/application.yml
│   │   │   └── test/java/com/algo/trading/auth/
│   │   └── build.gradle
│   ├── backtest/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/algo/trading/backtest/
│   │   │   │   │   ├── config/BacktestJobConfig.java
│   │   │   │   │   ├── controller/BacktestController.java
│   │   │   │   │   ├── step/BacktestStep.java
│   │   │   │   │   └── BacktestApplication.java
│   │   │   │   └── resources/application.yml
│   │   │   └── test/java/com/algo/trading/backtest/
│   │   └── build.gradle
│   ├── common/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/algo/trading/common/
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── AppProperties.java
│   │   │   │   │   │   └── SharedConfig.java
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── InstrumentType.java
│   │   │   │   │   │   ├── Order.java
│   │   │   │   │   │   ├── Side.java
│   │   │   │   │   │   └── TradeData.java
│   │   │   │   │   └── repository/TradeDataRepository.java
│   │   │   │   └── resources/
│   │   │   │       ├── db/changelog/changes/20250624_create_trade_data_table.yaml
│   │   │   │       ├── db/changelog/db.changelog-master.yaml
│   │   │   │       └── application.yml
│   │   │   └── test/java/com/algo/trading/common/
│   │   └── build.gradle
│   ├── gui/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/algo/trading/gui/
│   │   │   │   │   ├── MainApp.java
│   │   │   │   │   └── MainController.java
│   │   │   │   └── resources/fxml/MainView.fxml
│   │   │   └── test/java/com/algo/trading/gui/
│   │   └── build.gradle
│   ├── indicators/
│   │   ├── src/
│   │   │   ├── main/java/com/algo/trading/indicators/service/IndicatorService.java
│   │   │   └── test/java/com/algo/trading/indicators/
│   │   └── build.gradle
│   ├── live/
│   │   ├── src/
│   │   │   ├── main/java/com/algo/trading/live/
│   │   │   │   ├── config/LiveProperties.java
│   │   │   │   ├── controller/LiveController.java
│   │   │   │   ├── service/LiveTradingService.java
│   │   │   │   └── LiveApplication.java
│   │   │   └── resources/application.yml
│   │   └── build.gradle
│   ├── ml/
│   │   ├── src/
│   │   │   ├── main/java/com/algo/trading/ml/
│   │   │   │   ├── config/BatchConfig.java
│   │   │   │   ├── config/MlProperties.java
│   │   │   │   ├── controller/MlController.java
│   │   │   │   ├── step/FeatureExtractor.java
│   │   │   │   ├── step/ModelTrainer.java
│   │   │   │   └── MlApplication.java
│   │   │   └── resources/application.yml
│   │   └── test/
│   │       ├── java/com/algo/trading/ml/controller/MlControllerIntegrationTest.java
│   │       └── resources/application-test.yml
│   │   └── build.gradle
│   └── paper/
│       ├── src/
│       │   ├── main/java/com/algo/trading/paper/
│       │   │   ├── config/KiteConfig.java
│       │   │   ├── config/PaperProperties.java
│       │   │   ├── controller/PaperController.java
│       │   │   ├── service/PaperTradingService.java
│       │   │   └── PaperApplication.java
│       │   └── resources/application.yml
│       └── test/java/com/algo/trading/paper/
│       └── build.gradle
├── HELP.md
├── README.md
├── application.yml             # profile‐aware (dev/paper/live) defaults
├── build.gradle                # root build, common plugins & dependency management
├── docker-compose.yml          # MySQL, Redis, Kafka, Zookeeper
└── settings.gradle
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
# From project root:
./gradlew clean build --refresh-dependencies

# In separate shells:
./gradlew :modules:auth:bootRun
./gradlew :modules:backtest:bootRun
./gradlew :modules:ml:bootRun
./gradlew :modules:paper:bootRun
./gradlew :modules:live:bootRun

# Finally start the GUI:
./gradlew :modules:gui:run
```

---

### 🚀 Run Tests

```bash
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
