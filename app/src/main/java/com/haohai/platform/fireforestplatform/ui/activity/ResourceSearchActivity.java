package com.haohai.platform.fireforestplatform.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityResourceBinding;
import com.haohai.platform.fireforestplatform.ui.cell.GridChooseDialog;
import com.haohai.platform.fireforestplatform.ui.cell.SatelliteSearchDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Grid;
import com.haohai.platform.fireforestplatform.ui.multitype.Resource;
import com.haohai.platform.fireforestplatform.ui.multitype.ResourceType;
import com.haohai.platform.fireforestplatform.ui.multitype.ResourceViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ResourceViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class ResourceSearchActivity extends BaseLiveActivity<ActivityResourceBinding, ResourceViewModel> implements ResourceViewBinder.OnItemClickListener, INaviInfoCallback, GridChooseDialog.GridChooseDialogListener {

    private GridChooseDialog gridChooseDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().postType();
        obtainViewModel().initGrid();
    }

    private void init_() {
        binding.topBar.title.setText("资源检索");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.newsSmart.setRefreshHeader(new ClassicsHeader(this));
        binding.newsSmart.setEnableLoadMore(false);
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.newsSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page=1;
                obtainViewModel().postResourceByApiUrl();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page++;
                obtainViewModel().postResourceByApiUrl();
                refreshLayout.finishLoadMore(1000);
            }
        });

        ResourceViewBinder resourceViewBinder = new ResourceViewBinder(this);
        resourceViewBinder.setListener(this);
        obtainViewModel().adapter.register(Resource.class, resourceViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);


        gridChooseDialog = new GridChooseDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = gridChooseDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        gridChooseDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        gridChooseDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gridChooseDialog.create();
        }
    }

    private void bind_() {
        binding.typeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_fire_type_real) {
                    @Override
                    public void onBind(final CustomDialog dialog, View v) {
                        LinearLayout ll_type;
                        TextView text_close;
                        TextView text_confirm;
                        ll_type = v.findViewById(R.id.ll_type);
                        text_close = v.findViewById(R.id.text_close);
                        text_confirm = v.findViewById(R.id.text_confirm);
                        ll_type.removeAllViews();
                        for (int i = 0; i < obtainViewModel().resourceTypes.size(); i++) {
                            ResourceType resource = obtainViewModel().resourceTypes.get(i);
                            View p_ = LayoutInflater.from(ResourceSearchActivity.this).inflate(R.layout.item_fire_type_real, null);
                            LinearLayout all = p_.findViewById(R.id.all);
                            ImageView icon = p_.findViewById(R.id.icon);
                            TextView title = p_.findViewById(R.id.title);
                            title.setText(resource.getName());
                            if (resource.isChecked()) {
                                icon.setImageResource(R.drawable.yes);
                            } else {
                                icon.setImageResource(R.drawable.no);
                            }
                            all.setOnClickListener(v15 -> {
                                if (!resource.isChecked()) {
                                    obtainViewModel().resourceType = resource;
                                    for (int m = 0; m < obtainViewModel().resourceTypes.size(); m++) {
                                        obtainViewModel().resourceTypes.get(m).setChecked(false);
                                    }
                                }
                                resource.setChecked(!resource.isChecked());
                                onBind(dialog, v);
                            });

                            ll_type.addView(p_);
                        }
                        text_close.setOnClickListener(v13 -> {
                            dialog.dismiss();
                        });
                        text_confirm.setOnClickListener(v14 -> {
                            int tag = 0;
                            for (int i = 0; i < obtainViewModel().resourceTypes.size(); i++) {
                                ResourceType resource = obtainViewModel().resourceTypes.get(i);
                                if(resource.isChecked()){
                                    tag++;
                                }
                            }
                            if(tag==0){
                                Toast.makeText(ResourceSearchActivity.this, "请选择资源类型", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            binding.typeName.setText(obtainViewModel().resourceType.getName());
                            dialog.dismiss();
                            obtainViewModel().postResourceByApiUrl();
                        });

                    }
                }).setOnBackgroundMaskClickListener((dialog, v12) -> {
                    return false;
                }).setCancelable(true).setMaskColor(getResources().getColor(R.color.trans_mask));
            }
        });
        binding.areaName.setOnClickListener(v -> {
            gridChooseDialog.show();
        });
    }

    @Override
    protected ActivityResourceBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_resource);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ResourceViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ResourceViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().loadMore.observe(this, integer -> {
            if(integer == 0){
                binding.newsSmart.setEnableLoadMore(false);
            }else if(integer == 1){
                binding.newsSmart.setEnableLoadMore(true);
            }
        });
        obtainViewModel().resourceTypeList.observe(this, this::resourceTypeChanged);
        obtainViewModel().resourceList.observe(this, this::resourceListChanged);
        obtainViewModel().gridList.observe(this, this::gridListChanged);
    }

    private void gridListChanged(List<Grid> grids) {
        gridChooseDialog.setGridList(grids);
    }

    private void resourceListChanged(List<Resource> resources) {

    }

    private void resourceTypeChanged(List<ResourceType> resourceTypes) {
        if(resourceTypes!=null && resourceTypes.size()>0){
            ResourceType resourceType = resourceTypes.get(0);
            HhLog.e("resourceTypeChanged " + resourceType);
            binding.typeName.setText(resourceType.getName());
            obtainViewModel().apiUrl = resourceType.getApiUrl();
            obtainViewModel().resourceType = resourceType;

            obtainViewModel().postResourceByApiUrl();
        }
    }

    @Override
    public void onItemClick(Resource resource) {

    }
    @Override
    public void onGuideClick(Resource resource) {
        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);

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

    @Override
    public void onGridChooseDialogRefresh() {

    }

    @Override
    public void onGridChoose(int index) {
        if(index == -1){
            //默认
            binding.areaName.setText("区域");
            obtainViewModel().gridNo = null;
        }else{
            Grid grid = obtainViewModel().gridGridList.get(index);
            binding.areaName.setText(grid.getName());
            obtainViewModel().gridNo = grid.getGridNo();
        }
        obtainViewModel().postResourceByApiUrl();
    }
}