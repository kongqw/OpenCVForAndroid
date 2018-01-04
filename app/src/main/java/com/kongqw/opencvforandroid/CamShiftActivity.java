package com.kongqw.opencvforandroid;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kongqw.opencv.listener.OnCamShiftListener;
import com.kongqw.opencvforandroid.view.CamShiftView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class CamShiftActivity extends AppCompatActivity implements OnCamShiftListener {

    private CamShiftView mCamShiftView;
    private ImageView mImageViewTarget;
    private ImageView mImageViewBackProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_cam_shift);
        initView();

        mCamShiftView.setOnCamShiftListener(this);
    }

    private void initView() {
        mCamShiftView = findViewById(R.id.cam_shift_view);
        mImageViewTarget = findViewById(R.id.iv_target);
        mImageViewBackProject = findViewById(R.id.iv_back_project);
    }

    @Override
    public void onTarget(final Mat target) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(target.width(), target.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(target, bitmap);
                mImageViewTarget.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onCalcBackProject(final Mat backProject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(backProject.width(), backProject.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(backProject, bitmap);
                mImageViewBackProject.setImageBitmap(bitmap);
            }
        });
    }
}
