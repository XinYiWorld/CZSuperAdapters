package com.xinyi.czsuperadapter.interfaces;

/**
 * Created by 陈章 on 2016/12/19 0019.
 * func:
 * 统一标准
 */
public interface ILoad {
    void startLoadMore();
    boolean isLoadingMore();
    void finishLoadMore(boolean hasMore);
    void setOnLoaderListener(LoaderListener mLoaderListener);
}
