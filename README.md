# 说明

[![](https://jitpack.io/v/kongqw/FaceDetectLibrary.svg)](https://jitpack.io/#kongqw/FaceDetectLibrary)

人脸检测 + 人脸识别 的Demo

> aar也已经一并上传，如果导入库有问题，直接导入 [aar](https://github.com/kongqw/KqwFaceDetectionDemo/tree/master/openCVLibrary2411/build/outputs/aar) 即可


运行Demo前请先安装 [OpenCV Manager](https://github.com/kongqw/KqwFaceDetectionDemo/tree/master/OpenCVManager) **(必须！！)**

给Demo授予CAMERA权限

------------



Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        compile 'com.github.kongqw:FaceDetectLibrary:1.1.1'
	}
