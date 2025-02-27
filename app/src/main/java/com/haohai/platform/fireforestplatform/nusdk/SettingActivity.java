/**
 * com.genew.nudemo
 * SettingActivity.java
 * 2020-10-29 liangjiahao
 * Copyright (c) Genew Technologies 2010-2016. All rights reserved.
 */
package com.haohai.platform.fireforestplatform.nusdk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.genew.base.net.base.OnRequestResultListener;
import com.genew.base.net.bean.NiuxinResultInfo;
import com.genew.base.setting.SettingManager;
import com.genew.base.utils.DrawableHelper;
import com.genew.mpublic.bean.OrganizationInfo;
import com.genew.mpublic.router.Router;
import com.genew.mpublic.router.api.Api;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.koushikdutta.ion.Response;


/**
 * TODO Add class comment here<p/>
 * @version 1.0.0
 * @since 1.0.0
 * @author liangjiahao
 * @history<br/>
 * ver    date       author desc
 * 1.0.0  2020-10-29 liangjiahao created<br/>
 * <p/>
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener
{
	private TextView nameTV;
	private TextView positionTV;
	private ImageView ivHeadPicture;
	private TextView tvLoginName;
	private TextView logoutTV;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		setContentView(R.layout.activity_setting);
		init();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		initPerson();
		DrawableHelper.xxxdo(ivHeadPicture,Api.getApiContacts().getHeadPicUrl(Api.getApiAuth().getMyContactInfo().id),0, R.drawable.head_picture_default, 5);
	}

	private void initPerson()
	{
//		CacheImplementation
		if (Api.getApiAuth().getMyContactInfo().displayName != null)
		{
			nameTV.setText(Api.getApiAuth().getMyContactInfo().displayName);
		}

		OrganizationInfo organizationInfo = Api.getApiContacts().getDefaultOrganizationInfo();
		if (organizationInfo != null)
		{
			positionTV.setText(organizationInfo.name);
		}
	}

	private void init() {
		setOnClickListener( R.id.tv_user_setting);
		setOnClickListener(R.id.tv_sip_phone_setting);
		setOnClickListener(R.id.tv_security_setting);
		setOnClickListener(R.id.iv_head_picture);
		setOnClickListener(R.id.tv_logout);
		setOnClickListener(R.id.user_info);


		logoutTV = (TextView) findViewById(R.id.tv_logout);
		nameTV = (TextView) findViewById(R.id.user_name);
		positionTV = (TextView) findViewById(R.id.user_position);
		ivHeadPicture = (ImageView) findViewById(R.id.iv_head_picture);
		ivHeadPicture.setOnClickListener(this);
		tvLoginName = (TextView) findViewById(R.id.tv_login_name);
		tvLoginName.setText(Api.getApiAuth().getMyContactInfo().name);
	}

	private void setOnClickListener(int id)
	{
		findViewById(id).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		int id = v.getId();

		switch (id) {
		case R.id.tv_sip_phone_setting:
			Api.getApPhoneUi().startSipSettingActivity(this);
			break;
		case R.id.tv_security_setting:
			Router.startAuthSecuritySettingActivity();
			break;
		case R.id.user_info:
		case R.id.iv_head_picture:
			Router.startAuthUserSettingActivity();
			break;
		case R.id.tv_logout:
			logoutApp();
			break;
		default:
			break;
		}
	}

	private void logoutApp() {
		//			SettingManager.getInstance().setValue(SettingKey.FTP_USER, "");
		SettingManager
				.getInstance().setValue(SettingManager.SettingKey.FTP_PWD, "");
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.setMessage(getString(R.string.main_logout_processing));
		dialog.show();
		System.out.println("aaaa----------------------------开始注销");
		Api.getApiAuth().logout(logoutListener);
	}

	/**
	 * 注销回调
	 */
	private final OnRequestResultListener logoutListener = new OnRequestResultListener() {
		@Override
		public void onResult(Response<String> response, NiuxinResultInfo info) {
			dialog.dismiss();
			startActivity(new Intent(SettingActivity.this, LoginActivity.class));
			finish();
		}
	};
}
