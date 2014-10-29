package com.example.jeroenstevens.graduation_android.rest.requestBody;

import com.example.jeroenstevens.graduation_android.object.Item;

public class ItemPostRequestBody {

    public Item item;

    public ItemPostRequestBody(String inputText, int collectionId) {
        this.item = new Item(inputText, collectionId);
    }
}
