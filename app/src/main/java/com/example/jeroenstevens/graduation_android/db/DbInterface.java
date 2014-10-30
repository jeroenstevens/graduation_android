package com.example.jeroenstevens.graduation_android.db;

import android.database.Cursor;

import com.example.jeroenstevens.graduation_android.object.BaseObject;
import com.example.jeroenstevens.graduation_android.object.Collection;

import java.util.ArrayList;
import java.util.List;

public class DbInterface {

    public static List<Collection> getCollections() {
        List<BaseObject> baseObjects = DbContentProvider.get(DbHelper.COLLECTION_TABLE_NAME);
        List<Collection> collections = new ArrayList<Collection>();

        // Change BaseObjects to Collections;
        Cursor cursor = null;
        for(int i = 0; i < baseObjects.size(); i++) {
            BaseObject object = baseObjects.get(i);

            cursor = object.getCursor();
            cursor.moveToPosition(i);

            Collection collection = new Collection(cursor);
            collections.add(collection);
        }

        if (cursor != null) {
            cursor.close();
        }

        return collections;
    }
}
