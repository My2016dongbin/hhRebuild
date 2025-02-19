package com.haohai.platform.fireforestplatform.utils;

import com.baidu.trace.LBSTraceClient;
import com.haohai.platform.fireforestplatform.ui.bean.VideoDeleteModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qc
 * on 2023/7/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CommonData {
    public static String pushFlag = "wwyt";//对应平台配置项目标签 http://192.168.1.88:8082/#/resource/appVersion

    public static String token = "";
    public static double lat = 0;
    public static double lng = 0;
    public static double lat_old = 0;
    public static double lng_old = 0;
    public static boolean hasSensor = false;
    public static boolean hasSign = false;
    public static boolean hasGet = false;
    public static int walkDistance = 0;
    public static int dis_int = 0;
    public static String warnType = "";
    public static boolean isUpdate = false;

    public static boolean hasMainApp = true;
    public static boolean hasMainVideo = true;
    public static boolean hasMainMessage = true;
    public static boolean hasMainMap = true;
    public static boolean hasMainMy = true;

    public static int versionCode = 0;
    public static int versionCodeService = 0;

    public static int videoAddingIndex = 0;
    public static int videoDeleteIndex = 0;
    public static String videoDeleteMonitorId = "";
    public static String videoDeleteChannelId = "";
    public static List<Integer> videoPlayingIndexList = new ArrayList<>();
    public static List<VideoDeleteModel> videoDeleteModelList = new ArrayList<>();

    //百度鹰眼轨迹
    public static LBSTraceClient mTraceClient;

    public static int mainTabIndex = 0;
    public static String search;

    public static void clear() {
        token = "";
        lat = 0;
        lng = 0;
        hasSensor = false;
    }

    //大华乐橙
    public static String SECRET = "721eb68686854c48a492bbea68ff22";//"2e4c74dd0786457eaee3d8d1f7a961";
    public static String APPID = "lce9ac1981165f43ba";//"lccb9f39c3fa3343a2";
    public static String daHuaTokenStr = "";
    public static String daHuaId = "";
    public static JSONObject device;
    public static String subAccount;
    public static String subOpenid;
    public static String subToken;
    public static String subId;
    public static JSONObject deviceSub;
    public static JSONArray deviceSubList;
}
