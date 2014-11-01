package com.example.jeroenstevens.graduation_android.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.adapter.CollectionsAdapter;
import com.example.jeroenstevens.graduation_android.fragment.AddCollectionDialogFragment;
import com.example.jeroenstevens.graduation_android.object.Collection;
import com.example.jeroenstevens.graduation_android.syncadapter.SyncService;

public class CollectionActivity extends Activity {
    public static final String TAG = "CollectionActivity";

    public static final int SELECT_PICTURE_FOR_COLLECTION_DIALOG = 1;

    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;

    public static final long SECONDS_PER_HOUR = 3600L;
    public static final long SYNC_INTERVAL_IN_HOURS = 6L;

    public static final long SYNC_SMALL_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
    public static final long SYNC_LARGE_INTERVAL = SECONDS_PER_HOUR * SYNC_INTERVAL_IN_HOURS;

    private ListView mListView;
    private ContentObserver mContentObserver;
    private BroadcastReceiver mDbBroadcastReceiver;
    private IntentFilter mDbIntentFilter;

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

        setBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDbBroadcastReceiver != null) {
            registerReceiver(mDbBroadcastReceiver, mDbIntentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDbBroadcastReceiver != null) {
            unregisterReceiver(mDbBroadcastReceiver);
        }
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
                mListView.setAdapter(new CollectionsAdapter(CollectionActivity.this, Collection.all()));
            }
        });
    }

    private void setBroadcastReceiver() {

        mDbBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG, action);

                if (action.equals(SyncService.DATABASE_UPDATED)) {
                    refresh();
                }
            }
        };

        mDbIntentFilter = new IntentFilter(SyncService.DATABASE_UPDATED);
        registerReceiver(mDbBroadcastReceiver, mDbIntentFilter);
    }
}

