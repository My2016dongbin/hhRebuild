package com.haohai.platform.fireforestplatform.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseFragment;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.FgExamine;
import com.haohai.platform.fireforestplatform.ui.activity.EmergencyDetailActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Examine;
import com.haohai.platform.fireforestplatform.ui.multitype.ExamineViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ExamineViewModel;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBackgroundMaskClickListener;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class ExamineFragment extends BaseFragment<FgExamine, ExamineViewModel> implements ExamineViewBinder.OnItemClickListener {

    public static ExamineFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        ExamineFragment fragment = new ExamineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        init_();
        bind_();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        obtainViewModel().postData(true);
    }

    private void bind_() {

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
        return R.layout.fragment_examine;
    }

    @Override
    public ExamineViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ExamineViewModel.class);
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

        ExamineViewBinder messageViewBinder = new ExamineViewBinder(requireActivity());
        messageViewBinder.setListener(this);
        obtainViewModel().adapter.register(Examine.class, messageViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(requireActivity()));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    @Override
    public void onItemClick(Examine message) {
        Intent intent = new Intent(requireActivity(), EmergencyDetailActivity.class);
        intent.putExtra("id",message.getId());
        intent.putExtra("examine",true);
        startActivity(intent);
    }

    private final String examineStr = "请输入审核意见";
    @Override
    public void onExamineClick(Examine message) {
        new InputDialog("审核结果", "请问审核是否通过？", "通过", "驳回", "取消","")
                .setCancelable(true)
                .setOkButtonClickListener(new OnInputDialogButtonClickListener<InputDialog>() {
                    @Override
                    public boolean onClick(InputDialog dialog, View v, String inputStr) {
                        if(inputStr.isEmpty()){
                            Toast.makeText(requireActivity(), examineStr, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        obtainViewModel().pass_(inputStr,message,"pass");
                        return false;
                    }
                })
                .setCancelButtonClickListener(new OnInputDialogButtonClickListener<InputDialog>() {
                    @Override
                    public boolean onClick(InputDialog dialog, View v, String inputStr) {
                        if(inputStr.isEmpty()){
                            Toast.makeText(requireActivity(), examineStr, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        obtainViewModel().pass_(inputStr,message,"reject");
                        return false;
                    }
                })
                .setInputHintText(examineStr)
                .setOnBackgroundMaskClickListener(new OnBackgroundMaskClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog dialog, View v) {
                        return false;
                    }
                })
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
