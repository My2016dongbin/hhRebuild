package com.haohai.platform.fireforestplatform.ui.cell;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogMainDeviceListBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogResourceListBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.MainDevice;
import com.haohai.platform.fireforestplatform.ui.multitype.MainDeviceViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.ResourceType;
import com.haohai.platform.fireforestplatform.ui.multitype.ResourceTypeViewBinder;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class MainDeviceListDialog extends Dialog implements MainDeviceViewBinder.OnItemClickListener{

    private final Context context;
    private MainDeviceDialogListener dialogListener;
    private final DialogMainDeviceListBinding binding;
    public MultiTypeAdapter adapter;
    public List<MainDevice> mainDeviceList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();

    public MainDeviceListDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_main_device_list, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(MainDeviceDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setMainDeviceList(List<MainDevice> mainDeviceList) {
        this.mainDeviceList = mainDeviceList;
        updateData();
    }

    public void updateData() {
        items.clear();
        if (mainDeviceList != null && mainDeviceList.size()!=0) {
            items.addAll(mainDeviceList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        binding.rlv.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);
        //下拉刷新
        binding.smart.setRefreshFooter(new BallPulseFooter(context).setSpinnerStyle(SpinnerStyle.Scale));
        binding.smart.setRefreshHeader(new MaterialHeader(context).setShowBezierWave(true));
        binding.smart.setEnableLoadMore(false);
        //设置样式后面的背景颜色
        binding.smart.setPrimaryColorsId(R.color.back_color_f8, R.color.back_color_f8)
                .setBackgroundColor(context.getResources().getColor(R.color.back_color_f8));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.smart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                dialogListener.onMainDeviceDialogRefresh();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });

        MainDeviceViewBinder mainDeviceViewBinder = new MainDeviceViewBinder(context);
        mainDeviceViewBinder.setListener(this);
        adapter.register(MainDevice.class, mainDeviceViewBinder);
        adapter.register(Empty.class, new EmptyViewBinder(context));
        binding.rlv.setAdapter(adapter);
        assertHasTheSameAdapter(binding.rlv, adapter);
    }

    @Override
    public void onItemClick(MainDevice mainDevice) {
        dialogListener.onMainDeviceDialogItemClick(mainDevice);
    }


    public interface MainDeviceDialogListener{
        void onMainDeviceDialogRefresh();
        void onMainDeviceDialogItemClick(MainDevice mainDevice);
    }
}
