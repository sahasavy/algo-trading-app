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
│   │   │   │   │   └── com/
│   │   │   │   │       └── algo/
│   │   │   │   │           └── trading/
│   │   │   │   │               └── auth/
│   │   │   │   │                   ├── config/
│   │   │   │   │                   │   └── CacheConfig.java
│   │   │   │   │                   ├── controller/
│   │   │   │   │                   │   └── AuthController.java
│   │   │   │   │                   ├── exception/
│   │   │   │   │                   │   ├── AuthErrorResponse.java
│   │   │   │   │                   │   ├── AuthException.java
│   │   │   │   │                   │   └── GlobalExceptionHandler.java
│   │   │   │   │                   ├── model/
│   │   │   │   │                   │   └── SessionToken.java
│   │   │   │   │                   ├── service/
│   │   │   │   │                   │   ├── KiteService.java
│   │   │   │   │                   │   └── TokenService.java
│   │   │   │   │                   ├── AuthApplication.java
│   │   │   │   │                   └── AuthProperties.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   │       └── java/
│   │   │           └── com/
│   │   │               └── algo/
│   │   │                   └── trading/
│   │   │                       └── auth/
│   │   └── build.gradle
│   ├── backtest/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/
│   │   │   │   │       └── algo/
│   │   │   │   │           └── trading/
│   │   │   │   │               └── backtest/
│   │   │   │   │                   ├── config/
│   │   │   │   │                   │   └── BacktestJobConfig.java
│   │   │   │   │                   ├── controller/
│   │   │   │   │                   │   └── BacktestController.java
│   │   │   │   │                   ├── step/
│   │   │   │   │                   │   └── BacktestStep.java
│   │   │   │   │                   └── BacktestApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   │       └── java/
│   │   │           └── com/
│   │   │               └── algo/
│   │   │                   └── trading/
│   │   │                       └── backtest/
│   │   └── build.gradle
│   ├── common/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/
│   │   │   │   │       └── algo/
│   │   │   │   │           └── trading/
│   │   │   │   │               └── common/
│   │   │   │   │                   ├── brokerage/
│   │   │   │   │                   │   ├── BrokerageCalculator.java
│   │   │   │   │                   │   ├── BrokerageConfig.java
│   │   │   │   │                   │   ├── FeeStructure.java
│   │   │   │   │                   │   ├── OrderParams.java
│   │   │   │   │                   │   └── ZerodhaBrokerageCalculator.java
│   │   │   │   │                   ├── config/
│   │   │   │   │                   │   ├── AppProperties.java
│   │   │   │   │                   │   └── SharedConfig.java
│   │   │   │   │                   ├── model/
│   │   │   │   │                   │   ├── InstrumentType.java
│   │   │   │   │                   │   ├── Order.java
│   │   │   │   │                   │   ├── OrderSide.java
│   │   │   │   │                   │   └── TradeData.java
│   │   │   │   │                   └── repository/
│   │   │   │   │                       └── TradeDataRepository.java
│   │   │   │   └── resources/
│   │   │   │       ├── db/
│   │   │   │       │   └── changelog/
│   │   │   │       │       ├── changes/
│   │   │   │       │       │   └── 20250624_create_trade_data_table.yaml
│   │   │   │       │       └── db.changelog-master.yaml
│   │   │   │       ├── application.yml
│   │   │   │       └── brokerage-schedule.yml
│   │   │   └── test/
│   │   │       └── java/
│   │   │           └── com/
│   │   │               └── algo/
│   │   │                   └── trading/
│   │   │                       └── common/
│   │   │                           └── brokerage/
│   │   │                               └── ZerodhaBrokerageCalculatorTest.java
│   │   └── build.gradle
│   ├── gui/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/
│   │   │   │   │       └── algo/
│   │   │   │   │           └── trading/
│   │   │   │   │               └── gui/
│   │   │   │   │                   ├── autologin/
│   │   │   │   │                   │   └── AutoLoginManager.java
│   │   │   │   │                   ├── config/
│   │   │   │   │                   │   └── GuiConfig.java
│   │   │   │   │                   ├── controller/
│   │   │   │   │                   │   └── MainController.java
│   │   │   │   │                   ├── util/
│   │   │   │   │                   │   └── Utilities.java
│   │   │   │   │                   ├── GuiApplication.java
│   │   │   │   │                   └── MainApp.java
│   │   │   │   └── resources/
│   │   │   │       ├── fxml/
│   │   │   │       │   └── MainView.fxml
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   │       └── java/
│   │   │           └── com/
│   │   │               └── algo/
│   │   │                   └── trading/
│   │   │                       └── gui/
│   │   └── build.gradle
│   ├── indicators/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/
│   │   │   │   │       └── algo/
│   │   │   │   │           └── trading/
│   │   │   │   │               └── indicators/
│   │   │   │   │                   ├── service/
│   │   │   │   │                   │   └── IndicatorService.java
│   │   │   │   │                   └── IndicatorsApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   │       └── java/
│   │   │           └── com/
│   │   │               └── algo/
│   │   │                   └── trading/
│   │   │                       └── indicators/
│   │   └── build.gradle
│   ├── live/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/
│   │   │   │   │       └── algo/
│   │   │   │   │           └── trading/
│   │   │   │   │               └── live/
│   │   │   │   │                   ├── config/
│   │   │   │   │                   │   └── LiveProperties.java
│   │   │   │   │                   ├── controller/
│   │   │   │   │                   │   └── LiveController.java
│   │   │   │   │                   ├── service/
│   │   │   │   │                   │   └── LiveTradingService.java
│   │   │   │   │                   └── LiveApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   │       └── java/
│   │   │           └── com/
│   │   │               └── algo/
│   │   │                   └── trading/
│   │   │                       └── live/
│   │   └── build.gradle
│   ├── ml/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/
│   │   │   │   │       └── algo/
│   │   │   │   │           └── trading/
│   │   │   │   │               └── ml/
│   │   │   │   │                   ├── config/
│   │   │   │   │                   │   ├── BatchConfig.java
│   │   │   │   │                   │   └── MlProperties.java
│   │   │   │   │                   ├── controller/
│   │   │   │   │                   │   └── MlController.java
│   │   │   │   │                   ├── step/
│   │   │   │   │                   │   ├── FeatureExtractor.java
│   │   │   │   │                   │   └── ModelTrainer.java
│   │   │   │   │                   └── MlApplication.java
│   │   │   │   └── resources/
│   │   │   │       └── application.yml
│   │   │   └── test/
│   │   │       ├── java/
│   │   │       │   └── com/
│   │   │       │       └── algo/
│   │   │       │           └── trading/
│   │   │       │               └── ml/
│   │   │       │                   └── controller/
│   │   │       │                       └── MlControllerIntegrationTest.java
│   │   │       └── resources/
│   │   │           └── application-test.yml
│   │   └── build.gradle
│   └── paper/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── com/
│       │   │   │       └── algo/
│       │   │   │           └── trading/
│       │   │   │               └── paper/
│       │   │   │                   ├── config/
│       │   │   │                   │   ├── KiteConfig.java
│       │   │   │                   │   └── PaperProperties.java
│       │   │   │                   ├── controller/
│       │   │   │                   │   └── PaperController.java
│       │   │   │                   ├── service/
│       │   │   │                   │   └── PaperTradingService.java
│       │   │   │                   └── PaperApplication.java
│       │   │   └── resources/
│       │   │       └── application.yml
│       │   └── test/
│       │       └── java/
│       │           └── com/
│       │               └── algo/
│       │                   └── trading/
│       │                       └── paper/
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

The `docker-compose.yml` defines MySQL, Redis, ZooKeeper, and Kafka—all on a shared backend network. Each service has
its own named container, volume, and exposed port, so we can start or stop them independently:

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

### 🚀 Build and Run automated login flow

```bash
# From project root:
./gradlew clean build --refresh-dependencies

export KITE_UID=AB1234          # Zerodha login ID
export KITE_PWD=SuperSecret!    # Zerodha password
export KITE_TOTP_SEED=JBSWY3DPEHPK3PXP   # 32-char base-32 seed

./gradlew :modules:gui:run
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
