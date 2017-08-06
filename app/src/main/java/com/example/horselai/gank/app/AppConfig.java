package com.example.horselai.gank.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;
import java.util.Set;

/**
 * Created by laixiaolong on 2017/1/21.
 * <p>
 * 该类包含应用SharedPreferences的配置方法
 */

public class AppConfig
{
    private static AppConfig config;

    private AppConfig()
    {
    }

    public static AppConfig getAppConfig()
    {
        if (config == null) {
            config = new AppConfig();
        }
        return config;
    }


    public boolean putIntegerToPref(String key, int value)
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.context());

        return preferences.edit().putInt(key, value).commit();
    }

    public boolean putStringToPref(String key, String value)
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.context());

        return preferences.edit().putString(key, value).commit();
    }

    public boolean putLongToPref(String key, long value)
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.context());

        return preferences.edit().putLong(key, value).commit();
    }

    public boolean putFloatToPref(String key, float value)
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.context());

        return preferences.edit().putFloat(key, value).commit();
    }


    public boolean putBooleanToPref(String key, boolean value)
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.context());

        return preferences.edit().putBoolean(key, value).commit();
    }


    public boolean putStringSetToPref(String key, Set<String> value)
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.context());

        return preferences.edit().putStringSet(key, value).commit();
    }

    public int getIntegerFromPref(String key, int defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(App.context()).getInt(key, defaultValue);
    }


    public long getLongFromPref(String key, long defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(App.context()).getLong(key, defaultValue);
    }


    public float getFloatFromPref(String key, float defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(App.context()).getFloat(key, defaultValue);
    }

    public String getStringFromPref(String key, String defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(App.context()).getString(key, defaultValue);
    }

    public Set<String> getStringSetFromPref(String key, Set<String> defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(App.context()).getStringSet(key, defaultValue);
    }

    public boolean getBooleanFromPref(String key, boolean defaultValue)
    {
        return PreferenceManager.getDefaultSharedPreferences(App.context()).getBoolean(key, defaultValue);
    }


    public Map<String, ?> getAllFromPref()
    {
        return PreferenceManager.getDefaultSharedPreferences(App.context()).getAll();
    }


    public boolean removeFromPref(String key, SharedPreferences preferences)
    {
        return preferences.edit().remove(key).commit();
    }

    public boolean clearPref(SharedPreferences preferences)
    {
        return preferences.edit().clear().commit();
    }


}
