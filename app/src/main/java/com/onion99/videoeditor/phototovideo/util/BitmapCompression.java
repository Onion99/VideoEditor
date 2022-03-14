package com.onion99.videoeditor.phototovideo.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitmapCompression {
    public BitmapCompression() {
        //add
    }

    public static Bitmap decodeSampledBitmapFromLocal(Context context, String str, int i, int i2) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        options.inSampleSize = calculateInSampleSize(options, i, i2);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(str, options);
    }

    public static int calculateInSampleSize(Options options, int i, int i2) {
        int i3 = options.outHeight;
        int i4 = options.outWidth;
        int i5 = 1;
        if (i3 > i2 || i4 > i) {
            int i6 = i3 / 2;
            int i7 = i4 / 2;
            while (i6 / i5 > i2 && i7 / i5 > i) {
                i5 *= 2;
            }
        }
        return i5;
    }

    public static Bitmap decodeFile(File file, int i, int i2) {
        try {
            Options options = new Options();
            int i3 = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            while ((options.outWidth / i3) / 2 >= i2 && (options.outHeight / i3) / 2 >= i) {
                i3 *= 2;
            }
            Options options2 = new Options();
            options2.inSampleSize = i3;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public static Bitmap adjustImageOrientation(File file, Bitmap bitmap) {
        int i = 0;
        try {
            int attributeInt = new ExifInterface(file.getAbsolutePath()).getAttributeInt("Orientation", 1);
            if (attributeInt == 3) {
                i = 180;
            } else if (attributeInt == 6) {
                i = 90;
            } else if (attributeInt == 8) {
                i = 270;
            }
            if (i != 0) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.preRotate((float) i);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
            }
            return bitmap.copy(Config.ARGB_8888, true);
        } catch (IOException unused) {
            return null;
        }
    }

    public static void adjustImageOrientationUri(Context context, Uri uri) {
        Matrix matrix = new Matrix();
        Cursor query = context.getContentResolver().query(uri, new String[]{"orientation"}, null, null, null);
        if (query.getCount() == 1) {
            query.moveToFirst();
            matrix.preRotate((float) query.getInt(0));
        }
        Utils.bitmap = Bitmap.createBitmap(Utils.bitmap, 0, 0, Utils.bitmap.getWidth(), Utils.bitmap.getHeight(), matrix, false);
        Utils.bitmap.copy(Config.ARGB_8888, true);
    }

    public static Bitmap scaleCenterCrop(Bitmap bitmap, int i, int i2) {
        float f = (float) i2;
        float width = (float) bitmap.getWidth();
        float f2 = (float) i;
        float height = (float) bitmap.getHeight();
        float max = Math.max(f / width, f2 / height);
        float f3 = width * max;
        float f4 = max * height;
        float f5 = (f - f3) / 2.0f;
        float f6 = (f2 - f4) / 2.0f;
        RectF rectF = new RectF(f5, f6, f3 + f5, f4 + f6);
        Bitmap createBitmap = Bitmap.createBitmap(i2, i, bitmap.getConfig());
        new Canvas(createBitmap).drawBitmap(bitmap, null, rectF, null);
        return createBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight()), new RectF(0.0f, 0.0f, (float) Utils.width, (float) Utils.width), ScaleToFit.CENTER);
        float[] fArr = new float[9];
        matrix.getValues(fArr);
        return Bitmap.createScaledBitmap(bitmap, (int) (((float) bitmap.getWidth()) * fArr[0]), (int) (((float) bitmap.getHeight()) * fArr[4]), true);
    }

    public static Bitmap getResizedBitmap(Bitmap bitmap, int i, int i2) {
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight()), new RectF(0.0f, 0.0f, (float) i, (float) i2), ScaleToFit.CENTER);
        float[] fArr = new float[9];
        matrix.getValues(fArr);
        return Bitmap.createScaledBitmap(bitmap, (int) (((float) bitmap.getWidth()) * fArr[0]), (int) (((float) bitmap.getHeight()) * fArr[4]), true);
    }

    public static Bitmap decodeSampledBitmapFromAssets(Context context, String str, int i, int i2) throws IOException {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getAssets().open(str), new Rect(), options);
        options.inSampleSize = calculateInSampleSize(options, i, i2);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(context.getAssets().open(str), new Rect(), options);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor query = context.getContentResolver().query(uri, null, null, null, null);
        if (query == null) {
            return uri.getPath();
        }
        query.moveToFirst();
        String string = query.getString(query.getColumnIndex("_data"));
        query.close();
        return string;
    }
}
