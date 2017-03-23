package com.xinyi.czsuperadapter.type_maker;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.xinyi.czsuperadapter.AVLoadingIndicatorView;
import com.xinyi.czsuperadapter.R;
import com.xinyi.czsuperadapter.interfaces.ILoad;
import com.xinyi.czsuperadapter.interfaces.ILoadController;
import com.xinyi.czsuperadapter.interfaces.LoadMode;
import com.xinyi.czsuperadapter.interfaces.LoaderListener;
import com.xinyi.czsuperadapter.interfaces.LoadingState;
import com.xinyi.czsuperadapter.main.CommonViewHolder;
import com.xinyi.czsuperadapter.main.MultiTypeMaker;

import static com.xinyi.czsuperadapter.interfaces.LoadingState.PREPARE_TO_LOAD;


/**
 * Created by 陈章 on 2016/12/20 0020.
 * func:
 * 负责分担加载view生成和逻辑
 */

public class LoadTypeMaker extends MultiTypeMaker implements ILoad, ILoadController {
    private static final String TAG = "LoadTypeMaker";
    protected LoadingState mLoadingState;
    protected LoaderListener mLoaderListener;
    protected LoadMode mLoadMode;
    protected boolean isLoadingMore;
    private LoadViewHolder loadViewHolder;
    private RecyclerView.OnScrollListener scrollListener;
    private int scrollDetY;
    private int loadIndicatorStyle;
    private int loadIndicatorColor;



    public LoadTypeMaker(RecyclerView recyclerView) {
        super(recyclerView);
        init();
    }


    @Override
    public void init() {
        mLoadingState = PREPARE_TO_LOAD;
        mLoadMode = LoadMode.CLICK_TO_LOAD;
        isLoadingMore = false;
        loadViewHolder = new LoadViewHolder();
    }

    @Override
    public int getType(int position) {
        return TYPE_LOADER;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.layout_loader_view;
    }

    @Override
    public void bindViewHolder(CommonViewHolder holder, Object data, int viewType, int position) {
        Log.i(TAG, "bindViewHolder: load");
        loadViewHolder.tv_prepare_to_load_more = holder.findViewById(R.id.tv_prepare_to_load_more);
        loadViewHolder.ll_loading = holder.findViewById(R.id.ll_loading);
        loadViewHolder.tv_no_more = holder.findViewById(R.id.tv_no_more);
        loadViewHolder.av_loading_view  = holder.findViewById(R.id.av_loading_view);
        loadViewHolder.av_loading_view.setmIndicatorId(loadIndicatorStyle);
        loadViewHolder.av_loading_view.setmIndicatorColor(loadIndicatorColor);
        loadViewHolder.tv_prepare_to_load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: loadmore ");
                if(lockObserver != null){
                    if(!lockObserver.isRefreshing()){
                        startLoadMore();
                    }
                }
            }
        });


        setLoadingState(mLoadingState);
    }


    @Override
    public void bindRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        if (scrollListener == null) {
            scrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    scrollDetY = dy;
                    if (mLoaderListener != null) {
                        mLoaderListener.onScroll(dx,dy);
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.i(TAG, "onScrollStateChanged: ");
                    if(lockObserver.isRefreshing()) return;
                    if (getLoadMode() == LoadMode.CLICK_TO_LOAD) return;

                    //判断是否最后一item个显示出来
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                    //可见的item个数
                    int visibleChildCount = layoutManager.getChildCount();
                    if (visibleChildCount > 0 && (newState == RecyclerView.SCROLL_STATE_IDLE)) {
                        View lastVisibleView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                        int lastVisiblePosition = recyclerView.getChildLayoutPosition(lastVisibleView);
                        Log.i(TAG, "onScrollStateChanged: isLoadingMore = " + isLoadingMore());
                        if (lastVisiblePosition >= layoutManager.getItemCount() - 1 && !isLoadingMore() && mLoadingState != LoadingState.NO_MORE_DATA && scrollDetY > 0) {                  //滑动到底部
                            startLoadMore();
                            recyclerView.smoothScrollToPosition(layoutManager.getItemCount() - 1);
                        }
                    }
                }
            };
        }
        recyclerView.addOnScrollListener(scrollListener);
    }


    class LoadViewHolder {
        View tv_prepare_to_load_more;
        View ll_loading;
        View tv_no_more;
        AVLoadingIndicatorView av_loading_view;
    }

    @Override
    public void startLoadMore() {
        setLoadingState(LoadingState.LOADING);
    }

    @Override
    public void finishLoadMore(boolean hasMore) {
        if (hasMore) {
            setLoadingState(PREPARE_TO_LOAD);
        } else {
            setLoadingState(LoadingState.NO_MORE_DATA);
        }
    }

    public void setLoadingState(LoadingState mLoadingState) {
        this.mLoadingState = mLoadingState;
        loadViewHolder.tv_prepare_to_load_more.setVisibility(getLoadMode() == LoadMode.CLICK_TO_LOAD ? View.VISIBLE : View.GONE);

        if (PREPARE_TO_LOAD == mLoadingState) {
            setLoadingMore(false);
            if (getLoadMode() == LoadMode.CLICK_TO_LOAD) {                    //点击加载更多
                loadViewHolder.tv_prepare_to_load_more.setVisibility(View.VISIBLE);
                loadViewHolder.ll_loading.setVisibility(View.GONE);
                loadViewHolder.av_loading_view.setVisibility(View.GONE);
                loadViewHolder.tv_no_more.setVisibility(View.GONE);
            } else {
                loadViewHolder.ll_loading.setVisibility(View.GONE);
                loadViewHolder.av_loading_view.setVisibility(View.GONE);
            }
        } else if (LoadingState.LOADING == mLoadingState) {
            loadViewHolder.tv_prepare_to_load_more.setVisibility(View.GONE);
            loadViewHolder.ll_loading.setVisibility(View.VISIBLE);
            loadViewHolder.av_loading_view.setVisibility(View.VISIBLE);
            loadViewHolder.tv_no_more.setVisibility(View.GONE);
            setLoadingMore(true);
            if (mLoaderListener != null) {
                mLoaderListener.onLoadMore();
            }
        } else if (LoadingState.NO_MORE_DATA == mLoadingState) {
            setLoadingMore(false);
            loadViewHolder.tv_prepare_to_load_more.setVisibility(View.GONE);
            loadViewHolder.ll_loading.setVisibility(View.GONE);
            loadViewHolder.av_loading_view.setVisibility(View.GONE);
            loadViewHolder.tv_no_more.setVisibility(View.VISIBLE);
        }
    }

    public LoadingState getLoadingState() {
        return mLoadingState;
    }

    public void setOnLoaderListener(LoaderListener mLoaderListener) {
        this.mLoaderListener = mLoaderListener;
    }


    public void setLoadMode(LoadMode mLoadMode) {
        this.mLoadMode = mLoadMode;
    }

    public LoadMode getLoadMode() {
        return mLoadMode;
    }

    //判断是否正在加载更多（用于滚动到底部加载更多的模式，避免重复加载。）
    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
        if(lockObserver != null) lockObserver.setLoadingMore(loadingMore);
    }


    public void setLoadIndicatorStyle(int loadIndicatorStyle){
        this.loadIndicatorStyle = loadIndicatorStyle;
    }

    public void setLoadIndicatorColor(int loadIndicatorColor){
        this.loadIndicatorColor = loadIndicatorColor;
    }

}
