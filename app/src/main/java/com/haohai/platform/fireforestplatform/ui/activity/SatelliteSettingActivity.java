package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivitySatelliteFireInfoBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivitySatelliteSettingBinding;
import com.haohai.platform.fireforestplatform.event.SettingEvent;
import com.haohai.platform.fireforestplatform.ui.cell.OneBodyDetailDialog;
import com.haohai.platform.fireforestplatform.ui.cell.SatelliteSettingDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.SatelliteFireMessage;
import com.haohai.platform.fireforestplatform.ui.viewmodel.SatelliteFireMessageInfoViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.SatelliteSettingViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.haohai.platform.fireforestplatform.utils.StringData;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SatelliteSettingActivity extends BaseLiveActivity<ActivitySatelliteSettingBinding, SatelliteSettingViewModel> implements SatelliteSettingDialog.SatelliteSettingDialogListener {

    private SatelliteSettingDialog settingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("设置");


        settingDialog = new SatelliteSettingDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = settingDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        settingDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.75);
        dialogWindow.setAttributes(lp);
        settingDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settingDialog.create();
        }
        try {
            Set<String> satelliteSet = (Set<String>) SPUtils.get(this, SPValue.satelliteSet, new HashSet<>());
            Set<Integer> set_satellite = new HashSet<>();
            for (String s : satelliteSet) {
                set_satellite.add(Integer.parseInt(s));
            }
            settingDialog.setSatelliteSet(set_satellite);
        } catch (Exception e) {
            Log.e("TAG", "init_: settingDialog SPUtils " + e.getMessage());
        }
        try {
            Set<String> landformsSet = (Set<String>) SPUtils.get(this, SPValue.landformsSet, new HashSet<>());
            Set<Integer> set_landforms = new HashSet<>();
            for (String s : landformsSet) {
                set_landforms.add(Integer.parseInt(s));
            }
            settingDialog.setLandformsSet(set_landforms);
        } catch (Exception e) {
            Log.e("TAG", "init_: settingDialog SPUtils " + e.getMessage());
        }
        try {
            Set<String> fireCountsSet = (Set<String>) SPUtils.get(this, SPValue.fireCountsSet, new HashSet<>());
            Set<Integer> set_fireCounts = new HashSet<>();
            for (String s : fireCountsSet) {
                set_fireCounts.add(Integer.parseInt(s));
            }
            settingDialog.setFireCountsSet(set_fireCounts);
        } catch (Exception e) {
            Log.e("TAG", "init_: settingDialog SPUtils " + e.getMessage());
        }
        try {
            Set<String> otherSet = (Set<String>) SPUtils.get(this, SPValue.otherSet, new HashSet<>());
            Set<Integer> set_other = new HashSet<>();
            for (String s : otherSet) {
                set_other.add(Integer.parseInt(s));
            }
            settingDialog.setOtherSet(set_other);
        } catch (Exception e) {
            Log.e("TAG", "init_: settingDialog SPUtils " + e.getMessage());
        }

        //初始化设置参数状态
        boolean voiceState = (boolean) SPUtils.get(this, SPValue.voice, false);
        binding.voiceSwitch.setChecked(voiceState);
    }

    private void bind_() {
        binding.setting.setOnClickListener(v -> {
            settingDialog.show();
        });
        binding.voiceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPUtils.put(SatelliteSettingActivity.this, SPValue.voice, isChecked);
            Toast.makeText(SatelliteSettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new SettingEvent("voice", isChecked));
        });
    }

    @Override
    protected ActivitySatelliteSettingBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_satellite_setting);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public SatelliteSettingViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(SatelliteSettingViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
    }

    @Override
    public void onSatelliteSettingDialogRefresh() {

    }
}