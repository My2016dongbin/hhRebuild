package com.haohai.platform.fireforestplatform.ui.cell;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteDetailListBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteSearchAdvancedBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SatelliteSearchAdvancedDialog extends Dialog implements DatePicker.OnDateChangedListener {

    private final Context context;
    private SatelliteSearchAdvancedDialogListener dialogListener;
    private final DialogSatelliteSearchAdvancedBinding binding;
    private boolean isStart = false;
    private int year;
    private int month;
    private int day;
    public int chooseHour;
    public int chooseMinute;
    private StringBuffer date;
    private StringBuffer endDate;

    public SatelliteSearchAdvancedDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_satellite_search_advanced, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(SatelliteSearchAdvancedDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {
        binding.start.setOnClickListener(v -> {
            isStart = true;
            showDataDialog();
        });
        binding.end.setOnClickListener(v -> {
            isStart = false;
            showDataDialog();
        });
        binding.reset.setOnClickListener(v -> {
            binding.start.setText("请选择开始时间");
            binding.end.setText("请选择结束时间");
        });
        binding.confirm.setOnClickListener(v -> {
            //条件判断
            if(!binding.start.getText().toString().contains("请选择") && !binding.end.getText().toString().contains("请选择")){
                hide();
                dialogListener.onSatelliteSearchAdvancedDialogRefresh(binding.start.getText().toString().replace("  ","T"),binding.end.getText().toString().replace("  ","T"));
            }else{
                Toast.makeText(context, "请选择查询时间", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init_() {
        date = new StringBuffer();
        endDate = new StringBuffer();
        initDateTime();
    }

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

    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                if (endDate.length() > 0) { //清除上次记录的日期
                    endDate.delete(0, endDate.length());
                }
                if (isStart){
                    date.append(year);
                    if (month < 9){
                        date.append("-0").append((month + 1));
                    }else {
                        date.append("-").append((month + 1));
                    }
                    if (day <10){
                        date.append("-0").append(day);
                    } else {
                        date.append("-").append(day);
                    }
                    binding.start.setText(date);
                }else {
                    date.append(year);
                    if (month < 9){
                        date.append("-0").append((month + 1));
                    }else {
                        date.append("-").append((month + 1));
                    }
                    if (day <10){
                        date.append("-0").append(day);
                    } else {
                        date.append("-").append(day);
                    }
                    binding.end.setText(date);
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
        View dialogView = View.inflate(getContext(), R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH);
        int day1 = date.get(Calendar.DATE);
        String endData = year1 - 10 + "-" + month1 + "-" + day1;
        Log.i("TAG", "showDataDialog: " + endData);
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
        datePicker.init(year, month, day,this);
    }
    /**
     * 日期选择控件
     */
    private void showTimeDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setPositiveButton("设置", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isStart){
                    if (chooseHour < 10 && chooseMinute <10){
                        binding.start.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    }else if (chooseHour < 10 && chooseMinute >10){
                        binding.start.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    }else if (chooseHour > 10 && chooseMinute < 10){
                        binding.start.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    }else {
                        binding.start.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                }else {
                    if (chooseHour < 10 && chooseMinute <10){
                        binding.end.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    }else if (chooseHour < 10 && chooseMinute >10){
                        binding.end.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    }else if (chooseHour > 10 && chooseMinute < 10){
                        binding.end.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    }else {
                        binding.end.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                }

                dialog.dismiss();
            }
        });
        builder1.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog timeDialog = builder1.create();
        View dialogView = View.inflate(getContext(), R.layout.dialog_time, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);   //设置时间显示为24小时
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);  //设置当前小时
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

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }


    public interface SatelliteSearchAdvancedDialogListener{
        void onSatelliteSearchAdvancedDialogRefresh(String startTime,String endTime);
    }
}
