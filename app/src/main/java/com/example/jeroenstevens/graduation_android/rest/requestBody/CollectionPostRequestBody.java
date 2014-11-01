package com.example.jeroenstevens.graduation_android.rest.requestBody;

import com.example.jeroenstevens.graduation_android.object.Collection;

public class CollectionPostRequestBody {
    Collection collection;

    public CollectionPostRequestBody(String name, int userId) {
        this.collection = new Collection();
        this.collection.name = name;
        this.collection.userId = userId;
    }

    public CollectionPostRequestBody(Collection collection) {
        this.collection = collection;
    }
}
