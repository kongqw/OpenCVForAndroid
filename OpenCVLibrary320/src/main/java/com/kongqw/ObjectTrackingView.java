package com.kongqw;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.kongqw.listener.OnCalcBackProjectListener;
import com.kongqw.listener.OnObjectTrackingListener;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by kqw on 2016/7/13.
 * ObjectTrackingView
 */
public class ObjectTrackingView extends BaseCameraView implements View.OnTouchListener, OnCalcBackProjectListener {

    private static final String TAG = "ObjectTrackingView";
    private static final Scalar TRACKING_RECT_COLOR = new Scalar(255, 255, 0, 255);

    // CamShift 目标追踪器
    private ObjectTracker objectTracker;
    // 追踪目标区域
    private Rect mTrackWindow;
    // 追踪状态
    private boolean isTracking;

    @Override
    public void onOpenCVLoadSuccess() {
        Log.i(TAG, "onOpenCVLoadSuccess: ");
        // 目标追踪器
        objectTracker = new ObjectTracker();
        objectTracker.setOnCalcBackProjectListener(this);
    }

    @Override
    public void onOpenCVLoadFail() {
        Log.i(TAG, "onOpenCVLoadFail: ");
    }

    public ObjectTrackingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    private double mCameraArea;

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // 子线程（非UI线程）
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        // 目标追踪
        if (isTracking && null != mTrackWindow) {

            if (0 == mCameraArea) {
                mCameraArea = mGray.size().area();
            }

            RotatedRect rotatedRect = objectTracker.objectTracking(mRgba);
            Rect rect = rotatedRect.boundingRect();
            double area = rect.area();

            if (1 < area && mCameraArea > area) {
                // 检测到有效的目标位置
                Imgproc.ellipse(mRgba, rotatedRect, TRACKING_RECT_COLOR, 3);
                Imgproc.rectangle(mRgba, rect.tl(), rect.br(), TRACKING_RECT_COLOR, 3);
                if (null != mOnObjectTrackingListener) {
                    Point center = rotatedRect.center;
                    mOnObjectTrackingListener.onObjectLocation(center);
                }
            } else {
                // 目标跟丢
                Log.i(TAG, "onCameraFrame: 目标丢失");
                isTracking = false;
                mTrackWindow = null;

                if (null != mOnObjectTrackingListener) {
                    mOnObjectTrackingListener.onObjectLost();
                }
            }
        }
        return mRgba;
    }


    int xDown;
    int yDown;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (null == mRgba) {
            return true;
        }
        int cols = mRgba.cols();
        int rows = mRgba.rows();
        int xOffset = (getWidth() - cols) / 2;
        int yOffset = (getHeight() - rows) / 2;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTracking = false;
                xDown = (int) event.getX() - xOffset;
                yDown = (int) event.getY() - yOffset;
                break;
            case MotionEvent.ACTION_UP:
                int xUp = (int) event.getX() - xOffset;
                int yUp = (int) event.getY() - yOffset;

                int width = Math.abs(xUp - xDown);
                int height = Math.abs(yUp - yDown);

                if (0 == width || 0 == height) {
                    // Toast.makeText(getContext(), "目标太小", Toast.LENGTH_SHORT).show();
                    break;
                }

                // 获取跟踪目标
                mTrackWindow = new Rect(Math.min(xDown, xUp), Math.min(yDown, yUp), width, height);

                // 创建跟踪目标
                objectTracker.createTrackedObject(mRgba, mTrackWindow);

                isTracking = true;

                Toast.makeText(getContext().getApplicationContext(), "已经选中跟踪目标！", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    private OnCalcBackProjectListener mOnCalcBackProjectListener;

    public void setOnCalcBackProjectListener(OnCalcBackProjectListener listener) {
        mOnCalcBackProjectListener = listener;
    }

    @Override
    public void onCalcBackProject(Mat backProject) {
        if (null != mOnCalcBackProjectListener) {
            mOnCalcBackProjectListener.onCalcBackProject(backProject);
        }
    }

    private OnObjectTrackingListener mOnObjectTrackingListener;

    public void setOnObjectTrackingListener(OnObjectTrackingListener listener) {
        mOnObjectTrackingListener = listener;
    }
}
