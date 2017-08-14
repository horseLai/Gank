package com.example.horselai.gank.http.api;

import com.example.horselai.gank.util.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by horseLai on 2017/7/15.
 */

public final class GankApi
{

    public static final String ANDROID = "Android";
    public static final String ALL = "all";
    public static final String WEB = "前端";
    public static final String IOS = "iOS";
    public static final String EXPENDS = "拓展资源";
    public static final String VIDEO = "休息视频";
    public static final String BEAUTY = "福利";
    public static final String APP = "App";
    public static final String RECOMMEND = "瞎推荐";


    private GankApi()
    {
    }

    public static String apiAndroid(int itemNum, int pageNum)
    {
        return encodeNormalApiUrl(ANDROID, itemNum, pageNum);
    }

    public static String apiRecommend(int itemNum, int pageNum)
    {
        return encodeNormalApiUrl(RECOMMEND, itemNum, pageNum);
    }

    public static String apiAll(int itemNum, int pageNum)
    {
        return encodeNormalApiUrl(ALL, itemNum, pageNum);
    }

    public static String apiIOS(int itemNum, int pageNum)
    {
        return encodeNormalApiUrl(IOS, itemNum, pageNum);
    }

    public static String apiBeauty(int itemNum, int pageNum)
    {
        return encodeNormalApiUrl(BEAUTY, itemNum, pageNum);
    }


    public static String apiRelaxVideo(int itemNum, int pageNum)
    {
        return encodeNormalApiUrl(VIDEO, itemNum, pageNum);
    }

    public static String apiExpandRes(int itemNum, int pageNum)
    {
        return encodeNormalApiUrl(EXPENDS, itemNum, pageNum);
    }


    public static String apiWeb(int itemNum, int pageNum)
    {
        return encodeNormalApiUrl(WEB, itemNum, pageNum);
    }

    public static String apiApp(int itemNum, int pageNum)
    {
        return encodeNormalApiUrl(APP, itemNum, pageNum);
    }

    public static String apiRandom(String type, int itemNum)
    {
        return encodeRandom(type, itemNum);
    }

    private static String encodeRandom(String type, int itemNum)
    {
        //http://gank.io/api/random/data/%E7%A6%8F%E5%88%A9/1
        return "http://gank.io/api/random/data/" + Utils.urlEncodeUTF8(type) + "/" + itemNum;
    }


    /**
     * @param date
     * @param daysOffset 正值，代表今天的前几天
     * @return
     */
    public static String apiByDate(Date date, int daysOffset)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -daysOffset);
        //http://gank.io/api/day/2017/07/07
        return "http://gank.io/api/day/" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
    }


    public static String encodeNormalApiUrl(String type, int itemNum, int pageNum)
    {
        return "http://gank.io/api/data/" + Utils.urlEncodeUTF8(type) + "/" + itemNum + "/" + pageNum;
    }


    //http://gank.io/api/search/query/listview/category/Android/count/10/page/1
    public static String apiSearch(String keyWords, String category, int count, int page)
    {
        return "http://gank.io/api/search/query/" + Utils.urlEncodeUTF8(keyWords) + "/category/" + Utils.urlEncodeUTF8(category) + "/count/" + count + "/page/" + page;
    }
}
