package com.xinyi.czsuperadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xinyi.czsuperadapter.interfaces.ICRUD;

/**
 * Created by 陈章 on 2016/12/20 0020.
 * func:
 * 只负责（正数据）的增删改查操作
 */

public abstract class ICRUDAdapter<T> extends RecyclerView.Adapter implements ICRUD<T> {
    protected Context mContext;
    protected List<T> mNormalData;
    protected boolean autoNotify = true;

    public ICRUDAdapter(Context mContext) {
        this.mContext = mContext;
        init();
    }

    public void init(){
        autoNotify = true;
        if(mNormalData == null){
            mNormalData = new ArrayList<>();
        }
    }

    @Override
    public void add(T t) {
        mNormalData.add(t);
        myNotify();
    }

    @Override
    public void remove(Object o) {
        mNormalData.remove(o);
        myNotify();
    }

    @Override
    public void addAll(Collection<? extends T> collection) {
        mNormalData.addAll(collection);
        myNotify();
    }

    @Override
    public void addAll(int i, Collection<? extends T> collection) {
        mNormalData.addAll(i,collection);
        myNotify();
    }

    @Override
    public void removeAll(Collection<? extends T> collection) {
        mNormalData.removeAll(collection);
        myNotify();
    }

    @Override
    public void clear() {
        mNormalData.clear();
        myNotify();
    }

    @Override
    public void set(int i, T t) {
        mNormalData.set(i,t);
        myNotify();
    }

    @Override
    public void add(int  i, T t) {
        mNormalData.add(i,t);
        myNotify();
    }

    @Override
    public void remove(int i) {
        mNormalData.remove(i);
        myNotify();
    }

    @Override
    public void replaceAll(Collection<? extends T> collection) {
        mNormalData = (List<T>) collection;
        myNotify();
    }

    @Override
    public int size() {
        return mNormalData.size();
    }

    public void setAutoNotify(boolean autoNotify) {
        this.autoNotify = autoNotify;
    }

    protected void myNotify(){
        if(autoNotify){
            notifyDataSetChanged();
        }
    }

    protected void myNotifyItem(int position){
        if(autoNotify){
            notifyItemInserted(position);
        }
    }

}
