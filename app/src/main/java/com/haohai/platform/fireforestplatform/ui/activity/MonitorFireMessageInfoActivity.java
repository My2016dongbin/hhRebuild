package com.haohai.platform.fireforestplatform.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityMonitorFireBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityMonitorFireInfoBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessage;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessageViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.MonitorFireMessageInfoViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.MonitorFireMessageListViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImagePagerUtil;
import com.haohai.platform.fireforestplatform.utils.StringData;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

public class MonitorFireMessageInfoActivity extends BaseLiveActivity<ActivityMonitorFireInfoBinding, MonitorFireMessageInfoViewModel> {

    private String id;
    private boolean message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        message = getIntent().getBooleanExtra("message",false);
        init_();
        bind_();
        HhLog.e("id " + id);
        if(message)obtainViewModel().postDataDetail(id);
        else obtainViewModel().postData(id);
    }

    private void init_() {
        binding.topBar.title.setText("监控报警详情");

        binding.refresh.setRefreshHeader(new ClassicsHeader(this));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if(message)obtainViewModel().postDataDetail(id);
                else obtainViewModel().postData(id);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
    }

    private void bind_() {
        binding.leftPic.setOnClickListener(v -> {
            MonitorFireMessage messageBeanValue = obtainViewModel().messageBean.getValue();
            if (Objects.requireNonNull(messageBeanValue).getPicPath1() != null
                    && !Objects.equals(messageBeanValue.getPicPath1(), "null")
                    && !Objects.equals(messageBeanValue.getPicPath1(), "")) {
                /*List<String> leftImage = new ArrayList<>();
                leftImage.add(Objects.requireNonNull(messageBeanValue).getPicPath1());
                ImagePagerUtil imagePagerUtil = new ImagePagerUtil(MonitorFireMessageInfoActivity.this, leftImage);
                imagePagerUtil.setContentText("报警图片");
                imagePagerUtil.show();*/

                Intent intent = new Intent(this, PhotoViewerActivity.class);
                intent.putExtra("url",Objects.requireNonNull(messageBeanValue).getPicPath1());
                this.startActivity(intent);
            }
        });
        binding.rightPic.setOnClickListener(v -> {
            MonitorFireMessage messageBeanValue = obtainViewModel().messageBean.getValue();
            if (Objects.requireNonNull(messageBeanValue).getPicPath2() != null
                    && !Objects.equals(messageBeanValue.getPicPath2(), "null")
                    && !Objects.equals(messageBeanValue.getPicPath2(), "")) {
                /*List<String> rightImage = new ArrayList<>();
                rightImage.add(Objects.requireNonNull(messageBeanValue).getPicPath2());
                ImagePagerUtil imagePagerUtil = new ImagePagerUtil(MonitorFireMessageInfoActivity.this, rightImage);
                imagePagerUtil.setContentText("报警图片");
                imagePagerUtil.show();*/

                Intent intent = new Intent(this, PhotoViewerActivity.class);
                intent.putExtra("url",Objects.requireNonNull(messageBeanValue).getPicPath2());
                this.startActivity(intent);
            }
        });
        binding.leftVideo.setOnClickListener(v -> {
            MonitorFireMessage messageBeanValue = obtainViewModel().messageBean.getValue();
            Intent intent = new Intent(MonitorFireMessageInfoActivity.this, VideoStreamActivity.class);
            intent.putExtra("url", Objects.requireNonNull(messageBeanValue).getVideoPath1());
            startActivity(intent);
        });
        binding.rightVideo.setOnClickListener(v -> {
            MonitorFireMessage messageBeanValue = obtainViewModel().messageBean.getValue();
            Intent intent = new Intent(MonitorFireMessageInfoActivity.this, VideoStreamActivity.class);
            intent.putExtra("url", Objects.requireNonNull(messageBeanValue).getVideoPath2());
            startActivity(intent);
        });
    }

    @Override
    protected ActivityMonitorFireInfoBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_monitor_fire_info);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public MonitorFireMessageInfoViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MonitorFireMessageInfoViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().messageBean.observe(this, this::updateUi);
    }

    @SuppressLint("SetTextI18n")
    private void updateUi(MonitorFireMessage monitorFireMessage) {
        binding.lineName.title.setText("报警设备名称");
        binding.lineName.content.setText(monitorFireMessage.getName());
        binding.lineType.title.setText("报警类型");
        binding.lineType.content.setText(parseFireType(monitorFireMessage.getType()));
        binding.lineTime.title.setText("报警时间");
        binding.lineTime.content.setText(StringData.parse19(monitorFireMessage.getAlarmDatetime()));
        binding.lineLngLat.title.setText("火点经纬度");
        binding.lineLngLat.content.setText(monitorFireMessage.getAlarmLongitude() + "," + monitorFireMessage.getAlarmLatitude());
        binding.lineAddress.title.setText("报警地点");
        binding.lineAddress.content.setText(monitorFireMessage.getAddress());
        binding.lineHandle.title.setText("处理状态");
        binding.lineHandle.content.setText(Objects.equals(monitorFireMessage.getIsHandle(), "1") ? "已处理" : "未处理");
        binding.lineHandleTime.title.setText("处理时间");
        binding.lineHandleTime.content.setText(StringData.parse19(monitorFireMessage.getHandleTime()));
        binding.lineHandleUser.title.setText("处理人");
        binding.lineHandleUser.content.setText(monitorFireMessage.getHandleUser());
        binding.lineHandleOptions.title.setText("处理意见");
        binding.lineHandleOptions.content.setText(monitorFireMessage.getHandleOpinions());
        if(monitorFireMessage.getPicPath1()!=null && !monitorFireMessage.getPicPath1().isEmpty()){
            binding.leftPic.setVisibility(View.VISIBLE);
            Glide.with(this).load(monitorFireMessage.getPicPath1()).error(R.drawable.ic_jaizai).placeholder(R.drawable.ic_jaizai).into(binding.leftPic);
        }else{
            binding.leftPic.setVisibility(View.GONE);
        }
        if(monitorFireMessage.getPicPath2()!=null && !monitorFireMessage.getPicPath2().isEmpty()){
            binding.rightPic.setVisibility(View.VISIBLE);
            Glide.with(this).load(monitorFireMessage.getPicPath2()).error(R.drawable.ic_jaizai).placeholder(R.drawable.ic_jaizai).into(binding.rightPic);
        }else{
            binding.rightPic.setVisibility(View.GONE);
        }
        if(monitorFireMessage.getVideoPath1()!=null && !monitorFireMessage.getVideoPath1().isEmpty()){
            binding.leftVideo.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.drawable.video_play_normal1).into(binding.leftVideo);
        }else{
            binding.leftVideo.setVisibility(View.GONE);
        }
        if(monitorFireMessage.getVideoPath2()!=null && !monitorFireMessage.getVideoPath2().isEmpty()){
            binding.rightVideo.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.drawable.video_play_normal1).into(binding.rightVideo);
        }else{
            binding.rightVideo.setVisibility(View.GONE);
        }
    }

    private String parseFireType(String type) {
        String msg = "报警";
        if(Objects.equals(type, "2")){
            msg = "监控报警";
        }
        return msg;
    }
}