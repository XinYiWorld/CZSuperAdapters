package com.xinyi.czsuperadapter.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xinyi.czsuperadapter.ICRUDAdapter;
import com.xinyi.czsuperadapter.interfaces.IAddTypeMaker;
import com.xinyi.czsuperadapter.interfaces.IRemoveTypeMaker;

/**
 * Created by 陈章 on 2016/12/20 0020.
 * func:
 * 对刷新、加载更多、头布局、脚布局、多种样式的融合。
 * 1)需要注意的问题：刷新与加载更多两个事件是冲突的 touch 和scroll事件，而且加载更多的时候不能刷新，刷新的时候不能加载更多。
 */

public class CZSuperAdapter<T> extends ICRUDAdapter<T> implements IAddTypeMaker,IRemoveTypeMaker {
    private static final String TAG = "CZSuperAdapter";
    private RecyclerView recyclerView;
    private MultiTypeMaker mNormalTypeMaker;
    private final LockObserver lockObserver;
    private final TypeManager typeManager;

    public CZSuperAdapter(Context mContext, RecyclerView recyclerView, MultiTypeMaker mNormalTypeMaker) {
        super(mContext);
        this.recyclerView = recyclerView;
        this.mNormalTypeMaker = mNormalTypeMaker;
        mNormalTypeMaker.setType(MultiTypeMaker.TYPE_NORMAL);
        lockObserver = new LockObserver();                  //解决刷新与加载更多冲突
        typeManager = new TypeManager();                    //管理视图
    }

    @Override
    public int getItemCount() {
        return mNormalData.size() + typeManager.getRefreshControllerCount() + typeManager.getLoadControllerCount() + typeManager.getHeaderCount() + typeManager.getFooterCount();
    }

    @Override
    public int getItemViewType(int position) {
        Log.i(TAG, "getItemViewType: itemCount = " + getItemCount());
        if (position == 0) {
            if (typeManager.getRefreshControllerCount() == 1) {
                return MultiTypeMaker.TYPE_REFRESH;
            } else {
                return position;        //无刷新
            }
        } else if (position == getItemCount() - 1) {
            if (typeManager.getLoadControllerCount() == 1) {
                return MultiTypeMaker.TYPE_LOADER;
            } else {
                return position;        //无加载更多
            }
        } else {
            return position;    //无法在此处直接处理type，将type置为position在onCreateViewHolder方法里处理。
        }
    }


    //想要扩展Adapter，commonViewHolder扩展也是关键。
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MultiTypeMaker multiTypeMaker;
        if (!MultiTypeMaker.isNormalType(viewType)) {
            multiTypeMaker = FuncMultiTypeMakerFactory.create(viewType, recyclerView);
            multiTypeMaker.setType(viewType);
            bindController(multiTypeMaker);        //绑定TypeMaker与对应的监听控制器
        } else {  //viewType就是position了(****************************注意要兼容刷新和加载更多没有情况****************************)
            int headerCount = typeManager.getHeaderCount();
            int footerCount = typeManager.getFooterCount();
            int refreshControllerCount = typeManager.getRefreshControllerCount();

            int position = viewType;
            if (headerCount > 0 && footerCount == 0) {                //有头无尾
                if (position >= refreshControllerCount && position <= headerCount + refreshControllerCount -1) {
                    multiTypeMaker = typeManager.getHeader(position - refreshControllerCount);
                    multiTypeMaker.setType(MultiTypeMaker.TYPE_HEADER);
                } else {
                    multiTypeMaker = mNormalTypeMaker;
//                  multiTypeMaker.getType(position - typeManager.getRefreshControllerCount() - typeManager.getHeaderCount());
                }
            } else if (headerCount == 0 && footerCount > 0) {          //无头有尾
                int mNormalDataEndIndex = mNormalData.size() + refreshControllerCount + headerCount - 1;
                if (position >= refreshControllerCount + headerCount && position <= mNormalDataEndIndex) {
                    multiTypeMaker = mNormalTypeMaker;
                } else {
                    multiTypeMaker = typeManager.getFooter(position - mNormalData.size() - refreshControllerCount - headerCount);
                    multiTypeMaker.setType(MultiTypeMaker.TYPE_FOOTER);
                }
            } else if (headerCount == 0 && footerCount == 0) {         //无头无尾
                multiTypeMaker = mNormalTypeMaker;
            } else if (headerCount > 0 && footerCount > 0) {            //有头有尾
                if (position >= refreshControllerCount && position <= headerCount + refreshControllerCount - 1) {
                    multiTypeMaker = typeManager.getHeader(position - refreshControllerCount);
                    multiTypeMaker.setType(MultiTypeMaker.TYPE_HEADER);
                } else {
                    if (position >= headerCount + refreshControllerCount - 1 && position <= mNormalData.size() + refreshControllerCount + headerCount - 1) {
                        multiTypeMaker = mNormalTypeMaker;
                    } else {
                        multiTypeMaker = typeManager.getFooter(position - mNormalData.size() - refreshControllerCount - headerCount);
                        multiTypeMaker.setType(MultiTypeMaker.TYPE_FOOTER);
                    }
                }
            } else {
                multiTypeMaker = null;
            }
        }
        CommonViewHolder commonViewHolder = new CommonViewHolder(LayoutInflater.from(mContext).inflate(multiTypeMaker.getLayoutId(viewType), parent, false));
        commonViewHolder.setMultiTypeMaker(multiTypeMaker);
        return commonViewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {        //(*****************position和maker一定要对应上**********************)
        int headerCount = typeManager.getHeaderCount();
        int footerCount = typeManager.getFooterCount();
        int refreshControllerCount = typeManager.getRefreshControllerCount();


        CommonViewHolder commonViewHolder = (CommonViewHolder) holder;
        MultiTypeMaker multiTypeMaker = commonViewHolder.getMultiTypeMaker();
        multiTypeMaker.setType(getViewHolderType(position));
        int normalViewStartPosition = position - typeManager.getRefreshControllerCount() - typeManager.getHeaderCount();
        if (multiTypeMaker.isNormalType(multiTypeMaker.getType(position))) {
            switch (multiTypeMaker.getType()){
                case MultiTypeMaker.TYPE_NORMAL:
                    multiTypeMaker = mNormalTypeMaker;      //(**********multiTypeMaker要重新赋值，否则会复用脚布局。*************)
                    multiTypeMaker.bindViewHolder(commonViewHolder, mNormalData.get(normalViewStartPosition), multiTypeMaker.getType(normalViewStartPosition), normalViewStartPosition);
                    break;
                case MultiTypeMaker.TYPE_HEADER:
                    multiTypeMaker = typeManager.getHeader(position - refreshControllerCount);
                    multiTypeMaker.bindViewHolder(commonViewHolder, multiTypeMaker.getData(), MultiTypeMaker.TYPE_HEADER, position - refreshControllerCount);
                    break;
                case MultiTypeMaker.TYPE_FOOTER:
                    multiTypeMaker = typeManager.getFooter(position - mNormalData.size() - refreshControllerCount - headerCount);
                    multiTypeMaker.bindViewHolder(commonViewHolder,multiTypeMaker.getData(),MultiTypeMaker.TYPE_FOOTER,position - mNormalData.size() - refreshControllerCount - headerCount);
                    break;
            }
        } else {
            multiTypeMaker.bindViewHolder(commonViewHolder, null, multiTypeMaker.getType(position), position);
        }
    }

