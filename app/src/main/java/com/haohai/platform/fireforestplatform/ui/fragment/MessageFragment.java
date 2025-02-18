package com.haohai.platform.fireforestplatform.ui.fragment;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.FgMessage;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.KKFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LevelFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.MonitorFireMessageInfoActivity;
import com.haohai.platform.fireforestplatform.ui.activity.MonitorFireMessageListActivity;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.ui.activity.SatelliteFireMessageListActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.activity.TaskListInfoActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Message;
import com.haohai.platform.fireforestplatform.ui.multitype.MessageViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessage;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FgMessageViewModel;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
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

        obtainViewModel().postData(true);
        return binding.getRoot();
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
        if(index == 2){
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
        if(Objects.equals(message.getType(), "monitorFireAlarm")){//任务
            Intent intent = new Intent(requireActivity(), TaskListInfoActivity.class);
            intent.putExtra("message",true);
            intent.putExtra("id",message.getId());
            updateReadState(message.getId());
            startActivity(intent);
        }
        if(Objects.equals(message.getType(), "monitorFirealarm")) {//监控报警
            //Intent intent = new Intent(requireActivity(), MonitorFireMessageListActivity.class);
            Intent intent = new Intent(requireActivity(), MonitorFireMessageInfoActivity.class);
            intent.putExtra("id",message.getId());
            intent.putExtra("message",true);
            startActivity(intent);

        }
        if(Objects.equals(message.getType(), "satelliteFirealarm")){//卫星报警
            Intent intent = new Intent(requireActivity(), SatelliteFireMessageListActivity.class);
            intent.putExtra("id",message.getId());
            startActivity(intent);

        }
        if(Objects.equals(message.getType(), "kkFirealarm")){//卡口报警
            Intent intent = new Intent(requireActivity(), KKFireMessageListActivity.class);
            intent.putExtra("id",message.getId());
            startActivity(intent);
        }
        if(Objects.equals(message.getType(), "fireLevel")){//火警等级
            Intent intent = new Intent(requireActivity(), LevelFireMessageListActivity.class);
            intent.putExtra("id",message.getId());
            startActivity(intent);
        }
    }


    private void updateReadState(String messageId) {
        RequestParams params = new RequestParams(URLConstant.GET_CHANGE_STATE_NEW);
        params.addParameter("messageId",messageId);
        //params.addParameter("foreignId",ids);
        //params.addParameter("messageType","monitorFirealarm");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",messageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.setBodyContent(jsonObject.toString());
        HhHttp.methodX(HttpMethod.GET, params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onResponse: " + result );
                if(result.contains(":200,")){
                    EventBus.getDefault().post(new MessageRefresh());
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
        EventBus.getDefault().unregister(this);
    }
}
