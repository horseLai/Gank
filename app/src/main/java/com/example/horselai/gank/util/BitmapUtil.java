package com.example.horselai.gank.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.opengl.GLES10;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by laixiaolong on 2017/1/24.
 * <p>
 * 该类包含位图压缩处理、转码等通用方法
 * <br/>
 * 图片最好在子线程中进行处理
 */

public class BitmapUtil
{

    private static final String TAG = "BitmapUtil";

    private static final int DEFAULT_MAX_TEXTURE_DIMENTION = 2048;

    private BitmapUtil()
    {
    }

    /**
     * 解码位图成字节数组
     *
     * @param src 要解码的位图
     * @return 解码成功返回相应的字节数组， 失败返回空字节数组
     * @throws IOException
     */
    public static byte[] decodeBitmap(@NonNull Bitmap src) throws IOException
    {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();

        if (src.compress(Bitmap.CompressFormat.PNG, 100, bout)) {

            return bout.toByteArray();
        }
        return new byte[]{};
    }

    /**
     * 获取能显示bitmap的最大尺寸
     *
     * @return
     */
    public static int getMaxTextureDimention()
    {
        final int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        return Math.max(maxTextureSize[0], DEFAULT_MAX_TEXTURE_DIMENTION);
    }

    /**
     * 将Bitmap转成Drawable
     *
     * @param src
     * @return
     * @throws IOException
     */
    public static Drawable decodeBitmapToDrawable(@NonNull Bitmap src) throws IOException
    {
        final ByteArrayInputStream bin = new ByteArrayInputStream(decodeBitmap(src));
        final Drawable image = Drawable.createFromStream(bin, "image");
        bin.close();
        return image;
    }


    public static BitmapFactory.Options calculateSampleSize(Resources res, @IdRes int resId, int targetWidth, int targetHeight) throws IOException
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //decode一遍
        BitmapFactory.decodeResource(res, resId, options);

