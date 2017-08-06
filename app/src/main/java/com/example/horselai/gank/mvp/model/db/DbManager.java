package com.example.horselai.gank.mvp.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

/**
 * Created by laixiaolong on 2017/5/4.
 */

public class DbManager
{
    private static final String TAG = "DbManager";
    private SqlDbHelper mDbHelper;
    private SQLiteDatabase mDb;

    private DbManager(Context context)
    {
        mDbHelper = new SqlDbHelper(context);
    }

    public static DbManager newManager(Context context)
    {
        if (context == null) throw new NullPointerException("context must not be null ..");
        return new DbManager(context);
    }

    /**
     * @param value
     * @return 插入成功返回id， 插入失败返回-1
     */
    public long insertSingle(String table, GankNews value)
    {
        conformWritableDb();

        mDb.beginTransaction();
        try {
            final ContentValues values = new ContentValues();
            formContentValues(values, value);
            final long insert = mDb.insert(table, "", values);
            mDb.setTransactionSuccessful();
            return insert;
        } catch (SQLiteConstraintException e) {
            return -1;
        } catch (SQLException e) {
            return -1;
        } finally {
            mDb.endTransaction();
        }

    }


    /**
     * @param newses
     * @return 返回受影响的条数
     */
    public long insertAll(String table, ArrayList<GankNews> newses)
    {
        if (Utils.isEmpty(newses)) return 0;

        conformWritableDb();

        mDb.beginTransaction();
        long insert;
        long effect = 0;
        try {
            final ContentValues values = new ContentValues();
            for (GankNews value : newses) {
                formContentValues(values, value);
                insert = mDb.insert(table, "", values);
                if (insert != -1) ++effect;
                values.clear();
            }
            mDb.setTransactionSuccessful();
            return effect;
        } catch (SQLiteConstraintException e) {
            return 0;
        } catch (SQLException e) {
            return 0;
        } finally {
            mDb.endTransaction();
        }
    }

    /**
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return 返回受影响的条数
     */
    public int delete(String table, String whereClause, String[] whereArgs)
    {
        conformWritableDb();
        mDb.beginTransaction();
        try {
            final int delete = mDb.delete(table, whereClause, whereArgs);
            mDb.setTransactionSuccessful();
            return delete;
        } catch (SQLException e) {
            return 0;
        } finally {
            mDb.endTransaction();
        }
    }

    /**
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return 返回受影响的条数
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs)
    {
        conformWritableDb();

        mDb.beginTransaction();
        try {
            final int update = mDb.update(table, values, whereClause, whereArgs);
            mDb.setTransactionSuccessful();
            return update;
        } catch (SQLException e) {
            return 0;
        } finally {
            mDb.endTransaction();
        }
    }


    /**
     * @param sql
     * @param whereArgs
     * @return 如果根据搜索条件得到的结果不止一条，那么只返回按顺序的第一条
     */
    public GankNews findSingle(String sql, String[] whereArgs)
    {
        conformReadableDb();
        mDb.beginTransaction();
        try {
            final Cursor cursor = mDb.rawQuery(sql, whereArgs);
            if (cursor == null || cursor.getCount() < 0) return null;
            GankNews news = null;
            if (cursor.moveToNext()) {
                news = new GankNews();
                formGankNews(cursor, news);
            }
            cursor.close();
            mDb.setTransactionSuccessful();
            return news;
        } catch (SQLException e) {
            return null;
        } finally {
            mDb.endTransaction();
        }
    }


    public ArrayList<GankNews> find(String sql, String[] whereArgs)
    {
        conformReadableDb();
        mDb.beginTransaction();
        try {
            final Cursor cursor = mDb.rawQuery(sql, whereArgs);
            if (cursor == null || cursor.getCount() < 1) return null;

            final ArrayList<GankNews> newses = new ArrayList<>();
            GankNews news;
            while (cursor.moveToNext()) {
                news = new GankNews();
                formGankNews(cursor, news);
                newses.add(news);
            }
            cursor.close();
            mDb.setTransactionSuccessful();
            return newses;
        } catch (SQLException e) {
            return null;
        } finally {
            mDb.endTransaction();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //                           private methods below
    ///////////////////////////////////////////////////////////////////////////


    @NonNull private void formContentValues(ContentValues values, GankNews value)
    {
        values.put(Tables.History.DESC, value.desc);
        values.put(Tables.History.URL, value.url);
        values.put(Tables.History.IMAGE, value.image);
        values.put(Tables.History.TYPE, value.type);
        values.put(Tables.History.WHO, value.who);
        values.put(Tables.History.SOURCE, value.source);
        values.put(Tables.History.PUBLISH, value.publishedAt);
    }

    private void formGankNews(Cursor cursor, GankNews news)
    {
        news.desc = cursor.getString(cursor.getColumnIndex(Tables.History.DESC));
        news.url = cursor.getString(cursor.getColumnIndex(Tables.History.URL));
        news.image = cursor.getString(cursor.getColumnIndex(Tables.History.IMAGE));
        news.type = cursor.getString(cursor.getColumnIndex(Tables.History.TYPE));
        news.who = cursor.getString(cursor.getColumnIndex(Tables.History.WHO));
        news.source = cursor.getString(cursor.getColumnIndex(Tables.History.SOURCE));
        news.publishedAt = cursor.getColumnName(cursor.getColumnIndex(Tables.History.PUBLISH));
    }

    private void conformWritableDb()
    {
        if (mDb == null || !mDb.isOpen()) {
            mDb = mDbHelper.getWritableDatabase();
        } else if (mDb.isReadOnly()) {
            mDb.close();
            mDb = mDbHelper.getWritableDatabase();
        }
    }


    private void conformReadableDb()
    {
        if (mDb == null || !mDb.isOpen()) {
            mDb = mDbHelper.getReadableDatabase();
        } else if (!mDb.isReadOnly()) {
            mDb.close();
            mDb = mDbHelper.getReadableDatabase();
        }
    }

    /**
     *
     */
    public void release()
    {
        mDbHelper.close();
    }

}
