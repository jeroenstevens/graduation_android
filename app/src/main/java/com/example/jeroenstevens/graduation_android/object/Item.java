package com.example.jeroenstevens.graduation_android.object;

public class Item {
    private String name;
    private String imageUrl;
    private int collectionId;
    private int id;

    public Item(String name, int collectionId) {
        this.name = name;
        this.collectionId = collectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public int getId() {
        return id;
    }
}
