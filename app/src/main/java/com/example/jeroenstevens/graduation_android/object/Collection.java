package com.example.jeroenstevens.graduation_android.object;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.jeroenstevens.graduation_android.db.DbHelper;

import java.io.Serializable;
import java.util.List;

public class Collection extends BaseObject implements Serializable {

    private String name;
    private String imageUrl;
    private Bitmap image;
    private List<Item> items;
    private int userId;
    private int id;

    public Bitmap getImage() {
        return image;
    }

    public Collection(Cursor cursor) {
        super(cursor);

        name = cursor.getString(cursor.getColumnIndex(DbHelper.COLLECTION_COL_NAME));
        byte[] byteArrayImage = cursor.getBlob(cursor.getColumnIndex(DbHelper.COLLECTION_COL_IMAGE));
        if (byteArrayImage != null) {
            image = BitmapFactory.decodeByteArray(byteArrayImage, 0, byteArrayImage.length);
        }
    }

    public Collection(String name, int userId) {
        super(null);
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Item> getItems() { return items; }

    public int getUserId() {
        return userId;
    }

    public int getId() { return id; }
}
