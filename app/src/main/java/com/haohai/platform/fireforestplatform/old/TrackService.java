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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrackService extends Service implements SensorEventListener {

    private static final String TAG = TrackService.class.getSimpleName();
    private static final double MAX_VALID_DISTANCE_KM = 0.2d;
    private static final double MIN_VALID_DISTANCE_KM = 0.002d;
    private static final float MAX_VALID_LOCATION_RADIUS_M = 50f;
    private static final float MOTION_ACCELERATION_THRESHOLD = 0.10f;
    private static final long RECENT_MOTION_WINDOW_MS = 15_000L;
    public static final String ACTION_RESTART_TRACK_SERVICE = "com.haohai.platform.fireforestplatform.action.RESTART_TRACK_SERVICE";
    private static final long TRACK_INTERVAL_MS = 10_000L;
    private static final long GEO_UPLOAD_INTERVAL_MS = 120_000L;
    private static final int GEO_UPLOAD_BATCH_SIZE = 20;
    private static final int GEO_RETRY_TIMES = 60;
    private static final int TRACK_NOTIFICATION_ID = 110;

    public AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption;
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
    private float lastAccelerationMagnitude = -1f;
    private long lastMotionTime = 0L;
    private MediaPlayer mediaPlayer;
    private boolean geoParamsGetting = false;
    private boolean geoLocationUploading = false;
    private long lastGeoUploadTime = 0L;
    private Runnable geoRetryRunnable;
    private final OkHttpClient geoHttpClient = new OkHttpClient();
    private final AMapLocationListener amapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            handleAmapLocation(aMapLocation);
        }
    };
    private final Runnable trackRunnable = new Runnable() {
        @Override
        public void run() {
            requestTrackLocation();
            parseDistance();
            uploadLocation();
            handleGeoTrack();
            trackHandler.postDelayed(this, TRACK_INTERVAL_MS);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        initWakeLock();
        //addVirtualLine();

        initAmapLoc();
        uploadQueue();

        //检测传感器
        boolean a = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        if (a) {
            CommonData.hasSensor = true;
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometer != null) {
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        lastGeoUploadTime = System.currentTimeMillis();

    }

    public void search(){
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
        if (isPoorLocation()) {
            CommonData.dis_int = 0;
            HhLog.e("距离过滤 定位精度差 radius: " + CommonData.locationRadius + " type: " + CommonData.locationType);
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
        if (distance < MIN_VALID_DISTANCE_KM) {
            CommonData.dis_int = 0;
            HhLog.e("距离过滤 静止漂移 " + distance + "千米，new: " + CommonData.lng + "，" + CommonData.lat + " old: " + CommonData.lng_old + "," + CommonData.lat_old);
            return;
        }
        if (distance > MAX_VALID_DISTANCE_KM) {
            CommonData.dis_int = 0;
            Log.e(TAG, "changeUserPosition: " + distance + " out testInfo");
            updateLastLocation();
            return;
        }
        if (!hasRecentMotion()) {
            CommonData.dis_int = 0;
            HhLog.e("距离过滤 传感器判断静止 " + distance + "千米，new: " + CommonData.lng + "，" + CommonData.lat + " old: " + CommonData.lng_old + "," + CommonData.lat_old);
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
        boolean upload = (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.upload, true);
        if(!upload){
            return;
        }
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

    private void handleGeoTrack() {
        if (!CommonData.hasSign || !isLocationUploadOpen()) {
            return;
        }
        saveGeoTrackPoint();
        long now = System.currentTimeMillis();
        if (now - lastGeoUploadTime >= GEO_UPLOAD_INTERVAL_MS) {
            sendGeoLocation();
        }
    }

    private boolean isLocationUploadOpen() {
        return (boolean) SPUtils.get(HhApplication.getInstance(), SPValue.upload, true);
    }

    private void getGeoParams(final Runnable successRunnable) {
        if (geoParamsGetting) {
            return;
        }
        geoParamsGetting = true;
        HhHttp.get()
                .url(URLConstant.GET_AMAP_TRACK_PARAMS)
                .build()
                .execute(new LoggedInStringCallback(null, this) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("getGeoParams " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (data == null) {
                                data = jsonObject;
                            }
                            CommonData.geoServiceId = data.optString("serviceId", "");
                            CommonData.geoTerminalId = data.optString("terminalId", "");
                            CommonData.geoTraceId = data.optString("traceId", "");
                            if (successRunnable != null && hasGeoParams()) {
                                successRunnable.run();
                            }
                        } catch (JSONException e) {
                            HhLog.e("getGeoParams error " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("getGeoParams onFailure: " + e.toString());
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        geoParamsGetting = false;
                    }
                });
    }

    private boolean hasGeoParams() {
        return CommonData.geoServiceId != null && CommonData.geoServiceId.length() > 0
                && CommonData.geoTerminalId != null && CommonData.geoTerminalId.length() > 0
                && CommonData.geoTraceId != null && CommonData.geoTraceId.length() > 0;
    }

    private void saveGeoTrackPoint() {
        if (CommonData.lng == 0 || CommonData.lat == 0 || String.valueOf(CommonData.lng).contains("E")) {
            return;
        }
        try {
            double[] doubles = LatLngChangeNew.calBD09toGCJ02(CommonData.lat, CommonData.lng);
            JSONArray points = readGeoTrackPoints();
            JSONObject point = new JSONObject();
            point.put("location", doubles[1] + "," + doubles[0]);
            point.put("locatetime", System.currentTimeMillis());
            points.put(point);
            saveGeoTrackPoints(points);
        } catch (Exception e) {
            HhLog.e("saveGeoTrackPoint error " + e.getMessage());
        }
    }

    private void sendGeoLocation() {
        if (!CommonData.hasSign || !isLocationUploadOpen() || geoLocationUploading || geoRetryRunnable != null) {
            return;
        }
        JSONArray allPoints = readGeoTrackPoints();
        if (allPoints.length() == 0) {
            lastGeoUploadTime = System.currentTimeMillis();
            return;
        }
        if (!hasGeoParams()) {
            getGeoParams(new Runnable() {
                @Override
                public void run() {
                    sendGeoLocation();
                }
            });
            return;
        }
        JSONArray uploadPoints = takeGeoTrackPoints(allPoints);
        uploadGeoTrackPoints(uploadPoints);
    }

    private JSONArray takeGeoTrackPoints(JSONArray points) {
        JSONArray uploadPoints = new JSONArray();
        int uploadSize = Math.min(points.length(), GEO_UPLOAD_BATCH_SIZE);
        for (int i = 0; i < uploadSize; i++) {
            try {
                uploadPoints.put(points.get(i));
            } catch (JSONException e) {
                HhLog.e("takeGeoTrackPoints error " + e.getMessage());
            }
        }
        return uploadPoints;
    }

    private void uploadGeoTrackPoints(final JSONArray points) {
        if (points.length() == 0) {
            return;
        }
        geoLocationUploading = true;
        HhLog.e("sendGeoLocation " + URLConstant.POST_AMAP_TRACK);
        HhLog.e("sendGeoLocation points length " +
                " key , " + CommonData.geoWebKey +
                " sid , " + CommonData.geoServiceId +
                " tid , " + CommonData.geoTerminalId +
                " trid , " + CommonData.geoTraceId
                );
        HhLog.e("sendGeoLocation points length " + points.length());
        HhLog.e("sendGeoLocation points " + points.toString());
        RequestBody requestBody = new FormBody.Builder()
                .add("key", CommonData.geoWebKey)
                .add("sid", CommonData.geoServiceId)
                .add("tid", CommonData.geoTerminalId)
                .add("trid", CommonData.geoTraceId)
                .add("points", points.toString())
                .build();
        Request request = new Request.Builder()
                .url(URLConstant.POST_AMAP_TRACK)
                .post(requestBody)
                .build();
        geoHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                trackHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        HhLog.e("sendGeoLocation onFailure: " + e.toString());
                        geoLocationUploading = false;
                        retrySendGeoLocation();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body() == null ? "" : response.body().string();
                trackHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleGeoUploadResult(responseText, points);
                    }
                });
            }
        });
    }

    private void handleGeoUploadResult(String response, JSONArray points) {
        HhLog.e("sendGeoLocation " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (Objects.equals(jsonObject.optString("errcode"), "10000")) {
                clearUploadedGeoTrackPoints(points);
                lastGeoUploadTime = System.currentTimeMillis();
                geoLocationUploading = false;
                if (readGeoTrackPoints().length() > 0) {
                    sendGeoLocation();
                }
            } else {
                geoLocationUploading = false;
                retrySendGeoLocation();
            }
        } catch (JSONException e) {
            HhLog.e("sendGeoLocation error " + e.getMessage());
            geoLocationUploading = false;
            retrySendGeoLocation();
        }
    }

    private JSONArray readGeoTrackPoints() {
        String pointsText = (String) SPUtils.get(HhApplication.getInstance(), SPValue.geoTrackPoints, "[]");
        try {
            return new JSONArray(pointsText);
        } catch (JSONException e) {
            HhLog.e("readGeoTrackPoints error " + e.getMessage());
        }
        return new JSONArray();
    }

    private void saveGeoTrackPoints(JSONArray points) {
        SPUtils.put(HhApplication.getInstance(), SPValue.geoTrackPoints, points.toString());
    }

    private void clearUploadedGeoTrackPoints(JSONArray uploadPoints) {
        long maxLocateTime = 0L;
        for (int i = 0; i < uploadPoints.length(); i++) {
            try {
                long locateTime = uploadPoints.getJSONObject(i).optLong("locatetime", 0L);
                if (locateTime > maxLocateTime) {
                    maxLocateTime = locateTime;
                }
            } catch (JSONException e) {
                HhLog.e("clearUploadedGeoTrackPoints error " + e.getMessage());
            }
        }
        JSONArray points = readGeoTrackPoints();
        JSONArray savePoints = new JSONArray();
        for (int i = 0; i < points.length(); i++) {
            try {
                JSONObject point = points.getJSONObject(i);
                if (point.optLong("locatetime", 0L) > maxLocateTime) {
                    savePoints.put(point);
                }
            } catch (JSONException e) {
                HhLog.e("clearUploadedGeoTrackPoints save error " + e.getMessage());
            }
        }
        saveGeoTrackPoints(savePoints);
    }

    private void retrySendGeoLocation() {
        if (!CommonData.hasSign || !isLocationUploadOpen() || geoRetryRunnable != null) {
            return;
        }
        int seconds = new Random().nextInt(GEO_RETRY_TIMES) + 1;
        geoRetryRunnable = new Runnable() {
            @Override
            public void run() {
                geoRetryRunnable = null;
                sendGeoLocation();
            }
        };
        trackHandler.postDelayed(geoRetryRunnable, seconds * 1000L);
    }

    void initAmapLoc() {
        try {
            AMapLocationClient.updatePrivacyShow(getApplicationContext(), true, true);
            AMapLocationClient.updatePrivacyAgree(getApplicationContext(), true);
            mLocationClient = new AMapLocationClient(getApplicationContext());
            mLocationClient.setLocationListener(amapLocationListener);

            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(false);
            mLocationOption.setOnceLocationLatest(false);
            mLocationOption.setNeedAddress(true);
            mLocationOption.setInterval(TRACK_INTERVAL_MS);
            mLocationOption.setHttpTimeOut(20000);
            mLocationOption.setMockEnable(false);
            mLocationOption.setLocationCacheEnable(false);
            mLocationClient.setLocationOption(mLocationOption);
        } catch (Exception e) {
            HhLog.e("initAmapLoc " + e.getMessage());
        }
    }

    private void handleAmapLocation(AMapLocation location) {
        if (location == null) {
            HhLog.e("handleAmapLocation location is null");
            return;
        }
        int errorCode = location.getErrorCode();
        if (errorCode != 0) {
            HhLog.e("handleAmapLocation error " + errorCode + " " + location.getErrorInfo());
            return;
        }
        double amapLatitude = location.getLatitude();
        double amapLongitude = location.getLongitude();
        if (!isValidLocation(amapLatitude, amapLongitude)) {
            HhLog.e("handleAmapLocation invalid " + amapLatitude + "," + amapLongitude + "," + location.getAccuracy());
            return;
        }
        double[] bdLocation = LatLngChangeNew.calGCJ02toBD09(amapLatitude, amapLongitude);
        CommonData.dis_int = 0;
        CommonData.lat = bdLocation[0];
        CommonData.lng = bdLocation[1];
        CommonData.locationRadius = location.getAccuracy();
        CommonData.locationType = location.getLocationType();
        CommonData.locationTime = System.currentTimeMillis();
        if (location.getAddress() != null && location.getAddress().length() > 0) {
            address = location.getAddress();
        }
        HhLog.e("handleAmapLocation amap " + amapLatitude + "," + amapLongitude + " bd " + CommonData.lat + "," + CommonData.lng + "," + location.getAccuracy() + " type=" + location.getLocationType());
        SPUtils.put(HhApplication.getInstance(), SPValue.latitude,CommonData.lat);
        SPUtils.put(HhApplication.getInstance(), SPValue.longitude,CommonData.lng);
    }

    private boolean isValidLocation(double latitude, double longitude) {
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            return false;
        }
        if (Double.isInfinite(latitude) || Double.isInfinite(longitude)) {
            return false;
        }
        if (latitude == 0 || longitude == 0) {
            return false;
        }
        if (String.valueOf(latitude).contains("E") || String.valueOf(longitude).contains("E")) {
            return false;
        }
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
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
        stopAmapLocation();
        stopSelf();
    }

    private void getAmapLocation() {
        if (mLocationClient == null) {
            initAmapLoc();
        }
        if (mLocationClient == null) {
            return;
        }
        mLocationClient.startLocation();
        hasLocationStarted = true;
        HhLog.e("getAmapLocation");
    }

    private void requestTrackLocation() {
        if (!hasLocationPermission()) {
            hasLocationStarted = false;
            HhLog.e("requestTrackLocation no location permission");
            return;
        }
        try {
            if (!hasLocationStarted) {
                getAmapLocation();
            } else {
                requestLocation();
            }
        } catch (Exception e) {
            HhLog.e("requestTrackLocation " + e.getMessage());
            try {
                reLocation();
                hasLocationStarted = true;
            } catch (Exception ex) {
                hasLocationStarted = false;
                HhLog.e("reLocation " + ex.getMessage());
            }
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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
        return (int) Math.round(distanceKm * 1000d);
    }

    private boolean isPoorLocation() {
//        return CommonData.locationRadius > MAX_VALID_LOCATION_RADIUS_M;
        return false;
    }

    private boolean hasRecentMotion() {
        if (mAccelerometer == null || lastAccelerationMagnitude < 0) {
            return true;
        }
        return System.currentTimeMillis() - lastMotionTime <= RECENT_MOTION_WINDOW_MS;
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
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.startLocation();
        }
        HhLog.e("requestLocation");
    }

    private void reLocation() {
        if (mLocationClient == null) {
            initAmapLoc();
        }
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        HhLog.e("reLocation");
    }

    private void stopAmapLocation() {
        try {
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
                mLocationClient = null;
            }
        } catch (Exception e) {
            HhLog.e("stopAmapLocation " + e.getMessage());
        }
        hasLocationStarted = false;
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
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        stopAmapLocation();
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
        if (event == null || event.sensor == null || event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float accelerationMagnitude = (float) Math.sqrt(x * x + y * y + z * z);
        if (lastAccelerationMagnitude >= 0
                && Math.abs(accelerationMagnitude - lastAccelerationMagnitude) > MOTION_ACCELERATION_THRESHOLD) {
            hasNotice = true;
            lastMotionTime = System.currentTimeMillis();
        }
        lastAccelerationMagnitude = accelerationMagnitude;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
