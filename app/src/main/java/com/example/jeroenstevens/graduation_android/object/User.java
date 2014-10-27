package com.example.jeroenstevens.graduation_android.object;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private final String email;
    private final String password;
    private final String userName;
    private ApiKey apiKey;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.userName = email.split("@")[0];
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public ApiKey getApiKey() {
        return apiKey;
    }

    public void setApiKey(ApiKey apiKey) {
        this.apiKey = apiKey;
    }
}

