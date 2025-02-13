/**
 * com.genew.base.permission
 * PermissionsUtil.java
 * 2020-12-07 liangjiahao
 * Copyright (c) Genew Technologies 2010-2016. All rights reserved.
 */
package com.haohai.platform.fireforestplatform.nusdk.permission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;

import com.blankj.utilcode.util.Utils;
import com.genew.base.permission.PermissionActivity;
import com.genew.base.permission.PermissionsUtil.TipInfo;
import com.genew.base.permission.WriteSettingsPermissionActivity;
import com.haohai.platform.fireforestplatform.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * TODO Add class comment here<p/>
 * @version 1.0.0
 * @since 1.0.0
 * @author liangjiahao
 * @history<br/>
 * ver    date       author desc
 * 1.0.0  2020-12-07 liangjiahao created<br/>
 * <p/>
 */
public class PermissionsUtil
{
	public static final String TAG = "PermissionGrantor";
	private static final Map<String, PermissionListener> listenerMap = new ConcurrentHashMap<>();

	public static void requestRecordAudioPermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.RECORD_AUDIO);
	}

	public static void requestCameraPermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.CAMERA);
	}

	public static void requestCameraAndAudioPermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO);
	}

	public static void requestGpsPermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION);
	}

	public static void requestReadStoragePermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.READ_EXTERNAL_STORAGE);
	}

	public static void requestWriteStoragePermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}

	public static void requestReadAndWriteStoragePermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}

	public static void requestWriteStorageAndRecorderPermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO);
	}

	public static void requestNecessaryPermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}

	public static void requestPhoneCallNecessaryPermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.RECORD_AUDIO,
				Manifest.permission.CAMERA);
	}

	public static void requestLocalContactPermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.READ_CONTACTS);
	}

	public static void requestPhoneStatePermission(PermissionListener listener)
	{
		requestPermission(Utils.getApp(),listener, Manifest.permission.READ_PHONE_STATE);
	}
	/**
	 * 申请授权，当用户拒绝时，会显示默认一个默认的Dialog提示用户
	 * @param context
	 * @param listener
	 * @param permission 要申请的权限
	 */
	public static void requestPermission(Context context, PermissionListener listener, String... permission) {
		requestPermission(context, listener, permission, true, null);
	}

	/**
	 *  申请授权，当用户拒绝时，可以设置是否显示Dialog提示用户，也可以设置提示用户的文本内容
	 * @param context
	 * @param listener
	 * @param permission 需要申请授权的权限
	 * @param showTip 当用户拒绝授权时，是否显示提示
	 * @param tip 当用户拒绝时要显示Dialog设置
	 */
	public static void requestPermission(@NonNull Context context, @NonNull PermissionListener listener
			, @NonNull String[] permission, boolean showTip, @Nullable TipInfo tip) {

		if (listener == null) {
			Log.e(TAG, "listener is null");
			return;
		}

		if (PermissionsUtil.hasPermission(context, permission)) {
			listener.permissionGranted(permission);
		} else {
			if (Build.VERSION.SDK_INT < 23) {
				listener.permissionDenied(permission);
			} else {
				String key = String.valueOf(System.currentTimeMillis());
				listenerMap.put(key, listener);
				Intent intent = new Intent(context, PermissionActivity.class);
				intent.putExtra("permission", permission);
				intent.putExtra("key", key);
				intent.putExtra("showTip", showTip);
				intent.putExtra("tip", tip);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Log.d(TAG, "requestPermission startActivity");
				context.startActivity(intent);
			}
		}
	}

	public static void requestWriteSettingsPermission(@NonNull Context context,TipInfo tipInfo)
	{
		if(Build.VERSION.SDK_INT >= 23)
		{
			Intent intent = new Intent(context, WriteSettingsPermissionActivity.class);
			if(null == tipInfo)
			{
				String defaultTitle = context.getResources().getString(R.string.permission_require);
				String defaultContent = context.getResources().getString(R.string.write_settings_permission_default_notice);
				String defaultCancel = context.getResources().getString(R.string.permission_default_cancel);
				String defaultEnsure = context.getResources().getString(R.string.permission_default_ensure);
				tipInfo = new TipInfo(defaultTitle,defaultContent,defaultCancel,defaultEnsure);
			}
			intent.putExtra("tip", tipInfo);
			intent.putExtra("actions",Settings.ACTION_MANAGE_WRITE_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.d(TAG, "request WriteSettings  Permission startActivity");
			context.startActivity(intent);
		}
	}

	public static void requestDrawOverlaysPermission(TipInfo tipInfo)
	{
		if(Build.VERSION.SDK_INT >= 23 &&!Settings.canDrawOverlays(Utils.getApp()))
		{
			Intent intent = new Intent(Utils.getApp(), WriteSettingsPermissionActivity.class);
			if(null == tipInfo)
			{
				String defaultTitle = Utils.getApp().getResources().getString(R.string.permission_require);
				String defaultContent = Utils.getApp().getResources().getString(R.string.permission_manage_overlay_default_notice);
				String defaultCancel = Utils.getApp().getResources().getString(R.string.permission_default_cancel);
				String defaultEnsure = Utils.getApp().getResources().getString(R.string.permission_default_ensure);
				tipInfo = new TipInfo(defaultTitle,defaultContent,defaultCancel,defaultEnsure);
			}
			intent.putExtra("tip", tipInfo);
			intent.putExtra("actions",Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.d(TAG, "request DrawOverlays  Permission startActivity");
			Utils.getApp().startActivity(intent);
		}
	}



	/**
	 * 判断权限是否授权
	 * @param context
	 * @param permissions
	 * @return
	 */
	public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {

		if (permissions.length == 0) {
			return false;
		}

		for (String per : permissions ) {
			int result = PermissionChecker.checkSelfPermission(context, per);
			if ( result != PermissionChecker.PERMISSION_GRANTED) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 判断一组授权结果是否为授权通过
	 * @param grantResult
	 * @return
	 */
	public static boolean isGranted(@NonNull int... grantResult) {

		if (grantResult.length == 0) {
			return false;
		}

		for (int result : grantResult) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 跳转到当前应用对应的设置页面
	 * @param context
	 */
	public static void gotoSetting(@NonNull Context context) {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + context.getPackageName()));
		context.startActivity(intent);
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	static PermissionListener fetchListener(String key) {
		return listenerMap.remove(key);
	}


	/*public static class TipInfo implements Serializable
	{

		private static final long serialVersionUID = 1L;

		String title;
		String content;
		String cancel;  //取消按钮文本
		String ensure;  //确定按钮文本

		public TipInfo ( @Nullable String title,  @Nullable String content,  @Nullable String cancel,  @Nullable String ensure) {
			this.title = title;
			this.content = content;
			this.cancel = cancel;
			this.ensure = ensure;
		}
	}*/
}
