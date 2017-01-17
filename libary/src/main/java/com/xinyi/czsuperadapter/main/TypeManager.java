package com.xinyi.czsuperadapter.main;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.xinyi.czsuperadapter.interfaces.IAddTypeMaker;
import com.xinyi.czsuperadapter.interfaces.IGetTypeMaker;
import com.xinyi.czsuperadapter.interfaces.IRemoveTypeMaker;

/**
 * Created by 陈章 on 2016/12/27 0027.
 * func:用于管理多类型的视图：包括刷新、加载更多、头布局（可以多个）、脚布局（可以多个）
 */

public class TypeManager implements IAddTypeMaker ,IGetTypeMaker ,IRemoveTypeMaker{
    private static final String TAG = "TypeManager";
    private RefreshController refreshController;
    private LoadController loadController;

    private List<MultiTypeMaker> headerContainer;
    private List<MultiTypeMaker> footerContainer;

    //增加头布局
    @Override
    public void addHeader(MultiTypeMaker header){
        if(headerContainer == null) headerContainer = new ArrayList<>();
        headerContainer.add(header);
    }

    @Override
    public void addFooter(MultiTypeMaker footer) {
        if(footerContainer == null) footerContainer = new ArrayList<>();
        footerContainer.add(footer);
    }

    @Override
    public void setLoadController(LoadController loadController) {
        this.loadController = loadController;
    }

    @Override
    public void setRefreshController(RefreshController refreshController) {
        this.refreshController = refreshController;
    }

    @Override
    public MultiTypeMaker getHeader(int position) {
        if(headerContainer == null) return null;
        return headerContainer.get(position);
    }

    @Override
    public MultiTypeMaker getFooter(int position) {
        if(footerContainer == null) return null;
        return footerContainer.get(position);
    }

    @Override
    public RefreshController getRefreshController() {
        return refreshController;
    }

    @Override
    public LoadController getLoadController() {
        return loadController;
    }

    public int getHeaderCount(){
        if(headerContainer == null) return 0;
        return headerContainer.size();
    }

    public int getFooterCount(){
        if(footerContainer == null) return 0;
        return footerContainer.size();
    }

    public int getRefreshControllerCount(){
        return  refreshController == null ? 0:1;
    }

    public int getLoadControllerCount(){
        return  loadController == null ? 0:1;
    }

    @Override
    public boolean removeHeader(MultiTypeMaker header) {
        if(headerContainer == null) return false;
        if(headerContainer.contains(header)){
            headerContainer.remove(header);
            Log.i(TAG, "removeHeader: 移除头成功");
            return true;
        }else{
            Log.i(TAG, "removeHeader: 未发现此头");
            return false;
        }
    }

    @Override
    public boolean removeFooter(MultiTypeMaker footer) {
        if(footer == null) return false;
        if(footerContainer.contains(footer)){
            footerContainer.remove(footer);
            Log.i(TAG, "removeFooter: 移除脚成功");
            return true;
        }else{
            Log.i(TAG, "removeFooter: 未发现此脚");
            return false;
        }
    }
}
