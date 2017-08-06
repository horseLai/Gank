package com.example.horselai.gank.mvp.presenter;

import android.content.Context;

import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.mvp.model.db.DbManager;
import com.example.horselai.gank.mvp.model.db.Tables;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/7/28.
 */

public class SaveHistoryPresenter extends AbsSuperPresenter<GankNews>
{

    private final DbManager mDbManager;

    public static final int HISTORY = 0;
    public static final int SAVE = 1;
    public static final int DELETE_HISTORY = 2;
    public static final int DELETE_SAVE = 3;


    public SaveHistoryPresenter(ISuperView mView, Context context)
    {
        super(mView);
        mDbManager = DbManager.newManager(context);

    }


    @Override public void update(Object... params)
    {
        if (!checkIsOnline()) return;

        switch ((int) params[0]) {

            case HISTORY:
                doUpdateComm(Tables.TABLE_HISTORY);
                break;
            case SAVE:
                doUpdateComm(Tables.TABLE_SAVE);
                break;
            case DELETE_HISTORY:
                doDeleteComm(Tables.TABLE_HISTORY, (GankNews) params[1]);
                break;
            case DELETE_SAVE:
                doDeleteComm(Tables.TABLE_SAVE, (GankNews) params[1]);
                break;

        }
    }

    private void doDeleteComm(final String table, final GankNews news)
    {
        AsyncService.getService().addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                final String sql = " url is ?";
                final int delete = mDbManager.delete(table, sql, new String[]{news.url});
                if (delete <= 0) {
                    getHandler().obtainMessage(FAIL, new Exception("删除失败(°ー°〃)")).sendToTarget();
                } else {
                    getHandler().obtainMessage(SUCCESS, null).sendToTarget();
                }
            }
        });
    }


    private void doUpdateComm(final String table)
    {
        AsyncService.getService().addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                //asc 升序排序  desc 为降序
                final String sql = "select * from " + table + " order by _id  desc ;";
                final ArrayList<GankNews> newses = mDbManager.find(sql, null);
                if (Utils.isEmpty(newses)) {
                    getHandler().obtainMessage(FAIL, new Exception("没有任何记录(°ー°〃)")).sendToTarget();
                } else {
                    getHandler().obtainMessage(SUCCESS, newses).sendToTarget();
                }
            }
        });
    }

    @Override public void release()
    {
        mDbManager.release();
    }
}
