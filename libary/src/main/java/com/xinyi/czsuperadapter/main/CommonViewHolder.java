package com.xinyi.czsuperadapter.main;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 陈章 on 2016/11/30 0030.
 * func:
 */

public class CommonViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private MultiTypeMaker multiTypeMaker;

    public CommonViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }


    public <T extends View> T findViewById(int viewId){
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public MultiTypeMaker getMultiTypeMaker() {
        return multiTypeMaker;
    }

    public void setMultiTypeMaker(MultiTypeMaker multiTypeMaker) {
        this.multiTypeMaker = multiTypeMaker;
    }
}
