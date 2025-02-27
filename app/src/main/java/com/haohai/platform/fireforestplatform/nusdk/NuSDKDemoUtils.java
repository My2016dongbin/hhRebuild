package com.haohai.platform.fireforestplatform.nusdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * TODO Add class comment here<p/>
 *
 * @author huanghui
 * @version 1.0.0
 * @history<br/> ver    date       author desc
 * 1.0.0  2019/8/31 huanghui created<br/>
 * <p/>
 * @since 1.0.0
 */
public class NuSDKDemoUtils
{
    /**
     * 获取当前版本号
     *
     * @return
     */
    public static String getCurrentVersion(Context context)
    {
        try
        {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return "未知版本";
    }

    public static String getVersionName(Context context){
        return "NUSDK V" + getCurrentVersion(context);
    }
}
