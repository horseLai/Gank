package com.example.horselai.gank.mvp.ui.fragment;

import com.example.horselai.gank.mvp.presenter.SaveHistoryPresenter;

/**
 * Created by horseLai on 2017/7/24.
 */

public class HistoryFragment extends CommSaveHistoryFragment
{

    @Override public void doUpdate()
    {
        updateData(SaveHistoryPresenter.HISTORY);
    }


    @Override public void onResume()
    {
        super.onResume();
        mRefresherHelper.startRefreshing();
        doUpdate();

    }
}
