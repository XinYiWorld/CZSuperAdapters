package com.xinyi.czsuperadapter.interfaces;

import com.xinyi.czsuperadapter.main.LoadController;
import com.xinyi.czsuperadapter.main.MultiTypeMaker;
import com.xinyi.czsuperadapter.main.RefreshController;

/**
 * Created by 陈章 on 2016/12/28 0028.
 * func:添加头部
 */

public interface IAddTypeMaker {
    void addHeader(MultiTypeMaker header);
    void addFooter(MultiTypeMaker footer);
    void setRefreshController(RefreshController refreshController);
    void setLoadController(LoadController loadController);
}
