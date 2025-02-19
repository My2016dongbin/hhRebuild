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
import com.haohai.platform.fireforestplatform.databinding.FgDanger;
import com.haohai.platform.fireforestplatform.ui.activity.AddingDangerActivity;
import com.haohai.platform.fireforestplatform.ui.bean.ParamsDancer;
import com.haohai.platform.fireforestplatform.ui.multitype.Danger;
import com.haohai.platform.fireforestplatform.ui.multitype.DangerViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.DangerViewModel;
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

public class DangerFragment extends BaseFragment<FgDanger, DangerViewModel> implements DangerViewBinder.OnItemClickListener {

    public static DangerFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        DangerFragment fragment = new DangerFragment();
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
        return R.layout.fragment_danger;
    }

    @Override
    public DangerViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(DangerViewModel.class);
    }


    private void init_() {
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

        DangerViewBinder messageViewBinder = new DangerViewBinder(requireActivity());
        messageViewBinder.setListener(this);
        obtainViewModel().adapter.register(Danger.class, messageViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(requireActivity()));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    @Override
    public void onItemClick(Danger message) {
        CommonData.danger = message;
        Intent intent = new Intent(requireActivity(), AddingDangerActivity.class);
        intent.putExtra("edit", true);
        intent.putExtra("read", true);
        intent.putExtra("danger", true);
        startActivity(intent);
    }

    @Override
    public void onEditClick(Danger message) {
        CommonData.danger = message;
        Intent intent = new Intent(requireActivity(), AddingDangerActivity.class);
        intent.putExtra("edit", true);
        intent.putExtra("read", false);
        intent.putExtra("danger", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Danger message) {
        TextInfo okTextInfo = new TextInfo();
        okTextInfo.setFontColor(requireActivity().getResources().getColor(R.color.c7));
        MessageDialog.show("温馨提示", "确定要删除这条数据吗？", "确定", "取消")
                .setButtonOrientation(LinearLayout.VERTICAL)
                .setOkTextInfo(okTextInfo)
                .setCancelTextInfo(okTextInfo)
                .setOtherTextInfo(okTextInfo)
                .setOkButtonClickListener((dialog, v1) -> {
                    remove_(message);
                    return false;
                })
                .setCancelButtonClickListener((dialog, v2) -> {

                    return false;
                })
                .setOnBackgroundMaskClickListener((dialog, v12) -> {
                    return false;
                })
                .setCancelable(true);
    }

    private void remove_(Danger message) {
        RequestParams params = new RequestParams(URLConstant.POST_HIDDEN_DANGER_REMOVE);
        ArrayList<String> ids = new ArrayList<>();
        ids.add(message.getId());
        ParamsDancer paramsDancer = new ParamsDancer(ids, message.getRiskType());
//        params.addBodyParameter("ids", ids.toString());
//        params.addBodyParameter("riskName", message.getRiskType());
        params.setBodyContent(new Gson().toJson(paramsDancer));
        HhHttp.postX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("POST_HIDDEN_DANGER_REMOVE params " + URLConstant.POST_HIDDEN_DANGER_REMOVE);
                HhLog.e("POST_HIDDEN_DANGER_REMOVE params " + params.toString());
                HhLog.e("POST_HIDDEN_DANGER_REMOVE params " + new Gson().toJson(paramsDancer));
                HhLog.e("POST_HIDDEN_DANGER_REMOVE result " + result);
                if(result.contains("200")){
                    Toast.makeText(requireActivity(), "操作成功", Toast.LENGTH_SHORT).show();
                    obtainViewModel().postData(true);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
