package com.kongqw;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.R;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Created by kqw on 2016/7/13.
 * RobotCameraView
 */
public class RobotPhotographView extends BaseRobotCameraView {

    private static final String TAG = "RobotPhotographView2";
    private CascadeClassifier mFaceDetector;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

    @Override
    public void onOpenCVLoadSuccess() {
        Log.i(TAG, "onOpenCVLoadSuccess: ");
        // 人脸检测器
        mFaceDetector = mObjectDetector.getJavaDetector(R.raw.lbpcascade_frontalface);
    }

    @Override
    public void onOpenCVLoadFail() {
        Log.i(TAG, "onOpenCVLoadFail: ");
    }

    public RobotPhotographView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // 子线程（非UI线程）
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        // 检测人脸  最小大小占屏比 0.2
        // Rect[] detectObject = mObjectDetector.detectObject(mFaceDetector, mGray, 0.2F);
        Rect face = mObjectDetector.detectFace(mFaceDetector, mGray);
        if (null != face) {
            // 画出人脸位置
            Imgproc.rectangle(mRgba, face.tl(), face.br(), FACE_RECT_COLOR, 3);
        }
        return mRgba;
    }
}
