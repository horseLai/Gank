package com.example.horselai.gank.base;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.http.loader.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by laixiaolong on 2017/4/25.
 * <p>
 * <p>注意：
 * <p>1. 请最好使用 StaggeredGridLayoutManager, 对于各种ItemType，
 * 如果你在底部使用了TYPE_STAGGERED (交错效果，实际上TYPE_GRID也是一样的，
 * 如果你把item的宽或高设置的不一样的话，他俩效果是一致的)类型，那么您应该让hasFooterView()返回false，
 * 并在 RecyclerView的scroll listener种实现上拉自动加载， 不然实现出来的效果会很奇怪，这样不友好</p>
 * <p>
 * 2. 如果你当真要单纯的使用LinearLayoutManager或GridLayoutManager，也是可以的，
 * 此时将把数据的itemType视为无效，而需要实现ViewHolderBinder的onCreateOrdinaryVieHolder(...)方法创建VH
 * ，实现onBindOrdinaryViewHolder(...)方法绑定数据
 * </p>
 * </p>
 */

public abstract class BaseMultipleTypeListAdapter<T extends BeanEntry> extends RecyclerView.Adapter
{
    protected Context mContext;
    //使用链表主要是因为这里可能有大量的插入、删除操作
    private LinkedList<T> mDataList;
    private LayoutInflater mLayoutInflater;
    private BaseViewHolderBinder mViewHolderBinder;
    private static final String TAG = "BaseMultipleTypeListAda";

    private boolean mLoadImageNow = true;
    private int mLastFirstItem;
    private RecyclerView.LayoutManager mLayoutManager;

    //独立于其他item type
    private static final int TYPE_FOOTER = 0x10;
    private static final int TYPE_NO_DATA = 0x11;
    private static final int TYPE_ORDINARY = 0x12;


    // protected abstract BaseViewHolderBinder<T> onCreateVHBinder();

    /**
     * 更新当前列表滑动状态 ，将该方法在recyclerView的滑动监听中调用即可
     *
     * @param manager
     * @param recyclerViewState
     */
    public void updateListScrollState(RecyclerView.LayoutManager manager, int recyclerViewState)
    {
        if (App.DEBUG) Log.i(TAG, "updateListScrollState: >>>> ");
        //检测状态，并且解决了手指稍微一动就发生状态改变的问题
        if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sManager = (StaggeredGridLayoutManager) manager;
            mLoadImageNow = recyclerViewState == RecyclerView.SCROLL_STATE_IDLE;
            final int[] into = sManager.findFirstVisibleItemPositions(new int[2]);

            mLoadImageNow &= (into[0] != mLastFirstItem);
            mLastFirstItem = into[0] != 0 ? into[0] : mLastFirstItem;

        } else if ((manager instanceof LinearLayoutManager)) {
            LinearLayoutManager llManager = (LinearLayoutManager) manager;
            mLoadImageNow = recyclerViewState == RecyclerView.SCROLL_STATE_IDLE;
            final int first = llManager.findFirstVisibleItemPosition();

            mLoadImageNow &= (first != mLastFirstItem);
            if (first != 0) mLastFirstItem = first;
        }

