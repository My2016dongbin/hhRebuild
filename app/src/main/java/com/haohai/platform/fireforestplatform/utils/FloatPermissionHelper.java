package com.haohai.platform.fireforestplatform.utils;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class FloatPermissionHelper {

    /**
     * 是否有悬浮窗权限
     */
    public static boolean hasOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 跳转系统悬浮窗权限设置页
     */
    public static void requestOverlayPermission(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否支持后台启动 Activity（Android 10+）
     */
    public static boolean canStartActivityFromBackground(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps.checkOpNoThrow("android:background_activity_starts",
                        android.os.Process.myUid(), context.getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                return true;
            }
        }
        return true;
    }

    /**
     * 跳转系统设置页（App详情页）
     */
    public static void openAppDetailSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转自启动权限设置页（适配主流厂商）
     */
    public static void openAutoStartSetting(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String manufacturer = Build.MANUFACTURER.toLowerCase();

        try {
            switch (manufacturer) {
                case "xiaomi":
                    intent.setComponent(new ComponentName("com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    break;
                case "huawei":
                    intent.setComponent(new ComponentName("com.huawei.systemmanager",
                            "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"));
                    break;
                case "oppo":
                    intent.setComponent(new ComponentName("com.coloros.safecenter",
                            "com.coloros.safecenter.startupapp.StartupAppListActivity"));
                    break;
                case "vivo":
                    intent.setComponent(new ComponentName("com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
                    break;
                case "meizu":
                    intent.setComponent(new ComponentName("com.meizu.safe",
                            "com.meizu.safe.permission.SmartBGActivity"));
                    break;
                default:
                    openAppDetailSetting(context);
                    return;
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            openAppDetailSetting(context);
        }
    }

    /**
     * 判断是否是国产系统（简化判断）
     */
    public static boolean isDomesticRom() {
        String brand = Build.BRAND.toLowerCase();
        return brand.contains("xiaomi") || brand.contains("huawei") || brand.contains("oppo")
                || brand.contains("vivo") || brand.contains("meizu");
    }
} 
