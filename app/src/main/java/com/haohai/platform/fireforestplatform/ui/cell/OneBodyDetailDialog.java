package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.DialogOneBodyDetailListBinding;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.EmergencyDetailActivity;
import com.haohai.platform.fireforestplatform.ui.activity.PhotoViewerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.VideoStreamActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.StringData;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.footer.FalsifyFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Objects;

public class OneBodyDetailDialog extends Dialog implements INaviInfoCallback {

    private final Context context;
    private OneBodyDetailDialogListener dialogListener;
    private final DialogOneBodyDetailListBinding binding;
    private OneBodyFire oneBodyFire;
    public int isRelease = 2;  //0疑似火情  1是真实火情  2是未处理

    public OneBodyDetailDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_onebody_detail, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(OneBodyDetailDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setOneBodyFire(OneBodyFire oneBodyFire) {
        this.oneBodyFire = oneBodyFire;
        updateData();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateData() {
        binding.name.setText(oneBodyFire.getIncidentTitle());
        binding.date.setText(CommonUtil.parseDate(oneBodyFire.getIncidentTime()));
        binding.lngLat.setText(oneBodyFire.getEmergIncidentVersion().getLongtitude()+"、"+oneBodyFire.getEmergIncidentVersion().getLatitude());
        binding.address.setText(CommonUtil.parseNullString(oneBodyFire.getEmergIncidentVersion().getIncidentAddr(), "-"));
        binding.type.setText(CommonUtil.parseNullString(oneBodyFire.getIncidentTypeName(), "-"));
        binding.level.setText(CommonUtil.parseNullString(oneBodyFire.getIncidentLevel(), "-")+"级");
        binding.series.setText(CommonUtil.parseNullString(oneBodyFire.getEmergIncidentVersion().getSensitive(), "-"));
        binding.more.setOnClickListener(v -> {
            Intent intent = new Intent(context, EmergencyDetailActivity.class);
            intent.putExtra("id",oneBodyFire.getId());
            context.startActivity(intent);
        });
        //binding.refresh.setRefreshHeader(new ClassicsHeader(context));
        binding.refresh.setRefreshFooter(new FalsifyFooter(context));
        binding.refresh.setEnableRefresh(false);
        binding.refresh.setEnableLoadMore(true);

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(100);
                Intent intent = new Intent(context, EmergencyDetailActivity.class);
                intent.putExtra("id",oneBodyFire.getId());
                context.startActivity(intent);
            }
        });
        /*Glide.with(context).load(oneBodyFire.getPicPath1())
                .error(context.getResources().getDrawable(R.drawable.ic_no_pic))
                .into(binding.lightPic);
        Glide.with(context).load(oneBodyFire.getPicPath2())
                .error(context.getResources().getDrawable(R.drawable.ic_no_pic))
                .into(binding.hotPic);*/
        /*binding.lightVideo.setOnClickListener(v -> {
            if(oneBodyFire.getVideoPath1()==null || Objects.equals(oneBodyFire.getVideoPath1(), "")){
                Toast.makeText(context, "暂无可见光视频", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, VideoStreamActivity.class);
            intent.putExtra("url", Objects.requireNonNull(oneBodyFire).getVideoPath1());
            context.startActivity(intent);
        });
        binding.hotVideo.setOnClickListener(v -> {
            if(oneBodyFire.getVideoPath2()==null || Objects.equals(oneBodyFire.getVideoPath2(), "")){
                Toast.makeText(context, "暂无热成像视频", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, VideoStreamActivity.class);
            intent.putExtra("url", Objects.requireNonNull(oneBodyFire).getVideoPath2());
            context.startActivity(intent);
        });*/
        /*binding.lightPic.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoViewerActivity.class);
            intent.putExtra("url",Objects.requireNonNull(oneBodyFire).getPicPath1());
            context.startActivity(intent);
        });
        binding.hotPic.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoViewerActivity.class);
            intent.putExtra("url",Objects.requireNonNull(oneBodyFire).getPicPath2());
            context.startActivity(intent);
        });*/

        binding.guide.setOnClickListener(v -> {
            NaviSetting.updatePrivacyShow(context, true, true);
            NaviSetting.updatePrivacyAgree(context, true);

            //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
            //AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            //启动导航组件
            //AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);


            double[] doubles_start = LatLngChangeNew.calBD09toGCJ02(CommonData.lat, CommonData.lng);
            LatLng latLng_start = new LatLng(doubles_start[0],doubles_start[1]);
            double[] doubles_end = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(oneBodyFire.getEmergIncidentVersion().getLatitude()), Double.parseDouble(oneBodyFire.getEmergIncidentVersion().getLongtitude()));
            LatLng latLng_end = new LatLng(doubles_end[0],doubles_end[1]);


            Poi start = new Poi("", latLng_start, "");
            Poi end = new Poi(oneBodyFire.getEmergIncidentVersion().getIncidentAddr()+"火点", latLng_end, "");
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


    public interface OneBodyDetailDialogListener{
        void onOneBodyDetailDialogRefresh();
    }
}
