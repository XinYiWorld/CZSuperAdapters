package com.xinyi.czsuperadapter.interfaces;

/**
 * Created by 陈章 on 2017/5/26 0026.
 * func: 记忆位置
 */
public interface IMemoryPosition {
    int getPreviousSelectPosition();
    void setPreviousSelectPosition(int previousSelectPosition);
    void selectPosition(final int position);
}
