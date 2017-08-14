package com.example.horselai.gank.mvp.model;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.bean.home.CommHomeItem;
import com.example.horselai.gank.comm.ICallback;
import com.example.horselai.gank.http.api.GankApi;
import com.example.horselai.gank.http.request.HttpRequest;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.util.JsonUtil;
import com.example.horselai.gank.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by horseLai on 2017/7/15.
 * <p>
 * 干货集中营数据服务
 */


// ps: 这个类中有较多的重复代码，因为我觉得如果通过多写点代码能降低复杂度，
// 因而提高性能，还能减少逻辑上的复杂混乱，那么，我觉得这样做就是有价值的，
// 正真的简洁是逻辑的简洁，当然，此时水平有限，欢迎指正
public final class GankFetcher
{
    private final AsyncService mService;
    private static final String TAG = "GankFetcher";
    private static final boolean DEBUG = true;


    public static GankFetcher getFetcher()
    {
        return Builder.GANK_FETCHER;
    }


    final static class Builder
    {
        private static final GankFetcher GANK_FETCHER = new GankFetcher();
    }

    private GankFetcher()
    {
        mService = AsyncService.getService();
    }


    /**
     * @param apiType  干货类型, 注意：不包括‘福利’
     * @param itemNum  条数
     * @param pageNum  页码
     * @param callback
     */
    public void fetchGankByTypeDefaultAsync(final String apiType, final int itemNum, final int pageNum, final ICallback<String, ArrayList<GankNews>> callback)
    {
        mService.addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                final String url = GankApi.encodeNormalApiUrl(apiType, itemNum, pageNum);

                final ArrayList<GankNews> newses = fetchNormalGank(url, GankApi.apiRandom(GankApi.BEAUTY, itemNum));

                if (Utils.isEmpty(newses)) {
                    callback.onFail(new Exception("Failed to fetch Gank!"), url);
                    return;
                }
                callback.onSuccess(url, newses);
            }
        });

    }


    /**
     * @param apiUrl       干货api链接 ，注意：不包括‘福利’
     * @param apiBeautyUrl 福利api链接, null 时使用图片插空方式填补图片空缺，非null时为全部替换，两者都能保证有配图
     * @return
     */
    public ArrayList<GankNews> fetchNormalGank(String apiUrl, String apiBeautyUrl)
    {
        return fetchNormalGank(apiUrl, apiBeautyUrl, -1);
    }


    /**
     * @param apiUrl       干货api链接 ，注意：不包括‘福利’
     * @param apiBeautyUrl 福利api链接
     * @param itemType
     * @return
     */
    public ArrayList<GankNews> fetchNormalGank(String apiUrl, String apiBeautyUrl, int itemType)
    {
        try {
            final JsonArray results = getJsonArrComm(apiUrl);
            //两种填补图片空缺的方式
            //1. 统一请求图片替换，最好随机产生图片，
            //2. 插空的方式，填补无图片的空位
            //final String beautyUrl = GankApi.apiBeauty(itemNum, pageNum);
            JsonArray beautyArr = null;
            // 指定图片api链接时
            if (!TextUtils.isEmpty(apiBeautyUrl)) {
                beautyArr = getJsonArrComm(apiBeautyUrl);
                if (JsonUtil.isEmpty(beautyArr)) return null;
            }

            final ArrayList<GankNews> gankNewses = new ArrayList<>();

            int nullImageCount = 0;
            int imageIndex = 0;
            GankNews news;
            JsonObject obj;
            JsonElement tmp;
            for (JsonElement e : results) {
                obj = (JsonObject) e;
                news = new GankNews();

                if (itemType > 0) news.itemType = itemType;
                else news.itemType = BaseMultipleTypeListAdapter.ItemType.TYPE_STAGGERED;

                tmp = obj.get("source");
                if (!JsonUtil.isJsonNull(tmp)) news.source = tmp.getAsString();
                else news.source = "未知";

                news.desc = obj.get("desc").getAsString();
                news.publishedAt = obj.get("publishedAt").getAsString();
                tmp = obj.get("who");
                if (!JsonUtil.isJsonNull(tmp)) {
                    news.who = tmp.getAsString();
                } else {
                    news.who = "佚名";
                }
                news.type = obj.get("type").getAsString();
                news.url = obj.get("url").getAsString();

                if (beautyArr == null) {
                    tmp = obj.get("images");
                    if (JsonUtil.isJsonNull(tmp)) {
                        ++nullImageCount;
                        Log.i(TAG, "fetchNormalGank: nullImageCount >> " + nullImageCount);
                    } else {
                        news.image = tmp.getAsJsonArray().get(0).getAsString();
                    }
                } else {
                    //把图片加上
                    obj = (JsonObject) beautyArr.get(imageIndex);
                    ++imageIndex;
                    news.image = obj.get("url").getAsString();
                }
                gankNewses.add(news);
            }
            //  Log.i(TAG, "fetchNormalGank: nullImageCount >> " + nullImageCount);
            //不指定图片api链接时进行乱序插空填补空缺图片
            if (beautyArr == null && TextUtils.isEmpty(apiBeautyUrl)) {
                final ArrayList<GankBeauty> beauties = fetchGankBeautyRandom(nullImageCount);
                if (Utils.isEmpty(beauties)) return null;
                //填补无图片项
                for (GankBeauty beauty : beauties) {
                    news = gankNewses.get(imageIndex++);
                    if (TextUtils.isEmpty(news.image)) news.image = beauty.url;
                }
            }

            //  Log.i(TAG, "fetchNormalGank: "+gankNewses.toString());

            return gankNewses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据日期异步获取指定日期的数据
     *
     * @param date      目标日期
     * @param dayOffset 与目标日期相差的天数（前一天为-， 后一天为+）
     * @param callback  数据回调（注意，是异步回掉）
     */
    public void fetchGankByDateAsync(final Date date, final int dayOffset, final ICallback<String, SparseArray<Object>> callback)
    {

        mService.addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                final String url = GankApi.apiByDate(date, dayOffset);
                final SparseArray<Object> todays = fetchGankByDate(url, true);
                if (todays == null || todays.size() == 0) {
                    callback.onFail(new Exception("Failed to fetch Gank!"), url);
                    return;
                }
                callback.onSuccess(url, todays);
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    //                              日推
    ///////////////////////////////////////////////////////////////////////////


    public static final int DAY_RECOMMEND_NEWS = 0;
    public static final int DAY_RECOMMEND_BEAUTY = 1;

    /**
     * 根据日期获取数据
     *
     * @param url 数据接口
     * @return 返回解析后的数据
     */
    public SparseArray<Object> fetchGankByDate(String url, boolean canCache)
    {
        if (DEBUG) Log.i(TAG, "fetchGankApp: url >>: " + url);

        try {

            String content;
            try {
                content = HttpRequest.newNormalRequest(url, canCache).doRequest("utf-8");
            } catch (Exception e) {
                return null;
            }
            if (TextUtils.isEmpty(content)) return null;
            final JsonParser parser = new JsonParser();
            final JsonObject rootObj = (JsonObject) parser.parse(content);
            final boolean error = rootObj.get("error").getAsBoolean();
            if (error) return null;

            // 开始 解析数据
            final SparseArray<Object> commItems = new SparseArray<>();
            final JsonObject results = rootObj.get("results").getAsJsonObject();
            final JsonArray category = rootObj.get("category").getAsJsonArray();
            if (JsonUtil.isJsonNull(results) || JsonUtil.isEmpty(category)) return null;

            Log.i(TAG, "fetchGankByDate: 组合界面数据");
            //组合界面数据
            formDayRecommend(category, results, commItems);
            return commItems;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void formDayRecommend(JsonArray category, JsonObject results, SparseArray<Object> commItems)
    {
        String type;
        ArrayList<CommHomeItem<GankNews>> items = new ArrayList<>();
        GankBeauty beauty = new GankBeauty();

        for (JsonElement e : category) {
            type = e.getAsString();
            //Log.i(TAG, "formDayRecommend: type >>: " + type);
            if (!GankApi.BEAUTY.equals(type)) {
                parseGankNews(type, results.get(type).getAsJsonArray(), items);
            } else {
                parseBeauty(results.get(type).getAsJsonArray(), beauty);
            }
        }

        commItems.put(DAY_RECOMMEND_NEWS, items);
        commItems.put(DAY_RECOMMEND_BEAUTY, beauty);
    }

    private void parseGankNews(String type, JsonArray newsArr, ArrayList<CommHomeItem<GankNews>> items)
    {

        JsonObject obj;
        JsonElement tmp;
        GankNews news;
        final int itemType = decideItemType(type, newsArr.size());

        items.add(new CommHomeItem<GankNews>(BaseMultipleTypeListAdapter.ItemType.TYPE_HEADER, type, null, null));

        CommHomeItem<GankNews> item;
        ArrayList<GankBeauty> beauties = fetchGankBeautyRandom(newsArr.size());
        int beautyIndex = 0;
        for (JsonElement e : newsArr) {
            obj = (JsonObject) e;
            news = new GankNews();

            news.desc = obj.get("desc").getAsString();
            news.publishedAt = obj.get("publishedAt").getAsString();
            tmp = obj.get("who");
            if (!JsonUtil.isJsonNull(tmp)) {
                news.who = tmp.getAsString();
            } else {
                news.who = "佚名";
            }

            tmp = obj.get("source");
            if (!JsonUtil.isJsonNull(tmp)) {
                news.source = tmp.getAsString();
            } else {
                news.source = "未知";
            }

            Log.i(TAG, "parseGankNews: ic_news_white.who = " + news.who);
            news.type = obj.get("type").getAsString();
            news.url = obj.get("url").getAsString();
            news.image = beauties.get(beautyIndex++).url;

            item = new CommHomeItem<>(itemType, type, news, null);
            items.add(item);
        }

        beauties.clear();

    }

    private int decideItemType(String type, int size)
    {
        int itemType;
        if (GankApi.VIDEO.equals(type)) {
            itemType = BaseMultipleTypeListAdapter.ItemType.TYPE_OTHER;
        } else if (GankApi.ANDROID.equals(type)) {
            itemType = BaseMultipleTypeListAdapter.ItemType.TYPE_GRID;
        } else {
            //
            if (size % 2 != 0) itemType = BaseMultipleTypeListAdapter.ItemType.TYPE_LINEAR;
            else itemType = BaseMultipleTypeListAdapter.ItemType.TYPE_GRID;
        }

        return itemType;
    }

    private void parseBeauty(JsonArray beautyArr, GankBeauty beauty)
    {
        if (!JsonUtil.isEmpty(beautyArr)) {
            final JsonObject obj = beautyArr.get(0).getAsJsonObject();

            beauty.publishedAt = obj.get("publishedAt").getAsString();
            final JsonElement tmp = obj.get("who");
            if (!JsonUtil.isJsonNull(tmp)) {
                beauty.who = tmp.getAsString();
            } else {
                beauty.who = "佚名";
            }
            beauty.url = obj.get("url").getAsString();
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    //                          福利
    ///////////////////////////////////////////////////////////////////////////

    public ArrayList<GankBeauty> fetchGankBeautyRandom(int itemNum)
    {
        return fetchGankBeauty(GankApi.apiRandom("福利", itemNum), -1);
    }

    public ArrayList<GankBeauty> fetchGankBeautyRandom(int itemNum, int itemType)
    {
        return fetchGankBeauty(GankApi.apiRandom("福利", itemNum), itemType);
    }

    public ArrayList<GankBeauty> fetchGankBeauty(int itemNum, int pageNum)
    {
        return fetchGankBeauty(GankApi.apiBeauty(itemNum, pageNum), -1);
    }


    public ArrayList<GankBeauty> fetchGankBeauty(String apiBeauty, int itemType)
    {
        try {
            final JsonArray beautyArr = getJsonArrComm(apiBeauty);
            if (JsonUtil.isEmpty(beautyArr)) return null;

            final ArrayList<GankBeauty> beauties = new ArrayList<>();
            GankBeauty beauty;
            JsonObject obj;
            JsonElement tmp;
            for (JsonElement e : beautyArr) {
                obj = (JsonObject) e;
                beauty = new GankBeauty();
                if (itemType > 0) beauty.itemType = itemType;

                beauty.publishedAt = obj.get("publishedAt").getAsString();
                beauty.url = obj.get("url").getAsString();
                tmp = obj.get("who");
                if (!JsonUtil.isJsonNull(tmp)) {
                    beauty.who = tmp.getAsString();
                } else {
                    beauty.who = "佚名";
                }

                beauties.add(beauty);
            }

            return beauties;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    ///////////////////////////////////////////////////////////////////////////
    //                          主页数据
    ///////////////////////////////////////////////////////////////////////////


    public ArrayList<CommHomeItem> formHomePageGank()
    {
        //不指定泛型类型，这样泛型擦除后类型是BeanEntry，
        // 意味着这里的几种类型都兼容，虽然会有警告
        final ArrayList<CommHomeItem> data = new ArrayList<>();

        //福利
        final ArrayList<GankBeauty> gankBeauties = fetchGankBeautyRandom(5);
        data.add(new CommHomeItem(BaseMultipleTypeListAdapter.ItemType.TYPE_SLIDE_ROTATION, GankApi.BEAUTY, null, gankBeauties));

        //bar
        data.add(new CommHomeItem(BaseMultipleTypeListAdapter.ItemType.TYPE_CATEGORY_BAR, null, null, null));

        //Android
        ArrayList<GankNews> newses = fetchNormalGank(GankApi.apiRandom(GankApi.ANDROID, 4), GankApi.apiRandom(GankApi.BEAUTY, 4));
        if (!Utils.isEmpty(newses)) {
            data.add(new CommHomeItem(BaseMultipleTypeListAdapter.ItemType.TYPE_HEADER, GankApi.ANDROID, null, null));
            transform(GankApi.ANDROID, data, newses, BaseMultipleTypeListAdapter.ItemType.TYPE_GRID);
        }

        //IOS
        newses = fetchNormalGank(GankApi.apiRandom(GankApi.IOS, 3), GankApi.apiRandom(GankApi.BEAUTY, 3));
        if (!Utils.isEmpty(newses)) {
            data.add(new CommHomeItem(BaseMultipleTypeListAdapter.ItemType.TYPE_HEADER, GankApi.IOS, null, null));
            transform(GankApi.IOS, data, newses, BaseMultipleTypeListAdapter.ItemType.TYPE_LINEAR);
        }

        //WEB
        newses = fetchNormalGank(GankApi.apiRandom(GankApi.WEB, 4), GankApi.apiRandom(GankApi.BEAUTY, 4));
        if (!Utils.isEmpty(newses)) {
            data.add(new CommHomeItem(BaseMultipleTypeListAdapter.ItemType.TYPE_HEADER, GankApi.WEB, null, null));
            transform(GankApi.WEB, data, newses, BaseMultipleTypeListAdapter.ItemType.TYPE_GRID);
        }

        //拓展资源
        newses = fetchNormalGank(GankApi.apiRandom(GankApi.EXPENDS, 3), GankApi.apiRandom(GankApi.BEAUTY, 3));
        if (!Utils.isEmpty(newses)) {
            data.add(new CommHomeItem(BaseMultipleTypeListAdapter.ItemType.TYPE_HEADER, GankApi.EXPENDS, null, null));
            transform(GankApi.EXPENDS, data, newses, BaseMultipleTypeListAdapter.ItemType.TYPE_LINEAR);
        }
        //app
        newses = fetchNormalGank(GankApi.apiRandom(GankApi.APP, 4), GankApi.apiRandom(GankApi.BEAUTY, 4));
        if (!Utils.isEmpty(newses)) {
            data.add(new CommHomeItem(BaseMultipleTypeListAdapter.ItemType.TYPE_HEADER, GankApi.APP, null, null));
            transform(GankApi.APP, data, newses, BaseMultipleTypeListAdapter.ItemType.TYPE_GRID);
        }


        //休息视频
        newses = fetchNormalGank(GankApi.apiRandom(GankApi.VIDEO, 2), GankApi.apiRandom(GankApi.BEAUTY, 2));
        if (!Utils.isEmpty(newses)) {
            data.add(new CommHomeItem(BaseMultipleTypeListAdapter.ItemType.TYPE_HEADER, GankApi.VIDEO, null, null));
            transform(GankApi.VIDEO, data, newses, BaseMultipleTypeListAdapter.ItemType.TYPE_OTHER);
        }

        return data;
    }

    private void transform(String label, ArrayList<CommHomeItem> data, ArrayList<GankNews> newses, int itemType)
    {
        CommHomeItem item;
        for (GankNews news : newses) {
            item = new CommHomeItem(itemType, label, news, null);
            data.add(item);
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    //                  private methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 请求json数据，并解析出有效数据
     *
     * @param url
     * @return
     * @throws Exception
     */
    private JsonArray getJsonArrComm(String url) throws Exception
    {
        final HttpRequest httpRequest = HttpRequest.newNormalRequest(url, false);
        final String result = httpRequest.doRequest("UTF-8");

        final JsonObject object = JsonUtil.parseJson(result);
        if (JsonUtil.isJsonNull(object)) return null;

        final boolean error = object.get("error").getAsBoolean();
        if (error) return null;
        return object.get("results").getAsJsonArray();
    }


    private int checkItemType(String type)
    {
        switch (type) {
            case "福利":
                return BaseMultipleTypeListAdapter.ItemType.TYPE_SLIDE_ROTATION;

            case "Android":
                return BaseMultipleTypeListAdapter.ItemType.TYPE_GRID;

            case "iOS":
                return BaseMultipleTypeListAdapter.ItemType.TYPE_LINEAR;

            case "休息视频":
                return BaseMultipleTypeListAdapter.ItemType.TYPE_OTHER;

            case "拓展资源":
                return BaseMultipleTypeListAdapter.ItemType.TYPE_GRID;

            case "all":
                return BaseMultipleTypeListAdapter.ItemType.TYPE_STAGGERED;

        }
        return BaseMultipleTypeListAdapter.ItemType.TYPE_STAGGERED;
    }


    public static final String SEARCH_TOTAL = "total";
    public static final String SEARCH_CATEGORY = "source";
    public static final String SEARCH_DATA = "data";


    public ArrayMap<String, Object> search(String url, int itemType)
    {
        try {
            final String content = HttpRequest.newNormalRequest(url, false).doRequest("UTF-8");

            if (TextUtils.isEmpty(content)) return null;
            final JsonParser parser = new JsonParser();
            final JsonObject rootObj = parser.parse(content).getAsJsonObject();

            final boolean error = rootObj.get("error").getAsBoolean();
            final int totalCount = rootObj.get("count").getAsInt();
            if (error || totalCount < 1) return null;

            final JsonArray results = rootObj.get("results").getAsJsonArray();
            if (JsonUtil.isEmpty(results)) return null;

            final ArrayMap<String, Object> data = new ArrayMap<>(3);
            data.put(SEARCH_TOTAL, totalCount);

            final ArrayList<GankNews> searches = new ArrayList<>(totalCount);

            GankNews result;
            JsonObject obj;
            JsonElement tmp;
            for (JsonElement e : results) {
                result = new GankNews();
                obj = e.getAsJsonObject();

                result.itemType = itemType;

                result.desc = obj.get("desc").getAsString();
                result.publishedAt = obj.get("publishedAt").getAsString();
                //result.readability = obj.get("readability").getAsString();
                result.type = obj.get("type").getAsString();
                result.url = obj.get("url").getAsString();
                tmp = obj.get("who");
                if (!JsonUtil.isJsonNull(tmp)) result.who = tmp.getAsString();
                else result.who = "未知";

                searches.add(result);
            }
            data.put(SEARCH_DATA, searches);
            //Log.i(TAG, "search: " + searches);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
