package com.zaidhuda.pollease.Objects;

import java.io.Serializable;

/**
 * Created by Zaid on 19/12/2015.
 */
public class User implements Serializable {
    private final String identifier;
    private String ID, token;

    public User(String identifier) {
        this.identifier = identifier;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
