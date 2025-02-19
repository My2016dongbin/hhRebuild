package com.haohai.platform.fireforestplatform.old.linyi;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.maps.model.LatLng;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.NaviSetting;
import com.amap.api.navi.model.AMapNaviLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class ResourceSearchActivity extends HhBaseActivity implements INaviInfoCallback, ResourceTypeViewBinder.OnResourceTypeItemClick, ResourceViewBinder.OnResourceItemClick {

    private static final String TAG = ResourceSearchActivity.class.getSimpleName();
    private LinearLayout quLayout;
    private ImageView iv_city;
    private TextView quText;
    private LinearLayout qu_layout;
    private LinearLayout jiedaoLayout;
    private TextView jiedaoText;
    public List<Grid> quList;
    public List<String> quStrList;
    public List<Grid> jiedaoList;
    public List<String> jiedaoStrList;
    private WheelView areaWy;
    public int currentQuanxian = 0;   //0是全国权限  1是省级权限
    public int currentChooseArea = 0;  //当前在选择区还是街道   0选择区  1选择街道
    public String currentChooseQu = "";
    public String currentChooseJiedao = "";
    public String currentChooseType = "";
    public int quSelectIndex = 0;
    public int jieDaoSelectIndex = 0;
    public int typeSelectIndex = 0;
    public boolean isChooseQu = false;
    public String currentQuId;
    public String currentQuNo;
    public String currentStreeNo;
    private TextView searchView;
    private RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    public List<ResourceType> resourceTypeList;
    public List<Resource> resourceChooseList;
    private TextView daohangGuihuaButton;
    public ProgressDialog progressDialog;
    private ImageView backButton;
    private LinearLayout leixingLayout;
    private TextView leixingView;
    private List<ResourceTypeList> resourceTypeLists = new ArrayList<>();
    private List<Area> allAreaList;
    private List<Grid> gridList ;
    private List<TeamDTO> teamDTOList = new ArrayList<>();
    private List<CheckStationDTO> checkStationDTOList = new ArrayList<>();
    private List<MonitorDTO> monitorDTOList = new ArrayList<>();
    private List<WaterSourceDTO> waterSourceDTOList = new ArrayList<>();
    private List<WatchTowerDTO> watchTowerDTOList = new ArrayList<>();
    private List<FireCommandDTO> fireCommandDTOList = new ArrayList<>();
    private List<DangerSourceDTO> dangerSourceDTOList = new ArrayList<>();
    private List<CemeteryDTO> cemeteryDTOList =new ArrayList<>();
    private List<MaterialRepositoryDTO> materialRepositoryDTOList = new ArrayList<>();
    private List<HelicopterPointDTO> helicopterPointDTOList= new ArrayList<>();
    public List<String> typeStrList;
    private String access_token;
    private String resourceType;
    private List<Resource> resourceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主题色白色 黑字
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.whiteColor));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_resource_search2);

        quList = new ArrayList<>();
        quStrList = new ArrayList<>();
        jiedaoList = new ArrayList<>();
        jiedaoStrList = new ArrayList<>();
        resourceChooseList = new ArrayList<>();
        typeStrList = new ArrayList<>();
        resourceTypeList = new ArrayList<>();
        allAreaList=new ArrayList<>();
        gridList=new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        access_token = CommonData.token;
        initView();
        getAllQu();
        getTypeformService();
    }

    boolean cityRoot;
    private void initView() {
//        cityRoot = new DbConfig(ResourceSearchActivity.this).getUser().getJobTitle().contains("市");
        cityRoot = true;
        backButton = (ImageView) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listView = (RecyclerView) findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);
        register();
        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);

        daohangGuihuaButton = (TextView) findViewById(R.id.daohang_guihua_button);
        searchView = (TextView) findViewById(R.id.search_view);
        quLayout = (LinearLayout) findViewById(R.id.qu_layout);
        iv_city = (ImageView) findViewById(R.id.iv_city);
        quText = (TextView) findViewById(R.id.qu_text);
        qu_layout = (LinearLayout) findViewById(R.id.qu_layout);
        jiedaoLayout = (LinearLayout) findViewById(R.id.jiedao_layout);
        jiedaoText = (TextView) findViewById(R.id.jiedao_text);
        leixingLayout = (LinearLayout) findViewById(R.id.type_layout);
        leixingView = (TextView) findViewById(R.id.type_view);

        RxViewAction.clickNoDouble(leixingLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showTypeDialog(typeStrList);
                    }
                });

        RxViewAction.clickNoDouble(searchView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                            for (int i = 0; i < jiedaoList.size(); i++) {
                                if (jiedaoList.get(i).getName().equals(currentChooseJiedao)) {
                                    currentStreeNo = jiedaoList.get(i).getGridNo();
                                }
                            }
                            if(cityRoot){
                                if(quText.getText().toString().contains("请选择")){
                                    Toast.makeText(ResourceSearchActivity.this, "请选择区", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(leixingView.getText().toString().contains("类型")){
                                    Toast.makeText(ResourceSearchActivity.this,"请选择类型",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                getDataFromDb();
                            }else{
                                if(quText.getText().toString().contains("请选择")){
                                    Toast.makeText(ResourceSearchActivity.this, "请选择区", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if((!quText.getText().toString().contains("请选择")) && (currentStreeNo==null|| currentStreeNo.equals(""))){
                                    Toast.makeText(ResourceSearchActivity.this, "请选择街道", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(leixingView.getText().toString().contains("类型")){
                                    Toast.makeText(ResourceSearchActivity.this,"请选择类型",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                getDataFromDb();
                            }

                    }
                });

        RxViewAction.clickNoDouble(quLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 0;
                        showAreaDialog(quStrList);
                    }
                });
        RxViewAction.clickNoDouble(jiedaoLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 1;
                        /*for (int i = 0; i < quList.size(); i++) {
                            if (quList.get(i).getName().equals(currentChooseQu)) {
                                currentQuId = quList.get(i).getId();
                                currentQuNo = quList.get(i).getGridNo();
                            }
                        }*/
                        if (quText.getText().equals("请选择区")){
                            Toast.makeText(ResourceSearchActivity.this, "请先选择区", Toast.LENGTH_SHORT).show();

                        }else {

                            getAllJieDao(true);
                        }

                    }
                });


        RxViewAction.clickNoDouble(daohangGuihuaButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "call: dianle" );
                        showDialogProgress(progressDialog,"导航规划中...");
                        MapUtils mapUtils = new MapUtils();
                        String jingweiStr = getLocation();
                        String starWeidu = "";
                        String starJingdu = "";
                        if (!jingweiStr.isEmpty()){
                            List<String> jingweiList = Arrays.asList(jingweiStr.split(","));
                            starWeidu = jingweiList.get(1);
                            starJingdu = jingweiList.get(0);
                        }
                        resourceChooseList.clear();
                        for (int i = 0; i < resourceTypeList.size(); i++) {
                            List<Resource> resourceList = resourceTypeList.get(i).getResourceList();
                            for (int j = 0; j < resourceList.size(); j++) {
                                if (resourceList.get(j).isChoose) {
                                    double distance = mapUtils.GetDistance(Double.parseDouble(starWeidu), Double.parseDouble(starJingdu),resourceList.get(j).getLat(),resourceList.get(j).getLng()       );
                                    resourceList.get(j).setDistance(distance);
                                    resourceChooseList.add(resourceList.get(j));
                                }
                            }
                        }
                        if (resourceChooseList.size() == 0){
                            Toast.makeText(ResourceSearchActivity.this, "请选择资源点", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return;
                        }
                        Collections.sort(resourceChooseList, new Comparator<Resource>(){

                            @Override
                            public int compare(Resource o1, Resource o2) {
                                return (int) (o1.distance - o2.distance);
                            }
                        });

                        Poi start = new Poi("", new LatLng(Double.parseDouble(starWeidu),Double.parseDouble(starJingdu)), "");
                        Poi end = null;
                        List<Poi> poiList = new ArrayList();
/*
                        for (int i = 0; i < resourceChooseList.size(); i++) {

                            Log.e(TAG, "call: 距离--" + resourceChooseList.get(i).distance );
                            if (i == resourceChooseList.size() - 1){
                                //终点
                                com.skyline.terraexplorer.utils.LatLng latLng = new LatLngChange().transformFromWGSToGCJ(new com.skyline.terraexplorer.utils.LatLng(resourceChooseList.get(i).getLat(),resourceChooseList.get(i).getLng()));

                                end = new Poi(resourceChooseList.get(i).getName(),  new LatLng(latLng.latitude,latLng.longitude), "");
                            }else {
                                com.skyline.terraexplorer.utils.LatLng latLng = new LatLngChange().transformFromWGSToGCJ(new com.skyline.terraexplorer.utils.LatLng(resourceChooseList.get(i).getLat(),resourceChooseList.get(i).getLng()));

                                //途经点
                                poiList.add(new Poi(resourceChooseList.get(i).getName(), new LatLng(latLng.latitude,latLng.longitude), ""));

                            }
                        }
*/

                        //导航规划
                        if (resourceChooseList.size() ==1){ //只有一个点时 这个点时这个点是终点
                            //终点
                            LatLng latLng = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(0).getLat(),resourceChooseList.get(0).getLng()));
                            end = new Poi(resourceChooseList.get(0).getName(),  new LatLng(latLng.latitude,latLng.longitude), "");
                        }else if (resourceChooseList.size() == 2){  //有两个点时 第一个点是途径点  第二个点是重点
                            //途经点
                            LatLng latLng = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(0).getLat(),resourceChooseList.get(0).getLng()));
                            poiList.add(new Poi(resourceChooseList.get(0).getName(), new LatLng(latLng.latitude,latLng.longitude), ""));
                            //终点
                            LatLng latLng1 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng()));
                            end = new Poi(resourceChooseList.get(1).getName(),  new LatLng(latLng1.latitude,latLng1.longitude), "");

                        }else if (resourceChooseList.size() == 3){  //有三个点时  第一个点时途径点  判断第二个点跟第三个点距离第一个近的时途径点
                            //途经点1
                            LatLng latLng = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(0).getLat(),resourceChooseList.get(0).getLng()));
                            poiList.add(new Poi(resourceChooseList.get(0).getName(), new LatLng(latLng.latitude,latLng.longitude), ""));
                            double distance2 = mapUtils.GetDistance(resourceChooseList.get(0).getLat(),resourceChooseList.get(0).getLng(),resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng());
                            double distance3 = mapUtils.GetDistance(resourceChooseList.get(0).getLat(),resourceChooseList.get(0).getLng(),resourceChooseList.get(2).getLat(),resourceChooseList.get(2).getLng());

                            if (distance2 > distance3){     //如果第二个点比第三个点远  那么第三个点是途径点  第二个点是重点
                                //途经点2
                                LatLng latLng2 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(2).getLat(),resourceChooseList.get(2).getLng()));
                                poiList.add(new Poi(resourceChooseList.get(2).getName(), new LatLng(latLng2.latitude,latLng2.longitude), ""));
                                //终点
                                LatLng latLng1 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng()));
                                end = new Poi(resourceChooseList.get(1).getName(),  new LatLng(latLng1.latitude,latLng1.longitude), "");
                            }else {
                                //途经点2
                                LatLng latLng2 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng()));
                                poiList.add(new Poi(resourceChooseList.get(1).getName(), new LatLng(latLng2.latitude,latLng2.longitude), ""));
                                //终点
                                LatLng latLng1 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(2).getLat(),resourceChooseList.get(2).getLng()));
                                end = new Poi(resourceChooseList.get(2).getName(),  new LatLng(latLng1.latitude,latLng1.longitude), "");
                            }
                        }else if (resourceChooseList.size() == 4){
                            //途经点1
                            LatLng latLng = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(0).getLat(),resourceChooseList.get(0).getLng()));
                            poiList.add(new Poi(resourceChooseList.get(0).getName(), new LatLng(latLng.latitude,latLng.longitude), ""));
                            double distance2 = mapUtils.GetDistance(resourceChooseList.get(0).getLat(),resourceChooseList.get(0).getLng(),resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng());
                            double distance3 = mapUtils.GetDistance(resourceChooseList.get(0).getLat(),resourceChooseList.get(0).getLng(),resourceChooseList.get(2).getLat(),resourceChooseList.get(2).getLng());
                            double distance4 = mapUtils.GetDistance(resourceChooseList.get(0).getLat(),resourceChooseList.get(0).getLng(),resourceChooseList.get(3).getLat(),resourceChooseList.get(3).getLng());
                            double min = distance2 < distance3 ? distance2:distance3;
                            min = min < distance4 ? min : distance4;
                            if (min == distance2){
                                //途经点2
                                LatLng latLng1 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng()));
                                poiList.add(new Poi(resourceChooseList.get(1).getName(), new LatLng(latLng1.latitude,latLng1.longitude), ""));
                                if (distance3 > distance4){     //4是途径点 3是终点
                                    //途经点3
                                    LatLng latLng2 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(3).getLat(),resourceChooseList.get(3).getLng()));
                                    poiList.add(new Poi(resourceChooseList.get(3).getName(), new LatLng(latLng2.latitude,latLng2.longitude), ""));
                                    //终点
                                    LatLng latLng3 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(2).getLat(),resourceChooseList.get(2).getLng()));
                                    end = new Poi(resourceChooseList.get(2).getName(),  new LatLng(latLng3.latitude,latLng3.longitude), "");
                                }else {  //4是终点 3是途径点
                                    //途经点3
                                    LatLng latLng2 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(2).getLat(),resourceChooseList.get(2).getLng()));
                                    poiList.add(new Poi(resourceChooseList.get(2).getName(), new LatLng(latLng2.latitude,latLng2.longitude), ""));
                                    //终点
                                    LatLng latLng3 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(3).getLat(),resourceChooseList.get(3).getLng()));
                                    end = new Poi(resourceChooseList.get(3).getName(),  new LatLng(latLng3.latitude,latLng3.longitude), "");
                                }
                            }else if (min == distance3){
                                //途经点2
                                LatLng latLng1 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(2).getLat(),resourceChooseList.get(2).getLng()));
                                poiList.add(new Poi(resourceChooseList.get(2).getName(), new LatLng(latLng1.latitude,latLng1.longitude), ""));
                                if (distance2 > distance4){     //4是途径点 2是终点
                                    //途经点3
                                    LatLng latLng2 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(3).getLat(),resourceChooseList.get(3).getLng()));
                                    poiList.add(new Poi(resourceChooseList.get(3).getName(), new LatLng(latLng2.latitude,latLng2.longitude), ""));
                                    //终点
                                    LatLng latLng3 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng()));
                                    end = new Poi(resourceChooseList.get(1).getName(),  new LatLng(latLng3.latitude,latLng3.longitude), "");
                                }else {  //4是终点 3是途径点
                                    //途经点3
                                    LatLng latLng2 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng()));
                                    poiList.add(new Poi(resourceChooseList.get(1).getName(), new LatLng(latLng2.latitude,latLng2.longitude), ""));
                                    //终点
                                    LatLng latLng3 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(3).getLat(),resourceChooseList.get(3).getLng()));
                                    end = new Poi(resourceChooseList.get(3).getName(),  new LatLng(latLng3.latitude,latLng3.longitude), "");
                                }
                            }else if (min == distance4){
                                //途经点2
                                LatLng latLng1 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(3).getLat(),resourceChooseList.get(3).getLng()));
                                poiList.add(new Poi(resourceChooseList.get(3).getName(), new LatLng(latLng1.latitude,latLng1.longitude), ""));
                                if (distance2 > distance3){     //3是途径点 2是终点
                                    //途经点3
                                    LatLng latLng2 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(2).getLat(),resourceChooseList.get(2).getLng()));
                                    poiList.add(new Poi(resourceChooseList.get(2).getName(), new LatLng(latLng2.latitude,latLng2.longitude), ""));
                                    //终点
                                    LatLng latLng3 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng()));
                                    end = new Poi(resourceChooseList.get(1).getName(),  new LatLng(latLng3.latitude,latLng3.longitude), "");
                                }else {  //4是终点 3是途径点
                                    //途经点3
                                    LatLng latLng2 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(1).getLat(),resourceChooseList.get(1).getLng()));
                                    poiList.add(new Poi(resourceChooseList.get(1).getName(), new LatLng(latLng2.latitude,latLng2.longitude), ""));
                                    //终点
                                    LatLng latLng3 = new LatLngChange().transformFromWGSToGCJ(new LatLng(resourceChooseList.get(2).getLat(),resourceChooseList.get(2).getLng()));
                                    end = new Poi(resourceChooseList.get(2).getName(),  new LatLng(latLng3.latitude,latLng3.longitude), "");
                                }
                            }
                        }

                        NaviSetting.updatePrivacyShow(ResourceSearchActivity.this, true, true);
                        NaviSetting.updatePrivacyAgree(ResourceSearchActivity.this, true);
                        AmapNaviParams params = new AmapNaviParams(start, poiList, end, AmapNaviType.DRIVER);
                        params.setUseInnerVoice(true);
                        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, ResourceSearchActivity.this);
                         progressDialog.dismiss();

                    }
                });


    }

    private void showTypeDialog(List<String> typeStrList) {
        View areaView = LayoutInflater.from(this).inflate(R.layout.dialog_area, null);
        areaWy = ((WheelView) areaView.findViewById(R.id.wheel_view_area));
        areaWy.setIsLoop(false);

        areaWy.setItems(typeStrList, typeSelectIndex);//init selected position is 0 初始选中位置为0


        areaWy.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                    currentChooseType = areaWy.getSelectedItem();
                    typeSelectIndex = areaWy.getSelectedPosition();
                    leixingView.setText(currentChooseType);
                    switch (currentChooseType){
                        case "护林检查站":
                            resourceType="checkStation";
                            break;
                        case "直升机机降点":
                            resourceType = "helicopterPoint";
                            break;
                        case "队伍驻防点":
                            resourceType = "team";
                            break;
                        case "危险源":
                            resourceType = "dangerSource";
                            break;
                        case "物资储备库":
                            resourceType = "materialRepository";
                            break;
                        case "现有水源地":
                            resourceType = "waterSource";
                            break;
                        case "改造水源地":
                            resourceType = "fireCommand";
                            break;
                        case "新建水源地":
                            resourceType = "watchTower";
                            break;
                        case "墓地":
                            resourceType = "cemetery";
                            break;
                        case "瞭望塔":
                            resourceType = "watchTower";
                            break;
                        case "视频监控点":
                            resourceType = "monitor";
                            break;
                        case "森林防火监测中心":
                            resourceType = "fireCommand";
                            break;
                    }
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请选择资源类型")
                .setView(areaView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String areStr = "";
                        String area = areaWy.getSelectedItem();
                    }
                })
                .show();
    }
    private void getTypeformService() {
        JSONObject jsonObject =new JSONObject();
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/resourceList/list");
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "resource: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --1-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        Log.e(TAG, "onSuccess: data = bingo " + data );
                        Gson gson = new Gson();
                        resourceTypeLists.clear();
                        resourceTypeLists = gson.fromJson(String.valueOf(data), new TypeToken<List<ResourceTypeList>>() {
                        }.getType());
                        typeStrList.add("类型");
                        for (int i = 0; i < resourceTypeLists.size(); i++) {
                            if (resourceTypeLists.get(i).getIsDisplay().equals("1")) {
                                typeStrList.add(resourceTypeLists.get(i).getName());
                                Log.e(TAG, "onSuccess: resourceTypeLists.get(i) = " + resourceTypeLists.get(i).toString() );
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    /**
     * 获取护林员检查站
     */
    private void initCheckStationIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();
        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/checkStation/list");
        params.setConnectTimeout(200000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: --" +  jsonObject.toString());
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess3------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        checkStationDTOList.clear();
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String description = object.optString("description");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String picture = object.optString("picture");
                                    String state = object.optString("state");

                                    String peopleCount = object.optString("peopleCount");
                                    String extinguisherCount = object.optString("extinguisherCount");
                                    String sawCount = object.optString("sawCount");
                                    String truckCount = object.optString("truckCount");
                                    String dataSnapshot = object.optString("dataSnapshot");
                                    String isAllday = object.optString("isAllday");
                                    String mountain = object.optString("mountain");
                                    String peopleName = object.optString("peopleName");
                                    String waterPistolCount = object.optString("waterPistolCount");
                                    String twoToolCount = object.optString("twoToolCount");
                                    String otherToolCount = object.optString("otherToolCount");
                                    String hasMonitor = object.optString("hasMonitor");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "checkStation", "/api/checkStation", gridId,
                                            gridName, gridNo, "护林检查站", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("护林检查站", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError:checkStation 请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 直升机机降点
     */
    private void initHelicopterPointIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/helicopterPoint/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess11----- " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        helicopterPointDTOList.clear();
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String address = object.optString("address");
                                    String createTime = object.optString("createTime");
                                    String createUser = object.optString("createUser");
                                    String description = object.optString("description");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String gridId = object.optString("gridId");
                                    String gridName = object.optString("gridName");
                                    String gridNo = object.optString("gridNo");
                                    String type = object.optString("type");
                                    String groupId = object.optString("groupId");
                                    String iconFile = object.optString("iconFile");
                                    String id = object.optString("id");
                                    String leaderId = object.optString("leaderId");
                                    String leaderName = object.optString("leaderName");

                                    String leaderPhone = object.optString("leaderPhone");
                                    String name = object.optString("name");
                                    String picture = object.optString("picture");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");
                                    String textColor = object.optString("textColor");
                                    String updateTime = object.optString("updateTime");
                                    String updateUser = object.optString("updateUser");
                                    String waterSource = object.optString("waterSource");
                                    String resourceType = object.optString("resourceType");
                                    String state = object.optString("state");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "helicopterPoint", "/api/helicopterPoint", gridId,
                                            gridName, gridNo, "直升机机降点", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                            resourceTypeList.add(new ResourceType("直升机机降点", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: materialRepository请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 物资库
     */
    private void initMaterialRepositoryIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/materialRepository/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess10------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        materialRepositoryDTOList.clear();
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String type = object.optString("type");
                                    String name = object.optString("name");
                                    Log.e(TAG, "onSuccess: wuzi" + name);
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    int fireEquipmentCount = object.optInt("fireEquipmentCount");
                                    int safeEquipmentCount = object.optInt("safeEquipmentCount");
                                    int outdoorEquipmentCount = object.optInt("outdoorEquipmentCount");
                                    int communicateEquipmentCount = object.optInt("communicateEquipmentCount");
                                    int carCount = object.optInt("carCount");
                                    int machineCount = object.optInt("machineCount");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");
                                    String state = object.optString("state");

                                    String windFireCount = object.optString("windFireCount");
                                    String sprayFireCount = object.optString("sprayFireCount");
                                    String waterPumpCount = object.optString("waterPumpCount");
                                    String twoToolCount = object.optString("twoToolCount");
                                    String waterPistolCount = object.optString("waterPistolCount");
                                    String chainSawCount = object.optString("chainSawCount");
                                    String bushCutterCount = object.optString("bushCutterCount");
                                    String fireCutterCount = object.optString("fireCutterCount");
                                    String fireproofClothesCount = object.optString("fireproofClothesCount");
                                    String glovesCount = object.optString("glovesCount");
                                    String helmetCount = object.optString("helmetCount");
                                    String shoesCount = object.optString("shoesCount");
                                    String waterBagCount = object.optString("waterBagCount");
                                    String waterSacCount = object.optString("waterSacCount");
                                    String oilDrumCount = object.optString("oilDrumCount");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");
                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "materialRepository", "/api/materialRepository", gridId,
                                            gridName, gridNo, "物资储备库", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("物资储备库", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: materialRepository请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 墓地数据
     */
    private void initCemeteryIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/cemetery/list");

        params.setConnectTimeout(200000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess9--- " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        cemeteryDTOList.clear();
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    Log.e(TAG, "onSuccess: 1111");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");

                                    String graveCount = object.optString("graveCount");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");
                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), "", "cemetery", "/api/cemetery", gridId,
                                            gridName, gridNo, "墓地", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("墓地", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: cemetery请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 重大危险源数据
     */
    private void initDangerSourceIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/dangerSource/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess8------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        dangerSourceDTOList.clear();
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");

                                    String isMajorHazard = object.optString("isMajorHazard");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");
                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "dangerSource", "/api/dangerSource", gridId,
                                            gridName, gridNo, "危险源", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("危险源", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError:dangerSource 请求失败" + ex.toString());

                if (ex.toString().contains("timeout")){
                    initDangerSourceIntoDb();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 指挥部数据
     */
    private void initFireCommandIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();
        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/fireCommand/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess7------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        fireCommandDTOList.clear();
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "fireCommand", "/api/fireCommand", gridId,
                                            gridName, gridNo, "森林防火监测中心", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("森林防火监测中心", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError:fireCommand 请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 瞭望塔数据
     */
    private void initWatchTowerIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/watchTower/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess6------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        watchTowerDTOList.clear();
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");

                                    String watchRange = object.optString("watchRange");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), "", "watchTower", "/api/watchTower", gridId,
                                            gridName, gridNo, "瞭望塔", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("瞭望塔", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: watchTower请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 水源地数据 现有
     */
    private void initWaterSourceIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/waterSource/list");
        params.setConnectTimeout(200000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess5------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String waterCapacity = object.optString("waterCapacity");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    String isHelicopterWater = object.optString("isHelicopterWater");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "waterSource", "/api/waterSource", gridId,
                                            gridName, gridNo, "现有水源地", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("现有水源地", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: waterSource请求失败" + ex.toString());

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 水源地数据 改造
     */
    private void initWaterSourceUpdateIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/fireCommand/list");
        params.setConnectTimeout(200000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess5------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String waterCapacity = object.optString("waterCapacity");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    String isHelicopterWater = object.optString("isHelicopterWater");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "fireCommand", "/api/fireCommand", gridId,
                                            gridName, gridNo, "改造水源地", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("改造水源地", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: waterSource请求失败" + ex.toString());

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 水源地数据 新建
     */
    private void initWaterSourceNewIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/watchTower/list");
        params.setConnectTimeout(200000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess5------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String waterCapacity = object.optString("waterCapacity");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    String isHelicopterWater = object.optString("isHelicopterWater");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "watchTower", "/api/watchTower", gridId,
                                            gridName, gridNo, "新建水源地", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("新建水源地", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: waterSource请求失败" + ex.toString());

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 视频监控点数据
     */
    private void initMonitorIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/monitor/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        Log.e(TAG, "initResourceIntoDb: jsonObject.toString() " + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess4------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        monitorDTOList.clear();
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String monitorNo = object.optString("monitorNo");
                                    String isOnline = object.optString("isOnline");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String monitorArea = object.optString("monitorArea");
                                    String placeName = object.optString("placeName");
                                    String sweepArea = object.optString("sweepArea");
                                    String altitude = object.optString("altitude");
                                    String towerHeight = object.optString("towerHeight");
                                    String northCorrection = object.optString("northCorrection");
                                    String horizontalCorrection = object.optString("horizontalCorrection");
                                    String visualRange = object.optString("visualRange");
                                    String visualTime = object.optString("visualTime");
                                    String horizontalAngle = object.optString("horizontalAngle");
                                    String verticalAngle = object.optString("verticalAngle");
                                    String remark = object.optString("remark");
                                    String state = object.optString("state");

                                    String monitorRange = object.optString("monitorRange");
                                    String isNetworking = object.optString("isNetworking");
                                    String isIntelligentEntry = object.optString("isIntelligentEntry");
                                    String description = object.optString("description");
                                    String picture = object.optString("description");


                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");
                                    // String otherPic = " ";

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), "", "camera", "/api/camera", gridId,
                                            gridName, gridNo, "视频监控点", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("视频监控点", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: monitor请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 下载专业队伍资源点
     */
    private void initTeamDTOIntoDb() {
        items.clear();
        adapter.notifyDataSetChanged();
        Log.e(TAG, "bingo: " );
        JSONObject jsonObject = new JSONObject();

        try {
            if(cityRoot){
                if(jiedaoText.getText().toString().contains("请选择")){
                    jsonObject.put("districtNo",currentQuNo==null?"":currentQuNo);
                }else{
                    jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
                }
            }else{
                jsonObject.put("gridNo",currentStreeNo==null?"":currentStreeNo);
            }
        } catch (JSONException e) {
            Log.e(TAG, "bingo e = : " + e.toString());
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/team/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "bingo initTeamDTOIntoDb: jsonObject.toString() = " + jsonObject.toString() );
        params.addHeader("Authorization","bearer " + access_token);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess2------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        teamDTOList.clear();
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String type = object.optString("type");
                                    String peopleNumber = object.optString("peopleNumber");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");

                                    String teamCount = object.optString("teamCount");
                                    String phone = object.optString("phone");
                                    String truckCount = object.optString("truckCount");
                                    String troopCarrierCount = object.optString("troopCarrierCount");
                                    String commandCarCount = object.optString("commandCarCount");
                                    String waterPumpCount = object.optString("waterPumpCount");
                                    String windFireCount = object.optString("windFireCount");
                                    String twoToolCount = object.optString("twoToolCount");
                                    String waterPistolCount = object.optString("waterPistolCount");
                                    String intercomCount = object.optString("intercomCount");
                                    String equipmentTruckCount = object.optString("equipmentTruckCount");
                                    String barracksMeasure = object.optString("barracksMeasure");
                                    String waterCarCount = object.optString("waterCarCount");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "team", "/api/team", gridId,
                                            gridName, gridNo, "队伍驻防点", groupId);
                                    resource.setDistrictNo(districtNo);
                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                            resourceTypeList.add(new ResourceType("队伍驻防点", resourceList));
                        }else {
                            resourceTypeList.clear();
                        }
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: team请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getDataFromDb() {
        resourceList.clear();

        if (quText.getText().toString().equals("请选择区")){
            Toast.makeText(this, "请选择区", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((!cityRoot) && jiedaoText.getText().toString().equals("请选择街道")){
            Toast.makeText(this, "请选择街道", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (leixingView.getText().toString()){
            case "护林检查站":
                initCheckStationIntoDb();
                break;
            case "直升机机降点":
                initHelicopterPointIntoDb();
                break;
            case "物资储备库":
                initMaterialRepositoryIntoDb();
                break;
            case "墓地":
                initCemeteryIntoDb();
                break;
            case "危险源":
                initDangerSourceIntoDb();
                break;
            case "防火指挥部":
                initFireCommandIntoDb();
                break;
            case "规划建设水源地":
                initWatchTowerIntoDb();
                break;
            case "现有水源地":
                initWaterSourceIntoDb();
                break;
            case "改造水源地":
                initWaterSourceUpdateIntoDb();
                break;
            case "新建水源地":
                initWaterSourceNewIntoDb();
                break;
            case "视频监控点":
                initMonitorIntoDb();
                break;
            case "队伍驻防点":
                initTeamDTOIntoDb();
                break;
            case "森林防火监测中心":
                initFireCommandIntoDb();
                break;
        }
        resourceTypeList.clear();
/*
        DbManager db = new DbConfig(getApplicationContext()).getDbManager();
        List<com.ruyiruyi.rylibrary.db.Resource> resourceList = null;
        if (leixingView.getText().equals("类型")){
            resourceList =  x.getDb(new DbConfig(this).getDaoConfig()).selector(com.ruyiruyi.rylibrary.db.Resource.class)
                    .where("state","=","ACTIVE")
                    .where("isdisplay","=","1")
                    .findAll();
        }else {
            resourceList =  x.getDb(new DbConfig(this).getDaoConfig()).selector(com.ruyiruyi.rylibrary.db.Resource.class)
                    .where("state","=","ACTIVE")
                    .where("isdisplay","=","1")
                    .where("name","=", leixingView.getText().toString())
                    .findAll();
        }
        for (int i = 0; i < resourceList.size(); i++) {
            Log.e(TAG, "getDataFromDb: name=" + resourceList.get(i).getName() );
            com.ruyiruyi.rylibrary.db.Resource resource1 = resourceList.get(i);
            if (resourceList.get(i).getName().equals("护林检查站")) {
                Log.e(TAG, "getDataFromDb: 1111111" );
                //检查站
                List<CheckStationDTO> checkStationDTOList = db.selector(CheckStationDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();
                Log.e(TAG, "getDataFromDb1: "+checkStationDTOList.size() );
                List<Resource> checkStationDTOResourceList = new ArrayList<>();
                for (int  j= 0; j < checkStationDTOList.size(); j++) {
                    CheckStationDTO dto = checkStationDTOList.get(j);
                    checkStationDTOResourceList.add(new Resource(dto.getId(),dto.getName(),Double.parseDouble(dto.getLatitude()),Double.parseDouble(dto.getLongitude()),dto.getType(),resource1.getCode(),resource1.getApiUrl(),dto.getGridId(),
                            dto.getGridName(),dto.getGridNo(),"护林检查站"));
                }
                resourceTypeList.add(new ResourceType("护林检查站",checkStationDTOResourceList));
            }else if (resourceList.get(i).getName().equals("专业队")){
                List<TeamDTO> dtoList = db.selector(TeamDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();


                List<Resource> TeamDTOResourceList = new ArrayList<>();
                for (int j = 0; j < dtoList.size(); j++) {
                    TeamDTO dto = dtoList.get(j);
                    TeamDTOResourceList.add(new Resource(dto.getId(),dto.getName(),Double.parseDouble(dto.getLatitude()),Double.parseDouble(dto.getLongitude()),dto.getType(),resource1.getCode(),resource1.getApiUrl(),dto.getGridId(),
                            dto.getGridName(),dto.getGridNo(),"专业队"));
                }

            }else if (resourceList.get(i).getName().equals("指挥部")){
                //指挥部
                List<FireCommandDTO> fireCommandDTOList = db.selector(FireCommandDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();
                List<Resource> fireCommandDTOResourceList = new ArrayList<>();
                for (int j = 0; j < fireCommandDTOList.size(); j++) {
                    FireCommandDTO dto = fireCommandDTOList.get(j);
                    fireCommandDTOResourceList.add(new Resource(dto.getId(),dto.getName(),Double.parseDouble(dto.getLatitude()),Double.parseDouble(dto.getLongitude()),dto.getType(),resource1.getCode(),resource1.getApiUrl(),dto.getGridId(),
                            dto.getGridName(),dto.getGridNo(),"指挥部"));
                }
                resourceTypeList.add(new ResourceType("指挥部",fireCommandDTOResourceList));
            }else if (resourceList.get(i).getName().equals("物资库")){

                //物资库
                List<MaterialRepositoryDTO> materialRepositoryDTOList = db.selector(MaterialRepositoryDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();
                List<Resource> materialRepositoryDTOResourceList = new ArrayList<>();
                for (int j = 0; j < materialRepositoryDTOList.size(); j++) {
                    MaterialRepositoryDTO dto = materialRepositoryDTOList.get(j);
                    materialRepositoryDTOResourceList.add(new Resource(dto.getId(),dto.getName(),Double.parseDouble(dto.getLatitude()),Double.parseDouble(dto.getLongitude()),dto.getType(),resource1.getCode(),resource1.getApiUrl(),dto.getGridId(),
                            dto.getGridName(),dto.getGridNo(),"物资库"));
                }
                resourceTypeList.add(new ResourceType("物资库",materialRepositoryDTOResourceList));
            } else if (resourceList.get(i).getName().equals("视频监控点")){

                //视频监控点
                List<MonitorDTO> monitorDTOList = db.selector(MonitorDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();
                List<Resource> monitorDTOResourceList = new ArrayList<>();
                if (monitorDTOList!=null) {
                    for (int j = 0; j < monitorDTOList.size(); j++) {
                        MonitorDTO dto = monitorDTOList.get(j);
                        monitorDTOResourceList.add(new Resource(dto.getId(), dto.getName(), Double.parseDouble(dto.getLatitude()), Double.parseDouble(dto.getLongitude()), "", "", "", "", "", "", "视频监控点"));
                    }
                }
                resourceTypeList.add(new ResourceType("视频监控点",monitorDTOResourceList));
            }else if (resourceList.get(i).getName().equals("水源地")){
                //水源地
                List<WaterSourceDTO> waterSourceDTOList = db.selector(WaterSourceDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();
                List<Resource> waterSourceDTOListResourceList = new ArrayList<>();
                if (waterSourceDTOList!=null) {
                    for (int j = 0; j < waterSourceDTOList.size(); j++) {
                        WaterSourceDTO dto = waterSourceDTOList.get(j);
                        waterSourceDTOListResourceList.add(new Resource(dto.getId(), dto.getName(), Double.parseDouble(dto.getLatitude()), Double.parseDouble(dto.getLongitude()), "", "", "", "", "", "", "水源地"));
                    }
                }
                resourceTypeList.add(new ResourceType("水源地",waterSourceDTOListResourceList));

            }else if (resourceList.get(i).getName().equals("瞭望塔")){
                //瞭望塔
                List<WatchTowerDTO> watchTowerDTOList = db.selector(WatchTowerDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();
                List<Resource> watchTowerResourceList = new ArrayList<>();
                if (watchTowerDTOList!=null) {
                    for (int j = 0; j < watchTowerDTOList.size(); j++) {
                        WatchTowerDTO dto = watchTowerDTOList.get(j);
                        watchTowerResourceList.add(new Resource(dto.getId(), dto.getName(), Double.parseDouble(dto.getLatitude()), Double.parseDouble(dto.getLongitude()), "", "", "", "", "", "", "瞭望塔"));
                    }
                }
                resourceTypeList.add(new ResourceType("瞭望塔",watchTowerResourceList));
            }else if (resourceList.get(i).getName().equals("重大危险源")){

                //重大危险源
                List<DangerSourceDTO> dangerSourceDTOList = db.selector(DangerSourceDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();
                List<Resource>dangerSourceDTOResourceList = new ArrayList<>();
                if (dangerSourceDTOList!=null) {
                    for (int j = 0; j < dangerSourceDTOList.size(); j++) {
                        DangerSourceDTO dto = dangerSourceDTOList.get(j);
                        dangerSourceDTOResourceList.add(new Resource(dto.getId(), dto.getName(), Double.parseDouble(dto.getLatitude()), Double.parseDouble(dto.getLongitude()), "", "", "", "", "", "", "重大危险源"));
                    }
                }
                resourceTypeList.add(new ResourceType("重大危险源",dangerSourceDTOResourceList));
            }else if (resourceList.get(i).getName().equals("墓地")){

                //墓地
                List<CemeteryDTO> cemeteryDTOList = db.selector(CemeteryDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();
                List<Resource> cemeteryResourceList = new ArrayList<>();
                if (cemeteryDTOList!=null) {
                    for (int j = 0; j < cemeteryDTOList.size(); j++) {
                        CemeteryDTO dto = cemeteryDTOList.get(j);
                        cemeteryResourceList.add(new Resource(dto.getId(), dto.getName(), Double.parseDouble(dto.getLatitude()), Double.parseDouble(dto.getLongitude()), "", "", "", "", "", "", "墓地"));
                    }
                }
                resourceTypeList.add(new ResourceType("墓地",cemeteryResourceList));
            }else if (resourceList.get(i).getName().equals("直升机机降点")){
                //直升机机降点
                List<HelicopterPointDTO> helicopterPointDTOList = db.selector(HelicopterPointDTO.class)
                        .where("state", "=", "ACTIVE")
                        .where("streetno", "=", currentStreeNo)
                        .findAll();
                Log.e(TAG, "getDataFromDb1: "+helicopterPointDTOList.toString() );
                List<Resource> helicopterPointResourceList = new ArrayList<>();
                for (int j = 0; j < helicopterPointDTOList.size(); j++) {
                    HelicopterPointDTO dto = helicopterPointDTOList.get(j);
                    helicopterPointResourceList.add(new Resource(dto.getId(),dto.getName(),Double.parseDouble(dto.getLat()),Double.parseDouble(dto.getLng()),"","","","","","","直升机机降点"));
                }
                resourceTypeList.add(new ResourceType("直升机机降点",helicopterPointResourceList));
            }

        }*/

    }

    private void initData() {
        items.clear();
        for (int i = 0; i < resourceTypeList.size(); i++) {
            List<Resource> resourceList = resourceTypeList.get(i).getResourceList();
            if (resourceList.size() > 0){
                items.add(resourceTypeList.get(i));
                if (resourceTypeList.get(i).isOpen){

                    for (int j = 0; j < resourceList.size(); j++) {
                        items.add(resourceList.get(j));
                    }
                }
            }

        }
        if (items.size() == 0){
            Toast.makeText(this, "该区域暂无资源点", Toast.LENGTH_SHORT).show();
            items.add(new Empty("暂无数据"));
        }
            assertAllRegistered(adapter,items);
            adapter.notifyDataSetChanged();
            //resourceList.clear();
    }

    private void register() {
        ResourceViewBinder resourceViewBinder = new ResourceViewBinder();
        resourceViewBinder.setListener(this);
        adapter.register(Resource.class, resourceViewBinder);
        ResourceTypeViewBinder resourceTypeViewBinder = new ResourceTypeViewBinder();
        resourceTypeViewBinder.setListener(this);
        adapter.register(ResourceType.class, resourceTypeViewBinder);
        adapter.register(Empty.class,new EmptyViewBinder(this));
    }
    private void getAreaFromService() {
        showDialogProgress(progressDialog, "数据获取中...");
        JSONObject jsonObject = new JSONObject();
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "auth/api/sysArea/getAllSysArea");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization", "bearer " + CommonData.token);
        Log.i(TAG, "getAreaFromService: " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:diqu -- " + result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    allAreaList.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        String id = object.getString("id");
                        String name = object.getString("name");
                        String parentId = object.getString("parentId");
                        String level = object.getString("level");
                        String createTime = object.getString("createTime");
                        Area area = new Area(id, name, parentId, createTime, level);
                        allAreaList.add(area);
                    }
                    DbConfig dbConfig = new DbConfig(getApplicationContext());
                    DbManager db = dbConfig.getDbManager();
                    try {
                        db.saveOrUpdate(allAreaList);
                    } catch (DbException e) {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: 请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 获取所有街道数据
     */
    private void getAllJieDao(boolean show) {
        jiedaoList.clear();
        DbManager db = new DbConfig(getApplicationContext()).getDbManager();
        try {
            jiedaoList = db.selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .where("level", "=", "1")
                    .where("parentid", "=", currentQuId)
                    .findAll();

            jiedaoStrList.clear();
            jiedaoStrList.add("请选择街道");
            for (int i = 0; i < jiedaoList.size(); i++) {
                jiedaoStrList.add(jiedaoList.get(i).getName());
            }
            if(show){
                showAreaDialog(jiedaoStrList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有区的数据
     */
    private void getAllQu() {

        quList.clear();
        DbManager db = new DbConfig(getApplicationContext()).getDbManager();
        try {
            quList = db.selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .and("level", "=", "2")
                    .and("gridno", "like", "3702%")
                    .findAll();

            quStrList.clear();

                quStrList.add("请选择区");
                for (int i = 0; i < quList.size(); i++) {
                    if (!quList.get(i).getName().equals("高新区")) {
                        quStrList.add(quList.get(i).getName());
                    }
                }
                //区级市固定 市级可任选
                if(!cityRoot){
                    for (int i = 0; i < quList.size(); i++) {
                        if(quList.get(i).getGridNo().contains((CharSequence) SPUtils.get(this, SPValue.gridNo,""))){
                            currentQuNo = quList.get(i).getGridNo();
                            currentQuId = quList.get(i).getId();
                            currentChooseQu = quList.get(i).getName();
                            quText.setText(currentChooseQu);
                            quText.setTextColor(getResources().getColor(R.color.text_color9));
                            quLayout.setClickable(false);
                            iv_city.setVisibility(View.GONE);
                        }
                    }
                }
                getAllJieDao(false);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    private void showAreaDialog(List<String> strList) {
        View areaView = LayoutInflater.from(this).inflate(R.layout.dialog_area, null);
        areaWy = ((WheelView) areaView.findViewById(R.id.wheel_view_area));
        areaWy.setIsLoop(false);
        if (currentChooseArea == 0){
            areaWy.setItems(strList, quSelectIndex);//init selected position is 0 初始选中位置为0
        }else {
            areaWy.setItems(strList, jieDaoSelectIndex);//init selected position is 0 初始选中位置为0
        }

        areaWy.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                if (currentChooseArea == 0){   //选择区
                    isChooseQu = true;
                    currentChooseQu = areaWy.getSelectedItem();
                    quSelectIndex = areaWy.getSelectedPosition();
                    quText.setText(currentChooseQu);
                    currentChooseJiedao = "";
                    jiedaoText.setText("请选择街道");
                    jieDaoSelectIndex = 0;


                    for (int i = 0; i < quList.size(); i++) {
                        if (quList.get(i).getName().equals(currentChooseQu)) {
                            currentQuId = quList.get(i).getId();
                            currentQuNo = quList.get(i).getGridNo();
                        }
                    }
                }else {    //选择街道
                    currentChooseJiedao = areaWy.getSelectedItem();
                    jieDaoSelectIndex = areaWy.getSelectedPosition();
                    jiedaoText.setText(currentChooseJiedao);
                }

            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请选择区域")
                .setView(areaView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String areStr = "";
                        String area = areaWy.getSelectedItem();
                    }
                })
                .show();
    }
    /**
     * 资源点类型的点击 条目收缩
     * @param resourceTypeName
     * @param isOpen
     */
    @Override
    public void onResourceTypeItemClickListener(String resourceTypeName, boolean isOpen) {
        for (int i = 0; i < resourceTypeList.size(); i++) {
            if (resourceTypeList.get(i).getResourceTypeName().equals(resourceTypeName)) {
                resourceTypeList.get(i).setOpen(isOpen);
            }
        }
        initData();
    }

    /**
     * 导航回调
     * @param resource
     */
    @Override
    public void onDaoHangClickListener(Resource resource) {
        String jingweiStr = getLocation();
        String starWeidu = "";
        String starJingdu = "";
        if (!jingweiStr.isEmpty()){
            List<String> jingweiList = Arrays.asList(jingweiStr.split(","));
            starWeidu = jingweiList.get(1);
            starJingdu = jingweiList.get(0);
        }
        LatLng latLng = new LatLngChange().transformFromWGSToGCJ(new LatLng(resource.getLat(),resource.getLng()));


        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);
        Poi start = new Poi("", new LatLng(Double.parseDouble(starWeidu),Double.parseDouble(starJingdu)), "");
        Poi end = new Poi(resource.getName(), new LatLng(latLng.latitude,latLng.longitude),"");
        AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER);
        params.setUseInnerVoice(true);
        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, ResourceSearchActivity.this);
    }

    /**
     * 资源点选中回调
     * @param resource
     * @param isChoose
     */
    @Override
    public void onItemChooseClickListener(Resource resource, boolean isChoose) {
        int count = 0;
        for (int i = 0; i < resourceTypeList.size(); i++) {
            List<Resource> resourceList = resourceTypeList.get(i).getResourceList();
            for (int j = 0; j < resourceList.size(); j++) {
                if (resourceList.get(j).isChoose) {
                    count ++;
                }
            }
        }
        if (count >= 4 && isChoose == true){
            Toast.makeText(this, "最多可以选取4个资源点", Toast.LENGTH_SHORT).show();
        }else {
            for (int i = 0; i < resourceTypeList.size(); i++) {
                List<Resource> resourceList = resourceTypeList.get(i).getResourceList();
                for (int j = 0; j < resourceList.size(); j++) {
                    if (resourceList.get(j).getId().equals(resource.getId())) {
                        resourceList.get(j).setChoose(isChoose);
                    }
                }
            }

            initData();
        }


    }

    /**
     * 资源点检查的点击
     * @param resource
     */
    @Override
    public void onItemCheckClickListener(Resource resource) {
        Intent intent = new Intent(getApplicationContext(), ResourceCheckActivity.class);
        intent.putExtra("resourceID", resource.getId());
        intent.putExtra("resourceName", resource.getName());
        intent.putExtra("planResourceType", resourceType);
        intent.putExtra("from", "resourceserch");
        intent.putExtra("APIURL", resource.getApiUrl());
        intent.putExtra("GRID_NO", resource.getGridNo());
        intent.putExtra("resourcegird", resource.getGridName());
        intent.putExtra("girdno", (String) SPUtils.get(this,SPValue.gridNo,""));
        intent.putExtra("lat", resource.getLat());
        intent.putExtra("lng", resource.getLng());
        intent.putExtra("groupId",resource.getGroupId());
        startActivity(intent);
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public void onNaviDirectionChanged(int i) {

    }

    @Override
    public void onDayAndNightModeChanged(int i) {

    }

    @Override
    public void onBroadcastModeChanged(int i) {

    }

    @Override
    public void onScaleAutoChanged(boolean b) {

    }
    /**
     * 获取当前位置经纬度
     * @return
     */
    @JavascriptInterface
    public String getLocation() {
        //获得位置服务
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Toast.makeText(this, "请打开GPS和使用网络定位以提高精度", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
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
            Toast.makeText(this, "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();

        }
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location location= locationManager.getLastKnownLocation(provider);
            try {
                return location.getLongitude()+","+location.getLatitude();
            }catch (Exception e){
                return "0.00,0.00";
            }

        }
        return null;
    }

    /**
     * 定位器provider
     * @param locationManager
     * @return
     */
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            return LocationManager.NETWORK_PROVIDER;//网络定位
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;//GPS定位
        }else{
            Toast.makeText(this,"未开启本应用地理位置信息，请先开启！",Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
