package kong.qingwei.opencv320;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 目标检测
     *
     * @param view view
     */
    public void onDetecting(View view) {
    }

    /**
     * 目标追踪
     *
     * @param view view
     */
    public void onTracking(View view) {
        startActivity(new Intent(this, ObjectTrackingActivity.class));
    }
}
