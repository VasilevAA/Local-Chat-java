package client.messages;

public class CommandMessage extends Response{
    public enum Type{
        NICK_CHANGE,
        ROOM_CHANGE,
        EXIT,
        NO_COMMAND,
        CLEAR
    }

    private Type type;
    private String argument;

    public String getArgument() {
        return argument;
    }

    public Type getType() {
        return type;
    }

    CommandMessage(String strType, String argument) {
        switch (strType){
            case "exit":
                type = Type.EXIT;
                break;
            case "nick_change":
                type = Type.NICK_CHANGE;
                break;
            case "room_change":
                type = Type.ROOM_CHANGE;
                break;
            case "clear":
                type = Type.CLEAR;
                break;
        }
        this.argument = argument;
    }

}
