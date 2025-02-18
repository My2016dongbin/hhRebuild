package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityKkFireInfoBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityMonitorFireInfoBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.KKFireMessage;
import com.haohai.platform.fireforestplatform.ui.viewmodel.KKFireMessageInfoViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.StringData;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.Objects;

public class KKFireMessageInfoActivity extends BaseLiveActivity<ActivityKkFireInfoBinding, KKFireMessageInfoViewModel> {

    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        init_();
        bind_();
        HhLog.e("id " + id);
        obtainViewModel().postData(id);
    }

    private void init_() {
        binding.topBar.title.setText("卡口报警详情");

        binding.refresh.setRefreshHeader(new ClassicsHeader(this));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().postData(id);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
    }

    private void bind_() {

    }

    @Override
    protected ActivityKkFireInfoBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_kk_fire_info);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public KKFireMessageInfoViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(KKFireMessageInfoViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().messageBean.observe(this, this::updateUi);
    }

    @SuppressLint("SetTextI18n")
    private void updateUi(KKFireMessage kkFireMessage) {
        binding.lineName.title.setText("报警设备名称");
        binding.lineName.content.setText(kkFireMessage.getName());
        binding.lineType.title.setText("报警类型");
        binding.lineType.content.setText(kkFireMessage.getType());
        binding.lineTime.title.setText("报警时间");
        binding.lineTime.content.setText(StringData.parse19(kkFireMessage.getAlarmDatetime()));
        binding.lineLngLat.title.setText("火点经纬度");
        binding.lineLngLat.content.setText(kkFireMessage.getAlarmLongitude()+","+kkFireMessage.getAlarmLatitude());
        binding.lineAddress.title.setText("报警地点");
        binding.lineAddress.content.setText(kkFireMessage.getAddress());
        binding.lineHandle.title.setText("处理状态");
        binding.lineHandle.content.setText(Objects.equals(kkFireMessage.getIsHandle(), "1")?"已处理":"未处理");
        binding.lineHandleTime.title.setText("处理时间");
        binding.lineHandleTime.content.setText(StringData.parse19(kkFireMessage.getHandleTime()));
        binding.lineHandleUser.title.setText("处理人");
        binding.lineHandleUser.content.setText(kkFireMessage.getHandleUser());
        binding.lineHandleOptions.title.setText("处理意见");
        binding.lineHandleOptions.content.setText(kkFireMessage.getHandleOpinions());
        Glide.with(this).load(kkFireMessage.getPicPath1()).error(R.drawable.ic_no_pic).into(binding.leftPic);
        Glide.with(this).load(kkFireMessage.getPicPath2()).error(R.drawable.ic_no_pic).into(binding.rightPic);
        Glide.with(this).load(R.drawable.video_play_normal1).error(R.drawable.ic_no_pic).into(binding.leftVideo);
        Glide.with(this).load(R.drawable.video_play_normal1).error(R.drawable.ic_no_pic).into(binding.rightVideo);
    }
}