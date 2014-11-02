package com.example.jeroenstevens.graduation_android.rest.requestBody;

import com.example.jeroenstevens.graduation_android.object.User;
import com.google.gson.annotations.Expose;

public class UserRegisterRequestBody {

    @Expose
    User user;


    public UserRegisterRequestBody(String email, String password, String userName) {

        User user = new User();
        user.email = email;
        user.password = password;
        user.userName = userName;

        this.user = user;
    }
}
