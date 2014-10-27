package com.example.jeroenstevens.graduation_android.rest;

import com.example.jeroenstevens.graduation_android.object.ApiKey;
import com.example.jeroenstevens.graduation_android.object.Collection;
import com.example.jeroenstevens.graduation_android.rest.requestBody.UserAuthenticateRequestBody;
import com.example.jeroenstevens.graduation_android.rest.requestBody.UserRegisterRequestBody;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface API {
    @GET("/users/{user_id}/collections")
    void getCollections(@Path("user_id") String userId, Callback<List<Collection>> callback);

    @POST("/users")
    void registerUser(@Body UserRegisterRequestBody payload, Callback<ApiKey> callback); // payload new User(email, password)

    @POST("/session")
    void authenticateUser(@Body UserAuthenticateRequestBody payload, Callback<ApiKey> callback); // payload new User(email, password)
}
