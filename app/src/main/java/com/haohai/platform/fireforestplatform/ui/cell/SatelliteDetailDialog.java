package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.NaviSetting;
import com.amap.api.navi.model.AMapNaviLocation;
import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteDetailListBinding;
import com.haohai.platform.fireforestplatform.ui.activity.PhotoViewerActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.SatelliteFire;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.Objects;

public class SatelliteDetailDialog extends Dialog implements INaviInfoCallback {

    private final Context context;
    private SatelliteDetailDialogListener dialogListener;
    private final DialogSatelliteDetailListBinding binding;
    private SatelliteFire satelliteFire;

    public SatelliteDetailDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_satellite_detail, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(SatelliteDetailDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setSatelliteFire(SatelliteFire satelliteFire) {
        this.satelliteFire = satelliteFire;
        updateUi();
    }

    @SuppressLint("SetTextI18n")
    private void updateUi() {
        binding.address.setText(satelliteFire.getFormattedAddress());
        binding.time.setText(CommonUtil.parse19String(satelliteFire.getObservationDatetime(),""));
        binding.latLng.setText(satelliteFire.getLongitude()+","+satelliteFire.getLatitude());
        binding.real.setText(satelliteFire.getCredibility());
        binding.fireArea.setText(satelliteFire.getArea());
        binding.xArea.setText(satelliteFire.getPixelArea());
        binding.xCount.setText(satelliteFire.getPixelNumber());
        binding.pince.setText(satelliteFire.getObservationFrequency());
        binding.type.setText(satelliteFire.getLandType());
        binding.dateFrom.setText(satelliteFire.getSatellite());
        binding.fireNo.setText(satelliteFire.getFireNo());
        Glide.with(context).load(URLConstant.SATELLITE_IMAGE + satelliteFire.getLightImageAddress()).error(R.drawable.ic_no_pic)
                .placeholder(R.drawable.ic_jaizai).into(binding.image1);
        Glide.with(context).load(URLConstant.SATELLITE_IMAGE + satelliteFire.getIrImageAddress()).error(R.drawable.ic_no_pic)
                .placeholder(R.drawable.ic_jaizai).into(binding.image2);
        binding.image1.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoViewerActivity.class);
            intent.putExtra("url", URLConstant.SATELLITE_IMAGE + satelliteFire.getLightImageAddress());
            context.startActivity(intent);
        });
        binding.image2.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoViewerActivity.class);
            intent.putExtra("url",URLConstant.SATELLITE_IMAGE + satelliteFire.getIrImageAddress());
            context.startActivity(intent);
        });
        binding.guide.setOnClickListener(v -> {
            NaviSetting.updatePrivacyShow(context, true, true);
            NaviSetting.updatePrivacyAgree(context, true);

            //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
            //AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            //启动导航组件
            //AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);


            double[] doubles_start = LatLngChangeNew.calBD09toGCJ02(CommonData.lat, CommonData.lng);
            LatLng latLng_start = new LatLng(doubles_start[0],doubles_start[1]);
            double[] doubles_end = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(satelliteFire.getLatitude()), Double.parseDouble(satelliteFire.getLongitude()));
            LatLng latLng_end = new LatLng(doubles_end[0],doubles_end[1]);


            Poi start = new Poi("", latLng_start, "");
            Poi end = new Poi(satelliteFire.getFormattedAddress()+"卫星火点", latLng_end, "");
            AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            params.setUseInnerVoice(true);
            AmapNaviPage.getInstance().showRouteActivity(HhApplication.getInstance(), params, this);

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {

    }

    private void init_() {

    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviDirectionChanged(int i) {

    }

    @Override
    public void onDayAndNightModeChanged(int i) {

    }

    @Override
    public void onBroadcastModeChanged(int i) {

    }

    @Override
    public void onScaleAutoChanged(boolean b) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }


    public interface SatelliteDetailDialogListener{
        void onSatelliteDetailDialogRefresh();
    }
}
