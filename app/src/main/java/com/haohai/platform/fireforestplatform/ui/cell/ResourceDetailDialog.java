package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.DialogOneBodyDetailListBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogResourceDetailBinding;
import com.haohai.platform.fireforestplatform.helper.DialogHelper;
import com.haohai.platform.fireforestplatform.old.ResourceAddActivity;
import com.haohai.platform.fireforestplatform.old.bean.MapDialogDismiss;
import com.haohai.platform.fireforestplatform.ui.bean.Resource;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Objects;

public class ResourceDetailDialog extends Dialog implements INaviInfoCallback {

    private final Context context;
    private ResourceDetailDialogListener dialogListener;
    private final DialogResourceDetailBinding binding;
    private Resource resource;

    public ResourceDetailDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_resource_detail, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(ResourceDetailDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
        updateData();
    }

    @SuppressLint("SetTextI18n")
    private void updateData() {
        boolean manager = (boolean) SPUtils.get(context, SPValue.manager, false);
        /*if(resource.getApiUrl()==null || !manager){
            //binding.llPlay.setVisibility(View.GONE);
            //binding.delete.setVisibility(View.GONE);
            binding.name.setText(resource.getResourceName());
        }else{
            //binding.llPlay.setVisibility(View.VISIBLE);
            //binding.delete.setVisibility(View.VISIBLE);
            binding.name.setText(resource.getName());
        }*/
        if(resource.getResourceName()==null || resource.getResourceName().isEmpty()){
            binding.name.setText(resource.getName());
        }else{
            binding.name.setText(resource.getResourceName());
        }
        try{
            binding.lngLat.setText(parse10(resource.getPosition().getLng())+","+parse10(resource.getPosition().getLat()));
        }catch (Exception e){
            binding.lngLat.setText("暂无数据");
        }
        binding.address.setText(CommonUtil.parseNullString(resource.getAddress(),"暂无地址"));
    }

    private String parse10(String s) {
        if(s==null){
            return "";
        }
        if(s.length()>10){
            s = s.substring(0,10);
        }
        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void guide() {
        NaviSetting.updatePrivacyShow(context, true, true);
        NaviSetting.updatePrivacyAgree(context, true);

        //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
        //AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
        //启动导航组件
        //AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);


        double[] doubles_start = LatLngChangeNew.calBD09toGCJ02(CommonData.lat, CommonData.lng);
        LatLng latLng_start = new LatLng(doubles_start[0],doubles_start[1]);
        double[] doubles_end = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(resource.getPosition().getLat()), Double.parseDouble(resource.getPosition().getLng()));
        LatLng latLng_end = new LatLng(doubles_end[0],doubles_end[1]);


        Poi start = new Poi("", latLng_start, "");
        Poi end = new Poi(resource.getName(), latLng_end, "");
        AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
        params.setUseInnerVoice(true);
        AmapNaviPage.getInstance().showRouteActivity(HhApplication.getInstance(), params, this);

    }
    private void bind_() {
        binding.edit.setOnClickListener(v -> {
            editRes(resource.getResourceType(),resource.getId(),resource.getObj(),resource.getType().replace("/api/",""));
        });
        binding.delete.setOnClickListener(v -> {
            deleteRes(resource.getResourceType(),resource.getId(),resource.getType().replace("/api/",""));
        });
        binding.guide.setOnClickListener(v -> guide());
    }


    private void editRes(String type, String id, JSONObject resourceObj,String tp) {
        Intent intent = new Intent(context, ResourceAddActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("type",tp);
        intent.putExtra("resourceObj",resourceObj.toString());
        context.startActivity(intent);
    }

    private void deleteRes(String type,String id,String tp) {
        if(Objects.equals(type, "foreastRoom")){
            type = "materialRepository";
        }
        JSONObject resObj =new JSONObject();
        try {
            resObj.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DialogHelper.getInstance().show(context,"正在删除");
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/"+ tp);
        params.setAsJsonContent(true);
        params.setBodyContent(resObj.toString());
        params.addHeader("Authorization", "bearer " + CommonData.token);
        String finalType = tp;
        HhLog.e("params " + params.toString());
        HhLog.e("params " + resObj.toString());
        x.http().request(HttpMethod.DELETE,params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "deleteRes Success: " + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if(Objects.equals(code, "200")){
                        //Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();

                        DialogHelper.getInstance().close();
                        Toast.makeText(context, "资源点删除成功,地图即将重置", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new MapDialogDismiss(finalType));
                            }
                        },1000);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "deleteRes onError: " + ex.toString() );
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                DialogHelper.getInstance().close();
            }
        });
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


    public interface ResourceDetailDialogListener{
        void onResourceDetailDialogRefresh();
    }
}
