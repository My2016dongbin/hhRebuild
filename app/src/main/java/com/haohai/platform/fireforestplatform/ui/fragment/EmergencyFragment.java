package com.haohai.platform.fireforestplatform.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseFragment;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.FgEmergency;
import com.haohai.platform.fireforestplatform.databinding.FgMessage;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.event.OpenFilter;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.DragActivity;
import com.haohai.platform.fireforestplatform.ui.activity.EmergencyDetailActivity;
import com.haohai.platform.fireforestplatform.ui.activity.KKFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LevelFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.MonitorFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.SatelliteFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyFilter;
import com.haohai.platform.fireforestplatform.ui.cell.EmergencyFilterDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.Emergency;
import com.haohai.platform.fireforestplatform.ui.multitype.EmergencyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Message;
import com.haohai.platform.fireforestplatform.ui.multitype.MessageViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.ui.viewmodel.EmergencyViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FgMessageViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class EmergencyFragment extends BaseFragment<FgEmergency, EmergencyViewModel> implements EmergencyViewBinder.OnItemClickListener, EmergencyFilterDialog.EmergencyFilterListener {
    private EmergencyFilterDialog filterDialog;
    public static EmergencyFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        EmergencyFragment fragment = new EmergencyFragment();
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

        obtainViewModel().postData(true);
        return binding.getRoot();
    }


    private void bind_() {
        /*binding.move.setVisibility(View.GONE);
        binding.move.setOnClickListener(v -> {
            filterDialog.show();
        });*/
    }


    ///报警管理rightIcon --> 突发事件筛选
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(OpenFilter event) {
        filterDialog.show();
    }

    private void initFilterDialog() {
        filterDialog = new EmergencyFilterDialog(requireActivity(), R.style.ActionSheetDialogStyle);
        Window dialogWindow = filterDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        filterDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        filterDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            filterDialog.create();
        }
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
    }

    @Override
    public int bindLayoutId() {
        return R.layout.fragment_emergency;
    }

    @Override
    public EmergencyViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(EmergencyViewModel.class);
    }


    private void init_() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.messageSmart.setRefreshHeader(new ClassicsHeader(requireActivity()));
        binding.messageSmart.setRefreshFooter(new ClassicsFooter(requireActivity()));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.messageSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().pageNum=1;
                obtainViewModel().postData(true);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().pageNum++;
                obtainViewModel().postData(true);
                refreshLayout.finishLoadMore(1000);
            }
        });

        EmergencyViewBinder messageViewBinder = new EmergencyViewBinder(requireActivity());
        messageViewBinder.setListener(this);
        obtainViewModel().adapter.register(Emergency.class, messageViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(requireActivity()));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);

        initFilterDialog();
    }

    @Override
    public void onItemClick(Emergency message) {
        Intent intent = new Intent(requireActivity(), EmergencyDetailActivity.class);
        intent.putExtra("id",message.getId());
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onEmergencyFilterRefresh() {

    }

    @Override
    public void onEmergencyFilterResult(EmergencyFilter filter) {
        obtainViewModel().filter = filter;
        obtainViewModel().postData(true);
    }
}
