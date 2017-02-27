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
        compile 'com.github.kongqw:FaceDetectLibrary:1.1.1'
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

### 保存人脸特征

``` java
boolean isSave = FaceUtil.saveImage(Context context, Mat mat, Rect rect, String fileName);
```

### 提取人脸特征

``` java
Bitmap bitmap = FaceUtil.getImage(Context context, String fileName);
```

### 人脸识别（特征对比）
``` java
double score = FaceUtil.compare(Context context, String file1, String file2);
```
