package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityIdentifyDetailBinding;
import com.haohai.platform.fireforestplatform.ui.bean.CheckResult;
import com.haohai.platform.fireforestplatform.ui.bean.HandleResult;
import com.haohai.platform.fireforestplatform.ui.cell.CheckDialog;
import com.haohai.platform.fireforestplatform.ui.cell.HandleDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.Identify;
import com.haohai.platform.fireforestplatform.ui.viewmodel.IdentifyDetailViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IdentifyDetailActivity extends BaseLiveActivity<ActivityIdentifyDetailBinding, IdentifyDetailViewModel> implements HandleDialog.HandleDialogListener, CheckDialog.CheckDialogListener {

    private HandleDialog handleDialog;
    private CheckDialog checkDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel().id = getIntent().getStringExtra("id");
        init_();
        bind_();
        obtainViewModel().getInfo();
    }

    private void init_() {
        binding.topBar.title.setText("识别详情");

        //处置Dialog
        initHandleDialog();
        //督导Dialog
        initCheckDialog();
    }

    private void initHandleDialog() {
        handleDialog = new HandleDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = handleDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        handleDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.6);
        dialogWindow.setAttributes(lp);
        handleDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            handleDialog.create();
        }
    }
    private void initCheckDialog() {
        checkDialog = new CheckDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = checkDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        checkDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.6);
        dialogWindow.setAttributes(lp);
        checkDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkDialog.create();
        }
    }

    private void bind_() {
        binding.handle.setOnClickListener(v -> {
            handleDialog.setHandleResult(obtainViewModel().handleResult);
            handleDialog.show();
        });
        binding.check.setOnClickListener(v -> {
            checkDialog.setCheckResult(obtainViewModel().checkResult);
            checkDialog.show();
        });
    }


    @Override
    protected ActivityIdentifyDetailBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_identify_detail);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public IdentifyDetailViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(IdentifyDetailViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().identifyDetail.observe(this, this::detailUi);
    }

    @SuppressLint("SetTextI18n")
    private void detailUi(Identify identify) {
        if (identify != null) {
            binding.textMonitor.setText(CommonUtil.parseNullString(identify.getName(), "-"));
            binding.textType.setText(CommonUtil.parseNullString(identify.getAlarmType(), "-"));
            binding.textDate.setText(CommonUtil.parse19String(identify.getAlarmDatetime(), "-"));
            binding.textLngLat.setText(CommonUtil.parseNullString(identify.getAlarmLongitude()+","+identify.getAlarmLatitude(), "-"));
            binding.textAddress.setText(CommonUtil.parseNullString(identify.getAddress(), "-"));
            binding.textAngle.setText(CommonUtil.parseNullString(identify.getHorizonAngle()+","+identify.getVerticalAngle(), "-"));
            String status = obtainViewModel().parseStatus(identify);
            binding.textReal.setText(CommonUtil.parseNullString(status, "-"));
            binding.textRemark.setText(CommonUtil.parseNullString(identify.getHandleOpinions(), "-"));
            if(Objects.equals(status, "未处理")){
                binding.handle.setVisibility(View.VISIBLE);
            }else{
                binding.handle.setVisibility(View.GONE);
            }
            binding.llWarnPic.removeAllViews();
            List<String> images = new ArrayList<>();
            images.add(identify.getPicPath1());
            images.add(identify.getPicPath2());
            for (int m = 0; m < images.size(); m++) {
                String image = images.get(m);
                View p_ = LayoutInflater.from(this).inflate(R.layout.item_image, null);
                ImageView imageView = p_.findViewById(R.id.image);
                imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(this,TbsActivity.class);
                    intent.putExtra("url",image);
                    intent.putExtra("title","报警图片");
                    startActivity(intent);
                });
                Glide.with(this).load(image).error(getResources().getDrawable(R.drawable.ic_no_pic)).into(imageView);
                binding.llWarnPic.addView(p_);
            }

            binding.llWarnVideo.removeAllViews();
            List<String> videos = new ArrayList<>();
            videos.add(identify.getVideoPath1());
            videos.add(identify.getVideoPath2());
            for (int m = 0; m < videos.size(); m++) {
                String video = videos.get(m);
                View p_ = LayoutInflater.from(this).inflate(R.layout.item_image, null);
                ImageView imageView = p_.findViewById(R.id.image);
                ImageView play = p_.findViewById(R.id.play);
                play.setVisibility(View.VISIBLE);
                play.setOnClickListener(v -> {
                    Intent intent = new Intent(this,TbsActivity.class);
                    intent.putExtra("url",video);
                    intent.putExtra("title","报警视频");
                    startActivity(intent);
                });
                Glide.with(this).load(video).error(getResources().getDrawable(R.drawable.ic_no_pic)).into(imageView);
                binding.llWarnVideo.addView(p_);
            }
            if(identify.getDisposeList()!=null){
                binding.llLines.removeAllViews();
                List<Identify.Line> lines = identify.getDisposeList();
                for (int m = 0; m < lines.size(); m++) {
                    Identify.Line line = lines.get(m);
                    View p_ = LayoutInflater.from(this).inflate(R.layout.item_line, null);
                    ImageView imageView = p_.findViewById(R.id.image);
                    TextView title = p_.findViewById(R.id.title);
                    TextView hint = p_.findViewById(R.id.hint);
                    title.setText(line.getInitiator());
                    hint.setText(line.getContent());
                    binding.llLines.addView(p_);
                }
            }
        }
    }


    @Override
    public void onCheckDialogResult(CheckResult checkResult) {
        obtainViewModel().checkResult = checkResult;
        obtainViewModel().postCheck();
    }

    @Override
    public void onHandleDialogResult(HandleResult handleResult) {
        obtainViewModel().handleResult = handleResult;
        obtainViewModel().postHandle();
    }
}