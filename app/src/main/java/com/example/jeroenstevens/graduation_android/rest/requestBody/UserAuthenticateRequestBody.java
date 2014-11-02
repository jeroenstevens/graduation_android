package com.example.jeroenstevens.graduation_android.rest.requestBody;

import com.google.gson.annotations.Expose;

public class UserAuthenticateRequestBody {

    @Expose
    private final String email;
    @Expose
    private final String password;
    @Expose
    private final String scope;

    public UserAuthenticateRequestBody(String email, String password) {
        this.email = email;
        this.password = password;
        this.scope = "ANDROID";
    }
}
