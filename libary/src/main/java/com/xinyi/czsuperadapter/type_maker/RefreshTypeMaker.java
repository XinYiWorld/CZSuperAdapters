package com.xinyi.czsuperadapter.type_maker;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import com.xinyi.czsuperadapter.AVLoadingIndicatorView;
import com.xinyi.czsuperadapter.R;
import com.xinyi.czsuperadapter.interfaces.IRefresh;
import com.xinyi.czsuperadapter.interfaces.RefreshListener;
import com.xinyi.czsuperadapter.main.CommonViewHolder;
import com.xinyi.czsuperadapter.main.MultiTypeMaker;

/**
 * Created by 陈章 on 2016/12/20 0020.
 * func:
 * 负责分担刷新view生成和逻辑
 */

public class RefreshTypeMaker extends MultiTypeMaker implements IRefresh {
    private static final String TAG = "RefreshTypeMaker";
    private RefreshingState mRefreshingState;
    private RefreshingStateNode mRefreshingStateNode;
    private RefreshListener mRefreshListener;
    private int refreshIndicatorStyle;
    private int refreshIndicatorColor;
    private int pullArrowImage;

    private  boolean isRefreshing;

    private RefreshViewHolder refreshViewHolder;
    private boolean hasMeasuredRefreshViewHeight;
    private int originalRefreshViewHeight;
    private ValueAnimator animatorScrollToTop;
    private double maxPaddingTop;
    private int standardScrollToTopTime;
    private View.OnTouchListener touchListener;

    public RefreshTypeMaker(RecyclerView recyclerView) {
        super(recyclerView);
        refreshViewHolder = new RefreshViewHolder();
        hasMeasuredRefreshViewHeight = false;
        standardScrollToTopTime = 500;
        isRefreshing = false;
    }

    @Override
    public int getType(int position) {
       return TYPE_REFRESH;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.layout_refresh_view;
    }

    @Override
    public void bindViewHolder(CommonViewHolder holder, Object data, int viewType, int position) {
        Log.i(TAG, "bindViewHolder: refresh");
        refreshViewHolder.ll_root = holder.findViewById(R.id.ll_root);
        refreshViewHolder.ll_pull_or_release_to_refresh_wrapper = holder.findViewById(R.id.ll_pull_or_release_to_refresh_wrapper);
        refreshViewHolder.tv_pull_or_release_to_refresh = holder.findViewById(R.id.tv_pull_or_release_to_refresh);
        refreshViewHolder.ll_refreshing_wrapper = holder.findViewById(R.id.ll_refreshing_wrapper);
        refreshViewHolder.iv_arrow = holder.findViewById(R.id.iv_arrow);
        refreshViewHolder.av_loading_view = holder.findViewById(R.id.av_loading_view);
        refreshViewHolder.av_loading_view.setmIndicatorId(refreshIndicatorStyle);
        refreshViewHolder.av_loading_view.setmIndicatorColor(refreshIndicatorColor);
        refreshViewHolder.iv_arrow.setImageResource(pullArrowImage);
        setRefreshingState(mRefreshingStateNode);
    }

    @Override
    public void init() {
        mRefreshingState = RefreshingState.NONE;
        mRefreshingStateNode = new  RefreshingStateNode(RefreshingState.UN_KNOWN, RefreshingState.NONE);
        isRefreshing = false;
    }

