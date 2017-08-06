package com.example.horselai.gank.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.horselai.gank.R;


/**
 * Created by laixiaolong on 2017/4/1.
 * <p>
 * 用于处理透明式statusBar
 */

public class StatusBarCompat
{

    private static final int DEFAULT_COLOR = Color.parseColor("#20000000");
    private static final int TRANSPARENT = Color.TRANSPARENT;


    public static int getStatusBarHeight(Context context)
    {
        final int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        return resId < 0 ? 0 : context.getResources().getDimensionPixelSize(resId);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void compatStatusBar(Activity activity, int statusBarColor)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (statusBarColor == -1) statusBarColor = DEFAULT_COLOR;
            activity.getWindow().setStatusBarColor(statusBarColor);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (statusBarColor == -1) statusBarColor = DEFAULT_COLOR;

            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));

            View statusBackView = new View(activity);
            statusBackView.setBackgroundColor(statusBarColor);
            contentView.addView(statusBackView, params);


        }
    }


    public static void compatStatusBarDefault(Activity activity)
    {
        compatStatusBar(activity, -1);
    }

    public static void transparentStatusBar(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    public static void compatToolbar(Toolbar toolbar, Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setPadding(0, (int) activity.getResources().getDimension(R.dimen.status_bar_height), 0, 0);
        }
    }

}
