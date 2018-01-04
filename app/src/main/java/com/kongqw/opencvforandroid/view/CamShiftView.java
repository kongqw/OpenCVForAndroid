package com.kongqw.opencvforandroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.kongqw.opencv.CamShiftTracker;
import com.kongqw.opencv.OpenCVCameraView;
import com.kongqw.opencv.listener.OnCamShiftListener;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Kongqw on 2018/1/4.
 * CamShiftView
 */

public class CamShiftView extends OpenCVCameraView implements View.OnTouchListener {

    private static final String TAG = CamShiftView.class.getSimpleName();
    private static final Scalar CAM_SHIFT_COLOR = new Scalar(0, 255, 255, 0);
    private CamShiftTracker mCamShiftTracker;
    private boolean isTracking;

    private int xDown;
    private int yDown;
    private Rect mTargetRect;

    public CamShiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    @Override
    protected void onOpenCVLoadSuccess() {
        super.onOpenCVLoadSuccess();
        mCamShiftTracker = new CamShiftTracker();
    }

    @Override
    protected void onCameraFrame(Mat rgba, Mat gray) {

        if (!hasVaildTarget(rgba)) {
            // 没有跟随目标
            isTracking = false;
            return;
        }

        if (!isTracking) {
            // 初始化目标
            mCamShiftTracker.init(rgba, mTargetRect);
            isTracking = true;
        } else {
            // 跟随目标
            RotatedRect rotatedRect = mCamShiftTracker.update(rgba, mTargetRect);
            Imgproc.ellipse(rgba, rotatedRect, CAM_SHIFT_COLOR);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTracking = false;
                mTargetRect = null;
                xDown = (int) event.getX();
                yDown = (int) event.getY();
                Log.i(TAG, "onTouch: 按下位置 ： x = " + xDown + "  y = " + yDown);

                break;
            case MotionEvent.ACTION_UP:
                int xUp = (int) event.getX();
                int yUp = (int) event.getY();

                Log.i(TAG, "onTouch: 抬起位置 ： x = " + xUp + "  y = " + yUp);

                int x = Math.min(xDown, xUp);
                int y = Math.min(yDown, yUp);
                int width = Math.abs(xUp - xDown);
                int height = Math.abs(yUp - yDown);

                mTargetRect = new Rect(x, y, width, height);

                break;
            default:
                break;
        }
        return true;
    }

    private boolean hasVaildTarget(Mat mat) {
        if (null == mTargetRect) {
            return false;
        }

        // 图像的宽度
        int cols = mat.cols();
        // 图像的高度
        int rows = mat.rows();

        return !(mTargetRect.x + mTargetRect.width > cols || mTargetRect.y + mTargetRect.height > rows);
    }

    public void setOnCamShiftListener(OnCamShiftListener listener) {
        mCamShiftTracker.setOnCamShiftListener(listener);
    }
}
