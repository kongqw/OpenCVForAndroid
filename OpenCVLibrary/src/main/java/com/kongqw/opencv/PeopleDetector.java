package com.kongqw.opencv;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.HOGDescriptor;

/**
 * Created by Kongqw on 2018/1/4.
 * HOG PeopleDetector
 */

public class PeopleDetector {

    private static final String TAG = PeopleDetector.class.getSimpleName();
    private HOGDescriptor mHOGDescriptor;

    public PeopleDetector() {
        mHOGDescriptor = new HOGDescriptor();
        mHOGDescriptor.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
    }

    /**
     * 人物检测
     *
     * @param img 输入图像
     * @return 检测位置
     */
    public Rect[] detectPeople(Mat img) {
        MatOfRect foundLocations = new MatOfRect();
        MatOfDouble foundWeights = new MatOfDouble();
        mHOGDescriptor.detectMultiScale(
                img, // 输入图像
                foundLocations, // 检测到的位置
                foundWeights, // 检测窗口得分
                0,
                new Size(8, 8), // HoG检测窗口移动时的步长(水平及竖直)。
                new Size(32, 32),  // 在原图外围添加像素，作者在原文中提到，适当的pad可以提高检测的准确率（可能pad后能检测到边角的目标？） 常见的pad size 有(8, 8), (16, 16), (24, 24), (32, 32).
                1.05,  // 通常scale在 1.01 - 1.5 这个区间
                2.0,
                false
        );

        Log.i(TAG, "detectPeople: foundWeights = " + foundWeights.toString());

        return foundLocations.toArray();
    }

    /**
     * 人物检测
     *
     * @param img                  输入图像
     * @param hitThreshold         hitThreshold
     * @param winStride            winStride
     * @param padding              padding
     * @param scale                scale
     * @param finalThreshold       finalThreshold
     * @param useMeanshiftGrouping useMeanshiftGrouping
     * @return
     */
    public Rect[] detectPeople(Mat img, double hitThreshold, Size winStride, Size padding, double scale, double finalThreshold, boolean useMeanshiftGrouping) {
        MatOfRect foundLocations = new MatOfRect();
        MatOfDouble foundWeights = new MatOfDouble();
        mHOGDescriptor.detectMultiScale(
                img, // 输入图像
                foundLocations, // 检测到的位置
                foundWeights, // 检测窗口得分
                hitThreshold,
                winStride, // HoG检测窗口移动时的步长(水平及竖直)。
                padding,  // 在原图外围添加像素，作者在原文中提到，适当的pad可以提高检测的准确率（可能pad后能检测到边角的目标？） 常见的pad size 有(8, 8), (16, 16), (24, 24), (32, 32).
                scale,  // 通常scale在 1.01 - 1.5 这个区间
                finalThreshold,
                useMeanshiftGrouping
        );

        Log.i(TAG, "detectPeople: foundWeights = " + foundWeights.toString());

        return foundLocations.toArray();
    }
}
