package com.example.jeroenstevens.graduation_android.object;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "collections")
public class Collection extends Model {

    public static final String COL_ID = "_id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_NAME = "name";
    public static final String COL_IMAGE_PATH = "image_path";
    public static final String COL_UPDATED_AT = "updated_at";
    public static final String COL_CREATED_AT = "created_at";

    @Expose
    @Column(name = COL_ID)
    public UUID id;

    @Expose
    @Column(name = COL_USER_ID)
    public UUID userId;

    @Expose
    @Column(name = COL_NAME, index = true)
    public String name;

    @Expose
    @Column(name = COL_IMAGE_PATH)
    public String imagePath;

    @Expose
    @Column(name = COL_CREATED_AT)
    public String createdAt;

    @Expose
    @Column(name = COL_UPDATED_AT)
    public String updatedAt;

    private List<Item> items;

    public Collection() {
        super();
        id = UUID.randomUUID();
        createdAt = new Date(System.currentTimeMillis()).toString();
        updatedAt = new Date(System.currentTimeMillis()).toString();
    }

    public static List<Collection> all() {
        return new Select().from(Collection.class).execute();
    }

    public static List<Collection> getInRange(String columnName, String startDatetime, String endDatetime) {
        return new Select().from(Collection.class).where(
//                columnName + " >= Datetime('?') AND " + columnName + " <= Datetime('?')", startDatetime, endDatetime
                columnName + " < DATEADD(" + columnName + ", " + -10 + ", SYSDATETIME());"
        ).execute();
    }

    public static List<Collection> whereDateAgo(String columnName, String dateAgo) {
        return new Select().from(Collection.class).where(columnName + " > ?", dateAgo).execute();
    }

    public static Collection get(UUID id) {
        return new Select().from(Collection.class).where("id == ?", id).executeSingle();
    }

    public void updateFromRemote(Collection remoteObject) {
        this.updatedAt = remoteObject.updatedAt;
        this.name = remoteObject.name;
        this.save();
    }
}
