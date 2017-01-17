package com.xinyi.czsuperadapter.main;

import android.support.v7.widget.RecyclerView;

import com.xinyi.czsuperadapter.type_maker.LoadTypeMaker;
import com.xinyi.czsuperadapter.type_maker.RefreshTypeMaker;

/**
 * Created by 陈章 on 2016/12/20 0020.
 * func:
 * 专门用于生成刷新、加载更多、头、脚布局以及其逻辑控制。
 */

public class FuncMultiTypeMakerFactory {
    public static MultiTypeMaker create(int type, RecyclerView recyclerView){
        MultiTypeMaker typeMaker = null;
        switch (type){
            case MultiTypeMaker.TYPE_REFRESH:
                typeMaker = new RefreshTypeMaker(recyclerView);
                break;

            case MultiTypeMaker.TYPE_LOADER:
                typeMaker = new LoadTypeMaker(recyclerView);
                break;
        }
        return typeMaker;
    }

}
