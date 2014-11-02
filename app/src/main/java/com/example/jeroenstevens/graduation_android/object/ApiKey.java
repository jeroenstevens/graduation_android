package com.example.jeroenstevens.graduation_android.object;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.UUID;

@Table(name = "apikeys")
public class ApiKey extends Model {

    public static final String COL_ID = "_id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_ACCESS_TOKEN = "access_token";
    public static final String COL_EXPIRES_AT = "updated_at";
    public static final String COL_CREATED_AT = "created_at";
    private static final String COL_SCOPE = "scope";
    private static final String COL_LAST_ACCESS = "last_access";
    private static final String COL_IS_LOCKED = "is_locked";

    @Expose
    @Column(name = COL_ID)
    public UUID id;

    @Expose
    @Column(name = COL_USER_ID)
    public UUID userId;

    @Expose
    @Column(name = COL_ACCESS_TOKEN)
    public String accessToken;

    @Expose
    @Column(name = COL_SCOPE)
    public String scope;

    @Expose
    @Column(name = COL_CREATED_AT)
    public String createdAt;

    @Expose
    @Column(name = COL_EXPIRES_AT)
    public String expiresAt;

    @Expose
    @Column(name = COL_LAST_ACCESS)
    public String lastAccess;

    @Expose
    @Column(name = COL_IS_LOCKED)
    public String isLocked;

    public User user() {
        return new Select().from(User.class).where("id == ?", this.userId).executeSingle();
    }
}

