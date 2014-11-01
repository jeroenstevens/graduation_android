package com.example.jeroenstevens.graduation_android.object;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

@Table(name = "collections")
public class Collection extends Model implements Serializable {

    public static final String COL_ID = "_id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_NAME = "name";
    public static final String COL_IMAGE_PATH = "image_path";
    public static final String COL_UPDATED_AT = "updated_at";
    public static final String COL_CREATED_AT = "created_at";

    @Column(name = COL_ID)
    public int id;
    @Column(name = COL_USER_ID)
    public int userId;
    @Column(name = COL_NAME, index = true)
    public String name;
    @Column(name = COL_IMAGE_PATH)
    public String imagePath;
    @Column(name = COL_CREATED_AT)
    public String createdAt;
    @Column(name = COL_UPDATED_AT)
    public String updatedAt;

    private List<Item> items;

    public String getName() {
        return name;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public static List<Collection> all() {
        return new Select().from(Collection.class).execute();
    }

    public static List<Collection> getInRange(String columnName, String startDatetime, String endDatetime) {
        return new Select().from(Collection.class).where(
                columnName + " >= Datetime('?') AND " + columnName + " <= Datetime('?')", startDatetime, endDatetime
        ).execute();
    }
}
