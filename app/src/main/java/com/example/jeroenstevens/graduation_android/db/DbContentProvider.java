package com.example.jeroenstevens.graduation_android.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.jeroenstevens.graduation_android.object.BaseObject;

import java.util.ArrayList;
import java.util.List;

public class DbContentProvider extends ContentProvider {

    private static final String TAG = "DbContentProvider";
    private DbHelper dbHelper;
    public static final String AUTHORITY = "ourContentProviderAuthorities";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private static Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        dbHelper = new DbHelper(mContext);
        return true;
    }

    @Override
    public String getType(Uri arg0) {
        return null;
    }

    public static Uri getContentUri(String tableName) {
        return Uri.withAppendedPath(CONTENT_URI, tableName);
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        String table = getTableName(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long value = database.insert(table, null, initialValues);
        notifyObservers(table);

        return Uri.withAppendedPath(CONTENT_URI, String.valueOf(value));
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table = getTableName(uri);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(table, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String whereClause,
                      String[] whereArgs) {
        String table = getTableName(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        notifyObservers(table);

        return database.update(table, values, whereClause, whereArgs);
    }

    @Override
    public int delete(Uri uri, String where, String[] args) {
        String table = getTableName(uri);
        SQLiteDatabase dataBase=dbHelper.getWritableDatabase();
        notifyObservers(table);

        return dataBase.delete(table, where, args);
    }

    public static String getTableName(Uri uri){
        String value = uri.getPath();
        value = value.replace("/", "");
        return value;
    }

    private void notifyObservers(String tableName) {
        mContext.getContentResolver().notifyChange(getContentUri(tableName), null);
    }

    public static List<BaseObject> get(String tableName) {
        Cursor cursor = mContext.getContentResolver().query(getContentUri(tableName), null, null, null, null);

        ArrayList<BaseObject> objects = new ArrayList<BaseObject>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                objects.add(new BaseObject(cursor));
            }
        }

        return objects;
    }
}

