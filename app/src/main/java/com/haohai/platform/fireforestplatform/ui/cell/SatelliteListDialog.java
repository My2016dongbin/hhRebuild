package com.haohai.platform.fireforestplatform.ui.cell;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogOneBodyListBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFireViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.SatelliteFire;
import com.haohai.platform.fireforestplatform.ui.multitype.SatelliteFireViewBinder;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

public class SatelliteListDialog extends Dialog implements SatelliteFireViewBinder.OnItemClickListener {

    private final Context context;
    private SatelliteDialogListener dialogListener;
    private final DialogSatelliteBinding binding;
    public MultiTypeAdapter adapter;
    public List<SatelliteFire> satelliteFireList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    private int filterState = 0;//0按时间分类  1按编号分类

    public SatelliteListDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_satellite_list, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(SatelliteDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
    public void setSatelliteFireList(List<SatelliteFire> satelliteFireList) {
        this.satelliteFireList = satelliteFireList;
        updateFilterData();
        binding.warn.setText(satelliteFireList.size()+"");
    }
    public void updateData() {
        items.clear();
        if (satelliteFireList != null && satelliteFireList.size()!=0) {
            items.addAll(satelliteFireList);
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
        binding.filterLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                TextInfo okTextInfo = new TextInfo();
                okTextInfo.setFontColor(context.getResources().getColor(R.color.c7));
                MessageDialog.show("卫星火警分类", "","按时间分类","按编号分类")
                        .setButtonOrientation(LinearLayout.VERTICAL)
                        .setOkTextInfo(okTextInfo)
                        .setCancelTextInfo(okTextInfo)
                        .setOtherTextInfo(okTextInfo)
                        .setOkButtonClickListener((dialog, v1) -> {
                            show();
                            filterState = 0;
                            updateFilterState();
                            updateFilterData();
                            return false;
                        })
                        .setCancelButtonClickListener((dialog, v2) -> {
                            show();
                            filterState = 1;
                            updateFilterState();
                            updateFilterData();
                            return false;
                        })
                        .setOnBackgroundMaskClickListener((dialog, v12) -> {
                            show();
                            return false;
                        })
                        .setCancelable(true);
            }
        });
    }

    private void updateFilterData() {
        for (int i = 0; i < satelliteFireList.size(); i++) {
            if(filterState == 0){
                //时间分类
                if(i > 0){
                    satelliteFireList.get(i).setShowTime(!satelliteFireList.get(i).getCreateTime().equals(satelliteFireList.get(i - 1).getCreateTime()));
                }else{
                    satelliteFireList.get(i).setShowTime(true);
                }
                satelliteFireList.get(i).setShowNo(false);

            }else{
                //编号分类
                if(i > 0){
                    satelliteFireList.get(i).setShowNo(!satelliteFireList.get(i).getFireNo().equals(satelliteFireList.get(i - 1).getFireNo()));
                }else{
                    satelliteFireList.get(i).setShowNo(true);
                }
                satelliteFireList.get(i).setShowTime(false);

            }
        }
        updateData();
    }

    private void updateFilterState() {
        if(filterState == 0){
            binding.filterText.setText("按时间分类");
        }
        if(filterState == 1){
            binding.filterText.setText("按编号分类");
        }
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
                dialogListener.onSatelliteDialogRefresh();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
        SatelliteFireViewBinder satelliteFireViewBinder = new SatelliteFireViewBinder(context);
        satelliteFireViewBinder.setListener(this);
        adapter.register(SatelliteFire.class, satelliteFireViewBinder);
        adapter.register(Empty.class, new EmptyViewBinder(context));
        binding.rlv.setAdapter(adapter);
        assertHasTheSameAdapter(binding.rlv, adapter);
    }

    @Override
    public void onItemClick(SatelliteFire satelliteFire) {
        dialogListener.onSatelliteDialogItemClick(satelliteFire);
    }


    public interface SatelliteDialogListener{
        void onSatelliteDialogRefresh();
        void onSatelliteDialogItemClick(SatelliteFire satelliteFire);
    }
}
