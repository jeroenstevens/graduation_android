package com.example.jeroenstevens.graduation_android.object;

public class Item {
    private String name;
    private String imageUrl;
    private int collectionId;
    private int id;
//
//    public static final String COL_ID = "_id";
//    public static final String COL_USER_ID = "user_id";
//    public static final String COL_NAME = "name";
//    public static final String COL_IMAGE_PATH = "image_path";
//    public static final String COL_EXPIRES_AT = "updated_at";
//    public static final String COL_CREATED_AT = "created_at";
//
//    @Column(name = COL_ID)
//    public int id;
//    @Column(name = COL_USER_ID)
//    public int userId;
//    @Column(name = COL_NAME, index = true)
//    public String name;
//    @Column(name = COL_IMAGE_PATH)
//    public String imagePath;
//    @Column(name = COL_CREATED_AT)
//    public String createdAt;
//    @Column(name = COL_EXPIRES_AT)
//    public String expiresAt;

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
