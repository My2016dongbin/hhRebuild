package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityModelBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivitySignStatisticsBinding;
import com.haohai.platform.fireforestplatform.old.SignMonthActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.SignModel;
import com.haohai.platform.fireforestplatform.ui.multitype.SignModelViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskListViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ModelViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.SignStatisticsViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class SignStatisticsActivity extends BaseLiveActivity<ActivitySignStatisticsBinding, SignStatisticsViewModel> implements SignModelViewBinder.OnItemClickListener, DatePicker.OnDateChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().postData();
    }

    private void init_() {
        binding.topBar.title.setText("考勤统计");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.smart.setRefreshHeader(new ClassicsHeader(this));
        //binding.smart.setRefreshFooter(new ClassicsFooter(this));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.smart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().all_person = 0;
                obtainViewModel().sign_person = 0;
                obtainViewModel().walk_distance = 0;
                obtainViewModel().sign_count = 0;

                obtainViewModel().page = 1;
                obtainViewModel().postData();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page++;
                obtainViewModel().postData();
                refreshLayout.finishLoadMore(1000);
            }
        });

        SignModelViewBinder binder = new SignModelViewBinder(this);
        binder.setListener(this);
        obtainViewModel().adapter.register(SignModel.class, binder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);

        //initDate
        Calendar calendar = Calendar.getInstance();
        obtainViewModel().year = calendar.get(Calendar.YEAR);
        obtainViewModel().month = calendar.get(Calendar.MONTH);
        obtainViewModel().day = calendar.get(Calendar.DAY_OF_MONTH);
        obtainViewModel().date.append(calendar.get(Calendar.YEAR));
        if (calendar.get(Calendar.MONTH) <= 9) {
            obtainViewModel().date.append("-0").append((calendar.get(Calendar.MONTH) + 1));
        } else {
            obtainViewModel().date.append("-").append((calendar.get(Calendar.MONTH) + 1));
        }
        obtainViewModel().date.append("-01 00:00:00");
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,1);
        calendar.add(Calendar.DAY_OF_MONTH,-1);

        obtainViewModel().endDate.append(calendar.get(Calendar.YEAR));
        if (calendar.get(Calendar.MONTH) <= 9) {
            obtainViewModel().endDate.append("-0").append((calendar.get(Calendar.MONTH) + 1));
        } else {
            obtainViewModel().endDate.append("-").append((calendar.get(Calendar.MONTH) + 1));
        }
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            obtainViewModel().endDate.append("-0").append(calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            obtainViewModel().endDate.append("-").append(calendar.get(Calendar.DAY_OF_MONTH));
        }
        obtainViewModel().endDate.append(" 00:00:00");
        binding.date.setText("本月");
    }

    private void bind_() {
        binding.flDate.setOnClickListener(v -> {
            showDataDialog(true);
        });
        binding.flFilter.setOnClickListener(v -> {
            TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(SignStatisticsActivity.this.getResources().getColor(R.color.c7));
            MessageDialog.show("考勤状态", "","全部","无考勤","有考勤")
                    .setButtonOrientation(LinearLayout.VERTICAL)
                    .setOkTextInfo(okTextInfo)
                    .setCancelTextInfo(okTextInfo)
                    .setOtherTextInfo(okTextInfo)
                    .setOkButtonClickListener((dialog, v1) -> {
                        obtainViewModel().attendanceStatus = "";
                        binding.filter.setText("全部");
                        obtainViewModel().page = 1;

                        obtainViewModel().all_person = 0;
                        obtainViewModel().sign_person = 0;
                        obtainViewModel().walk_distance = 0;
                        obtainViewModel().sign_count = 0;
                        obtainViewModel().postData();
                        return false;
                    })
                    .setCancelButtonClickListener((dialog, v1) -> {
                        obtainViewModel().attendanceStatus = "0";
                        binding.filter.setText("无考勤");
                        obtainViewModel().page = 1;

                        obtainViewModel().all_person = 0;
                        obtainViewModel().sign_person = 0;
                        obtainViewModel().walk_distance = 0;
                        obtainViewModel().sign_count = 0;
                        obtainViewModel().postData();
                        return false;
                    })
                    .setOtherButtonClickListener((dialog, v1) -> {
                        obtainViewModel().attendanceStatus = "1";
                        binding.filter.setText("有考勤");
                        obtainViewModel().page = 1;

                        obtainViewModel().all_person = 0;
                        obtainViewModel().sign_person = 0;
                        obtainViewModel().walk_distance = 0;
                        obtainViewModel().sign_count = 0;
                        obtainViewModel().postData();
                        return false;
                    })
                    .setCancelable(true);
        });
    }


    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        obtainViewModel().year = year;
        obtainViewModel().month = monthOfYear;
        obtainViewModel().day = dayOfMonth;
    }

    /**
     * 日期选择控件
     */
    private void showDataDialog(boolean start) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(start){
                    if (obtainViewModel().date.length() > 0) { //清除上次记录的日期
                        obtainViewModel().date.delete(0, obtainViewModel().date.length());
                    }
                    obtainViewModel().date.append(obtainViewModel().year);
                    if (obtainViewModel().month <= 9) {
                        obtainViewModel().date.append("-0").append((obtainViewModel().month + 1));
                    } else {
                        obtainViewModel().date.append("-").append((obtainViewModel().month + 1));
                    }
                    if (obtainViewModel().day < 10) {
                        obtainViewModel().date.append("-0").append(obtainViewModel().day);
                    } else {
                        obtainViewModel().date.append("-").append(obtainViewModel().day);
                    }
                    obtainViewModel().date.append(" 00:00:00");
                    showDataDialog(false);
                }else{
                    if (obtainViewModel().endDate.length() > 0) { //清除上次记录的日期
                        obtainViewModel().endDate.delete(0, obtainViewModel().endDate.length());
                    }
                    obtainViewModel().endDate.append(obtainViewModel().year);
                    if (obtainViewModel().month <= 9) {
                        obtainViewModel().endDate.append("-0").append((obtainViewModel().month + 1));
                    } else {
                        obtainViewModel().endDate.append("-").append((obtainViewModel().month + 1));
                    }
                    if (obtainViewModel().day < 10) {
                        obtainViewModel().endDate.append("-0").append(obtainViewModel().day);
                    } else {
                        obtainViewModel().endDate.append("-").append(obtainViewModel().day);
                    }
                    obtainViewModel().endDate.append(" 23:59:59");
                    binding.date.setText(parse9(obtainViewModel().date.toString()) + "\n~\n" + parse9(obtainViewModel().endDate.toString()));
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
        datePicker.init(obtainViewModel().year, obtainViewModel().month, obtainViewModel().day, this);
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
    protected ActivitySignStatisticsBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_sign_statistics);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public SignStatisticsViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(SignStatisticsViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        //天气数据
        obtainViewModel().updateState.observe(this, integer -> upDateUi());
    }

    private void upDateUi() {
        binding.hlUser.setText(obtainViewModel().all_person+"");
        binding.signUser.setText(obtainViewModel().sign_person+"");
        binding.xhCount.setText(obtainViewModel().sign_count+"");
        binding.xhDistance.setText(obtainViewModel().walk_distance+"");
    }

    @Override
    public void onItemClick(SignModel signModel) {
        HhLog.e("onItemClick");
        Intent intent = new Intent(this,SignMonthActivity.class);
        intent.putExtra("id",signModel.getUserId());
        intent.putExtra("name",signModel.getName());
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}