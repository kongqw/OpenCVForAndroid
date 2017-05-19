package com.kongqw;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
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

    private CascadeClassifier mCascadeClassifier;
    private int mMinNeighbors;
    private float mRelativeObjectWidth;
    private float mRelativeObjectHeight;
    private Scalar mRectColor;

    /**
     * 构造方法
     *
     * @param context              上下文
     * @param id                   级联分类器ID
     * @param minNeighbors         连续几帧确认目标
     * @param relativeObjectWidth  最小宽度屏占比
     * @param relativeObjectHeight 最小高度屏占比
     * @param rectColor            画笔颜色
     */
    public ObjectDetector(Context context, int id, int minNeighbors, float relativeObjectWidth, float relativeObjectHeight, Scalar rectColor) {
        context = context.getApplicationContext();
        mCascadeClassifier = createDetector(context, id);
        mMinNeighbors = minNeighbors;
        mRelativeObjectWidth = relativeObjectWidth;
        mRelativeObjectHeight = relativeObjectHeight;
        mRectColor = rectColor;
    }

    /**
     * 创建检测器
     *
     * @param context 上下文
     * @param id      级联分类器ID
     * @return 检测器
     */
    private CascadeClassifier createDetector(Context context, int id) {
        CascadeClassifier javaDetector;
        InputStream is = null;
        FileOutputStream os = null;
        try {
            is = context.getResources().openRawResource(id);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
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
     * @param gray   灰度图像
     * @param object 识别结果的容器
     * @return 检测到的目标位置集合
     */
    public Rect[] detectObject(Mat gray, MatOfRect object) {
        // 使用Java人脸检测
        mCascadeClassifier.detectMultiScale(
                gray, // 要检查的灰度图像
                object, // 检测到的人脸
                1.1, // 表示在前后两次相继的扫描中，搜索窗口的比例系数。默认为1.1即每次搜索窗口依次扩大10%;
                mMinNeighbors, // 默认是3 控制误检测，表示默认几次重叠检测到人脸，才认为人脸存在
                Objdetect.CASCADE_SCALE_IMAGE,
                getSize(gray, mRelativeObjectWidth, mRelativeObjectHeight), // 目标最小可能的大小
                gray.size()); // 目标最大可能的大小

        return object.toArray();
    }

    /**
     * 根据屏占比获取大小
     *
     * @param gray                 gray
     * @param relativeObjectWidth  最小宽度屏占比
     * @param relativeObjectHeight 最小高度屏占比
     * @return 大小
     */
    private Size getSize(Mat gray, float relativeObjectWidth, float relativeObjectHeight) {
        Size size = gray.size();
        int cameraWidth = gray.cols();
        int cameraHeight = gray.rows();
        int width = Math.round(cameraWidth * relativeObjectWidth);
        int height = Math.round(cameraHeight * relativeObjectHeight);
        size.width = 0 >= width ? 0 : (cameraWidth < width ? cameraWidth : width); // width [0, cameraWidth]
        size.height = 0 >= height ? 0 : (cameraHeight < height ? cameraHeight : height); // height [0, cameraHeight]
        return size;
    }

    /**
     * 获取画笔颜色
     *
     * @return 颜色
     */
    public Scalar getRectColor() {
        return mRectColor;
    }
}
