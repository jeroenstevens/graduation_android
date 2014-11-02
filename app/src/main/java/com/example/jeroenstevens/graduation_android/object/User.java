package com.example.jeroenstevens.graduation_android.object;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Table(name = "users")
public class User extends Model {

    public static final String COL_ID = "_id";
    public static final String COL_USER_NAME = "user_name";
    public static final String COL_EMAIL = "email";
    public static final String COL_UPDATED_AT = "updated_at";
    public static final String COL_CREATED_AT = "created_at";

    @Expose
    @Column(name = COL_ID)
    public UUID id;

    @Expose
    @Column(name = COL_EMAIL)
    public String email;

    @Expose
    @Column(name = COL_USER_NAME)
    public String userName;

    @Expose
    @Column(name = COL_CREATED_AT)
    public String createdAt;

    @Expose
    @Column(name = COL_UPDATED_AT)
    public String updatedAt;

    @Expose
    public String password;

    public User() {
        super();
        id = UUID.randomUUID();
        createdAt = new Date(System.currentTimeMillis()).toString();
        updatedAt = new Date(System.currentTimeMillis()).toString();
    }

    public List<ApiKey> apiKeys() {
        return new Select().from(ApiKey.class).where("user_id == ?", this.id).execute();
    }

    public static User getCurrentUser(String authToken) {
        ApiKey apiKey = new Select().from(ApiKey.class).where(ApiKey.COL_ACCESS_TOKEN + " = ?", authToken).executeSingle();
        User user = new Select().from(User.class).where(User.COL_ID + " = ?", apiKey.userId).executeSingle();

        // If Also create user when authenticating, fetch from server.

        return user;
    }
}

