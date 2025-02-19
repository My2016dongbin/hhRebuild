package com.haohai.platform.fireforestplatform.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityChangePasswordBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityNewsBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.NewsViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ChangePassWordViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.NewsViewModel;
import com.haohai.platform.fireforestplatform.utils.HhToast;

import java.util.Objects;

public class ChangePassWordActivity extends BaseLiveActivity<ActivityChangePasswordBinding, ChangePassWordViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("修改密码");
    }

    private void bind_() {
        binding.submit.setOnClickListener(v -> check());
    }

    private void check() {
        String oldStr = binding.oldPassword.getText().toString();
        String newStr = binding.newPassword.getText().toString();
        String confirmStr = binding.confirmPassword.getText().toString();
        if(oldStr.length()==0 || newStr.length()==0 || confirmStr.length()==0 ){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
            return;
        }
        if(newStr.length()<6 || confirmStr.length()<6 ){
            Toast.makeText(this,"新密码至少6位",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Objects.equals(newStr, confirmStr)){
            Toast.makeText(this,"新密码输入不一致",Toast.LENGTH_SHORT).show();
            return;
        }
        obtainViewModel().submit(oldStr,newStr);
    }

    @Override
    protected ActivityChangePasswordBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_change_password);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ChangePassWordViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ChangePassWordViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}