package com.haohai.platform.fireforestplatform.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityComprehensiveCheckBinding;
import com.haohai.platform.fireforestplatform.old.linyi.CommonUtils;
import com.haohai.platform.fireforestplatform.old.linyi.Grid;
import com.haohai.platform.fireforestplatform.ui.bean.CheckResource;
import com.haohai.platform.fireforestplatform.ui.cell.TypeChooseDialog;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ComprehensiveCheckViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComprehensiveCheckActivity extends BaseLiveActivity<ActivityComprehensiveCheckBinding, ComprehensiveCheckViewModel> implements TypeChooseDialog.TypeChooseDialogListener, DatePicker.OnDateChangedListener {

    private TypeChooseDialog typeChooseDialog;
    private TypeChooseDialog userChooseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        initDateTime();
        obtainViewModel().postData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init_() {
        binding.topBar.title.setText("综合检查");
        binding.topBar.rightImage.setVisibility(View.VISIBLE);
        binding.topBar.rightImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
        CommonData.checkResourceList = new ArrayList<>();
    }

    private void bind_() {
        binding.textTime.setOnClickListener(v -> {
            showDataDialog();
        });
        binding.submit.setOnClickListener(v -> {
            obtainViewModel().title = binding.editTitle.getText().toString();
            obtainViewModel().info = binding.editZz.getText().toString();
            obtainViewModel().endTime = binding.textTime.getText().toString();
            obtainViewModel().submit();
        });
        binding.textGrid.setOnClickListener(v -> {
            if(obtainViewModel().gridList==null || obtainViewModel().gridList.isEmpty()){
                Toast.makeText(this, "网格数据加载中..", Toast.LENGTH_SHORT).show();
                return;
            }
            chooseGrid();
        });
        binding.textZz.setOnClickListener(v -> {
            if(obtainViewModel().userList==null || obtainViewModel().userList.isEmpty()){
                Toast.makeText(this, "当前网格下无人员信息", Toast.LENGTH_SHORT).show();
                return;
            }
            chooseUser();
        });
        binding.checkAdd.setOnClickListener(v -> {
            startActivityForResult(new Intent(this,ComprehensiveAddCheckActivity.class),CommonData.CHECK_REQUEST_CODE);
        });
    }

    private void chooseUser(){
        userChooseDialog = new TypeChooseDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = userChooseDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        userChooseDialog.setDialogListener(this);
        userChooseDialog.setTreeList(parseStringsUser(),obtainViewModel().userIndex,2);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        userChooseDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            userChooseDialog.create();
        }
        userChooseDialog.show();
    }
    private void chooseGrid(){
        typeChooseDialog = new TypeChooseDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = typeChooseDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        typeChooseDialog.setDialogListener(this);
        typeChooseDialog.setTreeList(parseStrings(),obtainViewModel().gridIndex,1);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        typeChooseDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            typeChooseDialog.create();
        }
        typeChooseDialog.show();
    }

    private List<String> parseStringsUser() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < obtainViewModel().userList.size(); i++) {
            list.add(obtainViewModel().userList.get(i).getFullName());
        }
        return list;
    }
    private List<String> parseStrings() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < obtainViewModel().gridList.size(); i++) {
            list.add(obtainViewModel().gridList.get(i).getName());
        }
        return list;
    }

    @Override
    protected ActivityComprehensiveCheckBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_comprehensive_check);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ComprehensiveCheckViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ComprehensiveCheckViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().resourceList.observe(this, this::updateResource);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CommonData.CHECK_REQUEST_CODE && resultCode == 1 ){
            obtainViewModel().resourceList.postValue(CommonData.checkResourceList);
        }
    }

    private void updateResource(List<CheckResource> checkResources) {
        binding.llResource.removeAllViews();
        for (int i = 0; i < checkResources.size(); i++) {
            CheckResource checkResource = checkResources.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.check_resource_item, null);
            TextView title = view.findViewById(R.id.title);
            TextView content = view.findViewById(R.id.content);
            TextView type = view.findViewById(R.id.type);
            ImageView icon = view.findViewById(R.id.icon);
            String url = "http://192.168.1.196:9000/oabucket/下载_20240807627246.jpg";
            try{
                url = checkResource.getImgs().get(0).getImg();
            }catch (Exception e){
                HhLog.e(e.toString());
            }
            Glide.with(ComprehensiveCheckActivity.this)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new GranularRoundedCorners(20,0,0,20)))
                    .into(icon);
            title.setText(checkResource.getName());
            content.setText(checkResource.getDescription());
            type.setText(new CommonUtils().parseType(checkResource.getResourceType()));
            binding.llResource.addView(view);
        }
    }

    @Override
    public void onTypeChooseDialogRefresh() {

    }

    @Override
    public void onTypeChoose(String type, int index, int code) {
        if(code == 1){
            binding.textGrid.setText(type);
            obtainViewModel().gridIndex = index;
            obtainViewModel().getUsers();
        }else if(code == 2){
            binding.textZz.setText(type);
            obtainViewModel().userIndex = index;
        }
    }

    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (obtainViewModel().date.length() > 0) { //清除上次记录的日期
                    obtainViewModel().date.delete(0, obtainViewModel().date.length());
                }
                if (obtainViewModel().endDate.length() > 0) { //清除上次记录的日期
                    obtainViewModel().endDate.delete(0, obtainViewModel().endDate.length());
                }
                obtainViewModel().date.append(obtainViewModel().year);
                if (obtainViewModel().month < 9) {
                    obtainViewModel().date.append("-0").append((obtainViewModel().month + 1));
                } else {
                    obtainViewModel().date.append("-").append((obtainViewModel().month + 1));
                }
                if (obtainViewModel().day < 10) {
                    obtainViewModel().date.append("-0").append(obtainViewModel().day);
                } else {
                    obtainViewModel().date.append("-").append(obtainViewModel().day);
                }
                binding.textTime.setText(obtainViewModel().date);
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
        View dialogView = View.inflate(this, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH);
        int day1 = date.get(Calendar.DATE);
        String endData = year1 + 10 + "-" + month1 + "-" + day1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(endData);
        } catch (ParseException e) {

        }
        long endTimre = date2.getTime();


        long starTimre = System.currentTimeMillis();

        datePicker.setMaxDate(endTimre);
        datePicker.setMinDate(starTimre);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(obtainViewModel().year, obtainViewModel().month, obtainViewModel().day, this);
    }

    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        obtainViewModel().year = calendar.get(Calendar.YEAR);
        obtainViewModel().month = calendar.get(Calendar.MONTH);
        obtainViewModel().day = calendar.get(Calendar.DAY_OF_MONTH);
        obtainViewModel().chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        obtainViewModel().chooseMinute = calendar.get(Calendar.MINUTE);

        defaultDate();
    }

    private void defaultDate(){
        if (obtainViewModel().date.length() > 0) { //清除上次记录的日期
            obtainViewModel().date.delete(0, obtainViewModel().date.length());
        }
        if (obtainViewModel().endDate.length() > 0) { //清除上次记录的日期
            obtainViewModel().endDate.delete(0, obtainViewModel().endDate.length());
        }
        obtainViewModel().date.append(obtainViewModel().year);
        if (obtainViewModel().month < 9) {
            obtainViewModel().date.append("-0").append((obtainViewModel().month + 1));
        } else {
            obtainViewModel().date.append("-").append((obtainViewModel().month + 1));
        }
        if (obtainViewModel().day < 10) {
            obtainViewModel().date.append("-0").append(obtainViewModel().day);
        } else {
            obtainViewModel().date.append("-").append(obtainViewModel().day);
        }
        binding.textTime.setText(obtainViewModel().date);



        if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute < 10) {
            binding.textTime.append("  0" + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
        } else if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute > 10) {
            binding.textTime.append("  0" + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
        } else if (obtainViewModel().chooseHour > 10 && obtainViewModel().chooseMinute < 10) {
            binding.textTime.append("  " + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
        } else {
            binding.textTime.append("  " + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
        }

    }

    /**
     * 日期选择控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTimeDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute < 10) {
                    binding.textTime.append("  0" + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
                } else if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute > 10) {
                    binding.textTime.append("  0" + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
                } else if (obtainViewModel().chooseHour > 10 && obtainViewModel().chooseMinute < 10) {
                    binding.textTime.append("  " + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
                } else {
                    binding.textTime.append("  " + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
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
                obtainViewModel().chooseHour = hourOfDay;
                obtainViewModel().chooseMinute = minute;
            }
        });
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        obtainViewModel().year = year;
        obtainViewModel().month = monthOfYear;
        obtainViewModel().day = dayOfMonth;
    }
}