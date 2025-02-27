package com.haohai.platform.fireforestplatform.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.Utils;
import com.genew.base.net.base.OnRequestResultListener;
import com.genew.base.net.bean.NiuxinLoginInfo;
import com.genew.base.net.bean.NiuxinResultInfo;
import com.genew.base.utils.ToastUtils;
import com.genew.mpublic.router.api.Api;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseFragment;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.FgMain;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.nusdk.ErrorCode;
import com.haohai.platform.fireforestplatform.nusdk.NuSDKMainActivity;
import com.haohai.platform.fireforestplatform.nusdk.permission.PermissionListener;
import com.haohai.platform.fireforestplatform.nusdk.permission.PermissionsUtil;
import com.haohai.platform.fireforestplatform.old.SignMonthActivity;
import com.haohai.platform.fireforestplatform.old.WeatherActivity;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.FireUploadActivity;
import com.haohai.platform.fireforestplatform.ui.activity.HiddenDangerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.NewsActivity;
import com.haohai.platform.fireforestplatform.ui.activity.NewsInfoActivity;
import com.haohai.platform.fireforestplatform.ui.activity.RangerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.banner.BannerBean;
import com.haohai.platform.fireforestplatform.ui.banner.ImageAdapter;
import com.haohai.platform.fireforestplatform.ui.cell.MainDeviceListDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.MainDevice;
import com.haohai.platform.fireforestplatform.ui.multitype.MainFgMenu;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.MainFgMenuViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.DialogTreeViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FgMainViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.koushikdutta.ion.Response;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.blankj.utilcode.util.ThreadUtils.runOnUiThread;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.LoggerFactory;

public class MainFragment extends BaseFragment<FgMain, FgMainViewModel> implements DialogTreeViewModel.VideoTreeCallback, MainDeviceListDialog.MainDeviceDialogListener {

    public static MainFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        init_();
        click_();

        //加载网络数据
        obtainViewModel().initData();

