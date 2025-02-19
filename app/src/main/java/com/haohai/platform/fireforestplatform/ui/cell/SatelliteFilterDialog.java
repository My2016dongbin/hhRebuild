package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.EmergencyFilterBinding;
import com.haohai.platform.fireforestplatform.databinding.SatelliteFilterBinding;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyFilter;
import com.haohai.platform.fireforestplatform.ui.bean.SatelliteFilter;
import com.haohai.platform.fireforestplatform.ui.bean.TypeBean;
import com.haohai.platform.fireforestplatform.ui.bean.TypeTree;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import okhttp3.Call;

public class SatelliteFilterDialog extends Dialog implements DatePicker.OnDateChangedListener {

    private final Context context;
    private SatelliteFilterListener listener;
    private final SatelliteFilterBinding binding;
    public StringBuffer date = new StringBuffer();//"2023-12-01"
    public int year = 2023;
    public int month = 12;
    public int day = 1;
    public int chooseHour = 12;
    public int chooseMinute = 30;
    public String startTime = "";
    public String endTime = "";
    private int filterState = 0;//0按时间分类  1按编号分类
    public List<String> flowList = new ArrayList<>();

    public SatelliteFilterDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_satellite_filter, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(SatelliteFilterListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void bind_() {
        binding.reset.setOnClickListener(v -> {
            binding.flow.getAdapter().setSelectedList(0);
            binding.start.setTextColor(context.getResources().getColor(R.color.c4));
            binding.mid.setTextColor(context.getResources().getColor(R.color.c4));
            binding.end.setTextColor(context.getResources().getColor(R.color.c4));
            binding.start.setBackground(context.getResources().getDrawable(R.drawable.c_conner));
            binding.end.setBackground(context.getResources().getDrawable(R.drawable.c_conner));
            binding.start.setText("请选择开始时间");
            binding.end.setText("请选择结束时间");
            binding.start.setClickable(false);
            binding.end.setClickable(false);
            startTime = "";
            endTime = "";
            filterState = 0;
            updateFilter();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            endTime = format.format(c.getTime()).replace(" ", "T");
            c.add(Calendar.HOUR, - 24);
            startTime = format.format(c.getTime()).replace(" ", "T");
            Toast.makeText(context, "已重置", Toast.LENGTH_SHORT).show();
        });
        binding.close.setOnClickListener(v -> {
            dismiss();
            listener.onSatelliteFilterDismiss();
        });
        binding.dismiss.setOnClickListener(v -> {
            dismiss();
            listener.onSatelliteFilterDismiss();
        });
        binding.confirm.setOnClickListener(v -> {
            dismiss();
            SatelliteFilter filter = new SatelliteFilter();
            filter.setStartTime(startTime);
            filter.setEndTime(endTime);
            filter.setFilterState(filterState);
            listener.onSatelliteFilterResult(filter);
        });

        binding.flow.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                if(selectPosSet.contains(5)){
                    binding.start.setTextColor(context.getResources().getColor(R.color.c6));
                    binding.mid.setTextColor(context.getResources().getColor(R.color.c6));
                    binding.end.setTextColor(context.getResources().getColor(R.color.c6));
                    binding.start.setBackground(context.getResources().getDrawable(R.drawable.c2_conner));
                    binding.end.setBackground(context.getResources().getDrawable(R.drawable.c2_conner));
                    binding.start.setText("请选择开始时间");
                    binding.end.setText("请选择结束时间");
                    startTime = "";
                    endTime = "";
                    binding.start.setClickable(true);
                    binding.end.setClickable(true);
                }else{
                    binding.start.setTextColor(context.getResources().getColor(R.color.c4));
                    binding.mid.setTextColor(context.getResources().getColor(R.color.c4));
                    binding.end.setTextColor(context.getResources().getColor(R.color.c4));
                    binding.start.setBackground(context.getResources().getDrawable(R.drawable.c_conner));
                    binding.end.setBackground(context.getResources().getDrawable(R.drawable.c_conner));
                    binding.start.setText("请选择开始时间");
                    binding.end.setText("请选择结束时间");
                    binding.start.setClickable(false);
                    binding.end.setClickable(false);
                    if(selectPosSet.contains(0)){
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar c = Calendar.getInstance();
                        endTime = format.format(c.getTime()).replace(" ", "T");
                        c.add(Calendar.HOUR, - 24);
                        startTime = format.format(c.getTime()).replace(" ", "T");
                    }
                    if(selectPosSet.contains(1)){
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar c = Calendar.getInstance();
                        endTime = format.format(c.getTime()).replace(" ", "T");
                        c.add(Calendar.HOUR, - 72);
                        startTime = format.format(c.getTime()).replace(" ", "T");
                    }
                    if(selectPosSet.contains(2)){
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar c = Calendar.getInstance();
                        endTime = format.format(c.getTime()).replace(" ", "T");
                        c.add(Calendar.HOUR, - 120);
                        startTime = format.format(c.getTime()).replace(" ", "T");
                    }
                    if(selectPosSet.contains(3)){
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar c = Calendar.getInstance();
                        endTime = format.format(c.getTime()).replace(" ", "T");
                        c.add(Calendar.HOUR, - 168);
                        startTime = format.format(c.getTime()).replace(" ", "T");
                    }
                    if(selectPosSet.contains(4)){
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar c = Calendar.getInstance();
                        endTime = format.format(c.getTime()).replace(" ", "T");
                        c.add(Calendar.HOUR, - 720);
                        startTime = format.format(c.getTime()).replace(" ", "T");
                    }
                }
                
            }
        });
        binding.start.setOnClickListener(v -> {
            showDataDialog(true);
        });
        binding.end.setOnClickListener(v -> {
            showDataDialog(false);
        });
        binding.llFilterNo.setOnClickListener(v -> {
            filterState = 1;
            updateFilter();
        });
        binding.llFilterTime.setOnClickListener(v -> {
            filterState = 0;
            updateFilter();
        });

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateFilter() {
        //0按时间分类  1按编号分类
        if(filterState == 0){
            binding.statusNo.setImageDrawable(context.getResources().getDrawable(R.drawable.no));
            binding.statusTime.setImageDrawable(context.getResources().getDrawable(R.drawable.yes));
        }else{
            binding.statusNo.setImageDrawable(context.getResources().getDrawable(R.drawable.yes));
            binding.statusTime.setImageDrawable(context.getResources().getDrawable(R.drawable.no));
        }
    }

    private void init_() {
        initLocalData();
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);

        /*flowList.add("1小时内");
        flowList.add("3小时内");*/
        flowList.add("1天内");
        flowList.add("3天内");
        flowList.add("5天内");
        flowList.add("7天内");
        flowList.add("30天内");
        flowList.add("自定义");
        binding.flow.setAdapter(new TagAdapter<String>(flowList)
        {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.tv,
                        binding.flow, false);
                tv.setBackground(context.getResources().getDrawable(R.drawable.flow_selector));
                tv.setTextColor(context.getResources().getColor(R.color.c6));
                tv.setText(s);
                return tv;
            }
        });

        binding.flow.getAdapter().setSelectedList(0);
        binding.start.setTextColor(context.getResources().getColor(R.color.c4));
        binding.mid.setTextColor(context.getResources().getColor(R.color.c4));
        binding.end.setTextColor(context.getResources().getColor(R.color.c4));
        binding.start.setBackground(context.getResources().getDrawable(R.drawable.c_conner));
        binding.end.setBackground(context.getResources().getDrawable(R.drawable.c_conner));
        binding.start.setText("请选择开始时间");
        binding.end.setText("请选择结束时间");
        startTime = "";
        endTime = "";
        binding.start.setClickable(false);
        binding.end.setClickable(false);
    }

    private void initLocalData() {

    }

    /**
     * 日期选择控件
     */
    private void showDataDialog(boolean start) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("确定", new OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                date.append(year);
                if (month <= 9) {
                    date.append("-0").append((month));
                } else {
                    date.append("-").append((month));
                }
                if (day < 10) {
                    date.append("-0").append(day);
                } else {
                    date.append("-").append(day);
                }
                dialog.dismiss();
                showTimeDialog(start);
                //选择时间
                /*showTimeDialog();*/
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                show();
            }
        });


        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(context, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH) + 1;
        int day1 = date.get(Calendar.DATE);
        String startData = year1 - 10 + "-" + month1 + "-" + day1;
        String endData = year1 + 10 + "-" + month1 + "-" + day1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date_s = null;
        Date date_e = null;
        try {
            date_s = simpleDateFormat.parse(startData);
            date_e = simpleDateFormat.parse(endData);
        } catch (ParseException e) {
            Log.e("TAG", "e: " + e.getMessage());
        }
        long startTime = date_s.getTime();
        long endTime = date_e.getTime();

        datePicker.setMaxDate(endTime);
        datePicker.setMinDate(startTime);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month - 1, day, this);
    }

    /**
     * 日期选择控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTimeDialog(boolean start) {
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder1.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (chooseHour < 10 && chooseMinute < 10) {
                    date.append(" 0" + chooseHour + ":0" + chooseMinute + ":00");
                } else if (chooseHour < 10 && chooseMinute >= 10) {
                    date.append(" 0" + chooseHour + ":" + chooseMinute + ":00");
                } else if (chooseHour >= 10 && chooseMinute < 10) {
                    date.append(" " + chooseHour + ":0" + chooseMinute + ":00");
                } else {
                    date.append(" " + chooseHour + ":" + chooseMinute + ":00");
                }

                if(start){
                    startTime = date.toString();
                    binding.start.setText(date);
                }else{
                    endTime = date.toString();
                    binding.end.setText(date);
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


        final androidx.appcompat.app.AlertDialog timeDialog = builder1.create();
        View dialogView = View.inflate(context, R.layout.dialog_time, null);
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
    public void onDateChanged(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
        year = years;
        month = monthOfYear + 1;
        day = dayOfMonth;
    }

    public interface SatelliteFilterListener {
        void onSatelliteFilterRefresh();

        void onSatelliteFilterResult(SatelliteFilter filter);
        void onSatelliteFilterDismiss();
    }
}
