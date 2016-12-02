package kong.qingwei.kqwfacedetectiondemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

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

/**
 * Created by kqw on 2016/9/9.
 * CameraFaceDetectionView
 */
public class CameraFaceDetectionView extends JavaCameraView implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "RobotCameraView";
    private OnFaceDetectorListener mOnFaceDetectorListener;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private CascadeClassifier mJavaDetector;

    private Mat mRgba;
    private Mat mGray;

    private int mAbsoluteFaceSize = 0;
    // 脸部占屏幕多大面积的时候开始识别
    private static final float RELATIVE_FACE_SIZE = 0.2f;

    public CameraFaceDetectionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadOpenCV(context);

        setCvCameraViewListener(this);
    }

    private void loadOpenCV(Context context) {
        // 初始化OpenCV
        boolean b = OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, context, mLoaderCallback);
        Log.i(TAG, "loadOpenCV: b = " + b);
    }

    private boolean isLoadSuccess = false;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext().getApplicationContext()) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV加载成功");
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
                }
                break;
                default:
                    super.onManagerConnected(status);
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

        // Java人脸检测器
        if (mJavaDetector != null) {
            MatOfRect faces = new MatOfRect();
            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
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

    public void setOnFaceDetectorListener(OnFaceDetectorListener listener) {
        mOnFaceDetectorListener = listener;
    }

    public interface OnFaceDetectorListener {
        // 检测到一个人脸的回调
        void onFace(Mat mat, Rect rect);
    }
}
