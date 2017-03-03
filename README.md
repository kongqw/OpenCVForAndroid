# 说明

运行Demo前请先安装 [OpenCV Manager](https://github.com/kongqw/KqwFaceDetectionDemo/tree/master/OpenCVManager) **(必须！！)**
并给Demo授予CAMERA权限

[![](https://jitpack.io/v/kongqw/FaceDetectLibrary.svg)](https://jitpack.io/#kongqw/FaceDetectLibrary)


------------

## 部署

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

``` gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

``` gradle
dependencies {
        compile 'com.github.kongqw:FaceDetectLibrary:1.2.1'
}
```


Step 3. Add it in your app build.gradle

``` gradle
android {
    ……
    packagingOptions {
        exclude 'META-INF/services/javax.annotation.processing.Processor'
        pickFirst 'org/bytedeco/javacpp/macosx-x86_64/libusb-1.0.dylib'
        pickFirst 'META-INF/maven/org.bytedeco.javacpp-presets/opencv/pom.properties'
        pickFirst 'META-INF/maven/org.bytedeco.javacpp-presets/opencv/pom.xml'
        pickFirst 'META-INF/maven/org.bytedeco.javacpp-presets/ffmpeg/pom.properties'
        pickFirst 'META-INF/maven/org.bytedeco.javacpp-presets/ffmpeg/pom.xml'
    }
}
```

Step 4. Set screen orientation in your AndroidManifest.xml
``` xml
<?xml version="1.0" encoding="utf-8"?>
<manifest ……>

    <application
        ……>
        <activity
            ……
            android:screenOrientation="landscape">
            ……
        </activity>
    </application>

</manifest>
```

## XML

``` xml
<com.kongqw.view.CameraFaceDetectionView
        android:id="@+id/cameraFaceDetectionView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

## Activity

### 添加人脸检测监听

``` java
CameraFaceDetectionView cameraFaceDetectionView = (CameraFaceDetectionView) findViewById(R.id.cameraFaceDetectionView);
cameraFaceDetectionView.setOnFaceDetectorListener(new CameraFaceDetectionView.OnFaceDetectorListener() {
    @Override
    public void onFace(Mat mat, Rect rect) {

    }
});
```

### 添加加载OpenCV的监听

``` java
mCameraFaceDetectionView.setOnOpenCVInitListener(new OnOpenCVInitListener() {
    @Override
    public void onLoadSuccess() {
        Log.i(TAG, "onLoadSuccess: ");
    }

    @Override
    public void onLoadFail() {
        Log.i(TAG, "onLoadFail: ");
    }

    @Override
    public void onMarketError() {
        Log.i(TAG, "onMarketError: ");
    }

    @Override
    public void onInstallCanceled() {
        Log.i(TAG, "onInstallCanceled: ");
    }

    @Override
    public void onIncompatibleManagerVersion() {
        Log.i(TAG, "onIncompatibleManagerVersion: ");
    }

    @Override
    public void onOtherError() {
        Log.i(TAG, "onOtherError: ");
    }
});
```

### 加载OpenCV

``` java
mCameraFaceDetectionView.loadOpenCV(getApplicationContext());
```

### 切换摄像头

``` java
mCameraFaceDetectionView.switchCamera();
```

### 保存人脸特征

``` java
boolean isSave = FaceUtil.saveImage(Context context, Mat mat, Rect rect, String fileName);
```

### 删除人脸特征

``` java
boolean isSave = FaceUtil.deleteImage(Context context, String fileName);
```

### 提取人脸特征

> 用于显示，对比直接调用 FaceUtil.compare 即可。

``` java
Bitmap bitmap = FaceUtil.getImage(Context context, String fileName);
```

### 人脸识别（特征对比）

> 特征文件不存在没有抛异常，返回-1.

``` java
double score = FaceUtil.compare(Context context, String fileName1, String fileName2);
```


