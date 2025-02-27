/**
 * com.genew.nudemo
 * MsgActivity.java
 * 2020-10-29 liangjiahao
 * Copyright (c) Genew Technologies 2010-2016. All rights reserved.
 */
package com.haohai.platform.fireforestplatform.nusdk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.genew.mpublic.base.BaseFragment;
import com.genew.mpublic.router.Router;
import com.haohai.platform.fireforestplatform.R;

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
public class MsgActivity extends AppCompatActivity
{

	@Override
	public void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		setContentView(R.layout.activity_msg);
		BaseFragment conversationFragment = Router.getConversationFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.msg_fragment_container, conversationFragment);
		fragmentTransaction.commit();
	}

}
