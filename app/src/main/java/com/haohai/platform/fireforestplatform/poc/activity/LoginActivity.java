package com.haohai.platform.fireforestplatform.poc.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.huamai.poc.IPocEngineEventHandler;
import com.huamai.poc.PocEngineFactory;

import io.reactivex.disposables.Disposable;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.poc.permission.RxPermissions;

import java.util.Objects;


public class LoginActivity extends BaseActivity {
    private EditText mAccountView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Dialog mSettingDialog;
    private TextView left;
    private ImageView left_icon;

    private Handler handler = new Handler();
    SharedPreferences mSharedPreferences;
    private RxPermissions rxPermissions = new RxPermissions(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        mSharedPreferences = getSharedPreferences("poc-demo", Context.MODE_PRIVATE);

        blackText();
        left = (TextView) findViewById(R.id.left);
        left_icon = (ImageView) findViewById(R.id.left_icon);
        left.setOnClickListener(v -> finish());
        left_icon.setOnClickListener(v -> finish());
        mAccountView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        final Button signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAccountView.setText(mSharedPreferences.getString("account", ""));
        mPasswordView.setText(mSharedPreferences.getString("password", ""));

        //TODO 测试账号 29291002~29291011
        if(mAccountView.getText().toString().isEmpty()){
            mAccountView.setText("29291003");
            mPasswordView.setText("13579");
        }

        if (PocEngineFactory.get().hasServiceConnected()) {
            LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            LoginActivity.this.finish();
        } else {
            signInButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //signInButton.performClick();
                }
            }, 1000);
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //根据唯一码获取账号信息
        findViewById(R.id.get_account_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                PocEngineFactory.get().addEventHandler(iPocEngineEventHandler);
                PocEngineFactory.get().login("");
            }
        });

        findViewById(R.id.settingBt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSettingDialog != null) {
                    mSettingDialog.dismiss();
                }
                mSettingDialog = new Dialog(LoginActivity.this);

                ViewGroup layout = (ViewGroup) LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_login_setting,
                        null, false);
                mSettingDialog.setContentView(layout);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSettingDialog != null) {
            mSettingDialog.dismiss();
        }
    }

    private void checkPer() {
        Disposable result = rxPermissions.requestEachCombined(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        postDoInit();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        String tip = "miss: " + permission.name;
                        Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_LONG).show();
                        postFinish();
                    } else {
                        String tip = "miss: " + permission.name;
                        Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_LONG).show();
                        postFinish();
                    }
                });
    }

    private void postFinish() {
        handler.postDelayed(() -> LoginActivity.this.finish(), 500);
    }

    private void postDoInit() {
    }

    private void attemptLogin() {
        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        } else if (!isNumberValid(email)) {
            mAccountView.setError(getString(R.string.error_invalid_email));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            //登录
            PocEngineFactory.get().addEventHandler(iPocEngineEventHandler);
            PocEngineFactory.get().login(email, password);

            mSharedPreferences.edit().putString("account", email).commit();
            mSharedPreferences.edit().putString("password", password).commit();
        }
    }

    //登录时相关的回调，该过程可能需要几秒，可根据不同状态显示相应的UI
    IPocEngineEventHandler iPocEngineEventHandler = new IPocEngineEventHandler() {

        @Override
        public void onLoginStepProgress(int progress, String msg) {
            if (progress == LoginProgress.PRO_LOGIN_SUCCESS) {
                PocEngineFactory.get().removeEventHandler(iPocEngineEventHandler);
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                LoginActivity.this.finish();
            } else if (progress == LoginProgress.PRO_BINDING_ACCOUNT_FAILED) {
                Toast.makeText(LoginActivity.this, "Login failed " + msg, Toast.LENGTH_SHORT).show();
            } else if (progress == LoginProgress.PRO_BINDING_ACCOUNT_NOT_EXIST) {
                Toast.makeText(LoginActivity.this, "Login failed " + msg, Toast.LENGTH_SHORT).show();
            } else if (progress == LoginProgress.PRO_BINDING_ACCOUNT_NOT_ACTIVE) {
                Toast.makeText(LoginActivity.this, "Login failed " + msg, Toast.LENGTH_SHORT).show();
            } else if (progress == LoginProgress.PRO_LOGIN_FAILED) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Login failed " + msg, Toast.LENGTH_SHORT).show();
            }

            ((TextView) findViewById(R.id.tips)).setText(parseMSG(msg));
        }
    };

    private String parseMSG(String msg) {
        String rt = msg;
        if(Objects.equals(msg, "ACCOUNT_VERIFY")){
            rt = "账号验证中";
        }
        if(Objects.equals(msg, "CONN_SERVER")){
            rt = "正在连接服务";
        }
        if(Objects.equals(msg, "SYNC_CONTACTS")){
            rt = "正在同步对话";
        }
        if(Objects.equals(msg, "SYNC_CHANNEL")){
            rt = "正在同步群组";
        }
        if(Objects.equals(msg, "LOGIN_SUCCESS")){
            rt = "登录成功";
        }
        return rt;
    }

    private boolean isNumberValid(String input) {
        try {
            Long.valueOf(input);
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        findViewById(R.id.tips).setVisibility(show ? View.VISIBLE : View.GONE);
    }
}