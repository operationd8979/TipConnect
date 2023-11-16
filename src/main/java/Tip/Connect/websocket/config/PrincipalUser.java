package Tip.Connect.websocket.config;

import java.security.Principal;

public class PrincipalUser implements Principal {

    private String userID;


    public PrincipalUser(String userID) {
        this.userID = userID;
    }

    @Override
    public String getName() {
        return userID;
    }

}