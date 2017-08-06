package com.example.horselai.gank.mvp.model.db;

import android.content.ContentValues;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.bean.GankNews;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/7/28.
 */
public class DbManagerTest
{

    private DbManager mManager;

    public DbManagerTest()
    {
        mManager = DbManager.newManager(App.context());
    }

    @Test public void insert() throws Exception
    {
        final GankNews news = new GankNews();
        news.desc = "hefoiawfjkel";
        news.url = "http://www.baidu.com";
        mManager.insertSingle(Tables.TABLE_HISTORY, news);


    }

    @Test public void insertAll() throws Exception
    {
        final ArrayList<GankNews> newses = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            final GankNews news = new GankNews();
            news.desc = "hello" + i;
            news.url = "http://www.baidu.comd" + i;
            newses.add(news);
        }
        final long insertAll = mManager.insertAll(Tables.TABLE_HISTORY, newses);
        System.out.println("inserted row :" + insertAll);

        find();
    }

    @Test public void delete() throws Exception
    {
        final int delete = mManager.delete(Tables.TABLE_HISTORY, "desc=?", new String[]{"hello0"});
        System.out.println("deleted row : " + delete);

        find();
    }

    @Test public void update() throws Exception
    {
        final ContentValues contentValues = new ContentValues(1);
        contentValues.put(Tables.History.WHO, "horseLai");
        final int update = mManager.update(Tables.TABLE_HISTORY, contentValues, "desc = ?", new String[]{"hello1"});
        System.out.println("updated row :" + update);

        find();
    }

    @Test public void findSingle() throws Exception
    {
        String sql = "select * from " + Tables.TABLE_HISTORY + " where who is ?";
        final GankNews news = mManager.findSingle(sql, new String[]{"horseLai"});
        System.out.println(news);
    }

    @Test public void find() throws Exception
    {
        final ArrayList<GankNews> newses = mManager.find("select * from " + Tables.TABLE_HISTORY, null);
        for (GankNews e : newses) {
            System.out.println(e);
        }
    }

}