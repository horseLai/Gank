package com.example.horselai.gank.http.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.example.horselai.gank.util.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by horseLai on 2017/7/14.
 * <p>
 * 该类是个管理型工具类，用来处理Bitmap图片，包含图片压缩，切图，变换等一系列操作
 */

public final class BitmapManager
{

    private static final String TAG = "BitmapManager";
    public static final boolean DEBUG = ImageLoader.DEBUG;

    public static final int X_MODE_REVERSE_LTR = 0x2;
    public static final int X_MODE_REVERSE_RTL = 0x3;
    public static final int Y_MODE_REVERSE_TTB = 0x4;
    public static final int Y_MODE_REVERSE_BTT = 0x5;

    private BitmapManager()
    {
    }

    static class Build
    {
        private static final BitmapManager MANAGER = new BitmapManager();
    }

    public static BitmapManager getInstance()
    {
        return Build.MANAGER;
    }


    public int calculateSampleSize(BitmapFactory.Options options, int targetW, int targetH)
    {
        final int srcW = options.outWidth;
        final int srcH = options.outHeight;

        int sampleSize = 1;

        if (srcW > targetW || srcH > targetH) {
            //
            final int target = Math.max(targetH, targetW);
            final int heightRatio = Math.round((float) srcH / (float) target);
            final int widthRatio = Math.round((float) srcW / (float) target);

            sampleSize = Math.max(heightRatio, widthRatio);
        }
        Log.i(TAG, "calculateSampleSize: final sampleSize >>: " + sampleSize);
        return sampleSize;
    }


    public Bitmap decodeBitmap(Bitmap bitmap, int targetW, int targetH)
    {
        if (bitmap == null) throw new NullPointerException("Bitmap is null !");

        ByteArrayInputStream bis = null;
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bis = new ByteArrayInputStream(bos.toByteArray(), 0, bos.size());
            return decodeBitmap(bis, targetW, targetH);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.close(bis);
        }
        return null;
    }


    public Bitmap decodeBitmap(String absPath, int targetW, int targetH) throws IOException
    {
        return decodeBitmap(BitmapFactory.decodeFile(absPath), targetW, targetH);
    }


    public Bitmap decodeBitmap(InputStream is, int targetW, int targetH) throws IOException
    {
        if (is == null) throw new NullPointerException("InputStream is null !");
        final BitmapFactory.Options options = new BitmapFactory.Options();

        // pretend to decode
        options.inJustDecodeBounds = true;

        //使用ALPHA_8效果要好于RGB_565,具体体现在内存和图片清晰度上
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;

        int read;
        final byte[] buf = new byte[1024 * 2];
        byte[] bytes;
        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new ByteArrayOutputStream();
            while ((read = bis.read(buf)) != -1) {
                bos.write(buf, 0, read);
            }

            bos.flush();
            bytes = bos.toByteArray();
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            // decode
            options.inSampleSize = calculateSampleSize(options, targetW, targetH);
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        } finally {
            Utils.close(bis, is, bos);
        }
    }

    /**
     * @param is
     * @param maxSizeInKb 控制的目标图片大小，单位kb
     * @return
     */
    public Bitmap compressBitmap(InputStream is, int maxSizeInKb)
    {
        try {
            return compressBitmap(BitmapFactory.decodeStream(is), maxSizeInKb);
        } finally {
            Utils.close(is);
        }
    }

    /**
     * @param bitmap
     * @param maxSizeInKb 控制的目标图片大小，单位kb
     * @return
     */
    public Bitmap compressBitmap(Bitmap bitmap, int maxSizeInKb)
    {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            if (DEBUG) Log.i(TAG, "compressBitmap: before size >>: " + bos.size() / 1024 + "kb");

            while (bos.size() / 1024 > maxSizeInKb && quality > 6) {
                bos.reset();
                quality -= 6;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            }
            final byte[] bytes = bos.toByteArray();
            if (DEBUG) {
                Log.i(TAG, "compressBitmap: final quality >>: " + quality);
                Log.i(TAG, "compressBitmap: final size >>: " + bytes.length / 1024 + "kb");
            }

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } finally {
            Utils.close(bos);
        }
    }


    /**
     * 根据传入的图片创建一张为指定宽高的图片
     *
     * @param bitmap
     * @param targetW
     * @param targetH
     * @param filter
     * @return
     */
    public Bitmap createNewBitmap(Bitmap bitmap, int targetW, int targetH, boolean filter)
    {

        //matrix 的短时大量创建会导致大范围内存波动，
        //造成卡顿， 最好建立缓冲
        final Matrix matrix = new Matrix();
        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();
        matrix.setScale(targetW / (float) width, targetH / (float) height);

        return Bitmap.createBitmap(bitmap, 0, 0, targetW, targetH, matrix, filter);
    }

    /**
     * 根据传入的图片创建一张为指定宽高的图片
     *
     * @param is
     * @param targetW
     * @param targetH
     * @param filter
     * @return
     */
    public Bitmap createNewBitmap(InputStream is, int targetW, int targetH, boolean filter)
    {
        try {
            return createNewBitmap(BitmapFactory.decodeStream(is), targetW, targetH, filter);
        } finally {
            Utils.close(is);
        }
    }

    public void recycleBitmap(Bitmap bitmap)
    {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }


    /**
     * 释放 ImageView关联的相关图片资源
     *
     * @param view
     */
    public void releaseImage(ImageView view)
    {
        final Drawable background = view.getBackground();
        Bitmap bitmap = null;
        if (background != null && background instanceof BitmapDrawable) {
            final BitmapDrawable bitmapDrawable = (BitmapDrawable) background;
            bitmap = bitmapDrawable.getBitmap();
            recycleBitmap(bitmap);
        }
        bitmap = view.getDrawingCache();
        if (bitmap != null) recycleBitmap(bitmap);
        view.destroyDrawingCache();
    }


    public Bitmap centerBaseRotate(Bitmap bitmap, float angle)
    {
        final Matrix matrix = new Matrix();
        matrix.setRotate(angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public Bitmap translate(Bitmap bitmap, float dx, float dy)
    {
        final Matrix matrix = new Matrix();
        matrix.postTranslate(dx, dy);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    /**
     * 轴逆转，形成镜面反射效果
     *
     * @param bitmap
     * @param mode
     * @return
     */
    public Bitmap axisReverse(Bitmap bitmap, int mode)
    {
        final Matrix matrix = new Matrix();
        switch (mode) {
            case X_MODE_REVERSE_LTR:
                matrix.setScale(1, 1);
                break;
            case X_MODE_REVERSE_RTL:
                matrix.setScale(-1, 1);
                break;
            case Y_MODE_REVERSE_TTB:
                matrix.setScale(1, 1);
                break;
            case Y_MODE_REVERSE_BTT:
                matrix.setScale(1, -1);
                break;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }


    /**
     * @param bitmap
     * @param percent 缩放百分率（0 ~ 1的数）
     * @return
     */
    public Bitmap scaleXY(Bitmap bitmap, float percent)
    {
        if (percent <= 0) percent = 0.1F;

        final Matrix matrix = new Matrix();
        matrix.setScale(percent, percent);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }


}
