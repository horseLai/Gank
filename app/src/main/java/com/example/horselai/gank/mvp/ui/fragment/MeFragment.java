package com.example.horselai.gank.mvp.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.base.BaseFragment;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.mvp.presenter.MePresenter;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.FileManager;

import java.io.File;

/**
 * Created by horseLai on 2017/7/24.
 */

public class MeFragment extends BaseFragment implements View.OnClickListener, ISuperView<String>
{

    private static final String TAG = "MeFragment";
    private TextView tvCacheSize;
    private AbsSuperPresenter<String> mPresenter;
    private BottomSheetBehavior<View> mSheetBehavior;
    private BottomSheetDialog mSheetDialog;

    @Override protected int provideLayoutId()
    {
        return R.layout.fragment_me_content;
    }

    @Override protected void initView(View rootView, Bundle savedInstanceState)
    {
        mPresenter = new MePresenter(this, mContext);


        rootView.findViewById(R.id.layout_clean).setOnClickListener(this);
        rootView.findViewById(R.id.tv_official_web).setOnClickListener(this);
        rootView.findViewById(R.id.tv_developer).setOnClickListener(this);
        rootView.findViewById(R.id.tv_licence).setOnClickListener(this);
        rootView.findViewById(R.id.tv_version).setOnClickListener(this);
        tvCacheSize = (TextView) rootView.findViewById(R.id.tv_cache_size);


        initBottomSheet(rootView);

    }

    private void initBottomSheet(View rootView)
    {

        mSheetDialog = new BottomSheetDialog(mContext);
        mSheetDialog.setCancelable(true);
        // setContentView(...)可以时layoutId， 也可以时View，这个跟Dialog一样
        //mSheetDialog.setContentView(R.layout.me_licence);

    }


    @Override public void onResume()
    {
        super.onResume();
        updateCacheSize();
    }

    public void updateCacheSize()
    {
        final FileManager fileManager = FileManager.getInstance();
        final File diskCacheDir = ImageLoader.getImageLoader().getDiskCacheDir(mContext);
        long fileSize = fileManager.calculateFileSize(diskCacheDir);
        fileSize += fileManager.calculateFileSize(App.getAppCacheDir());
        fileSize += fileManager.calculateFileSize(App.getHttpCacheDir());
        tvCacheSize.setText(fileManager.formatSize(fileSize));
    }


    @Override public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.layout_clean:
                checkIfClean();
                break;
            case R.id.tv_official_web:
                GankUI.startExternalBrowser(mContext, getResources().getString(R.string.official_web));
                break;

            case R.id.tv_developer:
                showDeveloper();
                break;
            case R.id.tv_licence:
                showLicence();
                break;
            case R.id.tv_version:
                showVersion();
                break;
        }
    }

    private void showVersion()
    {
        mSheetDialog.setContentView(R.layout.me_version);
        if (mSheetDialog.isShowing()) mSheetDialog.dismiss();
        else mSheetDialog.show();
    }

    private void showDeveloper()
    {
        mSheetDialog.setContentView(R.layout.me_developer);
        if (mSheetDialog.isShowing()) mSheetDialog.dismiss();
        else mSheetDialog.show();
    }

    private void showLicence()
    {
        mSheetDialog.setContentView(R.layout.me_licence);
        if (mSheetDialog.isShowing()) mSheetDialog.dismiss();
        else mSheetDialog.show();
    }


    private void checkIfClean()
    {
        new AlertDialog.Builder(mContext).setMessage(getResources().getString(R.string.alert_warming)).setPositiveButton("清理", new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                mPresenter.update();
                dialog.dismiss();
            }
        }).setNegativeButton("暂不", new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        }).setIcon(R.drawable.logo_work).setTitle("来自干货的提醒").create().show();

    }


    @Override public void onLoadOk(String data)
    {
        updateCacheSize();
        App.toastShort("已清理" + data + "内存ˋ( ° ▽、° ) ");
    }

    @Override public void onLoadFailed(Exception e)
    {
        App.toastShort(e.getMessage());
    }
}
