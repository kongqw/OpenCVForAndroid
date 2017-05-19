package kong.qingwei.opencv320;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.kongqw.permissionslibrary.PermissionsManager;

public class MainActivity extends BaseActivity {

    private PermissionsManager mPermissionsManager;

    // 要校验的权限
    private final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    // 识别请求码
    private final int REQUEST_CODE_DETECTION = 0;
    // 追踪请求码
    private final int REQUEST_CODE_TRACK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 动态权限校验
        mPermissionsManager = new PermissionsManager(this) {

            @Override
            public void authorized(int requestCode) {
                // 权限通过
                switch (requestCode) {
                    case REQUEST_CODE_DETECTION:
                        startActivity(new Intent(MainActivity.this, ObjectDetectingActivity.class));
                        break;
                    case REQUEST_CODE_TRACK:
                        startActivity(new Intent(MainActivity.this, ObjectTrackingActivity.class));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                // 缺少必要权限
                showPermissionDialog();
            }

            @Override
            public void ignore(int requestCode) {
                // Android 6.0 以下系统不校验
                authorized(requestCode);
            }
        };
    }

    /**
     * 复查权限
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 用户做出选择以后复查权限，判断是否通过了权限申请
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }

    /**
     * 目标检测
     *
     * @param view view
     */
    public void onDetecting(View view) {
        // 检查权限
        mPermissionsManager.checkPermissions(REQUEST_CODE_DETECTION, PERMISSIONS);
    }

    /**
     * 目标追踪
     *
     * @param view view
     */
    public void onTracking(View view) {
        // 检查权限
        mPermissionsManager.checkPermissions(REQUEST_CODE_TRACK, PERMISSIONS);
    }
}
