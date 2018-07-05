package client;

import client.menu.StartMenu;
import javafx.application.Application;
import javafx.stage.Stage;

public class RunClient extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new StartMenu().show();
    }
}
