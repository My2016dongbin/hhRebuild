package com.haohai.platform.fireforestplatform.old;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;

import java.util.List;

public class AutoStartActivity extends BaseActivity {
    TextView title;
    TextView left;
    ImageView left_icon;
    TextView tv_go;
    TextView tv_goVivo;
    TextView tv_go3;
    TextView tv_go_float;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主题色白色 黑字
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.whiteColor));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_auto_start);
        title = (TextView) findViewById(R.id.title);
        left = (TextView) findViewById(R.id.left);
        left_icon = (ImageView) findViewById(R.id.left_icon);
        tv_go = (TextView) findViewById(R.id.tv_go);
        tv_goVivo = (TextView) findViewById(R.id.tv_go2);
        tv_go3 = (TextView) findViewById(R.id.tv_go3);
        tv_go_float = (TextView) findViewById(R.id.tv_go_float);
        title.setText("权限开启");
        left.setOnClickListener(v -> {
            onBackPressed();
        });
        left_icon.setOnClickListener(v -> {
            onBackPressed();
        });
        tv_go_float.setOnClickListener(v -> {
            checkOverlayPermission();
        });
        tv_go3.setOnClickListener(v -> {
            gotoAppDetailIntent(AutoStartActivity.this);
        });
        tv_go.setOnClickListener(v -> {
            start();
        });
        tv_goVivo.setOnClickListener(v -> {
            AutoStartActivity.this.startActivity(new Intent(Settings.ACTION_SETTINGS));
        });

    }

    private void start() {
        CommonUtil.enterWhiteListSetting(this);
    }


    /**
     * 跳转到应用详情界面
     */
    public static void gotoAppDetailIntent(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }



    protected boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                List<ResolveInfo> infos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (infos == null || infos.isEmpty()) {
                    return true;
                }
                startActivityForResult(intent,1718);
                return false;
            }else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                List<ResolveInfo> infos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (infos == null || infos.isEmpty()) {
                    return true;
                }
                startActivityForResult(intent,1718);
            }
        }
        return true;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1718) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Log.e("TAG", "onActivityResult granted");
                }
            }
        }
    }
}