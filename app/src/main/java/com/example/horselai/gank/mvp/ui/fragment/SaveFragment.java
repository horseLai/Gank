package com.example.horselai.gank.mvp.ui.fragment;

import com.example.horselai.gank.mvp.presenter.SaveHistoryPresenter;

/**
 * Created by horseLai on 2017/7/24.
 */

public class SaveFragment extends CommSaveHistoryFragment
{

    @Override public void doUpdate()
    {
        updateData(SaveHistoryPresenter.SAVE);
    }

    @Override public void onResume()
    {
        super.onResume();
        mRefresherHelper.startRefreshing();
        doUpdate();

    }

}
