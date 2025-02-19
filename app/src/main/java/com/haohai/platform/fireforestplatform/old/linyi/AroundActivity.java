package com.haohai.platform.fireforestplatform.old.linyi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.NaviSetting;
import com.amap.api.navi.model.AMapNaviLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;
import com.haohai.platform.fireforestplatform.ui.multitype.AroundViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.ui.multitype.Resource;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class AroundActivity extends BaseActivity implements AroundViewBinder.AroundItemClick, INaviInfoCallback {
    private static final String TAG ="AroundActivity" ;
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private Dialog aroundDialog;
    private View resourceListInflater;
    private View aroundListInflater;
    private RecyclerView aroundListView;
    private LinearLayout ll_around_loc;
    private TextView tv_around_loc;
    private TextView tv_around_clear;
    private RecyclerView resourceListView;
    private TextView aroundFenleiTextView;
    private TextView tv_grid;
    private TextView tv_type;
    private ScrollView sv_grid;
    private LinearLayout ll_grid;
    private LinearLayout aroundFenleiLayout;
    private int choose1 = 2;
    private int aroundKm = 5;
    private AlertDialog.Builder builder;
    private boolean canAroundClick = false;
    private String latitude = "0";
    private String longitude = "0";
    private MultiTypeAdapter aroundAdapter;
    private List<Object> aroundItems = new ArrayList<>();
    private List<JSONObject> allResourcePoints = new ArrayList<>();
    private JSONObject resourceObj = new JSONObject();
    private JSONArray arrayList = new JSONArray();
    private Resource currentAround;
    private TextView otherresourcenameview;
    private TextView resoucedizhiview;
    private TextView otherresoucedizhiview;
    private Dialog otherresourceinfoDialog;
    private View otherresourceInflater;
    private TextView res_edit;
    private TextView res_delete;
    private TextView otherresourcejingweiduview;
    private ImageView iv_guide;
    private boolean clickOtherRes = false;
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
        setContentView(R.layout.activity_around);
        initView();
    }

    private void initView() {
        ImageView left_icon = findViewById(R.id.left_icon);
        TextView left = findViewById(R.id.left);
        TextView right = findViewById(R.id.right);
        right.setOnClickListener(v -> {
            aroundDialog.show();
        });
        left.setOnClickListener(v -> {
            finish();
        });
        left_icon.setOnClickListener(v -> {
            finish();
        });



        //baiduMap
        mapView = findViewById(R.id.baidu_mapview);
        mBaiduMap = mapView.getMap();
        //显示卫星图层
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setMyLocationEnabled(true);
        mapView.showZoomControls(false);

        //只显示道路 不显示其他标注
        mBaiduMap.showMapPoi(true);
        //设置最大最小缩放等级
        mBaiduMap.setMaxAndMinZoomLevel(18, 5);
        flyBaiduMapZoom(CommonData.lat, CommonData.lng, 14);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(!canAroundClick){
                    return;
                }
                canAroundClick = false;
                tv_around_loc.setText("当前选择:"+latLng.longitude+","+latLng.latitude);
                postAround5Km(latLng.latitude,latLng.longitude);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        aroundDialog.show();
                    }
                });
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                return ;
            }
        });
        mBaiduMap.setOnMarkerClickListener(marker -> {
            Bundle extraInfo = marker.getExtraInfo();
            String markerId = extraInfo.getString("id");
            int markerType = extraInfo.getInt("type");

            for (int i = 0; i < arrayList.length(); i++) {
                try {
                    JSONObject model = (JSONObject) arrayList.get(i);
                    Resource resource = new Gson().fromJson(model.toString(),new TypeToken<Resource>(){}.getType());
                    if(Objects.equals(resource.getId(), markerId)){
                        showDetail(resource);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return false;
        });



        //资源点周边Dialog
        aroundDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        aroundListInflater = LayoutInflater.from(this).inflate(R.layout.dialog_around_list, null);
        aroundListInflater.setMinimumWidth(10000);
        aroundListView = ((RecyclerView) aroundListInflater.findViewById(R.id.onebody_info_listview));
        ll_around_loc = ((LinearLayout) aroundListInflater.findViewById(R.id.ll_around_loc));
        tv_around_loc = ((TextView) aroundListInflater.findViewById(R.id.tv_around_loc));
        tv_around_clear = ((TextView) aroundListInflater.findViewById(R.id.tv_around_clear));
        aroundDialog.setContentView(aroundListInflater);
        aroundFenleiTextView = ((TextView) aroundListInflater.findViewById(R.id.fenlei_view));
        aroundFenleiLayout = ((LinearLayout) aroundListInflater.findViewById(R.id.fenlei_layout));
        aroundFenleiTextView.setText(aroundKm + "Km");
        Window aroundListWindow = aroundDialog.getWindow();
        aroundListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams aroundListLp = aroundListWindow.getAttributes();

        WindowManager aroundwm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int aroundheight = aroundwm.getDefaultDisplay().getHeight();
        aroundListLp.height = (int) (aroundheight * 0.8);
        aroundListWindow.setAttributes(aroundListLp);
        aroundDialog.setCanceledOnTouchOutside(true);
        RxViewAction.clickNoDouble(aroundFenleiLayout).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                showAroundKmChangeDialog();
            }
        });
        RxViewAction.clickNoDouble(ll_around_loc).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                aroundDialog.dismiss();
                Toast.makeText(AroundActivity.this, "请在地图上点击选择周边位置", Toast.LENGTH_SHORT).show();
                canAroundClick = true;
            }
        });
        RxViewAction.clickNoDouble(tv_around_clear).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                aroundDialog.dismiss();
                Toast.makeText(AroundActivity.this, "已重置", Toast.LENGTH_SHORT).show();
                //清除旧的卫星定位
                clearAllResourceMarker();
                tv_around_loc.setText("请选择周边位置");
                aroundObj = null;
                aroundList.clear();
                initAroundData();
            }
        });

        LinearLayoutManager aroundlinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        aroundListView.setLayoutManager(aroundlinearLayoutManager);
        aroundAdapter = new MultiTypeAdapter(aroundItems);

        AroundViewBinder aroundViewBinder = new AroundViewBinder();
        aroundViewBinder.setListener(this);
        aroundAdapter.register(Resource.class, aroundViewBinder);
        aroundAdapter.register(Empty.class,new EmptyViewBinder(this));
        aroundListView.setAdapter(aroundAdapter);
        assertHasTheSameAdapter(aroundListView, aroundAdapter);


        /*
         *  其它资源点详细信息
         */
        otherresourceinfoDialog = new Dialog(AroundActivity.this, R.style.ActionSheetDialogStyle);
        otherresourceInflater = LayoutInflater.from(AroundActivity.this).inflate(R.layout.dialog_resource_info_other, null);
        otherresourceInflater.setMinimumWidth(10000);
        otherresourcenameview = ((TextView) otherresourceInflater.findViewById(R.id.resourcename_view));
        otherresoucedizhiview = ((TextView) otherresourceInflater.findViewById(R.id.resoucedizhi_view));
        res_edit = ((TextView) otherresourceInflater.findViewById(R.id.res_edit));
        res_delete = ((TextView) otherresourceInflater.findViewById(R.id.res_delete));
        otherresourcejingweiduview = ((TextView) otherresourceInflater.findViewById(R.id.resourcejingweidu_view));
        iv_guide = ((ImageView) otherresourceInflater.findViewById(R.id.iv_guide));

        otherresourceinfoDialog.setContentView(otherresourceInflater);
        otherresourceinfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clickOtherRes = false;
            }
        });
        Window resourceDialogWindowOther = otherresourceinfoDialog.getWindow();
        resourceDialogWindowOther.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams resourceLpFireOther = resourceDialogWindowOther.getAttributes();
        resourceDialogWindowOther.setAttributes(resourceLpFireOther);
        otherresourceinfoDialog.setCanceledOnTouchOutside(true);
    }

    private void showAroundKmChangeDialog() {
        //默认选中第三个  //1Km   3Km   5Km   10Km   15Km
        final String[] items = {"1Km", "3Km", "5Km", "10Km", "15Km"};
        builder = new AlertDialog.Builder(this).setIcon(R.drawable.ic_icon).setTitle("选择周边距离")
                .setSingleChoiceItems(items,choose1 , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choose1 = i;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        aroundList.clear();
                        JSONArray list = new JSONArray();
                        if(aroundObj == null){
                            Toast.makeText(AroundActivity.this, "请先在地图上选择周边点位", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (choose1 == 0){
                            aroundKm = 1;
                            aroundFenleiTextView.setText("1Km");
                            try {
                                JSONArray list1 = aroundObj.getJSONArray("list1");
                                list = list1;
                                aroundList = new Gson().fromJson(String.valueOf(list1), new TypeToken<List<Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (choose1 == 1){
                            aroundKm = 3;
                            aroundFenleiTextView.setText("3Km");
                            try {
                                JSONArray list3 = aroundObj.getJSONArray("list3");
                                list = list3;
                                aroundList = new Gson().fromJson(String.valueOf(list3), new TypeToken<List<Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (choose1 == 2){
                            aroundKm = 5;
                            aroundFenleiTextView.setText("5Km");
                            try {
                                JSONArray list5 = aroundObj.getJSONArray("list5");
                                list = list5;
                                aroundList = new Gson().fromJson(String.valueOf(list5), new TypeToken<List<Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (choose1 == 3){
                            aroundKm = 10;
                            aroundFenleiTextView.setText("10Km");
                            try {
                                JSONArray list10 = aroundObj.getJSONArray("list10");
                                list = list10;
                                aroundList = new Gson().fromJson(String.valueOf(list10), new TypeToken<List<Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (choose1 == 4){
                            aroundKm = 15;
                            aroundFenleiTextView.setText("15Km");
                            try {
                                JSONArray list15 = aroundObj.getJSONArray("list15");
                                list = list15;
                                aroundList = new Gson().fromJson(String.valueOf(list15), new TypeToken<List<Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //清除所有资源点
                        clearAllResourceMarker();
                        //添加资源点
                        markers(list);
                    }
                });
        builder.create().show();
    }

    private void clearAllResourceMarker() {
        mBaiduMap.clear();
    }

    private List<Resource> aroundList = new ArrayList<>();
    private JSONObject aroundObj;
    /**
     * 查询周边5Km资源
     * @param latitude
     * @param longitude
     */
    private void postAround5Km(double latitude, double longitude) {
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/resourceList/getAroundResourceListNew");
        Around around = new Around(new Around.Position(latitude,longitude),5);
        params.setBodyContent(new Gson().toJson(around));
        params.addHeader("Authorization","bearer " + CommonData.token);
        Log.e(TAG, "postAround5Km: params" +  params);
        Log.e(TAG, "postAround5Km: toJson" +  new Gson().toJson(around));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "postAround5Km: " + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if(code != 200){
                        Toast.makeText(AroundActivity.this, "获取周边数据失败,请稍候重试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONArray data = jsonObject.getJSONArray("data");
                    aroundObj = (JSONObject) data.get(0);
                    JSONArray list1 = aroundObj.getJSONArray("list1");
                    JSONArray list3 = aroundObj.getJSONArray("list3");
                    JSONArray list5 = aroundObj.getJSONArray("list5");
                    JSONArray list10 = aroundObj.getJSONArray("list10");
                    JSONArray list15 = aroundObj.getJSONArray("list15");
                    JSONArray list = new JSONArray();
                    aroundList.clear();
                    if(aroundKm == 1){
                        list = list1;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<Resource>>() {
                        }.getType());
                    }else if(aroundKm == 3){
                        list = list3;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<Resource>>() {
                        }.getType());
                    }else if(aroundKm == 5){
                        list = list5;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<Resource>>() {
                        }.getType());
                    }else if(aroundKm == 10){
                        list = list10;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<Resource>>() {
                        }.getType());
                    }else if(aroundKm == 15){
                        list = list15;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<Resource>>() {
                        }.getType());
                    }
                    initAroundData();
                    //清除所有资源点
                    clearAllResourceMarker();
                    //添加资源点
                    markers(list);

                    for (int i = 0; i < aroundList.size(); i++) {
                        JSONObject obj = (JSONObject) list.get(i);
                        aroundList.get(i).setObj(obj);
                    }
                    //initAroundData();
                    Log.e("list", list.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(AroundActivity.this, "获取周边数据失败,请稍候重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void markers(JSONArray list) {
        arrayList = list;
        List<OverlayOptions> options = new ArrayList<>();
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_fire);
        for (int i = 0; i < list.length(); i++) {
            try {
                JSONObject res = (JSONObject) list.get(i);
                //TODO 需后端配置后自动获取类型图标
                if (Objects.equals(res.getString("resourceType"), "helicopterPoint")) {//停机坪
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.airport);
                }
                if (Objects.equals(res.getString("resourceType"), "team")) {//消防专业队
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.teem);
                }
                if (Objects.equals(res.getString("resourceType"), "dangerSource")) {//危险源
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.danger);
                }
                if (Objects.equals(res.getString("resourceType"), "materialRepository")) {//物资库
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.wuziku);
                }
                if (Objects.equals(res.getString("resourceType"), "waterSource")) {//水源地
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.water);
                }
                if (Objects.equals(res.getString("resourceType"), "cemetery")) {//墓地
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.md);
                }
                if (Objects.equals(res.getString("resourceType"), "watchTower")) {//瞭望塔
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.lwt);
                }
                if (Objects.equals(res.getString("resourceType"), "checkStation")) {//护林检查站
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.check);
                }
                if (Objects.equals(res.getString("resourceType"), "monitor/kakou")) {//卡口
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.kk);
                }
                if (Objects.equals(res.getString("resourceType"), "monitor/jiankong")) {//监控点
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.onbody);
                }
                if (Objects.equals(res.getString("resourceType"), "fireCommand")) {//指挥部
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.zhihui);
                }
                //新增资源类型
                if (Objects.equals(res.getString("resourceType"), "weatherStation")) {//气象站
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.qixiang);
                }
                if (Objects.equals(res.getString("resourceType"), "touristAttraction")) {//旅游景点
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.lvyou);
                }
                if (Objects.equals(res.getString("resourceType"), "residentialArea")) {//居民地
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.jumindi);
                }
                if (Objects.equals(res.getString("resourceType"), "ancientTree")) {//古树名木
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.gushu);
                }
                if (Objects.equals(res.getString("resourceType"), "historicSites")) {//文物古迹
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.wenwu);
                }
                if (Objects.equals(res.getString("resourceType"), "shoppingMall")) {//商场
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.shop);
                }
                if (Objects.equals(res.getString("resourceType"), "school")) {//学校
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.school);
                }
                if (Objects.equals(res.getString("resourceType"), "baseStation")) {//通信基站
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.jizhan);
                }
                if (Objects.equals(res.getString("resourceType"), "shelter")) {//避难场所
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.binan);
                }
                if (Objects.equals(res.getString("resourceType"), "medicalAgency")) {//医疗机构
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.yiliao);
                }
                if (Objects.equals(res.getString("resourceType"), "garrisonPoint")) {//靠前驻防点
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.kaoqian);
                }
                try {
                    JSONObject position = res.getJSONObject("position");
                    String lat = position.getString("lat");
                    String lng = position.getString("lng");
                    double[] doubles = com.haohai.platform.fireforestplatform.utils.LatLngChangeNew.calWGS84toBD09(Double.parseDouble(lat), Double.parseDouble(lng));
                    com.baidu.mapapi.model.LatLng point = new com.baidu.mapapi.model.LatLng(doubles[0], doubles[1]);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", res.getString("id"));
                    bundle.putInt("type", 1);
                    OverlayOptions option = new MarkerOptions()
                            .position(point)
                            .extraInfo(bundle)
                            .icon(btm);
                    options.add(i, option);
                } catch (Exception e) {
                    Log.e(TAG, "satelliteMarker: " + e.getMessage());
                    continue;
                }
                mBaiduMap.addOverlays(options);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 初始化周边资源数据
     */
    private void initAroundData() {

        aroundItems.clear();
        if (aroundList.size() == 0){
            aroundItems.add(new Empty("暂无数据"));
        }else {
            aroundItems.addAll(aroundList);
        }


        assertAllRegistered(aroundAdapter, aroundItems);
        aroundAdapter.notifyDataSetChanged();
    }

    private void flyBaiduMapZoom(double lat, double lng, int zoom) {
        //飞到精确点上
        com.baidu.mapapi.model.LatLng ll = new com.baidu.mapapi.model.LatLng(
                lat, lng);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(zoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }
    /**
     * 周边飞到地图上
     */
    private void initAroundFlyMap() {
        Log.e(TAG, "initAroundFlyMap: currentAround.toString() = " + currentAround.toString() );
        final JSONObject jsonObject = new JSONObject();
        JSONObject posObj = new JSONObject();
        try {
            posObj.put("lat", currentAround.getPosition().getLat());
            posObj.put("lng", currentAround.getPosition().getLng());
            jsonObject.put("position", posObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("GPSposition", jsonObject.toString());
        double[] doubles = com.haohai.platform.fireforestplatform.utils.LatLngChangeNew.calWGS84toBD09(currentAround.getPosition().getLat(), currentAround.getPosition().getLng());
        com.baidu.mapapi.model.LatLng point = new com.baidu.mapapi.model.LatLng(doubles[0], doubles[1]);
        flyBaiduMapZoom(doubles[0],doubles[1],17);
    }

    @Override
    public void onAroundItemClickListener(Resource resource) {
        showDetail(resource);
    }

    private void showDetail(Resource resource) {
        currentAround = resource;
        aroundDialog.dismiss();
        //飞到精确点上
        initAroundFlyMap();
        //加载周边资源详细数据

        otherresourcenameview.setText(resource.getName());
        otherresoucedizhiview.setText(new CommonUtils().parseNull(resource.getAddress(),""));
        if(resource.getAddress()==null){
            otherresoucedizhiview.setVisibility(View.GONE);
        }
        otherresourcejingweiduview.setText(resource.getPosition().getLng()+","+resource.getPosition().getLat());
        RxViewAction.clickNoDouble(iv_guide).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                //导航
                try {
                    NaviSetting.updatePrivacyShow(AroundActivity.this, true, true);
                    NaviSetting.updatePrivacyAgree(AroundActivity.this, true);
                    double[] startGroup = bdToGaoDe(Double.parseDouble(AppDelegate.latitude),Double.parseDouble(AppDelegate.longitude));
                    double[] endGroup = new double[0];
                    endGroup = LatLngChangeNew.calWGS84toGCJ02(resource.getPosition().getLat(),resource.getPosition().getLng());
                    Poi start = new Poi("我的位置", new com.amap.api.maps.model.LatLng(startGroup[0], startGroup[1]), "");
                    Poi end = new Poi(resource.getName(), new com.amap.api.maps.model.LatLng(endGroup[0], endGroup[1]), "");
                    AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
                    params.setUseInnerVoice(true);
                    AmapNaviPage.getInstance().showRouteActivity(AroundActivity.this, params, AroundActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        res_edit.setVisibility(View.GONE);
        res_delete.setVisibility(View.GONE);
        /*RxViewAction.clickNoDouble(res_edit).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                editRes(resource.getResourceType(),resource.getId(),resource.obj);
            }
        });
        RxViewAction.clickNoDouble(res_delete).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                deleteRes(resource.getResourceType(),resource.getId());
            }
        });*/
        otherresourceinfoDialog.show();
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
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

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

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }
}