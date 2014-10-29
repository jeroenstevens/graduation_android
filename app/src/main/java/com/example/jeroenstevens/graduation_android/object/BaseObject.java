package com.example.jeroenstevens.graduation_android.object;

import android.database.Cursor;

public class BaseObject {

    private Cursor mCursor;

    public BaseObject(Cursor cursor) {
        mCursor = cursor;
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
