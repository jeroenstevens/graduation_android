package com.example.jeroenstevens.graduation_android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "graduation_android.db";
    private static final int DATABASE_VERSION = 1;

    // DB Table consts
    public static final String COLLECTION_TABLE_NAME = "collection";
    public static final String COLLECTION_COL_ID = "_id";
    public static final String COLLECTION_COL_NAME = "name";
    public static final String COLLECTION_COL_IMAGE = "image";

    // Database creation sql statement
    public static final String CREATE_TABLE_COLLECTION = "create table "
            + COLLECTION_TABLE_NAME + "(" +
            COLLECTION_COL_ID + " integer primary key autoincrement, " +
            COLLECTION_COL_NAME + " text not null, " +
            COLLECTION_COL_IMAGE + " BLOB" +
            ");";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_COLLECTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + COLLECTION_TABLE_NAME);
        onCreate(db);
    }

}
