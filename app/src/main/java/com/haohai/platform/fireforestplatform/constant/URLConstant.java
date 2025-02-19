package com.haohai.platform.fireforestplatform.constant;

public class URLConstant {

//    public static final String BASE_PATH = "http://192.168.1.42:8082/";//Release 本地
    public static final String BASE_PATH = "http://27.223.102.28:52110/";//Release 生产环境

    public static final String SATELLITE_IMAGE = "http://web.ehaohai.com:2018";//卫星图片前缀
//    public static final String COMMON_VERSION = /*"http://192.168.1.88:8082/"*/BASE_PATH + "auth/api/androidUpgrade/page";
    public static final String COMMON_VERSION = /*"http://192.168.1.88:8082/"*/BASE_PATH + "resource/androidUpgrade/page";


    public static final String PERMISSION_MAIN = BASE_PATH + "auth/api/auth/auth/user/auth";
    public static final String PERMISSION_PER = BASE_PATH + "auth/api/auth/auth/list/element/from/menu";

    //市北应急
//    public static final String POST_LOGIN = BASE_PATH + "prod-api/auth/login";//登录
    public static final String POST_LOGIN = BASE_PATH + "sso/nocode/login";//登录
    public static final String GET_USER_INFO = BASE_PATH + "system/user/getInfo";//个人信息
    public static final String GET_EMERGENCY_PAGE = BASE_PATH + "duty/emergIncident/page";//报警管理-突发事件列表incidentQryType=2&&突发事件审核列表incidentQryType=1&&未报送事件列表incidentQryType=0
    public static final String POST_EMERGENCY_AUDIT = BASE_PATH + "duty/emergIncident/audit";//报警管理-审核按钮
    public static final String GET_EMERGENCY_DETAIL_LIST = BASE_PATH + "duty/emergIncidentVersion/incidentVersionList";//报警管理-突发事件详情-事件列表   ?incidentId=1745637754265534464&pageNum=1&pageSize=10
    public static final String GET_EMERGENCY_DETAIL = BASE_PATH + "duty/emergIncident/getEmergIncidentInfo/";//报警管理-突发事件详情-内部事件记录列表-详情  1745637754265534464/1749718668431982592
    public static final String GET_EMERGENCY_DETAIL_NEW = BASE_PATH + "duty/emergIncident/getEmergIncidentInfo";//报警管理-突发事件详情-详情new
    public static final String GET_EMERGENCY_AUDIT = BASE_PATH + "duty/dutyAuditLog/list";//报警管理-突发事件-审核记录
    public static final String GET_WORK_DAY = BASE_PATH + "duty/duty/dutyByDay";//值班日历-按日查询-网格分组-(领导)
    public static final String GET_WORK_MONTH_GRID = BASE_PATH + "duty/duty/month/";//值班日历-按照网格本月查询-(所有人)
    public static final String POST_PICTURE = BASE_PATH + "system/upload";//文件上传 post请求 form-data格式
    public static final String GET_HIDDEN_DANGER_PAGE = BASE_PATH + "resource/hiddenDanger/page";//隐患排查列表 hiddenType  风险隐患NatuRiskManage 安全生产Rectification
    public static final String GET_TYPE_TREE = BASE_PATH + "resource/baseIncidentType/listTree";//获取事件类型树
    public static final String POST_HIDDEN_DANGER_ADD = BASE_PATH + "resource/hiddenDanger/add";//hiddenType  风险隐患NatuRiskManage 安全生产Rectification
    public static final String POST_HIDDEN_DANGER_REMOVE = BASE_PATH + "resource/hiddenDanger/remove";//hiddenType  风险隐患NatuRiskManage 安全生产Rectification
    public static final String POST_HIDDEN_DANGER_EDIT = BASE_PATH + "resource/hiddenDanger/edit";//hiddenType  风险隐患NatuRiskManage 安全生产Rectification
    public static final String GET_EVENT_UPLOAD_PAGE = BASE_PATH + "duty/emergIncident/page";//事件上报列表 pageNum=1&pageSize=10&incidentQryType=9
    public static final String POST_EVENT_UPLOAD_EDIT = BASE_PATH + "duty/emergIncident/resubmit";//事件上报编辑/续报
    public static final String POST_EVENT_UPLOAD_ADD = BASE_PATH + "duty/emergIncident";//事件上报新增
    public static final String DELETE_EVENT_UPLOAD_DELETE = BASE_PATH + "duty/emergIncident/";//事件上报删除 duty/emergIncident/${id}
    public static final String GET_FIRE_COUNT_SB = BASE_PATH + "duty/emergIncident/selectTotalByHandle";//事件数量查询






    //接口请求状态
    public static final String STATES_SUCCESS = "success";   // 密码登录
    public static final String STATES_ERROR =  "error";   // 密码登录
    /*****1.测试*****/
    public static final String POST_TEST = BASE_PATH + "common/test";   // 1.1发送验证码

