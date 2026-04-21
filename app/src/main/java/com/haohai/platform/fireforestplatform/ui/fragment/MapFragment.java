package com.haohai.platform.fireforestplatform.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
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
import com.haohai.platform.fireforestplatform.ui.bean.TeamMate;
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
import com.haohai.platform.fireforestplatform.ui.cell.TeamMateDetailDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private TeamMateDetailDialog teamMateDetailDialog;
    private static final int MEASURE_MODE_NONE = 0;
    private static final int MEASURE_MODE_DISTANCE = 1;
    private static final int MEASURE_MODE_AREA = 2;
    private static final int MAP_OVERLAY_TYPE_MEASURE = -100;
    private static final long TEAM_LOCATION_REFRESH_TIME = 10000L;
    private int currentMeasureMode = MEASURE_MODE_NONE;
    private boolean teamLocationEnabled = false;
    private final List<LatLng> distancePoints = new ArrayList<>();
    private final List<Marker> distanceMarkers = new ArrayList<>();
    private final List<Polyline> distancePolylines = new ArrayList<>();
    private final List<Text> distanceTexts = new ArrayList<>();
    private final List<LatLng> areaPoints = new ArrayList<>();
    private final List<Marker> areaMarkers = new ArrayList<>();
    private Polyline areaPolyline;
    private Polygon areaPolygon;
    private Text areaText;
    private final Handler teamLocationHandler = new Handler();
    private final Runnable teamLocationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!teamLocationEnabled) {
                return;
            }
            obtainViewModel().getTeamMateData(false);
            teamLocationHandler.postDelayed(this, TEAM_LOCATION_REFRESH_TIME);
        }
    };

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
        obtainViewModel().aMap = binding.aMapView.getMap();
        obtainViewModel().aMap.setMapType(AMap.MAP_TYPE_SATELLITE);

        init_();
        bind_();
        obtainViewModel().getData();
        //开启轨迹服务
        requireActivity().startService(new Intent(requireActivity(), TrackService.class));

        return binding.getRoot();
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
        binding.viewTask.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), TaskActivity.class));
        });
        binding.viewLocation.setOnClickListener(v -> {
            flyBaiduMapZoom(CommonData.lat, CommonData.lng, 14);
            userLocationMarker();
        });
        binding.viewTeamLocation.setOnClickListener(v -> toggleTeamLocation());
        binding.viewGridShequ.setOnClickListener(v -> {
            sheQuListDialog.show();
        });
        binding.viewMeasureDistance.setOnClickListener(v -> toggleDistanceMeasure());
        binding.viewMeasureArea.setOnClickListener(v -> toggleAreaMeasure());
        updateMeasureButtonState();
        updateToggleButton(binding.viewTeamLocation, teamLocationEnabled);
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
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_Grid_COMMUNITY)){
            binding.viewGridShequ.setVisibility(View.GONE);
            binding.viewGridAll.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MAP_TASK)){
            binding.viewTask.setVisibility(View.GONE);
        }


        flyBaiduMapZoom(CommonData.lat, CommonData.lng, 14);
        obtainViewModel().aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                closeInput(binding.editFind);
                handleMeasureMapClick(latLng);
            }
        });
        obtainViewModel().aMap.setOnPOIClickListener(new AMap.OnPOIClickListener() {
            @Override
            public void onPOIClick(Poi poi) {
                closeInput(binding.editFind);
                handleMeasureMapClick(poi.getCoordinate());
            }
        });
        obtainViewModel().aMap.setOnMarkerClickListener(marker -> {
            Object markerObject = marker.getObject();
            if (!(markerObject instanceof Bundle)) {
                return false;
            }
            Bundle extraInfo = (Bundle) markerObject;
            String markerId = extraInfo.getString("id");
            int markerType = extraInfo.getInt("type");
            if (markerType == MAP_OVERLAY_TYPE_MEASURE) {
                return true;
            }
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
            }else if(markerType == obtainViewModel().TEAM_MATE){
                TeamMate teamMate = getTeamMateById(markerId);
                if (teamMate != null) {
                    teamMateDetailDialog.setTeamMate(teamMate);
                    delayDialog(teamMateDetailDialog);
                }
                return true;
            }

            return false;
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
        //队友Marker详情Dialog
        initTeamMateDetailDialog();

        //跳转当前位置
        new Handler().postDelayed(() -> {
            if(CommonData.lat!=0 && (obtainViewModel().oneBodyList.getValue()==null||obtainViewModel().oneBodyList.getValue().isEmpty())) {
                flyBaiduMapZoom(CommonData.lat, CommonData.lng, 14);
            }
        }, 3000);
    }

    private void flyBaiduMapZoom(double lat, double lng, int zoom) {
        //飞到精确点上
        obtainViewModel().aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.amap.api.maps.model.LatLng(lat, lng),zoom));
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时必须调用mMapView. onResume ()
        binding.aMapView.onResume();
        if (teamLocationEnabled) {
            teamLocationHandler.removeCallbacks(teamLocationRunnable);
            teamLocationHandler.postDelayed(teamLocationRunnable, TEAM_LOCATION_REFRESH_TIME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.aMapView.onPause();
        teamLocationHandler.removeCallbacks(teamLocationRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.aMapView.onDestroy();
        teamLocationHandler.removeCallbacks(teamLocationRunnable);
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
        //队友位置数据
        obtainViewModel().teamMateList.observe(requireActivity(), this::teamMateChanged);
    }

    private void oneBodyFireChanged(List<OneBodyFire> oneBodyFires) {
        obtainViewModel().aMap.clear();
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
            resourceMarker(Objects.requireNonNull(obtainViewModel().resourceList.getValue()));
        }
        //绘制当前社区图层数据
        if(obtainViewModel().sheQuCurrentList.getValue()!=null && !obtainViewModel().sheQuCurrentList.getValue().isEmpty()){
            sheQuMarker(Objects.requireNonNull(obtainViewModel().sheQuCurrentList.getValue()));
        }
        //绘制队友位置Marker
        if(teamLocationEnabled && obtainViewModel().teamMateList.getValue()!=null){
            teamMateMarker(Objects.requireNonNull(obtainViewModel().teamMateList.getValue()));
        }
        redrawMeasureOverlays();
    }

    private void satelliteFireChanged(List<SatelliteFire> satelliteFires) {
        obtainViewModel().aMap.clear();
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
        obtainViewModel().aMap.clear();
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
        obtainViewModel().aMap.clear();
        updateMarkers();
    }
    private void resourceTypeChanged(List<ResourceType> resourceTypes) {
        //更新Dialog列表数据
        resourceListDialog.setResourceTypeList(resourceTypes);
    }

    private void teamMateChanged(List<TeamMate> teamMates) {
        if (!teamLocationEnabled) {
            return;
        }
        obtainViewModel().aMap.clear();
        updateMarkers();
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
        List<Marker> markers = obtainViewModel().aMap.addMarkers(options, false);

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
        obtainViewModel().aMap.addPolygon(polygonOptions);

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
        List<Marker> markers = obtainViewModel().aMap.addMarkers(options, false);

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
        List<Marker> markers = obtainViewModel().aMap.addMarkers(options, false);

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
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.user);
        LatLng point = new LatLng(CommonData.lat, CommonData.lng);
        MarkerOptions option = new MarkerOptions()
                .position(point)
                .icon(btm);
        Marker marker = obtainViewModel().aMap.addMarker(option);
        Bundle bundle = new Bundle();
        bundle.putString("id", "userLocation");
        bundle.putInt("type", obtainViewModel().USER_LOCATION);
        marker.setObject(bundle);
    }

    private void teamMateMarker(List<TeamMate> teamMates){
        ArrayList<MarkerOptions> options = new ArrayList<>();
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_friend);
        for (TeamMate teamMate : teamMates) {
            if(teamMate.getPosition()==null){
                continue;
            }
            try {
                double[] doubles = LatLngChangeNew.calWGS84toGCJ02(teamMate.getPosition().getLat(), teamMate.getPosition().getLng());
                LatLng point = new LatLng(doubles[0], doubles[1]);
                MarkerOptions option = new MarkerOptions()
                        .position(point)
                        .icon(btm);
                options.add(option);
            }catch (Exception e){
                Log.e(TAG, "teamMateMarker: " + e.getMessage());
            }
        }
        List<Marker> markers = obtainViewModel().aMap.addMarkers(options, false);
        int markerIndex = 0;
        for (TeamMate teamMate : teamMates) {
            if(teamMate.getPosition()==null){
                continue;
            }
            if(markerIndex >= markers.size()){
                break;
            }
            Bundle bundle = new Bundle();
            bundle.putString("id", teamMate.getId());
            bundle.putInt("type", obtainViewModel().TEAM_MATE);
            markers.get(markerIndex).setObject(bundle);
            markerIndex++;
        }
    }

    private TeamMate getTeamMateById(String teamMateId){
        List<TeamMate> teamMates = obtainViewModel().teamMateList.getValue();
        if(teamMates == null){
            return null;
        }
        for (TeamMate teamMate : teamMates) {
            if(Objects.equals(teamMate.getId(), teamMateId)){
                return teamMate;
            }
        }
        return null;
    }

    private void toggleTeamLocation() {
        teamLocationEnabled = !teamLocationEnabled;
        updateToggleButton(binding.viewTeamLocation, teamLocationEnabled);
        if(teamLocationEnabled){
            obtainViewModel().getTeamMateData(true);
            teamLocationHandler.removeCallbacks(teamLocationRunnable);
            teamLocationHandler.postDelayed(teamLocationRunnable, TEAM_LOCATION_REFRESH_TIME);
        }else{
            teamLocationHandler.removeCallbacks(teamLocationRunnable);
            obtainViewModel().teamMateList.postValue(new ArrayList<>());
            obtainViewModel().aMap.clear();
            updateMarkers();
        }
    }

    private void toggleDistanceMeasure() {
        if (currentMeasureMode == MEASURE_MODE_DISTANCE) {
            currentMeasureMode = MEASURE_MODE_NONE;
            clearDistanceMeasurement();
        } else {
            currentMeasureMode = MEASURE_MODE_DISTANCE;
            clearAreaMeasurement();
            clearDistanceMeasurement();
        }
        updateMeasureButtonState();
    }

    private void toggleAreaMeasure() {
        if (currentMeasureMode == MEASURE_MODE_AREA) {
            currentMeasureMode = MEASURE_MODE_NONE;
            clearAreaMeasurement();
        } else {
            currentMeasureMode = MEASURE_MODE_AREA;
            clearDistanceMeasurement();
            clearAreaMeasurement();
        }
        updateMeasureButtonState();
    }

    private void updateMeasureButtonState() {
        updateMeasureButton(binding.viewMeasureDistance, currentMeasureMode == MEASURE_MODE_DISTANCE);
        updateMeasureButton(binding.viewMeasureArea, currentMeasureMode == MEASURE_MODE_AREA);
    }

    private void updateMeasureButton(View button, boolean selected) {
        if (button == binding.viewMeasureDistance) {
            binding.distance.setImageResource(selected ? R.drawable.measure_distance : R.drawable.measure_distance_un);
            binding.distanceText.setTextColor(requireActivity().getResources().getColor(selected ? R.color.theme_color_blue : R.color.black90));
            return;
        }
        if (button == binding.viewMeasureArea) {
            binding.area.setImageResource(selected ? R.drawable.measure_area : R.drawable.measure_area_un);
            binding.areaText.setTextColor(requireActivity().getResources().getColor(selected ? R.color.theme_color_blue : R.color.black90));
            return;
        }
        updateToggleButton(button, selected);
    }

    private void updateToggleButton(View button, boolean selected) {
        if (button == binding.viewTeamLocation) {
            binding.teamLocationIcon.setImageResource(selected ? R.drawable.marker_friend : R.drawable.marker_friend_un);
            binding.teamLocationText.setTextColor(requireActivity().getResources().getColor(selected ? R.color.theme_color_blue : R.color.black90));
            return;
        }
        button.setBackgroundResource(selected ? R.drawable.blue_conner : R.drawable.white_conner);
        updateMeasureButtonTextColor(button, selected ? Color.WHITE : Color.BLACK);
    }

    private void updateMeasureButtonTextColor(View view, int color) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                updateMeasureButtonTextColor(viewGroup.getChildAt(i), color);
            }
        }
    }

    private boolean handleMeasureMapClick(LatLng latLng) {
        if (currentMeasureMode == MEASURE_MODE_DISTANCE) {
            if (distancePoints.size() == 2) {
                distancePoints.clear();
            }
            distancePoints.add(latLng);
            renderDistanceMeasurement();
            return true;
        }
        if (currentMeasureMode == MEASURE_MODE_AREA) {
            areaPoints.add(latLng);
            renderAreaMeasurement();
            return true;
        }
        return false;
    }

    private void redrawMeasureOverlays() {
        if (!distancePoints.isEmpty()) {
            renderDistanceMeasurement();
        }
        if (!areaPoints.isEmpty()) {
            renderAreaMeasurement();
        }
        updateMeasureButtonState();
    }

    private void clearDistanceMeasurement() {
        distancePoints.clear();
        clearDistanceRenderObjects();
    }

    private void clearDistanceRenderObjects() {
        for (Marker marker : distanceMarkers) {
            marker.remove();
        }
        distanceMarkers.clear();
        for (Polyline polyline : distancePolylines) {
            polyline.remove();
        }
        distancePolylines.clear();
        for (Text text : distanceTexts) {
            text.remove();
        }
        distanceTexts.clear();
    }

    private void renderDistanceMeasurement() {
        clearDistanceRenderObjects();
        for (LatLng point : distancePoints) {
            distanceMarkers.add(addMeasureMarker(point));
        }
        if (distancePoints.size() < 2) {
            return;
        }
        LatLng startPoint = distancePoints.get(0);
        LatLng endPoint = distancePoints.get(1);
        distancePolylines.add(obtainViewModel().aMap.addPolyline(new PolylineOptions()
                .add(startPoint, endPoint)
                .width(8)
                .color(Color.parseColor("#FF3875C5"))));
        float distance = AMapUtils.calculateLineDistance(startPoint, endPoint);
        distanceTexts.add(obtainViewModel().aMap.addText(new TextOptions()
                .position(getMidPoint(startPoint, endPoint))
                .text(formatDistance(distance))
                .fontColor(Color.WHITE)
                .backgroundColor(Color.parseColor("#00000000"))
                .fontSize(32)));
    }

    private void clearAreaMeasurement() {
        areaPoints.clear();
        clearAreaRenderObjects();
    }

    private void clearAreaRenderObjects() {
        for (Marker marker : areaMarkers) {
            marker.remove();
        }
        areaMarkers.clear();
        if (areaPolyline != null) {
            areaPolyline.remove();
            areaPolyline = null;
        }
        if (areaPolygon != null) {
            areaPolygon.remove();
            areaPolygon = null;
        }
        if (areaText != null) {
            areaText.remove();
            areaText = null;
        }
    }

    private void renderAreaMeasurement() {
        clearAreaRenderObjects();
        for (LatLng point : areaPoints) {
            areaMarkers.add(addMeasureMarker(point));
        }
        if (areaPoints.size() == 2) {
            areaPolyline = obtainViewModel().aMap.addPolyline(new PolylineOptions()
                    .addAll(areaPoints)
                    .width(8)
                    .color(Color.parseColor("#FFF28F25")));
            return;
        }
        if (areaPoints.size() < 3) {
            return;
        }
        areaPolygon = obtainViewModel().aMap.addPolygon(new PolygonOptions()
                .addAll(areaPoints)
                .strokeWidth(8)
                .strokeColor(Color.parseColor("#FFF28F25"))
                .fillColor(Color.parseColor("#66F28F25")));
        areaText = obtainViewModel().aMap.addText(new TextOptions()
                .position(getAreaLabelPoint(areaPoints))
                .text(formatArea(calculateArea(areaPoints)))
                .fontColor(Color.WHITE)
                .backgroundColor(Color.parseColor("#00000000"))
                .fontSize(32));
    }

    private Marker addMeasureMarker(LatLng point) {
        Marker marker = obtainViewModel().aMap.addMarker(new MarkerOptions()
                .position(point)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Bundle bundle = new Bundle();
        bundle.putString("id", "measure");
        bundle.putInt("type", MAP_OVERLAY_TYPE_MEASURE);
        marker.setObject(bundle);
        return marker;
    }

    private LatLng getMidPoint(LatLng startPoint, LatLng endPoint) {
        return new LatLng((startPoint.latitude + endPoint.latitude) / 2d, (startPoint.longitude + endPoint.longitude) / 2d);
    }

    private LatLng getAreaLabelPoint(List<LatLng> points) {
        double lat = 0;
        double lng = 0;
        for (LatLng point : points) {
            lat += point.latitude;
            lng += point.longitude;
        }
        return new LatLng(lat / points.size(), lng / points.size());
    }

    private String formatDistance(float distance) {
        if (distance > 100f) {
            return String.format(Locale.getDefault(), "%.0f米", distance);
        }
        return String.format(Locale.getDefault(), "%.1f米", distance);
    }

    private String formatArea(double area) {
        if (area > 100d) {
            return String.format(Locale.getDefault(), "%.0f平方米", area);
        }
        return String.format(Locale.getDefault(), "%.1f平方米", area);
    }

    private double calculateArea(List<LatLng> points) {
        if (points.size() < 3) {
            return 0d;
        }
        double avgLat = 0d;
        for (LatLng point : points) {
            avgLat += Math.toRadians(point.latitude);
        }
        avgLat /= points.size();
        double radius = 6378137d;
        double area = 0d;
        for (int i = 0; i < points.size(); i++) {
            LatLng current = points.get(i);
            LatLng next = points.get((i + 1) % points.size());
            double currentX = Math.toRadians(current.longitude) * radius * Math.cos(avgLat);
            double currentY = Math.toRadians(current.latitude) * radius;
            double nextX = Math.toRadians(next.longitude) * radius * Math.cos(avgLat);
            double nextY = Math.toRadians(next.latitude) * radius;
            area += currentX * nextY - nextX * currentY;
        }
        return Math.abs(area) / 2d;
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
    private void initTeamMateDetailDialog() {
        teamMateDetailDialog = new TeamMateDetailDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = teamMateDetailDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
//        lp.height = (int) (height * 0.22);
        dialogWindow.setAttributes(lp);
        teamMateDetailDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            teamMateDetailDialog.create();
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


        obtainViewModel().aMap.clear();
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
