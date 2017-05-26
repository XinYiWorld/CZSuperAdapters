package com.xinyi.czsuperadapter.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinyi.czsuperadapter.ICRUDAdapter;
import com.xinyi.czsuperadapter.interfaces.IAddTypeMaker;
import com.xinyi.czsuperadapter.interfaces.IRemoveTypeMaker;

/**
 * Created by 陈章 on 2016/12/20 0020.
 * func:
 * 对刷新、加载更多、头布局、脚布局、多种样式的融合。
 * 1)需要注意的问题：刷新与加载更多两个事件是冲突的 touch 和scroll事件，而且加载更多的时候不能刷新，刷新的时候不能加载更多。
 * 2)onCreateViewHolder返回的holder被利用的问题:即如果主体数据新增了几条数据，主体数据会复用脚布局的view，导致空指针问题。
 *   复用多视图来实现刷新、加载更多、头、脚，最蛋痛的还是复用的问题。
 *  //TODO 复用的问题
 *    1》》使用setIsRecyclable(false)勉强可以解决复用的问题，但是影响性能。
 * 3)在adapter里对recyclerView的item设置点击事件后，再对recyclerView本身设置touch事件时，捕捉不到Down这个action。
 * 4）点击事件与刷新冲突（未解决）
 */

public class CZSuperAdapter<T> extends ICRUDAdapter<T> implements IAddTypeMaker,IRemoveTypeMaker {
    private static final String TAG = "CZSuperAdapter";
    private RecyclerView recyclerView;
    private MultiTypeMaker mNormalTypeMaker;
    private  LockObserver lockObserver;
    private  TypeManager typeManager;

    private CommonViewHolder.OnItemClickListener onItemClickListener;
    private CommonViewHolder.OnItemLongClickListener onItemLongClickListener;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    //-----------将构造方法分为2部分，解决Cannot reference this before supertype constructor has been called的问题 -----------------------------------------------------------------------
    public CZSuperAdapter(Context mContext) {
        super(mContext);
    }

    public void setmNormalTypeMaker(MultiTypeMaker mNormalTypeMaker) {
        this.mNormalTypeMaker = mNormalTypeMaker;
        mNormalTypeMaker.setType(MultiTypeMaker.TYPE_NORMAL);
        lockObserver = new LockObserver();                  //解决刷新与加载更多冲突
        typeManager = new TypeManager();                    //管理视图
    }
    //----------------------------------------------------------------------------------

