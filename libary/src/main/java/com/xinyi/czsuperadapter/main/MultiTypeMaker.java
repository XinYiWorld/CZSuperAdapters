package com.xinyi.czsuperadapter.main;

import android.support.v7.widget.RecyclerView;

import com.xinyi.czsuperadapter.interfaces.IBindRecyclerView;
import com.xinyi.czsuperadapter.interfaces.IMemoryPosition;

/**
 * Created by 陈章 on 2016/12/20 0020.
 * func:
 */

public abstract  class MultiTypeMaker<T> implements IBindRecyclerView ,IMemoryPosition{
    public static final int TYPE_REFRESH = 1001;            //下拉刷新
    public static final int  TYPE_HEADER = 1002;            //头布局
    public static final int  TYPE_FOOTER = 1003;            //脚布局
    public static final int  TYPE_LOADER = 1004;            //加载更多
    public static final int  TYPE_NORMAL = 1005;            //基本类型
    public static final int  TYPE_UNKNOWN = 1006;            //未知类型

    protected  static RecyclerView recyclerView;
    protected  LockObserver lockObserver;
    private int type;
    private T data;


    //记忆位置
    private int previousSelectPosition = -1;


    public abstract int getType(int position);
    public abstract int getLayoutId(int viewType);
    public abstract void bindViewHolder(CommonViewHolder holder, T data, int viewType, int position);
    public void bindLockObserver(LockObserver lockObserver){
        this.lockObserver = lockObserver;
    }
    public void init(){};
    @Override
    public void bindRecyclerView(RecyclerView recyclerView) {

    }

    public MultiTypeMaker() {
        init();
    }

    public MultiTypeMaker(RecyclerView recyclerView) {
        this();
        bindRecyclerView(recyclerView);
    }

    //判断是否是基本类型
    public static boolean isNormalType(int viewType){
        switch (viewType){
            case MultiTypeMaker.TYPE_REFRESH:
            case MultiTypeMaker.TYPE_LOADER:
                return false;
            default:
                return  true;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public int getPreviousSelectPosition() {
        return previousSelectPosition;
    }

    @Override
    public void setPreviousSelectPosition(int previousSelectPosition) {
        this.previousSelectPosition = previousSelectPosition;
    }

    @Override
    public void selectPosition(final int position){

    }
}
