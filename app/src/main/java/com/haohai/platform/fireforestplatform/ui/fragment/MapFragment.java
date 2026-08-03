package com.haohai.platform.fireforestplatform.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
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
import com.haohai.platform.fireforestplatform.event.Search;
import com.haohai.platform.fireforestplatform.old.ResourceAddActivity;
import com.haohai.platform.fireforestplatform.old.TrackService;
import com.haohai.platform.fireforestplatform.old.bean.MapDialogDismiss;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.SatelliteSettingActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.bean.Resource;
import com.haohai.platform.fireforestplatform.ui.cell.DroneDetailDialog;
import com.haohai.platform.fireforestplatform.ui.cell.DroneListDialog;
import com.haohai.platform.fireforestplatform.ui.cell.LandDetailDialog;
import com.haohai.platform.fireforestplatform.ui.cell.LandListDialog;
import com.haohai.platform.fireforestplatform.ui.cell.SheQuListDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.DroneFire;
import com.haohai.platform.fireforestplatform.ui.multitype.LandFire;
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
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MapFragment extends BaseFragment<FgMap, FgMapViewModel> implements OneBodyListDialog.OneBodyDialogListener, SatelliteListDialog.SatelliteDialogListener, OneBodyDetailDialog.OneBodyDetailDialogListener, SatelliteDetailDialog.SatelliteDetailDialogListener, ResourceDetailDialog.ResourceDetailDialogListener, ResourceListDialog.ResourceDialogListener, SatelliteSearchDialog.SatelliteSearchDialogListener, SatelliteSearchAdvancedDialog.SatelliteSearchAdvancedDialogListener, SheQuListDialog.SheQuDialogListener, LandListDialog.LandDialogListener, DroneListDialog.DroneDialogListener, LandDetailDialog.LandDetailDialogListener {

    private final String TAG = MapFragment.class.getSimpleName();
    private OneBodyListDialog oneBodyListDialog;
    private LandListDialog landListDialog;
    private DroneListDialog droneListDialog;
    private SatelliteListDialog satelliteListDialog;
    private OneBodyDetailDialog oneBodyDetailDialog;
    private LandDetailDialog landDetailDialog;
    private DroneDetailDialog droneDetailDialog;
    private SatelliteDetailDialog satelliteDetailDialog;
    private SatelliteSearchAdvancedDialog satelliteSearchAdvancedDialog;
    private SatelliteSearchDialog satelliteSearchDialog;
    private ResourceListDialog resourceListDialog;
    private SheQuListDialog sheQuListDialog;
    private ResourceDetailDialog resourceDetailDialog;
    private AMap aMap;
    private Marker userLocationMarkerOnMap;
    private boolean hasRequestedBackgroundLocationSettings = false;
    private boolean waitingForBackgroundLocationSettings = false;
    private boolean hasRequestedBatteryOptimizationSettings = false;

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
        ensureTrackServiceRunning();

        return binding.getRoot();
    }

    private void ensureTrackServiceRunning() {
        if (!hasForegroundLocationPermission()) {
            requestForegroundLocationPermission();
            return;
        }
        startTrackServiceCompat();
        if (!hasBackgroundLocationPermission()) {
            requestBackgroundLocationPermission();
            return;
        }
        requestIgnoreBatteryOptimizationsIfNeed();
    }

    private boolean hasForegroundLocationPermission() {
        Context context = getContext();
        return context != null
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasBackgroundLocationPermission() {
        Context context = getContext();
        if (context == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForegroundLocationPermission() {
        RxPermissions rxPermissions = new RxPermissions(requireActivity());
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        ensureTrackServiceRunning();
                    } else {
                        Toast.makeText(requireActivity(), "请开启定位权限后再使用巡护定位功能", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            startTrackServiceCompat();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (hasRequestedBackgroundLocationSettings) {
                return;
            }
            hasRequestedBackgroundLocationSettings = true;
            waitingForBackgroundLocationSettings = true;
            Toast.makeText(requireActivity(), "请在系统设置中开启“始终允许”定位权限，保证锁屏和后台持续巡护", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
            startActivity(intent);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(requireActivity());
        rxPermissions.request(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        ensureTrackServiceRunning();
                    } else {
                        Toast.makeText(requireActivity(), "后台定位权限未开启，锁屏和后台定位可能会被系统限制", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void requestIgnoreBatteryOptimizationsIfNeed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        PowerManager powerManager = (PowerManager) requireActivity().getSystemService(Context.POWER_SERVICE);
        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(requireActivity().getPackageName())) {
            if (hasRequestedBatteryOptimizationSettings) {
                return;
            }
            hasRequestedBatteryOptimizationSettings = true;
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                HhLog.e(e.getMessage());
            }
        }
    }

    private void startTrackServiceCompat() {
        Intent intent = new Intent(requireActivity(), TrackService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(requireActivity(), intent);
        } else {
            requireActivity().startService(intent);
        }
    }

    private void bind_() {
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
        binding.viewWarnListLand.setOnClickListener(v -> {
            delayDialog(landListDialog);
        });
        binding.viewWarnListDrone.setOnClickListener(v -> {
            delayDialog(droneListDialog);
        });
        binding.viewTask.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), TaskActivity.class));
        });
        binding.viewLocation.setOnClickListener(v -> {
            double[] doubles = LatLngChangeNew.calBD09toGCJ02(CommonData.lat,CommonData.lng);
            flyBaiduMapZoom(doubles[0],doubles[1], 14);
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
        }else if(index == 3 && Objects.equals(type, "landFire")){
            obtainViewModel().getLandData();
            landListDialog.show();
        }else if(index == 3 && Objects.equals(type, "droneFire")){
            obtainViewModel().getDroneData();
            droneListDialog.show();
        }
    }

    ///资源删除-地图资源刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MapDialogDismiss event) {
        Log.e(TAG, "onGetMessage: type" + event.getType() );
        ResourceType resourceType = new ResourceType();
        resourceType.setApiUrl("/api/"+event.getType());
        resourceType.setCode(event.getType());
        HhLog.e("onGetMessage " + event.isAdd());
        if(event.isAdd()){
            obtainViewModel().updateResEventAdding(resourceType,event.getName());
        }else{
            obtainViewModel().updateResEvent(resourceType);
        }
        Log.e(TAG, "onGetMessage: apiurl" + resourceType.getApiUrl() );
        resourceDetailDialog.hide();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Search event) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    double[] doubles = LatLngChangeNew.calWGS84toGCJ02(event.getLat(), event.getLng());
                    flyBaiduMapZoom(doubles[0],doubles[1], 14);
                }catch (Exception e){
                    Log.e(TAG, "oneBodyFireChanged: " + e.getMessage() );
                }
            }
        },100);
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
        /*if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_Grid_COMMUNITY)){*/
            binding.viewGridShequ.setVisibility(View.GONE);
            binding.viewGridAll.setVisibility(View.GONE);
        /*}*/
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
            if(markerType == obtainViewModel().ONE_BODY){
                OneBodyFire oneBodyFire = new OneBodyFire();
                List<OneBodyFire> oneBodyListValue = obtainViewModel().oneBodyList.getValue();
                assert oneBodyListValue != null;
                for (OneBodyFire values:oneBodyListValue) {
                    if(Objects.equals(values.getId(), markerId)){
                        oneBodyFire = values;
                    }
                }
                oneBodyDetailDialog.setOneBodyFire(oneBodyFire);
                delayDialog(oneBodyDetailDialog);
            }else if(markerType == obtainViewModel().LAND){
                LandFire landFire = new LandFire();
                List<LandFire> landListValue = obtainViewModel().landList.getValue();
                assert landListValue != null;
                for (LandFire values:landListValue) {
                    if(Objects.equals(values.getId(), markerId)){
                        landFire = values;
                    }
                }
                landDetailDialog.setOneBodyFire(landFire);
                delayDialog(landDetailDialog);
            }else if(markerType == obtainViewModel().DRONE){
                DroneFire droneFire = new DroneFire();
                List<DroneFire> droneListValue = obtainViewModel().droneList.getValue();
                assert droneListValue != null;
                for (DroneFire values:droneListValue) {
                    if(Objects.equals(values.getId(), markerId)){
                        droneFire = values;
                    }
                }
                droneDetailDialog.setDroneFire(droneFire);
                delayDialog(droneDetailDialog);
            }else if(markerType == obtainViewModel().SATELLITE){
                for (SatelliteFire res: Objects.requireNonNull(obtainViewModel().satelliteList.getValue())) {
                    if(Objects.equals(res.getId(), markerId)){
                        satelliteDetailDialog.setSatelliteFire(res);
                        delayDialog(satelliteDetailDialog);
                        return false;
                    }
                }
            }else if(markerType == obtainViewModel().RESOURCE){
                for (Resource res: Objects.requireNonNull(obtainViewModel().resourceList.getValue())) {
                    if(Objects.equals(res.getId(), markerId)){
                        resourceDetailDialog.setResource(res);
                        delayDialog(resourceDetailDialog);
                        return false;
                    }
                }
            }

            return false;
        });

        //一体机报警列表Dialog
        initOneBodyListDialog();
        //地表火报警列表Dialog
        initLandListDialog();
        //无人机报警列表Dialog
        initDroneListDialog();
        //卫星报警列表Dialog
        initSatelliteListDialog();
        //卫星报警查询Dialog
        initSatelliteSearchDialog();
        //卫星报警查询高级Dialog
        initSatelliteSearchAdvancedDialog();
        //一体机报警Marker详情Dialog
        initOneBodyDetailDialog();
        //地表火报警Marker详情Dialog
        initLandDetailDialog();
        //无人机报警Marker详情Dialog
        initDroneDetailDialog();
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
        if (waitingForBackgroundLocationSettings) {
            waitingForBackgroundLocationSettings = false;
            if (!hasBackgroundLocationPermission()) {
                Toast.makeText(requireActivity(), "后台定位权限未开启，锁屏和后台定位可能会被系统限制", Toast.LENGTH_LONG).show();
            }
        }
        ensureTrackServiceRunning();
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
        //地表火火警数据
        obtainViewModel().landList.observe(requireActivity(), this::landFireChanged);
        //无人机火警数据
        obtainViewModel().droneList.observe(requireActivity(), this::droneFireChanged);
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
        clearMapMarkers();
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
    private void landFireChanged(List<LandFire> oneBodyFires) {
        clearMapMarkers();
        //更新Dialog列表数据
        landListDialog.setOneBodyFireList(oneBodyFires,obtainViewModel().currentPageLand);
        //更新所有Marker
        updateMarkers();
        //跳转第一火点
        try{
            LandFire oneBody = oneBodyFires.get(0);
            double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(oneBody.getLatitude()), Double.parseDouble(oneBody.getLongitude()));
            flyBaiduMapZoom(doubles[0],doubles[1], 14);
        }catch (Exception e){
            Log.e(TAG, "oneBodyFireChanged: " + e.getMessage() );
        }
    }
    private void droneFireChanged(List<DroneFire> droneFires) {
        clearMapMarkers();
        //更新Dialog列表数据
        droneListDialog.setDroneFireList(droneFires,obtainViewModel().currentPageDrone);
        //更新所有Marker
        updateMarkers();
        //跳转第一火点
        try{
            DroneFire droneFire = droneFires.get(0);
            double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(droneFire.getLatitude()), Double.parseDouble(droneFire.getLongitude()));
            flyBaiduMapZoom(doubles[0],doubles[1], 14);
        }catch (Exception e){
            Log.e(TAG, "droneFireChanged: " + e.getMessage() );
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
        //绘制地表火火点Marker
        HhLog.e("updateMarkers: " + obtainViewModel().landList.getValue());
        if(obtainViewModel().landList.getValue()!=null){
            List<LandFire> value = obtainViewModel().landList.getValue();
            List<LandFire> list = new ArrayList<>();
            if(value!=null && value.size()>0){
                //0全部  1未处理  2真实火点  3疑似火点
                if(obtainViewModel().landFilterState == 0){
                    /*for (int i = 0; i < value.size(); i++) {
                        OneBodyFire fire = value.get(i);
                        if(!Objects.equals(fire.getIsReal(), "0")){
                            list.add(fire);
                        }
                    }*/
                    list.addAll(value);
                }
                if(obtainViewModel().landFilterState == 1){
                    for (int i = 0; i < value.size(); i++) {
                        LandFire fire = value.get(i);
                        if(fire.getIsReal() == null){
                            list.add(fire);
                        }
                    }
                }
                if(obtainViewModel().landFilterState == 2){
                    for (int i = 0; i < value.size(); i++) {
                        LandFire fire = value.get(i);
                        if(Objects.equals(fire.getIsReal(), "1")){
                            list.add(fire);
                        }
                    }
                }
                if(obtainViewModel().landFilterState == 3){
                    for (int i = 0; i < value.size(); i++) {
                        LandFire fire = value.get(i);
                        if(Objects.equals(fire.getIsReal(), "0")){
                            list.add(fire);
                        }
                    }
                }
            }
            landMarker(list);
        }
        //绘制无人机火点Marker
        if(obtainViewModel().droneList.getValue()!=null){
            droneMarker(Objects.requireNonNull(obtainViewModel().droneList.getValue()));
        }
        //绘制卫星火点Marker
        if(obtainViewModel().satelliteList.getValue()!=null){
            satelliteMarker(Objects.requireNonNull(obtainViewModel().satelliteList.getValue()));
        }
        //绘制资源点Marker
        if(obtainViewModel().resourceList.getValue()!=null){
            resourceMarker(Objects.requireNonNull(obtainViewModel().resourceList.getValue()));
        }
        //绘制当前社区图层数据
        if(obtainViewModel().sheQuCurrentList.getValue()!=null && !obtainViewModel().sheQuCurrentList.getValue().isEmpty()){
            sheQuMarker(Objects.requireNonNull(obtainViewModel().sheQuCurrentList.getValue()));
        }
    }

    private void satelliteFireChanged(List<SatelliteFire> satelliteFires) {
        clearMapMarkers();
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
        clearMapMarkers();
        //更新所有Marker
        updateMarkers();
        //跳转第一火点
        long now = new Date().getTime();
        if(now - CommonData.longAdding < 5000){
            //新添加了资源
            try{
                double[] doubles = LatLngChangeNew.calWGS84toGCJ02(CommonData.latAdding, CommonData.lngAdding);
                flyBaiduMapZoom(doubles[0],doubles[1], 14);
            }catch (Exception e){
                Log.e(TAG, "resourceChanged: " + e.getMessage() );
            }
        }else{
            //跳转第一个资源点
            try{
                Resource resource = resources.get(0);
                double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(resource.getPosition().getLat()), Double.parseDouble(resource.getPosition().getLng()));
                flyBaiduMapZoom(doubles[0],doubles[1], 14);
            }catch (Exception e){
                Log.e(TAG, "resourceChanged: " + e.getMessage() );
            }
        }
    }
    private void sheQuChanged(List<SheQu> sheQus) {
        //更新Dialog列表数据
        sheQuListDialog.setSheQuList(sheQus);
    }
    private void sheQuCurrentChanged(List<ArrayList<Double>> points) {
        //更新地图图层数据
        clearMapMarkers();
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
    private void landMarker(List<LandFire> landFires) {
        ArrayList<MarkerOptions> options = new ArrayList<>();
        List<LandFire> markerLandFires = new ArrayList<>();
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_yellow_fire);//默认森林防火
        for (int i = 0; i < landFires.size(); i++) {
            /*switch (landFires.get(i).getType()){
                case "2":
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_red_fire);//森林防火
                    break;
                case "4":
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_blue_fire);//海域监控
                    break;
                case "5":
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_yellow_fire);//国土报警
                    break;
            }*/
            try {
                double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(landFires.get(i).getLatitude()), Double.parseDouble(landFires.get(i).getLongitude()));
                LatLng point = new LatLng(doubles[0], doubles[1]);
                MarkerOptions option = new MarkerOptions()
                        .position(point)
                        .icon(btm);
                options.add(option);
                markerLandFires.add(landFires.get(i));
            }catch (Exception e){
                Log.e(TAG, "landMarker: " + e.getMessage() );
                continue;
            }
        }
        List<Marker> markers = aMap.addMarkers(options, false);

        try{
            for (int i = 0; i < markers.size(); i++) {
                Bundle bundle = new Bundle();
                bundle.putString("id", markerLandFires.get(i).getId());
                bundle.putInt("type", obtainViewModel().LAND);
                markers.get(i).setObject(bundle);
            }
        }catch (Exception e){
            //
        }
    }
    private void droneMarker(List<DroneFire> droneFires) {
        ArrayList<MarkerOptions> options = new ArrayList<>();
        List<DroneFire> markerDroneFires = new ArrayList<>();
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_red_fire);//默认森林防火
        for (int i = 0; i < droneFires.size(); i++) {
            try {
                double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(droneFires.get(i).getLatitude()), Double.parseDouble(droneFires.get(i).getLongitude()));
                LatLng point = new LatLng(doubles[0], doubles[1]);
                MarkerOptions option = new MarkerOptions()
                        .position(point)
                        .icon(btm);
                options.add(option);
                markerDroneFires.add(droneFires.get(i));
            }catch (Exception e){
                Log.e(TAG, "droneMarker: " + e.getMessage() );
                continue;
            }
        }
        List<Marker> markers = aMap.addMarkers(options, false);

        try{
            for (int i = 0; i < markers.size(); i++) {
                Bundle bundle = new Bundle();
                bundle.putString("id", markerDroneFires.get(i).getId());
                bundle.putInt("type", obtainViewModel().DRONE);
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
        ArrayList<MarkerOptions> options = new ArrayList<>();
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_fire);
        for (int i = 0; i < resources.size(); i++) {
            //TODO 需后端配置后自动获取类型图标
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/helicopterPoint")){//停机坪
                btm = BitmapDescriptorFactory.fromResource(R.drawable.airport);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/team")){//消防专业队
                btm = BitmapDescriptorFactory.fromResource(R.drawable.teem);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/dangerSource")){//危险源
                btm = BitmapDescriptorFactory.fromResource(R.drawable.danger);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/materialRepository")){//物资库
                btm = BitmapDescriptorFactory.fromResource(R.drawable.wuziku);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/waterSource")){//水源地
                btm = BitmapDescriptorFactory.fromResource(R.drawable.water);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/cemetery")){//墓地
                btm = BitmapDescriptorFactory.fromResource(R.drawable.md);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/watchTower")){//瞭望塔
                btm = BitmapDescriptorFactory.fromResource(R.drawable.lwt);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/checkStation")){//护林检查站
                btm = BitmapDescriptorFactory.fromResource(R.drawable.check);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/forestRoom")){//护林房
                btm = BitmapDescriptorFactory.fromResource(R.drawable.room);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/monitor/kakou")){//卡口
                btm = BitmapDescriptorFactory.fromResource(R.drawable.kk);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/monitor/jiankong")){//监控点
                btm = BitmapDescriptorFactory.fromResource(R.drawable.onbody);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/fireCommand")){//指挥部
                btm = BitmapDescriptorFactory.fromResource(R.drawable.zhihui);
            }
            /*if(Objects.equals(resources.get(i).getApiUrl(), "/api/isolationNet")){//隔离网
                btm = BitmapDescriptorFactory.fromResource(R.drawable.geliwang);
            }*/
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/isolationBelt") && Objects.equals(resources.get(i).getType(), "2")){//隔离网
                btm = BitmapDescriptorFactory.fromResource(R.drawable.geliwang);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/isolationBelt") && Objects.equals(resources.get(i).getType(), "1")){//隔离带
                btm = BitmapDescriptorFactory.fromResource(R.drawable.gelidai);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/hazardousChemical")){//危化品企业 chemicalEnterprises  /api/hazardousChemical
                btm = BitmapDescriptorFactory.fromResource(R.drawable.weihuapinqiye);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/miningEnterprises")){//采矿企业
                btm = BitmapDescriptorFactory.fromResource(R.drawable.caikuangqiye);
            }
            if(Objects.equals(resources.get(i).getApiUrl(), "/api/fireEscape")){//防火通道
                btm = BitmapDescriptorFactory.fromResource(R.drawable.fanghuotongdao);
            }
            //新增资源类型
            if(resources.get(i).getApiUrl()==null){
                if(Objects.equals(resources.get(i).getResourceType(), "weatherStation")){//气象站
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.qixiang);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "weatherCar")){//气象车
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.weathercar);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "touristAttraction")){//旅游景点
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.lvyou);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "residentialArea")){//居民地
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.jumindi);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "ancientTree")){//古树名木
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.gushu);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "historicSites")){//文物古迹
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.wenwu);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "shoppingMall")){//商场
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.shop);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "school")){//学校
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.school);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "baseStation")){//通信基站
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.jizhan);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "shelter")){//避难场所
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.binan);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "medicalAgency")){//医疗机构
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.yiliao);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "garrisonPoint")){//靠前驻防点
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.kaoqian);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "gasStation")){//加油站
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.jiayouzhan);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "productionUnits")){//林区生产经营单位
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.linqushengchanjingyingdanwei);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "policeOrgan")){//公安机关
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.gognanjiguan);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "fireRescueTeam")){//扑火队
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.puhuodui);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "forestCommunity")){//林中社区
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.linzhongshequ);
                }
                if(Objects.equals(resources.get(i).getResourceType(), "protect")){//重点保护对象
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.zhongdianbaohuduixiang);
                }

                //未知类型
                if(resources.get(i).getResourceType()==null || Objects.equals(resources.get(i).getResourceType(), "")){//靠前驻防点
                    btm = BitmapDescriptorFactory.fromResource(R.drawable.danger);
                    resources.get(i).setApiUrl("/api/dangerSource");
                    resources.get(i).setResourceType("dangerSource");
                }
            }
            try {
                double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(resources.get(i).getPosition().getLat()), Double.parseDouble(resources.get(i).getPosition().getLng()));
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
                bundle.putString("id", resources.get(i).getId());
                bundle.putInt("type", obtainViewModel().RESOURCE);
                markers.get(i).setObject(bundle);
            }
        }catch (Exception e){
            //
        }
    }

    private void userLocationMarker(){
        double[] doubles = LatLngChangeNew.calBD09toGCJ02(CommonData.lat,CommonData.lng);
        LatLng point = new LatLng(doubles[0], doubles[1]);
        if (userLocationMarkerOnMap != null) {
            userLocationMarkerOnMap.setPosition(point);
            return;
        }
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.user);
        MarkerOptions option = new MarkerOptions()
                .position(point)
                .icon(btm);
        userLocationMarkerOnMap = aMap.addMarker(option);
        Bundle bundle = new Bundle();
        bundle.putString("id", "userLocation");
        bundle.putInt("type", obtainViewModel().USER_LOCATION);
        userLocationMarkerOnMap.setObject(bundle);
    }

    private void clearMapMarkers() {
        aMap.clear();
        userLocationMarkerOnMap = null;
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
    private void initLandListDialog() {
        landListDialog = new LandListDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = landListDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        landListDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        landListDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            landListDialog.create();
        }
    }
    private void initDroneListDialog() {
        droneListDialog = new DroneListDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = droneListDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        droneListDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        droneListDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            droneListDialog.create();
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
    private void initLandDetailDialog() {
        landDetailDialog = new LandDetailDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = landDetailDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        landDetailDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.6);
        dialogWindow.setAttributes(lp);
        landDetailDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            landDetailDialog.create();
        }
    }
    private void initDroneDetailDialog() {
        droneDetailDialog = new DroneDetailDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = droneDetailDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        dialogWindow.setAttributes(lp);
        droneDetailDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            droneDetailDialog.create();
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


        clearMapMarkers();
        //更新所有Marker
        updateMarkers();
    }

    @Override
    public void onOneBodyDialogItemClick(OneBodyFire oneBodyFire) {
        oneBodyListDialog.hide();
        oneBodyDetailDialog.setOneBodyFire(oneBodyFire);
        oneBodyDetailDialog.show();
        double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(oneBodyFire.getAlarmLatitude()),Double.parseDouble(oneBodyFire.getAlarmLongitude()));
        flyBaiduMapZoom(doubles[0],doubles[1], 14);
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
        double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(satelliteFire.getLatitude()),Double.parseDouble(satelliteFire.getLongitude()));
        flyBaiduMapZoom(doubles[0],doubles[1], 14);
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

    @Override
    public void onLandDialogRefresh() {
        obtainViewModel().currentPageLand = 1;
        obtainViewModel().getLandData();
    }

    @Override
    public void onLandDialogLoadMore() {
        obtainViewModel().currentPageLand++;
        obtainViewModel().getLandData();
    }

    @Override
    public void onLandDialogFilterState(int state) {
        //0全部  1未处理  2真实火点  3疑似火点
        obtainViewModel().landFilterState = state;


        clearMapMarkers();
        //更新所有Marker
        updateMarkers();
    }

    @Override
    public void onLandDialogItemClick(LandFire landFire) {
        landListDialog.hide();
        landDetailDialog.setOneBodyFire(landFire);
        landDetailDialog.show();
        double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(landFire.getLatitude()),Double.parseDouble(landFire.getLongitude()));
        flyBaiduMapZoom(doubles[0],doubles[1], 14);
    }

    @Override
    public void onDroneDialogRefresh() {
        obtainViewModel().currentPageDrone = 1;
        obtainViewModel().getDroneData();
    }

    @Override
    public void onDroneDialogLoadMore() {
        obtainViewModel().currentPageDrone++;
        obtainViewModel().getDroneData();
    }

    @Override
    public void onDroneDialogFilterState(int state) {
        //0全部  1未处理  2真实火点  3疑似火点
        obtainViewModel().droneFilterState = state;
        obtainViewModel().currentPageDrone = 1;
        obtainViewModel().droneList.setValue(new ArrayList<>());
        obtainViewModel().getDroneData();
    }

    @Override
    public void onDroneDialogItemClick(DroneFire droneFire) {
        droneListDialog.hide();
        droneDetailDialog.setDroneFire(droneFire);
        droneDetailDialog.show();
        try{
            double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(droneFire.getLatitude()),Double.parseDouble(droneFire.getLongitude()));
            flyBaiduMapZoom(doubles[0],doubles[1], 14);
        }catch (Exception e){
            Log.e(TAG, "onDroneDialogItemClick: " + e.getMessage() );
        }
    }

    @Override
    public void onLandDetailDialogRefresh() {
        //处理火警后回调
        obtainViewModel().getLandData();
    }
}
