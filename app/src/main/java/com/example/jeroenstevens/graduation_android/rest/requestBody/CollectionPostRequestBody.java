package com.example.jeroenstevens.graduation_android.rest.requestBody;

import com.example.jeroenstevens.graduation_android.object.Collection;
import com.google.gson.annotations.Expose;

import java.util.UUID;

public class CollectionPostRequestBody {

//    @Expose
//    private final int userId;
    @Expose
    private final Collection collection;

    public CollectionPostRequestBody(String name, UUID userId) {
        Collection collection = new Collection();
        collection.name = name;
        collection.userId = userId;

        this.collection = collection;
    }

    public CollectionPostRequestBody(Collection collection) {
        this.collection = collection;
    }
}
