package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        this.controller = loader.getController();
        primaryStage.setTitle("Currency Calculator");
        primaryStage.setScene(new Scene(root, 410, 400));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        controller.shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
