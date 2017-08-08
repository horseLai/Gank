package com.example.horselai.gank.mvp.ui.fragment;

import com.example.horselai.gank.mvp.presenter.ReadingPresenter;

/**
 * Created by horseLai on 2017/8/8.
 */

public class EquipMacPlayFragment extends CommReadingListFragment
{


    @Override protected int getFragmentType()
    {
        return ReadingPresenter.TYPE_MAC_PLAY;
    }

}
