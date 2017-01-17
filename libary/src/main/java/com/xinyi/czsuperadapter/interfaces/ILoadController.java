package com.xinyi.czsuperadapter.interfaces;

/**
 * Created by 陈章 on 2016/12/22 0022.
 * func:
 */

public interface ILoadController {
    void finishLoadMore(boolean hasMore);
    void setOnLoaderListener(LoaderListener mLoaderListener);
    void setLoadMode(LoadMode mLoadMode);
}
