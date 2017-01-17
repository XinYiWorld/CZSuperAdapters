package com.xinyi.czsuperadapter.interfaces;

/**
 * Created by 陈章 on 2016/12/22 0022.
 * func:  加载更多状态控制
 */

public enum LoadingState {
    PREPARE_TO_LOAD,            //点击加载更多
    LOADING,                    //正在加载中
    NO_MORE_DATA                //没有更多数据
}
