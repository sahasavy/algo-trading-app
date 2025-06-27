package com.algo.trading.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class MainApp extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        log.info("Starting Spring context for GUI");
        springContext = new SpringApplicationBuilder(GuiApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            log.info("Loading JavaFX scene from FXML");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            primaryStage.setTitle("Algo Trading GUI");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            log.error("Failed to start JavaFX application", e);
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        log.info("Shutting down Spring context and exiting");
        springContext.close();
        Platform.exit();
    }
}
