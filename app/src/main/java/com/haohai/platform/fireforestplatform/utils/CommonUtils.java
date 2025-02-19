package com.haohai.platform.fireforestplatform.utils;

import com.haohai.platform.fireforestplatform.utils.model.DahuaSubDeviceList;
import com.haohai.platform.fireforestplatform.utils.model.DahuaSubToken;
import com.haohai.platform.fireforestplatform.utils.model.DahuaToken;
import com.haohai.platform.fireforestplatform.utils.model.DahuaTokenInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by qc
 * on 2022/10/27.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CommonUtils {

    public static DahuaToken paramsInit(Map<String, Object> paramsMap) {
        DahuaToken dahuaToken = new DahuaToken();
        long time = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();
        StringBuilder paramString = new StringBuilder();
        paramString.append("time:").append(time).append(",");
        paramString.append("nonce:").append(nonce).append(",");
        paramString.append("appSecret:").append(CommonData.SECRET);
        String sign = "";
        // 计算MD5得值
        sign = MD5Helper.encodeToLowerCase(paramString.toString().trim());
        Map<String, Object> systemMap = new HashMap<String, Object>();
        systemMap.put("ver", "1.0");
        systemMap.put("sign", sign);
        systemMap.put("appId", CommonData.APPID);
        systemMap.put("nonce", nonce);
        systemMap.put("time", time);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("system", systemMap);
        map.put("params", paramsMap);
        map.put("id", id);
        dahuaToken.setSystem(new DahuaToken.System("1.0",CommonData.APPID,sign,time,nonce));
        dahuaToken.setParams(new DahuaToken.Params(paramsMap.get("deviceId")+"",paramsMap.get("code")+"",paramsMap.get("token")+""));
        dahuaToken.setId(id);
        return dahuaToken;
    }

    public static DahuaTokenInfo paramsInitList(Map<String, Object> paramsMap) {
        DahuaTokenInfo dahuaToken = new DahuaTokenInfo();
        long time = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();
        StringBuilder paramString = new StringBuilder();
        paramString.append("time:").append(time).append(",");
        paramString.append("nonce:").append(nonce).append(",");
        paramString.append("appSecret:").append(CommonData.SECRET);
        String sign = "";
        // 计算MD5得值
        sign = MD5Helper.encodeToLowerCase(paramString.toString().trim());
        Map<String, Object> systemMap = new HashMap<String, Object>();
        systemMap.put("ver", "1.0");
        systemMap.put("sign", sign);
        systemMap.put("appId", CommonData.APPID);
        systemMap.put("nonce", nonce);
        systemMap.put("time", time);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("system", systemMap);
        map.put("params", paramsMap);
        map.put("id", id);
        dahuaToken.setSystem(new DahuaTokenInfo.System("1.0",CommonData.APPID,sign,time,nonce));
        List<DahuaTokenInfo.Params.Device> list = new ArrayList<>();
        List<String> listStr = new ArrayList<>();
        listStr.add("0");
        list.add(new DahuaTokenInfo.Params.Device(paramsMap.get("deviceId")+"",listStr));
        dahuaToken.setParams(new DahuaTokenInfo.Params(list,true,paramsMap.get("token")+""));
        dahuaToken.setId(id);
        return dahuaToken;
    }
    public static DahuaTokenInfoDemo paramsInitInfo(Map<String, Object> paramsMap) {
        DahuaTokenInfoDemo dahuaToken = new DahuaTokenInfoDemo();
        long time = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();
        StringBuilder paramString = new StringBuilder();
        paramString.append("time:").append(time).append(",");
        paramString.append("nonce:").append(nonce).append(",");
        paramString.append("appSecret:").append(CommonData.SECRET);
        String sign = "";
        // 计算MD5得值
        sign = MD5Helper.encodeToLowerCase(paramString.toString().trim());
        Map<String, Object> systemMap = new HashMap<String, Object>();
        systemMap.put("ver", "1.0");
        systemMap.put("sign", sign);
        systemMap.put("appId", CommonData.APPID);
        systemMap.put("nonce", nonce);
        systemMap.put("time", time);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("system", systemMap);
        map.put("params", paramsMap);
        map.put("id", id);
        dahuaToken.setSystem(new DahuaTokenInfoDemo.System("1.0",CommonData.APPID,sign,time,nonce));
        dahuaToken.setParams(new DahuaTokenInfoDemo.Params(paramsMap.get("token")+"",(int)paramsMap.get("page"),(int)paramsMap.get("pageSize"),paramsMap.get("source")+""));
        dahuaToken.setId(id);
        return dahuaToken;
    }


    public static DahuaSubToken paramsSubToken(Map<String, Object> paramsMap) {
        DahuaSubToken dahuaToken = new DahuaSubToken();
        long time = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();
        StringBuilder paramString = new StringBuilder();
        paramString.append("time:").append(time).append(",");
        paramString.append("nonce:").append(nonce).append(",");
        paramString.append("appSecret:").append(CommonData.SECRET);
        String sign = "";
        // 计算MD5得值
        sign = MD5Helper.encodeToLowerCase(paramString.toString().trim());
        Map<String, Object> systemMap = new HashMap<String, Object>();
        systemMap.put("ver", "1.0");
        systemMap.put("sign", sign);
        systemMap.put("appId", CommonData.APPID);
        systemMap.put("nonce", nonce);
        systemMap.put("time", time);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("system", systemMap);
        map.put("params", paramsMap);
        map.put("id", id);
        dahuaToken.setSystem(new DahuaSubToken.System("1.0",CommonData.APPID,sign,time,nonce));
        dahuaToken.setParams(new DahuaSubToken.Params(paramsMap.get("token")+"",paramsMap.get("openid")+""));
        dahuaToken.setId(id);
        return dahuaToken;
    }

    public static DahuaSubDeviceList paramsSubDeviceList(Map<String, Object> paramsMap) {
        DahuaSubDeviceList dahuaToken = new DahuaSubDeviceList();
        long time = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();
        StringBuilder paramString = new StringBuilder();
        paramString.append("time:").append(time).append(",");
        paramString.append("nonce:").append(nonce).append(",");
        paramString.append("appSecret:").append(CommonData.SECRET);
        String sign = "";
        // 计算MD5得值
        sign = MD5Helper.encodeToLowerCase(paramString.toString().trim());
        Map<String, Object> systemMap = new HashMap<String, Object>();
        systemMap.put("ver", "1.0");
        systemMap.put("sign", sign);
        systemMap.put("appId", CommonData.APPID);
        systemMap.put("nonce", nonce);
        systemMap.put("time", time);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("system", systemMap);
        map.put("params", paramsMap);
        map.put("id", id);
        dahuaToken.setSystem(new DahuaSubDeviceList.System("1.0",CommonData.APPID,sign,time,nonce));
        dahuaToken.setParams(new DahuaSubDeviceList.Params(paramsMap.get("token")+"",(int)paramsMap.get("pageNo"),(int)paramsMap.get("pageSize")));
        dahuaToken.setId(id);
        return dahuaToken;
    }
}
