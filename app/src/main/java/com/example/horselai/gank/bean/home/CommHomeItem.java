package com.example.horselai.gank.bean.home;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.base.BeanEntry;

import java.util.ArrayList;

/**
 * Created by laixiaolong on 2017/4/7.
 */

public class CommHomeItem<T extends BeanEntry> extends BeanEntry
{
    public String headerLabel;
    public T data;
    public ArrayList<T> dataList;

    public CommHomeItem()
    {
    }

    public CommHomeItem(String headerLabel, T data, ArrayList<T> dataList)
    {
        this.headerLabel = headerLabel;
        this.data = data;
        this.dataList = dataList;
    }

    public CommHomeItem(BaseMultipleTypeListAdapter.ItemType itemType, String headerLabel, T data, ArrayList<T> dataList)
    {
        this.itemType = itemType;
        this.headerLabel = headerLabel;
        this.data = data;
        this.dataList = dataList;
    }

    @Override public String toString()
    {
        return "CommHomeItem{" + "itemType=" + itemType + ", headerLabel='" + headerLabel + '\'' + ", data=" + data + ", dataList=" + dataList + '}';
    }
}
