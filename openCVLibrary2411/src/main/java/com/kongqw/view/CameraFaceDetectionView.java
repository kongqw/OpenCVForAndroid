package com.kongqw.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;

import com.kongqw.interfaces.OnFaceDetectorListener;
import com.kongqw.interfaces.OnOpenCVInitListener;

import org.opencv.R;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_SCALE_IMAGE;

/**
 * Created by kqw on 2016/9/9.
 * CameraFaceDetectionView
 */
public class CameraFaceDetectionView extends JavaCameraView implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "RobotCameraView";
    private OnFaceDetectorListener mOnFaceDetectorListener;
    private OnOpenCVInitListener mOnOpenCVInitListener;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private CascadeClassifier mJavaDetector;
    // 记录切换摄像头点击次数
    private int mCameraSwitchCount = 0;

    private Mat mRgba;
    private Mat mGray;

    private int mAbsoluteFaceSize = 0;
    // 脸部占屏幕多大面积的时候开始识别
    private static final float RELATIVE_FACE_SIZE = 0.2f;

    public CameraFaceDetectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 加载OpenCV
     *
     * @param context context
     * @return 是否安装了OpenCV
     */
    public boolean loadOpenCV(Context context) {
        // 初始化OpenCV
        boolean isLoaded = OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, context, mLoaderCallback);

        if (isLoaded) {
            // OpenCV加载成功
            setCvCameraViewListener(this);
        } else {
            // 加载失败
            Log.i(TAG, "loadOpenCV: ----------------------------");
            Log.i(TAG, "loadOpenCV: " + "请先安装OpenCV Manager！ https://github.com/kongqw/KqwFaceDetectionDemo/tree/master/OpenCVManager");
            Log.i(TAG, "loadOpenCV: ----------------------------");
        }

        return isLoaded;
    }

    private boolean isLoadSuccess = false;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext().getApplicationContext()) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "onManagerConnected: OpenCV加载成功");
                    if (null != mOnOpenCVInitListener) {
                        mOnOpenCVInitListener.onLoadSuccess();
                    }
                    isLoadSuccess = true;
                    try {
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getContext().getApplicationContext().getDir("cascade", Context.MODE_PRIVATE);
                        File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(cascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "级联分类器加载失败");
                            mJavaDetector = null;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "没有找到级联分类器");
                    }
                    enableView();

                    break;
                case LoaderCallbackInterface.MARKET_ERROR: // OpenCV loader can not start Google Play Market.
                    Log.i(TAG, "onManagerConnected: 打开Google Play失败");
                    if (null != mOnOpenCVInitListener) {
                        mOnOpenCVInitListener.onMarketError();
                    }
                    break;
                case LoaderCallbackInterface.INSTALL_CANCELED: // Package installation has been canceled.
                    Log.i(TAG, "onManagerConnected: 安装被取消");
                    if (null != mOnOpenCVInitListener) {
                        mOnOpenCVInitListener.onInstallCanceled();
                    }
                    break;
                case LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION: // Application is incompatible with this version of OpenCV Manager. Possibly, a service update is required.
                    Log.i(TAG, "onManagerConnected: 版本不正确");
                    if (null != mOnOpenCVInitListener) {
                        mOnOpenCVInitListener.onIncompatibleManagerVersion();
                    }
                    break;
                default: // Other status,
                    Log.i(TAG, "onManagerConnected: 其他错误");
                    if (null != mOnOpenCVInitListener) {
                        mOnOpenCVInitListener.onOtherError();
                    }
                    // super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    public void enableView() {
        if (isLoadSuccess) {
            super.enableView();
        }
    }

    @Override
    public void disableView() {
        if (isLoadSuccess) {
            super.disableView();
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
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // 子线程（非UI线程）
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * RELATIVE_FACE_SIZE) > 0) {
                mAbsoluteFaceSize = Math.round(height * RELATIVE_FACE_SIZE);
            }
        }

        if (mJavaDetector != null) {
            MatOfRect faces = new MatOfRect();
            mJavaDetector.detectMultiScale(mGray, // 要检查的灰度图像
                    faces, // 检测到的人脸
                    1.1, // 表示在前后两次相继的扫描中，搜索窗口的比例系数。默认为1.1即每次搜索窗口依次扩大10%;
                    10, // 默认是3 控制误检测，表示默认几次重叠检测到人脸，才认为人脸存在
                    CV_HAAR_FIND_BIGGEST_OBJECT // 返回一张最大的人脸（无效？）
                            | CV_HAAR_SCALE_IMAGE
                            | CV_HAAR_DO_ROUGH_SEARCH
                            | CV_HAAR_DO_CANNY_PRUNING, //CV_HAAR_DO_CANNY_PRUNING ,// CV_HAAR_SCALE_IMAGE, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
                    new Size(mGray.width(), mGray.height()));

            // 检测到人脸
            Rect[] facesArray = faces.toArray();
            for (Rect aFacesArray : facesArray) {
                Core.rectangle(mRgba, aFacesArray.tl(), aFacesArray.br(), FACE_RECT_COLOR, 3);
                if (null != mOnFaceDetectorListener) {
                    mOnFaceDetectorListener.onFace(mRgba, aFacesArray);
                }
            }
        }
        return mRgba;
    }

    /**
     * 切换摄像头
     *
     * @return 切换摄像头是否成功
     */
    public boolean switchCamera() {
        // 摄像头总数
        int numberOfCameras = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            numberOfCameras = Camera.getNumberOfCameras();
        }
        // 2个及以上摄像头
        if (1 < numberOfCameras) {
            // 设备没有摄像头
            int index = ++mCameraSwitchCount % numberOfCameras;
            disableView();
            setCameraIndex(index);
            enableView();
            return true;
        }
        return false;
    }

    /**
     * 添加人脸识别额监听
     *
     * @param listener 回调接口
     */
    public void setOnFaceDetectorListener(OnFaceDetectorListener listener) {
        mOnFaceDetectorListener = listener;
    }

    /**
     * 添加加载OpenCV的监听
     *
     * @param listener 回调接口
     */
    public void setOnOpenCVInitListener(OnOpenCVInitListener listener) {
        mOnOpenCVInitListener = listener;
    }
}
