package com.xinyi.czsuperadapter.interfaces;

import java.util.Collection;
import java.util.List;

/**
 * Created by 陈章 on 2016/11/30 0030.
 * func:
 */

public interface ICRUD<T>{
    
    void add(T t);

    
    void remove(Object o);

    
    void addAll(Collection<? extends T> collection);

    
    void addAll(int i, Collection<? extends T> collection);

    
    void removeAll(Collection<? extends T> collection);

    void replaceAll(Collection<? extends T> collection);
    
    void clear();

    void set(int i, T t);

    
    void add(int i, T t);


    void remove(int i);

    int size();

    List<T> getNormalData();
}
