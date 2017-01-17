package com.xinyi.czsuperadapter.main;

import android.graphics.Color;

import com.xinyi.czsuperadapter.AVLoadingIndicatorView;
import com.xinyi.czsuperadapter.R;
import com.xinyi.czsuperadapter.interfaces.IBindTypeMaker;
import com.xinyi.czsuperadapter.interfaces.RefreshListener;
import com.xinyi.czsuperadapter.type_maker.RefreshTypeMaker;

/**
 * Created by 陈章 on 2016/12/22 0022.
 * func:
 * 控制刷新View的回调监听
 */

public class RefreshController implements IBindTypeMaker {
    private RefreshTypeMaker refreshTypeMaker;
    private RefreshListener mRefreshListener;
    private Builder builder;
    private int refreshIndicatorStyle;
    private int refreshIndicatorColor;
    private int pullArrowImage;



    private RefreshController(Builder builder) {
        this.builder = builder;
        this.mRefreshListener = builder.mRefreshListener;
        this.refreshIndicatorStyle = builder.refreshIndicatorStyle;
        this.refreshIndicatorColor = builder.refreshIndicatorColor;
        this.pullArrowImage = builder.pullArrowImage;
    }


    public void finishRefresh() {
        if (refreshTypeMaker != null) {
            refreshTypeMaker.finishRefresh();
        }
    }


    @Override
    public void bindTypeMaker(MultiTypeMaker typeMaker) {
        this.refreshTypeMaker = (RefreshTypeMaker) typeMaker;
        if (refreshTypeMaker != null) {
            refreshTypeMaker.setOnRefreshListener(mRefreshListener);
            refreshTypeMaker.setRefreshIndicatorStyle(refreshIndicatorStyle);
            refreshTypeMaker.setRefreshIndicatorColor(refreshIndicatorColor);
            refreshTypeMaker.setPullArrowImage(pullArrowImage);
        }
    }

    //定制刷新的一些样式等
    public static class Builder {
        RefreshListener mRefreshListener;
        int refreshIndicatorStyle;
        int refreshIndicatorColor;
        int pullArrowImage;

        public Builder() {
            refreshIndicatorStyle = AVLoadingIndicatorView.BallPulse;
            refreshIndicatorColor = Color.BLACK;
            pullArrowImage = R.drawable.down_arrows_footer;
        }

        /**
         * 刷新监听者
         *
         * @param mRefreshListener
         * @return
         */
        public Builder setOnRefreshListener(RefreshListener mRefreshListener) {
            this.mRefreshListener = mRefreshListener;
            return this;
        }

        /**
         * 刷新进度条的样式请参照 AVLoadingIndicatorView
         *
         * @param refreshIndicatorStyle
         * @return
         */
        public Builder setRefreshIndicatorStyle(int refreshIndicatorStyle) {
            this.refreshIndicatorStyle = refreshIndicatorStyle;
            return this;
        }

        public Builder setRefreshIndicatorColor(int refreshIndicatorColor) {
            this.refreshIndicatorColor = refreshIndicatorColor;
            return this;
        }

        public Builder setPullArrowImage(int pullArrowImage) {
            this.pullArrowImage = pullArrowImage;
            return this;
        }


        public Builder build() {
            return this;
        }

        public RefreshController create() {
            return new RefreshController(this);
        }

    }
}
