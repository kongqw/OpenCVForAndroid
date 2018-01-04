package com.kongqw.opencv;

import com.kongqw.opencv.listener.OnCamShiftListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by Kongqw on 2018/1/4.
 * CamShiftTracker
 */

public class CamShiftTracker {

    private static final String TAG = CamShiftTracker.class.getSimpleName();

    private int mHMin = 0;
    private int mHMax = 180;
    private int mSMin = 60;
    private int mSMax = 200;
    private int mVMin = 60;
    private int mVMax = 200;

    private Mat mask;
    private Mat hsv;
    private final List<Mat> images;
    private Mat image;
    private final MatOfInt from_to;
    private final MatOfInt channels;
    private final Mat hist;
    private final MatOfInt histSize;
    private final MatOfFloat ranges;
    private Mat backProject;
    private TermCriteria termCriteria;

    private OnCamShiftListener mOnCamShiftListener;

    /**
     * 初始化
     */
    public CamShiftTracker() {
        images = new Vector<>();
        from_to = new MatOfInt(0, 0);
        channels = new MatOfInt(0);
        hist = new Mat();
        histSize = new MatOfInt(255);
        ranges = new MatOfFloat(0F, 256F);

        termCriteria = new TermCriteria(TermCriteria.EPS, 10, 1);
    }


    /**
     * 计算目标直方图
     *
     * @param rgba       RGBA
     * @param targetRect 目标位置
     */
    public void init(Mat rgba, Rect targetRect) {
        if (null == mask)
            mask = new Mat(rgba.size(), CvType.CV_8UC1);
        if (null == hsv)
            hsv = new Mat(rgba.size(), CvType.CV_8UC3);
        if (null == image)
            image = new Mat(rgba.size(), CvType.CV_8UC3);

        updateImages(rgba);
        Mat tempMask = mask.submat(targetRect);
        List<Mat> singletonList = Collections.singletonList(images.get(0).submat(targetRect));

        if (null != mOnCamShiftListener) {
            mOnCamShiftListener.onTarget(tempMask);
        }

        Imgproc.calcHist(singletonList, channels, tempMask, hist, histSize, ranges);
        // 将hist矩阵进行数组范围归一化，都归一化到0~255
        Core.normalize(hist, hist, 0, 255, Core.NORM_MINMAX);
    }

    /**
     * 计算目目标位置
     *
     * @param rgba       RGBA
     * @param targetRect 目标位置
     * @return 目标移动位置
     */
    public RotatedRect update(Mat rgba, Rect targetRect) {

        if (null == backProject)
            backProject = new Mat(rgba.size(), CvType.CV_8UC1);

        // 获取Source Images
        updateImages(rgba);
        // 计算直方图的反投影。
        Imgproc.calcBackProject(
                images, // Source arrays  当前最新图像
                channels, // 计算反投影的通道列表
                hist, // 目标直方图
                backProject,
                ranges,
                1.0);
        // 计算两个数组的按位连接（dst = src1 & src2）计算两个数组或数组和标量的每个元素的逐位连接。
        Core.bitwise_and(backProject, mask, backProject, new Mat());

        if (null != mOnCamShiftListener) {
            mOnCamShiftListener.onCalcBackProject(backProject);
        }

        // 追踪目标
        return Video.CamShift(backProject, targetRect, termCriteria);
    }

    /**
     * 设置 HSV 阈值
     *
     * @param hMin H 最小值
     * @param hMax H 最大值
     * @param sMin S 最小值
     * @param sMax S 最大值
     * @param vMin V 最小值
     * @param vMax V 最大值
     */
    public void setHSV(int hMin, int hMax, int sMin, int sMax, int vMin, int vMax) {
        mHMin = hMin;
        mHMax = hMax;
        mSMin = sMin;
        mSMax = sMax;
        mVMin = vMin;
        mVMax = vMax;
    }

    private void updateImages(Mat rgba) {
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV);

        Core.inRange(
                hsv,
                new Scalar(mHMin, mSMin, mVMin),
                new Scalar(mHMax, mSMax, mVMax),
                mask
        );

        images.clear();
        images.add(hsv);
        image.create(hsv.size(), hsv.depth());
        images.add(image);
        Core.mixChannels(images, images, from_to);
    }

    /**
     * 添加跟随的监听
     *
     * @param listener listener
     */
    public void setOnCamShiftListener(OnCamShiftListener listener) {
        mOnCamShiftListener = listener;
    }
}
