package kong.qingwei.kqwfacedetectiondemo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by kongqingwei on 2017/2/15.
 * 检查权限的类
 */

public abstract class PermissionsManager {

    private static final String PACKAGE_URL_SCHEME = "package:";
    private Activity mTargetActivity;

    /**
     * 权限通过
     *
     * @param requestCode 请求码
     */
    public abstract void authorized(int requestCode);

    /**
     * 有权限没有通过
     *
     * @param requestCode      请求码
     * @param lacksPermissions 被拒绝的权限
     */
    public abstract void noAuthorization(int requestCode, String[] lacksPermissions);

    /**
     * Android 6.0 以下的系统不校验权限
     * <p>
     * Android 6.0 以下的系统，只要在清单文件中加入了权限，即使在设置中拒绝，checkSelfPermission也会返回已授权！校验没有意义。
     */
    public abstract void ignore();

    /**
     * 构造方法
     *
     * @param targetActivity 目标Activity 申请权限的Activity
     */
    public PermissionsManager(Activity targetActivity) {
        mTargetActivity = targetActivity;
    }

    /**
     * 检查权限
     *
     * @param requestCode 请求码
     * @param permissions 准备校验的权限
     */
    public void checkPermissions(int requestCode, String... permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0 动态检查权限
            ArrayList<String> lacks = new ArrayList<>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(mTargetActivity.getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
                    lacks.add(permission);
                }
            }

            if (!lacks.isEmpty()) {
                // 有权限没有授权
                String[] lacksPermissions = new String[lacks.size()];
                lacksPermissions = lacks.toArray(lacksPermissions);
                //申请CAMERA权限
                ActivityCompat.requestPermissions(mTargetActivity, lacksPermissions, requestCode);
            } else {
                // 授权
                authorized(requestCode);
            }
        } else {
            // 6.0 以下版本不校验权限
            ignore();
        }
    }

    /**
     * 复查权限
     * <p>
     * 调用checkPermissions方法后，会提示用户对权限的申请做出选择，选择以后（同意或拒绝）
     * TargetActivity会回调onRequestPermissionsResult方法，
     * 在onRequestPermissionsResult回调方法里，我们调用此方法来复查权限，检查用户的选择是否通过了权限申请
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 授权结果
     */
    public void recheckPermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                // 未授权
                noAuthorization(requestCode, permissions);
                return;
            }
        }
        // 授权
        authorized(requestCode);
    }

    /**
     * 进入应用设置
     *
     * @param context context
     */
    public static void startAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
        context.startActivity(intent);
    }
}