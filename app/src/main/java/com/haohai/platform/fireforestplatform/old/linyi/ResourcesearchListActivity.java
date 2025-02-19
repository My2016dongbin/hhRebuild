package com.haohai.platform.fireforestplatform.old.linyi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class ResourcesearchListActivity extends HhBaseActivity implements DatePicker.OnDateChangedListener,ResourceSearchListViewBinder.OnPlanItemClick {
    private static final String TAG = ResourcesearchListActivity.class.getSimpleName();
    public static int ORDER_CHANGE = 113;
    private LinearLayout diquLayout;
    private Dialog shaixuanDialog;
    private View shaixuanInflater;
    private TextView gaojiStartimeText;
    private TextView gaojiEndtimeText;
    private TextView shengLayout;
    private TextView shiLayout;
    private TextView quLayout;
    private TextView findButton;
    private StringBuffer date;
    private int year;
    private int month;
    private int day;
    private TextView chongzhiButton;
    private ProgressDialog progressDialog;
    private boolean isShowDialog = true;
    private List<ResourceSearch> checkplanList;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private RecyclerView listView;
    private int currentPage = 1;
    private int totalSize;
    private int lastPage;
    private boolean isShuaxin=false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String CurrentTime;
    private String chooetime="";
    private String endtime;
    public int currentChooseArea = 0;  //当前在选择省还是市   0选择省  1选择市
    private WheelView areaWy;
    public int shengSelectIndex = 0;
    public int shiSelectIndex = 0;
    public int quSelectIndex = 0;
    public boolean isChooseSheng = false;
    public String currentChooseSheng = "";
    public String currentChooseShi = "";
    public String currentChooseQu = "";
    private boolean fromMap = false;
    public List<Area> shengList;
    public List<String> shengStrList;
    public List<Area> shiList;
    public List<String> shiStrList;
    public List<Area> quList;
    public List<String> quStrList;
    private List<Area> allAreaList;
    private int chooseStatus;
    private LinearLayout leixingLayout;
    public List<String> typeStrList;
    private List<ResourceTypeList> resourceTypeLists = new ArrayList<>();
    public int typeSelectIndex = 0;
    public String currentChooseType = "";
    private TextView leixingView;
    private String resourceType="checkStation";
    private int typenum;
    private TextView zhengxuText;
    private TextView daoxuText;
    private String timeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_search_list);
        progressDialog = new ProgressDialog(this);
        isShowDialog = true;
        shengList = new ArrayList<>();
        shiList = new ArrayList<>();
        quList = new ArrayList<>();
        shengStrList = new ArrayList<>();
        shiStrList = new ArrayList<>();
        quStrList = new ArrayList<>();
        allAreaList = new ArrayList<>();
        typeStrList = new ArrayList<>();
        typeStrList.add("护林检查站");
        typeStrList.add("消防专业队");
        typeStrList.add("物资储备库");
        initView();
        initDateTime();
        getDataFromService();
        getAreaFromService();
    }

    private void initView() {
        checkplanList=new ArrayList<>();
        date = new StringBuffer();
        diquLayout =findViewById(R.id.diqu_layout);
        shaixuanDialog=new Dialog(this, R.style.ActionSheetDialogStyle);
        shaixuanInflater= LayoutInflater.from(this).inflate(R.layout.dialog_resource_search,null);
        shaixuanInflater.setMinimumWidth(10000);
        shaixuanDialog.setContentView(shaixuanInflater);
        Window gaojiDialogWindow = shaixuanDialog.getWindow();
        gaojiDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams gaojiLp = gaojiDialogWindow.getAttributes();

        WindowManager wmGaoji = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int heightGaoji = wmGaoji.getDefaultDisplay().getHeight();
        gaojiLp.height = (int) (heightGaoji * 0.8);
        gaojiDialogWindow.setAttributes(gaojiLp);
        shaixuanDialog.setCanceledOnTouchOutside(true);
        initshaixuanView();
        adapter = new MultiTypeAdapter(items);
        listView = findViewById(R.id.check_plan_listview);
        register();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.check_plan_refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isShowDialog = false;
                getDataFromService();
            }
        });
        /**
         * 省市的点击
         */
        RxViewAction.clickNoDouble(shengLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 0;
                        //     getAllAre();
                        showAreaDialog(shengStrList);
                    }
                });
        RxViewAction.clickNoDouble(shiLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 1;
                        Log.e(TAG, "call: 12321--" + shengLayout.getText().toString());
                        if (fromMap) {
                            showAreaDialog(shiStrList);
                        } else {
                            if (shengLayout.getText().toString().equals("请选择省")) {
                                Toast.makeText(ResourcesearchListActivity.this, "请先选择省", Toast.LENGTH_SHORT).show();
                            } else {
                                String currentShengId = "";
                                for (int i = 0; i < shengList.size(); i++) {
                                    if (shengList.get(i).getName().equals(currentChooseSheng)) {
                                        currentShengId = shengList.get(i).getId();
                                    }
                                }

                                initShi(currentShengId);
                                ;
                            }
                        }
                    }
                });
        RxViewAction.clickNoDouble(quLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 2;
                        Log.e(TAG, "call: 12321--" + shiLayout.getText().toString());
                        if (fromMap) {
                            showAreaDialog(quStrList);
                        } else {
                            if (shiLayout.getText().toString().equals("请选择市")) {
                                Toast.makeText(ResourcesearchListActivity.this, "请先选择市", Toast.LENGTH_SHORT).show();
                            } else {
                                String currentshiId = "";
                                for (int i = 0; i < shiList.size(); i++) {
                                    if (shiList.get(i).getName().equals(currentChooseShi)) {
                                        currentshiId = shiList.get(i).getId();
                                    }
                                }

                                initqu(currentshiId);
                            }
                        }
                    }
                });
    }

    private void initshaixuanView() {
        gaojiStartimeText=shaixuanInflater.findViewById(R.id.gaoji_startime_text);
        gaojiEndtimeText=shaixuanInflater.findViewById(R.id.gaoji_endtime_text);
        shengLayout=shaixuanInflater.findViewById(R.id.sheng_text);
        shiLayout=shaixuanInflater.findViewById(R.id.shi_text);
        quLayout=shaixuanInflater.findViewById(R.id.qu_text);
        chongzhiButton = shaixuanInflater.findViewById(R.id.chongzhi_button);
        findButton=shaixuanInflater.findViewById(R.id.find_button);
        leixingLayout = shaixuanInflater.findViewById(R.id.type_layout);
        leixingView = shaixuanInflater.findViewById(R.id.type_view);
        zhengxuText = shaixuanInflater.findViewById(R.id.zhengxu_text);
        daoxuText = shaixuanInflater.findViewById(R.id.daoxu_text);
        RxViewAction.clickNoDouble(zhengxuText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        chooseStatus=0;
                        zhengxuText.setBackgroundResource(R.drawable.bg_text_lan);
                        zhengxuText.setTextColor(getResources().getColor(R.color.c12));
                        daoxuText.setBackgroundResource(R.drawable.bg_text_hui);
                        daoxuText.setTextColor(getResources().getColor(R.color.c6));
                    }
                });
        RxViewAction.clickNoDouble(daoxuText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        chooseStatus=0;
                        daoxuText.setBackgroundResource(R.drawable.bg_text_lan);
                        daoxuText.setTextColor(getResources().getColor(R.color.c12));
                        zhengxuText.setBackgroundResource(R.drawable.bg_text_hui);
                        zhengxuText.setTextColor(getResources().getColor(R.color.c6));
                    }
                });
        RxViewAction.clickNoDouble(diquLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        shaixuanDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(gaojiStartimeText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showDataDialog(gaojiStartimeText);
                    }
                });

        RxViewAction.clickNoDouble(gaojiEndtimeText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showDataDialog(gaojiEndtimeText);
                    }
                });
        RxViewAction.clickNoDouble(chongzhiButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        gaojiStartimeText.setText("请输入开始时间");
                        gaojiEndtimeText.setText("请输入结束时间");

                        shengLayout.setText("请选择省");
                        shiLayout.setText("请选择市");
                        quLayout.setText("请选择区");
                        chooseStatus=0;
                        chooetime="";
                    }
                });
        RxViewAction.clickNoDouble(findButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        getDataFromService();
                        shaixuanDialog.dismiss();
                    }
                });
        RxViewAction.clickNoDouble(leixingLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showTypeDialog(typeStrList);
                    }
                });
    }
    private void showDataDialog(final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }

                date.append(String.valueOf(year));
                if (month < 9){
                    date.append("-0").append(String.valueOf(month + 1));
                }else {
                    date.append("-").append(String.valueOf(month + 1));
                }
                if (day <10){
                    date.append("-0").append(String.valueOf(day));
                } else {
                    date.append("-").append(String.valueOf(day));
                }
                textView.setText(date);
                chooetime="";

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog  dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH);
        int day1 = date.get(Calendar.DATE);
        String endData = year1 - 10 + "-" + month1 + "-" + day1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(endData);
        } catch (ParseException e) {

        }
        long starTimre = date2.getTime();


        long endTimre = System.currentTimeMillis();

        //datePicker.setMaxDate(endTimre);
        datePicker.setMinDate(starTimre);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month, day, this);
    }
    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        int monthnow=month+1;
        CurrentTime = year + "-" + monthnow + "-" + day;
        Log.e(TAG, "initDateTime: "+CurrentTime );
    }
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }
    private void getDataFromService() {
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto=new JSONObject();
            jsonObject.put("dto",dto);
            jsonObject.put("limit",200);
            jsonObject.put("page",1);
            Log.e(TAG, "getDataFromService: "+quLayout.getText().toString() );
            if (!quLayout.getText().toString().equals("")){
                dto.put("gridName",currentChooseQu);
            }
            if (!leixingView.getText().toString().equals("类型")){
                dto.put("checkType",typenum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/planResource/queryResource");
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        Log.e(TAG, "getDataFromService: " + params);
        params.addHeader("Authorization", "bearer " + CommonData.token);

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject getJsonObj = data.getJSONObject(0);//获取json数组中的第一项
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                        Gson gson = new Gson();
                        checkplanList.clear();
                        checkplanList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<ResourceSearch>>() {
                        }.getType());
                        //Log.e(TAG, "onSuccess: "+checkplanList.get(0).planResourceDTOS.size() );
                        initData();
                        swipeRefreshLayout.setRefreshing(false);
                    }else {
                        Toast.makeText(ResourcesearchListActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
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
                progressDialog.dismiss();
            }
        });
    }
    private void initData() {
        items.clear();
        if (checkplanList.size()==0){
            items.add(new Empty("暂无数据"));
        }else {
            for (int i = 0; i < checkplanList.size(); i++) {
                items.add(checkplanList.get(i));
                if (checkplanList.size()>0) {
                    for (int j = 0; j < checkplanList.size(); j++) {
                        items.add(checkplanList.get(i));
                    }
                }
            }
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }
    private void register() {
        ResourceSearchListViewBinder resourceSearchListViewBinder =new ResourceSearchListViewBinder();
        resourceSearchListViewBinder.setListener(this);
        adapter.register(ResourceSearch.class, resourceSearchListViewBinder);
        adapter.register(Empty.class,new EmptyViewBinder(this));
    }
     /**
     * 显示地区选择的dialog
     */
    private void showAreaDialog(List<String> strList) {
        View areaView = LayoutInflater.from(this).inflate(R.layout.dialog_area, null);
        areaWy = ((WheelView) areaView.findViewById(R.id.wheel_view_area));
        areaWy.setIsLoop(false);
        if (currentChooseArea == 0) {
            areaWy.setItems(strList, shengSelectIndex);//init selected position is 0 初始选中位置为0
        } else if (currentChooseArea == 1) {
            areaWy.setItems(strList, shiSelectIndex);//init selected position is 0 初始选中位置为0
        } else {
            areaWy.setItems(strList, quSelectIndex);
        }

        areaWy.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                fromMap = false;
                if (currentChooseArea == 0) {   //选择省
                    isChooseSheng = true;
                    currentChooseSheng = areaWy.getSelectedItem();
                    shengSelectIndex = areaWy.getSelectedPosition();
                    shengLayout.setText(currentChooseSheng);
                    //选择剩要初始化市
                    shiLayout.setText("请选择市");
                    currentChooseShi = "请选择市";
                    shiSelectIndex = 0;
                    quLayout.setText("请选择市");
                    currentChooseQu = "请选择市";
                    quSelectIndex = 0;
                } else if (currentChooseArea == 1) {                          //选择市
                    currentChooseShi = areaWy.getSelectedItem();
                    shiSelectIndex = areaWy.getSelectedPosition();
                    shiLayout.setText(currentChooseShi);
                    quLayout.setText("请选择市");
                    currentChooseQu = "请选择市";
                    quSelectIndex = 0;
                } else {
                    currentChooseQu = areaWy.getSelectedItem();
                    quSelectIndex = areaWy.getSelectedPosition();
                    quLayout.setText(currentChooseQu);
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
    private void initqu(String currentshiId) {
        quList.clear();
        quStrList.clear();
        quStrList.add("请选择区");
        for (int i = 0; i < allAreaList.size(); i++) {
            if (allAreaList.get(i).getParentId().equals(currentshiId)) {
                quList.add(allAreaList.get(i));
                quStrList.add(allAreaList.get(i).getName());
            }
        }

        showAreaDialog(quStrList);
    }

    private void initShi(String currentShengId) {
        shiList.clear();
        shiStrList.clear();
        shiStrList.add("请选择市");
        for (int i = 0; i < allAreaList.size(); i++) {
            if (allAreaList.get(i).getParentId().equals(currentShengId)) {
                shiList.add(allAreaList.get(i));
                shiStrList.add(allAreaList.get(i).getName());
            }
        }

        showAreaDialog(shiStrList);
    }

    private void initArea() {
        shengStrList.clear();
        shengList.clear();
        shengStrList.add("请选择省");
        for (int i = 0; i < allAreaList.size(); i++) {
            if (allAreaList.get(i).getLevel().equals("1")) {
                shengList.add(allAreaList.get(i));
                shengStrList.add(allAreaList.get(i).getName());
            }
        }
    }
    /**
     * 获取区域数据
     */
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

                    initArea();


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
    //Day:日期字符串例如 2015-3-10  Num:需要减少的天数例如 7
    public static String getDateStr(String day,int Num) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(nowDate.getTime() - (long)Num * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
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
                Log.e(TAG, "onItemSelected: "+currentChooseType );
                leixingView.setText(currentChooseType);
                switch (currentChooseType){
                    case "护林检查站":
                        resourceType="checkStation";
                        typenum=3;
                        break;
                    case "消防专业队":
                        resourceType = "team";
                        typenum=5;
                        break;
                    case "物资储备库":
                        resourceType = "materialRepository";
                        typenum=4;
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult:requestCode " + requestCode);
        Log.e(TAG, "onActivityResult:resultCode " +  resultCode);
        if (resultCode == ORDER_CHANGE) {
            Log.e(TAG, "onActivityResult: 111" );
            checkplanList.clear();
            isShowDialog = true;
            getDataFromService();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getDataFromService();
    }

    @Override
    public void onChecklistItemClickListener(ResourceSearch checkPlanList) {
        Log.e(TAG, "onChecklistItemClickListener: "+checkPlanList.getId() );
            Intent intent = new Intent(getApplicationContext(), ResourceCheckActivity.class);
            intent.putExtra("resourceID", checkPlanList.getId());
            intent.putExtra("resourceName", checkPlanList.getName());
            //intent.putExtra("resourcegird", checkPlanList.getGridName());
            intent.putExtra("endTime", checkPlanList.getEndTime());
//            intent.putExtra("girdno", checkPlanList.getgridNo());
//            intent.putExtra("resourcetype", checkPlanList.getCheckType());
            startActivityForResult(intent, ORDER_CHANGE);

    }

}