package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityWorkerBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.WorkerDetail;
import com.haohai.platform.fireforestplatform.ui.cell.WorkerDetailDialog;
import com.haohai.platform.fireforestplatform.ui.viewmodel.WorkerViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkerActivity extends BaseLiveActivity<ActivityWorkerBinding, WorkerViewModel> implements CalendarView.OnCalendarSelectListener, CalendarView.OnMonthChangeListener, CalendarView.OnYearChangeListener, WorkerDetailDialog.WorkerDetailDialogListener {
    private WorkerDetailDialog workerDetailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String gridNo = intent.getStringExtra("gridNo");
        if(gridNo==null || gridNo.isEmpty()){
            gridNo = (String) SPUtils.get(this, SPValue.gridNo,"");
        }
        obtainViewModel().gridNo = gridNo;
        init_();
        bind_();
    }

    @SuppressLint("SetTextI18n")
    private void init_() {
        binding.topBar.title.setText("值班日历");

        Intent intent = getIntent();
        int year = intent.getIntExtra("year", binding.calendarView.getCurYear());
        obtainViewModel().currentYear = year;
        int month = intent.getIntExtra("month", binding.calendarView.getCurMonth());
        monthChoose(month);
        obtainViewModel().currentMonth = month;
        int day = intent.getIntExtra("day", binding.calendarView.getCurDay());
        obtainViewModel().currentDay = day;
        binding.calendarView.scrollToCalendar(obtainViewModel().currentYear,obtainViewModel().currentMonth,obtainViewModel().currentDay,true);
        binding.calendarView.setOnCalendarSelectListener(this);
        binding.calendarView.setOnMonthChangeListener(this);
        binding.calendarView.setOnYearChangeListener(this);
        binding.tvYear.setText(String.valueOf(year));
        obtainViewModel().mYear = year;
        binding.tvMonthDay.setText(month + "月" + day + "日");
        binding.tvLunar.setText("");
        binding.tvCurrentDay.setText(day+"");

        binding.calendarView.setMinimumHeight(1000);
        obtainViewModel().postData();

        initWorkerDetailDialog();
    }

    private void bind_() {
        binding.tvToday.setOnClickListener(v -> binding.calendarView.scrollToCurrent());
        binding.tvMonthDay.setOnClickListener(v -> {
            /*if (!binding.calendarLayout.isExpand()) {
                binding.calendarLayout.expand();
                return;
            }*/
            binding.calendarView.showYearSelectLayout(obtainViewModel().mYear);
            binding.tvLunar.setVisibility(View.GONE);
            binding.tvYear.setVisibility(View.GONE);
            binding.tvMonthDay.setText(String.valueOf(obtainViewModel().mYear));
        });
    }

    private void initWorkerDetailDialog() {
        workerDetailDialog = new WorkerDetailDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = workerDetailDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        workerDetailDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        workerDetailDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            workerDetailDialog.create();
        }
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }
    private Calendar calendar;
    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        binding.tvLunar.setVisibility(View.VISIBLE);
        binding.tvYear.setVisibility(View.VISIBLE);
        binding.tvMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        binding.tvYear.setText(String.valueOf(calendar.getYear()));
        binding.tvLunar.setText(calendar.getLunar());
        obtainViewModel().mYear = calendar.getYear();

        this.calendar = calendar;
        if(isClick){
            showDetailDialog();
        }
    }

    void showDetailDialog(){
        if(calendar==null){
            return;
        }
        int year = calendar.getYear();
        int month = calendar.getMonth();
        int day = calendar.getDay();
        WorkerDetail workerDetails = new WorkerDetail();
        for (int i = 0; i < CommonData.workerDetails.size(); i++) {
            WorkerDetail workerDetail = CommonData.workerDetails.get(i);
            List<WorkerDetail.Worker> list = workerDetail.getList();
            if(list!=null && !list.isEmpty()){
                WorkerDetail.Worker worker_ = list.get(0);
                try {
                    Date date_ = new Date(Long.parseLong(worker_.getDutyDate()));
                    java.util.Calendar calendar_ = java.util.Calendar.getInstance();
                    calendar_.setTime(date_);
                    int year_ = calendar_.get(java.util.Calendar.YEAR);
                    int month_ = calendar_.get(java.util.Calendar.MONTH)+1;
                    int day_ = calendar_.get(java.util.Calendar.DAY_OF_MONTH);
//                    HhLog.e(year + "-" + month + "-" + day);
//                    HhLog.e(year_ + "+" + month_ + "+" + day_);
                    if(year == year_ && month == month_ && day == day_){
                        workerDetails = workerDetail;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        workerDetailDialog.setWorkerDetail(workerDetails);
        if(workerDetails.getList()!=null){
            workerDetailDialog.show();
        }
    }

    @Override
    protected ActivityWorkerBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_worker);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public WorkerViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(WorkerViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().workerDetails.observe(this, this::workerDetailsChanged);
        obtainViewModel().monthChoose.observe(this, integer -> {
            binding.calendarView.scrollToCalendar(obtainViewModel().currentYear,integer,1);
            monthChoose(integer);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void monthChoose(int month) {
        binding.month1.setTextColor(getResources().getColor(R.color.c7));
        binding.month1.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month2.setTextColor(getResources().getColor(R.color.c7));
        binding.month2.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month3.setTextColor(getResources().getColor(R.color.c7));
        binding.month3.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month4.setTextColor(getResources().getColor(R.color.c7));
        binding.month4.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month5.setTextColor(getResources().getColor(R.color.c7));
        binding.month5.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month6.setTextColor(getResources().getColor(R.color.c7));
        binding.month6.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month7.setTextColor(getResources().getColor(R.color.c7));
        binding.month7.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month8.setTextColor(getResources().getColor(R.color.c7));
        binding.month8.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month9.setTextColor(getResources().getColor(R.color.c7));
        binding.month9.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month10.setTextColor(getResources().getColor(R.color.c7));
        binding.month10.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month11.setTextColor(getResources().getColor(R.color.c7));
        binding.month11.setBackground(getResources().getDrawable(R.drawable.white_circle));
        binding.month12.setTextColor(getResources().getColor(R.color.c7));
        binding.month12.setBackground(getResources().getDrawable(R.drawable.white_circle));
        if(month == 1){
            binding.month1.setTextColor(getResources().getColor(R.color.white));
            binding.month1.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.fullScroll(View.FOCUS_LEFT);
        }
        if(month == 2){
            binding.month2.setTextColor(getResources().getColor(R.color.white));
            binding.month2.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.fullScroll(View.FOCUS_LEFT);
        }
        if(month == 3){
            binding.month3.setTextColor(getResources().getColor(R.color.white));
            binding.month3.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.fullScroll(View.FOCUS_LEFT);
        }
        if(month == 4){
            binding.month4.setTextColor(getResources().getColor(R.color.white));
            binding.month4.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.fullScroll(View.FOCUS_LEFT);
        }
        if(month == 5){
            binding.month5.setTextColor(getResources().getColor(R.color.white));
            binding.month5.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.scrollTo(400,0);
        }
        if(month == 6){
            binding.month6.setTextColor(getResources().getColor(R.color.white));
            binding.month6.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.scrollTo(500,0);
        }
        if(month == 7){
            binding.month7.setTextColor(getResources().getColor(R.color.white));
            binding.month7.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.scrollTo(650,0);
        }
        if(month == 8){
            binding.month8.setTextColor(getResources().getColor(R.color.white));
            binding.month8.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.scrollTo(750,0);
        }
        if(month == 9){
            binding.month9.setTextColor(getResources().getColor(R.color.white));
            binding.month9.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.fullScroll(View.FOCUS_RIGHT);
        }
        if(month == 10){
            binding.month10.setTextColor(getResources().getColor(R.color.white));
            binding.month10.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.fullScroll(View.FOCUS_RIGHT);
        }
        if(month == 11){
            binding.month11.setTextColor(getResources().getColor(R.color.white));
            binding.month11.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.fullScroll(View.FOCUS_RIGHT);
        }
        if(month == 12){
            binding.month12.setTextColor(getResources().getColor(R.color.white));
            binding.month12.setBackground(getResources().getDrawable(R.drawable.theme_circle));
            binding.scroll.fullScroll(View.FOCUS_RIGHT);
        }
    }
    private void workerDetailsChanged(List<WorkerDetail> workerDetails) {
        CommonData.workerDetails = workerDetails;
        binding.calendarView.update();
        showDetailDialog();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMonthChange(int year, int month) {
        obtainViewModel().currentYear = year;
        obtainViewModel().currentMonth = month;
        monthChoose(month);
        Calendar calendar = binding.calendarView.getSelectedCalendar();
        binding.tvLunar.setVisibility(View.VISIBLE);
        binding.tvYear.setVisibility(View.VISIBLE);
        binding.tvMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        binding.tvYear.setText(String.valueOf(calendar.getYear()));
        binding.tvLunar.setText(calendar.getLunar());
        obtainViewModel().mYear = calendar.getYear();

        obtainViewModel().postData();
    }

    @Override
    public void onYearChange(int year) {
        binding.tvMonthDay.setText(String.valueOf(year));
    }

    @Override
    public void onWorkerDetailDialogRefresh() {

    }
}