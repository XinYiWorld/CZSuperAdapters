package com.xinyi.czsuperadapter.interfaces;

import com.xinyi.czsuperadapter.main.LoadController;
import com.xinyi.czsuperadapter.main.MultiTypeMaker;
import com.xinyi.czsuperadapter.main.RefreshController;

/**
 * Created by 陈章 on 2016/12/28 0028.
 * func:添加头部
 */

public interface IGetTypeMaker {
    MultiTypeMaker getHeader(int position);
    MultiTypeMaker getFooter(int position);
    RefreshController getRefreshController();
    LoadController getLoadController();
}
