package server.messages;

public class MessageToClient {

    private String response;

    private MessageToClient(String response){
        this.response = response;
    }

    public static MessageToClient COMMAND(String command,String argument) {
        return new MessageToClient("/command&#^$^#&" + command + "&#^$^#&" + argument);
    }
    public static MessageToClient COMMAND(String command) {
        return COMMAND(command,"_empty_");
    }

    public static MessageToClient PUBLIC_MESSAGE(String name, String message) {
        return new MessageToClient("/message&#^$^#&" + name + "&#^$^#&" + message);
    }

    public static MessageToClient WARNING(String warning) {
        return new MessageToClient("/warning&#^$^#&" + warning);
    }

    public static MessageToClient NOTE(String note) {
        return new MessageToClient("/event&#^$^#&" + note);
    }

    public static MessageToClient PRIVATE_MESSAGE(String name, String message, String reciever) {
        return new MessageToClient("/pmessage&#^$^#&" + name + "&#^$^#&" + message + "&#^$^#&" + reciever);
    }

    @Override
    public String toString() {
        return response;
    }
}
