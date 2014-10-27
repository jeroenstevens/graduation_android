package com.example.jeroenstevens.graduation_android.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class RestClient {

    private static API REST_CLIENT;
    private static String ROOT = "http://api.shinav.com";

    static {
        setupRestClient();
    }

    private RestClient() {}

    public static API get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ROOT)
                .setConverter(new GsonConverter(gson))
                .build();

        REST_CLIENT = restAdapter.create(API.class);

    }
}
