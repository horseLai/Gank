package com.example.horselai.gank.util;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Created by horseLai on 2017/7/21.
 */

public final class JsonUtil
{
    private JsonUtil()
    {
    }


    public static JsonObject parseJson(String json)
    {
        if (TextUtils.isEmpty(json)) return null;
        try {
            final JsonParser parser = new JsonParser();
            return (JsonObject) parser.parse(json);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static boolean isJsonNull(JsonElement element)
    {
        return element == null || element.isJsonNull();
    }

    public static boolean isEmpty(JsonArray array)
    {
        return isJsonNull(array) || array.size() < 1;
    }

}
