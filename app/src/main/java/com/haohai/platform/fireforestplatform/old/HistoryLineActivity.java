package com.haohai.platform.fireforestplatform.old;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.SupplementContent;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.TransportMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.bean.Person;
import com.haohai.platform.fireforestplatform.old.bean.Tree;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryLineActivity extends BaseActivity implements SensorEventListener, DatePicker.OnDateChangedListener, TreeViewBinder.OnTreeClick, PersonViewBinder.OnPersonClick {
    FrameLayout fl_bar;
    IndicatorSeekBar bar;
    LinearLayout ll_bitmaps;
    ImageView info_show;
    TextView info_index;
    TextView info_text;
    ImageView iv_back;
    TextView tv_find;
    private MapView baiduMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private Dialog searchDialog;
    private View searchInflater;
    private Dialog resultDialog;
    private View resultInflater;
    private LinearLayout ll_page1;
    private RecyclerView ll_page2;
    private TextView tv_title;
    private FrameLayout fl_date;
    private FrameLayout fl_date_end;
    private FrameLayout fl_start;
    private FrameLayout fl_end;
    private FrameLayout fl_user;
    private FrameLayout fl_back;
    private TextView tv_search;
    private TextView tv_date;
    private TextView tv_date_end;
    private TextView tv_start;
    private TextView tv_end;
    private TextView tv_user;
    private TextView tv_result_title;
    private ImageView cha;
    private ImageView cha_result;
    private TextView result_km;
    private TextView result_time;
    private TextView result_start;
    private TextView result_end;
    private final int status = 0;//0未查询 1日期 2查询结束
    private final BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         *
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            mBaiduMap.getUiSettings().setCompassEnabled(true);
            // MapView 销毁后不在处理新接收的位置
            if (location == null || baiduMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)// 设置定位数据的精度信息，单位：米
                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon)
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation
                    || location.getLocType() == BDLocation.TypeOffLineLocation) {
                if (isFirstLoc) {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(18.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }
        }
    };
    private final String TAG = HistoryLineActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_history_line);
        chooseUserId = getIntent().getStringExtra("id");
        chooseUserName = getIntent().getStringExtra("name");
        mHandler = new Handler(getMainLooper());
        init();
        initTree();
        startLocation();
    }


    private MultiTypeAdapter adapter;
    private final List<Object> items = new ArrayList<>();
    private List<Tree> treeList = new ArrayList<>();
    private void initTree() {
        RequestParams params = new RequestParams(URLConstant.GET_GRID_TREES);
        params.addHeader("Authorization", "bearer " + CommonData.token);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: initTree" + result );
                try {
                    JSONObject object = new JSONObject(result);
                    treeList = new Gson().fromJson(String.valueOf(object.getJSONArray("data")),new TypeToken<List<Tree>>(){}.getType());

                    treeList = TreeUtils.parseLevel(treeList);
                    initTreeData();

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

    //在这处理数据逻辑  获取到人员的条目 然后吧人员条目根据id 放到treeList 里 后调佣initTreeData ()进行更新

    private void initTreeData () {
        items.clear();
        if (treeList.size() == 0) {
            items.add(new Empty("暂无数据"));
        } else {
            items.addAll(treeList);
        }
        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }


    /**
     * 启动定位
     */
    private void startLocation() {
        // 定位初始化
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(mListener);
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        locationClientOption.setCoorType("bd09ll");
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        locationClientOption.setScanSpan(1000);
        //可选，设置是否使用gps，默认false
        locationClientOption.setOpenGps(true);
        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为true
        locationClientOption.setIsNeedAddress(true);
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation
        locationClientOption.setIsNeedLocationPoiList(true);
        // 设置定位参数
        mLocationClient.setLocOption(locationClientOption);
        // 开启定位
        mLocationClient.start();
    }

    private final List<LatLng> lineList = new ArrayList<>();
    private final List<String> timeList = new ArrayList<>();
    private final List<LatLng> allList = new ArrayList<>();
    private final double distance = 0;

    private String searchId;
    private void postData() {
        searchId = (String) SPUtils.get(this, SPValue.id,"");
        mBaiduMap.clear();
        timeList.clear();
        allList.clear();
        SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = smf.parse(tv_date.getText().toString() + " " + tv_start.getText().toString());
            endDate = smf.parse(tv_date_end.getText().toString() + " " + tv_end.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] longs = MapHelper.timeDistance(startDate.getTime(), endDate.getTime());

        //百度鹰眼轨迹查询
        // 请求标识
        int tag = 1;
        // 轨迹服务ID
        long serviceId = 235910;
        // 设备标识
        String entityName = chooseUserId;
        // 创建历史轨迹请求实例
        HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(tag, serviceId, entityName);

        // 开始时间 时间戳
        long startTime = parseDate(startDate.getTime());
        Log.e(TAG, "postData: startTime " + startTime );
        // 结束时间 时间戳
        long endTime = parseDate(endDate.getTime());
        // 设置开始时间
        historyTrackRequest.setStartTime(startTime);
        // 设置结束时间
        historyTrackRequest.setEndTime(endTime);


        // 设置需要纠偏
        historyTrackRequest.setProcessed(true);
        //设置断点补偿
        historyTrackRequest.setSupplementContent(SupplementContent.distance_and_points);


        // 创建纠偏选项实例
        ProcessOption processOption = new ProcessOption();
        // 设置需要去噪
        processOption.setNeedDenoise(true);
        // 设置需要抽稀
        processOption.setNeedVacuate(true);
        // 设置需要绑路
        processOption.setNeedMapMatch(true);
        // 设置精度过滤值(定位精度大于100米的过滤掉)
        processOption.setRadiusThreshold(100);
        // 设置交通方式
        processOption.setTransportMode(TransportMode.walking);
        // 设置纠偏选项
        historyTrackRequest.setProcessOption(processOption);


        // 设置里程填充方式
        historyTrackRequest.setSupplementMode(SupplementMode.walking);


        // 初始化轨迹监听器
        OnTrackListener mTrackListener = new OnTrackListener() {
            // 历史轨迹回调
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {
                if((longs[0] * 24 + longs[1])>24 || ((longs[0] * 24 + longs[1])==24)&&(longs[2]>0||longs[3]>0)){
                    Toast.makeText(HistoryLineActivity.this, "查询时间不能超过24小时", Toast.LENGTH_SHORT).show();
                    fl_bar.setVisibility(View.GONE);
                    return;
                }

                if(response.trackPoints == null){
                    return;
                }

                Log.e(TAG, "onHistoryTrackCallback: points = " + response.trackPoints.size() + " | "  + response.trackPoints );
                List<LatLng> list = new ArrayList<>();
                for (int i = 0; i < response.trackPoints.size(); i++) {
                    TrackPoint trackPoint = response.trackPoints.get(i);
                    if(trackPoint.getSpeed()<200){
                        com.baidu.trace.model.LatLng location = trackPoint.getLocation();
                        LatLng latLng = new LatLng(location.latitude,location.longitude);
                        list.add(latLng);
                        allList.add(latLng);
                        timeList.add(trackPoint.getCreateTime());
                    }
                }
                result_km.setText(parseSix(response.getDistance() + "") + "m");

                drawPolyLine(list);


                searchDialog.dismiss();
                tv_result_title.setText(tv_date.getText().toString() + "~" + tv_date_end.getText().toString() + "巡护轨迹");
                result_time.setText((longs[0] * 24 + longs[1]) + ":" + longs[2] + ":" + longs[3]);
                result_start.setText(tv_start.getText().toString());
                result_end.setText(tv_end.getText().toString());
                resultDialog.show();

                //轨迹Bar数据初始化
                bar.setMin(0f);
                bar.setMax(timeList.size()*1.0f - 1);
                Log.e(TAG, "onSuccess: timeList " + timeList );
                if(timeList.isEmpty()){
                    fl_bar.setVisibility(View.GONE);
                }else{
                    fl_bar.setVisibility(View.VISIBLE);
                    bar.setProgress(timeList.size()-1);
                    flyBaiduMap(allList.get(allList.size()-1).latitude,allList.get(allList.size()-1).longitude);
                }
            }
        };

        // 查询轨迹
        CommonData.mTraceClient.queryHistoryTrack(historyTrackRequest, mTrackListener);
    }

    private String parseSix(String str) {
        if (str.length() > 6) {
            return str.substring(0, 6);
        }
        return str;
    }
    private long parseDate(long date) {
        String str = date + "";
        if (str.length() > 10) {
            return Long.parseLong(str.substring(0, 10));
        }
        return date;
    }


    private final BitmapDescriptor mGreenTexture =
            BitmapDescriptorFactory.fromAsset("Icon_road_blue__.png");
    private final BitmapDescriptor mBitmapCar = BitmapDescriptorFactory.fromResource(R.drawable.ic_qi);
    private final BitmapDescriptor mBitmapStart = BitmapDescriptorFactory.fromResource(R.drawable.ic_qi);
    private final BitmapDescriptor mBitmapEnd = BitmapDescriptorFactory.fromResource(R.drawable.ic_zhong);
    private Polyline mPolyline;
    private Marker mMoveMarker;
    private Handler mHandler;
    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    private static final int TIME_INTERVAL = 30;
    private static final double DISTANCE = 0.000005;

    /**
     * 绘制轨迹
     */
    @SuppressLint("SetTextI18n")
    private void drawPolyLine(List<LatLng> list) {
        /*List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(i+1 <= list.size()-1){
                LatLng latLngL = list.get(i);
                LatLng latLngR = list.get(i+1);
                if(MapHelper.distance(latLngL.longitude,latLngL.latitude,latLngR.longitude,latLngR.latitude) > 5){
                    indexList.add(i);
                }
            }
        }
        for (int i = 0; i < indexList.size(); i++) {//[0,10,20,33,60]
            //[start~0,0~10,10~20,20~33,33~60,60~end]
            if(i == 0){
                List<LatLng> subList = list.subList(0,indexList.get(i));
                // 绘制纹理PolyLine
                PolylineOptions polylineOptions =
                        new PolylineOptions().points(subList).width(26).customTexture(mGreenTexture)
                                .dottedLine(false);
                mPolyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);
            }
            if(i > 0){
                List<LatLng> subList = list.subList(indexList.get(i-1),indexList.get(i));
                // 绘制纹理PolyLine
                PolylineOptions polylineOptions =
                        new PolylineOptions().points(subList).width(26).customTexture(mGreenTexture)
                                .dottedLine(false);
                mPolyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);
            }
            if(i == indexList.size()-1){
                List<LatLng> subList = list.subList(indexList.get(i),list.size());
                // 绘制纹理PolyLine
                PolylineOptions polylineOptions =
                        new PolylineOptions().points(subList).width(26).customTexture(mGreenTexture)
                                .dottedLine(false);
                mPolyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);
            }
        }*/
        // 绘制纹理PolyLine
        /*PolylineOptions polylineOptions =
                new PolylineOptions().points(list).width(26).customTexture(mGreenTexture)
                        .dottedLine(false);
        mPolyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);*/

        if(list.size()<2){
            Toast.makeText(this, "轨迹点位数据太少请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        OverlayOptions ooPolyline = new PolylineOptions().width(14).color(0xFF0099E1).points(list);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        mPolyline.setIsKeepScale(false);
        /*List<OverlayOptions> options = new ArrayList<OverlayOptions>();
        for (int i = 0; i < list.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            OverlayOptions option = new MarkerOptions()
                    .position(list.get(i))
                    .extraInfo(bundle)
                    .icon(BitmapDescriptorFactory.fromResource(com.haohai.platform.mapmodel.R.drawable.ic_blue_fire_));
            options.add(i, option);
        }
        mBaiduMap.addOverlays(options);*/
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try{
                    Bundle bundle = marker.getExtraInfo();
                    int index = bundle.getInt("index");
                    InfoWindow mInfoWindow;
                    //构造InfoWindow
                    View view = LayoutInflater.from(HistoryLineActivity.this).inflate(R.layout.history_info, null);
                    TextView info_index = view.findViewById(R.id.info_index);
                    TextView info_text = view.findViewById(R.id.info_text);
                    info_index.setText(index + "");
                    info_text.setText(timeList.get(index).substring(5, 19));
                    //构造InfoWindow
                    mInfoWindow = new InfoWindow(view, marker.getPosition(), -100);
                    //使InfoWindow生效
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }catch (Exception e){
                    Log.e(TAG, "onMarkerClick: " + e );
                }

                return false;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                for (int i = 0; i < list.size(); i++) {
                    LatLng model = list.get(i);
                    double distance = CommonUtil.distance(model.longitude, model.latitude, latLng.longitude, latLng.latitude);
                    Log.e(TAG, "onMapClick: distance = " + distance );
                    if(distance <= 0.1){
                        InfoWindow mInfoWindow;
                        //构造InfoWindow
                        View view = LayoutInflater.from(HistoryLineActivity.this).inflate(R.layout.history_info, null);
                        TextView info_index = view.findViewById(R.id.info_index);
                        TextView info_text = view.findViewById(R.id.info_text);
                        info_index.setText(i + "");
                        info_text.setText(timeList.get(i).substring(5, 19));
                        //构造InfoWindow
                        mInfoWindow = new InfoWindow(view, latLng, -100);
                        //使InfoWindow生效
                        mBaiduMap.showInfoWindow(mInfoWindow);
                        return;
                    }
                }
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                return;
            }
        });

        // 添加小车marker
        OverlayOptions markerOptions = new MarkerOptions()
                .flat(true)
                .anchor(0.5f, 0.5f)
                .icon(mBitmapCar).
                        position(list.get(0))
                .rotate((float) getAngle(0));
        //mMoveMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
        //绘制起点终点
        Bundle bundle = new Bundle();
        bundle.putInt("index", 0);
        OverlayOptions markerOptionsStart = new MarkerOptions()
                .icon(mBitmapStart).extraInfo(bundle).position(list.get(0));
        mBaiduMap.addOverlay(markerOptionsStart);
        Bundle bundleEnd = new Bundle();
        bundleEnd.putInt("index", list.size()-1);
        OverlayOptions markerOptionsEnd = new MarkerOptions()
                .icon(mBitmapEnd).extraInfo(bundleEnd).position(list.get(list.size() - 1));
        mBaiduMap.addOverlay(markerOptionsEnd);

        //默认显示最后一条info
        InfoWindow mInfoWindow;
        //构造InfoWindow
        View view = LayoutInflater.from(HistoryLineActivity.this).inflate(R.layout.history_info, null);
        TextView info_index = view.findViewById(R.id.info_index);
        TextView info_text = view.findViewById(R.id.info_text);
        info_index.setText(list.size()-1 + "");
        info_text.setText(timeList.get(list.size()-1).substring(5, 19));
        //构造InfoWindow
        mInfoWindow = new InfoWindow(view, list.get(list.size()-1), -100);
        //使InfoWindow生效
        mBaiduMap.showInfoWindow(mInfoWindow);

/*        int dex = 0;
        dex = list.size()/30;
        for (int i = 0; i < list.size(); i++) {
            if(dex!=0 && i%dex!=0){
                continue;
            }
            InfoWindow mInfoWindow;
            //构造InfoWindow
            View view = LayoutInflater.from(HistoryLineActivity.this).inflate(R.layout.history_info, null);
            TextView info_index = view.findViewById(R.id.info_index);
            TextView info_text = view.findViewById(R.id.info_text);
            //drawBitMap(timeList.get(i).substring(5, 19));
            info_index.setText(i + "");
            info_text.setText(timeList.get(i).substring(5, 19));
            *//*info_index.setText(i + "");
            info_text.setText(timeList.get(i).substring(5, 19));
            ll_bitmaps.buildDrawingCache();
            Bitmap bitmap = ll_bitmaps.getDrawingCache();
            info_show.setImageBitmap(bitmap);*//*

            //bitmap.recycle();

            *//*mBaiduMap.addOverlay(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)).
                            position(list.get(i)));*//*

            //构造InfoWindow
            mInfoWindow = new InfoWindow(view, list.get(i), -100);
            //使InfoWindow生效
            mBaiduMap.showInfoWindow(mInfoWindow);

        }*/
    }

    BitmapDescriptor mIconMarker;
    Bitmap bitmap;

    private void drawBitMap(String str) {
        BitmapDescriptor mIconMarker = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        float scale = this.getResources().getDisplayMetrics().density;
        Log.e("scale", "=" + scale);
        int width = mIconMarker.getBitmap().getWidth(), height = mIconMarker.getBitmap().getHeight();//marker的获取宽高
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444); //建立一个空的Bitmap
        bitmap = scaleWithWH(bitmap, width * scale, height * scale);//缩放
        //画笔进行添加文字（强烈推荐启舰的自定义控件三部曲http://blog.csdn.net/harvic880925/article/details/50995268）
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤
        paint.setColor(Color.RED);
        paint.setTextSize(14 * scale);

        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);//获取文字的范围
        //文字在mMarker中展示的位置
        float paddingLeft = (bitmap.getWidth() - bounds.width()) / 2;//在中间
        float paddingTop = (bitmap.getHeight() / scale);//在顶部

        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(str, paddingLeft, paddingTop, paint);

        //合并两个bitmap为一个
        canvas.drawBitmap(mIconMarker.getBitmap(), paddingLeft, paddingTop, null);//marker的位置
    }

    private Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }

    private void gc() {
        if (bitmap != null) {// 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    private void flyBaiduMap(double lat, double lng) {
        //飞到精确点上
        LatLng ll = new LatLng(
                lat, lng);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(20);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    /**
     * 根据点获取图标转的角度
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= mPolyline.getPoints().size()) {
            return -1.0;
        }
        LatLng startPoint = mPolyline.getPoints().get(startIndex);
        LatLng endPoint = mPolyline.getPoints().get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        } else if (slope == 0.0) {
            if (toPoint.longitude > fromPoint.longitude) {
                return -90;
            } else {
                return 90;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude
                - fromPoint.longitude));
        return slope;
    }

    private final int loopTag = 0;//防止跳点前一次轨迹

    /**
     * 循环进行移动逻辑
     */
    public void moveLooper(int loop) {
        new Thread() {
            public void run() {
                while (true) {
                    for (int i = 0; i < lineList.size() - 1; i++) {
                        if (loop != loopTag) {
                            return;
                        }
                        final LatLng startPoint = lineList.get(i);
                        final LatLng endPoint = lineList.get(i + 1);
                        mMoveMarker.setPosition(startPoint);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // refresh marker's rotate
                                if (baiduMapView == null) {
                                    return;
                                }
                                mMoveMarker.setRotate((float) getAngle(startPoint, endPoint));
                            }
                        });
                        double slope = getSlope(startPoint, endPoint);
                        // 是不是正向的标示
                        boolean isYReverse = (startPoint.latitude > endPoint.latitude);
                        boolean isXReverse = (startPoint.longitude > endPoint.longitude);
                        double intercept = getInterception(slope, startPoint);
                        double xMoveDistance =
                                isXReverse ? getXMoveDistance(slope) : -1 * getXMoveDistance(slope);
                        double yMoveDistance =
                                isYReverse ? getYMoveDistance(slope) : -1 * getYMoveDistance(slope);

                        for (double j = startPoint.latitude, k = startPoint.longitude;
                             j > endPoint.latitude == isYReverse
                                     && k > endPoint.longitude == isXReverse; ) {
                            LatLng latLng = null;

                            if (slope == Double.MAX_VALUE) {
                                latLng = new LatLng(j, k);
                                j = j - yMoveDistance;
                            } else if (slope == 0.0) {
                                latLng = new LatLng(j, k - xMoveDistance);
                                k = k - xMoveDistance;
                            } else {
                                latLng = new LatLng(j, (j - intercept) / slope);
                                j = j - yMoveDistance;
                            }

                            final LatLng finalLatLng = latLng;
                            if (finalLatLng.latitude == 0 && finalLatLng.longitude == 0) {
                                continue;
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (baiduMapView == null) {
                                        return;
                                    }
                                    if (loop != loopTag) {
                                        return;
                                    }
                                    mMoveMarker.setPosition(finalLatLng);
                                    // 设置 Marker 覆盖物的位置坐标,并同步更新与Marker关联的InfoWindow的位置坐标.
                                    mMoveMarker.setPositionWithInfoWindow(finalLatLng);
                                }
                            });
                            try {
                                Thread.sleep(TIME_INTERVAL);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }.start();
    }


    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * 1 / slope) / Math.sqrt(1 + 1 / (slope * slope)));
    }

    /**
     * 计算y方向每次移动的距离
     */
    private double getYMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {
        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != baiduMapView) {
            baiduMapView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        baiduMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (null != mBitmapCar) {
            mBitmapCar.recycle();
        }

        if (null != mGreenTexture) {
            mGreenTexture.recycle();
        }

        if (null != mBaiduMap) {
            mBaiduMap.clear();
        }

        if (null != baiduMapView) {
            baiduMapView.onDestroy();
        }
    }

    @Override
    public void onTreeClickListener() {

    }

    @Override
    public void onTreeItemClickListener(Tree tree) {

    }

    private String chooseUserId;
    private String chooseUserName;
    @Override
    public void onPersonClickListener(Person person) {
        searchPage = 0;
        tv_title.setText("查询巡护轨迹");
        ll_page2.setVisibility(View.GONE);
        ll_page1.setVisibility(View.VISIBLE);
        fl_back.setVisibility(View.GONE);
        tv_user.setText(person.getFullName());
        chooseUserId = person.getId();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || baiduMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locData);
        }
    }

    private int searchPage = 0;//0 时间选择  1 用户选择
    private void init() {
        if(chooseUserId==null||chooseUserId.isEmpty()){
            chooseUserId = (String) SPUtils.get(this,SPValue.id,"");//默认本账号UserId
        }
        fl_bar = findViewById(R.id.fl_bar);
        bar = findViewById(R.id.bar);
        bar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                float progressFloat = seekParams.progressFloat;
                int index = (int) (progressFloat/1);
                if(index > allList.size()-1){
                    index = allList.size()-1;
                }
                Log.e(TAG, "onSeeking: progressFloat " + progressFloat );
                Log.e(TAG, "onSeeking: index " + index );
                InfoWindow mInfoWindow;
                //构造InfoWindow
                View view = LayoutInflater.from(HistoryLineActivity.this).inflate(R.layout.history_info, null);
                TextView info_index = view.findViewById(R.id.info_index);
                TextView info_text = view.findViewById(R.id.info_text);
                info_index.setText(index + "");
                info_text.setText(timeList.get(index).substring(5, 19));
                //构造InfoWindow
                mInfoWindow = new InfoWindow(view, allList.get(index), -100);
                //使InfoWindow生效
                mBaiduMap.showInfoWindow(mInfoWindow);

                flyBaiduMap(allList.get(index).latitude,allList.get(index).longitude);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                Log.e(TAG, "onSeeking start : " + seekBar.getMin());
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                Log.e(TAG, "onSeeking stop : " + seekBar.getMax());
            }
        });
        ll_bitmaps = findViewById(R.id.ll_bitmaps);
        info_show = findViewById(R.id.info_show);
        info_index = findViewById(R.id.info_index);
        info_text = findViewById(R.id.info_text);
        iv_back = findViewById(R.id.iv_back);
        tv_find = findViewById(R.id.tv_find);
        baiduMapView = findViewById(R.id.baidu_mapview);
        iv_back.setOnClickListener(v -> {
            finish();
        });
        tv_find.setOnClickListener(v -> {
            searchDialog.hide();
            resultDialog.hide();
            searchDialog.show();
        });
        date = new StringBuffer();
        endDate = new StringBuffer();
        baiduMapView.showZoomControls(false);
        mBaiduMap = baiduMapView.getMap();
        //显示卫星图层
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationConfiguration myLocationConfiguration =
                new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
        mBaiduMap.getUiSettings().setCompassEnabled(false);
        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);

        //只显示道路 不显示其他标注
        mBaiduMap.showMapPoi(true);
        //设置最大最小缩放等级
        mBaiduMap.setMaxAndMinZoomLevel(18, 5);


        //定位初始化
        mLocationClient = new LocationClient(this);

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09"); // 设置坐标类型
        option.setScanSpan(1000);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();

        //查询巡护轨迹
        searchDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        searchInflater = LayoutInflater.from(this).inflate(R.layout.dialog_line_search, null);
        searchInflater.setMinimumWidth(10000);
        tv_title = searchInflater.findViewById(R.id.tv_title);
        ll_page1 = searchInflater.findViewById(R.id.ll_page1);
        ll_page2 = searchInflater.findViewById(R.id.ll_page2);
        fl_date = searchInflater.findViewById(R.id.fl_date);
        fl_date_end = searchInflater.findViewById(R.id.fl_date_end);
        fl_start = searchInflater.findViewById(R.id.fl_start);
        fl_end = searchInflater.findViewById(R.id.fl_end);
        fl_user = searchInflater.findViewById(R.id.fl_user);
        fl_back = searchInflater.findViewById(R.id.fl_back);
        tv_date = searchInflater.findViewById(R.id.tv_date);
        tv_date_end = searchInflater.findViewById(R.id.tv_date_end);
        tv_start = searchInflater.findViewById(R.id.tv_start);
        cha = searchInflater.findViewById(R.id.cha);
        tv_end = searchInflater.findViewById(R.id.tv_end);
        tv_user = searchInflater.findViewById(R.id.tv_user);
        tv_search = searchInflater.findViewById(R.id.tv_search);
        fl_date.setOnClickListener(v -> {
            isStart = true;
            showDataDialog();
        });
        fl_start.setOnClickListener(v -> {
            isStart = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showTimeDialog();
            }
        });
        fl_date_end.setOnClickListener(v -> {
            isStart = false;
            showDataDialog();
        });
        fl_back.setOnClickListener(v -> {
            searchPage = 0;
            tv_title.setText("查询巡护轨迹");
            ll_page2.setVisibility(View.GONE);
            ll_page1.setVisibility(View.VISIBLE);
            fl_back.setVisibility(View.GONE);
        });
        fl_user.setOnClickListener(v -> {
            searchPage = 1;
            tv_title.setText("选择查询用户");
            ll_page1.setVisibility(View.GONE);
            ll_page2.setVisibility(View.VISIBLE);
            fl_back.setVisibility(View.VISIBLE);
        });
        fl_end.setOnClickListener(v -> {
            isStart = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showTimeDialog();
            }
        });
        cha.setOnClickListener(v -> {
            searchDialog.dismiss();
        });
        tv_search.setOnClickListener(v -> {
            if (tv_date.getText().toString().contains("请选择")) {
                Toast.makeText(HistoryLineActivity.this, "请选择开始日期", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tv_start.getText().toString().contains("请选择")) {
                Toast.makeText(HistoryLineActivity.this, "请选择开始时间", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tv_date_end.getText().toString().contains("请选择")) {
                Toast.makeText(HistoryLineActivity.this, "请选择结束日期", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tv_end.getText().toString().contains("请选择")) {
                Toast.makeText(HistoryLineActivity.this, "请选择结束时间", Toast.LENGTH_SHORT).show();
                return;
            }
            postData();
        });
        searchDialog.setContentView(searchInflater);
        Window fireListWindow = searchDialog.getWindow();
        fireListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams fireListLp = fireListWindow.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        fireListLp.height = (int) (height * 0.5);
        fireListWindow.setAttributes(fireListLp);
        searchDialog.setCanceledOnTouchOutside(true);
        searchDialog.show();

        //查询结束
        resultDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        resultInflater = LayoutInflater.from(this).inflate(R.layout.dialog_line_result, null);
        resultInflater.setMinimumWidth(10000);
        tv_result_title = resultInflater.findViewById(R.id.tv_result_title);
        cha_result = resultInflater.findViewById(R.id.cha_result);
        result_km = resultInflater.findViewById(R.id.tv_km);
        result_time = resultInflater.findViewById(R.id.tv_time);
        result_start = resultInflater.findViewById(R.id.tv_start);
        result_end = resultInflater.findViewById(R.id.tv_end);
        cha_result.setOnClickListener(v -> {
            resultDialog.dismiss();
            searchDialog.show();
        });
        resultDialog.setContentView(resultInflater);
        Window fireListWindow2 = resultDialog.getWindow();
        fireListWindow2.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams fireListLp2 = fireListWindow2.getAttributes();
        WindowManager wm2 = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height2 = wm2.getDefaultDisplay().getHeight();
        fireListLp2.height = (int) (height2 * 0.2);
        fireListWindow2.setAttributes(fireListLp2);
        resultDialog.setCanceledOnTouchOutside(true);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ll_page2.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);
        TreeViewBinder binder = new TreeViewBinder();
        binder.setListener(this,this);
        binder.setPersonListener(this);//待优化
        adapter.register(Tree.class, binder);
        PersonViewBinder binder_person = new PersonViewBinder();
        binder_person.setListener(this);
        adapter.register(Person.class, binder_person);
        adapter.register(Empty.class, new EmptyViewBinder(this));
        ll_page2.setAdapter(adapter);
        assertHasTheSameAdapter(ll_page2, adapter);


        initDateTime();



        tv_user.setText(chooseUserName);
        fl_user.setClickable(false);
    }

    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private float mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private MyLocationData myLocationData;
    private float mCurrentAccracy;
    private boolean isFirstLoc = true;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (float) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(myLocationData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private StringBuffer date;
    private StringBuffer endDate;
    private int year;
    private int month;
    private int day;
    public int chooseHour;
    public int chooseMinute;

    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                if (endDate.length() > 0) { //清除上次记录的日期
                    endDate.delete(0, endDate.length());
                }
                date.append(year);
                if (month < 9) {
                    date.append("-0").append((month + 1));
                } else {
                    date.append("-").append((month + 1));
                }
                if (day < 10) {
                    date.append("-0").append(day);
                } else {
                    date.append("-").append(day);
                }
                if (isStart) {
                    tv_date.setText(date);
                } else {
                    tv_date_end.setText(date);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH);
        int day1 = date.get(Calendar.DATE);
        String endData = year1 - 10 + "-" + month1 + "-" + day1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(endData);
        } catch (ParseException e) {

        }
        long starTimre = date2.getTime();


        long endTimre = System.currentTimeMillis();

        datePicker.setMaxDate(endTimre);
        datePicker.setMinDate(starTimre);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month, day, this);
    }

    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);
        tv_date.setText(year + "-" + CommonUtil.parseZero(month+1) + "-" + CommonUtil.parseZero(day));
        tv_start.setText(CommonUtil.parseZero(chooseHour) + ":" + CommonUtil.parseZero(chooseMinute) + ":00");

        calendar.add(Calendar.HOUR_OF_DAY, 2);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);

        tv_date_end.setText(year + "-" + CommonUtil.parseZero(month+1) + "-" + CommonUtil.parseZero(day));
        tv_end.setText(CommonUtil.parseZero(chooseHour) + ":" + CommonUtil.parseZero(chooseMinute) + ":00");
    }

    private boolean isStart = true;

    /**
     * 日期选择控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTimeDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (isStart) {
                    tv_start.setText("");
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_start.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_start.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_start.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_start.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                } else {
                    tv_end.setText("");
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_end.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_end.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_end.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_end.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                }

                dialog.dismiss();
            }
        });
        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog timeDialog = builder1.create();
        View dialogView = View.inflate(this, R.layout.dialog_time, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        timePicker.setIs24HourView(true);   //设置时间显示为24小时

        timePicker.setHour(hour);  //设置当前小时
        timePicker.setMinute(minute); //设置当前分（0-59）

        timeDialog.setTitle("设置时间");
        timeDialog.setView(dialogView);
        timeDialog.show();


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {  //获取当前选择的时间
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                chooseHour = hourOfDay;
                chooseMinute = minute;
            }
        });
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }
}