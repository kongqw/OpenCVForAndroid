package com.kongqw.opencvforandroid.view;

import android.content.Context;
import android.util.AttributeSet;

import com.kongqw.opencv.OpenCVCameraView;
import com.kongqw.opencv.PeopleDetector;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Kongqw on 2018/1/4.
 * HogPeopleDetectView
 */

public class HogPeopleDetectView extends OpenCVCameraView {

    private static final Scalar HOG_FULL_BODY_COLOR = new Scalar(0, 255, 255, 0);
    private final PeopleDetector mPeopleDetector;

    public HogPeopleDetectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPeopleDetector = new PeopleDetector();
    }

    @Override
    protected void onCameraFrame(Mat rgba, Mat gray) {
        // TODO 做你想做的
        //

        Rect[] rects = mPeopleDetector.detectPeople(gray);
        for (Rect rect : rects) {
            Imgproc.rectangle(mRgba, rect.tl(), rect.br(), HOG_FULL_BODY_COLOR, 3);
        }
    }
}
