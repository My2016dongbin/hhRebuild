package com.haohai.platform.fireforestplatform.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseFragment;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.FgMine;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.event.SettingEvent;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FgMineViewModel;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.util.TextInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

public class MineFragment extends BaseFragment<FgMine, FgMineViewModel> {

    public static MineFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        EventBus.getDefault().register(this);
        init_();
        bind_();

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    ///Setting参数更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(SettingEvent event) {
        if (Objects.equals(event.getType(), "voice")) {
            obtainViewModel().voice = event.isValue();
            binding.voice.content.setText(event.isValue()?"开":"关");
        }
        if (Objects.equals(event.getType(), "upload")) {
            obtainViewModel().locationUpload = event.isValue();
            binding.position.content.setText(event.isValue()?"开":"关");
        }
    }

    private void bind_() {
        binding.outLogin.setOnClickListener(v -> {
            TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(requireActivity().getResources().getColor(R.color.text_color_red));
            MessageDialog.show("温馨提示", "确定要退出登录吗？","退出登录","取消")
                    .setButtonOrientation(LinearLayout.VERTICAL)
                    .setOkTextInfo(okTextInfo)
                    .setOkButtonClickListener((dialog, v1) -> {
                        obtainViewModel().outLogin();
                        return false;
                    })
                    .setCancelable(true);
        });
    }

    @Override
    protected void setupViewModel() {
        binding.setLifecycleOwner(this);
        binding.setFragmentModel(obtainViewModel());
        obtainViewModel().start(requireActivity());
    }

    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        //用户头像
        obtainViewModel().headImage.observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Glide.with(requireActivity())
                        .load(s)
                        .circleCrop()
                        .placeholder(R.drawable.icon_manage)
                        .into(binding.headImage);
            }
        });
    }

    @Override
    public int bindLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public FgMineViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(FgMineViewModel.class);
    }


    private void init_() {
        //权限
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MY_PASSWORD)){
            binding.viewPassword.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MY_POSITION)){
            binding.viewPosition.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MY_AUDIO)){
            binding.viewVoice.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MY_POSITION) && !CommonPermission.hasPermission(requireActivity(),CommonPermission.MY_AUDIO)){
            binding.viewBottom.setVisibility(View.GONE);
        }
        //初始化设置参数状态
        obtainViewModel().locationUpload = (boolean) SPUtils.get(requireActivity(), SPValue.upload, false);
        obtainViewModel().voice = (boolean) SPUtils.get(requireActivity(), SPValue.voice, false);
        obtainViewModel().mineMenuList.get(0).setContent("V" + getVersionName());
        obtainViewModel().mineMenuList.get(3).setContent(obtainViewModel().locationUpload?"开":"关");
        obtainViewModel().mineMenuList.get(4).setContent(obtainViewModel().voice?"开":"关");
        obtainViewModel().mineMenuListListener.setValue(obtainViewModel().mineMenuList);
    }

    private int getVersionCode(){
        return getPackageInfo().versionCode;
    }
    private String getVersionName(){
        return getPackageInfo().versionName;
    }
    private PackageInfo getPackageInfo() {
        PackageManager packageManager = requireActivity().getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                return packageManager.getPackageInfo(requireActivity().getPackageName(), PackageManager.PackageInfoFlags.of(0));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return packageManager.getPackageInfo(requireActivity().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
