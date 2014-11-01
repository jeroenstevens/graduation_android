package com.example.jeroenstevens.graduation_android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class ImageHelper {

    public Bitmap getBitmapFromImagePath(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        return handleBitmap(bitmap, imagePath);
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

}
