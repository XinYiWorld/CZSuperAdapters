package com.xinyi.czsuperadapter.main;

import android.graphics.Color;

import com.xinyi.czsuperadapter.AVLoadingIndicatorView;
import com.xinyi.czsuperadapter.interfaces.IBindTypeMaker;
import com.xinyi.czsuperadapter.interfaces.LoadMode;
import com.xinyi.czsuperadapter.interfaces.LoaderListener;
import com.xinyi.czsuperadapter.type_maker.LoadTypeMaker;

/**
 * Created by 陈章 on 2016/12/22 0022.
 * func:
 * 控制加载更多View的回调监听
 */

public class LoadController implements IBindTypeMaker{
    private LoadTypeMaker loadTypeMaker;
    private LoaderListener mLoadListener;
    private LoadMode mLoadMode;
    private Builder builder;
    private int loadIndicatorStyle;
    private int loadIndicatorColor;


    private  LoadController(Builder builder) {
        this.builder = builder;
        this.mLoadListener = builder.mLoaderListener;
        this.mLoadMode = builder.mLoadMode;
        this.loadIndicatorStyle = builder.loadIndicatorStyle;
        this.loadIndicatorColor = builder.loadIndicatorColor;
    }


    @Override
    public void bindTypeMaker(MultiTypeMaker typeMaker) {
        this.loadTypeMaker = (LoadTypeMaker) typeMaker;
        if(loadTypeMaker != null) {
            loadTypeMaker.setOnLoaderListener(mLoadListener);
            loadTypeMaker.setLoadMode(mLoadMode);
            loadTypeMaker.setLoadIndicatorStyle(loadIndicatorStyle);
            loadTypeMaker.setLoadIndicatorColor(loadIndicatorColor);
        }
    }


    public void finishLoadMore(boolean hasMore) {
        if(loadTypeMaker != null){
            loadTypeMaker.finishLoadMore(hasMore);
        }
    }


    //定制加载更多的一些样式等
    public static class Builder{
        LoaderListener mLoaderListener;
        int loadIndicatorStyle;
        int loadIndicatorColor;

        LoadMode mLoadMode;

        public Builder() {
             loadIndicatorStyle = AVLoadingIndicatorView.BallPulse;
             mLoadMode = LoadMode.SCROLL_BOTTOM_TO_LOAD;
             loadIndicatorColor = Color.BLACK;
        }

        /**
         * 加载更多监听者
         * @param mLoaderListener
         * @return
         */
        public Builder setOnLoaderListener(LoaderListener mLoaderListener){
            this.mLoaderListener = mLoaderListener;
            return this;
        }

        /**
         * 刷新进度条的样式请参照 AVLoadingIndicatorView
         * @param loadIndicatorStyle
         * @return
         */
        public Builder setLoadIndicatorStyle(int loadIndicatorStyle){
            this.loadIndicatorStyle = loadIndicatorStyle;
            return this;
        }

        public  Builder setLoadIndicatorColor(int loadIndicatorColor){
            this.loadIndicatorColor = loadIndicatorColor;
            return this;
        }



        public Builder setLoadMode( LoadMode mLoadMode){
            this.mLoadMode  = mLoadMode;
            return this;
        }

        public Builder build(){
            return this;
        }

        public LoadController create(){
            return new LoadController(this);
        }

    }
}
