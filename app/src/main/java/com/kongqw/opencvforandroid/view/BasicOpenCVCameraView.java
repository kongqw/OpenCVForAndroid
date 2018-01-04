package com.kongqw.opencvforandroid.view;

import android.content.Context;
import android.util.AttributeSet;

import com.kongqw.opencv.OpenCVCameraView;

import org.opencv.core.Mat;

/**
 * Created by Kongqw on 2018/1/4.
 * BasicOpenCVCameraView
 */

public class BasicOpenCVCameraView extends OpenCVCameraView {

    public BasicOpenCVCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCameraFrame(Mat rgba, Mat gray) {
        // TODO 做你想做的

    }
}
