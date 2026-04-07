package com.haohai.platform.fireforestplatform.ui.fragment;

import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.navi.NaviSetting;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseFragment;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.FgMap;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.old.ResourceAddActivity;
import com.haohai.platform.fireforestplatform.old.TrackService;
import com.haohai.platform.fireforestplatform.old.bean.MapDialogDismiss;
import com.haohai.platform.fireforestplatform.old.linyi.AroundActivity;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.SatelliteSettingActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.bean.Resource;
import com.haohai.platform.fireforestplatform.ui.cell.SheQuListDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.SheQu;
import com.haohai.platform.fireforestplatform.ui.cell.SatelliteSearchAdvancedDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.ui.cell.OneBodyDetailDialog;
import com.haohai.platform.fireforestplatform.ui.cell.OneBodyListDialog;
import com.haohai.platform.fireforestplatform.ui.cell.ResourceDetailDialog;
import com.haohai.platform.fireforestplatform.ui.cell.ResourceListDialog;
import com.haohai.platform.fireforestplatform.ui.cell.SatelliteDetailDialog;
import com.haohai.platform.fireforestplatform.ui.cell.SatelliteListDialog;
import com.haohai.platform.fireforestplatform.ui.cell.SatelliteSearchDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.ResourceType;
import com.haohai.platform.fireforestplatform.ui.multitype.SatelliteFire;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FgMapViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.GetJsonDataUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MapFragment extends BaseFragment<FgMap, FgMapViewModel> implements OneBodyListDialog.OneBodyDialogListener, SatelliteListDialog.SatelliteDialogListener, OneBodyDetailDialog.OneBodyDetailDialogListener, SatelliteDetailDialog.SatelliteDetailDialogListener, ResourceDetailDialog.ResourceDetailDialogListener, ResourceListDialog.ResourceDialogListener, SatelliteSearchDialog.SatelliteSearchDialogListener, SatelliteSearchAdvancedDialog.SatelliteSearchAdvancedDialogListener, SheQuListDialog.SheQuDialogListener {

    private final String TAG = MapFragment.class.getSimpleName();
    private OneBodyListDialog oneBodyListDialog;
    private SatelliteListDialog satelliteListDialog;
    private OneBodyDetailDialog oneBodyDetailDialog;
    private SatelliteDetailDialog satelliteDetailDialog;
    private SatelliteSearchAdvancedDialog satelliteSearchAdvancedDialog;
    private SatelliteSearchDialog satelliteSearchDialog;
    private ResourceListDialog resourceListDialog;
    private SheQuListDialog sheQuListDialog;
    private ResourceDetailDialog resourceDetailDialog;
    private AMap aMap;
    private CameraPosition lastResourceClusterCameraPosition;
    private final int resourceClusterGridSizeDp = 60;

    public static MapFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);

        NaviSetting.updatePrivacyShow(getActivity(), true, true);
        NaviSetting.updatePrivacyAgree(getActivity(), true);
        binding.aMapView.onCreate(savedInstanceState);
        aMap = binding.aMapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);

        init_();
        bind_();
        obtainViewModel().getData();
        //开启轨迹服务
        requireActivity().startService(new Intent(requireActivity(), TrackService.class));

        return binding.getRoot();
    }

    private void bind_() {
        binding.viewResourceAround.setOnClickListener(v -> {
            getActivity().startActivity(new Intent(getActivity(), AroundActivity.class));
        });
        binding.searchMenu.setOnClickListener(v -> {
            delayDialog(resourceListDialog);
        });
        binding.viewResourceList.setOnClickListener(v -> {
            resourceListDialog.show();
        });
        binding.viewResourceAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResourceAddActivity.class);
            startActivity(intent);
        });
        binding.editFind.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                obtainViewModel().search();
                return true;
            }
        });
        binding.editFind.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                obtainViewModel().search = s.toString();
            }
        });
        binding.viewStarSetting.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(requireActivity(), SatelliteSettingActivity.class));
        });
        binding.viewStarFire.setOnClickListener(v -> {
            delayDialog(satelliteListDialog);
        });
        binding.viewStarFind.setOnClickListener(v -> {
            delayDialog(satelliteSearchDialog);
        });
        binding.viewWarnList.setOnClickListener(v -> {
            delayDialog(oneBodyListDialog);
        });
        binding.viewTask.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), TaskActivity.class));
        });
        binding.viewLocation.setOnClickListener(v -> {
            flyBaiduMapZoom(CommonData.lat, CommonData.lng, 14);
            userLocationMarker();
        });
        binding.viewGridShequ.setOnClickListener(v -> {
            sheQuListDialog.show();
        });
    }

    private void delayDialog(Dialog dialog) {
        closeInput(binding.editFind);
        new Handler().postDelayed(dialog::show,200);
    }

    ///Tab切换
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MainTabChange event) {
        int index = event.getIndex();
        String type = event.getType();
        if(index == 3 && Objects.equals(type, "oneBody")){
            obtainViewModel().getOneBodyData();
            oneBodyListDialog.show();
        }else if(index == 3 && Objects.equals(type,"satellite")){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            obtainViewModel().endTime = format.format(c.getTime()).replace(" ", "T");
            c.add(Calendar.HOUR, -obtainViewModel().defaultFindTime);//获取默认小时之前的时间
            obtainViewModel().startTime = format.format(c.getTime()).replace(" ", "T");
            obtainViewModel().getSatelliteData(obtainViewModel().startTime,obtainViewModel().endTime);
            satelliteListDialog.show();
        }
    }

    ///资源删除-地图资源刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MapDialogDismiss event) {
        Log.e(TAG, "onGetMessage: type" + event.getType() );
        ResourceType resourceType = new ResourceType();
        resourceType.setApiUrl("/api/"+event.getType());
        resourceType.setCode(event.getType());
        obtainViewModel().updateResEvent(resourceType);
        Log.e(TAG, "onGetMessage: apiurl" + resourceType.getApiUrl() );
        resourceDetailDialog.hide();
    }


    @Override
    protected void setupViewModel() {
        binding.setLifecycleOwner(this);
        binding.setFragmentModel(obtainViewModel());
        obtainViewModel().start(getContext());
    }

    @Override
    public int bindLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    public FgMapViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(FgMapViewModel.class);
    }


    private void init_() {
        //权限
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_RESOURCE_SEARCH)){
            binding.topSearch.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_RESOURCE_LIST)){
            binding.viewResourceList.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_RESOURCE_ADD)){
            binding.viewResourceAdd.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_RESOURCE_LIST) && !CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_RESOURCE_ADD)){
            binding.viewResource.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_ALARM_LIST)){
            binding.viewWarnList.setVisibility(View.GONE);
            binding.viewAlarm.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_SATELLITE_SETTING)){
            binding.viewStarSetting.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_SATELLITE_LIST)){
            binding.viewStarFire.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_SATELLITE_SEARCH)){
            binding.viewStarFind.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_SATELLITE_SETTING) && !CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_SATELLITE_LIST) && !CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_SATELLITE_SEARCH)){
            binding.viewSatellite.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_Grid_COMMUNITY)){
            binding.viewGridShequ.setVisibility(View.GONE);
            binding.viewGridAll.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_TASK)){
            binding.viewTask.setVisibility(View.GONE);
        }


        flyBaiduMapZoom(CommonData.lat, CommonData.lng, 14);
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                closeInput(binding.editFind);
            }
        });
        aMap.setOnMarkerClickListener(marker -> {
            Bundle extraInfo = (Bundle) marker.getObject();
            String markerId = extraInfo.getString("id");
            int markerType = extraInfo.getInt("type");
            OneBodyFire oneBodyFire = new OneBodyFire();
            if(markerType == obtainViewModel().ONE_BODY){
                List<OneBodyFire> oneBodyListValue = obtainViewModel().oneBodyList.getValue();
                assert oneBodyListValue != null;
                for (OneBodyFire values:oneBodyListValue) {
                    if(Objects.equals(values.getId(), markerId)){
                        oneBodyFire = values;
                    }
                }
                oneBodyDetailDialog.setOneBodyFire(oneBodyFire);
                delayDialog(oneBodyDetailDialog);
            }else if(markerType == obtainViewModel().SATELLITE){
                SatelliteFire satellite = new SatelliteFire();
                List<SatelliteFire> satelliteFires = obtainViewModel().satelliteList.getValue();
                assert satelliteFires != null;
                for (SatelliteFire values:satelliteFires) {
                    if(Objects.equals(values.getId(), markerId)){
                        satellite = values;
                    }
                }
                satelliteDetailDialog.setSatelliteFire(satellite);
                delayDialog(satelliteDetailDialog);
            }else if(markerType == obtainViewModel().RESOURCE){
                for (Resource res: Objects.requireNonNull(obtainViewModel().resourceList.getValue())) {
                    if(Objects.equals(res.getId(), markerId)){
                        resourceDetailDialog.setResource(res);
                        delayDialog(resourceDetailDialog);
                        return false;
                    }
                }
            }else if(markerType == obtainViewModel().RESOURCE_CLUSTER){
                LatLng target = marker.getPosition();
                float currentZoom = aMap.getCameraPosition().zoom;
                if (currentZoom >= aMap.getMaxZoomLevel() - 1F) {
                    showClusterResourceDialog(marker);
                    return true;
                }
                float nextZoom = Math.min(currentZoom + 2F, aMap.getMaxZoomLevel());
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, nextZoom));
                return true;
            }

            return false;
        });
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                refreshResourceClustersIfNeeded(cameraPosition);
            }
        });

        //一体机报警列表Dialog
        initOneBodyListDialog();
        //卫星报警列表Dialog
        initSatelliteListDialog();
        //卫星报警查询Dialog
        initSatelliteSearchDialog();
        //卫星报警查询高级Dialog
        initSatelliteSearchAdvancedDialog();
        //一体机报警Marker详情Dialog
        initOneBodyDetailDialog();
        //卫星报警Marker详情Dialog
        initSatelliteDetailDialog();
        //资源点列表Dialog
        initResourceListDialog();
        //社区网格图层列表Dialog
        initSheQuListDialog();
        //资源点Marker详情Dialog
        initResourceDetailDialog();

        //跳转当前位置
        new Handler().postDelayed(() -> {
            if(CommonData.lat!=0 && (obtainViewModel().oneBodyList.getValue()==null||obtainViewModel().oneBodyList.getValue().isEmpty())) {
                flyBaiduMapZoom(CommonData.lat, CommonData.lng, 14);
            }
        }, 3000);
    }

    private void flyBaiduMapZoom(double lat, double lng, int zoom) {
        //飞到精确点上
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.amap.api.maps.model.LatLng(lat, lng),zoom));
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时必须调用mMapView. onResume ()
        binding.aMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.aMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.aMapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.aMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        //一体机火警数据
        obtainViewModel().oneBodyList.observe(requireActivity(), this::oneBodyFireChanged);
        //卫星火警数据
        obtainViewModel().satelliteList.observe(requireActivity(), this::satelliteFireChanged);
        //社区网格图层数据
        obtainViewModel().sheQuGridList.observe(requireActivity(), this::sheQuChanged);
        //社区当前图层数据
        obtainViewModel().sheQuCurrentList.observe(requireActivity(), this::sheQuCurrentChanged);
        //资源类型数据
        obtainViewModel().resourceTypeList.observe(requireActivity(), this::resourceTypeChanged);
        //资源数据
        obtainViewModel().resourceList.observe(requireActivity(), this::resourceChanged);
    }

    private void oneBodyFireChanged(List<OneBodyFire> oneBodyFires) {
        aMap.clear();
        //更新Dialog列表数据
        oneBodyListDialog.setOneBodyFireList(oneBodyFires,obtainViewModel().currentPage);
        //更新所有Marker
        updateMarkers();
        //跳转第一火点
        try{
            OneBodyFire oneBody = oneBodyFires.get(0);
            double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(oneBody.getAlarmLatitude()), Double.parseDouble(oneBody.getAlarmLongitude()));
            flyBaiduMapZoom(doubles[0],doubles[1], 14);
        }catch (Exception e){
            Log.e(TAG, "oneBodyFireChanged: " + e.getMessage() );
        }
    }

    private void updateMarkers() {
        //绘制一体机火点Marker
        if(obtainViewModel().oneBodyList.getValue()!=null){
            List<OneBodyFire> value = obtainViewModel().oneBodyList.getValue();
            List<OneBodyFire> list = new ArrayList<>();
            if(value!=null && value.size()>0){
                //0全部  1未处理  2真实火点  3疑似火点
                if(obtainViewModel().oneBodyFilterState == 0){
                    /*for (int i = 0; i < value.size(); i++) {
                        OneBodyFire fire = value.get(i);
                        if(!Objects.equals(fire.getIsReal(), "0")){
                            list.add(fire);
                        }
                    }*/
                    list.addAll(value);
                }
                if(obtainViewModel().oneBodyFilterState == 1){
                    for (int i = 0; i < value.size(); i++) {
                        OneBodyFire fire = value.get(i);
                        if(fire.getIsReal() == null){
                            list.add(fire);
                        }
                    }
                }
                if(obtainViewModel().oneBodyFilterState == 2){
                    for (int i = 0; i < value.size(); i++) {
                        OneBodyFire fire = value.get(i);
                        if(Objects.equals(fire.getIsReal(), "1")){
                            list.add(fire);
                        }
                    }
                }
                if(obtainViewModel().oneBodyFilterState == 3){
                    for (int i = 0; i < value.size(); i++) {
                        OneBodyFire fire = value.get(i);
                        if(Objects.equals(fire.getIsReal(), "0")){
                            list.add(fire);
                        }
                    }
                }
            }
            oneBodyMarker(list);
        }
        //绘制卫星火点Marker
        if(obtainViewModel().satelliteList.getValue()!=null){
            satelliteMarker(Objects.requireNonNull(obtainViewModel().satelliteList.getValue()));
        }
        //绘制资源点Marker
        if(obtainViewModel().resourceList.getValue()!=null){
            resourceMarker(obtainViewModel().getValidResourceList());
        }
        //绘制当前社区图层数据
        if(obtainViewModel().sheQuCurrentList.getValue()!=null && !obtainViewModel().sheQuCurrentList.getValue().isEmpty()){
            sheQuMarker(Objects.requireNonNull(obtainViewModel().sheQuCurrentList.getValue()));
        }
    }

    private void satelliteFireChanged(List<SatelliteFire> satelliteFires) {
        aMap.clear();
        //更新Dialog列表数据
        satelliteListDialog.setSatelliteFireList(satelliteFires);
        //更新所有Marker
        updateMarkers();
        //跳转第一火点
        try{
            SatelliteFire satellite = satelliteFires.get(0);
            double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(satellite.getLatitude()), Double.parseDouble(satellite.getLongitude()));
            flyBaiduMapZoom(doubles[0],doubles[1], 14);
        }catch (Exception e){
            Log.e(TAG, "oneBodyFireChanged: " + e.getMessage() );
        }
    }
    private void resourceChanged(List<Resource> resources) {
        aMap.clear();
        lastResourceClusterCameraPosition = null;
        //更新所有Marker
        updateMarkers();
        //跳转第一火点
        try{
            Resource resource = resources.get(0);
            double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(resource.getPosition().getLat()), Double.parseDouble(resource.getPosition().getLng()));
            flyBaiduMapZoom(doubles[0],doubles[1], 14);
        }catch (Exception e){
            Log.e(TAG, "resourceChanged: " + e.getMessage() );
        }
    }
    private void sheQuChanged(List<SheQu> sheQus) {
        //更新Dialog列表数据
        sheQuListDialog.setSheQuList(sheQus);
    }
    private void sheQuCurrentChanged(List<ArrayList<Double>> points) {
        //更新地图图层数据
        aMap.clear();
        lastResourceClusterCameraPosition = null;
        updateMarkers();
    }
    private void resourceTypeChanged(List<ResourceType> resourceTypes) {
        //更新Dialog列表数据
        resourceListDialog.setResourceTypeList(resourceTypes);
    }

    private void oneBodyMarker(List<OneBodyFire> oneBodyFires) {
        ArrayList<MarkerOptions> options = new ArrayList<>();
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_red_fire);//默认森林防火
        for (int i = 0; i < oneBodyFires.size(); i++) {
            if(oneBodyFires.get(i).getType() == null){
                continue;
            }
            switch (oneBodyFires.get(i).getType()){
                case "2":
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_red_fire);//森林防火
                    break;
                case "4":
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_blue_fire);//海域监控
                    break;
                case "5":
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_yellow_fire);//国土报警
                    break;
            }
            try {
                double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(oneBodyFires.get(i).getAlarmLatitude()), Double.parseDouble(oneBodyFires.get(i).getAlarmLongitude()));
                LatLng point = new LatLng(doubles[0], doubles[1]);
                MarkerOptions option = new MarkerOptions()
                        .position(point)
                        .icon(btm);
                options.add(i, option);
            }catch (Exception e){
                Log.e(TAG, "oneBodyMarker: " + e.getMessage() );
                continue;
            }
        }
        List<Marker> markers = aMap.addMarkers(options, false);

        try{
            for (int i = 0; i < markers.size(); i++) {
                Bundle bundle = new Bundle();
                bundle.putString("id", oneBodyFires.get(i).getId());
                bundle.putInt("type", obtainViewModel().ONE_BODY);
                markers.get(i).setObject(bundle);
            }
        }catch (Exception e){
            //
        }
    }
    private void sheQuMarker(List<ArrayList<Double>> lines) {
        //多边形顶点位置
        List<LatLng> points = new ArrayList<>();
        for (ArrayList<Double> m:lines) {
            points.add(new LatLng(m.get(1), m.get(0)));
        }
        Log.e(TAG, "updateMarkers: enter -- " + lines );

        // 声明 多边形参数对象
        PolygonOptions polygonOptions = new PolygonOptions();
        // 添加 多边形的每个顶点（顺序添加）
        polygonOptions.addAll(points);
        polygonOptions.strokeWidth(15) // 多边形的边框
                .strokeColor(Color.parseColor("#AAf28f25"))// 边框颜色
                .fillColor(Color.parseColor("#AAf28f25"));   // 多边形的填充色
        aMap.addPolygon(polygonOptions);

        //跳转到第一个点
        LatLng latLng = points.get(0);
        flyBaiduMapZoom(latLng.latitude,latLng.longitude,16);
        //隐藏Dialog
        sheQuListDialog.hide();
    }
    private void satelliteMarker(List<SatelliteFire> satelliteFires) {
        ArrayList<MarkerOptions> options = new ArrayList<>();
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_fire);
        for (int i = 0; i < satelliteFires.size(); i++) {
            try {
                double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(satelliteFires.get(i).getLatitude()), Double.parseDouble(satelliteFires.get(i).getLongitude()));
                LatLng point = new LatLng(doubles[0], doubles[1]);
                MarkerOptions option = new MarkerOptions()
                        .position(point)
                        .icon(btm);
                options.add(i, option);
            }catch (Exception e){
                Log.e(TAG, "satelliteMarker: " + e.getMessage() );
                continue;
            }
        }
        List<Marker> markers = aMap.addMarkers(options, false);

        try{
            for (int i = 0; i < markers.size(); i++) {
                Bundle bundle = new Bundle();
                bundle.putString("id", satelliteFires.get(i).getId());
                bundle.putInt("type", obtainViewModel().SATELLITE);
                markers.get(i).setObject(bundle);
            }
        }catch (Exception e){
            //
        }
    }
    private void resourceMarker(List<Resource> resources) {
        if (resources == null || resources.isEmpty()) {
            return;
        }
        Map<String, ResourceCluster> clusterMap = new HashMap<>();
        int clusterSizePx = dp2px(resourceClusterGridSizeDp);
        for (Resource resource : resources) {
            HhLog.e("resource apiUrl " + resource.getApiUrl());
            //TODO 需后端配置后自动获取类型图标
            try {
                double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(resource.getPosition().getLat()), Double.parseDouble(resource.getPosition().getLng()));
                LatLng point = new LatLng(doubles[0], doubles[1]);
                Point screenPoint = aMap.getProjection().toScreenLocation(point);
                String key = (screenPoint.x / clusterSizePx) + "_" + (screenPoint.y / clusterSizePx);
                ResourceCluster cluster = clusterMap.get(key);
                if (cluster == null) {
                    cluster = new ResourceCluster();
                    cluster.latLng = point;
                    clusterMap.put(key, cluster);
                }
                cluster.resources.add(resource);
                cluster.latSum += point.latitude;
                cluster.lngSum += point.longitude;
                cluster.latLng = new LatLng(cluster.latSum / cluster.resources.size(), cluster.lngSum / cluster.resources.size());
            }catch (Exception e){
                Log.e(TAG, "resourceMarker: " + e.getMessage() );
            }
        }
        ArrayList<MarkerOptions> options = new ArrayList<>();
        ArrayList<Bundle> markerBundles = new ArrayList<>();
        for (ResourceCluster cluster : clusterMap.values()) {
            if (cluster.resources.size() == 1) {
                Bundle bundle = new Bundle();
                Resource resource = cluster.resources.get(0);
                bundle.putString("id", resource.getId());
                bundle.putInt("type", obtainViewModel().RESOURCE);
                options.add(new MarkerOptions()
                        .position(cluster.latLng)
                        .anchor(0.5f, 0.5f)
                        .icon(resolveResourceIcon(resource)));
                markerBundles.add(bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("type", obtainViewModel().RESOURCE_CLUSTER);
                bundle.putInt("size", cluster.resources.size());
                ArrayList<String> resourceIds = new ArrayList<>();
                for (Resource resource : cluster.resources) {
                    resourceIds.add(resource.getId());
                }
                bundle.putStringArrayList("resourceIds", resourceIds);
                options.add(new MarkerOptions()
                        .position(cluster.latLng)
                        .anchor(0.5f, 0.5f)
                        .icon(createClusterIcon(cluster.resources.size())));
                markerBundles.add(bundle);
            }
        }
        List<Marker> markers = aMap.addMarkers(options, false);
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).setObject(markerBundles.get(i));
        }
    }

    private void refreshResourceClustersIfNeeded(CameraPosition cameraPosition) {
        List<Resource> resources = obtainViewModel().getValidResourceList();
        if (resources.isEmpty()) {
            lastResourceClusterCameraPosition = cameraPosition;
            return;
        }
        if (!shouldRefreshResourceClusters(cameraPosition)) {
            return;
        }
        lastResourceClusterCameraPosition = cameraPosition;
        aMap.clear();
        updateMarkers();
    }

    private boolean shouldRefreshResourceClusters(CameraPosition cameraPosition) {
        if (cameraPosition == null || lastResourceClusterCameraPosition == null) {
            return true;
        }
        LatLng lastTarget = lastResourceClusterCameraPosition.target;
        LatLng currentTarget = cameraPosition.target;
        if (lastTarget == null || currentTarget == null) {
            return true;
        }
        return Math.abs(cameraPosition.zoom - lastResourceClusterCameraPosition.zoom) > 0.1F
                || Math.abs(currentTarget.latitude - lastTarget.latitude) > 0.0001D
                || Math.abs(currentTarget.longitude - lastTarget.longitude) > 0.0001D;
    }

    private BitmapDescriptor resolveResourceIcon(Resource resource) {
        if (Objects.equals(resource.getApiUrl(), "/api/helicopterPoint")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.airport);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/team")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.teem);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/dangerSource")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.danger);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/materialRepository")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.wuziku);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/waterSource")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.water);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/cemetery")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.md);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/watchTower")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.lwt);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/checkStation")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.check);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/monitor/kakou")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.kk);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/monitor/jiankong")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.onbody);
        }
        if (Objects.equals(resource.getApiUrl(), "/api/fireCommand")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.zhihui);
        }
        if (resource.getApiUrl() == null) {
            if (Objects.equals(resource.getResourceType(), "weatherStation")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.qixiang);
            }
            if (Objects.equals(resource.getResourceType(), "touristAttraction")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.lvyou);
            }
            if (Objects.equals(resource.getResourceType(), "residentialArea")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.jumindi);
            }
            if (Objects.equals(resource.getResourceType(), "ancientTree")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.gushu);
            }
            if (Objects.equals(resource.getResourceType(), "historicSites")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.wenwu);
            }
            if (Objects.equals(resource.getResourceType(), "shoppingMall")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.shop);
            }
            if (Objects.equals(resource.getResourceType(), "school")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.school);
            }
            if (Objects.equals(resource.getResourceType(), "baseStation")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.jizhan);
            }
            if (Objects.equals(resource.getResourceType(), "shelter")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.binan);
            }
            if (Objects.equals(resource.getResourceType(), "medicalAgency")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.yiliao);
            }
            if (Objects.equals(resource.getResourceType(), "garrisonPoint")) {
                return BitmapDescriptorFactory.fromResource(R.drawable.kaoqian);
            }
            if (resource.getResourceType() == null || Objects.equals(resource.getResourceType(), "")) {
                resource.setApiUrl("/api/dangerSource");
                resource.setResourceType("dangerSource");
                return BitmapDescriptorFactory.fromResource(R.drawable.danger);
            }
        }
        return BitmapDescriptorFactory.fromResource(R.drawable.kaoqian);
    }

    private BitmapDescriptor createClusterIcon(int size) {
        TextView textView = new TextView(requireContext());
        int paddingHorizontal = dp2px(12);
        int minSize = dp2px(40);
        textView.setMinWidth(minSize);
        textView.setMinHeight(minSize);
        textView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(14);
        textView.setText(String.format(Locale.getDefault(), "%d", size));
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        background.setColor(Color.parseColor("#D94B39"));
        background.setStroke(dp2px(2), Color.parseColor("#FFF3E0"));
        textView.setBackground(background);
        return BitmapDescriptorFactory.fromView(textView);
    }

    private int dp2px(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.max(1, (int) (dp * density + 0.5F));
    }

    private void showClusterResourceDialog(Marker marker) {
        Bundle extraInfo = (Bundle) marker.getObject();
        if (extraInfo == null) {
            return;
        }
        ArrayList<String> resourceIds = extraInfo.getStringArrayList("resourceIds");
        List<Resource> clusterResources = getResourcesByIds(resourceIds);
        if (clusterResources.isEmpty()) {
            return;
        }
        if (clusterResources.size() == 1) {
            resourceDetailDialog.setResource(clusterResources.get(0));
            delayDialog(resourceDetailDialog);
            return;
        }
        String[] names = new String[clusterResources.size()];
        for (int i = 0; i < clusterResources.size(); i++) {
            Resource resource = clusterResources.get(i);
            String name = resource.getResourceName();
            if (name == null || name.isEmpty()) {
                name = resource.getName();
            }
            if (name == null || name.isEmpty()) {
                name = "资源点" + (i + 1);
            }
            names[i] = name;
        }
        new AlertDialog.Builder(requireContext())
//                .setTitle("请选择资源点")
                .setItems(names, (dialog, which) -> {
                    resourceDetailDialog.setResource(clusterResources.get(which));
                    delayDialog(resourceDetailDialog);
                })
//                .setNegativeButton("取消", null)
                .show();
    }

    private List<Resource> getResourcesByIds(List<String> resourceIds) {
        List<Resource> result = new ArrayList<>();
        if (resourceIds == null || resourceIds.isEmpty()) {
            return result;
        }
        List<Resource> allResources = obtainViewModel().resourceList.getValue();
        if (allResources == null || allResources.isEmpty()) {
            return result;
        }
        for (String resourceId : resourceIds) {
            for (Resource resource : allResources) {
                if (Objects.equals(resource.getId(), resourceId)) {
                    result.add(resource);
                    break;
                }
            }
        }
        return result;
    }

    private static class ResourceCluster {
        private final List<Resource> resources = new ArrayList<>();
        private double latSum;
        private double lngSum;
        private LatLng latLng;
    }

    private void userLocationMarker(){
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.user);
        LatLng point = new LatLng(CommonData.lat, CommonData.lng);
        MarkerOptions option = new MarkerOptions()
                .position(point)
                .icon(btm);
        Marker marker = aMap.addMarker(option);
        Bundle bundle = new Bundle();
        bundle.putString("id", "userLocation");
        bundle.putInt("type", obtainViewModel().USER_LOCATION);
        marker.setObject(bundle);
    }


    private void initOneBodyListDialog() {
        oneBodyListDialog = new OneBodyListDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = oneBodyListDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        oneBodyListDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        oneBodyListDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            oneBodyListDialog.create();
        }
    }
    private void initOneBodyDetailDialog() {
        oneBodyDetailDialog = new OneBodyDetailDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = oneBodyDetailDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        oneBodyDetailDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.6);
        dialogWindow.setAttributes(lp);
        oneBodyDetailDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            oneBodyDetailDialog.create();
        }
    }
    private void initSatelliteListDialog() {
        satelliteListDialog = new SatelliteListDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = satelliteListDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        satelliteListDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        satelliteListDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            satelliteListDialog.create();
        }
    }
    private void initSatelliteDetailDialog() {
        satelliteDetailDialog = new SatelliteDetailDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = satelliteDetailDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        satelliteDetailDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        satelliteDetailDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            satelliteDetailDialog.create();
        }
    }
    private void initSatelliteSearchAdvancedDialog() {
        satelliteSearchAdvancedDialog = new SatelliteSearchAdvancedDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = satelliteSearchAdvancedDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        satelliteSearchAdvancedDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        satelliteSearchAdvancedDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            satelliteSearchAdvancedDialog.create();
        }
    }
    private void initSatelliteSearchDialog() {
        satelliteSearchDialog = new SatelliteSearchDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = satelliteSearchDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        satelliteSearchDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        satelliteSearchDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            satelliteSearchDialog.create();
        }
    }
    private void initSheQuListDialog() {
        sheQuListDialog = new SheQuListDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = sheQuListDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        sheQuListDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        sheQuListDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sheQuListDialog.create();
        }
    }
    private void initResourceListDialog() {
        resourceListDialog = new ResourceListDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = resourceListDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        resourceListDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        resourceListDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resourceListDialog.create();
        }
    }
    private void initResourceDetailDialog() {
        resourceDetailDialog = new ResourceDetailDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = resourceDetailDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        resourceDetailDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        resourceDetailDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resourceDetailDialog.create();
        }
    }

    @Override
    public void onOneBodyDialogRefresh() {
        obtainViewModel().currentPage = 1;
        obtainViewModel().getOneBodyData();
    }
    @Override
    public void onOneBodyDialogLoadMore() {
        obtainViewModel().currentPage++;
        obtainViewModel().getOneBodyData();
    }

    @Override
    public void onOneBodyDialogFilterState(int state) {
        //0全部  1未处理  2真实火点  3疑似火点
        obtainViewModel().oneBodyFilterState = state;


        aMap.clear();
        //更新所有Marker
        updateMarkers();
    }

    @Override
    public void onOneBodyDialogItemClick(OneBodyFire oneBodyFire) {
        oneBodyListDialog.hide();
        oneBodyDetailDialog.setOneBodyFire(oneBodyFire);
        oneBodyDetailDialog.show();
        flyBaiduMapZoom(Double.parseDouble(oneBodyFire.getAlarmLatitude()),Double.parseDouble(oneBodyFire.getAlarmLongitude()),14);
    }

    @Override
    public void onSatelliteDialogRefresh() {
        obtainViewModel().getSatelliteData(obtainViewModel().startTime,obtainViewModel().endTime);
    }

    @Override
    public void onSatelliteDialogItemClick(SatelliteFire satelliteFire) {
        satelliteListDialog.hide();
        satelliteDetailDialog.setSatelliteFire(satelliteFire);
        satelliteDetailDialog.show();
        flyBaiduMapZoom(Double.parseDouble(satelliteFire.getLatitude()),Double.parseDouble(satelliteFire.getLongitude()),14);
    }

    @Override
    public void onOneBodyDetailDialogRefresh() {
        //处理火警后回调
        obtainViewModel().getOneBodyData();
    }

    @Override
    public void onSatelliteDetailDialogRefresh() {

    }

    @Override
    public void onResourceDialogRefresh() {
        //资源类型列表刷新
        obtainViewModel().getResourceTypeData();
    }

    @Override
    public void onResourceDialogItemClick(ResourceType resourceType,boolean state) {
        //资源类型点击
        //更新Dialog列表数据
        obtainViewModel().clickModel(resourceType,state);
    }

    @Override
    public void onResourceDetailDialogRefresh() {

    }

    @Override
    public void onSatelliteSearchDialogRefresh(String startTime, String endTime) {
        if(startTime!=null){
            obtainViewModel().startTime = startTime;
            obtainViewModel().endTime = endTime;
            obtainViewModel().getSatelliteData(obtainViewModel().startTime,obtainViewModel().endTime);
            satelliteListDialog.show();
        }else{
            //advanced
            satelliteSearchAdvancedDialog.show();
        }
    }

    @Override
    public void onSatelliteSearchAdvancedDialogRefresh(String startTime, String endTime) {
        obtainViewModel().startTime = startTime;
        obtainViewModel().endTime = endTime;
        obtainViewModel().getSatelliteData(obtainViewModel().startTime,obtainViewModel().endTime);
        satelliteListDialog.show();
    }

    @Override
    public void onSheQuDialogRefresh() {
        //资源类型列表刷新
        obtainViewModel().initSheQuGrid();
    }

    @Override
    public void onSheQuDialogItemClick(SheQu sheQu, boolean state) {
        //社区网格列表点击
        //更新Dialog列表数据
        obtainViewModel().clickModelShequ(sheQu, state);
    }
}
