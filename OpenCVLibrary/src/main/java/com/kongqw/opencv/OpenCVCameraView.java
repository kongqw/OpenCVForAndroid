package com.kongqw.opencv;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;


import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

/**
 * Created by kqw on 2018/1/4.
 * OpenCVCameraView
 */
public abstract class OpenCVCameraView extends JavaCameraView implements CameraBridgeViewBase.CvCameraViewListener2, LoaderCallbackInterface {

    static {
        System.loadLibrary("opencv_java3");
    }

    protected abstract void onCameraFrame(Mat rgba, Mat gray);

    protected void onOpenCVLoadSuccess() {
    }

    protected void onOpenCVLoadFailed() {
    }

    private static final String TAG = OpenCVCameraView.class.getSimpleName();
    protected Mat mRgba;
    protected Mat mGray;

    public OpenCVCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setCvCameraViewListener(this);

        loadOpenCV(context);
    }

    /**
     * 加载OpenCV
     *
     * @param context 上下文
     * @return 是否加载成功
     */
    private boolean loadOpenCV(Context context) {
        // 初始化OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            return OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, context, this);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            this.onManagerConnected(LoaderCallbackInterface.SUCCESS);

            return true;
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        // 子线程（非UI线程）
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        onCameraFrame(mRgba, mGray);

        return mRgba;
    }

    /**
     * 窗口显示状态发生变化
     *
     * @param visibility visibility
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.i(TAG, "onWindowVisibilityChanged: " + visibility);
        switch (visibility) {
            case VISIBLE:
                Log.i(TAG, "onWindowVisibilityChanged: VISIBLE");
                enableView();
                break;
            case INVISIBLE:
            case GONE:
            default:
                Log.i(TAG, "onWindowVisibilityChanged: INVISIBLE");
                disableView();
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
        disableView();
        disconnectCamera();
    }

    /**
     * 加载OpenCV 状态
     *
     * @param status status of initialization (see initialization status constants).
     */
    @Override
    public void onManagerConnected(int status) {
        switch (status) {
            case LoaderCallbackInterface.SUCCESS:
                Log.i(TAG, "onManagerConnected: 加载成功");
                onOpenCVLoadSuccess();
                enableView();
                break;
            default:
                Log.i(TAG, "onManagerConnected: 加载失败");
                onOpenCVLoadFailed();
                disconnectCamera();
                break;
        }
    }

    /**
     * 设置分辨率
     *
     * @param width  宽
     * @param height 高
     */
    public void setResolution(int width, int height) {
        disconnectCamera();
        mMaxHeight = height;
        mMaxWidth = width;
        connectCamera(getWidth(), getHeight());
    }

    /**
     * 未安装OpenCV Manager
     *
     * @param operation operation
     * @param callback  answer object with approve and cancel methods and the package description.
     */
    @Override
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {

    }
}
