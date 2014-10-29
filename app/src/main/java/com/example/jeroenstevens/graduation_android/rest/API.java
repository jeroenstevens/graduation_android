package com.example.jeroenstevens.graduation_android.rest;

import com.example.jeroenstevens.graduation_android.object.ApiKey;
import com.example.jeroenstevens.graduation_android.object.Collection;
import com.example.jeroenstevens.graduation_android.object.Item;
import com.example.jeroenstevens.graduation_android.rest.requestBody.CollectionPostRequestBody;
import com.example.jeroenstevens.graduation_android.rest.requestBody.ItemPostRequestBody;
import com.example.jeroenstevens.graduation_android.rest.requestBody.UserAuthenticateRequestBody;
import com.example.jeroenstevens.graduation_android.rest.requestBody.UserRegisterRequestBody;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface API {
    @GET("/users/{user_id}/collections")
    void getCollections(@Path("user_id") String userId, Callback<List<Collection>> callback);

    @POST("/users/{user_id}/collections")
    void postCollection(@Path("user_id") String userId, @Body CollectionPostRequestBody payload, Callback<Collection> callback);

    @POST("/users")
    void registerUser(@Body UserRegisterRequestBody payload, Callback<ApiKey> callback); // payload new User(email, password)

    @POST("/session")
    void authenticateUser(@Body UserAuthenticateRequestBody payload, Callback<ApiKey> callback); // payload new User(email, password)

    @GET("/collections/{collection_id}/items")
    void getItems(@Path("collection_id") int collectionId, Callback<List<Item>> callback);

    @POST("/collections/{collection_id}/items")
    void postItem(@Path("collection_id") int collectionId, @Body ItemPostRequestBody payload, Callback<Item> callback);

    @DELETE("/collections/{collection_id}/items/{item_id}")
    void deleteItem(@Path("collection_id") int collectionId, @Path("item_id") int itemId, Callback<Item> callback);
}
