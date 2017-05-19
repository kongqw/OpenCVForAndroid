package com.kongqw;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kongqingwei on 2017/5/17.
 * ObjectDetector
 */

public class ObjectDetector {

    private Context mApplicationContext;
    private MatOfRect mObject;
    private Size mMinSize;
    private Size mMaxSize;

    /**
     * 构造方法
     * 需要在OpenCV加载成功之后再创建
     *
     * @param context 检测器
     */
    public ObjectDetector(Context context) {
        mApplicationContext = context.getApplicationContext();
        mObject = new MatOfRect();
        mMinSize = new Size();
        mMaxSize = new Size();
    }

    public void release() {
        mObject = null;
        mMinSize = null;
        mMaxSize = null;
    }

    /**
     * 获取Java识别器
     *
     * @param id 识别器id
     * @return 识别器
     */
    public CascadeClassifier getJavaDetector(int id) {
        CascadeClassifier javaDetector;
        InputStream is = null;
        FileOutputStream os = null;
        try {
            is = mApplicationContext.getResources().openRawResource(id);
            File cascadeDir = mApplicationContext.getDir("cascade", Context.MODE_PRIVATE);
            // File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            File cascadeFile = new File(cascadeDir, id + ".xml");
            os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            javaDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if (javaDetector.empty()) {
                javaDetector = null;
            }

            boolean delete = cascadeDir.delete();
            return javaDetector;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != os) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 目标检测
     *
     * @param detector     detector
     * @param gray         gray
     * @param minNeighbors minNeighbors
     * @param minSize      minSize
     * @return 目标
     */
    private Rect[] detectObject(CascadeClassifier detector, Mat gray, int minNeighbors, Size minSize) {

        // 使用Java人脸检测
        if (detector != null) {
            detector.detectMultiScale(
                    gray, // 要检查的灰度图像
                    mObject, // 检测到的人脸
                    1.1, // 表示在前后两次相继的扫描中，搜索窗口的比例系数。默认为1.1即每次搜索窗口依次扩大10%;
                    minNeighbors, // 默认是3 控制误检测，表示默认几次重叠检测到人脸，才认为人脸存在
                    Objdetect.CASCADE_SCALE_IMAGE/* | Objdetect.CASCADE_DO_CANNY_PRUNING*/,
                    // Objdetect.CASCADE_FIND_BIGGEST_OBJECT,
                    // getMinSize(gray.cols(), gray.rows(), relativeObjectSize), // 目标最小可能的大小
                    minSize, // 目标最小可能的大小
                    mMaxSize); // 目标最大可能的大小


            return mObject.toArray();
        } else {
            return null;
        }
    }

    /**
     * 检测一张脸
     *
     * @param detector detector
     * @param gray     gray
     * @return 一张人脸位置
     */
    public Rect detectFace(CascadeClassifier detector, Mat gray/*, float relativeObjectSize*/) {

        Rect[] faces = detectObject(
                detector,
                gray,
                6,
                getMinSize(gray.cols(), gray.rows(), 0.2F, 0.2F)
        );
        if (null != faces && 0 < faces.length) {
//            Rect face = faces[0];
//
//            double[] doubles = {face.br().x + ((double) face.width / 4), face.br().y + ((double) face.height / 4)};
//            face.set(doubles);
//
//            face.width /= 2;
//            face.height /= 2;
//
//            Rect rect = new Rect((int) face.tl().x + face.width / 4, (int) face.tl().y + face.height / 4, face.width / 2, face.height / 2);
            return faces[0];
        }
        return null;
    }

    /**
     * 上半身检测
     *
     * @param detector detector
     * @param gray     gray
     * @return 上半身位置
     */
    public Rect detectUpperBody(CascadeClassifier detector, Mat gray/*, float relativeObjectWidth, float relativeObjectHeight*/) {

        Rect[] upperBody = detectObject(
                detector,
                gray,
                2,
                getMinSize(gray.cols(), gray.rows(), 0.2F, 0.4F)
        );
        if (null != upperBody && 0 < upperBody.length) {
            return upperBody[0];
        }
        return null;
    }

    /**
     * 全身检测
     *
     * @param detector detector
     * @param gray     gray
     * @return 全身位置
     */
    public Rect detectFullBody(CascadeClassifier detector, Mat gray/*, float relativeObjectWidth, float relativeObjectHeight*/) {

        Rect[] fullBody = detectObject(
                detector,
                gray,
                2,
                getMinSize(gray.cols(), gray.rows(), 0.1F, 0.5F)
        );
        if (null != fullBody && 0 < fullBody.length) {
            return fullBody[0];
        }
        return null;
    }

//    /**
//     * 获取检测对象的最小大小
//     *
//     * @param relativeObjectSize 大小占比
//     * @return 大小
//     */
//    private Size getMinSize(float relativeObjectSize) {
//        if (mAbsoluteObjectSize == 0) {
//            int height = mGray.rows();
//            if (Math.round(height * relativeObjectSize) > 0) {
//                // 高度乘以百分比  四舍五入
//                mAbsoluteObjectSize = Math.round(height * relativeObjectSize);
//            }
//        }
//        mMinSize.width = mMinSize.height = mAbsoluteObjectSize;
//        return mMinSize;
//    }

    /**
     * 获取检测对象的最小大小
     *
     * @param cameraWidth          图像宽度
     * @param cameraHeight         图像高度
     * @param relativeObjectWidth  屏宽占比
     * @param relativeObjectHeight 屏高占比
     * @return 大小
     */
    private Size getMinSize(int cameraWidth, int cameraHeight, float relativeObjectWidth, float relativeObjectHeight) {
        int width = Math.round(cameraWidth * relativeObjectWidth);
        int height = Math.round(cameraHeight * relativeObjectHeight);
        mMinSize.width = 0 >= width ? 0 : (cameraWidth < width ? cameraWidth : width); // width [0, cameraWidth]
        mMinSize.height = 0 >= height ? 0 : (cameraHeight < height ? cameraHeight : height); // height [0, cameraHeight]
        return mMinSize;
    }
}
