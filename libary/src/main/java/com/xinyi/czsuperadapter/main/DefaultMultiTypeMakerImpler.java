package com.xinyi.czsuperadapter.main;

/**
 * Created by 陈章 on 2016/12/20 0020.
 * func:MultiTypeMaker的实现
 */

public   class DefaultMultiTypeMakerImpler extends MultiTypeMaker<Object>{

    @Override
    public int getType(int position) {
        return 0;
    }

    @Override
    public int getLayoutId(int viewType) {
        return 0;
    }

    @Override
    public void bindViewHolder(CommonViewHolder holder, Object data, int viewType, int position) {

    }
}