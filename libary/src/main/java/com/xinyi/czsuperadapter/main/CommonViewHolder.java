package com.xinyi.czsuperadapter.main;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 陈章 on 2016/11/30 0030.
 * func:
 */

public class CommonViewHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> mViews;
    private MultiTypeMaker multiTypeMaker;
    private ViewGroup parent;


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

    public ViewGroup getParent() {
        return parent;
    }

    public void setParent(ViewGroup parent) {
        this.parent = parent;
    }


    /**
     * set the on item click listener
     * 设置Item的点击事件
     *
     * @param listener listener
     * @param position position
     */
    public void setOnItemClickListener(final  OnItemClickListener listener, final int position) {
        if (listener == null) {
            this.itemView.setOnClickListener(null);
        } else {
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(v, position);
                }
            });
        }
    }


    /**
     * set the on item long click listener
     * 设置Item的长点击事件
     *
     * @param listener listener
     * @param position position
     */
    public void setOnItemLongClickListener(final  OnItemLongClickListener listener, final int position) {
        if (listener == null) {
            this.itemView.setOnLongClickListener(null);
        } else {
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    return listener.onItemLongClick(v, position);
                }
            });
        }
    }


    /**
     * the click listeners callback
     * 点击事件回调
     */
    public interface OnItemClickListener {
        /**
         * on item click call back
         *
         * @param convertView convertView
         * @param position position
         */
        void onItemClick(View convertView, int position);
    }

    /**
     * the long click listeners callback
     * 长点击事件回调
     */
    public interface OnItemLongClickListener {
        /**
         * on item long click call back
         *
         * @param convertView convertView
         * @param position position
         * @return true false
         */
        boolean onItemLongClick(View convertView, int position);
    }
}