        if (mLoadImageNow) {
            notifyDataSetChanged();
        }
    }


    /**
     * @param mViewHolderBinder 数据的绑定工作都转交该类处理
     */
    public void setViewHolderBinder(BaseViewHolderBinder<T> mViewHolderBinder)
    {
        this.mViewHolderBinder = mViewHolderBinder;
    }

    private boolean mHasLayoutManager = false;

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        if (!mHasLayoutManager) {
            mHasLayoutManager = true;
            mLayoutManager = recyclerView.getLayoutManager();
        }
    }


    public BaseMultipleTypeListAdapter(Context context)
    {
        this.mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataList = new LinkedList<>();
    }


    public enum ItemType
    {
        TYPE_SLIDE_ROTATION(1),
        TYPE_HEADER(2),
        TYPE_LINEAR(3),
        TYPE_GRID(4),
        TYPE_STAGGERED(5),
        TYPE_LIST(6),
        TYPE_OTHER(7),
        TYPE_CATEGORY_BAR(8);

        private int itemType = 0;

        ItemType(int type)
        {
            this.itemType = type;
        }

        public int value()
        {
            return itemType;
        }
    }


    public void addItemsToHeadPos(ArrayList<T> items)
    {
        insertItemsIntoIndex(0, items);
    }


    public void insertItemsIntoFootPos(ArrayList<T> items)
    {
        insertItemsIntoIndex(mDataList.size(), items);
    }

    public void addItem(T item)
    {
        if (item != null) {
            mDataList.add(item);
            notifyItemInserted(mDataList.size() - 1);
        }
    }

    public void insertItem(T item, int index)
    {
        if (item != null) {
            mDataList.add(index, item);
            notifyItemInserted(index);
        }
    }

    public void insertItemsIntoIndex(int index, ArrayList<T> items)
    {
        if (items != null && !items.isEmpty()) {
            mDataList.addAll(index, items);
            notifyItemRangeInserted(index, items.size());
        }
    }

    public void removeAllItems()
    {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void removeItems(int startIndex, int endIndex)
    {
        if (mDataList.size() - 1 >= endIndex) {
            mDataList.subList(startIndex, endIndex).clear();
            notifyItemRangeRemoved(startIndex, endIndex);
        }
    }

    public void removeItem(T item)
    {
        if (item != null && mDataList.contains(item)) {
            mDataList.remove(item);
            // notifyItemRemoved(mDataList.indexOf(item));
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position)
    {
        if (position >= 0 && position < mDataList.size()) {
            mDataList.remove(position);
            //使用notifyItemRemoved 有时会因位置没更新而导致再次删除时删除出错
            //notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    public LinkedList<T> getDataList()
    {
        return mDataList;
    }


    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (mViewHolderBinder == null) {
            throw new RuntimeException("大哥，BaseCommViewHolderBinder是必须设置的，不然咋搞 ,,ԾㅂԾ,, ！！");
        }


        if (viewType == TYPE_FOOTER) {
            return mViewHolderBinder.onCreateFooterViewHolder(parent, viewType);
        } else if (viewType == TYPE_NO_DATA) {
            return mViewHolderBinder.onCreateNoDataViewHolder(parent, viewType);
        }

        //使用LinearLayoutManager或GridLayoutManager时
        if (TYPE_ORDINARY == viewType) {
            return mViewHolderBinder.onCreateOrdinaryVieHolder(parent, viewType);
        } else {
            //使用StaggeredGridLayoutManager时
            if (viewType == ItemType.TYPE_SLIDE_ROTATION.itemType) {
                return mViewHolderBinder.onCreateSliderRotationViewHolder(parent, viewType);
            } else if (viewType == ItemType.TYPE_CATEGORY_BAR.itemType) {
                return mViewHolderBinder.onCreateCategoryBarViewHolder(parent, viewType);
            } else if (viewType == ItemType.TYPE_HEADER.itemType) {
                return mViewHolderBinder.onCreateHeaderViewHolder(parent, viewType);
            } else if (viewType == ItemType.TYPE_LINEAR.itemType) {
                return mViewHolderBinder.onCreateLinearViewHolder(parent, viewType);
            } else if (viewType == ItemType.TYPE_GRID.itemType) {
                return mViewHolderBinder.onCreateGridViewHolder(parent, viewType);
            } else if (viewType == ItemType.TYPE_STAGGERED.itemType) {
                return mViewHolderBinder.onCreateStaggeredViewHolder(parent, viewType);
            } else if (viewType == ItemType.TYPE_LIST.itemType) {
                View view = mLayoutInflater.inflate(R.layout.base_home_vh_type_list, parent, false);
                RecyclerView rvList = (RecyclerView) view.findViewById(R.id.rv_base_home_list);
                return mViewHolderBinder.onCreateListItemViewHolder(rvList, parent, viewType);
            } else if (viewType == ItemType.TYPE_OTHER.itemType) {
                return mViewHolderBinder.onCreateOtherViewHolder(parent, viewType);
            } else {
                throw new RuntimeException("同志，你的ItemType没有匹配到哦，请确定你在数据中设置了ItemType参数 ,,ԾㅂԾ,,！");
            }
        }
    }


    long delay = 0;

    @Override public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {
        final long start = System.currentTimeMillis();

        if (mViewHolderBinder == null) {
            throw new RuntimeException("大哥，BaseCommViewHolderBinder是必须设置的，不然咋搞 ,,ԾㅂԾ,, ！！");
        }
        if (mDataList.isEmpty()) return;


        final int itemViewType = getItemViewType(position);
        //对于链表而言，使用iterator做get操作性能更好
        final ListIterator<T> iterator = mDataList.listIterator(position);
        //if (!iterator.hasNext()) return;

        if (!mLoadImageNow) {
            //滑动过程中把其他现有队列任务都清理掉
            ImageLoader.getImageLoader().getThreadPoolHandler().clearTaskQueue();
        }

        if (itemViewType == ItemType.TYPE_SLIDE_ROTATION.itemType) {
            mViewHolderBinder.onBindSliderRotationViewHolder(holder, mLoadImageNow, iterator.next(), position);
        } else if (itemViewType == ItemType.TYPE_CATEGORY_BAR.itemType) {
            mViewHolderBinder.onBindCategoryBarViewHolder(holder, mLoadImageNow, iterator.next(), position);
        } else if (itemViewType == ItemType.TYPE_HEADER.itemType) {
            mViewHolderBinder.onBindHeaderViewHolder(holder, mLoadImageNow, iterator.next(), position);
        } else if (itemViewType == ItemType.TYPE_LINEAR.itemType) {
            mViewHolderBinder.onBindLinearViewHolder(holder, mLoadImageNow, iterator.next(), position);
        } else if (itemViewType == ItemType.TYPE_GRID.itemType) {
            mViewHolderBinder.onBindGridViewHolder(holder, mLoadImageNow, iterator.next(), position);
        } else if (itemViewType == ItemType.TYPE_STAGGERED.itemType) {
            mViewHolderBinder.onBindStaggeredViewHolder(holder, mLoadImageNow, iterator.next(), position);
        } else if (itemViewType == ItemType.TYPE_LIST.itemType) {
            mViewHolderBinder.onBindListViewHolder(holder, mLoadImageNow, iterator.next(), position);
        } else if (itemViewType == ItemType.TYPE_OTHER.itemType) {
            mViewHolderBinder.onBindOtherViewHolder(holder, mLoadImageNow, iterator.next(), position);
        } else if (TYPE_ORDINARY == itemViewType) {
            mViewHolderBinder.onBindOrdinaryViewHolder(holder, mLoadImageNow, iterator.next(), position);
        }

        final long last = System.currentTimeMillis();
        delay += (last - start);
        if (position % 20 == 0) {
            Log.i(TAG, "onBindViewHolder: delay : " + delay + " ms");
            delay = 0;
        }
    }


    /**
     * 决定是否包含footerView，如果
     *
     * @return
     */
    public abstract boolean hasFooterView();


    @Override public int getItemCount()
    {
        final int size = mDataList.size();
        return hasFooterView() ? size + 1 : size;
    }


    @Override public int getItemViewType(int position)
    {
        final int size = mDataList.size();

        //没有数据
        if (size == 0) {
            return TYPE_NO_DATA;
        }

        if (mLayoutManager instanceof LinearLayoutManager) {
            return hasFooterView() && position == size ? TYPE_FOOTER : TYPE_ORDINARY;
        }
        return hasFooterView() && position == size ? TYPE_FOOTER : mDataList.listIterator(position).next().itemType.itemType;
    }


}
