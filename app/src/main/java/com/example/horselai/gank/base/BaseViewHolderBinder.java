package com.example.horselai.gank.base;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by laixiaolong on 2017/4/10.
 * <p>
 * 根据需要实现相应的方法即可（如有疑问，请参考BaseMultipleTypeListAdapter注释）
 */

public abstract class BaseViewHolderBinder<T extends BeanEntry> implements View.OnClickListener, View.OnLongClickListener
{

    protected Context mContext;
    protected LayoutInflater mLayoutInflater;

    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;
    protected Handler mHandler;


    /**
     * 释放资源，在Fragment或Activity的onDestroy()或onStop()中调用
     */
    public abstract void release();

    /**
     * 配套的，如果希望item响应点击事件，应该在 onCreateViewHolder时为itemView绑定View.OnClickListener
     *
     * @param mOnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        this.mOnItemLongClickListener = listener;
    }

    public void setHandler(Handler handler)
    {
        this.mHandler = handler;
    }


    public interface OnItemClickListener
    {
        void onItemClicked(View v, int position);
    }

    public interface OnItemLongClickListener
    {
        void onItemLongClicked(View v, int position);
    }


    @Override public void onClick(View v)
    {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClicked(v, (Integer) v.getTag());
        }
    }

    @Override public boolean onLongClick(View view)
    {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClicked(view, (Integer) view.getTag());
        }
        return true;
    }

    public BaseViewHolderBinder(Context mContext)
    {
        this.mContext = mContext;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 导航栏
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected RecyclerView.ViewHolder onCreateCategoryBarViewHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateCategoryBarViewHolder has not been override ");
    }


    /**
     * 瀑布流方式;
     * <p>
     * 注意如一下方式创建及设置参数
     * <pre>
     * mTmpView = mLayoutInflater.inflate(R.layout.xxx, false);
     * mStaggeredLayoutParams = (StaggeredGridLayoutManager.LayoutParams) mTmpView.getLayoutParams();</br>
     * mStaggeredLayoutParams.setFullSpan(false);
     * mTmpView.setLayoutParams(mStaggeredLayoutParams);
     *  xxxVH xxVH = new xxxVH(mTmpView);
     * mHolderCache.put(viewType, listVH);
     * return xxVH;
     * </pre>
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected RecyclerView.ViewHolder onCreateStaggeredViewHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateStaggeredViewHolder has not been override ");
    }


    protected RecyclerView.ViewHolder onCreateLinearViewHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateLinearViewHolder has not been override ");
    }


    /**
     * 网格显示;
     * <p>
     * 注意如一下方式创建及设置参数
     * <pre>
     * mTmpView = mLayoutInflater.inflate(R.layout.xxx, parent, false);
     * mStaggeredLayoutParams = (StaggeredGridLayoutManager.LayoutParams) mTmpView.getLayoutParams();</br>
     * mStaggeredLayoutParams.setFullSpan(false);
     * mTmpView.setLayoutParams(mStaggeredLayoutParams);
     *  xxxVH xxVH = new xxxVH(mTmpView);
     * mHolderCache.put(viewType, listVH);
     * return xxVH;
     * </pre>
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected RecyclerView.ViewHolder onCreateGridViewHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateGridViewHolder has not been override ");
    }

    /**
     * 注意如一下方式创建及设置参数
     * <pre>
     * mTmpView = mLayoutInflater.inflate(R.layout.xxx, parent, false);
     * mStaggeredLayoutParams = (StaggeredGridLayoutManager.LayoutParams) mTmpView.getLayoutParams();
     * mStaggeredLayoutParams.setFullSpan(true);
     * mTmpView.setLayoutParams(mStaggeredLayoutParams);
     *  xxxVH xxVH = new xxxVH(mTmpView);
     * mHolderCache.put(viewType, listVH);
     * return xxVH;
     * </pre>
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected RecyclerView.ViewHolder onCreateOtherViewHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateOtherViewHolder has not been override ");
    }

    /**
     * 如果你使用到了"ItemType.TYPE_LIST"类型，那么需要在这里设置rvList的参数，包括Adapter， LayoutManager
     *
     * @param rvList
     * @param parent
     * @param viewType
     * @return
     */
    protected RecyclerView.ViewHolder onCreateListItemViewHolder(RecyclerView rvList, ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateListItemViewHolder has not been override ");
    }


    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateHeaderViewHolder has not been override ");
    }


    protected RecyclerView.ViewHolder onCreateSliderRotationViewHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateSliderRotationViewHolder has not been override ");
    }


    /**
     * <pre>
     *
     *   mTmpView = mLayoutInflater.inflate(R.layout.xxx, parent, false);
     * mStaggeredLayoutParams = (StaggeredGridLayoutManager.LayoutParams) mTmpView.getLayoutParams();
     * mStaggeredLayoutParams.setFullSpan(true);
     * mTmpView.setLayoutParams(mStaggeredLayoutParams);
     * xxxVH xxVH = new xxxVH(mTmpView);
     * mHolderCache.put(viewType, listVH);
     * return xxVH;
     * </pre>
     *
     * @param parent
     * @param viewType
     * @return
     */
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateFooterViewHolder has not been override ");
    }


    /**
     * 当且仅当使用LinearLayoutManager或GridLayoutManager情况下实现该方法
     *
     * @param parent
     * @param viewType
     * @return
     */
    public RecyclerView.ViewHolder onCreateOrdinaryVieHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateOrdinaryVieHolder has not been override ");
    }

    public RecyclerView.ViewHolder onCreateNoDataViewHolder(ViewGroup parent, int viewType)
    {
        throw new IllegalStateException("onCreateNoDataViewHolder has not been override ");
    }

    protected void onBindListViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        bindTag(holder, position);
    }

    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        bindTag(holder, position);
    }

    protected void onBindSliderRotationViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        bindTag(holder, position);
    }

    protected void onBindOtherViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        bindTag(holder, position);
    }

    private void bindTag(RecyclerView.ViewHolder holder, int position)
    {
        if (holder == null) return;
        holder.itemView.setTag(position);
    }

    protected void onBindCategoryBarViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        bindTag(holder, position);
    }


    protected void onBindStaggeredViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        bindTag(holder, position);
    }


    protected void onBindGridViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        bindTag(holder, position);
    }


    protected void onBindLinearViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        bindTag(holder, position);
    }


    public void onBindOrdinaryViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        bindTag(holder, position);
    }

}
