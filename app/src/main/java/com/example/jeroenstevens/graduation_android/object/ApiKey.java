package com.example.jeroenstevens.graduation_android.object;

import java.io.Serializable;

public class ApiKey implements Serializable {
    private String id;
    private String accessToken;
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

