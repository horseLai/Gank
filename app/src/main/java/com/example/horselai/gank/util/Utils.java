package com.example.horselai.gank.util;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.google.gson.JsonArray;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Created by laixiaolong on 2017/3/23.
 */

public class Utils
{

    private static Snackbar mSnackbar;
    private static final int SHOW_LENGTH = 5000; //5S
    private static AlphaAnimation mAlphaAnimation;


    public static void close(Closeable... closeables)
    {
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) closeable.close();
            }
        } catch (IOException e) {
            //do nothing
        }

    }


    public static String encodeStringToUTF8(String toEncode)
    {
        return Charset.forName("UTF-8").encode(toEncode).toString();
    }


    public static boolean needCreateNewTask(AsyncTask task)
    {
        return task == null || task.getStatus() == AsyncTask.Status.FINISHED || task.isCancelled();
    }


    public static AlertDialog buildProgressDialog(Context context, ViewGroup parent)
    {
        final View view = LayoutInflater.from(context).inflate(R.layout.progresssbar_layout, parent, false);

        return new AlertDialog.Builder(context).setView(view).setCancelable(true).create();
    }


    public static void showDefaultSnackBar(View view, String msg)
    {
        if (mSnackbar == null || !mSnackbar.isShownOrQueued()) {
            mSnackbar = Snackbar.make(view, msg, SHOW_LENGTH);
            mSnackbar.getView().setBackgroundColor(view.getContext().getResources().getColor(R.color.hotPink));
            mSnackbar.setActionTextColor(view.getContext().getResources().getColor(R.color.skyBlue));
        }
        mSnackbar.setText(msg);
        mSnackbar.setDuration(SHOW_LENGTH);
        mSnackbar.setAction("我知道了", new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                mSnackbar.dismiss();
            }
        });
        mSnackbar.show();
    }

    public static void showSnackBar(View view, String msg, String actionText, View.OnClickListener listener)
    {
        if (mSnackbar == null || !mSnackbar.isShownOrQueued()) {
            mSnackbar = Snackbar.make(view, msg, SHOW_LENGTH);
            mSnackbar.getView().setBackgroundColor(view.getContext().getResources().getColor(R.color.hotPink));
            mSnackbar.setActionTextColor(view.getContext().getResources().getColor(R.color.skyBlue));
        }
        mSnackbar.setText(msg);
        mSnackbar.setDuration(SHOW_LENGTH);
        if (listener == null) {
            mSnackbar.setAction("我知道了", new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    mSnackbar.dismiss();
                }
            });
        } else {
            mSnackbar.setAction(actionText, listener);
        }
        mSnackbar.show();
    }

    public static void startDefaultAlphaAnimate(final View view)
    {
        if (mAlphaAnimation == null) mAlphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        mAlphaAnimation.reset();
        mAlphaAnimation.setDuration(500);
        mAlphaAnimation.setRepeatCount(0);
        view.setAnimation(mAlphaAnimation);
        mAlphaAnimation.startNow();
        view.postDelayed(new Runnable()
        {
            @Override public void run()
            {
                view.clearAnimation();
            }
        }, 505);

    }


    public static boolean isRefresherShow(SwipeRefreshLayout srRefresher)
    {
        return srRefresher.isShown() || srRefresher.isLaidOut() || srRefresher.isActivated();
    }


    public static String urlEncodeUTF8(String toEncode)
    {
        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return toEncode;
    }


    public static String urlDecodeUTF8(String toDecode)
    {
        try {
            return URLDecoder.decode(toDecode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return toDecode;
    }


    public static boolean isEmpty(Collection list)
    {
        return list == null || list.isEmpty();
    }


    public static String getString(@StringRes int strRes)
    {
        return App.context().getResources().getString(strRes);
    }

    public static int getColor(@ColorRes int colorRes)
    {
        return App.context().getResources().getColor(colorRes);
    }


    public static float getDimen(@DimenRes int dimenRes)
    {
        return App.context().getResources().getDimension(dimenRes);
    }


    public static boolean isJsonArrayEmpty(JsonArray array)
    {
        return array.isJsonNull() || array.size() == 0;
    }

    public static void alertDialog(Context context, String msg)
    {
        new AlertDialog.Builder(context).setMessage(msg).setPositiveButton("ok", new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        }).create().show();
    }

    public static void alertDialog(Context context, String title, String msg, @DrawableRes int icon)
    {
        new AlertDialog.Builder(context).setTitle(title).setIcon(icon).setMessage(msg).setPositiveButton("ok", new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        }).create().show();
    }


    public static PopupMenu popupMenu(@NonNull View anchorView, @MenuRes int menuRes, boolean showIcon, PopupMenu.OnMenuItemClickListener listener)
    {
        final PopupMenu popupMenu = new PopupMenu(anchorView.getContext(), anchorView);
        popupMenu.inflate(menuRes);
        popupMenu.setOnMenuItemClickListener(listener);
        popupMenu.setGravity(Gravity.END);
        if (showIcon) {
            //使用反射，强制显示菜单图标
            try {
                Field field = popupMenu.getClass().getDeclaredField("mPopup");
                field.setAccessible(true);
                MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
                mHelper.setForceShowIcon(true);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        popupMenu.show();
        return popupMenu;
    }

    public static ProgressDialog buildProgressDialog(Context context)
    {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMax(100);
        return dialog;
    }


    public static void startActivity(Context context, Bundle extra, Class<? extends AppCompatActivity> clazz)
    {
        Intent starter = new Intent(context, clazz);
        starter.putExtras(extra);
        context.startActivity(starter);
    }

    public static void startActivity(Context context, Class<? extends AppCompatActivity> clazz)
    {
        Intent starter = new Intent(context, clazz);
        context.startActivity(starter);
    }


    public static void shareTextPlain(Context context, String textMsg)
    {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, textMsg);
        intent.setType("text/plain");

        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(intent);
        else Toast.makeText(context, "没有找到能分享的应用！O.O", Toast.LENGTH_SHORT).show();

    }

    public static boolean isEmpty(ArrayMap arrayMap)
    {
        return arrayMap == null || arrayMap.isEmpty();
    }


    /**
     * 往剪切板中写入一条数据
     *
     * @param context
     * @param content
     * @param listener
     */
    public static void copyTextIntoClipboard(Context context, String content, ClipboardManager.OnPrimaryClipChangedListener listener)
    {
        final ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        manager.addPrimaryClipChangedListener(listener);
        manager.setPrimaryClip(ClipData.newPlainText("text", content));
    }

    /**
     * 从截切板中提取第一条数据
     *
     * @param context
     * @return
     */
    public static ClipData.Item extractPrimaryFromClipboard(Context context)
    {
        final ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (!manager.hasPrimaryClip()) return null;
        return manager.getPrimaryClip().getItemAt(0);
    }
}
