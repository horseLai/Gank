package com.example.horselai.gank.mvp.model.db;

/**
 * Created by horseLai on 2017/7/28.
 */

public class Tables
{
    public static final String TABLE_HISTORY = "ScanHistory";
    public static final String TABLE_SAVE = "ScanSave";

    public interface History
    {
        String DESC = "desc";
        String PUBLISH = "publishedAt";
        String SOURCE = "source";
        String TYPE = "type";
        String URL = "url";
        String WHO = "who";
        String IMAGE = "image";
    }

    public interface Save extends History
    {

    }
}
