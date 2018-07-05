package client.messages;



public abstract class Response {



    public static Response getCorrectResponse(String message, String nick){
        String[] strings = message.split("(\\s+|)&#\\^\\$\\^#&(\\s+|)");

        switch (strings[0]) { //response type
            case "/message":
                return new VisualMessage(message,nick).setupMessageBlock(strings);
            case "/pmessage":
                return new VisualMessage(message, nick).setupPrivateMessageBlock(strings);
            case "/event":
                return new VisualMessage(message, nick).setupEventBlock(strings[1]);
            case "/warning":
                return new VisualMessage(message, nick).setupWarningBlock(strings[1]);
            case "/command":
                return new CommandMessage(strings[1], strings[2]);
        }

        return null;
    }
}
