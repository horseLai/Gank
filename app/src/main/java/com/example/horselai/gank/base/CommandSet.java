package com.example.horselai.gank.base;

/**
 * Created by laixiaolong on 2017/3/11.
 * <p>
 * 命令集， 所有fragment回调命令在这里集中声明（主要是避免fragment回调指令较多值出现重复值的情况）
 */
public interface CommandSet
{

    //book fragment
    int ACTION_HIDE_NAV_BAR = 0X3;
    int ACTION_SHOW_NAV_BAR = 0X4;


    //BBCHomeFragment
    int ACTION_GO_TO_PAGE = 0X4;

}
