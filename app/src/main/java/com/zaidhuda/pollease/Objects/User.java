package com.zaidhuda.pollease.Objects;

import java.io.Serializable;

/**
 * Created by Zaid on 19/12/2015.
 */
public class User implements Serializable {
    private final String identifier;
    private String id, token;

    public User(String identifier) {
        this.identifier = identifier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
