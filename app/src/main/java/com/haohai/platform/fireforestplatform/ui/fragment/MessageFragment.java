package com.haohai.platform.fireforestplatform.ui.fragment;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

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

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseFragment;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.FgMessage;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.EmergencyDetailActivity;
import com.haohai.platform.fireforestplatform.ui.activity.KKFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LevelFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.MessageDetailActivity;
import com.haohai.platform.fireforestplatform.ui.activity.MonitorFireMessageListActivity;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.ui.activity.SatelliteFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskListInfoActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Message;
import com.haohai.platform.fireforestplatform.ui.multitype.MessageViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FgMessageViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

public class MessageFragment extends BaseFragment<FgMessage, FgMessageViewModel> implements MessageViewBinder.OnItemClickListener {

    public static MessageFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        MessageFragment fragment = new MessageFragment();
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

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        obtainViewModel().postData(true);
    }

    ///未读消息数量
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageRefresh event) {
        obtainViewModel().postData(false);
    }

    ///Tab切换
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MainTabChange event) {
        int index = event.getIndex();
        if(index == 1){
            obtainViewModel().postData(false);
        }
    }

    private void bind_() {
        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog.show("温馨提示", "确定清除所有未读消息吗？","清除","取消")
                        .setButtonOrientation(LinearLayout.HORIZONTAL)
                        .setOkTextInfo(new TextInfo().setFontColor(requireActivity().getResources().getColor(R.color.text_color_red)))
                        .setOkButtonClickListener((dialog, v1) -> {
                            if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.MESSAGE_CLEAR)){
                                Toast.makeText(requireActivity(), "当前账号没有操作权限", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            obtainViewModel().clearMessage();
                            return false;
                        })
                        .setCancelable(true);
            }
        });
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
        return R.layout.fragment_message;
    }

    @Override
    public FgMessageViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(FgMessageViewModel.class);
    }


    private void init_() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.messageSmart.setRefreshHeader(new ClassicsHeader(requireActivity()));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.messageSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().postData(true);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });

        MessageViewBinder messageViewBinder = new MessageViewBinder(requireActivity());
        messageViewBinder.setListener(this);
        obtainViewModel().adapter.register(Message.class, messageViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(requireActivity()));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    @Override
    public void onItemClick(Message message) {

        /*Intent intent = new Intent(requireActivity(), MessageDetailActivity.class);
        startActivity(intent);
        CommonData.message = message;*/

        if(Objects.equals(message.getType(), "incident")){//事件/预警
            Intent intent = new Intent(requireActivity(), EmergencyDetailActivity.class);
            intent.putExtra("id",message.getTypeId());
            startActivity(intent);
            obtainViewModel().postRead(message);
        }
        if(Objects.equals(message.getType(), "audit")){//审核
            Intent intent = new Intent(requireActivity(), EmergencyDetailActivity.class);
            intent.putExtra("id",message.getTypeId());
            startActivity(intent);
            obtainViewModel().postRead(message);
        }
        if(Objects.equals(message.getType(), "task")){//任务
            Intent intent = new Intent(requireActivity(), TaskListInfoActivity.class);
            intent.putExtra("id",message.getTypeId());
            startActivity(intent);
            obtainViewModel().postRead(message);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
