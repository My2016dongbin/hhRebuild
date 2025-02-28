package com.haohai.platform.fireforestplatform.constant;

public class URLConstant {

    public static final String BASE_PATH = "http://192.168.1.34:8012/";//Release 测试服务器
//    public static final String BASE_PATH = "http://120.221.19.46:10000/";//Release-穿透
//    public static final String BASE_PATH = "http://192.168.1.40:8011/";//Release web

    public static final String SATELLITE_IMAGE = "http://120.221.19.46:10000";
    public static final String COMMON_VERSION = "http://192.168.1.88:8082/auth/api/androidUpgrade/page";

    public static final String PERMISSION_MAIN = BASE_PATH + "auth/api/auth/auth/user/auth";
    public static final String PERMISSION_PER = BASE_PATH + "auth/api/auth/auth/list/element/from/menu";

    //融合通信相关debug
    //public static final String NU_SDK_IP = "123.139.88.18";
    //public static final String NU_SDK_PORT = "8089";
    //public static final String NU_SDK_ACCOUNT = "ceshi08";
    //public static final String NU_SDK_PASSWORD = "Nucleus!123";

    //融合通信相关release
    public static final String NU_SDK_IP = "119.167.67.162";
    public static final String NU_SDK_PORT = "8089";
    public static final String NU_SDK_ACCOUNT = "ceshi08";
    public static final String NU_SDK_PASSWORD = "Nucleus!123";



    //接口请求状态
    public static final String STATES_SUCCESS = "success";   // 密码登录
    public static final String STATES_ERROR =  "error";   // 密码登录
    /*****1.测试*****/
    public static final String POST_TEST = BASE_PATH + "common/test";   // 1.1发送验证码

    public static final String GET_USER_DISTANCE = BASE_PATH + "oa/api/trajectory/getUserTrajectoryMeters";//"oa/api/trajectory/userDayTotalDistance";//获取用户巡护距离

    //登录相关
    public static final String GET_LOGIN = BASE_PATH + "auth/oauth/token";//登录
    public static final String GET_USER_INFO = BASE_PATH + "auth/api/auth/user/get/userinfo";//个人信息
    
    public static final String GET_VERSION = BASE_PATH + "auth/api/androidUpgrade/getCurrent";//版本信息
    //地图相关
    public static final String POST_MAP_ONE_BODY = BASE_PATH + "fire/api/monitorFirealarm/page?type=appInternet";//一体机报警数据
    public static final String GET_MAP_FIRE_VIDEO_URL = BASE_PATH + "fire/api/monitorFirealarm/getMonitorAlarmById?type=appInternet";//一体机报警详情
//    public static final String POST_MAP_SATELLITE = BASE_PATH + "fire/api/satelliteFirealarm/list";//卫星报警数据
    public static final String POST_MAP_SATELLITE = BASE_PATH + "fire/api/satelliteFirealarm/getFirePage?fireIds=&gridNo=370214";//卫星报警数据NEW
    public static final String POST_MAP_RESOURCE_TYPE = BASE_PATH + "resource/api/resourceList/list";//资源类型列表
    public static final String POST_MAP_RESOURCE_TYPE_NEW = BASE_PATH + "resource/api/resourceType/list";//资源类型列表-新增
    public static final String POST_MAP_RESOURCE_LIST_NEW = BASE_PATH + "resource/api/commonResource/list";//按类型查询资源列表-新增
    public static final String POST_MAP_RESOURCE_LIST_START = BASE_PATH + "resource";//按类型查询资源列表-前缀 （前缀+ApiUrl+后缀）
    public static final String POST_MAP_RESOURCE_LIST_END = "/list";//类型查询资源列表-后缀 （前缀+ApiUrl+后缀）
    public static final String GET_MAP_RESOURCE_DETAIL_MONITOR = BASE_PATH + "resource/api/monitor";//资源详情查询-摄像机
    public static final String GET_RESOURCE_SEARCH = BASE_PATH + "resource/api/resourceList/searchResources";//资源搜索
    public static final String GET_GRID_BY_LEVEL = BASE_PATH + "resource/api/grid/getAreaDataAndPeople";//获取区3/街道4/社区5网格 ?level=3&gridNo=370214
    public static final String GET_GRID_LINE = BASE_PATH + "auth/api/sysAreaGeoInfo/getMapJson";//获取区3/街道4/社区5网格下gridNo边界 ?gridNo=370214&mapType=3