    public static final String GET_USER_DISTANCE = BASE_PATH + "oa/api/trajectory/getUserTrajectoryMeters";//"oa/api/trajectory/userDayTotalDistance";//获取用户巡护距离

    //登录相关
    public static final String GET_NAME = BASE_PATH + "auth/api/platformConfig/getDefaultConfigNotLogin";//获取项目名称
    //public static final String GET_LOGIN = BASE_PATH + "auth/oauth/token";//登录
    //public static final String GET_USER_INFO = BASE_PATH + "auth/api/auth/user/get/userinfo";//个人信息
    
    public static final String GET_VERSION = BASE_PATH + "auth/api/androidUpgrade/getCurrent";//版本信息
    //地图相关
    public static final String POST_MAP_ONE_BODY = BASE_PATH + "fire/api/monitorFirealarm/page?type=appInternet";//一体机报警数据
    public static final String GET_MAP_FIRE_VIDEO_URL = BASE_PATH + "fire/api/monitorFirealarm/getMonitorAlarmById?type=appInternet";//一体机报警详情
//    public static final String POST_MAP_SATELLITE = BASE_PATH + "fire/api/satelliteFirealarm/list";//卫星报警数据
    public static final String GET_MAP_SATELLITE = BASE_PATH + "resource/satelliteFireAlarm/list";//卫星报警数据NEW
    public static final String POST_MAP_RESOURCE_TYPE = BASE_PATH + "resource/api/resourceList/list";//资源类型列表
    public static final String POST_MAP_RESOURCE_TYPE_NEW = BASE_PATH + "command/emergeCommand/getResourceType";//资源类型列表-新增
    public static final String POST_MAP_RESOURCE_LIST_NEW = BASE_PATH + "command/emergeCommand/getSideResource";//按类型查询资源列表-新增
    public static final String POST_MAP_RESOURCE_LIST_START = BASE_PATH + "resource";//按类型查询资源列表-前缀 （前缀+ApiUrl+后缀）
    public static final String POST_MAP_RESOURCE_LIST_END = "/list";//类型查询资源列表-后缀 （前缀+ApiUrl+后缀）
    public static final String GET_MAP_RESOURCE_DETAIL_MONITOR = BASE_PATH + "resource/api/monitor";//资源详情查询-摄像机
    public static final String GET_RESOURCE_SEARCH = BASE_PATH + "resource/api/resourceList/searchResources";//资源搜索

    public static final String POST_MONITOR_COUNT = BASE_PATH + "resource/api/monitor/getCount";//查询监控设备数量
    public static final String POST_KK_COUNT = BASE_PATH + "resource/api/monitor/getKakouCount";//查询卡口设备数量
    public static final String GET_MONITOR_ONLINE_COUNT = BASE_PATH + "resource/api/camera/getOnlineDevices";//查询监控设备在线数量

    public static final String GET_MAIN_DEVICE_LIST = BASE_PATH + "resource/api/camera/getDevicesByOnlineStatus";//主页查询在线离线设备列表
    public static final String GET_MAIN_SIGN_INFO = BASE_PATH + "oa/api/attendance/myAttRecordByTime";//主页查询签到详情


    public static final String GET_FIRE_COUNT_ROLE = BASE_PATH + "auth/api/auth/auth/user/auth";//查询账号权限
    public static final String GET_FIRE_COUNT_FIRE_IDS = BASE_PATH + "fire/api/monitorFirealarmMessage/getSendMessageByResave";//查询领导账号fireIds
    public static final String GET_FIRE_COUNT = BASE_PATH + "fire/api/Statistic/getLeaderFireAlarmList";//"fire/api/Statistic/getLatestFireAlarmList";//查询报警数量


    public static final String GET_GRID = BASE_PATH + "auth/api/sysArea/getAllSysArea";//获取区域数据
    //public static final String POST_POSITION = BASE_PATH + "oa/api/trajectory/trackUpload";//位置上传
    public static final String POST_POSITION = BASE_PATH + "duty/appPosition/reportPosition";//位置上传

    public static final String POST_HIDDEN_DANGER = BASE_PATH +"fire/api/dangerCheck";//隐患排查提交
    public static final String POST_FIRE_UPLOAD = BASE_PATH +"fire/api/reportFirealarm";//事件上报提交

    //public static final String PUT_CHANGE_PASSWORD = BASE_PATH +"auth/api/auth/user/modfiy/passwd";//修改密码
    public static final String PUT_CHANGE_PASSWORD = BASE_PATH +"system/user/profile/updatePwd";//修改密码
    //http://117.132.5.139:16002/prod-api/system/user/profile/updatePwd?oldPassword=4f42142df925126c6bdfd337b6716cba&newPassword=87b0f3b5b7dec6372b529ea2b580030e

    public static final String POST_NEWS = BASE_PATH +"auth/api/auth/news/list";//新闻列表
    public static final String POST_NEWS_PAGE = BASE_PATH +"auth/api/auth/news/page?type=appInternet";//新闻列表-分页
    public static final String GET_NEWS_INFO = BASE_PATH +"auth/api/auth/news";//新闻列表详情

