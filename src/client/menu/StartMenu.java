package client.menu;

import client.chat.gui.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartMenu extends Stage {

    private TextField inputNicknameField;
    private TextField inputIdField;


    public StartMenu() {
        VBox vb = new VBox(10);
        vb.setPadding(new Insets(10));
        vb.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vb);

        setWidth(235);
        setHeight(150);
        setScene(scene);
        setResizable(false);
        setTitle("Chat 1.0");

        Label nickLabel = new Label("Nickname: ");
        nickLabel.setMinWidth(60);

        inputNicknameField = new TextField("Anon");
        HBox hbWithNickBlock = new HBox(5, nickLabel, inputNicknameField);

        Button startChatButton = new Button("Start chatting");
        startChatButton.setAlignment(Pos.CENTER);
        startChatButton.setPrefWidth(Double.MAX_VALUE);
        startChatButton.setOnAction(event -> initClient());

        inputNicknameField.textProperty().addListener((observable, oldValue, newValue) -> {
            startChatButton.setDisable((newValue.isEmpty() || newValue.length() < 3) ||
                    (inputIdField.getText().isEmpty() || inputIdField.getText().length() < 3));

            inputNicknameField.setText(newValue.matches("((?U)\\w+)|") ? newValue : oldValue);
        });

        inputIdField = new TextField("public");
        inputIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            startChatButton.setDisable((newValue.isEmpty() || newValue.length() < 3) ||
                    (inputNicknameField.getText().isEmpty() || inputNicknameField.getText().length() < 3));

            inputIdField.setText(newValue.matches("((?U)\\w+)|") ? newValue : oldValue);
        });

        Label idLabel = new Label("Room Id: ");
        idLabel.setMinWidth(60);

        HBox hbWithIdBlock = new HBox(5, idLabel, inputIdField);

        vb.getChildren().addAll(hbWithNickBlock, startChatButton, hbWithIdBlock);

    }

    private void initClient() {
        ChatWindow v = new ChatWindow(inputNicknameField.getText(), inputIdField.getText());
        v.show();
    }
}
