package com.example.jeroenstevens.graduation_android.rest.requestBody;

import com.example.jeroenstevens.graduation_android.object.User;

public class UserRegisterRequestBody {
    User user;

    public UserRegisterRequestBody(String email, String password) {
       this.user = new User(email, password);
    }
}
