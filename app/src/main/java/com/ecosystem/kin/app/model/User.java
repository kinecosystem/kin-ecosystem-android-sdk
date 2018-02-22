package com.ecosystem.kin.app.model;

import java.util.UUID;

/**
 * Created by yohaybarski on 20/02/2018.
 */

public class User {

    private UUID userID;

    public User(UUID userID) {
        this.userID = userID;
    }

    public User(String userID) {
        this(UUID.fromString(userID));
    }

    public UUID getUserID() {
        return userID;
    }

}
