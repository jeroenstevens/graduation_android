package com.example.jeroenstevens.graduation_android.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.activity.CollectionActivity;
import com.example.jeroenstevens.graduation_android.object.Collection;
import com.example.jeroenstevens.graduation_android.syncadapter.SyncService;
import com.example.jeroenstevens.graduation_android.utils.ImageHelper;
import com.example.jeroenstevens.graduation_android.view.InfiniteClearableEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddCollectionDialogFragment extends DialogFragment {

    private static final String TAG = "AddCollectionDialogFragment";
    private static final String IMAGE_PATH = "imagePath";

    private Uri outputFileUri;
    private TextView mDefaultButton;
    private TextView mUploadButton;
    private ImageView mPreview;
    private String mImagePath;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Add collection");
        dialog.setContentView(R.layout.dialog_add_collection);

        // Find views
        final InfiniteClearableEditText collectionName = (InfiniteClearableEditText) dialog.findViewById(R.id.name);
        mPreview = (ImageView) dialog.findViewById(R.id.preview);
        mDefaultButton = (TextView) dialog.findViewById(R.id.default_button);
        mUploadButton = (TextView) dialog.findViewById(R.id.upload_button);
        TextView okButton = (TextView) dialog.findViewById(R.id.ok_button);
        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel_button);

        // Set tags for the selection toggling
        mDefaultButton.setTag("default");
        mUploadButton.setTag("upload");

        // Click listener for opening the image chooser
        View.OnClickListener openImageIntent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageIntent();
            }
        };
        mUploadButton.setOnClickListener(openImageIntent);
        mPreview.setOnClickListener(openImageIntent);

        // Click listener to toggle selection and set the mPreview back to the default
        mDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelection(view);
                mPreview.setBackgroundResource(R.drawable.default_icon);
                mImagePath = null;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Insert into database
                Collection collection = new Collection();
                collection.name = collectionName.getText().toString();

                if (mImagePath != null) {
                    collection.imagePath = mImagePath;
                }
                collection.save();
                databaseUpdated();

                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getBaseContext(), "What kind of scavenger are you?!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Restore image from mImagePath and toggle on rotation
        Log.d(TAG, "mImagePath onCreate : " + mImagePath);
        if (savedInstanceState != null) {
            String imagePath = savedInstanceState.getString(IMAGE_PATH);
            if (imagePath != null) {
                Log.d(TAG, "imagePath : " + imagePath);
                mImagePath = imagePath;

                Bitmap bitmap = new ImageHelper().getBitmapFromImagePath(mImagePath);
                mPreview.setBackground(new BitmapDrawable(getResources(), bitmap));
                toggleSelection(mUploadButton);
            }
        }

        return dialog;
    }

    private void toggleSelection(View view) {
        view.setBackgroundColor(Color.parseColor("#7ED321"));
        if (view.getTag().equals("upload")) {
            mDefaultButton.setBackgroundColor(Color.TRANSPARENT);
        } else {
            mUploadButton.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator);
        root.mkdirs();
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        getActivity().startActivityForResult(chooserIntent, CollectionActivity.SELECT_PICTURE_FOR_COLLECTION_DIALOG);
    }

    public void setPreview(Intent intent) {
        final boolean isCamera;
        if(intent == null) {
            isCamera = true;
        } else {
            final String action = intent.getAction();
            if(action == null) {
                isCamera = false;
            } else {
                isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }

        Uri selectedImageUri;
        String imagePath;
        if(isCamera) {
            selectedImageUri = outputFileUri;
            imagePath = selectedImageUri.getPath();
        } else {
            selectedImageUri = intent.getData();
            imagePath = getPath(selectedImageUri);
        }

        Log.d(TAG, "selectedImageUri : " + selectedImageUri);
        Log.d(TAG, "imagePath : " + imagePath);

        mImagePath = imagePath;
        Bitmap bitmap = new ImageHelper().getBitmapFromImagePath(mImagePath);

        mPreview.setBackground(new BitmapDrawable(getResources(), bitmap));
        toggleSelection(mUploadButton);
    }

    private String getPath(Uri uri) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getActivity(), uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String path = cursor.getString(column_index);
        cursor.close();

        return path;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "mImagePath onSaveInstanceState : " + mImagePath);
        savedInstanceState.putString(IMAGE_PATH, mImagePath);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void databaseUpdated() {
        getActivity().sendBroadcast(new Intent(SyncService.DATABASE_UPDATED));
    }
}
