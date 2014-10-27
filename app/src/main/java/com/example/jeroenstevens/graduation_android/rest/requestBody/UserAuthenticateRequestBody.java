package com.example.jeroenstevens.graduation_android.rest.requestBody;

public class UserAuthenticateRequestBody {
    private final String email;
    private final String password;

    public UserAuthenticateRequestBody(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
