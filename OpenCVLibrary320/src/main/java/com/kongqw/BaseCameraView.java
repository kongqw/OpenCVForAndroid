package com.kongqw;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;

import com.kongqw.listener.OnOpenCVLoadListener;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

/**
 * Created by kongqingwei on 2017/5/17.
 * BaseRobotCameraView
 */

public abstract class BaseCameraView extends JavaCameraView implements LoaderCallbackInterface, CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "BaseRobotCameraView";

    public abstract void onOpenCVLoadSuccess();

    public abstract void onOpenCVLoadFail();

    // 标记当前OpenCV加载状态
    private boolean isLoadSuccess;
    protected Mat mRgba;
    protected Mat mGray;

    // 控制切换摄像头
    private int mCameraIndexCount = 0;

    public BaseCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 加载OpenCV
        // boolean loadOpenCV = loadOpenCV(context);
        // Log.i(TAG, "BaseRobotCameraView [Load OpenCV] : " + loadOpenCV);

        setCvCameraViewListener(this);
    }

    /**
     * 加载OpenCV
     *
     * @param context 上下文
     * @return 是否加载成功
     */
    public boolean loadOpenCV(Context context) {
        // 初始化OpenCV
        return OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, context, this);
    }

    @Override
    public void onManagerConnected(int status) {
        switch (status) {
            case LoaderCallbackInterface.SUCCESS:
                Log.i(TAG, "onManagerConnected: 加载成功");
                isLoadSuccess = true;

                // 加载成功
                onOpenCVLoadSuccess();

                enableView();

                if (null != mOnOpenCVLoadListener) {
                    mOnOpenCVLoadListener.onOpenCVLoadSuccess();
                }
                break;
            default:
                isLoadSuccess = false;
                // 加载失败
                // super.onManagerConnected(status);
                onOpenCVLoadFail();
                Log.i(TAG, "onManagerConnected: 加载失败");
                if (null != mOnOpenCVLoadListener) {
                    mOnOpenCVLoadListener.onOpenCVLoadFail();
                }
                break;
        }
    }

    @Override
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {
        // OpenCV Manager 没有安装
        Log.i(TAG, "onPackageInstall: ");

        // Toast.makeText(getContext(), "OpenCV Manager 没有安装", Toast.LENGTH_SHORT).show();
        if (null != mOnOpenCVLoadListener) {
            mOnOpenCVLoadListener.onNotInstallOpenCVManager();
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
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.i(TAG, "onWindowVisibilityChanged: " + visibility);
        switch (visibility) {
            case VISIBLE:
                Log.i(TAG, "onWindowVisibilityChanged: VISIBLE");
                enableView();
                break;
            case INVISIBLE:
                // Log.i(TAG, "onWindowVisibilityChanged: INVISIBLE");
                // disableView();
                // break;
            case GONE:
                // Log.i(TAG, "onWindowVisibilityChanged: GONE");
                // disableView();
                // break;
            default:
                // Log.i(TAG, "onWindowVisibilityChanged: default");
                disableView();
                break;
        }
    }

    @Override
    public void enableView() {
        // OpenCV 已经加载成功并且当前Camera关闭
        if (isLoadSuccess && !mEnabled) {
            super.enableView();
        }
    }

    @Override
    public void disableView() {
        if (mEnabled) {
            super.disableView();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
        disableView();
    }


    /**
     * 切换摄像头
     */
    public void swapCamera() {
        disableView();
        setCameraIndex(++mCameraIndexCount % getCameraCount());
        enableView();
    }

    /**
     * 获取摄像头个数
     *
     * @return 摄像头个数
     */
    private int getCameraCount() {
//        CameraManager manager = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            manager = (CameraManager) getContext().getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
//            try {
//                String[] cameraIdList = manager.getCameraIdList();
//                return cameraIdList.length;
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//                return 0;
//            }
//        } else {
//            return Camera.getNumberOfCameras();
//        }
        return Camera.getNumberOfCameras();
    }

    private OnOpenCVLoadListener mOnOpenCVLoadListener;

    /**
     * 添加OpenCV加载的监听
     *
     * @param listener 监听
     */
    public void setOnOpenCVLoadListener(OnOpenCVLoadListener listener) {
        mOnOpenCVLoadListener = listener;
    }
}
