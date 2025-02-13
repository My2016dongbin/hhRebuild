package com.haohai.platform.fireforestplatform.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MapFragment extends BaseFragment<FgMap, FgMapViewModel> implements OneBodyListDialog.OneBodyDialogListener, SatelliteListDialog.SatelliteDialogListener, OneBodyDetailDialog.OneBodyDetailDialogListener, SatelliteDetailDialog.SatelliteDetailDialogListener, ResourceDetailDialog.ResourceDetailDialogListener, ResourceListDialog.ResourceDialogListener, SatelliteSearchDialog.SatelliteSearchDialogListener, SatelliteSearchAdvancedDialog.SatelliteSearchAdvancedDialogListener, SheQuListDialog.SheQuDialogListener {

    private final String TAG = MapFragment.class.getSimpleName();
    private BaiduMap mBaiduMap;
    private OneBodyListDialog oneBodyListDialog;
    private SatelliteListDialog satelliteListDialog;
    private OneBodyDetailDialog oneBodyDetailDialog;
    private SatelliteDetailDialog satelliteDetailDialog;
    private SatelliteSearchAdvancedDialog satelliteSearchAdvancedDialog;
    private SatelliteSearchDialog satelliteSearchDialog;
    private ResourceListDialog resourceListDialog;
    private SheQuListDialog sheQuListDialog;
    private ResourceDetailDialog resourceDetailDialog;

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
                    double[] doubles = LatLngChangeNew.calWGS84toBD09(event.getLat(), event.getLng());
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

        //baiduMap
        mBaiduMap = binding.baiduMapview.getMap();
        //显示卫星图层
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setMyLocationEnabled(true);
        binding.baiduMapview.showZoomControls(false);

        //只显示道路 不显示其他标注
        mBaiduMap.showMapPoi(true);
        //设置最大最小缩放等级
        mBaiduMap.setMaxAndMinZoomLevel(18, 5);
        flyBaiduMapZoom(CommonData.lat, CommonData.lng, 14);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                closeInput(binding.editFind);
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                return ;
            }
        });
        mBaiduMap.setOnMarkerClickListener(marker -> {
            Bundle extraInfo = marker.getExtraInfo();
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
        //初始化离线地图
        MKOfflineMap mOffline = new MKOfflineMap();
        // 传入MKOfflineMapListener，离线地图状态发生改变时会触发该回调
        mOffline.init(new MKOfflineMapListener() {
            @Override
            public void onGetOfflineMapState(int i, int i1) {
                Log.e("MapFg", "onGetOfflineMapState: Downloading " + i + "," + i1);
            }
        });
        int cityId = 0;
        ArrayList<MKOLSearchRecord> records = mOffline.searchCity("青岛市");
        if (records != null && records.size() == 1) {
            cityId = records.get(0).cityID;
        }
        ArrayList<MKOLUpdateElement> updateInfo = mOffline.getAllUpdateInfo();
        if (updateInfo != null && updateInfo.size() > 0) {
            MKOLUpdateElement updateElement = updateInfo.get(0);
            if (updateElement.update) {
                //更新下载
                mOffline.update(cityId);
                Log.e("MapFg", "init_: update");
            } else {
                Log.e("MapFg", "init_: do nothing");
            }
        } else {
            // 开始下载离线地图
            // cityID 城市的数字标识
            mOffline.start(cityId);
            Log.e("MapFg", "init_: download");
        }

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
        com.baidu.mapapi.model.LatLng ll = new com.baidu.mapapi.model.LatLng(
                lat, lng);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(zoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时必须调用mMapView. onResume ()
        binding.baiduMapview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.baiduMapview.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBaiduMap.clear();
        binding.baiduMapview.onDestroy();
        EventBus.getDefault().unregister(this);
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
        mBaiduMap.clear();
        //更新Dialog列表数据
        oneBodyListDialog.setOneBodyFireList(oneBodyFires,obtainViewModel().currentPage);
        //更新所有Marker
        updateMarkers();
        //跳转第一火点
        try{
            OneBodyFire oneBody = oneBodyFires.get(0);
            double[] doubles = LatLngChangeNew.calWGS84toBD09(Double.parseDouble(oneBody.getAlarmLatitude()), Double.parseDouble(oneBody.getAlarmLongitude()));
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
    }

    private void satelliteFireChanged(List<SatelliteFire> satelliteFires) {
        mBaiduMap.clear();
        //更新Dialog列表数据
        satelliteListDialog.setSatelliteFireList(satelliteFires);
        //更新所有Marker
        updateMarkers();
        //跳转第一火点
        try{
            SatelliteFire satellite = satelliteFires.get(0);
            double[] doubles = LatLngChangeNew.calWGS84toBD09(Double.parseDouble(satellite.getLatitude()), Double.parseDouble(satellite.getLongitude()));
            flyBaiduMapZoom(doubles[0],doubles[1], 14);
        }catch (Exception e){
            Log.e(TAG, "oneBodyFireChanged: " + e.getMessage() );
        }
    }
    private void resourceChanged(List<Resource> resources) {
        mBaiduMap.clear();
        //更新所有Marker
        updateMarkers();
        //跳转第一火点
        long now = new Date().getTime();
        if(now - CommonData.longAdding < 5000){
            //新添加了资源
            try{
                double[] doubles = LatLngChangeNew.calWGS84toBD09(CommonData.latAdding, CommonData.lngAdding);
                flyBaiduMapZoom(doubles[0],doubles[1], 14);
            }catch (Exception e){
                Log.e(TAG, "resourceChanged: " + e.getMessage() );
            }
        }else{
            //跳转第一个资源点
            try{
                Resource resource = resources.get(0);
                double[] doubles = LatLngChangeNew.calWGS84toBD09(Double.parseDouble(resource.getPosition().getLat()), Double.parseDouble(resource.getPosition().getLng()));
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
        mBaiduMap.clear();
        updateMarkers();
    }
    private void resourceTypeChanged(List<ResourceType> resourceTypes) {
        //更新Dialog列表数据
        resourceListDialog.setResourceTypeList(resourceTypes);
    }

    private void oneBodyMarker(List<OneBodyFire> oneBodyFires) {
        List<OverlayOptions> options = new ArrayList<>();
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
                double[] doubles = LatLngChangeNew.calWGS84toBD09(Double.parseDouble(oneBodyFires.get(i).getAlarmLatitude()), Double.parseDouble(oneBodyFires.get(i).getAlarmLongitude()));
                com.baidu.mapapi.model.LatLng point = new com.baidu.mapapi.model.LatLng(doubles[0], doubles[1]);
                Bundle bundle = new Bundle();
                bundle.putString("id", oneBodyFires.get(i).getId());
                bundle.putInt("type", obtainViewModel().ONE_BODY);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .extraInfo(bundle)
                        .icon(btm);
                options.add(i, option);
            }catch (Exception e){
                Log.e(TAG, "oneBodyMarker: " + e.getMessage() );
                continue;
            }
        }
        mBaiduMap.addOverlays(options);
    }
    private void sheQuMarker(List<ArrayList<Double>> lines) {
        //多边形顶点位置
        List<LatLng> points = new ArrayList<>();
        for (ArrayList<Double> m:lines) {
            points.add(new LatLng(m.get(1), m.get(0)));
        }
        Log.e(TAG, "updateMarkers: enter -- " + lines );

        //构造PolygonOptions
        PolygonOptions mPolygonOptions = new PolygonOptions()
                .points(points)
                .fillColor(0xAAf28f25) //填充颜色
                .stroke(new Stroke(2, 0xAAf28f25)); //边框宽度和颜色

        //在地图上显示多边形
        mBaiduMap.addOverlay(mPolygonOptions);

        //跳转到第一个点
        LatLng latLng = points.get(0);
        flyBaiduMapZoom(latLng.latitude,latLng.longitude,16);
        //隐藏Dialog
        sheQuListDialog.hide();
    }
    private void satelliteMarker(List<SatelliteFire> satelliteFires) {
        List<OverlayOptions> options = new ArrayList<>();
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.ic_fire);
        for (int i = 0; i < satelliteFires.size(); i++) {
            try {
                double[] doubles = LatLngChangeNew.calWGS84toBD09(Double.parseDouble(satelliteFires.get(i).getLatitude()), Double.parseDouble(satelliteFires.get(i).getLongitude()));
                com.baidu.mapapi.model.LatLng point = new com.baidu.mapapi.model.LatLng(doubles[0], doubles[1]);
                Bundle bundle = new Bundle();
                bundle.putString("id", satelliteFires.get(i).getId());
                bundle.putInt("type", obtainViewModel().SATELLITE);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .extraInfo(bundle)
                        .icon(btm);
                options.add(i, option);
            }catch (Exception e){
                Log.e(TAG, "satelliteMarker: " + e.getMessage() );
                continue;
            }
        }
        mBaiduMap.addOverlays(options);
    }
    private void resourceMarker(List<Resource> resources) {
        List<OverlayOptions> options = new ArrayList<>();
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
                double[] doubles = LatLngChangeNew.calWGS84toBD09(Double.parseDouble(resources.get(i).getPosition().getLat()), Double.parseDouble(resources.get(i).getPosition().getLng()));
                com.baidu.mapapi.model.LatLng point = new com.baidu.mapapi.model.LatLng(doubles[0], doubles[1]);
                Bundle bundle = new Bundle();
                bundle.putString("id", resources.get(i).getId());
                bundle.putInt("type", obtainViewModel().RESOURCE);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .extraInfo(bundle)
                        .icon(btm);
                options.add(i, option);
            }catch (Exception e){
                Log.e(TAG, "satelliteMarker: " + e.getMessage() );
                continue;
            }
        }
        mBaiduMap.addOverlays(options);
    }

    private void userLocationMarker(){
        BitmapDescriptor btm = BitmapDescriptorFactory.fromResource(R.drawable.user);
        com.baidu.mapapi.model.LatLng point = new com.baidu.mapapi.model.LatLng(CommonData.lat, CommonData.lng);
        Bundle bundle = new Bundle();
        bundle.putString("id", "userLocation");
        bundle.putInt("type", obtainViewModel().USER_LOCATION);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .extraInfo(bundle)
                .icon(btm);
        mBaiduMap.addOverlay(option);
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


        mBaiduMap.clear();
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