    public static final String POST_MONITOR_COUNT = BASE_PATH + "resource/api/monitor/getCount";//查询监控设备数量
    public static final String POST_KK_COUNT = BASE_PATH + "resource/api/monitor/getKakouCount";//查询卡口设备数量
    public static final String GET_MONITOR_ONLINE_COUNT = BASE_PATH + "resource/api/camera/getOnlineDevices";//查询监控设备在线数量

    public static final String GET_MAIN_DEVICE_LIST = BASE_PATH + "resource/api/camera/getDevicesByOnlineStatus";//主页查询在线离线设备列表
    public static final String GET_MAIN_SIGN_INFO = BASE_PATH + "oa/api/attendance/myAttRecordByTime";//主页查询签到详情


    public static final String GET_FIRE_COUNT_ROLE = BASE_PATH + "auth/api/auth/auth/user/auth";//查询账号权限
    public static final String GET_FIRE_COUNT_FIRE_IDS = BASE_PATH + "fire/api/monitorFirealarmMessage/getSendMessageByResave";//查询领导账号fireIds
    public static final String GET_FIRE_COUNT = BASE_PATH + "fire/api/Statistic/getLeaderFireAlarmList";//"fire/api/Statistic/getLatestFireAlarmList";//查询报警数量


    public static final String GET_GRID = BASE_PATH + "auth/api/sysArea/getAllSysArea";//获取区域数据
    public static final String POST_POSITION = BASE_PATH + "oa/api/trajectory/trackUpload";//位置上传

    public static final String POST_PICTURE = /*"http://117.132.5.139:8011/"*/ BASE_PATH +"oa/api/workReport/fileUploadAnByNotToken";//上传图片
    public static final String POST_HIDDEN_DANGER = BASE_PATH +"fire/api/dangerCheck";//隐患排查提交
    public static final String POST_FIRE_UPLOAD = BASE_PATH +"fire/api/reportFirealarm";//火情上报提交

    public static final String PUT_CHANGE_PASSWORD = BASE_PATH +"auth/api/auth/user/modfiy/passwd";//修改密码

    public static final String POST_NEWS = BASE_PATH +"auth/api/auth/news/list";//新闻列表
    public static final String POST_NEWS_PAGE = BASE_PATH +"auth/api/auth/news/page?type=appInternet";//新闻列表-分页
    public static final String GET_NEWS_INFO = BASE_PATH +"auth/api/auth/news";//新闻列表详情

    public static final String POST_SUGGESTION = BASE_PATH +"oa/api/complaintsAndSuggestions";//投诉建议提交
    public static final String POST_SUGGESTION_LIST = BASE_PATH +"oa/api/complaintsAndSuggestions/page";//投诉建议查询

    public static final String POST_MESSAGE = BASE_PATH +"fire/api/appMessage/list";//获取消息列表
    public static final String POST_MESSAGE_NEW = BASE_PATH +"fire/api/appMessage/listNew";//NEW获取消息列表
    public static final String GET_CHANGE_STATE_NEW = BASE_PATH +"fire/api/appMessage/readOne";//NEW更改消息已读状态
    // 参数messageId:（消息列表id）
    // foreignId:（监控报警列表id/卫星报警列表id/任务列表报警id/火险等级id）
    // messageType："task")){//任务"monitorFirealarm")) {//监控报警"satelliteFirealarm")){//卫星报警"kkFirealarm")){//卡口报警"fireLevel")){//火警等级
    public static final String POST_MESSAGE_MONITOR = BASE_PATH +"fire/api/monitorFirealarm/list?type=appInternet";//获取监控报警信息列表
    public static final String POST_MESSAGE_SATELLITE = BASE_PATH +"fire/api/satelliteFirealarm/list";//获取卫星报警信息列表
    public static final String POST_MESSAGE_KK = BASE_PATH +"fire/api/kakouAlarm/list";//获取卡口报警信息列表

