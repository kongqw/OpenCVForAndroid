package com.kongqw.opencv.listener;

import org.opencv.core.Mat;

/**
 * Created by Kongqw on 2018/1/4.
 * OnCamShiftListener
 */

public interface OnCamShiftListener {
    /**
     * 初始化跟随目标
     *
     * @param target target
     */
    void onTarget(Mat target);

    /**
     * 反投影回调
     *
     * @param backProject backProject
     */
    void onCalcBackProject(Mat backProject);
}
