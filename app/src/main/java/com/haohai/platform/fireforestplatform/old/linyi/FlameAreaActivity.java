package com.haohai.platform.fireforestplatform.old.linyi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;
import com.haohai.platform.fireforestplatform.utils.CommonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;

public class FlameAreaActivity extends HhBaseActivity implements SensorEventListener, DatePicker.OnDateChangedListener, BaiduMap.OnMapClickListener, BaiduMap.OnMarkerClickListener {
    TextView tv_title;
    ImageView iv_back;
    private MapView baiduMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private TextView tv_clear;
    private TextView tv_go;
    private Dialog searchDialog;
    private View searchInflater;
    private Dialog resultDialog;
    private View resultInflater;
    private FrameLayout fl_date;
    private FrameLayout fl_date_end;
    private FrameLayout fl_start;
    private FrameLayout fl_end;
    private TextView tv_search;
    private TextView tv_date;
    private TextView tv_date_end;
    private TextView tv_start;
    private TextView tv_end;
    private ImageView cha;
    private ImageView cha_result;
    private int status = 0;//0未查询 1日期 2查询结束
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

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
                if (isFirstLoc && latLng==null) {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(18.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }
        }
    };
    private boolean look;
    private String pointFlame;
    private String latLng;
    private String[] parseLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flame_line);
        look = getIntent().getBooleanExtra("look", false);
        pointFlame = getIntent().getStringExtra("point");
        latLng = getIntent().getStringExtra("latLng");
        if(latLng!=null){
            parseLatLng = latLng.split(",");
        }
        init();
        startLocation();

        initOldDraw();
        if(latLng!=null){
            initFire();
            getAroundArea();
        }
    }

    private void getAroundArea() {
        double[] doubles = new LatLngChangeNew().calBD09toWGS84(Double.parseDouble(parseLatLng[0]),Double.parseDouble(parseLatLng[1]));
        //http://127.0.0.1:10100/fire/api/planBurnOff/getPlanBurnOffByScope?scope=1500&centre=POINT(119.219 36.6613)查询附近区域
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "fire/api/planBurnOff/getPlanBurnOffByScope");
        params.addParameter("scope","5000");
        params.addParameter("centre","POINT(" + doubles[1]+ " " + doubles[0] + ")");
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType", "Internet");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("TAG", "onSuccess: params " + params );
                    Log.e("TAG", "onSuccess: result " + result );
                    JSONObject object = new JSONObject(result);
                    JSONArray data = object.getJSONArray("data");
                    for (int m = 0; m < data.length(); m++) {
                        JSONObject obj = (JSONObject) data.get(m);
                        String position = obj.getString("position");
                        pointFlame = position;
                        areaPointList.clear();
                        //解析区域数据 LINESTRING(119.919 36.9613,119.219 36.9613,119.219 36.6613)
                        pointFlame = pointFlame.replace("LINESTRING(","");
                        pointFlame = pointFlame.replace(")","");
                        String[] points = pointFlame.split(",");
                        for (int i = 0; i < points.length; i++) {
                            String po = points[i];
                            String[] p_ = po.split(" ");
                            LatLng latLng = new LatLng(Double.parseDouble(p_[1]), Double.parseDouble(p_[0]));
                            if(i==0){
                                Log.e("TAG", "run: flyBaiduMap" + latLng.longitude + "," + latLng.latitude );
                                flyBaiduMap(latLng.latitude,latLng.longitude);
                            }
                            areaPointList.add(latLng);
                        }
                        parseDraw();
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

    private void initFire() {
        //定义Maker坐标点
        LatLng point = new LatLng(Double.parseDouble(parseLatLng[0]),Double.parseDouble(parseLatLng[1]));
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_fire);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);


        //飞到精确点上
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(point).zoom(17);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    private void initOldDraw() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(pointFlame!=null && pointFlame.length()!=0){
                    Log.e("TAG", "onCreate: pointFlame init = " + pointFlame );
                    areaPointList.clear();
                    //解析区域数据 LINESTRING(119.919 36.9613,119.219 36.9613,119.219 36.6613)
                    pointFlame = pointFlame.replace("LINESTRING(","");
                    pointFlame = pointFlame.replace(")","");
                    String[] points = pointFlame.split(",");
                    for (int i = 0; i < points.length; i++) {
                        String po = points[i];
                        String[] p_ = po.split(" ");
                        double[] calWGS84toBD09 = LatLngChangeNew.calWGS84toBD09(Double.parseDouble(p_[1]),Double.parseDouble(p_[0]));
                        if(i==0){
                            Log.e("TAG", "run: flyBaiduMap" + calWGS84toBD09[1] + "," + calWGS84toBD09[0] );
                            flyBaiduMap(calWGS84toBD09[0],calWGS84toBD09[1]);
                        }
                        areaPointList.add(new LatLng(calWGS84toBD09[0],calWGS84toBD09[1]));
                    }
                    parseDraw();
                }
            }
        },2000);
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

    private void flyBaiduMap(double lat, double lng) {
        //飞到精确点上
        LatLng ll = new LatLng(
                lat, lng);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(20);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

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

        if (null != mBaiduMap) {
            mBaiduMap.clear();
        }

        if (null != baiduMapView) {
            baiduMapView.onDestroy();
        }
    }

    private List<LatLng> areaPointList = new ArrayList<>();
    @Override
    public void onMapClick(LatLng latLng) {
        if(look){
            return;
        }
        areaPointList.add(latLng);
        parseDraw();
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    private void parseDraw() {
        if(areaPointList.size() > 2){
            if(latLng == null){
                mBaiduMap.clear();
            }
            drawArea();
        }else if (areaPointList.size() == 2){
            if(latLng == null){
                mBaiduMap.clear();
            }
            //绘制线
            drawLines();
        }else{
            if(latLng!=null){
                return;
            }
            //绘制点
            //定义Maker坐标点
            LatLng point = areaPointList.get(0);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_fire);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
        }
    }

    private Overlay LinesOverlay;
    private Overlay AreaOverlay;
    private void drawLines() {
        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(3)
                .color(0xAA1E90FF)
                .points(areaPointList);
        //在地图上绘制折线
        //mPloyline 折线对象
        LinesOverlay = mBaiduMap.addOverlay(mOverlayOptions);
    }

    private StringBuilder point;
    private void drawArea() {
        //构造PolygonOptions
        PolygonOptions mPolygonOptions = new PolygonOptions()
                .points(areaPointList)
                .fillColor(0x661E90FF) //填充颜色
                .stroke(new Stroke(3, 0xAA1E90FF)); //边框宽度和颜色

        //在地图上显示多边形
        AreaOverlay = mBaiduMap.addOverlay(mPolygonOptions);
        point = new StringBuilder("LINESTRING(");//LINESTRING(119.919 36.9613,119.219 36.9613,119.219 36.6613)
        for (int i = 0; i < areaPointList.size(); i++) {
            LatLng latLng = areaPointList.get(i);
            double[] bd09toWGS84 = new LatLngChangeNew().calBD09toWGS84(latLng.latitude, latLng.longitude);
            if(i == 0){
                point.append(bd09toWGS84[1]).append(" ").append(bd09toWGS84[0]);
            }else if(i == areaPointList.size()-1){
                point.append(",").append(bd09toWGS84[1]).append(" ").append(bd09toWGS84[0]).append(")");
            }else{
                point.append(",").append(bd09toWGS84[1]).append(" ").append(bd09toWGS84[0]);
            }
        }
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
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

    private void init() {
        tv_title = findViewById(R.id.tv_title);
        iv_back = findViewById(R.id.iv_back);
        baiduMapView = findViewById(R.id.baidu_mapview);
        tv_clear = findViewById(R.id.tv_clear);
        tv_go = findViewById(R.id.tv_go_to);
        if(look){
            tv_title.setText("查看区域");
            tv_go.setVisibility(View.GONE);
            tv_clear.setVisibility(View.GONE);
        }
        initDateTime();
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                finish();
            }
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

        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);


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
        fl_date = searchInflater.findViewById(R.id.fl_date);
        fl_date_end = searchInflater.findViewById(R.id.fl_date_end);
        fl_start = searchInflater.findViewById(R.id.fl_start);
        fl_end = searchInflater.findViewById(R.id.fl_end);
        tv_date = searchInflater.findViewById(R.id.tv_date);
        tv_date_end = searchInflater.findViewById(R.id.tv_date_end);
        tv_start = searchInflater.findViewById(R.id.tv_start);
        cha = searchInflater.findViewById(R.id.cha);
        tv_end = searchInflater.findViewById(R.id.tv_end);
        tv_search = searchInflater.findViewById(R.id.tv_search);
        RxViewAction.clickNoDouble(fl_date).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                isStart = true;
                showDataDialog();
            }
        });
        RxViewAction.clickNoDouble(fl_start).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                isStart = true;
                showTimeDialog();
            }
        });
        RxViewAction.clickNoDouble(fl_date_end).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                isStart = false;
                showDataDialog();
            }
        });
        RxViewAction.clickNoDouble(fl_end).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                isStart = false;
                showTimeDialog();
            }
        });
        RxViewAction.clickNoDouble(tv_go).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if(areaPointList.size()<3){
                    Toast.makeText(FlameAreaActivity.this, "请选择一个区域", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent data = new Intent();
                data.putExtra("point",point.toString());
                LatLng latLng = areaPointList.get(1);
                double[] doubles = new LatLngChangeNew().calBD09toWGS84(latLng.latitude, latLng.longitude);
                //POINT(119.219 36.992)
                data.putExtra("centre","POINT(" + doubles[1] + " " + doubles[0] + ")");
                setResult(RESULT_OK, data);
                Log.e("TAG", "run: point = " + point );
                finish();
            }
        });
        RxViewAction.clickNoDouble(tv_clear).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                areaPointList.clear();
                mBaiduMap.clear();
                Toast.makeText(FlameAreaActivity.this, "已清除", Toast.LENGTH_SHORT).show();
            }
        });
        RxViewAction.clickNoDouble(cha).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                searchDialog.dismiss();
            }
        });
        RxViewAction.clickNoDouble(tv_search).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if(tv_date.getText().toString().contains("请选择")){
                    Toast.makeText(FlameAreaActivity.this, "请选择开始日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tv_start.getText().toString().contains("请选择")){
                    Toast.makeText(FlameAreaActivity.this, "请选择开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tv_date_end.getText().toString().contains("请选择")){
                    Toast.makeText(FlameAreaActivity.this, "请选择结束日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tv_end.getText().toString().contains("请选择")){
                    Toast.makeText(FlameAreaActivity.this, "请选择结束时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                postData();
            }
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
        //searchDialog.show();

    }

    private void postData() {

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
                date.append(String.valueOf(year));
                if (month < 9) {
                    date.append("-0").append(String.valueOf(month + 1));
                } else {
                    date.append("-").append(String.valueOf(month + 1));
                }
                if (day < 10) {
                    date.append("-0").append(String.valueOf(day));
                } else {
                    date.append("-").append(String.valueOf(day));
                }
                if(isStart){
                    tv_date.setText(date);
                }else{
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
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);
    }

    private boolean isStart = true;
    /**
     * 日期选择控件
     */
    private void showTimeDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isStart){
                    tv_start.setText("");
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_start.append(" 0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_start.append(" 0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_start.append(" " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_start.append(" " + chooseHour + ":" + chooseMinute + ":00");
                    }
                }else{
                    tv_end.setText("");
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_end.append(" 0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_end.append(" 0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_end.append(" " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_end.append(" " + chooseHour + ":" + chooseMinute + ":00");
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);  //设置当前小时
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setMinute(minute); //设置当前分（0-59）
        }

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