    public static final String GET_MESSAGE_MONITOR_INFO = BASE_PATH +"fire/api/monitorFirealarm/get?type=appInternet";//获取报警信息详情
    //public static final String PUT_MESSAGE_MONITOR_STATE = BASE_PATH +"fire/api/monitorFirealarm/updateMonitorAlarm";//更改监控报警信息已读状态

    public static final String GET_MESSAGE_SATELLITE_INFO = BASE_PATH +"fire/api/satelliteFirealarm";//获取卫星报警信息详情
    public static final String PUT_MESSAGE_SATELLITE_STATE = BASE_PATH +"fire/api/satelliteFirealarm";//更改卫星报警信息已读状态

    public static final String GET_MESSAGE_KK_INFO = BASE_PATH +"fire/api/kakouAlarm/get";//获取卡口报警信息详情
    public static final String PUT_MESSAGE_KK_STATE = BASE_PATH +"fire/api/kakouAlarm";//更改卡口报警信息已读状态

    public static final String POST_SIGN_STATISTICS = BASE_PATH +"oa/api/attendance/record/month";//考勤统计-列表 {startTime: "2023-12-01 00:00:00", endTime: "2023-12-31 23:59:59", ids: [], attendanceStatus: ""}

    public static final String POST_MESSAGE_LEVEL = BASE_PATH +"fire/api/fireLevelApp/list";//获取火警等级信息列表
    public static final String GET_MESSAGE_LEVEL_INFO = BASE_PATH +"fire/api/fireLevelApp";//获取火警等级信息列表详情
    public static final String PUT_MESSAGE_LEVEL_STATE = BASE_PATH +"";//更改火警等级信息已读状态
    public static final String GET_MESSAGE_CLEAR = BASE_PATH +"fire/api/appMessage/updateUnread";//"fire/api/appMessage/updateUnread";//清除全部未读

    public static final String GET_VIDEO_TREES = BASE_PATH +"resource/api/grid/listGridTreesNew";//视频树 已收藏 "sheetType","collection" /*"NetworkType","Internet"*/
    public static final String GET_VIDEO_LIVE_URL = BASE_PATH +"resource/api/mediaKit/getLiveUrl";//视频流 /*"cameraId",gridCamera.getId(),"manufacturer","2","streamType","2","protocolType","rtsp"*/
    public static final String POST_STREAM = BASE_PATH +"resource/api/mediaKit/streamNotFoundwebHookNew";//流媒体提前手动拉流
    public static final String GET_CONTROL = BASE_PATH +"resource/api/liveVideo/control";//视频设备控制
    public static final String POST_VIDEO_COLLECTION = BASE_PATH +"resource/api/cameraCollection";//收藏 /*PostStar(gridCamera.getId(),gridCamera.getName(),null,gridCamera.getMonitorId())*/
    public static final String DELETE_VIDEO_COLLECTION = BASE_PATH +"resource/api/cameraCollection";//取消收藏

    public static final String GET_GRID_TREES = BASE_PATH +"resource/api/grid/listGridNewTrees";//网格(防火员)
    public static final String POST_GRID_TREES = BASE_PATH +"resource/api/grid/gridUser/list";//网格(防火员)

    public static final String POST_TASK_LIST = BASE_PATH +"oa/api/taskManagement/listNew?type=appInternet";//任务列表
    public static final String GET_TASK_INFO = BASE_PATH +"oa/api/taskManagement?type=appInternet";//获取任务详情
    public static final String PUT_TASK_STATE = BASE_PATH +"oa/api/taskManagement";//改变任务单状态
    public static final String POST_TASK_ROOM = BASE_PATH +"resource/api/meeting/room/plot/step/list";//任务查询房间id
    public static final String GET_TASK_SNAP = BASE_PATH +"resource/api/meeting/room/plot/snap";//任务-根据房间id查询标绘
    public static final String POST_LIVE_UPLOAD = BASE_PATH +"oa/api/taskDetail";//现场上报
    public static final String POST_ONE_BODY_IS_REAL = BASE_PATH +"fire/api/monitorFirealarm/realOrError";//处理一体机火警

    public static final String POST_PUSH_FEEDBACK = BASE_PATH +"resource/api/dispatchCommand/receiveFeedback";//推送反馈



}
