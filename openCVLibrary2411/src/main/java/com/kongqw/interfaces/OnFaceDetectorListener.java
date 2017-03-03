package com.kongqw.interfaces;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 * Created by kongqingwei on 2017/3/3.
 * 检测到人脸的回调
 */

public interface OnFaceDetectorListener {
    // 检测到一个人脸的回调
    void onFace(Mat mat, Rect rect);
}
