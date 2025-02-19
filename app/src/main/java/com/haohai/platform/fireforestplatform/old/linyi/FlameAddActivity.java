package com.haohai.platform.fireforestplatform.old.linyi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;
import com.haohai.platform.fireforestplatform.ui.bean.AreaModel;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.FlameList;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;

public class FlameAddActivity extends HhBaseActivity implements DatePicker.OnDateChangedListener {

    private ImageView iv_back;
    private TextView tv_title;
    private boolean edit = false;
    private String id;
    private String TAG = FlameAddActivity.class.getSimpleName();
    private EditText et_name;
    private LinearLayout ll_area;
    private TextView tv_area;
    private LinearLayout ll_start;
    private TextView tv_start;
    private LinearLayout ll_end;
    private TextView tv_end;
    private TextView tv_person;
    private EditText et_person;
    private TextView tv_phone;
    private EditText et_phone;
    private LinearLayout ll_flame;
    private TextView tv_flame;
    private EditText et_info;
    private TextView tv_commit;
    private boolean isChooseStarTime = false;
    private FlameList flameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flame_add);

        Intent intent = getIntent();
        edit = intent.getBooleanExtra("edit",false);
        id = intent.getStringExtra("planId");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        if(edit){
            tv_title.setText("编辑");
            initEdit();
        }
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                onBackPressed();
            }
        });

        init_();
        bind_();
        initGridData();
    }



    private void initGridData() {
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/grid/gridTree");
        params.addHeader("Authorization", "Bearer " + CommonData.token);
        params.addHeader("NetworkType", "Internet");
        params.setBodyContent(new JSONObject().toString());
        Log.e("TAG", "onSuccess: bingo grid" + CommonData.token );
        Log.e("TAG", "onSuccess: bingo grid" + params.toString() );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("TAG", "onSuccess: bingo grid" + result );
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    areaAllList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<AreaModel>>(){}.getType());

                    if(areaAllList.size()!=0){
                        cityList.clear();
                        areaStrList.clear();
                        for (int i = 0; i < areaAllList.size(); i++) {
                            cityList.add(areaAllList.get(i));
                            areaStrList.add(areaAllList.get(i).getName());
                        }

                        initArea();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                HhLog.e("areaStrList error " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private WheelView wv_area;
    private WheelView wv_qu;
    int areaIndex = 0;
    int quIndex = 0;
    String areaId;
    String quId;
    private String areaStr = "请选择区域";
    private String quStr = "";
    private List<String> areaStrList = new ArrayList<>();
    private List<String> quStrList = new ArrayList<>();
    private AreaModel currentArea = new AreaModel();
    private List<AreaModel> areaAllList = new ArrayList<>();
    private List<AreaModel> cityList = new ArrayList<>();
    private List<AreaModel> quList = new ArrayList<>();
    private String postGroupId = "";
    private void initArea() {
        Log.e("TAG", "initArea: "  );
        if(!edit){
            tv_area.setText("请选择区域");
        }
        RxViewAction.clickNoDouble(ll_area).subscribe(unused -> {
            HhLog.e("areaStrList " + areaStrList.toString());
            if(areaStrList.size() == 0){
                HhLog.e("areaStrList 0 " + areaStrList.size());
                Toast.makeText(this, "区域信息加载异常，请重新打开此页面", Toast.LENGTH_SHORT).show();
                return;
            }
            showAreaDialog();
        });
    }

    private void showAreaDialog() {
        View areaView = LayoutInflater.from(this).inflate(R.layout.dialog_grid_p_c, null);
        wv_area = ((WheelView) areaView.findViewById(R.id.wv_area));
        wv_area.setIsLoop(false);
        wv_area.setItems(areaStrList, areaIndex);

        wv_qu = ((WheelView) areaView.findViewById(R.id.wv_qu));
        wv_qu.setIsLoop(false);
        if(quStrList.size()==0){
            quStrList.add("请选择区域");
        }
        wv_qu.setItems(quStrList, quIndex);
        wv_qu.setVisibility(View.VISIBLE);

        wv_area.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                areaStr = wv_area.getSelectedItem();
                areaIndex = wv_area.getSelectedPosition();
                postGroupId = cityList.get(areaIndex).getNo();
                areaId = cityList.get(areaIndex).getNo();
                currentArea = cityList.get(areaIndex);
                quList = cityList.get(areaIndex).getChildren();
                quIndex = 0;
                quStrList.clear();
                quStr = "请选择区域";
                if(quList.size()==0){
                    quList.add(new AreaModel(quStr,""));
                }
                for (int i = 0; i < quList.size(); i++) {
                    quStrList.add(quList.get(i).getName());
                }
                wv_qu.setItems(quStrList, quIndex);
                //修改显示信息
                tv_area.setText(areaStr);
            }
        });
        wv_qu.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                quStr = wv_qu.getSelectedItem();
                quIndex = wv_qu.getSelectedPosition();
                if(quList.size()==0){
                    return;
                }
                postGroupId = quList.get(quIndex).getNo();
                quId = quList.get(quIndex).getNo();
                //修改显示信息
                if(quStr.contains("请选择")){
                    tv_area.setText(areaStr);
                }else{
                    tv_area.setText(areaStr + quStr);
                }
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请选择区域")
                .setView(areaView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postGroupId = cityList.get(areaIndex).getNo();
                        currentArea = cityList.get(areaIndex);
                        areaStr = wv_area.getSelectedItem();
                        areaIndex = wv_area.getSelectedPosition();
                        areaId = cityList.get(areaIndex).getNo();
                        quStr = wv_qu.getSelectedItem();
                        quIndex = wv_qu.getSelectedPosition();
                        //修改显示信息
                        if(quStr.contains("请选择")){
                            tv_area.setText(areaStr);
                        }else{
                            quId = quList.get(quIndex).getNo();
                            tv_area.setText(areaStr + quStr);
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void initEdit() {
        FlameList flame = new FlameList();
        flame.setId("id");
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "fire/api/planBurnOff/getPlanBurnOffById");
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType", "Internet");
        params.addParameter("id",id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray data = object.getJSONArray("data");
                    List<FlameList> flameLists = new Gson().fromJson(String.valueOf(data), new TypeToken<List<FlameList>>() {
                    }.getType());
                    flameList = flameLists.get(0);

                    et_name.setText(flameList.getName());
                    tv_start.setText(flameList.getStartTime());
                    tv_end.setText(flameList.getEndTime());
                    et_person.setText(flameList.getLeaderName());
                    et_phone.setText(flameList.getLeaderPhone());
                    tv_area.setText(flameList.getAddress());
                    pointFlame = flameList.getPosition();
                    tv_flame.setText("已选择");
                    et_info.setText(flameList.getRemark());

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

    private final int REQ_AREA_CODE = 21;
    private void bind_() {
        RxViewAction.clickNoDouble(tv_commit).subscribe(unused -> {
            if(et_name.getText().toString().isEmpty()){
                Toast.makeText(this, "请输入名称", Toast.LENGTH_SHORT).show();
               return;
            }
//            if(tv_area.getText().toString().contains("选择")){
//                Toast.makeText(this, "请选择区域", Toast.LENGTH_SHORT).show();
//               return;
//            }
            if(tv_start.getText().toString().contains("选择")){
                Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
               return;
            }
            if(tv_end.getText().toString().contains("选择")){
                Toast.makeText(this, "请选择结束时间", Toast.LENGTH_SHORT).show();
               return;
            }
            String startDate_ = tv_start.getText().toString();
            String endDate_ = tv_end.getText().toString();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateStart = null;
            Date dateEnd = null;
            try {
                dateStart = simpleDateFormat.parse(startDate_);
                dateEnd = simpleDateFormat.parse(endDate_);
                if(dateStart.getTime() >= dateEnd.getTime()){
                    Toast.makeText(this, "结束时间不能早于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(et_person.getText().toString().isEmpty()){
                Toast.makeText(this, "请输入责任人", Toast.LENGTH_SHORT).show();
               return;
            }
            if(et_phone.getText().toString().isEmpty()){
                Toast.makeText(this, "请输入联系方式", Toast.LENGTH_SHORT).show();
               return;
            }
            if(!tv_flame.getText().toString().equals("已选择")){
                Toast.makeText(this, "请选择焚烧区域", Toast.LENGTH_SHORT).show();
               return;
            }
            if(et_info.getText().toString().isEmpty()){
                Toast.makeText(this, "请输入描述", Toast.LENGTH_SHORT).show();
               return;
            }
            if(edit){
                commitEditing();
            }else{
                commitAdding();
            }
        });
        RxViewAction.clickNoDouble(ll_start).subscribe(unused -> {
            isChooseStarTime = true;
            showDataDialog();
        });
        RxViewAction.clickNoDouble(ll_end).subscribe(unused -> {
            isChooseStarTime = false;
            showDataDialog();
        });
        RxViewAction.clickNoDouble(ll_flame).subscribe(unused -> {
            Intent intent = new Intent(FlameAddActivity.this,FlameAreaActivity.class);
            intent.putExtra("point",pointFlame);
            startActivityForResult(intent,REQ_AREA_CODE);
        });
    }

    private String pointFlame;
    private String centre;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_AREA_CODE && data != null){
            pointFlame = data.getStringExtra("point");
            centre = data.getStringExtra("centre");
            tv_flame.setText("已选择");
        }
    }

    private StringBuffer date;
    private StringBuffer endDate;
    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FlameAddActivity.this);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                if (endDate.length() > 0) { //清除上次记录的日期
                    endDate.delete(0, endDate.length());
                }
                if (isChooseStarTime) {
                    date.append(String.valueOf(year));
                    if (month < 9) {
                        date.append("-0").append(String.valueOf(month + 1));
                    } else {
                        date.append("-").append(String.valueOf(month + 1));
                    }
                    if (day < 10) {
                        date.append("-0").append(String.valueOf(day));
                    } else {
                        date.append("-").append(String.valueOf(day));
                    }
                    tv_start.setText(date);
                } else {
                    date.append(String.valueOf(year));
                    if (month < 9) {
                        date.append("-0").append(String.valueOf(month + 1));
                    } else {
                        date.append("-").append(String.valueOf(month + 1));
                    }
                    if (day < 10) {
                        date.append("-0").append(String.valueOf(day));
                    } else {
                        date.append("-").append(String.valueOf(day));
                    }
                    tv_end.setText(date);
                }
                dialog.dismiss();
                showTimeDialog();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(FlameAddActivity.this, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH);
        int day1 = date.get(Calendar.DATE);
        String endData = year1 - 10 + "-" + month1 + "-" + day1;
        Log.i(TAG, "showDataDialog: " + endData);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(endData);
        } catch (ParseException e) {

        }
        long starTimre = date2.getTime();


        long endTimre = System.currentTimeMillis();

        //datePicker.setMaxDate(endTimre);
        //datePicker.setMinDate(starTimre);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month, day, this);
    }

    /**
     * 日期选择控件
     */
    private void showTimeDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(FlameAddActivity.this);
        builder1.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isChooseStarTime) {
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_start.append(" 0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_start.append(" 0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_start.append(" " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_start.append(" " + chooseHour + ":" + chooseMinute + ":00");
                    }
                } else {
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_end.append(" 0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_end.append(" 0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_end.append(" " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_end.append(" " + chooseHour + ":" + chooseMinute + ":00");
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
        View dialogView = View.inflate(FlameAddActivity.this, R.layout.dialog_time, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR);
        int minute = date.get(Calendar.MINUTE);

        timePicker.setIs24HourView(true);   //设置时间显示为24小时

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);  //设置当前小时
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setMinute(minute); //设置当前分（0-59）
        }

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

    private int year;
    private int month;
    private int day;
    public int chooseHour;
    public int chooseMinute;
    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);

    }

    private void commitAdding() {
        FlameList flame = new FlameList();
        flame.setName(et_name.getText().toString());
        flame.setPosition(pointFlame);
        flame.setCentre(centre);
        flame.setStartTime(tv_start.getText().toString());
        flame.setEndTime(tv_end.getText().toString());
        flame.setLeaderName(et_person.getText().toString());
        flame.setLeaderPhone(et_phone.getText().toString());
        flame.setAddress(tv_area.getText().toString());
        flame.setRemark(et_info.getText().toString());
        flame.setProvinceCode("");
        flame.setProvinceName("");
        flame.setCityCode(areaId);
        flame.setCityName(areaStr);
        flame.setCountyCode(quId);
        flame.setCountyName(quStr);
        flame.setGridNo((String) SPUtils.get(this, SPValue.gridNo,""));
        flame.setGroupId((String) SPUtils.get(this,SPValue.groupId,""));
        flame.setLeaderId("");
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "fire/api/planBurnOff/savePlanBurnOff");
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType", "Internet");
        params.setBodyContent(new Gson().toJson(flame));
        Log.e(TAG, "commitAdding: flame = " + new Gson().toJson(flame) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code") == 200 ){
                        Toast.makeText(FlameAddActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

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
    private void commitEditing() {
        FlameList flame = new FlameList();
        flame.setId(id);
        flame.setName(et_name.getText().toString());
        flame.setPosition(pointFlame);
        flame.setCentre(centre);
        flame.setStartTime(tv_start.getText().toString());
        flame.setEndTime(tv_end.getText().toString());
        flame.setLeaderName(et_person.getText().toString());
        flame.setLeaderPhone(et_phone.getText().toString());
        flame.setAddress(tv_area.getText().toString());
        flame.setRemark(et_info.getText().toString());
        flame.setProvinceCode("");
        flame.setProvinceName("");
        flame.setCityCode(areaId);
        flame.setCityName(areaStr);
        flame.setCountyCode(quId);
        flame.setCountyName(quStr);
        flame.setGridNo((String) SPUtils.get(this, SPValue.gridNo,""));
        flame.setGroupId((String) SPUtils.get(this,SPValue.groupId,""));
        flame.setLeaderId("");
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "fire/api/planBurnOff/updatePlanBurnOff");
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType", "Internet");
        params.setBodyContent(new Gson().toJson(flame));
        x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code") == 200 ){
                        Toast.makeText(FlameAddActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

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

    private void init_() {
        date = new StringBuffer();
        endDate = new StringBuffer();
        initDateTime();
        et_name = findViewById(R.id.et_name);
        ll_area = findViewById(R.id.ll_area);
        ll_area.setVisibility(View.GONE);
        tv_area = findViewById(R.id.tv_area);
        ll_start = findViewById(R.id.ll_start);
        tv_start = findViewById(R.id.tv_start);
        ll_end = findViewById(R.id.ll_end);
        tv_end = findViewById(R.id.tv_end);
        tv_person = findViewById(R.id.tv_person);
        et_person = findViewById(R.id.et_person);
        tv_phone = findViewById(R.id.tv_phone);
        et_phone = findViewById(R.id.et_phone);
        ll_flame = findViewById(R.id.ll_flame);
        tv_flame = findViewById(R.id.tv_flame);
        et_info = findViewById(R.id.et_info);
        tv_commit = findViewById(R.id.tv_commit);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }
}