package com.example.horselai.gank.app;

import android.app.Activity;
import android.os.Process;

import java.util.Stack;

/**
 * Created by laixiaolong on 2017/1/21.
 * <p>
 * 该类用于以LIFO方式管理应用中的activity
 */

public class AppManager
{
    private static AppManager manger;
    private Stack<Activity> mActivityStack;

    private AppManager()
    {
        mActivityStack = new Stack<>();
    }

    public static AppManager getAppManger()
    {
        if (manger == null) manger = new AppManager();

        return manger;
    }


    /**
     * 压入一个activity到栈中
     *
     * @param activity
     */
    public void pushActivity(Activity activity)
    {
        if (activity != null && !mActivityStack.contains(activity)) {
            mActivityStack.push(activity);
        }
    }


    /**
     * 关闭activity，并从栈中删除该activity，如果该activity存在于栈中的话
     *
     * @param activity
     */
    public void finishActivity(Activity activity)
    {
        if (activity != null) {

            if (mActivityStack.contains(activity)) {
                mActivityStack.remove(activity);
            }
            activity.finish();
        }
    }

    /**
     * 返回栈顶的activity，并且将其从栈中删除
     *
     * @return
     */
    public Activity popTopActivity()
    {
        return mActivityStack.pop();
    }


    /**
     * 关闭所有activity，并清栈
     */
    public void finishAllActivities()
    {
        for (Activity a : mActivityStack) {

            if (a != null) a.finish();
        }
        clearStack();
    }


    /**
     * 清空栈中的所有activity
     */
    public void clearStack()
    {
        mActivityStack.clear();
    }


    /**
     * 从栈中移除一个activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity)
    {
        if (activity != null && mActivityStack.contains(activity)) {
            mActivityStack.remove(activity);
        }
    }


    /**
     * 退出当前App进程，释放占用内存
     */
    public void exitApp()
    {
        try {
            finishAllActivities();

            System.exit(0);
            Process.killProcess(Process.myPid());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
