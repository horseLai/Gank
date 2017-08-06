package com.example.horselai.gank.mvp.ui.iView;

/**
 * Created by laixiaolong on 2017/4/24.
 */

public interface ISuperView<T>
{
    void onLoadOk(T data);

    void onLoadFailed(Exception e);
}
