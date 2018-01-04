package com.kongqw.opencvforandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.kongqw.opencvforandroid.view.HogPeopleDetectView;

public class HogPeopleDetectActivity extends AppCompatActivity {

    private HogPeopleDetectView mHogPeopleDetectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_hog_people_detect);

        initView();

        mHogPeopleDetectView.setResolution(480, 240);
    }

    private void initView() {
        mHogPeopleDetectView = findViewById(R.id.ocv_hog);
    }
}