        return calculateSample(options, targetWidth, targetHeight);
    }

    /**
     * 计算压缩率
     *
     * @param targetWidth  处理后希望得到的宽度
     * @param targetHeight 处理后希望得到的高度
     * @return 返回已经设置好的BitmapFactory.Options
     * @throws IOException
     */
    public static BitmapFactory.Options calculateSampleSize(BufferedInputStream bis, int targetWidth, int targetHeight) throws IOException
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //decode一遍
        BitmapFactory.decodeStream(bis, null, options);

        return calculateSample(options, targetWidth, targetHeight);
    }

    @NonNull
    public static BitmapFactory.Options calculateSample(BitmapFactory.Options options, int targetWidth, int targetHeight)
    {
        final int srcHeight = options.outHeight;
        final int srcWidth = options.outWidth;

        Log.i(TAG, "calculateSampleSize: srcHeight = " + srcHeight + "  srcWidth = " + srcWidth);
        Log.i(TAG, "calculateSampleSize: targetHeight = " + targetHeight + "  targetWidth = " + targetWidth);

        int inSampleSize = 1;
        if (srcHeight > targetHeight || srcWidth > targetWidth) {

            final int halfW = srcWidth / 2;
            final int halfH = srcHeight / 2;

            // 只要宽和高中有一个的一般大于指定高度，就重新计算
            while ((halfW / inSampleSize) >= targetWidth && (halfH / inSampleSize) >= targetHeight) {
                inSampleSize *= 2;
            }
        }

        //计算最大压缩比率，并取最大的那个压缩比（缺点：当宽高差距很大时，如果取直接取最大的那个值，会把长度小的那个给压缩没了）
        /*if (srcHeight > targetHeight || srcHeight > targetHeight)
        {
            //计算出实际宽度和目标宽高比率，因为inSampleSize > 1，所以用srcHeight / targetHeight
            final int heightRatio = Math.round((float) srcHeight / (float) targetHeight);
            final int widthRatio = Math.round((float) srcWidth / (float) targetWidth);

            //选择宽高中最小的比率作为inSampleSize值，这样可以
            // 保证最终图片的宽高一定都会大于或等于目标图片的宽高
            inSampleSize = heightRatio < widthRatio? heightRatio : widthRatio;
        }*/

        Log.i(TAG, "calculateSampleSize: inSampleSize = " + inSampleSize);

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return options;
    }


    /**
     * @param resId        drawable片图片id
     * @param targetWidth
     * @param targetHeight
     * @return
     * @throws IOException
     */
    public static Bitmap scaleDrawableResource(Resources res, int resId, int targetWidth, int targetHeight) throws IOException
    {
        return BitmapFactory.decodeResource(res, resId, calculateSampleSize(res, resId, targetWidth, targetHeight));
    }

    /**
     * @param src          要处理的图片
     * @param inSampleSize 缩放比率；<br/>
     *                     例如：inSampleSize == 4 返回原图像宽/高的 1/4, 以及原图像 1/16 的像素数.
     *                     <br/>注意：任意 inSampleSize <= 1 的值都会被看成是1
     * @return 返回压缩后的图片
     */
    public static Bitmap scaleWithSample(@NonNull Bitmap src, int inSampleSize) throws IOException
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        final byte[] bytes = decodeBitmap(src);
        if (bytes.length <= 0) return null;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    /**
     * @param src
     * @param quality   输出图片的质量，注意该值对PNG图片无效
     * @param limitSize 图片大小限定值，单位：kb
     * @return
     */
    public static Bitmap compressWithLimitSize(@NonNull Bitmap src, int quality, float limitSize)
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        boolean ok = src.compress(Bitmap.CompressFormat.PNG, quality, bout);
        Log.i(TAG, "compressWithLimitSize:  ok = " + ok);

        if (!ok) return src;

        final byte[] bytes = bout.toByteArray();
        final float kb = (float) bytes.length / 1024.0f;
        if (kb > limitSize) {

            Log.i(TAG, "compressWithLimitSize: kb = " + kb + "  limitsize = " + limitSize);
            final float ratio = limitSize / kb;
            Log.i(TAG, "compressWithLimitSize: ratio = " + ratio);

            Matrix matrix = new Matrix();
            matrix.postScale(ratio, ratio);

            return scaleWithMatrix(src, matrix);
        }
        return src;
    }


    /**
     * 根据宽高压缩位图
     *
     * @param src
     * @param targetWidth  处理后希望得到的宽度 > 0.0f
     * @param targetHeight 处理后希望得到的高度 > 0.0f
     * @return
     */
    public static Bitmap scaleWithWh(@NonNull Bitmap src, float targetWidth, float targetHeight)
    {
        if (targetHeight != 0.0f && targetWidth != 0.0f) {

            final Matrix matrix = new Matrix();
            matrix.postScale(targetWidth / (float) src.getWidth(), targetHeight / (float) src.getHeight());

            return scaleWithMatrix(src, matrix);
        }

        return src;
    }


    /**
     * @param src
     * @param matrix
     * @return
     */
    public static Bitmap scaleWithMatrix(@NonNull Bitmap src, @NonNull Matrix matrix)
    {
        if (matrix.isIdentity()) {
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        return src;
    }


    /**
     * 旋转图片
     *
     * @param src
     * @param angle 旋转的角度
     * @return
     */
    public static Bitmap postRotate(@NonNull Bitmap src, float angle)
    {
        final Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return scaleWithMatrix(src, matrix);
    }


    /**
     * 歪斜图片
     *
     * @param src
     * @param kx  范围：0.0f ~ 1.0f
     * @param ky  范围：0.0f ~ 1.0f
     * @return
     */
    public static Bitmap postSkew(@NonNull Bitmap src, float kx, float ky)
    {
        final Matrix matrix = new Matrix();
        matrix.postSkew(kx, ky);
        return scaleWithMatrix(src, matrix);
    }

    /**
     * 调动图片
     *
     * @param src
     * @param dx
     * @param dy
     * @return
     */
    public static Bitmap postTranslate(@NonNull Bitmap src, float dx, float dy)
    {
        final Matrix matrix = new Matrix();
        matrix.postTranslate(dx, dy);
        return scaleWithMatrix(src, matrix);
    }


    /**
     * @param needRecycle
     */
    public static void recycle(Bitmap needRecycle)
    {
        if (needRecycle != null && !needRecycle.isRecycled()) {
            needRecycle.recycle();
        }
    }

}
