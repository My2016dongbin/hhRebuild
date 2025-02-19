package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityTaskBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityWorkerListBinding;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.ui.cell.WorkerDetail2Dialog;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.ui.multitype.WorkerDetail;
import com.haohai.platform.fireforestplatform.ui.multitype.WorkerListViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.WorkerListViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class WorkerListActivity extends BaseLiveActivity<ActivityWorkerListBinding, WorkerListViewModel> implements WorkerListViewBinder.OnItemClickListener, DatePicker.OnDateChangedListener, WorkerDetail2Dialog.WorkerDetailDialogListener {
    private WorkerDetail2Dialog workerDetailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        obtainViewModel().id = intent.getStringExtra("id");
        init_();
        bind_();
        obtainViewModel().postData();
    }

    private void initWorkerDetailDialog() {
        workerDetailDialog = new WorkerDetail2Dialog(this, R.style.ActionSheetDialogStyle);
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

    void showDetailDialog(WorkerDetail.Worker worker , int index){
        workerDetailDialog.setWorkerDetail(worker,index);
        workerDetailDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    private void init_() {
        binding.topBar.title.setText("值班日历");
        Calendar calendar = Calendar.getInstance();
        obtainViewModel().year = calendar.get(Calendar.YEAR);
        obtainViewModel().month = calendar.get(Calendar.MONTH)+1;
        obtainViewModel().day = calendar.get(Calendar.DAY_OF_MONTH);
        obtainViewModel().date.append(obtainViewModel().year).append("-").append(CommonUtil.parseZero(obtainViewModel().month)).append("-").append(CommonUtil.parseZero(obtainViewModel().day));
        binding.textDate.setText(obtainViewModel().date);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.monitorFireSmart.setRefreshHeader(new ClassicsHeader(this));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.monitorFireSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().postData();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });

        WorkerListViewBinder workerListViewBinder = new WorkerListViewBinder(this);
        workerListViewBinder.setListener(this);
        obtainViewModel().adapter.register(WorkerDetail.class, workerListViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);

        initWorkerDetailDialog();
    }

    private void bind_() {
        binding.filterDate.setOnClickListener(v -> {
            showDataDialog();
        });
    }

    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (obtainViewModel().date.length() > 0) { //清除上次记录的日期
                    obtainViewModel().date.delete(0, obtainViewModel().date.length());
                }
                obtainViewModel().date.append(obtainViewModel().year);
                if (obtainViewModel().month <= 9) {
                    obtainViewModel().date.append("-0").append((obtainViewModel().month));
                } else {
                    obtainViewModel().date.append("-").append((obtainViewModel().month));
                }
                if (obtainViewModel().day < 10) {
                    obtainViewModel().date.append("-0").append(obtainViewModel().day);
                } else {
                    obtainViewModel().date.append("-").append(obtainViewModel().day);
                }
                dialog.dismiss();
                binding.textDate.setText(obtainViewModel().date);
                obtainViewModel().postData();
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
        datePicker.init(obtainViewModel().year, obtainViewModel().month-1, obtainViewModel().day, this);
    }


    private String parse9(String str) {
        if(str == null){
            return "暂无记录";
        }
        String r = str;
        try{
            r = str.substring(0,10).replace("T"," ");
        }catch (Exception e){
            HhLog.e(e.getMessage());
        }
        return r;
    }


    @Override
    protected ActivityWorkerListBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_worker_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public WorkerListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(WorkerListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }

    @Override
    public void onItemClick(WorkerDetail workerDetail) {
        /*Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("gridNo",workerDetail.getGridNo());
        intent.putExtra("year",obtainViewModel().year);
        intent.putExtra("month",obtainViewModel().month);
        intent.putExtra("day",obtainViewModel().day);
        startActivity(intent);*/
    }

    @Override
    public void onPersonClick(WorkerDetail workerDetail, int index) {
        try{
            List<WorkerDetail.Worker> list = workerDetail.getList();
            WorkerDetail.Worker worker = list.get(index);
            if(!worker.getArrangeName().isEmpty()){
                showDetailDialog(worker,index);
            }
        }catch (Exception e){
            HhLog.e("error onPersonClick " + e.toString());
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        obtainViewModel().year = year;
        obtainViewModel().month = monthOfYear+1;
        obtainViewModel().day = dayOfMonth;
    }

    @Override
    public void onWorkerDetailDialogRefresh() {

    }
}