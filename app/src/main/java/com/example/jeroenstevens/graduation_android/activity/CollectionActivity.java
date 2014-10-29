package com.example.jeroenstevens.graduation_android.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.adapter.CollectionsAdapter;
import com.example.jeroenstevens.graduation_android.db.DbContentProvider;
import com.example.jeroenstevens.graduation_android.db.DbHelper;
import com.example.jeroenstevens.graduation_android.fragment.AddCollectionDialogFragment;
import com.example.jeroenstevens.graduation_android.object.BaseObject;
import com.example.jeroenstevens.graduation_android.object.Collection;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends Activity {
    public static final String TAG = "CollectionActivity";

    public static final int SELECT_PICTURE_FOR_COLLECTION_DIALOG = 1;
    private ListView mListView;
    private ContentObserver mContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collections);

        mListView = (ListView) findViewById(R.id.list_view);
        refresh();

        mContentObserver = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                Log.d(TAG, "contentObserver : onChange ");
                super.onChange(selfChange);
                refresh();
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(DbContentProvider.getContentUri("collection"), true, mContentObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mContentObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_collection:
                new AddCollectionDialogFragment().show(getFragmentManager(), "AddCollectionDialogFragment");
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == SELECT_PICTURE_FOR_COLLECTION_DIALOG) {
                ((AddCollectionDialogFragment) getFragmentManager()
                        .findFragmentByTag("AddCollectionDialogFragment")).setPreview(data);
            }
        }
    }

    private void refresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListView.setAdapter(new CollectionsAdapter(CollectionActivity.this, getCollections()));
            }
        });
    }

    private List<Collection> getCollections() {
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

