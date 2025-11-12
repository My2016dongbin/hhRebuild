package com.haohai.platform.fireforestplatform.old;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;

public class FireMapActivity extends BaseActivity {
    private static final String TAG = FireMapActivity.class.getSimpleName();

    private com.amap.api.maps.MapView aMapView;
    private com.amap.api.maps.AMap aMap;

    /**
     * 当前地点击点
     */
    private LatLng currentPt;

    /**
     * 用于显示地图状态的面板
     */
    private TextView mStateBar;
    private TextView mStateBar2;
    private ImageView left_icon;
    private TextView left;

    //地理编码
    private GeocodeSearch geocoderSearch;
    private String cityAddress;
    private String latitude;
    private String longitude;
    private double longitude_double;
    private double latitude_double;
    private String countryName;
    private String province;
    private String city;
    private String district;
    private String town;
    private String street;
    private String adcode;

    public static final int MAP_REUEST_CODE = 2;
    public static final double LATITUDE_DEF = 0.00;//默认天安数码城: latitude: 36.32087806111286, longitude: 120.44349123197962
    public static final double LONGTITUDE_DEF = 0.00;//默认天安数码城: latitude: 36.32087806111286, longitude: 120.44349123197962

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏背景色（非沉浸式，不透明）
            getWindow().setStatusBarColor(Color.WHITE);

            // 设置状态栏文字图标为深色（黑色）
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
        setContentView(R.layout.activity_fire_map);
        //获取Intent传递的值
        Intent intent_g = getIntent();
        longitude_double = intent_g.getDoubleExtra("longitude_double", LONGTITUDE_DEF);
        latitude_double = intent_g.getDoubleExtra("latitude_double", LATITUDE_DEF);
        double[] doubles = LatLngChangeNew.calBD09toGCJ02(latitude_double, longitude_double);
        latitude_double = doubles[0];
        longitude_double = doubles[1];
        if (longitude_double == LONGTITUDE_DEF && latitude_double == LATITUDE_DEF) {
            Toast.makeText(FireMapActivity.this, "请检查授予定位权限并开启定位!", Toast.LENGTH_SHORT).show();
        }

        left_icon = (ImageView) findViewById(R.id.left_icon);
        left = (TextView) findViewById(R.id.left);
        left.setOnClickListener(v -> finish());
        left_icon.setOnClickListener(v -> finish());

        try {
            com.amap.api.services.core.ServiceSettings.updatePrivacyShow(this, true, true);
            com.amap.api.services.core.ServiceSettings.updatePrivacyAgree(this, true);
        } catch (Throwable ignore) {}
        aMapView = (com.amap.api.maps.MapView) findViewById(R.id.aMapView);
        aMapView.onCreate(savedInstanceState);
        aMap = aMapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.amap.api.maps.model.LatLng(latitude_double,longitude_double),16));
        mStateBar = (TextView) findViewById(R.id.state);
        mStateBar2 = (TextView) findViewById(R.id.state2);

        try {
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
                    if (rCode == 1000) {
                        if (regeocodeResult != null
                                && regeocodeResult.getRegeocodeAddress() != null) {
                            cityAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                            city = regeocodeResult.getRegeocodeAddress().getCity();
                            countryName = regeocodeResult.getRegeocodeAddress().getCountry();
                            province = regeocodeResult.getRegeocodeAddress().getProvince();
                            district = regeocodeResult.getRegeocodeAddress().getDistrict();
                            town = regeocodeResult.getRegeocodeAddress().getTownship();
                            street = regeocodeResult.getRegeocodeAddress().getStreetNumber().getStreet();
                            adcode = regeocodeResult.getRegeocodeAddress().getAdCode();
                            latitude = CommonUtil.parsePointSplit(String.valueOf(currentPt.latitude),6);
                            longitude = CommonUtil.parsePointSplit(String.valueOf(currentPt.longitude),6);

                            Log.e(TAG, "逆地理编码成功: " + cityAddress);
                            mStateBar.setText(cityAddress);
                            mStateBar2.setText("(" + longitude + " , " + latitude + ")");

                        } else {
                            Log.e(TAG, "逆地理编码结果为空");
                        }
                    } else {
                        Log.e(TAG, "逆地理编码失败，错误码: " + rCode);
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                    Log.e(TAG, "onGeocodeSearched" + geocodeResult);
                }
            });
        } catch (AMapException e) {
            e.printStackTrace();
        }
        initListener();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        aMapView.onSaveInstanceState(outState);
    }

    /**
     * 对地图事件的消息响应
     */
    private void initListener() {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(com.amap.api.maps.model.LatLng latLng) {
                currentPt = latLng;
                aMap.clear();
                com.amap.api.maps.model.BitmapDescriptor btm = com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_tomap);
                aMap.addMarker(new com.amap.api.maps.model.MarkerOptions().position(latLng)
                        .icon(btm)
                );

                //发起地理编码检索；
                try {
                    Log.e("发起地理编码检索","发起地理编码检索"+currentPt.latitude + " , " + currentPt.longitude);
                    RegeocodeQuery regeocodeQuery = new RegeocodeQuery(new LatLonPoint(currentPt.latitude, currentPt.longitude), 200, GeocodeSearch.AMAP);
                    geocoderSearch.getFromLocationAsyn(regeocodeQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        aMap.setOnPOIClickListener(new AMap.OnPOIClickListener() {
            @Override
            public void onPOIClick(Poi poi) {
                currentPt = poi.getCoordinate();
                aMap.clear();
                com.amap.api.maps.model.BitmapDescriptor btm = com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_tomap);
                aMap.addMarker(new com.amap.api.maps.model.MarkerOptions().position(currentPt)
                        .icon(btm)
                );

                //发起地理编码检索；
                try {
                    Log.e("发起地理编码检索","发起地理编码检索"+currentPt.latitude + " , " + currentPt.longitude);
                    RegeocodeQuery regeocodeQuery = new RegeocodeQuery(new LatLonPoint(currentPt.latitude, currentPt.longitude), 200, GeocodeSearch.AMAP);
                    geocoderSearch.getFromLocationAsyn(regeocodeQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void mapClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_map) {
            if (null == longitude || null == latitude) {
                Toast.makeText(FireMapActivity.this, "请在地图上选择火点位置", Toast.LENGTH_SHORT).show();
            } else {
                if (city == "" || city == null) {
                    Toast.makeText(this, "请重新选择", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("cityAddress", cityAddress);
                    intent.putExtra("countryName", countryName);
                    intent.putExtra("province", province);
                    intent.putExtra("city", city);
                    intent.putExtra("district", district);
                    intent.putExtra("town", town);
                    intent.putExtra("street", street);
                    intent.putExtra("adcode", adcode);
                    FireMapActivity.this.setResult(RESULT_OK, intent);
                    finish();
                }

            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        aMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        aMapView.onPause();
    }
}
