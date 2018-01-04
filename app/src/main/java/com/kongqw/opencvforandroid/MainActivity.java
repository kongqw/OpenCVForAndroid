package com.kongqw.opencvforandroid;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kongqw.permissionslibrary.PermissionsManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mButtonInitCameraView;
    private Button mButtonHogPeopleDetect;
    private Button mButtonCamShift;
    private PermissionsManager mPermissionsManager;

    // 要校验的权限
    private static final String[] PERMISSIONS_CAMERA = new String[]{Manifest.permission.CAMERA};

    private static final int REQUEST_CODE_INIT_CAMERA = 0;
    private static final int REQUEST_CODE_HOG_PEOPLE_DETECT = 1;
    private static final int REQUEST_CODE_CAM_SHIFT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        mButtonInitCameraView.setOnClickListener(this);
        mButtonHogPeopleDetect.setOnClickListener(this);
        mButtonCamShift.setOnClickListener(this);

        // 初始化
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                switch (requestCode) {
                    case REQUEST_CODE_INIT_CAMERA:
                        startActivity(new Intent(MainActivity.this, OpenCVCameraActivity.class));
                        break;
                    case REQUEST_CODE_HOG_PEOPLE_DETECT:
                        startActivity(new Intent(MainActivity.this, HogPeopleDetectActivity.class));
                        break;
                    case REQUEST_CODE_CAM_SHIFT:
                        startActivity(new Intent(MainActivity.this, CamShiftActivity.class));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                Toast.makeText(getApplicationContext(), requestCode + " ： 有权限没有通过！需要授权", Toast.LENGTH_SHORT).show();
                for (String permission : lacksPermissions) {
                    Log.i(TAG, "noAuthorization: " + permission);
                }
            }

            @Override
            public void ignore(int requestCode) {
                // Android 6.0 以下系统不校验
                authorized(requestCode);
            }
        };
    }

    private void initView() {
        mButtonInitCameraView = findViewById(R.id.btn_init_camera_view);
        mButtonHogPeopleDetect = findViewById(R.id.btn_hog_people_detect);
        mButtonCamShift = findViewById(R.id.btn_cam_shift);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_init_camera_view: // OpenCV Camera View 相关设置
                mPermissionsManager.checkPermissions(REQUEST_CODE_INIT_CAMERA, PERMISSIONS_CAMERA);
                break;
            case R.id.btn_hog_people_detect: // HOG 行人检测
                mPermissionsManager.checkPermissions(REQUEST_CODE_HOG_PEOPLE_DETECT, PERMISSIONS_CAMERA);
                break;
            case R.id.btn_cam_shift: // CamShift 目标跟随
                mPermissionsManager.checkPermissions(REQUEST_CODE_CAM_SHIFT, PERMISSIONS_CAMERA);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 用户做出选择以后复查权限，判断是否通过了权限申请
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }
}
