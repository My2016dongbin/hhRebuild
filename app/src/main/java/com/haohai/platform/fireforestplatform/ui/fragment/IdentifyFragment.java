package com.haohai.platform.fireforestplatform.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseFragment;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.FgIdentify;
import com.haohai.platform.fireforestplatform.ui.activity.IdentifyDetailActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Identify;
import com.haohai.platform.fireforestplatform.ui.multitype.IdentifyViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FgIdentifyViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class IdentifyFragment extends BaseFragment<FgIdentify, FgIdentifyViewModel> implements IdentifyViewBinder.OnItemClickListener {

    public static IdentifyFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        IdentifyFragment fragment = new IdentifyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        init_();
        bind_();

        return binding.getRoot();
    }

    private void bind_() {

    }

    @Override
    public void onResume() {
        super.onResume();
        obtainViewModel().postData(true);
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
        return R.layout.fragment_identify;
    }

    @Override
    public FgIdentifyViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(FgIdentifyViewModel.class);
    }


    private void init_() {
        assert getArguments() != null;
        obtainViewModel().args = getArguments().getString("args");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.messageSmart.setRefreshHeader(new ClassicsHeader(requireActivity()));
        binding.messageSmart.setRefreshFooter(new ClassicsFooter(requireActivity()));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.messageSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page = 1;
                obtainViewModel().postData(true);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page++;
                obtainViewModel().postData(true);
                refreshLayout.finishLoadMore(1000);
            }
        });

        IdentifyViewBinder messageViewBinder = new IdentifyViewBinder(requireActivity());
        messageViewBinder.setListener(this);
        obtainViewModel().adapter.register(Identify.class, messageViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(requireActivity()));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    @Override
    public void onItemClick(Identify message) {
        Intent intent = new Intent(requireActivity(), IdentifyDetailActivity.class);
        intent.putExtra("id",message.getId());
        startActivity(intent);
    }

    private void remove_(Identify message) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
