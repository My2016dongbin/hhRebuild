package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.event.VideoStream;
import com.haohai.platform.fireforestplatform.helper.DialogHelper;
import com.haohai.platform.fireforestplatform.old.WeatherActivity;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.banner.BannerBean;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.bean.VideoDeleteModel;
import com.haohai.platform.fireforestplatform.ui.multitype.MainDevice;
import com.haohai.platform.fireforestplatform.ui.multitype.MainFgMenu;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.SignModel;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class FgMainViewModel extends BaseViewModel {
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public final int BANNER_COUNT = 5;//轮播图数量限制
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();
    public List<Integer> resList = new ArrayList<>();
    public List<MainFgMenu> menus = new ArrayList<>();
    public float handle = 0;
    public float noHandle = 0;
    public float handleIdentify = 0;
    public float noHandleIdentify = 0;
    public float handleCheck = 0;
    public float noHandleCheck = 0;
    public Date date;
    public String dateStr = "";
    public String tempStr = "";
    public String tempByStr = "";
    public String weatherStr = "";
    public String weatherDateStr = "";
    public String weatherLocationStr = "";
    public String weatherUrl = "";
    public int oneBodyNum = 0;
    public int kkNum = 0;
    public final MutableLiveData<Integer> weatherState = new MutableLiveData<>();
    public final MutableLiveData<List<MainFgMenu>> menuList = new MutableLiveData<>();
    public final MutableLiveData<List<BannerBean>> bannerList = new MutableLiveData<>();
    public final MutableLiveData<List<MainDevice>> mainDeviceList = new MutableLiveData<>();
    public final MutableLiveData<Integer> deviceNumber = new MutableLiveData<>();
    public final MutableLiveData<Integer> deviceOnline = new MutableLiveData<>();
    public final MutableLiveData<Float> handleData = new MutableLiveData<>();
    public final MutableLiveData<Float> identifyData = new MutableLiveData<>();
    public final MutableLiveData<Float> checkData = new MutableLiveData<>();
    private List<News> newsList = new ArrayList<>();
    public String mainDeviceStatus = "";//""查询全部  "0"是不在线 "1"是在线
    public final MutableLiveData<String> name = new MutableLiveData<>();
    public void start(Context context){
        this.context = context;
    }

    /**
     * 初始化数据
     */
    public void initData(){
        getTestData();
        getName();
    }


    public void getName() {
        HhHttp.get()
                .url(URLConstant.GET_NAME_BY_TOKEN)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("TAG", "onSuccess: GET_NAME_BY_TOKEN = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length()>0) {
                                JSONObject model = (JSONObject) data.get(0);
                                String loginSystemName = model.getString("loginSystemName");
                                if(!loginSystemName.isEmpty()){
                                    name.postValue(loginSystemName);
                                }
                            }
                            name.postValue(jsonObject.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());//400 密码错误  401 账号未注册
                    }
                });
    }

    public void getStream(String cameraId, String monitorId, String channelId) {
        DialogHelper.getInstance().show(context, "获取中..");
        HhHttp.get().url(URLConstant.GET_VIDEO_LIVE_URL)
                .addParams("cameraId", cameraId)
                /*.addParams("manufacturer","2")//
                .addParams("streamType","2")//
                .addParams("protocol","RTSP")//*/
                .addParams("protocolType", "http")
                .build().execute(new LoggedInStringCallback(FgMainViewModel.this, context) {
            @Override
            public void onSuccess(String response, int id) {
                HhLog.e("getStream " + cameraId);
                HhLog.e("getStream " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if (data.length() > 0) {
                        JSONObject obj = (JSONObject) data.get(0);
                        String url = obj.getString("url");
                        HhLog.e("getStream url " + url);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DialogHelper.getInstance().close();
                                CommonData.videoDeleteMonitorId = monitorId;
                                CommonData.videoDeleteChannelId = channelId;
                                parseVideoDeleteIds(new VideoDeleteModel(CommonData.videoAddingIndex, monitorId, channelId));
                                EventBus.getDefault().post(new VideoStream(url, true, CommonData.videoAddingIndex));
                                EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(context,SPValue.videoIndex,1)));
                            }
                        }, 200);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {
                DialogHelper.getInstance().close();
            }
        });
    }

    private void parseVideoDeleteIds(VideoDeleteModel videoDeleteModel) {
        int modelIndex = videoDeleteModel.getIndex();
        for (int i = 0; i < CommonData.videoDeleteModelList.size(); i++) {
            if (modelIndex == CommonData.videoDeleteModelList.get(i).getIndex()) {
                CommonData.videoDeleteModelList.set(i,videoDeleteModel);
                return;
            }
        }
        CommonData.videoDeleteModelList.add(videoDeleteModel);
    }

    public void signInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        RequestParams params = new RequestParams(URLConstant.GET_MAIN_SIGN_INFO);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("time",formatter.format(curDate));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.setBodyContent(jsonObject.toString());
        params.addBodyParameter("time",formatter.format(curDate));
        params.addHeader("Authorization", "bearer " + CommonData.token);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data!=null && data.length()!=0){
                        //有记录
                        CommonData.hasSign = true;
                    }else{
                        //未签到
                        CommonData.hasSign = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void postNews(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.postString()
                .url(URLConstant.POST_NEWS_PAGE)
                .content(new Gson().toJson(new News((String) SPUtils.get(context, SPValue.groupId,""),1,BANNER_COUNT,new News.Dto(""))))
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        try {
                            HhLog.e("POST_NEWS_PAGE " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if(data.length()>0){
                                JSONObject obj = (JSONObject) data.get(0);
                                JSONArray dataList = obj.getJSONArray("dataList");
                                newsList = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<News>>() {
                                }.getType());

                                List<BannerBean> bannerListTest = new ArrayList<>();
                                for (int m = 0; m < newsList.size(); m++) {
                                    News news = newsList.get(m);
                                    bannerListTest.add(new BannerBean(news.getId(),news.getNewsName(),".html",news.getPic().replace("9101","15060"),news.getId()));
                                }
                                if(!bannerListTest.isEmpty()){
                                    bannerList.setValue(bannerListTest);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }


    private void walkInfo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        RequestParams params = new RequestParams(URLConstant.GET_USER_DISTANCE);
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addParameter("time",format);
        params.addParameter("userId",SPUtils.get(context, SPValue.id,""));
        Log.e("TAG", "onSuccess: bingo params = walkInfo " +  params.toString());
        //showDY3();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: bingo result = walkInfo" +  result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    CommonData.walkDistance = jsonObject.getInt("data");
                    CommonData.hasGet = true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError: walkInfo " + ex.getMessage() );
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                //hideDY3();
            }
        });
    }

    /**
     * 获取测试数据
     * */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void getTestData(){

        walkInfo();

        //Banner数据
        if(CommonPermission.hasPermission(context,CommonPermission.APP_BANNER)){
            List<BannerBean> bannerListTest = new ArrayList<>();
            bannerListTest.add(new BannerBean("","市园林和林业局局长到市属公园进行调研",".html","https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AAOEcdM.img","/bannerDetail?id=123456"));
            bannerListTest.add(new BannerBean("","青岛浩海网络科技股份有限公司提供技术支持欢迎加入浩海大家庭",".html","https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AAOEhRG.img","/bannerDetail?id=123456789"));
            bannerList.setValue(bannerListTest);
            postNews();
        }

        //Menu数据 ///后续通过后端返回路由和在线icon配置菜单
        List<MainFgMenu> menu_ = new ArrayList<>();
        int index = 0;
        if(CommonPermission.hasPermission(context,CommonPermission.APP_ALARM)){menu_.add(new MainFgMenu("","warn","报警管理","",R.drawable.icon_manage,"/warn/manage",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_SATELLITE)){menu_.add(new MainFgMenu("","star","卫星遥感","",R.drawable.icon_star,"/star",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_DANGER)){menu_.add(new MainFgMenu("","danger","隐患排查","",R.drawable.icon_danger,"/danger",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_UPLOAD)){menu_.add(new MainFgMenu("","fire","火情上报","",R.drawable.icon_fire,"/fire",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_VIDEO)){menu_.add(new MainFgMenu("","video","视频监控","",R.drawable.icon_video,"/video",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_TASK)){menu_.add(new MainFgMenu("","order","任务单","",R.drawable.icon_order,"/order",index));index++;}
        //if(CommonPermission.hasPermission(context,CommonPermission.APP_RANGER)){menu_.add(new MainFgMenu("","person","防火员","",R.drawable.icon_person,"/person",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_NEWS)){menu_.add(new MainFgMenu("","news","要闻","",R.drawable.icon_news,"/news",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_SIGN)){menu_.add(new MainFgMenu("","signManage","考勤管理","",R.drawable.icon_signmanage,"/sign/manage",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_WEATHER)){menu_.add(new MainFgMenu("","weather","天气","",R.drawable.icon_weather,"/weather",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_IDENTIFY)){menu_.add(new MainFgMenu("","identify","识别管理","",R.drawable.ic_identify,"/identify",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_HANDLE)){menu_.add(new MainFgMenu("","handle","处置管理","",R.drawable.ic_handle,"/handle",index));index++;}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_CHECK)){menu_.add(new MainFgMenu("","check","督导核实","",R.drawable.ic_check,"/check",index));}
        if(CommonPermission.hasPermission(context,CommonPermission.APP_XH_UPLOAD)){menu_.add(new MainFgMenu("","xhUpload","巡护上报","",R.drawable.ic_xh_upload,"/check",index));}
        menus = new ArrayList<>(menu_);
        menuList.setValue(menus);

        //设备数据
        HhHttp.postString()
                .content(new Gson().toJson(new CommonParams()))
                .url(URLConstant.POST_MONITOR_COUNT)
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()>0){
                        oneBodyNum = (int) data.get(0);
                        deviceNumber.postValue(oneBodyNum + kkNum);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {

            }
        });
        HhHttp.postString()
                .content(new Gson().toJson(new CommonParams()))
                .url(URLConstant.POST_KK_COUNT)
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()>0){
                        kkNum = (int) data.get(0);
                        deviceNumber.postValue(kkNum + oneBodyNum);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {

            }
        });
        //getMainDeviceData();
        HhHttp.get()
                .url(URLConstant.GET_MONITOR_ONLINE_COUNT)
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()>0){
                        JSONObject obj = (JSONObject) data.get(0);
                        int onlineMonitorCount = obj.getInt("onlineMonitorCount");
                        int onlineKakouCount = obj.getInt("onlineKakouCount");
                        deviceOnline.postValue(onlineMonitorCount+onlineKakouCount);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {

            }
        });
        String groupId = String.valueOf(SPUtils.get(context, SPValue.groupId, "001021"));
        String gridNo = String.valueOf(SPUtils.get(context, SPValue.gridNo, "370214"));
        String leaderRoleId = "896049317960220672";

        HhHttp.get()
                .url(URLConstant.GET_FIRE_COUNT_ROLE)
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                HhLog.e("GET_FIRE_COUNT_ROLE " + response );
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()>0){
                        JSONObject obj = (JSONObject) data.get(0);
                        JSONObject userDTO = obj.getJSONObject("userDTO");
                        String roleId = userDTO.getString("roleId");
                        //判断是否为领导账号
                        SPUtils.put(HhApplication.getInstance(), SPValue.manager, roleId.contains(leaderRoleId));
                        if(roleId.contains(leaderRoleId)){
                            //领导账号
                            //先获取fireIds
                            String userId = String.valueOf(SPUtils.get(context, SPValue.id, ""));
                            HhHttp.get()
                                    .url(URLConstant.GET_FIRE_COUNT_FIRE_IDS)
                                    .addParams("resaveId", userId)
                                    .build().execute(new LoggedInStringCallback(FgMainViewModel.this,context) {
                                @Override
                                public void onSuccess(String response, int id) {
                                    HhLog.e("GET_FIRE_COUNT_FIRE_IDS " + userId + response );
                                    try {
                                        String fireIds = "";
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        if(data.length()>0){
                                            for (int i = 0; i < data.length(); i++) {
                                                JSONObject o = (JSONObject) data.get(i);
                                                if(fireIds.isEmpty()){
                                                    fireIds+=o.getString("fireId");
                                                }else{
                                                    fireIds = fireIds + "," + o.getString("fireId");
                                                }
                                            }
                                        }

                                        //然后查询
                                        String finalFireIds = fireIds;
                                        HhHttp.get()
                                                .url(URLConstant.GET_FIRE_COUNT)
                                                .addParams("groupId", groupId)//"001021")
                                                .addParams("provinceCode",gridNo)//"370214")
                                                .addParams("fireIds",fireIds)
                                                .addParams("ip","0")
                                                .addParams("isAndroid","0")
                                                .build().execute(new LoggedInStringCallback(FgMainViewModel.this,context) {
                                            @Override
                                            public void onSuccess(String response, int id) {
                                                HhLog.e("GET_FIRE_COUNT LEADER groupId " + groupId + " ,gridNo " + gridNo + " ， fireIds "  + finalFireIds +" , " + response );
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    JSONArray data = jsonObject.getJSONArray("data");
                                                    if(data.length()>0){
                                                        JSONObject obj = (JSONObject) data.get(0);
                                                        int fireUntreatedCount = obj.getInt("fireUntreatedCount");
                                                        int fireSrocessedCount = obj.getInt("fireSrocessedCount");
                                                        handle = Float.parseFloat(fireSrocessedCount + "");
                                                        noHandle = Float.parseFloat(fireUntreatedCount + "");
                                                    }
                                                    handleData.postValue(handle);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call call, Exception e, int id) {
                                                handleData.postValue(handle);
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call call, Exception e, int id) {
                                    HhLog.e(e.getMessage());
                                    handleData.postValue(handle);
                                }
                            });


                        }else{
                            //普通用户
                            //直接查询
                            HhHttp.get()
                                    .url(URLConstant.GET_FIRE_COUNT)
                                    .addParams("groupId", groupId)//"001021")
                                    .addParams("provinceCode",gridNo)//"370214")
                                    .addParams("ip","0")
                                    .addParams("isAndroid","0")
                                    .build().execute(new LoggedInStringCallback(FgMainViewModel.this,context) {
                                @Override
                                public void onSuccess(String response, int id) {
                                    HhLog.e("GET_FIRE_COUNT USER groupId " + groupId + " ,gridNo " + gridNo + " ， " + response );
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        if(data.length()>0){
                                            JSONObject obj = (JSONObject) data.get(0);
                                            int fireUntreatedCount = obj.getInt("fireUntreatedCount");
                                            int fireSrocessedCount = obj.getInt("fireSrocessedCount");
                                            handle = Float.parseFloat(fireSrocessedCount + "");
                                            noHandle = Float.parseFloat(fireUntreatedCount + "");
                                        }
                                        handleData.postValue(handle);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call call, Exception e, int id) {
                                    handleData.postValue(handle);
                                }
                            });

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {
                handleData.postValue(handle);
            }
        });

        //查询识别数量 POST_IDENTIFY_COUNT
        HhHttp.postString()
                .content(new Gson().toJson(new CommonParams()))
                .url(URLConstant.POST_IDENTIFY_COUNT)
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                try {
                    HhLog.e("POST_IDENTIFY_COUNT " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()>0){
                        JSONObject obj = (JSONObject) data.get(0);
                        String totalCount = obj.getString("totalCount");
                        String undoCount = obj.getString("undoCount");
                        String count = obj.getString("count");
                        handleIdentify = Float.parseFloat(count + "");
                        noHandleIdentify = Float.parseFloat(undoCount + "");
                        identifyData.postValue(Float.parseFloat(count));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {

            }
        });
        //查询识别数量 POST_CHECK_COUNT
        HhHttp.postString()
                .content(new Gson().toJson(new CommonParams()))
                .url(URLConstant.POST_CHECK_COUNT)
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                try {
                    HhLog.e("POST_CHECK_COUNT " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()>0){
                        JSONObject obj = (JSONObject) data.get(0);
                        String totalCount = obj.getString("totalCount");
                        String undoCount = obj.getString("undoCount");
                        String count = obj.getString("count");
                        handleCheck = Float.parseFloat(count + "");
                        noHandleCheck = Float.parseFloat(undoCount + "");
                        checkData.postValue(Float.parseFloat(count));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {

            }
        });


        //获取天气数据
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWeatherData();
            }
        },2000);
    }

    public void getMainDeviceData(){
        loading.postValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_MAIN_DEVICE_LIST)
                .addParams("status",mainDeviceStatus)
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                loading.postValue(new LoadingEvent(false));
                //HhLog.e("getMainDeviceData " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    List<MainDevice> list = new Gson().fromJson(String.valueOf(data), new TypeToken<List<MainDevice>>() {
                    }.getType());
                    for (int i = 0; i < list.size(); i++) {
                        MainDevice mainDevice = list.get(i);
                        mainDevice.setIndex(i+1);
                    }
                    mainDeviceList.postValue(list);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {
                loading.postValue(new LoadingEvent(false));
            }
        });
    }

    public void getWeatherData() {
        date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        dateStr = format.format(date);

        HhLog.e("LngLat " + CommonData.lng + "," + CommonData.lat);
        double lng = 120.314031;
        double lat = 36.308606;
        //获取当前城市信息
        QWeather.getGeoCityLookup(context, (CommonData.lng==0?lng:CommonData.lng) + "," + (CommonData.lat==0?lat:CommonData.lat), new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e("TAG", "onError: weather" + throwable.getMessage());
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                String name = geoBean.getLocationBean().get(0).getName();
                String up = geoBean.getLocationBean().get(0).getAdm2();
                String upGov = geoBean.getLocationBean().get(0).getAdm1().replace("省","");
                Log.e("TAG", "onSuccess: weather" + upGov + "," + up + "," + name);
                weatherLocationStr = up;
                weatherState.postValue(new Random().nextInt(1000));
            }
        });
        //获取实况天气
        QWeather.getWeatherNow(context, (CommonData.lng==0?lng:CommonData.lng) + "," + (CommonData.lat==0?lat:CommonData.lat), new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                weatherUrl = CommonUtil.getHeFengIcon((now.getText().equals("晴")?"FFF68F":"FFFFFF"),now.getIcon(),"80");
                Log.e("TAG", "onSuccess: weatherUrl = " + weatherUrl );
                Log.e("TAG", "onSuccess: now = " + now );

                tempStr = now.getTemp()+"";
                tempByStr = "/" + now.getFeelsLike()+"°C";
                weatherStr = now.getText()+"";
                weatherDateStr = dateStr;
                weatherState.postValue(new Random().nextInt(1000));
            }
        });
    }

}