    //通过onAttachedToRecyclerView即可获得RecyclerView的引用 ，不需要传入RecyclerView参数，但兼容旧代码，不删除旧的构造方法。
    public CZSuperAdapter(Context mContext , MultiTypeMaker mNormalTypeMaker) {
        super(mContext);
        this.mNormalTypeMaker = mNormalTypeMaker;
        mNormalTypeMaker.setType(MultiTypeMaker.TYPE_NORMAL);
        lockObserver = new LockObserver();                  //解决刷新与加载更多冲突
        typeManager = new TypeManager();                    //管理视图
    }


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
//                return position;        //无刷新
                if(typeManager.getHeaderCount() > 0){
                    return MultiTypeMaker.TYPE_HEADER;
                }else if(mNormalData.size() > 0){
                    return MultiTypeMaker.TYPE_NORMAL;
                }else if(typeManager.getFooterCount() > 0){
                    return MultiTypeMaker.TYPE_FOOTER;
                }else {
                    return position;
                }
            }
        } else if (position == getItemCount() - 1) {
            if (typeManager.getLoadControllerCount() == 1) {
                return MultiTypeMaker.TYPE_LOADER;
            } else {
//                return position;        //无加载更多
                if(typeManager.getFooterCount() > 0){
                    return MultiTypeMaker.TYPE_FOOTER;
                }else  if(mNormalData.size() > 0){
                    return MultiTypeMaker.TYPE_NORMAL;
                }else  if(typeManager.getHeaderCount() > 0){
                    return MultiTypeMaker.TYPE_HEADER;
                }else{
                    return position;
                }
            }
        } else {
            return position;    //无法在此处直接处理type，将type置为position在onCreateViewHolder方法里处理。
        }
    }


    //想要扩展Adapter，commonViewHolder扩展也是关键。
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MultiTypeMaker multiTypeMaker = null;
        if (!MultiTypeMaker.isNormalType(viewType)) {
            multiTypeMaker = FuncMultiTypeMakerFactory.create(viewType, recyclerView);
            multiTypeMaker.setType(viewType);
            bindController(multiTypeMaker);        //绑定TypeMaker与对应的监听控制器
            CommonViewHolder commonViewHolder = new CommonViewHolder(LayoutInflater.from(mContext).inflate(multiTypeMaker.getLayoutId(0), parent, false));
            commonViewHolder.setParent(parent);
            commonViewHolder.setMultiTypeMaker(multiTypeMaker);
//            commonViewHolder.setIsRecyclable(false);
            return commonViewHolder;
        } else {  //viewType就是position了(****************************注意要兼容刷新和加载更多没有情况****************************)
            int position = viewType;
            int normalViewType = getViewHolderType(position);
            int headerCount = typeManager.getHeaderCount();
            int refreshControllerCount = typeManager.getRefreshControllerCount();
            CommonViewHolder commonViewHolder = null;
            switch (normalViewType){
                case MultiTypeMaker.TYPE_HEADER:        //头布局
                    multiTypeMaker = typeManager.getHeader(position - refreshControllerCount);
                    multiTypeMaker.setType(MultiTypeMaker.TYPE_HEADER);
                    commonViewHolder = new CommonViewHolder(LayoutInflater.from(mContext).inflate(multiTypeMaker.getLayoutId(0), parent, false));
                    commonViewHolder.setIsRecyclable(false);
                    break;
                case MultiTypeMaker.TYPE_NORMAL:        //主体布局
                    multiTypeMaker = mNormalTypeMaker;
                    multiTypeMaker.setType(MultiTypeMaker.TYPE_NORMAL);
                    int normalViewStartPosition = position - typeManager.getRefreshControllerCount() - typeManager.getHeaderCount();
                    commonViewHolder = new CommonViewHolder(LayoutInflater.from(mContext).inflate(multiTypeMaker.getLayoutId(normalViewStartPosition), parent, false));
                    commonViewHolder.setIsRecyclable(true);
                    break;
                case MultiTypeMaker.TYPE_FOOTER:        //脚布局
                    multiTypeMaker = typeManager.getFooter(position - mNormalData.size() - refreshControllerCount - headerCount);
                    multiTypeMaker.setType(MultiTypeMaker.TYPE_FOOTER);
                    commonViewHolder = new CommonViewHolder(LayoutInflater.from(mContext).inflate(multiTypeMaker.getLayoutId(0), parent, false));
                    commonViewHolder.setIsRecyclable(false);
                    break;
                default:
                    break;
            }
            commonViewHolder.setParent(parent);
            commonViewHolder.setMultiTypeMaker(multiTypeMaker);
            return commonViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {        //(*****************position和maker一定要对应上**********************)
        int headerCount = typeManager.getHeaderCount();
        int refreshControllerCount = typeManager.getRefreshControllerCount();
        CommonViewHolder commonViewHolder = (CommonViewHolder) holder;
        MultiTypeMaker multiTypeMaker = commonViewHolder.getMultiTypeMaker();
        int normalViewStartPosition = position - typeManager.getRefreshControllerCount() - typeManager.getHeaderCount();
        switch (multiTypeMaker.getType()/*getViewHolderType(getItemViewType(position))*/){
            case MultiTypeMaker.TYPE_HEADER:        //头布局
                multiTypeMaker = typeManager.getHeader(position - refreshControllerCount);
                commonViewHolder.setOnItemClickListener(null,position - refreshControllerCount);
                commonViewHolder.setOnItemLongClickListener(null,position - refreshControllerCount);
                multiTypeMaker.bindViewHolder(commonViewHolder, multiTypeMaker.getData(), MultiTypeMaker.TYPE_HEADER, position - refreshControllerCount);
                break;
            case MultiTypeMaker.TYPE_NORMAL:        //主体布局
                if(mNormalData.isEmpty()) {
                    ((CommonViewHolder) holder).getParent().setVisibility(View.GONE);
                    break;
                }
                ((CommonViewHolder) holder).getParent().setVisibility(View.VISIBLE);

                //绑定点击事件
                if(onItemClickListener != null){
                    commonViewHolder.setOnItemClickListener(onItemClickListener,normalViewStartPosition);
                }
                if(onItemLongClickListener != null){
                    commonViewHolder.setOnItemLongClickListener(onItemLongClickListener,normalViewStartPosition);
                }
                multiTypeMaker.bindViewHolder(commonViewHolder, mNormalData.get(normalViewStartPosition), multiTypeMaker.getType(normalViewStartPosition), normalViewStartPosition);
                break;
            case MultiTypeMaker.TYPE_FOOTER:        //脚布局 (有可能被主体数据利用了)
                multiTypeMaker = typeManager.getFooter(position - mNormalData.size() - refreshControllerCount - headerCount);
                commonViewHolder.setOnItemClickListener(null,position - mNormalData.size() - refreshControllerCount - headerCount);
                commonViewHolder.setOnItemLongClickListener(null,position - mNormalData.size() - refreshControllerCount - headerCount);
                multiTypeMaker.bindViewHolder(commonViewHolder,multiTypeMaker.getData(),MultiTypeMaker.TYPE_FOOTER,position - mNormalData.size() - refreshControllerCount - headerCount);
                break;
            default:        //刷新或加载更多
                commonViewHolder.setOnItemClickListener(null,position);
                commonViewHolder.setOnItemLongClickListener(null,position);
                multiTypeMaker.bindViewHolder(commonViewHolder, null, multiTypeMaker.getType(position), position);
                break;
        }
    }



    //确定非刷新、加载更多的其它视图的类型。(一定还是要根据position来判断，不能根据multiTypeMaker的gettype来判断，会有复用的问题。)
    private int getViewHolderType(int position) {
        if(position == MultiTypeMaker.TYPE_REFRESH) return MultiTypeMaker.TYPE_REFRESH;
        if(position == MultiTypeMaker.TYPE_LOADER) return MultiTypeMaker.TYPE_LOADER;

        //(****************************注意要兼容刷新和加载更多没有情况****************************)
        int headerCount = typeManager.getHeaderCount();
        int footerCount = typeManager.getFooterCount();
        int refreshControllerCount = typeManager.getRefreshControllerCount();

        if (headerCount > 0 && footerCount == 0) {                //有头无尾
            if (position > refreshControllerCount - 1 && position <= headerCount + refreshControllerCount -1) {
                return MultiTypeMaker.TYPE_HEADER;
            } else {
                return MultiTypeMaker.TYPE_NORMAL;
            }
        } else if (headerCount == 0 && footerCount > 0) {          //无头有尾
            int mNormalDataEndIndex = mNormalData.size() + refreshControllerCount + headerCount - 1;
            if (position > refreshControllerCount + headerCount - 1 && position <= mNormalDataEndIndex) {
                return MultiTypeMaker.TYPE_NORMAL;
            } else {
                return MultiTypeMaker.TYPE_FOOTER;
            }
        } else if (headerCount == 0 && footerCount == 0) {         //无头无尾
            return MultiTypeMaker.TYPE_NORMAL;
        } else if (headerCount > 0 && footerCount > 0) {            //有头有尾
            if (position > refreshControllerCount - 1 && position <= headerCount + refreshControllerCount -1) {
                return MultiTypeMaker.TYPE_HEADER;
            } else {
                if (position > headerCount + refreshControllerCount - 1 && position <= mNormalData.size() + refreshControllerCount + headerCount - 1) {
                    return MultiTypeMaker.TYPE_NORMAL;
                } else {
                    return MultiTypeMaker.TYPE_FOOTER;
                }
            }
        } else {
            return MultiTypeMaker.TYPE_UNKNOWN;
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



    /**
     * set the on item click listener
     * 设置Item的点击事件
     * @param listener listener
     */
    public void setOnItemClickListener(final CommonViewHolder.OnItemClickListener listener ) {
        this.onItemClickListener = listener;
    }


    /**
     * set the on item long click listener
     * 设置Item的长点击事件
     * @param listener listener
     */
    public void setOnItemLongClickListener(final CommonViewHolder.OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }
}
