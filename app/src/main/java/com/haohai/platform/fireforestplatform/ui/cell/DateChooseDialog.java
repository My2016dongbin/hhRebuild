package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogGridChooseBinding;
import com.haohai.platform.fireforestplatform.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateChooseDialog extends Dialog implements DatePicker.OnDateChangedListener {

    private final Context context;
    private GridChooseDialogListener dialogListener;
    private final DialogGridChooseBinding binding;
    private List<String> startHourList = new ArrayList<>();
    private List<String> startMinuteList = new ArrayList<>();
    private List<String> endHourList = new ArrayList<>();
    private List<String> endMinuteList = new ArrayList<>();
    private int startHourIndex = 0;
    private int startMinuteIndex = 0;
    private int endHourIndex = 0;
    private int endMinuteIndex = 0;
    public StringBuffer date = new StringBuffer();//"2023-12-01"
    public int year = 2023;
    public int month = 12;
    public int day = 1;
    public String id = "";

    public DateChooseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_choose_grid, null, false);
        setContentView(binding.getRoot());
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDialogListener(GridChooseDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    /**
     * 日期选择控件
     */
    private void showDataDialog() {
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
                binding.tvDate.setText(date);
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(context, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH)+1;
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
            Log.e("TAG", "e: " + e.getMessage() );
        }
        long startTime = date_s.getTime();
        long endTime = date_e.getTime();

        datePicker.setMaxDate(endTime);
        datePicker.setMinDate(startTime);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month-1, day, this);
    }

    private void bind_() {
        binding.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataDialog();
            }
        });
        binding.confirm.setOnClickListener(v -> {
            if(binding.tvDate.getText().toString().contains("选择日期")){
                Toast.makeText(context, "请选择日期", Toast.LENGTH_SHORT).show();
                return;
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startStr = year + "-" + month + "-" + day + " " + startHourList.get(startHourIndex) + ":" + startMinuteList.get(startMinuteIndex) + ":00";
            String endStr = year + "-" + month + "-" + day + " " + endHourList.get(endHourIndex) + ":" + endMinuteList.get(endMinuteIndex) + ":00";
            try {
                Date startDate = dateFormat.parse(startStr);
                Date endDate = dateFormat.parse(endStr);
                dialogListener.onDateChoose(startDate.getTime(),endDate.getTime(),id);
                dismiss();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        binding.reset.setOnClickListener(v -> {
            startHourIndex = 0;
            binding.startHour.setItems(startHourList, startHourIndex);
            dismiss();
        });
    }

    @SuppressLint("SetTextI18n")
    private void init_() {
        initMenus();

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        binding.tvDate.setText(year+ "-" + new CommonUtils().parseZero(month) + "-" + new CommonUtils().parseZero(day));

        binding.startHour.setIsLoop(true);
        binding.startHour.setItems(startHourList, startHourIndex);
        binding.startHour.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                startHourIndex = selectedIndex;
            }
        });
        binding.startMinute.setIsLoop(true);
        binding.startMinute.setItems(startMinuteList, startMinuteIndex);
        binding.startMinute.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                startMinuteIndex = selectedIndex;
            }
        });
        binding.endHour.setIsLoop(true);
        binding.endHour.setItems(endHourList, endHourIndex);
        binding.endHour.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                endHourIndex = selectedIndex;
            }
        });
        binding.endMinute.setIsLoop(true);
        binding.endMinute.setItems(endMinuteList, endMinuteIndex);
        binding.endMinute.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                endMinuteIndex = selectedIndex;
            }
        });
    }

    private void initMenus() {
        startHourList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String str;
            if(i < 10){
                str = "0" + i;
            }else{
                str = "" + i;
            }
            startHourList.add(str);
        }
        endHourList = new ArrayList<>();
        endHourList.addAll(startHourList);

        startMinuteList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            String str;
            if(i < 10){
                str = "0" + i;
            }else{
                str = "" + i;
            }
            startMinuteList.add(str);
        }
        endMinuteList = new ArrayList<>();
        endMinuteList.addAll(startMinuteList);

    }

    @Override
    public void onDateChanged(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
        year = years;
        month = monthOfYear+1;
        day = dayOfMonth;
    }

    public interface GridChooseDialogListener{
        void onGridChooseDialogRefresh();
        void onDateChoose(long start,long end,String id);
    }
}
