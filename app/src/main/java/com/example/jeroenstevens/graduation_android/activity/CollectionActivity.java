package com.example.jeroenstevens.graduation_android.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.adapter.CollectionsAdapter;
import com.example.jeroenstevens.graduation_android.db.DbContentProvider;
import com.example.jeroenstevens.graduation_android.db.DbHelper;
import com.example.jeroenstevens.graduation_android.db.DbInterface;
import com.example.jeroenstevens.graduation_android.fragment.AddCollectionDialogFragment;

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
                super.onChange(selfChange);
                refresh();
            }
        };

        Account account = AccountManager.get(this).getAccountsByType("com.example.jeroenstevens.graduation_android")[0];

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // Performing a sync no matter if it's off
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // Performing a sync no matter if it's off
        ContentResolver.requestSync(account, DbContentProvider.AUTHORITY, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(
                DbContentProvider.getContentUri(DbHelper.COLLECTION_TABLE_NAME), true, mContentObserver);
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
                mListView.setAdapter(new CollectionsAdapter(CollectionActivity.this, DbInterface.getCollections()));
            }
        });
    }
}

