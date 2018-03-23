package com.example.horselai.gank.base;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.util.StatusBarCompat;

/**
 * 包含页面交互的基本方法
 */
public abstract class BaseActivity extends AppCompatActivity
{

    @Override protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());

        StatusBarCompat.transparentStatusBar(this);
    }


    public abstract int provideContentViewId();

    @Override protected void onPause()
    {
        //清空请求队列
        ImageLoader.getImageLoader().getThreadPoolHandler().clearTaskQueue();
        AsyncService.getService().getPoolHandler().clearTaskQueue();
        super.onPause();
    }

    /**
     * 获取屏幕像素宽高
     *
     * @return wh[0]: widthPixels,  wh[1]: heightPixels
     */
    public int[] getScreenWh()
    {
        int[] wh = new int[2];
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        wh[0] = displayMetrics.widthPixels;
        wh[1] = displayMetrics.heightPixels;
        return wh;
    }

    @Override protected void onDestroy()
    {
        super.onDestroy();
        ImageLoader.getImageLoader().getThreadPoolHandler().cancelAndClearTaskQueue();
        AsyncService.getService().getPoolHandler().cancelAndClearTaskQueue();
    }

    /**
     * 检测权限、请求权限（适配Android 6.0 动态权限管理）,
     * 注意调用此方法后需要实现 {onPermissionHasGranted(..), onExplainPermission(..), onRequestPermissionsResult(..)}三个方法
     * ，具体作用看注释
     *
     * @param checkPermission 需要检查的权限
     * @param reqPermissions  需要申请的权限
     * @param reqCode         请求码
     */
    protected void checkAndRequestPermission(String checkPermission, String[] reqPermissions, int reqCode)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int granted = checkSelfPermission(checkPermission);
            if (granted != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(checkPermission)) {
                    //
                    App.toastShort("解释权限");
                    onExplainPermission(checkPermission);
                } else {
                    requestPermissions(reqPermissions, reqCode);
                }
            } else {
                onPermissionHasGranted(checkPermission);
            }
        } else {
            onPermissionHasGranted(checkPermission);
        }

    }

    /**
     * 所请求的权限已经挂载，处于可用状态时被调用
     *
     * @param checkPermission 所请求的权限
     */
    protected void onPermissionHasGranted(String checkPermission) {}

    /**
     * 需要解释权限时被调用
     *
     * @param checkPermission 所请求的权限
     */
    protected void onExplainPermission(String checkPermission) {}




    /**
     * 主动申请 gc
     */
    protected void gc()
    {
        Runtime.getRuntime().gc();
    }
}