    //public static final String POST_MESSAGE_NEW = BASE_PATH +"fire/api/appMessage/listNew";//NEW获取消息列表
    //public static final String GET_CHANGE_STATE_NEW = BASE_PATH +"fire/api/appMessage/readOne";//NEW更改消息已读状态
    public static final String POST_MESSAGE_NEW = BASE_PATH +"duty/appNotice/listByUserId";//NEW获取消息列表
    public static final String GET_MESSAGE_COUNT = BASE_PATH +"duty/appNotice/listByUserIdCount";//NEW获取消息数量
//    public static final String GET_CHANGE_STATE_NEW = BASE_PATH +"duty/incidentTask/editStatus";//NEW更改消息已读状态
    public static final String GET_CHANGE_STATE_NEW = BASE_PATH +"duty/appNotice/editStatus";//NEW更改消息已读状态
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
    public static final String POST_MESSAGE_CLEAR = BASE_PATH +"duty/appNotice/editAllStatus";//"fire/api/appMessage/updateUnread";//清除全部未读

    public static final String GET_VIDEO_TREES = BASE_PATH +"resource/api/grid/listGridTreesNew";//视频树 已收藏 "sheetType","collection" /*"NetworkType","Internet"*/
    public static final String GET_VIDEO_LIVE_URL = BASE_PATH +"resource/api/mediaKit/getLiveUrl";//视频流 /*"cameraId",gridCamera.getId(),"manufacturer","2","streamType","2","protocolType","rtsp"*/
    public static final String POST_STREAM = BASE_PATH +"resource/api/mediaKit/streamNotFoundwebHookNew";//流媒体提前手动拉流
    public static final String GET_CONTROL = BASE_PATH +"resource/api/liveVideo/control";//视频设备控制
    public static final String POST_VIDEO_COLLECTION = BASE_PATH +"resource/api/cameraCollection";//收藏 /*PostStar(gridCamera.getId(),gridCamera.getName(),null,gridCamera.getMonitorId())*/
    public static final String DELETE_VIDEO_COLLECTION = BASE_PATH +"resource/api/cameraCollection";//取消收藏

    public static final String GET_GRID_TREES = BASE_PATH +"resource/api/grid/listGridNewTrees";//网格(防火员)
    public static final String POST_GRID_TREES = BASE_PATH +"resource/api/grid/gridUser/list";//网格(防火员)

    //public static final String POST_TASK_LIST = BASE_PATH +"oa/api/taskManagement/listNew?type=appInternet";//任务列表
    public static final String POST_TASK_LIST = BASE_PATH +"duty/incidentTask/list";//任务列表
    //public static final String GET_TASK_INFO = BASE_PATH +"oa/api/taskManagement?type=appInternet";//获取任务详情
    public static final String GET_TASK_INFO = BASE_PATH +"duty/incidentTask/getInfo";//获取任务详情
    //public static final String PUT_TASK_STATE = BASE_PATH +"oa/api/taskManagement";//改变任务单状态任务状态 "id":"1753298129689640960","taskStatus":"progress" -> wait未开始 progress进行中 completed已完成
    public static final String PUT_TASK_STATE = BASE_PATH +"duty/incidentTask/editStatus";//改变任务单状态
    public static final String POST_TASK_ROOM = BASE_PATH +"resource/api/meeting/room/plot/step/list";//任务查询房间id
    public static final String GET_TASK_SNAP = BASE_PATH +"resource/api/meeting/room/plot/snap";//任务-根据房间id查询标绘
    //public static final String POST_LIVE_UPLOAD = BASE_PATH +"oa/api/taskDetail";//现场上报
    public static final String POST_LIVE_UPLOAD = BASE_PATH +"duty/incidentTask/report";//现场上报
    public static final String POST_ONE_BODY_IS_REAL = BASE_PATH +"fire/api/monitorFirealarm/realOrError";//处理一体机火警

    public static final String POST_PUSH_FEEDBACK = BASE_PATH +"resource/api/dispatchCommand/receiveFeedback";//推送反馈

    public static final String GET_NOTICE_PAGE = BASE_PATH +"duty/NotificationNotice/feedbackPage";//公告通知-预警预报 ?pageNum=1&pageSize=10
    public static final String POST_NOTICE_FEEDBACK = BASE_PATH +"duty/NotificationNotice/feedback";//公告通知-预警预报-反馈 {"fileList": null,"noticeId": "1772868139500044288"}
    public static final String POST_WEATHER_7DAY = BASE_PATH +"open/weatherNew/getWeatherLastSevenDay";// resource&open 查询7天天气 无参
    public static final String GET_CONTACTS_PAGE = BASE_PATH +"duty/person/page";//查询通讯录列表
    public static final String GET_CONTACTS_AREA = BASE_PATH +"system/group/treeselect";//查询通讯录树



}
