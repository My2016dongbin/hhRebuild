package com.haohai.platform.fireforestplatform.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityLoginBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityMapSnapBinding;
import com.haohai.platform.fireforestplatform.ui.bean.SnapModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.LoginViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.MapSnapViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.haohai.platform.fireforestplatform.utils.StringData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapSnapActivity extends BaseLiveActivity<ActivityMapSnapBinding, MapSnapViewModel> {

    private DistrictSearch mDistrictSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String roomId = getIntent().getStringExtra("roomId");
        init_();
        bind_();
        obtainViewModel().postTaskRoom(roomId);
    }

    private void init_() {
        binding.topBar.title.setText("标绘详情");
        //添加定位图层
        binding.mapView.getMap().setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        binding.mapView.getMap().setMyLocationEnabled(true);
        binding.mapView.showZoomControls(false);

        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(CommonData.lat)
                .longitude(CommonData.lng).build();
        // 设置定位数据
        binding.mapView.getMap().setMyLocationData(locData);

        LatLng ll = new LatLng(CommonData.lat,
                CommonData.lng);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        binding.mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


        //绘制区域边界
        OnGetDistricSearchResultListener listener = new OnGetDistricSearchResultListener() {

            @Override
            public void onGetDistrictResult(DistrictResult districtResult) {
                districtResult.getCenterPt();//获取行政区中心坐标点
                districtResult.getCityName();//获取行政区域名称
                List<List<com.baidu.mapapi.model.LatLng>> polyLines = districtResult.getPolylines();//获取行政区域边界坐标点
                //边界就是坐标点的集合，在地图上画出来就是多边形图层。有的行政区可能有多个区域，所以会有多个点集合。
                if (polyLines == null) {
                    return;
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (List<com.baidu.mapapi.model.LatLng> polyline : polyLines) {
                    OverlayOptions ooPolyline11 = new PolylineOptions().width(10)
                            .points(polyline).dottedLine(false).color(Color.BLUE);
                    binding.mapView.getMap().addOverlay(ooPolyline11);
                    for (com.baidu.mapapi.model.LatLng latLng : polyline) {
                        builder.include(latLng);
                    }
                }
            }

        };

        mDistrictSearch = DistrictSearch.newInstance();
        mDistrictSearch.setOnDistrictSearchListener(listener);//设置回调监听
    }

    private void bind_() {

    }

    private void flyBaiduMapZoom(double lat, double lng, int zoom) {
        //飞到精确点上
        com.baidu.mapapi.model.LatLng ll = new com.baidu.mapapi.model.LatLng(
                lat, lng);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(zoom);
        binding.mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    @Override
    protected ActivityMapSnapBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_map_snap);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public MapSnapViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MapSnapViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        //数据更新
        obtainViewModel().snapModelList.observe(this, this::snapDataChanged);
    }

    private void snapDataChanged(List<SnapModel> snapModels) {
        binding.mapView.getMap().clear();

        //1.边界
        DistrictSearchOption districtSearchOption = new DistrictSearchOption();
        districtSearchOption.cityName("青岛");//检索城市名称
        districtSearchOption.districtName("城阳区");
        mDistrictSearch.searchDistrict(districtSearchOption);//请求行政区数据
        //2.标绘
        for (int i = 0; i < snapModels.size(); i++) {
            SnapModel snapModel = snapModels.get(i);
            JSONObject geometry = snapModel.getGeometry();
            SnapModel.Properties properties = snapModel.getProperties();
            try {
                String type = geometry.getString("type");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                if(Objects.equals(type, "LineString")){
                    List<LatLng> lineList = new ArrayList<>();
                    for (int m = 0; m < coordinates.length(); m++) {
                        JSONArray lt = (JSONArray) coordinates.get(m);
                        lineList.add(new LatLng(Double.parseDouble(lt.get(1).toString()),Double.parseDouble(lt.get(0).toString())));
                    }
                    drawLine(lineList,properties);
                }
                if(Objects.equals(type, "Point")){
                    LatLng point = new LatLng(Double.parseDouble(coordinates.get(1).toString()),Double.parseDouble(coordinates.get(0).toString()));
                    drawPoint(point,properties);
                }
                flyBaiduMapZoom(36.292742,120.372336,12);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void drawPoint(LatLng point, SnapModel.Properties properties) {
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.hulin_);
        if(Objects.equals(properties.getStyle().getLabel().getText(), "防火队伍")){ 
            btm = BitmapDescriptorFactory.fromResource(R.drawable.duiwu_);
        }
        if(Objects.equals(properties.getStyle().getLabel().getText(), "防火队员")){
            btm = BitmapDescriptorFactory.fromResource(R.drawable.duiyuan_);
        }
        if(Objects.equals(properties.getStyle().getLabel().getText(), "飞机")){
            btm = BitmapDescriptorFactory.fromResource(R.drawable.feiji_);
        }
        if(Objects.equals(properties.getStyle().getLabel().getText(), "护林员")){
            btm = BitmapDescriptorFactory.fromResource(R.drawable.hulin_);
        }
        if(Objects.equals(properties.getStyle().getLabel().getText(), "防火员")){
            btm = BitmapDescriptorFactory.fromResource(R.drawable.duiyuan_);
        }
        if(Objects.equals(properties.getStyle().getLabel().getText(), "救护车")){
            btm = BitmapDescriptorFactory.fromResource(R.drawable.jiuhu_);
        }
        if(Objects.equals(properties.getStyle().getLabel().getText(), "消防车")){
            btm = BitmapDescriptorFactory.fromResource(R.drawable.xiaofang_);
        }
        Bundle bundle = new Bundle();
        bundle.putString("id", properties.getId());
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .extraInfo(bundle)
                .icon(btm);
        binding.mapView.getMap().addOverlay(option);
    }

    private void drawLine(List<LatLng> lineList, SnapModel.Properties properties) {
        // 覆盖物参数配置
        Bundle bundle = new Bundle();
        bundle.putString("id",properties.getId());
        OverlayOptions ooGeoPolyline = new PolylineOptions()
                .extraInfo(bundle)
                .width(6)
                .color(Color.parseColor(properties.getStyle().getColor()))
                .points(lineList);
        // 添加覆盖物
        binding.mapView.getMap().addOverlay(ooGeoPolyline);
    }
}