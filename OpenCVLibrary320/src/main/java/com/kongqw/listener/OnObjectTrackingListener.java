package com.kongqw.listener;

/**
 * Created by kongqingwei on 2017/5/17.
 * OnObjectTrackingListener 目标追踪监听器
 */

public interface OnObjectTrackingListener {

    void onObjectLocation();

    void onObjectLost();
}
