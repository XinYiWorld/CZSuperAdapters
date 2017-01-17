package com.xinyi.czsuperadapter.interfaces;

/**
 * Created by 陈章 on 2016/12/22 0022.
 * func:    //正在加载中监听回调
 */

public interface RefreshListener {
    void onRefresh();                       //正在刷新中
    void onPull(int distance);              //滑动的距离
}
