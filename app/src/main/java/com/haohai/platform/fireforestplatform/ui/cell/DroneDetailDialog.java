package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

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
import com.haohai.platform.fireforestplatform.databinding.DialogDroneDetailListBinding;
import com.haohai.platform.fireforestplatform.ui.activity.PhotoViewerActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.DroneFire;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.StringData;

import java.util.Objects;

public class DroneDetailDialog extends Dialog implements INaviInfoCallback {

    private final Context context;
    private final DialogDroneDetailListBinding binding;
    private DroneFire droneFire;

    public DroneDetailDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_drone_detail, null, false);
        setContentView(binding.getRoot());
    }

    public void setDroneFire(DroneFire droneFire) {
        this.droneFire = droneFire;
        updateData();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateData() {
        binding.name.setText(parseName(droneFire));
        binding.date.setText(StringData.parse19(droneFire.getWriteTime()));
        binding.lngLat.setText(droneFire.getLongitude() + "、" + droneFire.getLatitude());
        binding.address.setText(droneFire.getAddress());
        binding.real.setText(parseReal(droneFire.getIsHandle(),droneFire.getIsReal()));
        Glide.with(context).load(droneFire.getImg1())
                .error(context.getResources().getDrawable(R.drawable.ic_no_pic))
                .into(binding.lightPic);
        Glide.with(context).load(droneFire.getImg2())
                .error(context.getResources().getDrawable(R.drawable.ic_no_pic))
                .into(binding.hotPic);
        binding.lightPic.setOnClickListener(v -> openPic(droneFire.getImg1()));
        binding.hotPic.setOnClickListener(v -> openPic(droneFire.getImg2()));
        binding.guide.setOnClickListener(v -> {
            NaviSetting.updatePrivacyShow(context, true, true);
            NaviSetting.updatePrivacyAgree(context, true);

            double[] doublesStart = LatLngChangeNew.calBD09toGCJ02(CommonData.lat, CommonData.lng);
            LatLng latLngStart = new LatLng(doublesStart[0], doublesStart[1]);
            double[] doublesEnd = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(droneFire.getLatitude()), Double.parseDouble(droneFire.getLongitude()));
            LatLng latLngEnd = new LatLng(doublesEnd[0], doublesEnd[1]);

            Poi start = new Poi("", latLngStart, "");
            Poi end = new Poi(droneFire.getAddress()!=null?droneFire.getAddress():"" + "火点", latLngEnd, "");
            AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            params.setUseInnerVoice(true);
            AmapNaviPage.getInstance().showRouteActivity(HhApplication.getInstance(), params, this);
        });
    }

    private void openPic(String url) {
        if(TextUtils.isEmpty(url)){
            Toast.makeText(context, "暂无报警图片", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, PhotoViewerActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    private String parseName(DroneFire droneFire) {
        if(!TextUtils.isEmpty(droneFire.getDeviceName())){
            return droneFire.getDeviceName();
        }
        if(!TextUtils.isEmpty(droneFire.getEventName())){
            return droneFire.getEventName();
        }
        return "";
    }

    private String parseReal(String isHandle,String isReal) {
        if(Objects.equals(isHandle, "0") || isReal == null || Objects.equals(isReal, "") || Objects.equals(isReal, "null")){
            return "未处理";
        }
        if(Objects.equals(isReal, "1")){
            return "真实火情";
        }
        if(Objects.equals(isReal, "0")){
            return "疑似火情";
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
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
}
