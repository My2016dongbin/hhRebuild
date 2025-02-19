package com.haohai.platform.fireforestplatform.utils;

import com.baidu.trace.LBSTraceClient;
import com.haohai.platform.fireforestplatform.ui.bean.VideoDeleteModel;
import com.haohai.platform.fireforestplatform.ui.multitype.Alarm;
import com.haohai.platform.fireforestplatform.ui.multitype.Danger;
import com.haohai.platform.fireforestplatform.ui.multitype.Message;
import com.haohai.platform.fireforestplatform.ui.multitype.Safety;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.ui.multitype.Upload;
import com.haohai.platform.fireforestplatform.ui.multitype.WorkerDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qc
 * on 2023/7/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CommonData {
    public static String pushFlag = "sbyj";//对应平台配置项目标签 http://192.168.1.88:8082/#/resource/appVersion

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
    public static boolean hasMainVideo = false;
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

    public static TaskList taskLists;
    public static Danger danger;
    public static Message message;
    public static Safety safety;
    public static Upload upload;

    public static Alarm alarm;

    public static void clear() {
        token = "";
        lat = 0;
        lng = 0;
        hasSensor = false;
    }


    public static List<WorkerDetail> workerDetails = new ArrayList<>();
}
