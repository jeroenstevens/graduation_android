package com.example.jeroenstevens.graduation_android.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
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
import com.example.jeroenstevens.graduation_android.db.DbContentProvider;
import com.example.jeroenstevens.graduation_android.db.DbHelper;
import com.example.jeroenstevens.graduation_android.view.InfiniteClearableEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCollectionDialogFragment extends DialogFragment {

    private static final String TAG = "AddCollectionDialogFragment";
    private static final String PREVIEW = "previewBitmap";

    private Uri outputFileUri;
    private TextView defaultButton;
    private TextView uploadButton;
    private ImageView preview;
    private Bitmap mPreviewBitmap;

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
        preview = (ImageView) dialog.findViewById(R.id.preview);
        defaultButton = (TextView) dialog.findViewById(R.id.default_button);
        uploadButton = (TextView) dialog.findViewById(R.id.upload_button);
        TextView okButton = (TextView) dialog.findViewById(R.id.ok_button);
        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel_button);

        // Set tags for the selection toggling
        defaultButton.setTag("default");
        uploadButton.setTag("upload");

        // Click listener for opening the image chooser
        View.OnClickListener openImageIntent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageIntent();
            }
        };
        uploadButton.setOnClickListener(openImageIntent);
        preview.setOnClickListener(openImageIntent);

        // Click listener to toggle selection and set the preview back to the default
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelection(view);
                preview.setBackgroundResource(R.drawable.default_icon);
                mPreviewBitmap = null;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Insert into database
                ContentValues values = new ContentValues();
                values.put(DbHelper.COLLECTION_COL_NAME, collectionName.getText().toString());

                if (mPreviewBitmap != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    mPreviewBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    byte[] byteArray = bos.toByteArray();
                    values.put(DbHelper.COLLECTION_COL_IMAGE, byteArray);
                }

                getActivity().getContentResolver().insert(
                        DbContentProvider.getContentUri("collection"), values);

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

        // Restore preview image and toggle on rotation
        Log.d(TAG, "mPreviewBitmap onCreate : " + mPreviewBitmap);
        if (savedInstanceState != null) {
            Bitmap previewBitmap = savedInstanceState.getParcelable(PREVIEW);
            if (previewBitmap != null) {
                Log.d(TAG, "previewBitmap : " + previewBitmap);
                mPreviewBitmap = previewBitmap;
                preview.setBackground(new BitmapDrawable(getResources(), previewBitmap));
                toggleSelection(uploadButton);
            }
        }

        return dialog;
    }

    private void toggleSelection(View view) {
        view.setBackgroundColor(Color.parseColor("#7ED321"));
        if (view.getTag().equals("upload")) {
            defaultButton.setBackgroundColor(Color.TRANSPARENT);
        } else {
            uploadButton.setBackgroundColor(Color.TRANSPARENT);
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

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        Bitmap adjustedBitmap = handleBitmap(bitmap, imagePath);

        mPreviewBitmap = adjustedBitmap;
        preview.setBackground(new BitmapDrawable(getResources(), adjustedBitmap));
        toggleSelection(uploadButton);
    }

    private Bitmap handleBitmap(Bitmap bitmap, String imagePath) {
        ExifInterface exif = null;
        Bitmap adjustedBitmap = null;
        try {
            exif = new ExifInterface(imagePath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);

            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

            if (bitmap.getHeight() >= bitmap.getWidth()){

                adjustedBitmap = Bitmap.createBitmap(bitmap,
                        0,
                        bitmap.getHeight()/2 - bitmap.getWidth()/2,
                        bitmap.getWidth(),
                        bitmap.getWidth()/2,
                        matrix, true);
            } else {
                matrix.postScale(0.25f, 0.25f);
                adjustedBitmap = Bitmap.createBitmap(bitmap,
                        0,
                        0,
                        bitmap.getWidth(),
                        bitmap.getHeight(),
                        matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (adjustedBitmap != null) {
            return adjustedBitmap;
        } else {
            return bitmap;
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private String getPath(Uri uri) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getActivity(), uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "mPreviewBitmap onSaveInstanceState : " + mPreviewBitmap);
        savedInstanceState.putParcelable(PREVIEW, mPreviewBitmap);
        super.onSaveInstanceState(savedInstanceState);
    }
}
