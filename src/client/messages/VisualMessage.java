package client.messages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VisualMessage extends Response {

    private VBox messageBlock = new VBox(1);
    private String nick;
    private String message;


    public VBox getMessageBlock() {
        return messageBlock;
    }

    VisualMessage(String message, String nickname) {
        this.message = message;
        this.nick = nickname;
    }

    VisualMessage setupPrivateMessageBlock(String[] strings) {

        messageBlock.setStyle("-fx-background-color: lightblue;-fx-border-color: black;" +
                "-fx-border-width: 0.5px;");
        messageBlock.setPadding(new Insets(4));
        HBox.setHgrow(messageBlock, Priority.ALWAYS);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();

        Label nick = new Label(strings[1] + " - " + dtf.format(now));
        nick.setAlignment(Pos.CENTER_LEFT);
        nick.setStyle("-fx-font-weight: bold");
        nick.setPadding(new Insets(5));

        Label pm = new Label("to " + strings[3]);
        pm.setAlignment(Pos.CENTER);
        pm.setStyle("-fx-background-color: tomato;-fx-border-color: black;" +
                "-fx-border-width: 0.5px;-fx-font-weight: bold");
        pm.setPadding(new Insets(5));

        HBox hh = new HBox(1, nick, pm);

        Label text = new Label(strings[2]);
        text.setAlignment(Pos.CENTER_LEFT);
        text.setPadding(new Insets(5));
        text.setWrapText(true);
        HBox.setHgrow(text, Priority.ALWAYS);


        if (strings[1].equals(this.nick)) {
            messageBlock.setAlignment(Pos.CENTER_RIGHT);
            text.setAlignment(Pos.CENTER_RIGHT);
            hh.setAlignment(Pos.CENTER_RIGHT);
        }

        messageBlock.getChildren().addAll(hh, text);
        return this;
    }

    VisualMessage setupMessageBlock(String[] strs) {
        messageBlock.setStyle("-fx-background-color: white;-fx-border-color: black;" +
                "-fx-border-width: 0.5px;");
        messageBlock.setPadding(new Insets(4));
        HBox.setHgrow(messageBlock, Priority.ALWAYS);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();

        Label nick = new Label(strs[1] + " - " + dtf.format(now));
        nick.setAlignment(Pos.CENTER_LEFT);
        nick.setStyle("-fx-font-weight: bold");
        nick.setPadding(new Insets(5));

        Label text = new Label(strs[2]);
        text.setAlignment(Pos.CENTER_LEFT);
        text.setPadding(new Insets(5));
        text.setWrapText(true);
        HBox.setHgrow(text, Priority.ALWAYS);

        if (strs[1].equals(this.nick)) {
            messageBlock.setAlignment(Pos.CENTER_RIGHT);
            text.setAlignment(Pos.CENTER_RIGHT);
        }

        messageBlock.getChildren().addAll(nick, text);

        return this;
    }

    VisualMessage setupWarningBlock(String warning) {
        messageBlock.setPadding(new Insets(4));
        messageBlock.setAlignment(Pos.CENTER_RIGHT);

        Label text = new Label(warning);
        text.setWrapText(true);
        text.setPadding(new Insets(5));
        text.setStyle("-fx-background-radius: 5;-fx-border-radius: 5;-fx-background-color: lightcoral;-fx-border-color: black;" +
                "-fx-border-width: 0.5px;");

        messageBlock.getChildren().add(text);

        return this;
    }

    VisualMessage setupEventBlock(String event) {
        messageBlock.setPadding(new Insets(4));
        messageBlock.setAlignment(Pos.CENTER_RIGHT);

        Label text = new Label(event);
        text.setWrapText(true);
        text.setPadding(new Insets(5));
        text.setStyle("-fx-background-radius: 5;-fx-border-radius: 5;-fx-background-color: khaki;-fx-border-color: black;" +
                "-fx-border-width: 0.5px;");

        messageBlock.getChildren().add(text);

        return this;
    }
}