    @Override
    public void bindRecyclerView(final RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        if(touchListener == null ){
            touchListener = new View.OnTouchListener() {
                private float startY;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (isRefreshing()) return false;
                    if(lockObserver.isLoadingMore()) return false;

                    int action = motionEvent.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        startY = motionEvent.getY();                    //注意是点击的点getY
                        Log.i(TAG, "onTouch: startY = " + startY);
                        setRefreshingState(mRefreshingStateNode.set(RefreshingState.NONE, RefreshingState.PULL_TO_REFRESH));
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        float currentY = motionEvent.getY();
                        double detY = currentY - startY;
                        if(detY < 0 && refreshViewHolder.ll_root.getPaddingTop()<=-originalRefreshViewHeight){  //如果一开始就向上滑动，默认是加载更多。（*****************************************）
                            Log.i(TAG, "onTouch: 加载更多");
                            startY = currentY;  //注意起点的传递
                            return false;
                        }

                        if(detY > 0){
                            //不是最上面向下滑动，不触发刷新事件。（*****************************************）
                            View firstVisibleView = recyclerView.getChildAt(0);
                            int firstVisiblePosition = recyclerView.getChildLayoutPosition(firstVisibleView);
                            if(firstVisiblePosition > 1){
                                startY = currentY;  //注意起点的传递
                                return false;
                            }
                        }

                        Log.i(TAG, "onTouch: currentY = " + currentY);
                        if (refreshViewHolder.ll_root.getPaddingTop() > maxPaddingTop && detY > 0) {
                            //下滑到一定的距离不让下滑
                            startY = currentY;  //注意起点的传递
                            return false;
                        }
                        refreshViewHolder.ll_root.setPadding(0, (int) (refreshViewHolder.ll_root.getPaddingTop() + currentY - startY), 0, 0);


                        //刷新view全部出现，切换状态。
                        if (refreshViewHolder.ll_root.getPaddingTop() > 0) {
                            if (mRefreshingState == RefreshingState.PULL_TO_REFRESH) {            //防止箭头反复旋转
                                setRefreshingState(mRefreshingStateNode.set(RefreshingState.PULL_TO_REFRESH, RefreshingState.RELEASE_TO_REFRESH));
                            }
                        } else {
                            if (mRefreshingState == RefreshingState.RELEASE_TO_REFRESH) {         //防止箭头反复旋转
                                setRefreshingState(mRefreshingStateNode.set(RefreshingState.RELEASE_TO_REFRESH, RefreshingState.PULL_TO_REFRESH));
                            }else if(mRefreshingState == RefreshingState.NONE){     //一定要加这个从NONE到PULL_TO_REFRESH刷新的状态转换
                                setRefreshingState(mRefreshingStateNode.set(RefreshingState.NONE, RefreshingState.PULL_TO_REFRESH));
                            }
                        }
                        startY = currentY;  //注意起点的传递
                    } else if (action == MotionEvent.ACTION_UP) {                                 //释放时可能存在多种状态
                        if (mRefreshingState == RefreshingState.RELEASE_TO_REFRESH) {            //刷新
                            setRefreshingState(mRefreshingStateNode.set(RefreshingState.RELEASE_TO_REFRESH, RefreshingState.REFRESHING));
                            if (mRefreshListener != null) {
                                mRefreshListener.onRefresh();
                            }
                        } else {
                            float currentY = motionEvent.getY();
                            finishRefresh();
                            if(currentY - startY == 0 && !isRefreshing()) return false;        //只是点击没有拖动，不刷新。（解决与底部“点击加载更多”的冲突）（*****************************************）
                        }
                    }

                    if (mRefreshListener != null) {
                        int distance = refreshViewHolder.ll_root.getPaddingTop() + originalRefreshViewHeight;
                        Log.i(TAG, "onTouch: distance = " + distance);
                        mRefreshListener.onPull(distance);
                    }

                    return true;
                }
            };
        }
        recyclerView.setOnTouchListener(touchListener);
    }

    @Override
    public void finishRefresh() {
        setRefreshingState(mRefreshingStateNode.set(RefreshingState.UN_KNOWN, RefreshingState.NONE));
        refreshViewHolder.iv_arrow.animate().rotation(0);
    }

    @Override
    public void setOnRefreshListener(RefreshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
    }

    public void setRefreshIndicatorStyle(int refreshIndicatorStyle){
        this.refreshIndicatorStyle = refreshIndicatorStyle;
    }


    public void setRefreshIndicatorColor(int refreshIndicatorColor){
        this.refreshIndicatorColor = refreshIndicatorColor;
    }

    public void setPullArrowImage(int pullArrowImage) {
        this.pullArrowImage = pullArrowImage;
    }


    private void measureRefreshViewHeight() {
        refreshViewHolder.ll_root.measure(0, 0);
        originalRefreshViewHeight = refreshViewHolder.ll_root.getMeasuredHeight();
        maxPaddingTop = originalRefreshViewHeight * 2.0;
        Log.i(TAG, "bindRefreshView: measuredHeight = " + originalRefreshViewHeight);
        hasMeasuredRefreshViewHeight = true;
    }


    private void smoothScrollToTop() {
        //松开圆滑的跳到刷新
        if(animatorScrollToTop == null){
            animatorScrollToTop = new ValueAnimator();
            animatorScrollToTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentPaddingTop = (int) animation.getAnimatedValue();
                    refreshViewHolder.ll_root.setPadding(0, currentPaddingTop, 0, 0);
                }
            });
            animatorScrollToTop.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    refreshViewHolder.ll_refreshing_wrapper.setVisibility(View.VISIBLE);
                    refreshViewHolder.av_loading_view.setVisibility(View.VISIBLE);
                    refreshViewHolder.ll_pull_or_release_to_refresh_wrapper.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animatorScrollToTop.setEvaluator(new IntEvaluator());
        }
        animatorScrollToTop.setIntValues(refreshViewHolder.ll_root.getPaddingTop(),0);
        animatorScrollToTop.setDuration((long) (standardScrollToTopTime * refreshViewHolder.ll_root.getPaddingTop() / maxPaddingTop));
        animatorScrollToTop.start();
    }


    class RefreshViewHolder {
        View ll_root;
        View ll_pull_or_release_to_refresh_wrapper;
        TextView tv_pull_or_release_to_refresh;
        View ll_refreshing_wrapper;
        ImageView iv_arrow;
        AVLoadingIndicatorView av_loading_view;
    }

    //下拉刷新状态控制
    public enum RefreshingState{
        NONE,                        //初始状态
        PULL_TO_REFRESH,             //下拉刷新
        REFRESHING,                  //正在刷新中
        RELEASE_TO_REFRESH,           //松开刷新
        UN_KNOWN                     //未知状态
    }

    //状态结点，记录当前状态之前的状态，便于处理
    class RefreshingStateNode{
        private RefreshingState previousState;
        private RefreshingState currentState;

        public RefreshingStateNode(RefreshingState previousState, RefreshingState currentState) {
            this.previousState = previousState;
            this.currentState = currentState;
        }

        public RefreshingStateNode set(RefreshingState previousState, RefreshingState currentState){
            this.previousState = previousState;
            this.currentState = currentState;
            return this;
        }


        public RefreshingState getCurrentState() {
            return currentState;
        }

        public void setCurrentState(RefreshingState currentState) {
            this.currentState = currentState;
        }

        public RefreshingState getPreviousState() {
            return previousState;
        }

        public void setPreviousState(RefreshingState previousState) {
            this.previousState = previousState;
        }
    }

    protected void setRefreshingState(RefreshingStateNode refreshingStateNode) {
        this.mRefreshingState = refreshingStateNode.getCurrentState();
        if (mRefreshingState == RefreshingState.NONE) {                  //初始状态
            setRefreshing(false);
            if (!hasMeasuredRefreshViewHeight) {          //只测量一次
                measureRefreshViewHeight();
            }
            refreshViewHolder.ll_pull_or_release_to_refresh_wrapper.setVisibility(View.VISIBLE);
            refreshViewHolder.ll_refreshing_wrapper.setVisibility(View.GONE);
            refreshViewHolder.av_loading_view.setVisibility(View.GONE);             //不设置可不可见，不会有动画效果。
            refreshViewHolder.ll_root.setPadding(0, -originalRefreshViewHeight, 0, 0);
        } else {
            if (mRefreshingState == RefreshingState.REFRESHING) {        //刷新中
                setRefreshing(true);
                smoothScrollToTop();        //从当前位置跳至顶部进行刷新
            } else {
                setRefreshing(false);
                refreshViewHolder.ll_pull_or_release_to_refresh_wrapper.setVisibility(View.VISIBLE);
                refreshViewHolder.ll_refreshing_wrapper.setVisibility(View.GONE);
                refreshViewHolder.av_loading_view.setVisibility(View.GONE);
                if (mRefreshingState == RefreshingState.PULL_TO_REFRESH) {                //下拉刷新
                    refreshViewHolder.tv_pull_or_release_to_refresh.setText("下拉刷新");
                    if (refreshingStateNode.getPreviousState() == RefreshingState.RELEASE_TO_REFRESH) {
                        refreshViewHolder.iv_arrow.animate().rotation(0);
                    }
                } else if (mRefreshingState == RefreshingState.RELEASE_TO_REFRESH) {       //松开刷新
                    refreshViewHolder.tv_pull_or_release_to_refresh.setText("松开刷新");
                    if (refreshingStateNode.getPreviousState() == RefreshingState.PULL_TO_REFRESH) {
                        refreshViewHolder.iv_arrow.animate().rotation(180);
                    }
                }
            }
        }
    }

    public RefreshingState getRefreshingState() {
        return mRefreshingState;
    }


    //判断是否正在下拉刷新（用于滚动到底部下拉刷新的模式，避免重复加载。）
    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setRefreshing(boolean isRefreshing) {
        this.isRefreshing = isRefreshing;
        lockObserver.setRefreshing(isRefreshing);
    }
}
