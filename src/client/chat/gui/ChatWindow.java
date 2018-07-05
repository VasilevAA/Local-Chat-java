package client.chat.gui;

import client.chat.logic.ChatLogic;
import client.messages.CommandMessage;
import client.messages.Response;
import client.messages.VisualMessage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChatWindow extends Stage {

    private ChatLogic chatLogic;

    private VBox outputArea;
    private ScrollPane scrollForOutput;
    private TextArea inputTextField;

    public ChatWindow(String nick, String id) {

        String nickname = (nick.isEmpty() ? "Anon" : nick.trim());
        String roomId = id.trim().isEmpty() ? "public" : id.trim();

        setTitle("Nickname: " + nickname + "; Room: " + roomId);
        setMinHeight(400);
        setMinWidth(400);
        setHeight(400);
        setWidth(400);
        initModality(Modality.APPLICATION_MODAL);

        Button sendButton = new Button("Send");
        sendButton.setMinWidth(50);

        HBox hb = new HBox(5, inputTextField = new TextArea(), sendButton);
        HBox.setHgrow(inputTextField, Priority.ALWAYS);
        hb.setAlignment(Pos.CENTER);
        inputTextField.setWrapText(true);

        outputArea = new VBox(2);
        outputArea.setAlignment(Pos.TOP_CENTER);
        outputArea.heightProperty().addListener((observable, oldValue, newValue) -> scrollForOutput.setVvalue((Double) newValue));

        HBox.setHgrow(outputArea, Priority.ALWAYS);
        SplitPane vb = new SplitPane(scrollForOutput = new ScrollPane(outputArea), hb);
        HBox.setHgrow(scrollForOutput, Priority.ALWAYS);
        scrollForOutput.setFitToWidth(true);

        vb.setOrientation(Orientation.VERTICAL);
        vb.setPadding(new Insets(5));
        vb.setDividerPositions(0.8f);

        Scene scene = new Scene(vb);
        setScene(scene);

        inputTextField.setOnKeyPressed(event -> {
            if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
                inputTextField.appendText("\n");
                return;
            }
            if (event.getCode() == KeyCode.ENTER) {
                sendButton.fire();
            }
        });

        sendButton.setOnAction(event -> {
            if (!inputTextField.getText().trim().isEmpty()) {
                sendMessage(inputTextField.getText());
            }
        });

        setOnCloseRequest(event -> chatLogic.freeResources());

        inputTextField.requestFocus();

        chatLogic = new ChatLogic(nickname, roomId, this);
    }


    private void sendMessage(String message) {
        chatLogic.sendRequest(message);
        inputTextField.clear();
    }

    public void receiveMessage(Response response) {
        Platform.runLater(() -> {

            if (response instanceof VisualMessage) {
                outputArea.getChildren().add(((VisualMessage) response).getMessageBlock());
                return;
            }

            if (response instanceof CommandMessage) {

                CommandMessage command = ((CommandMessage) response);
                switch (command.getType()) {
                    case NICK_CHANGE:
                        updateNickname(command.getArgument());
                        break;
                    case ROOM_CHANGE:
                        updateRoomId(command.getArgument());
                        break;
                    case EXIT:
                        chatLogic.freeResources();
                        close();
                        break;
                    case CLEAR:
                        outputArea.getChildren().clear();
                        updateRoomId(chatLogic.getRoomId());
                        break;
                    case NO_COMMAND:
                    default:
                        break;
                }
            }
        });


    }

    private void updateNickname(String newNick) {
        chatLogic.setNickname(newNick);
        setTitle("Nickname: " + chatLogic.getNickname() + "; Room: " + chatLogic.getRoomId());

    }

    private void updateRoomId(String newId) {
        chatLogic.setRoomId(newId);

        Separator sep = new Separator();
        sep.setMinHeight(4);

        Label roomTitle = new Label(chatLogic.getRoomId());
        roomTitle.setStyle("-fx-font-weight: bold;");

        outputArea.getChildren().addAll(sep, roomTitle);
        setTitle("Nickname: " + chatLogic.getNickname() + " ; Room: " + chatLogic.getRoomId());
    }


}
