package com.example.jeroenstevens.graduation_android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.fragment.AddCollectionDialogFragment;

public class CollectionActivity extends Activity {
    public static final String TAG = "CollectionActivity";

    public static final int SELECT_PICTURE_FOR_COLLECTION_DIALOG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collection);
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
}

