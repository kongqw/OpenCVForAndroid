package com.kongqw.interfaces;

/**
 * Created by kongqingwei on 2017/3/3.
 * 加载OpenCV的回调
 */

public interface OnOpenCVInitListener {

    /**
     * 加载成功
     */
    void onLoadSuccess();

    /**
     * 加载失败
     */
    void onLoadFail();

    /**
     * 打开Google Play失败
     */
    void onMarketError();

    /**
     * 安装被取消
     */
    void onInstallCanceled();

    /**
     * 版本不正确
     */
    void onIncompatibleManagerVersion();

    /**
     * 其他错误
     */
    void onOtherError();
}
