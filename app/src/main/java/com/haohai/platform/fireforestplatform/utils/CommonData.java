package com.haohai.platform.fireforestplatform.utils;

import com.baidu.trace.LBSTraceClient;
import com.haohai.platform.fireforestplatform.ui.bean.VideoDeleteModel;
import com.haohai.platform.fireforestplatform.ui.multitype.CallingList;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;

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

    public static double lngAdding = 0;
    public static double latAdding = 0;
    public static long longAdding = 0;

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


    //网易云信
    public static String wyyAccId = "";
    public static String wyyToken = "";
    public static String audioRoomName = "";
    public static String audioRoomId = "";
    public static String xdChannelId = "";
    public static InvitedEvent invitedEvent;//被邀请事件
    public static List<InviteParamBuilder> invitedReqList = new ArrayList<>();//主动邀请请求
    public static List<CallingList> invitedUserList = new ArrayList<>();//主动邀请人列表
    public static List<CallingList> invitedUserListForDelete = new ArrayList<>();//主动邀请人列表copy

}
