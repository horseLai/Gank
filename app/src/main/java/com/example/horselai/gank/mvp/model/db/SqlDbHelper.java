package com.example.horselai.gank.mvp.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by laixiaolong on 2017/5/4.
 */

public class SqlDbHelper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "gankIo.db";


    private static final String TAG = "SqlDbHelper";

    public SqlDbHelper(Context context)
    {
        super(context, DB_NAME, null, 1);
    }


    @Override public void onCreate(SQLiteDatabase db)
    {
        //_id INTEGER PRIMARY KEY AUTO_INCREMENT,
        // 浏览记录列表
        String sql = "CREATE table " + Tables.TABLE_HISTORY + "( _id integer primary key autoincrement ,desc TEXT, url TEXT UNIQUE NOT NULL, source TEXT, type TEXT, who TEXT," + "image TEXT, publishedAt text)";
        db.execSQL(sql);

        //收藏列表
        sql = "CREATE table " + Tables.TABLE_SAVE + "( _id integer primary key autoincrement ,desc TEXT, url TEXT UNIQUE NOT NULL, source TEXT, type TEXT, who TEXT," + "image TEXT, publishedAt text)";
        db.execSQL(sql);
        Log.i(TAG, "onCreate: " + sql);

    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i(TAG, "onUpgrade: newVersion >> " + newVersion);
    }

    @Override public void onConfigure(SQLiteDatabase db)
    {
        super.onConfigure(db);
        Log.i(TAG, "onConfigure: ");
    }

    @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        super.onDowngrade(db, oldVersion, newVersion);
        Log.i(TAG, "onDowngrade: ");
    }

    @Override public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        Log.i(TAG, "onOpen: ");
    }
}
