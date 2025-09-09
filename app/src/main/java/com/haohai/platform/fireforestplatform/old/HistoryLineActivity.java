package com.haohai.platform.fireforestplatform.old;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.NaviSetting;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.helper.DialogHelper;
import com.haohai.platform.fireforestplatform.old.bean.Person;
import com.haohai.platform.fireforestplatform.old.bean.Tree;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryLineActivity extends BaseActivity implements DatePicker.OnDateChangedListener, TreeViewBinder.OnTreeClick, PersonViewBinder.OnPersonClick {
    LinearLayout ll_bitmaps;
    ImageView info_show;
    TextView info_index;
    TextView info_text;
    ImageView iv_back;
    TextView tv_find;
    private Dialog searchDialog;
    private View searchInflater;
    private Dialog resultDialog;
    private View resultInflater;
    private LinearLayout ll_page1;
    private RecyclerView ll_page2;
    private TextView tv_title;
    private FrameLayout fl_date;
    private FrameLayout fl_date_end;
    private FrameLayout fl_start;
    private FrameLayout fl_end;
    private FrameLayout fl_user;
    private FrameLayout fl_back;
    private TextView tv_search;
    private TextView tv_date;
    private TextView tv_date_end;
    private TextView tv_start;
    private TextView tv_end;
    private TextView tv_user;
    private TextView tv_result_title;
    private ImageView cha;
    private ImageView cha_result;
    private TextView result_km;
    private TextView result_time;
    private TextView result_start;
    private TextView result_end;
    private com.amap.api.maps.MapView aMapView;
    private com.amap.api.maps.AMap aMap;
    private final int status = 0;//0未查询 1日期 2查询结束
    private final String TAG = HistoryLineActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_history_line);
        chooseUserId = getIntent().getStringExtra("id");
        chooseUserName = getIntent().getStringExtra("name");
        mHandler = new Handler(getMainLooper());
        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);
        aMapView = findViewById(R.id.aMapView);
        aMapView.onCreate(savedInstanceState);
        aMap = aMapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        flyBaiduMap(CommonData.lat,CommonData.lng);
        init();
        initTree();
    }

    private MultiTypeAdapter adapter;
    private final List<Object> items = new ArrayList<>();
    private List<Tree> treeList = new ArrayList<>();
    private void initTree() {
        RequestParams params = new RequestParams(URLConstant.GET_GRID_TREES);
        params.addHeader("Authorization", "bearer " + CommonData.token);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: initTree" + result );
                try {
                    JSONObject object = new JSONObject(result);
                    treeList = new Gson().fromJson(String.valueOf(object.getJSONArray("data")),new TypeToken<List<Tree>>(){}.getType());

                    treeList = TreeUtils.parseLevel(treeList);
                    initTreeData();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void initTreeData () {
        items.clear();
        if (treeList.size() == 0) {
            items.add(new Empty("暂无数据"));
        } else {
            items.addAll(treeList);
        }
        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }



    private final List<String> timeList = new ArrayList<>();
    private final List<LatLng> allList = new ArrayList<>();
    private void postData(){
        RequestParams params = new RequestParams(URLConstant.HISTORY_LINE + SPUtils.get(HhApplication.getInstance(), SPValue.id, ""));
        params.addParameter("id",SPUtils.get(HhApplication.getInstance(), SPValue.id, ""));
        params.addParameter("time",tv_date.getText().toString());
        Log.e(TAG,"postData " + params);
        DialogHelper.getInstance().show(this, "正在查询轨迹..");
        HhHttp.postX(params,new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG,"postData " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");

                    List<com.amap.api.maps.model.LatLng> list = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = (JSONObject) data.get(i);
                        JSONObject position = obj.getJSONObject("position");

                        double[] doubles = LatLngChangeNew.calWGS84toGCJ02(position.getDouble("lat"), position.getDouble("lng"));
                        LatLng latLng = new LatLng(doubles[0],doubles[1]);
                        list.add(latLng);
                        allList.add(latLng);
                        timeList.add(obj.getString("offlineUploadTime"));
                    }
                    //result_km.setText("1024" + "m");

                    drawPolyLine(list);


                    searchDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                DialogHelper.getInstance().close();
            }
        });
    }

    private final BitmapDescriptor mGreenTexture =
            BitmapDescriptorFactory.fromAsset("Icon_road_blue__.png");
    private final BitmapDescriptor mBitmapCar = BitmapDescriptorFactory.fromResource(R.drawable.ic_qi);
    private final BitmapDescriptor mBitmapStart = BitmapDescriptorFactory.fromResource(R.drawable.ic_qi);
    private final BitmapDescriptor mBitmapEnd = BitmapDescriptorFactory.fromResource(R.drawable.ic_zhong);
    private Handler mHandler;

    private Polyline polyline; // 保存上一次的轨迹线

    /**
     * 绘制轨迹
     */
    @SuppressLint("SetTextI18n")
    private void drawPolyLine(List<com.amap.api.maps.model.LatLng> list) {
        if (list == null || list.size() < 2) {
            Toast.makeText(this, "轨迹点位数据太少，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }

        aMap.clear();
        // 清除旧的轨迹线（避免把其他 Marker 也清了）
        if (polyline != null) {
            polyline.remove();
        }

        // 设置折线样式
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(list)
                .color(0xFF1E90FF) // 轨迹线颜色 (深蓝)
                .width(12f)        // 轨迹线宽度
                .setUseTexture(true) // 支持纹理（如果要画虚线/箭头）
                .geodesic(true);     // 大地曲线，更符合地理实际

        // 添加轨迹线
        polyline = aMap.addPolyline(polylineOptions);

        // 起点 Marker
        aMap.addMarker(new MarkerOptions()
                .position(list.get(0))
                .snippet("")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // 终点 Marker
        aMap.addMarker(new MarkerOptions()
                .position(list.get(list.size() - 1))
                .snippet("")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // 自动缩放到轨迹范围
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (com.amap.api.maps.model.LatLng latLng : list) {
            boundsBuilder.include(latLng);
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
    }

    private void flyBaiduMap(double lat, double lng) {
        //飞到精确点上
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.amap.api.maps.model.LatLng(lat,lng),16));

    }


    @Override
    protected void onPause() {
        super.onPause();
        aMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        aMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (null != mBitmapCar) {
            mBitmapCar.recycle();
        }

        if (null != mGreenTexture) {
            mGreenTexture.recycle();
        }

        aMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        aMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onTreeClickListener() {

    }

    @Override
    public void onTreeItemClickListener(Tree tree) {

    }

    private String chooseUserId;
    private String chooseUserName;
    @Override
    public void onPersonClickListener(Person person) {
        tv_title.setText("查询巡护轨迹");
        ll_page2.setVisibility(View.GONE);
        ll_page1.setVisibility(View.VISIBLE);
        fl_back.setVisibility(View.GONE);
        tv_user.setText(person.getFullName());
        chooseUserId = person.getId();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void init() {
        if(chooseUserId==null||chooseUserId.isEmpty()){
            chooseUserId = (String) SPUtils.get(this,SPValue.id,"");//默认本账号UserId
        }
        ll_bitmaps = findViewById(R.id.ll_bitmaps);
        info_show = findViewById(R.id.info_show);
        info_index = findViewById(R.id.info_index);
        info_text = findViewById(R.id.info_text);
        iv_back = findViewById(R.id.iv_back);
        tv_find = findViewById(R.id.tv_find);
        iv_back.setOnClickListener(v -> {
            finish();
        });
        tv_find.setOnClickListener(v -> {
            searchDialog.hide();
            resultDialog.hide();
            searchDialog.show();
        });
        date = new StringBuffer();
        endDate = new StringBuffer();

        searchDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        searchInflater = LayoutInflater.from(this).inflate(R.layout.dialog_line_search, null);
        searchInflater.setMinimumWidth(10000);
        tv_title = searchInflater.findViewById(R.id.tv_title);
        ll_page1 = searchInflater.findViewById(R.id.ll_page1);
        ll_page2 = searchInflater.findViewById(R.id.ll_page2);
        fl_date = searchInflater.findViewById(R.id.fl_date);
        fl_date_end = searchInflater.findViewById(R.id.fl_date_end);
        fl_start = searchInflater.findViewById(R.id.fl_start);
        fl_end = searchInflater.findViewById(R.id.fl_end);
        fl_user = searchInflater.findViewById(R.id.fl_user);
        fl_back = searchInflater.findViewById(R.id.fl_back);
        tv_date = searchInflater.findViewById(R.id.tv_date);
        tv_date_end = searchInflater.findViewById(R.id.tv_date_end);
        tv_start = searchInflater.findViewById(R.id.tv_start);
        cha = searchInflater.findViewById(R.id.cha);
        tv_end = searchInflater.findViewById(R.id.tv_end);
        tv_user = searchInflater.findViewById(R.id.tv_user);
        tv_search = searchInflater.findViewById(R.id.tv_search);
        fl_date.setOnClickListener(v -> {
            isStart = true;
            showDataDialog();
        });
        fl_start.setOnClickListener(v -> {
            isStart = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showTimeDialog();
            }
        });
        fl_date_end.setOnClickListener(v -> {
            isStart = false;
            showDataDialog();
        });
        fl_back.setOnClickListener(v -> {
            tv_title.setText("查询巡护轨迹");
            ll_page2.setVisibility(View.GONE);
            ll_page1.setVisibility(View.VISIBLE);
            fl_back.setVisibility(View.GONE);
        });
        fl_user.setOnClickListener(v -> {
            tv_title.setText("选择查询用户");
            ll_page1.setVisibility(View.GONE);
            ll_page2.setVisibility(View.VISIBLE);
            fl_back.setVisibility(View.VISIBLE);
        });
        fl_end.setOnClickListener(v -> {
            isStart = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showTimeDialog();
            }
        });
        cha.setOnClickListener(v -> {
            searchDialog.dismiss();
        });
        tv_search.setOnClickListener(v -> {
            if (tv_date.getText().toString().contains("请选择")) {
                Toast.makeText(HistoryLineActivity.this, "请选择查询日期", Toast.LENGTH_SHORT).show();
                return;
            }
            /*if (tv_start.getText().toString().contains("请选择")) {
                Toast.makeText(HistoryLineActivity.this, "请选择开始时间", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tv_date_end.getText().toString().contains("请选择")) {
                Toast.makeText(HistoryLineActivity.this, "请选择结束日期", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tv_end.getText().toString().contains("请选择")) {
                Toast.makeText(HistoryLineActivity.this, "请选择结束时间", Toast.LENGTH_SHORT).show();
                return;
            }*/
            postData();
        });
        searchDialog.setContentView(searchInflater);
        Window fireListWindow = searchDialog.getWindow();
        fireListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams fireListLp = fireListWindow.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
//        fireListLp.height = (int) (height * 0.5);
        fireListWindow.setAttributes(fireListLp);
        searchDialog.setCanceledOnTouchOutside(true);
        searchDialog.show();

        //查询结束
        resultDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        resultInflater = LayoutInflater.from(this).inflate(R.layout.dialog_line_result, null);
        resultInflater.setMinimumWidth(10000);
        tv_result_title = resultInflater.findViewById(R.id.tv_result_title);
        cha_result = resultInflater.findViewById(R.id.cha_result);
        result_km = resultInflater.findViewById(R.id.tv_km);
        result_time = resultInflater.findViewById(R.id.tv_time);
        result_start = resultInflater.findViewById(R.id.tv_start);
        result_end = resultInflater.findViewById(R.id.tv_end);
        cha_result.setOnClickListener(v -> {
            resultDialog.dismiss();
            searchDialog.show();
        });
        resultDialog.setContentView(resultInflater);
        Window fireListWindow2 = resultDialog.getWindow();
        fireListWindow2.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams fireListLp2 = fireListWindow2.getAttributes();
        WindowManager wm2 = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height2 = wm2.getDefaultDisplay().getHeight();
        fireListLp2.height = (int) (height2 * 0.2);
        fireListWindow2.setAttributes(fireListLp2);
        resultDialog.setCanceledOnTouchOutside(true);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ll_page2.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);
        TreeViewBinder binder = new TreeViewBinder();
        binder.setListener(this,this);
        binder.setPersonListener(this);//待优化
        adapter.register(Tree.class, binder);
        PersonViewBinder binder_person = new PersonViewBinder();
        binder_person.setListener(this);
        adapter.register(Person.class, binder_person);
        adapter.register(Empty.class, new EmptyViewBinder(this));
        ll_page2.setAdapter(adapter);
        assertHasTheSameAdapter(ll_page2, adapter);


        initDateTime();



        tv_user.setText(chooseUserName);
        fl_user.setClickable(false);
    }

    private StringBuffer date;
    private StringBuffer endDate;
    private int year;
    private int month;
    private int day;
    public int chooseHour;
    public int chooseMinute;

    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                if (endDate.length() > 0) { //清除上次记录的日期
                    endDate.delete(0, endDate.length());
                }
                date.append(year);
                if (month < 9) {
                    date.append("-0").append((month + 1));
                } else {
                    date.append("-").append((month + 1));
                }
                if (day < 10) {
                    date.append("-0").append(day);
                } else {
                    date.append("-").append(day);
                }
                if (isStart) {
                    tv_date.setText(date);
                } else {
                    tv_date_end.setText(date);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();
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

        datePicker.setMaxDate(endTimre);
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
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);
        tv_date.setText(year + "-" + CommonUtil.parseZero(month+1) + "-" + CommonUtil.parseZero(day));
        tv_start.setText(CommonUtil.parseZero(chooseHour) + ":" + CommonUtil.parseZero(chooseMinute) + ":00");

        calendar.add(Calendar.HOUR_OF_DAY, 2);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);

        tv_date_end.setText(year + "-" + CommonUtil.parseZero(month+1) + "-" + CommonUtil.parseZero(day));
        tv_end.setText(CommonUtil.parseZero(chooseHour) + ":" + CommonUtil.parseZero(chooseMinute) + ":00");
    }

    private boolean isStart = true;

    /**
     * 日期选择控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTimeDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (isStart) {
                    tv_start.setText("");
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_start.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_start.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_start.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_start.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                } else {
                    tv_end.setText("");
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_end.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_end.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_end.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_end.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                }

                dialog.dismiss();
            }
        });
        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog timeDialog = builder1.create();
        View dialogView = View.inflate(this, R.layout.dialog_time, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        timePicker.setIs24HourView(true);   //设置时间显示为24小时

        timePicker.setHour(hour);  //设置当前小时
        timePicker.setMinute(minute); //设置当前分（0-59）

        timeDialog.setTitle("设置时间");
        timeDialog.setView(dialogView);
        timeDialog.show();


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {  //获取当前选择的时间
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                chooseHour = hourOfDay;
                chooseMinute = minute;
            }
        });
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }
}