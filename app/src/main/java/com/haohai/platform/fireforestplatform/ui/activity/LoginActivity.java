package com.haohai.platform.fireforestplatform.ui.activity;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityLoginBinding;
import com.haohai.platform.fireforestplatform.ui.viewmodel.LoginViewModel;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.haohai.platform.fireforestplatform.utils.StringData;

public class LoginActivity extends BaseLiveActivity<ActivityLoginBinding, LoginViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().getName();
    }

    private void init_() {
        binding.usernameEdit.setText((String)SPUtils.get(this, SPValue.userName,""));
        binding.passwordEdit.setText((String)SPUtils.get(this, SPValue.password,""));
    }

    private void bind_() {
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.usernameEdit.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, StringData.login_name_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(binding.passwordEdit.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, StringData.login_password_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                //登录
                obtainViewModel().login(binding.usernameEdit.getText().toString(),binding.passwordEdit.getText().toString());
            }
        });
    }

    @Override
    protected ActivityLoginBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_login);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public LoginViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(LoginViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
    }

    private void nameChanged(String name) {
        binding.name.setText(name);
    }
}