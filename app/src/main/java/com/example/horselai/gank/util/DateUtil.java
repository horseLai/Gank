package com.example.horselai.gank.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by laixiaolong on 2017/4/1.
 */

public class DateUtil
{
    private static final String TAG = "DateUtil >>";

    /**
     * @param daysBefore 今天之前的天数
     * @return
     */
    public static Date getLastDay(int daysBefore)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -daysBefore);
        return calendar.getTime();
    }

    /**
     * @param daysNext 今天之后的天数
     * @return
     */
    public static Date getNextDay(int daysNext)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, daysNext);
        return calendar.getTime();
    }

}