    //确定非刷新、加载更多的其它视图的类型。(一定还是要根据position来判断，不能根据multiTypeMaker的gettype来判断，会有复用的问题。)
    private int getViewHolderType(int position) {
        //(****************************注意要兼容刷新和加载更多没有情况****************************)
        int headerCount = typeManager.getHeaderCount();
        int footerCount = typeManager.getFooterCount();
        int refreshControllerCount = typeManager.getRefreshControllerCount();

        if (headerCount > 0 && footerCount == 0) {                //有头无尾
            if (position >= refreshControllerCount && position <= headerCount + refreshControllerCount -1) {
                return MultiTypeMaker.TYPE_HEADER;
            } else {
                return MultiTypeMaker.TYPE_NORMAL;
            }
        } else if (headerCount == 0 && footerCount > 0) {          //无头有尾
            int mNormalDataEndIndex = mNormalData.size() + refreshControllerCount + headerCount - 1;
            if (position >= refreshControllerCount + headerCount && position <= mNormalDataEndIndex) {
                return MultiTypeMaker.TYPE_NORMAL;
            } else {
                return MultiTypeMaker.TYPE_FOOTER;
            }
        } else if (headerCount == 0 && footerCount == 0) {         //无头无尾
            return MultiTypeMaker.TYPE_NORMAL;
        } else if (headerCount > 0 && footerCount > 0) {            //有头有尾
            if (position >= refreshControllerCount && position <= headerCount + refreshControllerCount -1) {
                return MultiTypeMaker.TYPE_HEADER;
            } else {
                if (position >= headerCount + refreshControllerCount - 1 && position <= mNormalData.size() + refreshControllerCount + headerCount - 1) {
                    return MultiTypeMaker.TYPE_NORMAL;
                } else {
                    return MultiTypeMaker.TYPE_FOOTER;
                }
            }
        } else {
            return MultiTypeMaker.TYPE_NORMAL;
        }
    }

    private void bindController(MultiTypeMaker multiTypeMaker) {
        multiTypeMaker.bindLockObserver(lockObserver);                  //禁止刷新与加载更多同时进行

        switch (multiTypeMaker.getType(0)) {
            case MultiTypeMaker.TYPE_REFRESH:
                lockObserver.setRefreshTypeMaker(multiTypeMaker);
                RefreshController refreshController = typeManager.getRefreshController();
                if (refreshController != null) {
                    refreshController.bindTypeMaker(multiTypeMaker);
                }
                break;
            case MultiTypeMaker.TYPE_LOADER:
                lockObserver.setLoadTypeMaker(multiTypeMaker);
                LoadController loadController = typeManager.getLoadController();
                if (loadController != null) {
                    loadController.bindTypeMaker(multiTypeMaker);
                }
                break;
        }
    }

    @Override
    public void addHeader(MultiTypeMaker header) {
        typeManager.addHeader(header);
        myNotify();
    }

    @Override
    public void addFooter(MultiTypeMaker footer) {
        typeManager.addFooter(footer);
        myNotify();
    }

    @Override
    public void setRefreshController(RefreshController refreshController) {
        typeManager.setRefreshController(refreshController);
    }

    @Override
    public void setLoadController(LoadController loadController) {
        typeManager.setLoadController(loadController);
    }

    @Override
    public boolean removeHeader(MultiTypeMaker header) {
        boolean success = typeManager.removeHeader(header);
        if(success){
            myNotify();
        }
        return success;
    }

    @Override
    public boolean removeFooter(MultiTypeMaker footer) {
        boolean success = typeManager.removeFooter(footer);
        if(success){
            myNotify();
        }
        return success;
    }
}
