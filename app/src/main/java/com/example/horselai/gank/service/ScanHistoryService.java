package com.example.horselai.gank.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.mvp.model.db.DbManager;
import com.example.horselai.gank.mvp.model.db.Tables;

/**
 * Created by horseLai on 2017/7/28.
 */

public class ScanHistoryService extends IntentService
{

    private DbManager mDbManager;
    public static final String ADD_ONE = "addHistory";
    public static final String ACTION_HISTORY = "action.ic_history_teal";
    public static final String ACTION_SAVE = "action.save";
    public static final String ACTION_SAVE_DONE = "action.save.work.done";
    public static final String ACTION_SAVED_ALREADY = "action.save.work.done。already";
    private static final String TAG = "ScanHistoryService";

    public ScanHistoryService(String name)
    {
        super(name);
    }

    public ScanHistoryService()
    {
        this("ScanHistoryService");
    }

    @Override protected void onHandleIntent(@Nullable Intent intent)
    {
        if (intent == null) return;

        final GankNews news = (GankNews) intent.getSerializableExtra(ADD_ONE);
        long id = -1;
        switch (intent.getAction()) {
            case ACTION_HISTORY: {
                if (contains(Tables.TABLE_HISTORY, news)) {
                    break;
                }
                id = mDbManager.insertSingle(Tables.TABLE_HISTORY, news);
                break;
            }
            case ACTION_SAVE: {
                if (!contains(Tables.TABLE_SAVE, news)) {
                    id = mDbManager.insertSingle(Tables.TABLE_SAVE, news);
                }

                if (id != -1) {
                    LocalBroadcastManager.getInstance(App.context()).sendBroadcast(new Intent(ACTION_SAVE_DONE));
                } else {
                    LocalBroadcastManager.getInstance(App.context()).sendBroadcast(new Intent(ACTION_SAVED_ALREADY));
                }
                break;
            }
            default:
                break;
        }

        if (App.DEBUG) Log.i(TAG, "onHandleIntent: inserted id: " + id);

    }

    private boolean contains(String table, GankNews news)
    {
        final String sql = "select * from " + table + " where url is ? ;";
        // final GankNews gankNews = mDbManager.findSingle(sql, new String[]{news.url});
        //Log.i(TAG, "contains: " + gankNews);
        return mDbManager.findSingle(sql, new String[]{news.url}) != null;
    }


    @Override public void onCreate()
    {
        super.onCreate();
        mDbManager = DbManager.newManager(App.context());
    }


    @Override public void onDestroy()
    {
        super.onDestroy();
        mDbManager.release();
    }
}
