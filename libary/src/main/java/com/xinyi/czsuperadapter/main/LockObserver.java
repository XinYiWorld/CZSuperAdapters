package com.xinyi.czsuperadapter.main;

import com.xinyi.czsuperadapter.type_maker.LoadTypeMaker;
import com.xinyi.czsuperadapter.type_maker.RefreshTypeMaker;

/**
 * Created by 陈章 on 2016/12/22 0022.
 * func:
 * 保证刷新时无法加载更多，加载更多时无法刷新。
 */
public class LockObserver {
    private  boolean isRefreshing,isLoadingMore;
    private RefreshTypeMaker refreshTypeMaker;
    private LoadTypeMaker loadTypeMaker;


    public LockObserver() {
        isRefreshing = false;
        isLoadingMore = false;
    }

    public void setLoadTypeMaker(MultiTypeMaker loadTypeMaker) {
        this.loadTypeMaker = (LoadTypeMaker) loadTypeMaker;
    }

    public void setRefreshTypeMaker(MultiTypeMaker refreshTypeMaker) {
        this.refreshTypeMaker = (RefreshTypeMaker) refreshTypeMaker;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }
}
