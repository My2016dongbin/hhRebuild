package com.haohai.platform.fireforestplatform.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityCallingListBinding;
import com.haohai.platform.fireforestplatform.old.HistoryLineActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.CallingList;
import com.haohai.platform.fireforestplatform.ui.multitype.CallingListViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.CallingListViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.CallingListViewModel;
import com.haohai.platform.fireforestplatform.utils.Action;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.FloatPermissionHelper;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

public class CallingListActivity extends BaseLiveActivity<ActivityCallingListBinding, CallingListViewModel> implements CallingListViewBinder.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().getTrees();
    }

    @Override
    protected void onResume() {
        super.onResume();

        CommonData.calling = false;

        //申请权限
        if (!FloatPermissionHelper.hasOverlayPermission(this)) {
            //悬浮窗权限
            CommonUtil.showConfirm(this, "为了更好的通话体验，是否现在去开启悬浮窗权限？", "去开启", "以后再说", new Action() {
                @Override
                public void click() {
                    FloatPermissionHelper.requestOverlayPermission(CallingListActivity.this);
                }
            });
        }else{
            /*//后台弹出权限
            if (!FloatPermissionHelper.canStartActivityFromBackground(this)) {
                CommonUtil.showConfirm(this, "为方便接听通话，是否现在去开启后台弹出权限？", "去开启", "以后再说", new Action() {
                    @Override
                    public void click() {
                        FloatPermissionHelper.openAutoStartSetting(CallingListActivity.this);
                    }
                });
            }*/
        }
    }

    private void init_() {
        binding.topBar.title.setText("视频通话");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.rlv.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        //下拉刷新
        binding.smart.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        binding.smart.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
        binding.smart.setEnableLoadMore(false);
        //设置样式后面的背景颜色
        binding.smart.setPrimaryColorsId(R.color.back_color_f8, R.color.back_color_f8)
                .setBackgroundColor(getResources().getColor(R.color.back_color_f8));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.smart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().getTrees();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });

        CallingListViewBinder callingListViewBinder = new CallingListViewBinder(this);
        callingListViewBinder.setListener(this);
        obtainViewModel().adapter.register(CallingList.class, callingListViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.rlv.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.rlv, obtainViewModel().adapter);
    }

    private void bind_() {
        CommonUtil.click(binding.start, new Action() {
            @Override
            public void click() {
                List<CallingList> list = new ArrayList<>();
                for (int i = 0; i < obtainViewModel().callingLists.size(); i++) {
                    CallingList model = obtainViewModel().callingLists.get(i);
                    if(model.isState()){
                        list.add(model);
                    }
                }
                CommonData.invitedUserList = list;
                CommonData.invitedUserListForDelete = new ArrayList<>();
                CommonData.invitedUserListForDelete.addAll(list);
                if(list.isEmpty()){
                    Toast.makeText(CallingListActivity.this, "请至少选择一个联系人", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(CallingListActivity.this, CallingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.putExtra("isCalling",true);
                CallingListActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected ActivityCallingListBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_calling_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public CallingListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(CallingListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }

    @Override
    public void onItemClick(CallingList callingList, boolean state) {
        try{
            int index = obtainViewModel().callingLists.indexOf(callingList);
            obtainViewModel().callingLists.get(index).setState(state);
            obtainViewModel().updateData();
        }catch (Exception e){
            //
        }
    }
}