package com.kongqw.listener;

import org.opencv.core.Mat;

/**
 * Created by kongqingwei on 2017/5/16.
 * OnCalcBackProjectListener
 */

public interface OnCalcBackProjectListener {

    // 直方图的反投影回调
    void onCalcBackProject(Mat backProject);
}
