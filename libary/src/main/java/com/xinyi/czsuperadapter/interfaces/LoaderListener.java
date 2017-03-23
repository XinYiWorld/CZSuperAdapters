package com.xinyi.czsuperadapter.interfaces;

/**
 * Created by 陈章 on 2016/12/22 0022.
 * func:加载更多监听回调
 */

public interface LoaderListener {
    void onLoadMore();      //正在加载中

    /**
     * 可以控制与其它的一些滑动控制的冲突，如SwipeRefreshLayout.
     * @param detX
     * @param detY  向上滑动为正，向下滑动为负。
     */
    void onScroll(int detX,int detY) ;       //获取滚动的方向及距离
}
