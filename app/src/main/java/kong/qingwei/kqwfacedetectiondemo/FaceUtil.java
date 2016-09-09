package kong.qingwei.kqwfacedetectiondemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_imgproc;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCompareHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvNormalizeHist;

//import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
//import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
//import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COMP_CORREL;
//import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COMP_INTERSECT;
//import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HIST_ARRAY;
//import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcHist;
//import static com.googlecode.javacv.cpp.opencv_imgproc.cvCompareHist;
//import static com.googlecode.javacv.cpp.opencv_imgproc.cvNormalizeHist;

//import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
//import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;


/**
 * Created by kqw on 2016/9/9.
 * FaceUtil
 */
public class FaceUtil {

    private Context mContext;
    public FaceUtil(Context context){
        mContext = context;
    }
    /**
     * 特征保存
     *
     * @param image    Mat
     * @param rect     人脸信息
     * @param fileName 文件名字
     * @return 保存是否成功
     */
    public boolean saveImage(Mat image, Rect rect, String fileName) {
        try {
            // 原图置灰
            Mat grayMat = new Mat();
            Imgproc.cvtColor(image, grayMat, Imgproc.COLOR_BGR2GRAY);
            // 把检测到的人脸重新定义大小后保存成文件
            Mat sub = grayMat.submat(rect);
            Mat mat = new Mat();
            Size size = new Size(100, 100);
            Imgproc.resize(sub, mat, size);
            return Highgui.imwrite(getFilePath(fileName), mat);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 提取特征
     *
     * @param fileName 文件名
     * @return 特征图片
     */
    public Bitmap getImage(String fileName) {
        try {
            return BitmapFactory.decodeFile(getFilePath(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 特征对比
     *
     * @param file1 人脸特征
     * @param file2 人脸特征
     * @return 相似度
     */
    public double CmpPic(String file1, String file2) {
        try {
            int l_bins = 256;
            int hist_size[] = {l_bins};
            float v_ranges[] = {0, 255};
            float ranges[][] = {v_ranges};
            opencv_core.IplImage Image1 = cvLoadImage(getFilePath(file1), CV_LOAD_IMAGE_GRAYSCALE);
            opencv_core.IplImage Image2 = cvLoadImage(getFilePath(file2), CV_LOAD_IMAGE_GRAYSCALE);
            opencv_core.IplImage imageArr1[] = {Image1};
            opencv_core.IplImage imageArr2[] = {Image2};
            opencv_imgproc.CvHistogram Histogram1 = opencv_imgproc.CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
            opencv_imgproc.CvHistogram Histogram2 = opencv_imgproc.CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
            cvCalcHist(imageArr1, Histogram1, 0, null);
            cvCalcHist(imageArr2, Histogram2, 0, null);
            cvNormalizeHist(Histogram1, 100.0);
            cvNormalizeHist(Histogram2, 100.0);
            // 参考：http://blog.csdn.net/nicebooks/article/details/8175002
            double c1 = cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL) * 100;
            double c2 = cvCompareHist(Histogram1, Histogram2, CV_COMP_INTERSECT);
            return (c1 + c2) / 2;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * 获取人脸特征路径
     *
     * @param fileName 人脸特征的图片的名字
     * @return 路径
     */
    private String getFilePath(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        try {
            // 内存路径
            return mContext.getFilesDir().getPath() + fileName + ".jpg";
            // 内存卡路径 需要SD卡读取权限
            // return Environment.getExternalStorageDirectory() + "/FaceDetect/" + fileName + ".jpg";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
