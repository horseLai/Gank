package com.example.horselai.gank.mvp.presenter.iPresenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;

/**
 * Created by laixiaolong on 2017/4/24.
 *
 * @param <T> 数据请求后返回的数据类型，即你的目标数据类型
 */
public abstract class AbsSuperPresenter<T>
{
    private final ISuperView<T> mView;

    protected static final int SUCCESS = 0;
    protected static final int FAIL = 1;

    private final Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case SUCCESS:
                    mView.onLoadOk((T) msg.obj);
                    break;
                case FAIL:
                    mView.onLoadFailed((Exception) msg.obj);
                    break;

            }
        }
    };

    public ISuperView getView()
    {
        return mView;
    }

    public Handler getHandler()
    {
        return mHandler;
    }

    public AbsSuperPresenter(ISuperView mView)
    {
        this.mView = mView;
    }

    public abstract void update(Object... params);


    public boolean checkIsOnline()
    {
        if (App.isOnline()) {
            return true;
        } else {
            mHandler.obtainMessage(FAIL, new Exception("网络未连接_〆(´Д｀ )")).sendToTarget();
            return false;
        }
    }

    public void release()
    {

    }

}
