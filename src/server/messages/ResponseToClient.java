package server.messages;

public class ResponseToClient {

    private String response;

    private ResponseToClient(String response){
        this.response = response;
    }

    public static ResponseToClient COMMAND(String command, String argument) {
        return new ResponseToClient("/command&#^$^#&" + command + "&#^$^#&" + argument);
    }
    public static ResponseToClient COMMAND(String command) {
        return COMMAND(command,"_empty_");
    }

    public static ResponseToClient PUBLIC_MESSAGE(String name, String message) {
        return new ResponseToClient("/message&#^$^#&" + name + "&#^$^#&" + message);
    }

    public static ResponseToClient WARNING(String warning) {
        return new ResponseToClient("/warning&#^$^#&" + warning);
    }

    public static ResponseToClient NOTE(String note) {
        return new ResponseToClient("/event&#^$^#&" + note);
    }

    public static ResponseToClient PRIVATE_MESSAGE(String name, String message, String reciever) {
        return new ResponseToClient("/pmessage&#^$^#&" + name + "&#^$^#&" + message + "&#^$^#&" + reciever);
    }

    @Override
    public String toString() {
        return response;
    }
}
