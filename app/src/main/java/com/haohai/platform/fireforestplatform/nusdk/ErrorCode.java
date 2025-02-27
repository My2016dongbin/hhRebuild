/**
 * com.genew.nudemo
 * ErrorCode.java
 * 2020-10-28 liangjiahao
 * Copyright (c) Genew Technologies 2010-2016. All rights reserved.
 */
package com.haohai.platform.fireforestplatform.nusdk;

import android.util.SparseArray;

/**
 * TODO Add class comment here<p/>
 * @version 1.0.0
 * @since 1.0.0
 * @author liangjiahao
 * @history<br/>
 * ver    date       author desc
 * 1.0.0  2020-10-28 liangjiahao created<br/>
 * <p/>
 */
public class ErrorCode
{
	private static final SparseArray<String> errorString = new SparseArray<String>();

	public static final String SUCCESS = "请求成功";

	static {
		errorString.put(100000, "用户名密码不正确");
		errorString.put(-1, "系统繁忙，请稍后再试");
		errorString.put(0, SUCCESS);
		errorString.put(100, "请求失败，无效的Token");
		errorString.put(101, "请求失败，无操作权限");
		errorString.put(200, "参数无效");
	}

	public static String getNiuxinErrorString(int errorCode) {
		String errString = errorString.get(errorCode);
		if (errString == null)
			errString = "无法连接服务器";
		return errString;
	}
}
