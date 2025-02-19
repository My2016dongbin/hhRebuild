package com.haohai.platform.fireforestplatform.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityAddingDangerBinding;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.old.FireMapActivity;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyFile;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyIndex;
import com.haohai.platform.fireforestplatform.ui.bean.Picture;
import com.haohai.platform.fireforestplatform.ui.bean.TypeBean;
import com.haohai.platform.fireforestplatform.ui.bean.TypeTree;
import com.haohai.platform.fireforestplatform.ui.cell.TypeChooseDialog;
import com.haohai.platform.fireforestplatform.ui.viewmodel.AddingDangerViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.util.TextInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.haohai.platform.fireforestplatform.old.ResourceAddActivity.MAP_REUEST_CODE;
import static com.haohai.platform.fireforestplatform.ui.activity.FireUploadActivity.MAP_REQUEST_CODE;

public class AddingDangerActivity extends BaseLiveActivity<ActivityAddingDangerBinding, AddingDangerViewModel> implements DatePicker.OnDateChangedListener, TypeChooseDialog.TypeChooseDialogListener {

    private static final int REQUEST_CODE_CHOOSE = 23;
    public static final int REQUEST_CODE_VIDEO = 66;
    private boolean edit;
    private boolean read;
    private boolean danger;
    private TypeChooseDialog typeChooseDialog;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edit = getIntent().getBooleanExtra("edit",false);
        read = getIntent().getBooleanExtra("read",false);
        danger = getIntent().getBooleanExtra("danger",false);
        parseDangerView();
        init_();
        bind_();
        obtainViewModel().postData();
        obtainViewModel().postTypeTree();
    }

    @SuppressLint("SetTextI18n")
    private void init_() {
        binding.topBar.title.setText("隐患排查");

        Calendar calendar = Calendar.getInstance();
        obtainViewModel().year = calendar.get(Calendar.YEAR);
        obtainViewModel().month = calendar.get(Calendar.MONTH)+1;
        obtainViewModel().day = calendar.get(Calendar.DAY_OF_MONTH);
        obtainViewModel().chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        obtainViewModel().chooseMinute = calendar.get(Calendar.MINUTE);

        initList();

        if(edit){
            //查询数据
            if(read){
                //只读
                lockUi();
            }
            updateUi();
            binding.textCommit.setText("保存修改");

        }else{
            //新增
            binding.textCommit.setText("保存");

            obtainViewModel().date.append(obtainViewModel().year).append("-").append(CommonUtil.parseZero(obtainViewModel().month)).append("-").append(CommonUtil.parseZero(obtainViewModel().day));
            /*if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute < 10) {
                obtainViewModel().date.append(" 0" + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
            } else if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute >= 10) {
                obtainViewModel().date.append(" 0" + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
            } else if (obtainViewModel().chooseHour >= 10 && obtainViewModel().chooseMinute < 10) {
                obtainViewModel().date.append(" " + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
            } else {
                obtainViewModel().date.append(" " + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
            }*/
            binding.textTime.setText(obtainViewModel().date);
        }

        initPictures();

    }

    private void initList() {
        obtainViewModel().typeList = new ArrayList<>();
        obtainViewModel().typeList.add(new TypeBean("0","安全生产"));
        obtainViewModel().typeList.add(new TypeBean("1","地灾隐患点"));
        obtainViewModel().typeList.add(new TypeBean("2","地灾易发区"));
        obtainViewModel().typeList.add(new TypeBean("3","山洪灾害"));
        obtainViewModel().typeList.add(new TypeBean("4","危旧房"));
        obtainViewModel().typeList.add(new TypeBean("5","气象灾害"));
        obtainViewModel().typeList.add(new TypeBean("6","森林防火"));

        obtainViewModel().levelList = new ArrayList<>();
        obtainViewModel().levelList.add(new TypeBean("5","暂未确认等级"));
        obtainViewModel().levelList.add(new TypeBean("4","Ⅳ级（一般）"));
        obtainViewModel().levelList.add(new TypeBean("3","Ⅲ级（较大）"));
        obtainViewModel().levelList.add(new TypeBean("2","Ⅱ级（重大）"));
        obtainViewModel().levelList.add(new TypeBean("1","Ⅰ级（特别重大）"));
    }

    private void initPictures() {
        if(edit && read){

        }else{
            obtainViewModel().pictureList.add(new Picture(null,null));
        }
        updatePictures();
    }

    @SuppressLint("SetTextI18n")
    private void updatePictures() {
        binding.viewPicture.removeAllViews();
        LayoutInflater mInflater = LayoutInflater.from(this);
        int num = 0;
        for (int m = 0; m < obtainViewModel().pictureList.size(); m++) {
            Picture picture = obtainViewModel().pictureList.get(m);
            View mView = mInflater.inflate(R.layout.item_picture, null);
            ImageView image = mView.findViewById(R.id.image);
            ImageView imageDelete = mView.findViewById(R.id.image_delete);

            if(picture.getUri() == null && picture.getUrl() == null){
                //新增 默认logo
                imageDelete.setVisibility(View.GONE);

            }else if(picture.getUrl() != null){
                //显示线上图片Url
                Glide.with(this).load(picture.getUrl()).into(image);
                imageDelete.setVisibility(View.VISIBLE);
                num++;
            }else{
                //更换 显示上次选择的Uri
                Glide.with(this).load(picture.getUri()).into(image);
                imageDelete.setVisibility(View.VISIBLE);
                num++;
            }

            image.setOnClickListener(v -> {
                if(picture.getUri() == null && picture.getUrl() == null){
                    //新增
                    addPicture(null);
                }else if(picture.getUrl() != null){
                    //不支持更换线上图片
                    Toast.makeText(this, "已上传图片暂不支持更换", Toast.LENGTH_SHORT).show();
                }else{
                    //更换
                    addPicture(picture.getId());
                }
            });
            imageDelete.setOnClickListener(v -> {
                deletePicture(picture.getId());
            });

            binding.viewPicture.addView(mView);
        }
        binding.pictureInfo.setText(num + " / " + 5);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null && resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE_CHOOSE){
                //选择图片
                List<Uri> uriList = Matisse.obtainResult(data);
                if(isNew){
                    //新增 移除添加按钮
                    for (int m = 0; m < uriList.size(); m++) {
                        obtainViewModel().pictureList.add(new Picture(null,uriList.get(m)));
                        if(m == uriList.size()-1 /*&& uriList.size() < obtainViewModel().maxPicture*/){
                            //添加新添加按钮
                            if(obtainViewModel().pictureList.size()-1<obtainViewModel().maxPicture){
                                obtainViewModel().pictureList.add(new Picture(null,null));
                                //移除旧添加按钮
                                obtainViewModel().pictureList.remove(obtainViewModel().pictureList.size()-uriList.size()-2);
                            }else{
                                //移除旧添加按钮
                                obtainViewModel().pictureList.remove(obtainViewModel().pictureList.size()-uriList.size()-1);
                            }
                            updatePictures();
                        }
                    }
                }else{
                    //替换
                    for (int m = 0; m < obtainViewModel().pictureList.size(); m++) {
                        Picture picture = obtainViewModel().pictureList.get(m);
                        if(Objects.equals(picture.getId(), pictureId) && uriList!=null && !uriList.isEmpty()){
                            obtainViewModel().pictureList.get(m).setUri(uriList.get(0));
                            updatePictures();
                            return;
                        }
                    }

                }


            }else if(requestCode == REQUEST_CODE_VIDEO){
                //选择视频

            }else if (requestCode == MAP_REQUEST_CODE) {
                obtainViewModel().longitude = data.getStringExtra("longitude");
                obtainViewModel().latitude = data.getStringExtra("latitude");
                obtainViewModel().cityAddress = data.getStringExtra("cityAddress");
                obtainViewModel().currentCity = data.getStringExtra("city");
                obtainViewModel().currentQu = data.getStringExtra("district");
                double[] doubles = LatLngChangeNew.calBD09toWGS84(Double.parseDouble(obtainViewModel().latitude), Double.parseDouble(obtainViewModel().longitude));

                binding.editAddress.setText(obtainViewModel().cityAddress);
            }
        }
    }

    private void deletePicture(String id) {
        for (int m = 0; m < obtainViewModel().pictureList.size(); m++) {
            if(Objects.equals(obtainViewModel().pictureList.get(m).getId(), id)){
                obtainViewModel().pictureList.remove(m);
                Picture picture = obtainViewModel().pictureList.get(obtainViewModel().pictureList.size() - 1);
                if(picture.getUri()==null && picture.getUrl()==null){
                    //移除旧添加按钮
                    obtainViewModel().pictureList.remove(obtainViewModel().pictureList.size()-1);
                }
                //添加新添加按钮
                obtainViewModel().pictureList.add(new Picture(null,null));
                updatePictures();
                return;
            }
        }
    }

    private boolean isNew = false;
    private String pictureId;
    private void addPicture(String id) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        int size = 1;
                        if(id == null){
                            //新增
                            isNew = true;
                            pictureId = null;
                            size = obtainViewModel().maxPicture - obtainViewModel().pictureList.size()+1;
                        }else{
                            //更换
                            isNew = false;
                            pictureId = id;

                        }
                        Matisse.from(AddingDangerActivity.this)
                                .choose(MimeType.ofAll())
                                .countable(true)
                                .capture(true)
                                .captureStrategy(
                                        new CaptureStrategy(false, "com.haohai.platform.fireforestplatform.fileprovider")
                                )
                                .maxSelectable(size)
                                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                .gridExpectedSize(
                                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                .thumbnailScale(0.85f)
                                .imageEngine(new GlideEngine())
                                .forResult(REQUEST_CODE_CHOOSE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void lockUi() {
        binding.textCommit.setVisibility(View.GONE);
        binding.editTitle.setEnabled(false);
        binding.editAddress.setEnabled(false);
        binding.editLeader.setEnabled(false);
        binding.editLeaderPhone.setEnabled(false);
        binding.editInfo.setEnabled(false);
        binding.textType.setClickable(false);
        binding.textLevel.setClickable(false);
        binding.textTime.setClickable(false);

        binding.editTitle.setHint("");
        binding.editAddress.setHint("");
        binding.editLeader.setHint("");
        binding.editLeaderPhone.setHint("");
        binding.editInfo.setHint("");

        binding.viewPictureInfo.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void updateUi() {
        if(danger && CommonData.danger!=null){
            obtainViewModel().danger = CommonData.danger;
            HhLog.e("toString() => " + obtainViewModel().danger.toString());

            //风险隐患
            binding.editTitle.setText(CommonUtil.parseNullString(CommonData.danger.getRiskName(),"-"));
            binding.textType.setText(CommonUtil.parseNullString(parseRiskTypeCode(CommonData.danger.getRiskType()),"-"));
            binding.editAddress.setText(CommonUtil.parseNullString(CommonData.danger.getAddress(),"-"));
            binding.textLevel.setText(parseLevel(CommonData.danger.getRiskLevel()));
            binding.editLeader.setText(CommonUtil.parseNullString(CommonData.danger.getDutyPerson(),"-"));
            binding.editLeaderPhone.setText(CommonUtil.parseNullString(CommonData.danger.getContactPhone(),"-"));
            binding.textTime.setText(CommonUtil.parseDate(CommonData.danger.getDataUpdateTime()));
            binding.editInfo.setText(CommonUtil.parseNullString(CommonData.danger.getRemark(),"-"));

            try{
                List<EmergencyFile> fileList = CommonData.danger.getFileList();
                binding.llFiles.removeAllViews();
                for (int m = 0; m < fileList.size(); m++) {
                    EmergencyFile file = fileList.get(m);
                    View view = LayoutInflater.from(this).inflate(R.layout.file,null);
                    TextView fileTitle = view.findViewById(R.id.file_title);
                    TextView file_review = view.findViewById(R.id.file_review);
                    fileTitle.setText(file.getFileName());
                    file_review.setOnClickListener(v -> {
                        jumpFile(file);
                    });
                    binding.llFiles.addView(view);
                }
            }catch (Exception e){
                HhLog.e(e.getMessage());
            }
        }
        if(!danger && CommonData.safety!=null){
            obtainViewModel().safety = CommonData.safety;
            //安全生产
            binding.editTitle.setText(CommonUtil.parseNullString(CommonData.safety.getRiskName(),"-"));
            binding.textType.setText(CommonUtil.parseNullString(parseRiskTypeCode(CommonData.safety.getRiskType()),"-"));

            binding.editAddress.setText(CommonUtil.parseNullString(CommonData.safety.getAddress(),"-"));
            binding.textLevel.setText(parseLevel(CommonData.safety.getRiskLevel()));
            binding.editLeader.setText(CommonUtil.parseNullString(CommonData.safety.getDutyPerson(),""));
            binding.editLeaderPhone.setText(CommonUtil.parseNullString(CommonData.safety.getContactPhone(),"-"));
            binding.textTime.setText(CommonUtil.parseDate(CommonData.safety.getCreateTime()));
            binding.editInfo.setText(CommonUtil.parseNullString(CommonData.safety.getRemark(),"-"));

            if(read){
                try{
                List<EmergencyFile> fileList = CommonData.safety.getFileList();
                binding.llFiles.removeAllViews();
                for (int m = 0; m < fileList.size(); m++) {
                    EmergencyFile file = fileList.get(m);
                    View view = LayoutInflater.from(this).inflate(R.layout.file,null);
                    TextView fileTitle = view.findViewById(R.id.file_title);
                    TextView file_review = view.findViewById(R.id.file_review);
                    fileTitle.setText(file.getFileName());
                    file_review.setOnClickListener(v -> {
                        jumpFile(file);
                    });
                    binding.llFiles.addView(view);
                }
            }catch (Exception e){
                HhLog.e(e.getMessage());
            }
            }
        }
    }


    private String parseLevel(String incidentLevel) {
        for (int m = 0; m < obtainViewModel().levelList.size(); m++) {
            if(Objects.equals(obtainViewModel().levelList.get(m).getId(), incidentLevel)){
                return obtainViewModel().levelList.get(m).getTitle();
            }
        }
        return "-";
    }

    private void jumpFile(EmergencyFile file) {
        Intent intent = new Intent(this,TbsActivity.class);
        intent.putExtra("url",file.getFileUrl());
        intent.putExtra("title",file.getFileName());
        startActivity(intent);
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
                binding.textTime.setText(obtainViewModel().date);
                //选择时间
                /*showTimeDialog();*/
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

    /**
     * 日期选择控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTimeDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder1.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute < 10) {
                    obtainViewModel().date.append(" 0" + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
                } else if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute >= 10) {
                    obtainViewModel().date.append(" 0" + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
                } else if (obtainViewModel().chooseHour >= 10 && obtainViewModel().chooseMinute < 10) {
                    obtainViewModel().date.append(" " + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
                } else {
                    obtainViewModel().date.append(" " + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
                }

                binding.textTime.setText(obtainViewModel().date);


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

    private void chooseTypeNew() {
        if(obtainViewModel().typeTrees==null||obtainViewModel().typeTrees.isEmpty()){
            Toast.makeText(this, "数据加载中..", Toast.LENGTH_SHORT).show();
            return;
        }

        typeChooseDialog = new TypeChooseDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = typeChooseDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        typeChooseDialog.setDialogListener(this);
        typeChooseDialog.setTreeList(obtainViewModel().typeTrees);
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

    private void chooseType() {
        CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_type_choose) {
            @Override
            public void onBind(final CustomDialog dialog, View v) {
                LinearLayout ll_type;
                TextView text_close;
                TextView text_confirm;
                ll_type = v.findViewById(R.id.ll_type);
                text_close = v.findViewById(R.id.text_close);
                text_confirm = v.findViewById(R.id.text_confirm);
                ll_type.removeAllViews();
                for (int i = 0; i < obtainViewModel().typeList.size(); i++) {
                    TypeBean typeBean = obtainViewModel().typeList.get(i);
                    View p_ = LayoutInflater.from(AddingDangerActivity.this).inflate(R.layout.item_emergency_choose, null);
                    LinearLayout all = p_.findViewById(R.id.all);
                    ImageView icon = p_.findViewById(R.id.icon);
                    TextView title = p_.findViewById(R.id.title);
                    title.setText(typeBean.getTitle());
                    if (typeBean.isChecked()) {
                        icon.setImageResource(R.drawable.yes);
                    } else {
                        icon.setImageResource(R.drawable.no);
                    }
                    all.setOnClickListener(v15 -> {
                        if (!typeBean.isChecked()) {
                            obtainViewModel().typeBean = typeBean;
                            for (int m = 0; m < obtainViewModel().typeList.size(); m++) {
                                obtainViewModel().typeList.get(m).setChecked(false);
                            }
                        }
                        typeBean.setChecked(!typeBean.isChecked());
                        onBind(dialog, v);
                    });

                    ll_type.addView(p_);
                }
                text_close.setOnClickListener(v13 -> {
                    dialog.dismiss();
                });
                text_confirm.setOnClickListener(v14 -> {
                    int tag = 0;
                    for (int i = 0; i < obtainViewModel().typeList.size(); i++) {
                        TypeBean typeBean = obtainViewModel().typeList.get(i);
                        if (typeBean.isChecked()) {
                            tag++;
                        }
                    }
                    dialog.dismiss();
                    if (tag > 0 && obtainViewModel().typeBean != null) {
                        binding.textType.setText(obtainViewModel().typeBean.getTitle());
                        if(binding.textType.getText().toString().contains("安全生产")){
                            danger = false;
                        }else{
                            danger = true;
                        }
                        parseDangerView();
                    }
                });

            }
        }).setOnBackgroundMaskClickListener((dialog, v12) -> {
            return false;
        }).setMaskColor(getResources().getColor(R.color.transparent_dialog))
                .setCancelable(true);
    }

    private void parseDangerView() {
        if(danger){
            binding.llLevel.setVisibility(View.VISIBLE);
            binding.llDate.setVisibility(View.VISIBLE);
            binding.llInfo.setVisibility(View.VISIBLE);
        }else{
            binding.llLevel.setVisibility(View.GONE);
            binding.llDate.setVisibility(View.GONE);
            binding.llInfo.setVisibility(View.GONE);
        }
    }

    private void chooseLevel() {
        CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_level_choose) {
            @Override
            public void onBind(final CustomDialog dialog, View v) {
                LinearLayout ll_type;
                TextView text_close;
                TextView text_confirm;
                ll_type = v.findViewById(R.id.ll_type);
                text_close = v.findViewById(R.id.text_close);
                text_confirm = v.findViewById(R.id.text_confirm);
                ll_type.removeAllViews();
                for (int i = 0; i < obtainViewModel().levelList.size(); i++) {
                    TypeBean typeBean = obtainViewModel().levelList.get(i);
                    View p_ = LayoutInflater.from(AddingDangerActivity.this).inflate(R.layout.item_emergency_choose, null);
                    LinearLayout all = p_.findViewById(R.id.all);
                    ImageView icon = p_.findViewById(R.id.icon);
                    TextView title = p_.findViewById(R.id.title);
                    title.setText(typeBean.getTitle());
                    if (typeBean.isChecked()) {
                        icon.setImageResource(R.drawable.yes);
                    } else {
                        icon.setImageResource(R.drawable.no);
                    }
                    all.setOnClickListener(v15 -> {
                        if (!typeBean.isChecked()) {
                            obtainViewModel().levelBean = typeBean;
                            for (int m = 0; m < obtainViewModel().levelList.size(); m++) {
                                obtainViewModel().levelList.get(m).setChecked(false);
                            }
                        }
                        typeBean.setChecked(!typeBean.isChecked());
                        onBind(dialog, v);
                    });

                    ll_type.addView(p_);
                }
                text_close.setOnClickListener(v13 -> {
                    dialog.dismiss();
                });
                text_confirm.setOnClickListener(v14 -> {
                    int tag = 0;
                    for (int i = 0; i < obtainViewModel().levelList.size(); i++) {
                        TypeBean typeBean = obtainViewModel().levelList.get(i);
                        if (typeBean.isChecked()) {
                            tag++;
                        }
                    }
                    dialog.dismiss();
                    if (tag > 0 && obtainViewModel().levelBean != null) {
                        binding.textLevel.setText(obtainViewModel().levelBean.getTitle());
                    }
                });

            }
        }).setOnBackgroundMaskClickListener((dialog, v12) -> {
            return false;
        }).setMaskColor(getResources().getColor(R.color.transparent_dialog))
                .setCancelable(true);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void bind_() {
        binding.endTitle.setVisibility(View.GONE);
        binding.editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    binding.endTitle.setVisibility(View.VISIBLE);
                }else{
                    binding.endTitle.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.map.setOnClickListener(v -> {
            if(edit && read){
                return;
            }
            Intent intent = new Intent(getApplicationContext(), FireMapActivity.class);
            intent.putExtra("longitude_double", CommonData.lng);
            intent.putExtra("latitude_double", CommonData.lat);
            startActivityForResult(intent, MAP_REQUEST_CODE);
        });
        binding.textType.setOnClickListener(v -> {
            if(edit && read){
                return;
            }
            chooseType();
        });
        binding.textLevel.setOnClickListener(v -> {
            if(edit && read){
                return;
            }
            chooseLevel();
        });
        binding.textTime.setOnClickListener(v -> {
            if(edit && read){
                return;
            }
            showDataDialog();
        });
        binding.textCommit.setOnClickListener(v -> {
            TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(getResources().getColor(R.color.c7));
            MessageDialog.show("温馨提示", "确定提交保存吗？","确定","取消")
                    .setButtonOrientation(LinearLayout.VERTICAL)
                    .setOkTextInfo(okTextInfo)
                    .setCancelTextInfo(okTextInfo)
                    .setOtherTextInfo(okTextInfo)
                    .setOkButtonClickListener((dialog, v1) -> {
                        if(binding.editTitle.getText().toString().isEmpty()){
                            Toast.makeText(this, "请输入隐患名称", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        obtainViewModel().danger.setRiskName(binding.editTitle.getText().toString());
                        obtainViewModel().safety.setRiskName(binding.editTitle.getText().toString());
                        if(binding.textType.getText().toString().isEmpty()){
                            Toast.makeText(this, "请选择隐患类型", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        obtainViewModel().danger.setTypeName(binding.textType.getText().toString());
                        obtainViewModel().safety.setTypeName(binding.textType.getText().toString());
                        obtainViewModel().danger.setIncidentName(binding.textType.getText().toString());
                        obtainViewModel().safety.setIncidentName(binding.textType.getText().toString());
                        obtainViewModel().danger.setIncident(binding.textType.getText().toString());
                        obtainViewModel().safety.setIncident(binding.textType.getText().toString());
                        if(binding.textType.getText().toString().equals("安全生产")){
                            obtainViewModel().danger.setHiddenType("Rectification");
                            obtainViewModel().safety.setHiddenType("Rectification");
                        }else{
                            obtainViewModel().danger.setHiddenType("NatuRiskManage");
                            obtainViewModel().safety.setHiddenType("NatuRiskManage");
                        }
                        obtainViewModel().danger.setRiskType(parseRiskType(binding.textType.getText().toString()));
                        obtainViewModel().safety.setRiskType(parseRiskType(binding.textType.getText().toString()));
                        if(binding.textLevel.getText().toString().isEmpty() && danger){
                            Toast.makeText(this, "请选择隐患级别", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if(danger){
                            obtainViewModel().danger.setRiskLevel(obtainViewModel().levelBean.getId());
                            obtainViewModel().safety.setRiskLevel(obtainViewModel().levelBean.getId());
                        }
                        if(binding.editAddress.getText().toString().isEmpty()){
                            Toast.makeText(this, "请选择隐患地址", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        obtainViewModel().danger.setAddress(binding.editAddress.getText().toString());
                        obtainViewModel().safety.setAddress(binding.editAddress.getText().toString());
                        String lat = obtainViewModel().latitude;
                        String lng = obtainViewModel().longitude;
                        try{
                            double[] doubles = LatLngChangeNew.calBD09toWGS84(Double.parseDouble(obtainViewModel().latitude), Double.parseDouble(obtainViewModel().longitude));
                            lat = doubles[0]+"";
                            lng = doubles[1]+"";
                        }catch(Exception e){
                            HhLog.e(e.getMessage());
                        }
                        obtainViewModel().danger.setPosition("("+lng+","+lat+")");
                        obtainViewModel().safety.setPosition("("+lng+","+lat+")");
                        if(binding.editLeader.getText().toString().isEmpty()){
                            Toast.makeText(this, "请输入责任人", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        obtainViewModel().danger.setDutyPerson(binding.editLeader.getText().toString());
                        obtainViewModel().safety.setDutyPerson(binding.editLeader.getText().toString());
                        if(binding.editLeaderPhone.getText().toString().isEmpty()){
                            Toast.makeText(this, "请输入责任人电话", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        obtainViewModel().danger.setContactPhone(binding.editLeaderPhone.getText().toString());
                        obtainViewModel().safety.setContactPhone(binding.editLeaderPhone.getText().toString());
                        if(binding.textTime.getText().toString().isEmpty() && danger){
                            Toast.makeText(this, "请选择隐患时间", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        obtainViewModel().danger.setDataUpdateTime(parseDate(binding.textTime.getText().toString()));
                        obtainViewModel().safety.setDataUpdateTime(parseDate(binding.textTime.getText().toString()));
                        if(binding.editInfo.getText().toString().isEmpty() && danger){
                            Toast.makeText(this, "请输入隐患描述", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        obtainViewModel().danger.setRemark(binding.editInfo.getText().toString());
                        obtainViewModel().safety.setRemark(binding.editInfo.getText().toString());
                        commit_();
                        return false;
                    })
                    .setCancelButtonClickListener((dialog, v2) -> {
                        return false;
                    })
                    .setOnBackgroundMaskClickListener((dialog, v12) -> {
                        return false;
                    })
                    .setCancelable(true);
        });
    }



    private String parseRiskType(String toString) {
        for (int i = 0; i < obtainViewModel().typeList.size(); i++) {
            TypeBean typeBean = obtainViewModel().typeList.get(i);
            if(Objects.equals(typeBean.getTitle(), toString)){
                return typeBean.getId();
            }
        }
        return "0";
    }
    private String parseRiskTypeCode(String code) {
        for (int i = 0; i < obtainViewModel().typeList.size(); i++) {
            TypeBean typeBean = obtainViewModel().typeList.get(i);
            if(Objects.equals(typeBean.getId(), code)){
                return typeBean.getTitle();
            }
        }
        return "安全生产";
    }

    private String parseDate(String toString) {
        if(toString.length()>12){
            return toString;
        }else{
            return toString + " 12:00:00";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void commit_() {
        obtainViewModel().fileList = new ArrayList<>();
        obtainViewModel().picturePostList = new ArrayList<>();
        for (int m = 0; m < obtainViewModel().pictureList.size(); m++) {
            obtainViewModel().picturePostList.add(obtainViewModel().pictureList.get(m));
        }
        if(obtainViewModel().picturePostList.size()>0){
            Picture picture = obtainViewModel().picturePostList.get(obtainViewModel().picturePostList.size() - 1);
            if(picture.getUri()==null){
                obtainViewModel().picturePostList.remove(obtainViewModel().picturePostList.size() - 1);
            }
        }
        for (int i = 0; i < obtainViewModel().picturePostList.size(); i++) {
            Picture picture = obtainViewModel().picturePostList.get(i);
            obtainViewModel().postPicToService(picture,edit,danger);
        }
        if(obtainViewModel().picturePostList.size()==0){
            obtainViewModel().commit(edit,danger);
        }
    }

    @Override
    protected ActivityAddingDangerBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_adding_danger);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public AddingDangerViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(AddingDangerViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        obtainViewModel().year = year;
        obtainViewModel().month = monthOfYear+1;
        obtainViewModel().day = dayOfMonth;
    }

    @Override
    public void onTypeChooseDialogRefresh() {

    }

    @Override
    public void onTypeChoose(TypeTree typeTree, TypeTree typeTreeSecond, TypeTree typeTreeThird) {
        String type = typeTree.getTypeName();
        if(typeTreeSecond!=null){
            type = type + "/" + typeTreeSecond.getTypeName();
        }
        if(typeTreeThird!=null){
            type = type + "/" + typeTreeThird.getTypeName();
        }
        binding.textType.setText(type);
        if(binding.textType.getText().toString().contains("安全生产")){
            danger = false;
        }else{
            danger = true;
        }
        parseDangerView();
    }
}