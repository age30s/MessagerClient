import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    String outMessage;
    String clientUser;
    String message;
    Boolean login = false;
    HashMap<Integer, String> usersOnClient = new HashMap<>();


    Message(String user){
        this.clientUser = user;

    }
}