package com.haohai.platform.fireforestplatform.utils;

import com.haohai.platform.fireforestplatform.ui.bean.VideoDeleteModel;

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
    public static float locationRadius = 0;
    public static int locationType = 0;
    public static long locationTime = 0;
    public static boolean hasSensor = false;
    public static boolean hasSign = false;
    public static boolean hasGet = false;
    public static int walkDistance = 0;
    public static int dis_int = 0;
    public static String geoWebKey = "ed0b631b12878335d9a9abd987ceac75";
    public static String geoServiceId = "";
    public static String geoTerminalId = "";
    public static String geoTraceId = "";
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

    public static double lngAdding = 0;
    public static double latAdding = 0;
    public static long longAdding = 0;

    public static int mainTabIndex = 0;
    public static String search;

    public static void clear() {
        token = "";
        lat = 0;
        lng = 0;
        lat_old = 0;
        lng_old = 0;
        locationRadius = 0;
        locationType = 0;
        locationTime = 0;
        hasSensor = false;
        geoServiceId = "";
        geoTerminalId = "";
        geoTraceId = "";
    }
}
