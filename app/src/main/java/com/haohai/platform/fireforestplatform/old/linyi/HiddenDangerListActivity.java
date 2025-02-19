package com.haohai.platform.fireforestplatform.old.linyi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.NaviSetting;
import com.amap.api.navi.model.AMapNaviLocation;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class HiddenDangerListActivity extends HhBaseActivity implements HiddenDangerListBinder.OnHiddenListItemClick, INaviInfoCallback {
    private static final String TAG = HiddenDangerListActivity.class.getSimpleName();
    public static int ORDER_CHANGE = 113;
    private RecyclerView listView;
    private List<HiddenDangerList> hiddenDangerList = new ArrayList<>();
    private final List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private final boolean isShowDialog = true;
    private final boolean isShuaxin = false;
    private final boolean isSearch = false;
    private ImageView left_icon;
    private TextView left;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主题色白色 黑字
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.whiteColor));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_hidden_danger_list);
        EventBus.getDefault().register(this);

        progressDialog = new ProgressDialog(this);

        initView();
        getDataFromService();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(RefreshModel message) {
        getDataFromService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void getDataFromService() {
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObject.put("checkType",6);

            jsonObjectBody.put("dto",jsonObject);
            jsonObjectBody.put("limit",200);
            jsonObjectBody.put("page",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/planResource/queryResource");
        params.setBodyContent(jsonObjectBody.toString());

        Log.e(TAG, "getDataFromService: params = " + params);
        Log.e(TAG, "getDataFromService: jsonObjectBody = " + jsonObjectBody.toString());
        params.addHeader("Authorization", "bearer " + CommonData.token);

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: hiddenDangerList" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code")==200){
                        JSONArray array = object.getJSONArray("data");
                        JSONObject out = (JSONObject) array.get(0);
                        JSONArray list = out.getJSONArray("dataList");
                        hiddenDangerList.clear();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject o = (JSONObject) list.get(i);
                            Log.e(TAG, "onSuccess: o = " + o);
                            HiddenDangerList model = new HiddenDangerList();
                            model.setTitle(o.getString("name"));
//                            model.setResourceType(o.getString("resourceType"));
                            model.setArea(o.getString("gridName"));
                            model.setCheckType("6");
                            model.setGridName(o.getString("gridName"));
                            model.setGridNo(o.getString("gridNo"));
                            model.setEndTime(o.getString("endTime"));
                            model.setGroupId(o.getString("groupId"));
                            model.setApiUrl("/api/checkStation");
                            model.setStatus(o.getInt("status"));
                            model.setLongitude(o.getString("longitude"));
                            model.setLatitude(o.getString("latitude"));
                            model.setUserType(o.getInt("userType"));
                            model.setCheckDate(o.getString("createTime"));
                            model.setPlayDate(o.getString("regulationTime"));//bingo
                            model.setWorker(o.getString("createUser")==null?"":o.getString("createUser"));
                            model.setResult(o.getString("description"));
                            model.setId(o.getString("id"));
                            model.setResourceId(o.getString("resourceId"));
                            JSONArray checkusers = o.getJSONArray("checkusers");
                            /*if(checkusers!=null && checkusers.length()!=0){
                                JSONObject users = (JSONObject) checkusers.get(0);
                                model.setWorker(users.getString("userName"));
                            }else{
                                model.setWorker("");
                            }*/
                            hiddenDangerList.add(model);
                        }

                        upDataData();
                    }else{//TODO 测试
                        upDataData();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }

    private void initView() {
        left_icon = findViewById(R.id.left_icon);
        left = findViewById(R.id.left);
        title = findViewById(R.id.title);
        title.setText("隐患排查列表");
        left_icon.setOnClickListener(v -> {
            finish();
        });
        left.setOnClickListener(v -> {
            finish();
        });

        swipeRefreshLayout = findViewById(R.id.hidden_refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        listView = findViewById(R.id.hidden_listview);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getDataFromService();
            swipeRefreshLayout.setRefreshing(false);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);

        register();

        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);

    }

    private void register() {
        HiddenDangerListBinder hiddenDangerListBinder = new HiddenDangerListBinder();
        hiddenDangerListBinder.setListener(this);
        adapter.register(HiddenDangerList.class, hiddenDangerListBinder);
    }

    private void upDataData() {
        items.clear();
        for (int i = 0; i < hiddenDangerList.size(); i++) {
            items.add(hiddenDangerList.get(i));
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }

    /**
     * itemBinder 条目点击回调
     */
    @Override
    public void onHiddenListItemClickListener(HiddenDangerList hiddenDangerList) {
        Intent intent = new Intent(this,ResourceInfoActivity.class);
        intent.putExtra("id",hiddenDangerList.getId());
        startActivity(intent);
    }

    /**
     * itemBinder 条目检查回调
     */
    @Override
    public void onCheckClickListener(HiddenDangerList resource) {
        Intent intent = new Intent(getApplicationContext(), ResourceCheckActivity.class);
        intent.putExtra("resourceID", resource.getResourceId());
        intent.putExtra("id", resource.getId());
        intent.putExtra("resourceName", resource.getTitle());
        intent.putExtra("checkType", 6);
        intent.putExtra("from", "resourceserch");
        intent.putExtra("APIURL", resource.getApiUrl());
        intent.putExtra("GRID_NO", resource.getGridNo());
        intent.putExtra("resourcegird", resource.getGridName());
        intent.putExtra("girdno", resource.getGridNo());
        intent.putExtra("longitude", resource.getLongitude());
        intent.putExtra("latitude", resource.getLatitude());
        intent.putExtra("lat", resource.getLatitude());
        intent.putExtra("lng", resource.getLongitude());
        intent.putExtra("groupId",resource.getGroupId());
        startActivity(intent);
    }
    /**
     * itemBinder 条目整治回调
     */
    @Override
    public void onZhengzhiClickListener(HiddenDangerList resource) {
        Intent intent = new Intent(getApplicationContext(), ResourcezhengzhiActivity.class);
        intent.putExtra("resourceID", resource.getId());
        intent.putExtra("id", resource.getId());
        intent.putExtra("resourceName", resource.getTitle());
        intent.putExtra("resourcegird", resource.getGridName());
        intent.putExtra("endTime", resource.getEndTime());
        intent.putExtra("girdno", resource.getGridNo());
        intent.putExtra("longitude", resource.getLongitude());
        intent.putExtra("latitude", resource.getLatitude());
        intent.putExtra("resourcetype", resource.getCheckType());
        intent.putExtra("checkType", 6);
        startActivityForResult(intent, ORDER_CHANGE);
    }
    /**
     * itemBinder 条目审核回调
     */
    @Override
    public void onShenheClickListener(HiddenDangerList resource) {
        Intent intent = new Intent(getApplicationContext(), ResourceshenheActivity.class);
        intent.putExtra("resourceID", resource.getId());
        intent.putExtra("id", resource.getId());
        intent.putExtra("resourceName", resource.getTitle());
        intent.putExtra("resourcegird", resource.getGridName());
        intent.putExtra("endTime", resource.getEndTime());
        intent.putExtra("girdno", resource.getGridNo());
        intent.putExtra("longitude", resource.getLongitude());
        intent.putExtra("latitude", resource.getLatitude());
        intent.putExtra("resourcetype", resource.getCheckType());
        intent.putExtra("checkType", 6);
        startActivityForResult(intent, ORDER_CHANGE);
    }
    /**
     * itemBinder 导航按钮点击
     */
    @Override
    public void onGuideClickListener(HiddenDangerList resource) {

        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);
       /*  //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
             AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
             //启动导航组件
             AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);*/
        double[] startGroup = bdToGaoDe(CommonData.lat,CommonData.lng);
        double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(resource.getLatitude()), Double.parseDouble(resource.getLongitude()));

        Poi start = new Poi("我的位置", new com.amap.api.maps.model.LatLng(startGroup[0], startGroup[1]), "");
        Poi end = new Poi(resource.getTitle(), new com.amap.api.maps.model.LatLng(doubles[0], doubles[1]), "");
        AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
        params.setUseInnerVoice(true);
        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, HiddenDangerListActivity.this);
    }

    private double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.sin(theta);
        gd_lat_lon[1] = z * Math.cos(theta);
        return gd_lat_lon;
    }
    private double[] gaoDeToBaidu(double gd_lat, double gd_lon) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.sin(theta) + 0.006;
        bd_lat_lon[1] = z * Math.cos(theta) + 0.0065;
        return bd_lat_lon;
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public void onNaviDirectionChanged(int i) {

    }

    @Override
    public void onDayAndNightModeChanged(int i) {

    }

    @Override
    public void onBroadcastModeChanged(int i) {

    }

    @Override
    public void onScaleAutoChanged(boolean b) {

    }
}