        return binding.getRoot();
    }

    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        //在线离线设备数据
        obtainViewModel().mainDeviceList.observe(requireActivity(), this::updateMainDeviceList);
        //Banner数据
        obtainViewModel().bannerList.observe(requireActivity(), this::initBanner);
        //Menu数据
        obtainViewModel().menuList.observe(requireActivity(), this::refreshMenus);
        //设备数据
        obtainViewModel().deviceNumber.observe(requireActivity(), integer -> {
            binding.deviceNumber.setText(String.valueOf(integer));
            Integer value = obtainViewModel().deviceOnline.getValue();
            if(value !=null){
                binding.deviceOnline.setText(String.valueOf(value));
                binding.deviceOffline.setText(String.valueOf(integer-value));
            }
        });
        obtainViewModel().deviceOnline.observe(requireActivity(), integer -> {
            binding.deviceOnline.setText(String.valueOf(integer));
            Integer value = obtainViewModel().deviceNumber.getValue();
            if(value !=null){
                binding.deviceNumber.setText(String.valueOf(value));
                binding.deviceOffline.setText(String.valueOf(value-integer));
            }
        });
        //Chart数据
        obtainViewModel().handleData.observe(requireActivity(), aFloat -> refreshChart());
        //天气数据
        obtainViewModel().weatherState.observe(requireActivity(), integer -> weatherRefresh());
    }

    private void updateMainDeviceList(List<MainDevice> mainDevices) {
        mainDeviceListDialog.setMainDeviceList(mainDevices);
    }

    private void weatherRefresh() {
        //设置背景色
        binding.wbCurrent.setBackgroundColor(0);
        //设置填充透明度
        binding.wbCurrent.getBackground().setAlpha(0);
        binding.wbCurrent.setScrollBarSize(0);
        binding.wbCurrent.loadUrl(obtainViewModel().weatherUrl);
        binding.wbCurrent.setVisibility(View.VISIBLE);
        binding.tvCurrent.setText(obtainViewModel().tempStr);
        binding.tvTempBy.setText(obtainViewModel().tempByStr);
        binding.tvText.setText(obtainViewModel().weatherStr);
        binding.tvDate.setText(" " + obtainViewModel().weatherLocationStr + " " +obtainViewModel().weatherDateStr+"更新");
    }

    @SuppressLint("SetTextI18n")
    private void refreshChart() {
        Log.e("TAG", "refreshChart: ");
        List<SliceValue> values = new ArrayList<>();
        SliceValue sliceValue = new SliceValue(obtainViewModel().handle, Color.parseColor("#eba461"));
        values.add(sliceValue);
        SliceValue sliceValue_no = new SliceValue(obtainViewModel().noHandle, Color.parseColor("#5e77fb"));
        values.add(sliceValue_no);

        PieChartData data = new PieChartData(values);
        data.setHasLabels(true);
        data.setHasLabelsOnlyForSelected(true);
        data.setHasLabelsOutside(false);
        data.setHasCenterCircle(true);

        data.setSlicesSpacing(6);

        // Get font size from dimens.xml and convert it to sp(library uses sp values).
        data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                14));

        int handle_percent = 0;
        if(obtainViewModel().handle+obtainViewModel().noHandle != 0){
            handle_percent = parsePercent(obtainViewModel().handle/(obtainViewModel().handle+obtainViewModel().noHandle));
        }
        int no_handle_percent = 100 - handle_percent;
        binding.chart2.setPieChartData(data);
        binding.warnCount.setText(String.valueOf((int)(obtainViewModel().handle+obtainViewModel().noHandle)));
        binding.handleCount.setText(String.valueOf((int)(obtainViewModel().handle)));
        Log.e("TAG", "refreshChart: " + (int) (obtainViewModel().handle));
        binding.handlePercent.setText(handle_percent+"%");
        binding.noHandleCount.setText(String.valueOf((int)(obtainViewModel().noHandle)));
        binding.noHandlePercent.setText(no_handle_percent+"%");
    }

    private int parsePercent(float value) {
        float v = value*100;
        String s = String.valueOf(v);
        int index = s.indexOf(".");

        HhLog.e("--" + value);
        HhLog.e("--" + s);
        return Integer.parseInt((index == -1)?s:s.substring(0, index));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshMenus(List<MainFgMenu> menus) {
        obtainViewModel().items.clear();
        if (menus.size()>0){
            obtainViewModel().items.addAll(menus);
        }else {
            obtainViewModel().items.add(new Empty());
        }

        assertAllRegistered(obtainViewModel().adapter,obtainViewModel().items);
        obtainViewModel().adapter.notifyDataSetChanged();
    }

    @Override
    protected void setupViewModel() {
        binding.setLifecycleOwner(this);
        binding.setFragmentModel(obtainViewModel());
        obtainViewModel().start(getContext());
    }

    @Override
    public int bindLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public FgMainViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(FgMainViewModel.class);
    }


    private void init_() {
        //权限
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.APP_BANNER)){
            binding.banner.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.APP_VIDEO)){
            binding.deviceView.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.APP_ALARM_INFO)){
            binding.alarmTitle.setVisibility(View.GONE);
            binding.alarmContent.setVisibility(View.GONE);
        }
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.APP_WEATHER)){
            binding.weatherTitle.setVisibility(View.GONE);
            binding.weatherContent.setVisibility(View.GONE);
        }
        initMainDeviceListDialog();
        //下拉刷新
        binding.mainSmart.setRefreshFooter(new BallPulseFooter(requireActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        binding.mainSmart.setRefreshHeader(new MaterialHeader(requireActivity()).setShowBezierWave(true));
        binding.mainSmart.setEnableLoadMore(false);
        //设置样式后面的背景颜色
        binding.mainSmart.setPrimaryColorsId(R.color.theme_color, android.R.color.white)
                .setBackgroundColor(getResources().getColor(R.color.background_colorf3));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.mainSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().initData();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                refreshLayout.finishLoadMore(1000);
            }
        });

        //设置RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(),4);
        binding.gridView.setLayoutManager(gridLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        ((FgMain) binding).gridView.setHasFixedSize(true);
        ((FgMain) binding).gridView.setNestedScrollingEnabled(false);

        MainFgMenuViewBinder menuViewBinder = new MainFgMenuViewBinder(requireActivity());
        menuViewBinder.setListener(mainFgMenu -> {
            if(mainFgMenu.getTitle().equals("火情上报")){
                requireActivity().startActivity(new Intent(requireActivity(), FireUploadActivity.class));
            }
            if(mainFgMenu.getTitle().equals("隐患排查")){
                requireActivity().startActivity(new Intent(requireActivity(), HiddenDangerActivity.class));
            }
            if(mainFgMenu.getTitle().equals("视频监控")){
                //EventBus.getDefault().post(new MainTabChange(1));
                /*CommonData.videoAddingIndex = 1;
                treeDialog.show();*/
                obtainViewModel().mainDeviceStatus = "";
                obtainViewModel().getMainDeviceData();
                mainDeviceListDialog.show();
            }
            if(mainFgMenu.getTitle().equals("报警管理")){
                EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(requireContext(),SPValue.mapIndex,3),"oneBody"));
            }
            if(mainFgMenu.getTitle().equals("卫星遥感")){
                EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(requireActivity(),SPValue.mapIndex,3),"satellite"));
            }
            if(mainFgMenu.getTitle().equals("任务单")){
                requireActivity().startActivity(new Intent(requireActivity(), TaskActivity.class));
            }
            if(mainFgMenu.getTitle().equals("防火员")){
                requireActivity().startActivity(new Intent(requireActivity(), RangerActivity.class));
            }
            if(mainFgMenu.getTitle().equals("要闻")){
                requireActivity().startActivity(new Intent(requireActivity(), NewsActivity.class));
            }
            if(mainFgMenu.getTitle().equals("考勤管理")){
                requireActivity().startActivity(new Intent(requireActivity(), SignMonthActivity.class));
            }
            if(mainFgMenu.getTitle().equals("天气")){
                requireActivity().startActivity(new Intent(requireActivity(), WeatherActivity.class));
            }
            if(mainFgMenu.getTitle().equals("融合通信")){
                checkPermissionAndLoginNuSDK();
                obtainViewModel().loading.postValue(new LoadingEvent(true,"连接中.."));
            }
        });
        obtainViewModel().adapter.register(MainFgMenu.class,menuViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(requireActivity()));
        ((FgMain) binding).gridView.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(((FgMain) binding).gridView, obtainViewModel().adapter);

        //设置Chart
        binding.chart2.setOnValueTouchListener(new ValueTouchListener());

    }

    OnRequestResultListener onLoginResultListener = new OnRequestResultListener()
    {

        @Override
        public void onResult(Response<String> response, NiuxinResultInfo info)
        {
            // 登录出现异常错误
            if (info == null)
            {
                runOnUiThread(() ->
                {
                    ToastUtils.xxxif(R.string.toast_login_fail_by_network);
                    obtainViewModel().loading.postValue(new LoadingEvent(false));
                });
            } else if (response != null)
            {
                NiuxinLoginInfo loginInfo = (NiuxinLoginInfo) info;

                if (loginInfo.isLoginSuccessed())
                {
                    //再次检测确认权限通过并成功登录后跳转融合通信主界面
                    requireActivity().startActivity(new Intent(requireActivity(), NuSDKMainActivity.class));

                } else
                {
                    runOnUiThread(() ->
                    {
                        String error = ErrorCode.getNiuxinErrorString(
                                loginInfo.status.code);
                        ToastUtils.xxxdo(getString(R.string.toast_login_fail_by_code) + error);
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                    });
                }
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        obtainViewModel().loading.postValue(new LoadingEvent(false));
    }

    private void checkPermissionAndLoginNuSDK()
    {
        final boolean needsResetLogback;
        needsResetLogback = !PermissionsUtil.hasPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        PermissionsUtil.requestNecessaryPermission(new PermissionListener()
        {
            @Override
            public void permissionGranted(
                    @NonNull String[] permission)
            {
                if(needsResetLogback)
                {
                    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                    loggerContext.reset();
                    ContextInitializer ci = new ContextInitializer(loggerContext);
                    try
                    {
                        ci.autoConfig();
                    } catch (JoranException e)
                    {
                        e.printStackTrace();
                    }
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(Settings.System.canWrite(Utils.getApp()))
                    {
                        loginNuSDK();
                    }else
                    {
                        //再次申请
                        PermissionsUtil.requestWriteSettingsPermission(requireActivity(),null);
                    }
                }else
                {
                    loginNuSDK();
                }
            }

            @Override
            public void permissionDenied(
                    @NonNull String[] permission)
            {
                com.blankj.utilcode.util.ToastUtils.showShort(getString(R.string.auth_must_grant_necessary_permission));
            }
        });
    }

    private void loginNuSDK() {
        String phone = (String) SPUtils.get(HhApplication.getInstance(), SPValue.phone, "");
        HhLog.e("nuLogin " + phone + "," + URLConstant.NU_SDK_PASSWORD);
        //NuSDK融合通信登录
        Api.getApiAuth().saveServerUrlData(URLConstant.NU_SDK_IP, URLConstant.NU_SDK_PORT, "", "", false);
        Api.getApiAuth().login(phone, URLConstant.NU_SDK_PASSWORD, Api.getApiAuth().getImeiNum(phone),
                true, onLoginResultListener);
    }

    @Override
    public void onMainDeviceDialogRefresh() {
        obtainViewModel().getMainDeviceData();
    }

    @Override
    public void onMainDeviceDialogItemClick(MainDevice mainDevice) {
        if(!Objects.equals(mainDevice.getIsOnline(), "1")){
            Toast.makeText(requireActivity(), "设备离线", Toast.LENGTH_SHORT).show();
            return;
        }
        List<MainDevice.CameraDTOS> cameraDTOS = mainDevice.getCameraDTOS();
        /*for (int i = 0; i < cameraDTOS.size(); i++) {
            MainDevice.CameraDTOS cameraModel = cameraDTOS.get(i);
            cameraModel.setChannelId(mainDevice.getId());
        }*/
        String left = "";
        String right = "";
        if(cameraDTOS.size()==1){
            mainDeviceListDialog.cancel();
            MainDevice.CameraDTOS dto_left = cameraDTOS.get(0);
            left= dto_left.getName();
            TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(requireActivity().getResources().getColor(R.color.c7));
            MessageDialog.show(mainDevice.getName(), "",left)
                    .setButtonOrientation(LinearLayout.VERTICAL)
                    .setOkTextInfo(okTextInfo)
                    .setOkButtonClickListener((dialog, v1) -> {
                        mainDeviceListDialog.cancel();
                        CommonData.videoAddingIndex = 1;
                        obtainViewModel().getStream(dto_left.getId(),dto_left.getControlDeviceId(),dto_left.getMonitorId());
                        return false;
                    })
                    .setCancelable(true);
        }
        if(cameraDTOS.size()==2){
            mainDeviceListDialog.cancel();
            MainDevice.CameraDTOS dto_left = cameraDTOS.get(0);
            left= dto_left.getName();
            MainDevice.CameraDTOS dto_right = cameraDTOS.get(1);
            right= dto_right.getName();
            TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(requireActivity().getResources().getColor(R.color.c7));
            MessageDialog.show(mainDevice.getName(), "",left,right)
                    .setButtonOrientation(LinearLayout.VERTICAL)
                    .setOkTextInfo(okTextInfo)
                    .setCancelTextInfo(okTextInfo)
                    .setOkButtonClickListener((dialog, v1) -> {
                        mainDeviceListDialog.cancel();
                        CommonData.videoAddingIndex = 1;
                        obtainViewModel().getStream(dto_left.getId(),dto_left.getControlDeviceId(),dto_left.getMonitorId());
                        return false;
                    })
                    .setCancelButtonClickListener((dialog, v1) -> {
                        mainDeviceListDialog.cancel();
                        CommonData.videoAddingIndex = 1;
                        obtainViewModel().getStream(dto_right.getId(),dto_right.getControlDeviceId(),dto_right.getMonitorId());
                        return false;
                    })
                    .setCancelable(true);
        }

    }

    private static class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {

        }

        @Override
        public void onValueDeselected() {

        }

    }

    private void initBanner(List<BannerBean> bannerBeanList) {
        binding.banner
                .addBannerLifecycleObserver(this)
                .setAdapter(new ImageAdapter(bannerBeanList))
                .setIndicator(new CircleIndicator(requireActivity()))
                .setIndicatorGravity(IndicatorConfig.Direction.RIGHT)
                .setIndicatorMargins(new IndicatorConfig.Margins(0,0,50,30))
                .setBannerRound(12).setLoopTime(5000)
                .setOnBannerListener((data, position) -> {
                    //startActivity(new Intent(requireActivity(),NewsActivity.class));
                    try{
                        List<BannerBean> value = obtainViewModel().bannerList.getValue();
                        BannerBean bannerBean = value.get(position);
                        Intent intent = new Intent(requireActivity(), NewsInfoActivity.class);
                        intent.putExtra("id",bannerBean.getId());
                        if(!bannerBean.getId().isEmpty()){
                            startActivity(intent);
                        }
                    }catch (Exception e){
                        HhLog.e(e.getMessage());
                    }
                });
    }

    private void click_() {
        //消息
        binding.btnMessage.setOnClickListener(v -> {
            EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(requireActivity(),SPValue.messageIndex,2)));
        });
        //监控设备
        binding.deviceMonitorView.setOnClickListener(v -> {
            obtainViewModel().mainDeviceStatus = "";
            obtainViewModel().getMainDeviceData();
            mainDeviceListDialog.show();
        });
        //在线数
        binding.deviceOnlineView.setOnClickListener(v -> {
            obtainViewModel().mainDeviceStatus = "1";
            obtainViewModel().getMainDeviceData();
            mainDeviceListDialog.show();
        });
        //离线数
        binding.deviceOfflineView.setOnClickListener(v -> {
            obtainViewModel().mainDeviceStatus = "0";
            obtainViewModel().getMainDeviceData();
            mainDeviceListDialog.show();
        });
        //报警信息-查看更多
        binding.moreWarn.setOnClickListener(v -> {
            EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(requireActivity(),SPValue.mapIndex,3),"oneBody"));
        });
        //天气信息-查看更多
        binding.moreWeather.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(requireActivity(), WeatherActivity.class));
        });
    }


    private MainDeviceListDialog mainDeviceListDialog;
    private void initMainDeviceListDialog() {
        mainDeviceListDialog = new MainDeviceListDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = mainDeviceListDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        mainDeviceListDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.8);
        dialogWindow.setAttributes(lp);
        mainDeviceListDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mainDeviceListDialog.create();
        }
    }

    @Override
    public void onLoading() {
        obtainViewModel().loading.setValue(new LoadingEvent(true, "加载中.."));
    }

    @Override
    public void finishLoading() {
        obtainViewModel().loading.setValue(new LoadingEvent(false));
    }


    @Override
    public void modelRefresh() {

    }


    @Override
    public void onStart() {
        super.onStart();
    }
}