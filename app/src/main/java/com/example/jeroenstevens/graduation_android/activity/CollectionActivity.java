package com.example.jeroenstevens.graduation_android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.view.InfiniteClearableEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends Activity {
    public static final String TAG = "CollectionActivity";

    public static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 1;
    private Uri outputFileUri;
    private TextView defaultButton;
    private TextView uploadButton;
    private ImageView preview;

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
                showAddCollectionDialog();
                break;
            default:
                break;
        }
        return true;
    }

    private void showAddCollectionDialog() {
        Dialog dialog = new Dialog(CollectionActivity.this);
        dialog.setTitle("Add collection");
        dialog.setContentView(R.layout.dialog_add_collection);

        InfiniteClearableEditText collectionName = (InfiniteClearableEditText) dialog.findViewById(R.id.name);
        preview = (ImageView) dialog.findViewById(R.id.preview);
        defaultButton = (TextView) dialog.findViewById(R.id.default_button);
        uploadButton = (TextView) dialog.findViewById(R.id.upload_button);
        TextView okButton = (TextView) dialog.findViewById(R.id.ok_button);
        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel_button);

        View.OnClickListener toggleSelection = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag().equals("upload")) {
                    openImageIntent();
                } else {
                    toggleSelection(view);
                    preview.setBackgroundResource(R.drawable.default_icon);
                }
            }
        };

        defaultButton.setOnClickListener(toggleSelection);
        uploadButton.setOnClickListener(toggleSelection);
        defaultButton.setTag("default");
        uploadButton.setTag("upload");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Database stuff
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "What kind of scavenger are you?!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
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
        final PackageManager packageManager = getPackageManager();
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

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if(data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
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
                    selectedImageUri = data.getData();
                    imagePath = getPath(selectedImageUri);
                }
                Log.d(TAG, "selectedImageUri : " + selectedImageUri);

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Bitmap adjustedBitmap = handleBitmap(bitmap, imagePath);

                preview.setBackground(new BitmapDrawable(getResources(), adjustedBitmap));
                toggleSelection(uploadButton);
            }
        }
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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private String getPath(Uri uri) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}

