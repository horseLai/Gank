package com.example.horselai.gank.mvp.ui.activity;

import android.app.WallpaperManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.AppbarActivity;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.http.loader.BitmapManager;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.mvp.presenter.ImagePresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.FileManager;
import com.example.horselai.gank.util.Utils;

import java.io.File;

public class ImageActivity extends AppbarActivity implements View.OnClickListener, ISuperView<String>
{

    private GankBeauty mBeauty;
    private ImageView mImageView;
    private ImagePresenter mPresenter;
    public static final String KEY_BEAUTY = "ic_beauty_teal";
    private Snackbar mSnackbar;
    private WallpaperManager mWpManager;

    @Override protected boolean homeAsUpEnable()
    {
        return true;
    }

    @Override protected View.OnClickListener onToolbarClick()
    {
        return null;
    }

    @Override public int provideContentViewId()
    {
        return R.layout.activity_image;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mWpManager = WallpaperManager.getInstance(this);

        getToolbar().setBackgroundColor(getResources().getColor(R.color.grayBlue));
        getToolbar().setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        //presenter
        mPresenter = new ImagePresenter(this);

        // extras
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mBeauty = (GankBeauty) extras.getSerializable(KEY_BEAUTY);
        }
        initWidgets();


    }

    private void initWidgets()
    {
        mImageView = (ImageView) findViewById(R.id.iv_other_image);
        mImageView.setOnClickListener(this);

        final TextView barTitle = getBarTitleView();
        if (mBeauty != null && !TextUtils.isEmpty(mBeauty.who))
            barTitle.setText("本图片由 '" + mBeauty.who + "' 上传");
        barTitle.setTextColor(Color.WHITE);

        mSnackbar = Snackbar.make(mImageView, "下载成功！", Snackbar.LENGTH_LONG);
        mSnackbar.setAction("我知道了", new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                mSnackbar.dismiss();
            }
        });
        mSnackbar.getView().setBackgroundColor(getResources().getColor(R.color.grayBlue));
    }

    // TODO: 2017/7/22 添加图片缩放、编辑、可设置成壁纸等 功能、


    boolean mFirstDisplay = true;

    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (!mFirstDisplay) return;

        mFirstDisplay = false;
        if (mBeauty == null) return;

        final String url = mBeauty.url + "?imageView2/0/w/" + mImageView.getWidth();
        ImageLoader.getImageLoader().displayImageAsync(mImageView, url, false);
    }


    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.image_setting, menu);
        return true;
    }


    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_download: {
                if (mBeauty != null) {
                    mPresenter.update(mBeauty.url);
                    showSnackBar("马上就下载 (●'◡'●)");
                    break;
                }
                showSnackBar("这是张假图片o_o ...");
                break;
            }
            case R.id.menu_menu: {
                showMenu(getToolbar());
                break;
            }

            case R.id.menu_share: {
                final String picPath = mPresenter.getPicPath(mBeauty.url);
                final FileManager fileManager = FileManager.getInstance();
                if (!fileManager.exists(picPath)) {
                    showSnackBar("图片不存在，请先下载！");
                    break;
                }
                Utils.shareImage(ImageActivity.this, Uri.fromFile(new File(picPath)), "来自‘干货集中营’");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void showMenu(View anchorView)
    {
        Utils.popupMenu(anchorView, R.menu.image_sub_menu, false, new PopupMenu.OnMenuItemClickListener()
        {
            @Override public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId()) {
                    case R.id.menu_wallpaper: {
                        final String picPath = mPresenter.getPicPath(mBeauty.url);
                        final FileManager fileManager = FileManager.getInstance();
                        final boolean exists = fileManager.exists(picPath);
                        if (exists) {
                            mPresenter.settingWallpaper(mWpManager, mBeauty.url);
                            mIsSettingWallpaper = false;
                            break;
                        }
                        mPresenter.update(mBeauty.url);
                        mIsSettingWallpaper = true;
                        break;
                    }

                }
                return true;
            }
        });
    }

    boolean mIsSettingWallpaper = false;


    private void showSnackBar(String msg)
    {
        mSnackbar.setText(msg);
        mSnackbar.show();
    }

    @Override public void onClick(View v)
    {

    }


    @Override public void onLoadOk(String data)
    {
        if (mIsSettingWallpaper) {
            mPresenter.settingWallpaper(mWpManager, mBeauty.url);
            mIsSettingWallpaper = false;
            return;
        }

        showSnackBar(data + "(●'◡'●)");
        /*
        //插入MediaStore.Images， 但是它存在于相册
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), mPresenter.getPicPath(mBeauty.url), "", "contributed by " + mBeauty.who);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } */
    }

    @Override public void onLoadFailed(Exception e)
    {
        showSnackBar(e.getMessage());
    }

    @Override protected void onDestroy()
    {
        super.onDestroy();
        BitmapManager.getInstance().releaseImage(mImageView);
        gc();
    }


}
