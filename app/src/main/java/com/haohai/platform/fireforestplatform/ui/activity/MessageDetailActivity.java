package com.haohai.platform.fireforestplatform.ui.activity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityMessageDetailBinding;
import com.haohai.platform.fireforestplatform.ui.viewmodel.MessageDetailViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;

import java.util.Objects;

public class MessageDetailActivity extends BaseLiveActivity<ActivityMessageDetailBinding, MessageDetailViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("消息详情");
        if (CommonData.message != null) {
            obtainViewModel().message = CommonData.message;
            if (!Objects.equals(obtainViewModel().message.getReadStatus(), "read")){
                obtainViewModel().postRead();
            }
            CommonData.message = null;
            updateUi();
        }

    }

    private void updateUi() {
        binding.lineType.title.setText("消息类型");
        binding.lineType.content.setText(parseType(obtainViewModel().message.getType()));
        binding.lineTitle.title.setText("消息标题");
        binding.lineTitle.content.setText(obtainViewModel().message.getTitle());
        binding.lineContent.title.setText("消息内容");
        binding.lineContent.content.setText(obtainViewModel().message.getContent());
    }

    private String parseType(String type) {
        String rt = "";
        if (Objects.equals(type, "audit")) {
            rt = "事件审核消息";
        } else if (Objects.equals(type, "incident")) {
            rt = "预警信息消息";
        } else {
            rt = "事件消息";
        }
        return rt;
    }

    private void bind_() {

    }

    @Override
    protected ActivityMessageDetailBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_message_detail);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public MessageDetailViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(MessageDetailViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}