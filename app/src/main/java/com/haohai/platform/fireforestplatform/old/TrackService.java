package com.haohai.platform.fireforestplatform.old;

import android.Manifest;
import android.app.AlarmManager;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.Ext;
import com.haohai.platform.fireforestplatform.event.WalkEvent;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public class TrackService extends Service implements SensorEventListener {

    private static final String TAG = TrackService.class.getSimpleName();
    private static final double MAX_VALID_DISTANCE_KM = 0.2d;
    public static final String ACTION_RESTART_TRACK_SERVICE = "com.haohai.platform.fireforestplatform.action.RESTART_TRACK_SERVICE";
    private static final long TRACK_INTERVAL_MS = 10_000L;
    private static final int TRACK_NOTIFICATION_ID = 110;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    //地理编码
    private GeoCoder mSearch;
    private String address = "";


    public TrackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final Handler trackHandler = new Handler(Looper.getMainLooper());
    private PowerManager.WakeLock wakeLock;
    private boolean hasLocationStarted = false;
    private boolean isManualStop = false;
    private MediaPlayer mediaPlayer;
    private final Runnable trackRunnable = new Runnable() {
        @Override
        public void run() {
            search();
            requestTrackLocation();
            parseDistance();
            uploadLocation();
            trackHandler.postDelayed(this, TRACK_INTERVAL_MS);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        initWakeLock();
        //addVirtualLine();

        initBaiduLoc();
        uploadQueue();

        //检测传感器
        boolean a = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        if (a) {
            CommonData.hasSensor = true;
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        //创建新的地理编码检索实例；
        mSearch = GeoCoder.newInstance();


        //创建地理编码检索监听者；
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                }

                //获取地理编码结果
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                }

                //获取反向地理编码结果
                //      Log.e(TAG, "onGetReverseGeoCodeResult: getAddress ==" + result.getAddress());
                //    Log.e(TAG, "onGetReverseGeoCodeResult: getBusinessCircle ==" + result.getBusinessCircle());
                //     Log.e(TAG, "onGetReverseGeoCodeResult: getSematicDescription ==" + result.getSematicDescription());
                //在result中获取点击最近地址
                List<PoiInfo> poiList = result.getPoiList();
                if(poiList==null){
                    return;
                }
                Log.e(TAG, "onGetReverseGeoCodeResult: size " + poiList.size() );
                if (poiList.size() == 0) {
                    address = "";
                } else {
                    PoiInfo poiInfo = poiList.get(0);
                    Log.e(TAG, "onGetReverseGeoCodeResult: ----" + poiInfo.toString() );
                    address = poiInfo.city + poiInfo.address;
                }

            }
        };
        //设置地理编码检索监听者；
        mSearch.setOnGetGeoCodeResultListener(listener);
        search();

    }

    public void search(){
        try{
            //发起地理编码检索；
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(CommonData.lat,CommonData.lng)));
        }catch (Exception e){
            Log.e(TAG, "search: " + e.getMessage());
        }
    }

    private void uploadQueue() {
        trackHandler.removeCallbacks(trackRunnable);
        trackHandler.post(trackRunnable);
    }

    private void parseDistance() {
        syncWalkDistanceDay();
        Log.e(TAG, "changeUserPosition: date = " + getWalkDayKey());
        if (CommonData.lat == 0 || CommonData.lng == 0) {
            CommonData.dis_int = 0;
            return;
        }
        if (CommonData.lat_old == 0 || CommonData.lng_old == 0) {
            updateLastLocation();
            CommonData.dis_int = 0;
            return;
        }
        double distance = CommonUtil.distance(CommonData.lng_old, CommonData.lat_old, CommonData.lng, CommonData.lat);
        HhLog.e("距离 distance " + distance + "千米，new: " + CommonData.lng + "，" + CommonData.lat + " old: " + CommonData.lng_old + "," + CommonData.lat_old);
        if (distance <= 0) {
            CommonData.dis_int = 0;
            updateLastLocation();
            return;
        }
        if (distance > MAX_VALID_DISTANCE_KM) {
            CommonData.dis_int = 0;
            Log.e(TAG, "changeUserPosition: " + distance + " out testInfo");
            updateLastLocation();
            return;
        }
        CommonData.dis_int = parseDistanceMeter(distance);
        if (CommonData.hasSign && CommonData.dis_int > 0) {
            CommonData.walkDistance += CommonData.dis_int;
            SPUtils.put(this, SPValue.walk, CommonData.walkDistance);
            HhLog.e("当前巡护距离 " + CommonData.walkDistance + " 米，较上次 " + CommonData.dis_int + "米");
            EventBus.getDefault().post(new WalkEvent());
            Log.e(TAG, "changeUserPosition: " + distance + " in testInfo");
        }
        Log.e(TAG, "changeUserPosition: distance = " + distance);
        Log.e(TAG, "changeUserPosition: LatLng = " + CommonData.lng_old + "," + CommonData.lat_old + " | " + CommonData.lng + "," + CommonData.lat);
        updateLastLocation();
    }

    private void uploadLocation() {
        if (CommonData.lng == 0 || String.valueOf(CommonData.lng).contains("E")) {
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        JSONObject posObj = new JSONObject();
        String id = (String) SPUtils.get(HhApplication.getInstance(), SPValue.id, "");
        double[] doubles = LatLngChangeNew.calBD09toWGS84(Double.parseDouble(String.valueOf(CommonData.lat)), Double.parseDouble(String.valueOf(CommonData.lng)));
        try {
            jsonObject.put("userId", id);
            posObj.put("lat", doubles[0]);
            posObj.put("lng", doubles[1]);
            jsonObject.put("position", posObj);
            jsonObject.put("cityName",address);
            jsonObject.put("dayTotalDistance", CommonData.hasSign?CommonData.walkDistance:0);//总距离
            jsonObject.put("distance", CommonData.hasSign?CommonData.dis_int:0);//距离上一次距离

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HhLog.e("position params " + jsonObject.toString());
        HhHttp.postStringTrackUpload()
                .url(URLConstant.POST_POSITION)
                .content(jsonObject.toString())
                .build()
                .execute(new LoggedInStringCallback(null, this) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("position " + response);
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString() + URLConstant.POST_POSITION);
                    }
                });
    }

    void initBaiduLoc() {
        mLocationClient = new LocationClient(HhApplication.getInstance());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        //LocationMode.Fuzzy_Locating, 模糊定位模式；v9.2.8版本开始支持，可以降低API的调用频率，但同时也会降低定位精度；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(5000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(true);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setNeedNewVersionRgc(true);
        //可选，设置是否需要最新版本的地址信息。默认需要，即参数为true

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isManualStop = false;
        ensureForeground();
        ensureWakeLock();
        ensureAlivePlayer();
        requestTrackLocation();
        uploadQueue();

        return START_REDELIVER_INTENT;
    }

    /*退出登录回调*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Ext ext) {
        isManualStop = true;
        trackHandler.removeCallbacksAndMessages(null);
        releaseWakeLock();
        releaseAlivePlayer();
        mLocationClient.stop();
        myListener = null;
        stopSelf();
    }

    private void getBaiduLocation() {
        mLocationClient.start();
        hasLocationStarted = true;
        HhLog.e("getBaiduLocation");
    }

    private void requestTrackLocation() {
        try {
            if (!hasLocationStarted) {
                getBaiduLocation();
            } else {
                requestLocation();
            }
        } catch (Exception e) {
            HhLog.e("requestTrackLocation " + e.getMessage());
            try {
                reLocation();
                hasLocationStarted = true;
            } catch (Exception ex) {
                HhLog.e("reLocation " + ex.getMessage());
            }
        }
    }

    private void syncWalkDistanceDay() {
        String today = getWalkDayKey();
        String walkDay = String.valueOf(SPUtils.get(this, SPValue.walkDay, ""));
        if ("".equals(walkDay) || "null".equals(walkDay)) {
            SPUtils.put(this, SPValue.walkDay, today);
            return;
        }
        if (!today.equals(walkDay)) {
            CommonData.walkDistance = 0;
            CommonData.dis_int = 0;
            SPUtils.put(this, SPValue.walk, CommonData.walkDistance);
            SPUtils.put(this, SPValue.walkDay, today);
            EventBus.getDefault().post(new WalkEvent());
        }
    }

    private String getWalkDayKey() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    private int parseDistanceMeter(double distanceKm) {
        int distanceMeter = (int) Math.round(distanceKm * 1000d);
        if (distanceMeter <= 0 && distanceKm > 0) {
            return 1;
        }
        return distanceMeter;
    }

    private void updateLastLocation() {
        CommonData.lng_old = CommonData.lng;
        CommonData.lat_old = CommonData.lat;
    }

    private void initWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager == null) {
            return;
        }
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getPackageName() + ":track_wake_lock");
        wakeLock.setReferenceCounted(false);
    }

    private void ensureWakeLock() {
        try {
            if (wakeLock != null && !wakeLock.isHeld()) {
                wakeLock.acquire();
            }
        } catch (Exception e) {
            HhLog.e("ensureWakeLock " + e.getMessage());
        }
    }

    private void releaseWakeLock() {
        try {
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
        } catch (Exception e) {
            HhLog.e("releaseWakeLock " + e.getMessage());
        }
    }

    private void ensureAlivePlayer() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alive);
            }
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
            HhLog.e("ensureAlivePlayer " + e.getMessage());
        }
    }

    private void releaseAlivePlayer() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            HhLog.e("releaseAlivePlayer " + e.getMessage());
        }
    }

    private void ensureForeground() {
        Notification notification;
        Intent intent = new Intent(this, MainActivity.class);
        int pendingIntentFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlag);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("location", "location", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
            Notification.Builder builder = new Notification.Builder(this, "location");
            builder.setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_icon))
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_icon)
                    .setContentText("为您持续巡护中...")
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(true)
                    .setOnlyAlertOnce(true);
            notification = builder.build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_icon)
                    .setContentText("为您持续巡护中...")
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
        }
        startForeground(TRACK_NOTIFICATION_ID, notification);
    }

    private void scheduleRestart() {
        Intent intent = new Intent(this, TrackKeepAliveReceiver.class);
        intent.setAction(ACTION_RESTART_TRACK_SERVICE);
        int pendingIntentFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, pendingIntentFlag);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        long triggerAtMillis = System.currentTimeMillis() + 5000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    private void requestLocation() {
        mLocationClient.requestLocation();
        HhLog.e("requestLocation");
    }

    private void reLocation() {
        mLocationClient.stop();
        mLocationClient.restart();
        HhLog.e("reLocation");
    }


    /**
     * 获取当前位置经纬度
     *
     * @return
     */
    @JavascriptInterface
    public String getLocationOld() {
        //获得位置服务
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = judgeProvider(locationManager);
        //有位置提供器的情况
        List<String> providerList = locationManager.getProviders(true);
        // 测试一般都在室内，这里颠倒了书上的判断顺序
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            // 当没有可用的位置提供器时，弹出Toast提示用户
            //Toast.makeText(getContext(), "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();

        }
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            try {
                Log.e(TAG, "getLocation: 84 " + location.getLongitude() + "," + location.getLatitude());
                double[] doubles = LatLngChangeNew.calWGS84toBD09(location.getLatitude(), location.getLongitude());
                CommonData.lng = doubles[1];
                CommonData.lat = doubles[0];
                Log.e(TAG, "getLocation: bd09 " + doubles[1] + "," + doubles[0]);
                return doubles[1] + "," + doubles[0];
            } catch (Exception e) {
                return "0.00,0.00";
            }

        } else {
            CommonData.lng = 0;
            CommonData.lat = 0;
        }
        return null;
    }


    /**
     * 获取当前位置经纬度
     *
     * @return
     */
    public void getLocation() {
        //获得位置服务
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_HIGH);//低功耗

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "您未开启定位权限", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.0001f, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG, "onLocationChanged: " + location.getLongitude() + "," + location.getLatitude());
                //Toast.makeText(HhApplication.getInstance(), "原生 onLocationChanged: " +  location.getLatitude() +"," +location.getLongitude(), Toast.LENGTH_LONG).show();
                double longitude = 0.00;
                double latitude = 0.00;
                try {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                } catch (Exception e) {

                }

                double[] doubles = LatLngChangeNew.calWGS84toBD09(latitude, longitude);
                CommonData.lng_old = CommonData.lng;
                CommonData.lat_old = CommonData.lat;
                CommonData.lng = doubles[1];
                CommonData.lat = doubles[0];
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "GPS已开启", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "请打开GPS", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "请打开GPS和使用网络定位以提高精度", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        // 获取最好的定位方式
        String provider = locationManager.getBestProvider(criteria, true); // true 代表从打开的设备中查找

        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        // 测试一般都在室内，这里颠倒了书上的判断顺序
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            // 当没有可用的位置提供器时，弹出Toast提示用户
            Toast.makeText(this, "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();
            return;
        }


        //为了压制getLastKnownLocation方法的警告
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // return null;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        double longitude = 0.00;
        double latitude = 0.00;
        try {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            double[] doubles = LatLngChangeNew.calWGS84toBD09(location.getLatitude(), location.getLongitude());
            CommonData.lng = doubles[1];
            CommonData.lat = doubles[0];
            Log.e(TAG, "getLocation: --" + CommonData.lng);
            Log.e(TAG, "getLocation: *--" + CommonData.lat);
        } catch (Exception e) {

        }
    }

    /**
     * 定位器provider
     *
     * @param locationManager
     * @return
     */
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;//网络定位
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;//GPS定位
        } else {
            //Toast.makeText(getContext(), "未开启本应用地理位置信息，请先开启！", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        trackHandler.removeCallbacksAndMessages(null);
        mSensorManager.unregisterListener(this);
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        hasLocationStarted = false;
        releaseWakeLock();
        releaseAlivePlayer();
        if (!isManualStop) {
            scheduleRestart();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (!isManualStop) {
            scheduleRestart();
        }
    }

    private boolean hasNotice = false;

    @Override
    public void onSensorChanged(SensorEvent event) {
        double value = event.values[0];
        if (value != 0) {
            hasNotice = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
