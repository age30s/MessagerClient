import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    String outMessage;
    String clientUser;
    String message;
    Boolean login = false;
    ArrayList<String> usersOnClient = new ArrayList<>();
    String exception;

    Boolean isEveryone = false;
    Message(String user){
        this.clientUser = user;

    }

    void setRecipient(String m){
        outMessage = m;
    }

    void setText(String s){
        message = s;
    }
}