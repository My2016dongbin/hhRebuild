package com.haohai.platform.fireforestplatform.old;

import android.Manifest;
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
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
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
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public class TrackService extends Service implements SensorEventListener {

    private static final String TAG = TrackService.class.getSimpleName();

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();


    public TrackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        //addVirtualLine();

        initBaiduLoc();
        getBaiduLocation();
        uploadQueue();

        //检测传感器
        boolean a = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        if (a) {
            CommonData.hasSensor = true;
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


    }

    private void uploadQueue() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getBaiduLocation();
                //getLocation();
                parseDistance();
                uploadLocation();
                uploadQueue();
            }
        }, 10000);
    }

    private boolean disState = false;
    private void parseDistance() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm");
        String format = simpleDateFormat.format(new Date());
        Log.e(TAG, "changeUserPosition: date = " + format);
        if (format.contains("00:00")) {//跨天清零
            CommonData.walkDistance = 0;
            //通知UI刷新巡护距离
            EventBus.getDefault().post(new WalkEvent());
            SPUtils.put(this, SPValue.walk, CommonData.walkDistance);
        }
        if (CommonData.lat_old != 0 && CommonData.lng_old != 0) {
            double distance = CommonUtil.distance(CommonData.lng_old, CommonData.lat_old, CommonData.lng, CommonData.lat);
            HhLog.e("距离 distance " + distance + "米，new: " + CommonData.lng + "，" + CommonData.lat + " old: " + CommonData.lng_old + "," + CommonData.lat_old);
            if (distance <= 0.2 && distance > 0 && (CommonData.hasSign)) {
                disState = true;
                /*if (!CommonData.hasSensor *//*|| CommonData.hasMove*//*) {//没有传感器或者传感器检测到了移动*/
                double dis_double = distance * 1000;
                String dis_str = dis_double + "";
                CommonData.dis_int = Integer.parseInt(dis_str.substring(0, dis_str.indexOf("."))) + 1;
                CommonData.walkDistance += CommonData.dis_int;
                //Toast.makeText(this, "当前巡护距离 " + CommonData.walkDistance + " 米，较上次 " + CommonData.dis_int + "米", Toast.LENGTH_SHORT).show();
                HhLog.e("当前巡护距离 " + CommonData.walkDistance + " 米，较上次 " + CommonData.dis_int + "米");
                //通知UI刷新巡护距离
                EventBus.getDefault().post(new WalkEvent());
                //Toast.makeText(this, distance+" in testInfo", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "changeUserPosition: " + distance + " in testInfo");
                /*} else {
                    CommonData.dis_int = 0;
                }*/
            } else {
                CommonData.dis_int = 0;
                Log.e(TAG, "changeUserPosition: " + distance + " out testInfo");
            }
            Log.e(TAG, "changeUserPosition: distance = " + distance);
            Log.e(TAG, "changeUserPosition: LatLng = " + CommonData.lng_old + "," + CommonData.lat_old + " | " + CommonData.lng + "," + CommonData.lat);

        }
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
            jsonObject.put("dayTotalDistance", CommonData.hasSign?CommonData.walkDistance:0);//总距离
            jsonObject.put("distance", CommonData.hasSign?CommonData.dis_int:0);//距离上一次距离

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HhLog.e("position params " + jsonObject.toString());
        HhHttp.postString()
                .url(URLConstant.POST_POSITION)
                .content(jsonObject.toString())
                .build()
                .execute(new LoggedInStringCallback(null, this) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("position " + response);
                        //TODO 判断移动距离有效后再替换旧坐标
                        if(disState || CommonData.lng_old==0){
                            CommonData.lng_old = CommonData.lng;
                            CommonData.lat_old = CommonData.lat;
                            disState = false;
                        }
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

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
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
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alive);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Notification notification;
        Intent intent_ = new Intent(this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("location", "location", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this, "location");
            builder.setContentIntent(PendingIntent.getActivity(this, 0, intent_, 0))
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_icon))
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_icon)
                    .setContentText("为您持续巡护中...")
                    .setWhen(System.currentTimeMillis());
            notification = builder.build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_icon)
                    .setContentText("为您持续巡护中...")
                    .setContentIntent(PendingIntent.getActivity(this, 0, intent_, 0))
                    .build();
        }
        startForeground(110, notification);

        return START_STICKY;
    }

    /*退出登录回调*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Ext ext) {
        mLocationClient.stop();
        myListener = null;
        stopSelf();
    }

    private void getBaiduLocation() {
        mLocationClient.start();
        HhLog.e("getBaiduLocation");
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
        mSensorManager.unregisterListener(this);
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
