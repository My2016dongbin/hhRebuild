package com.haohai.platform.fireforestplatform.ui.cell;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogOneBodyListBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessage;
import com.haohai.platform.fireforestplatform.ui.multitype.MonitorFireMessageViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFireViewBinder;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBackPressedListener;
import com.kongzue.dialogx.interfaces.OnBackgroundMaskClickListener;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;

public class OneBodyListDialog extends Dialog implements OneBodyFireViewBinder.OnItemClickListener {

    private final Context context;
    private OneBodyDialogListener dialogListener;
    private final DialogOneBodyListBinding binding;
    public MultiTypeAdapter adapter;
    public List<OneBodyFire> oneBodyFireList = new ArrayList<>();
    public List<OneBodyFire> filterList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();

    public OneBodyListDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_onebody_list, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(OneBodyDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setOneBodyFireList(List<OneBodyFire> oneBodyFireList,int page) {
        if(page>1){
            this.oneBodyFireList.addAll(oneBodyFireList);
        }else{
            this.oneBodyFireList = oneBodyFireList;
        }
        filterData();
    }

    private void filterData() {
        filterList = new ArrayList<>();
        for (int i = 0; i < oneBodyFireList.size(); i++) {
            OneBodyFire oneBodyFire = oneBodyFireList.get(i);
            if(filterState == 0){
                /*if(oneBodyFire.getIsReal()==null || Objects.equals(oneBodyFire.getIsReal(), "1")){
                    filterList.add(oneBodyFire);
                }*/
                filterList.add(oneBodyFire);
            }
            if(filterState == 1){
                if(oneBodyFire.getIsReal()==null){
                    filterList.add(oneBodyFire);
                }
            }
            if(filterState == 2){
                if(Objects.equals(oneBodyFire.getIsReal(), "1")){
                    filterList.add(oneBodyFire);
                }
            }
            if(filterState == 3){
                if(Objects.equals(oneBodyFire.getIsReal(), "0")){
                    filterList.add(oneBodyFire);
                }
            }
        }

        updateData();
    }

    public void updateData() {
        items.clear();
        if (filterList != null && filterList.size()!=0) {
            items.addAll(filterList);
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


    private int filterState = 1;//0全部  1未处理  2真实火点  3疑似火点
    private void bind_() {
        binding.filterLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_fire_type) {
                    @Override
                    public void onBind(final CustomDialog dialog, View v) {
                        TextView all;
                        TextView real;
                        TextView noHandle;
                        TextView fake;
                        all = v.findViewById(R.id.all);
                        real = v.findViewById(R.id.real);
                        noHandle = v.findViewById(R.id.no_handle);
                        fake = v.findViewById(R.id.fake);
                        all.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                show();
                                filterState = 0;
                                updateFilterState();
                                dialogListener.onOneBodyDialogFilterState(0);
                                filterData();
                                dialog.dismiss();
                            }
                        });
                        noHandle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                show();
                                filterState = 1;
                                updateFilterState();
                                dialogListener.onOneBodyDialogFilterState(1);
                                filterData();
                                dialog.dismiss();
                            }
                        });
                        real.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                show();
                                filterState = 2;
                                updateFilterState();
                                dialogListener.onOneBodyDialogFilterState(2);
                                filterData();
                                dialog.dismiss();
                            }
                        });
                        fake.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                show();
                                filterState = 3;
                                updateFilterState();
                                dialogListener.onOneBodyDialogFilterState(3);
                                filterData();
                                dialog.dismiss();
                            }
                        });
                    }
                }).setOnBackgroundMaskClickListener((dialog, v12) -> {
                            show();
                            return false;
                        })
                        .setCancelable(true);
                /*TextInfo okTextInfo = new TextInfo();
                okTextInfo.setFontColor(context.getResources().getColor(R.color.c7));
                MessageDialog.show("火情分类", "","全部","未处理","真实火点")
                        .setButtonOrientation(LinearLayout.VERTICAL)
                        .setOkTextInfo(okTextInfo)
                        .setCancelTextInfo(okTextInfo)
                        .setOtherTextInfo(okTextInfo)
                        .setOkButtonClickListener((dialog, v1) -> {
                            show();
                            filterState = 0;
                            updateFilterState();
                            dialogListener.onOneBodyDialogFilterState(0);
                            filterData();
                            return false;
                        })
                        .setCancelButtonClickListener((dialog, v2) -> {
                            show();
                            filterState = 1;
                            updateFilterState();
                            dialogListener.onOneBodyDialogFilterState(1);
                            filterData();
                            return false;
                        })
                        .setOtherButtonClickListener((dialog, v3) -> {
                            show();
                            filterState = 2;
                            updateFilterState();
                            dialogListener.onOneBodyDialogFilterState(2);
                            filterData();
                            return false;
                        })
                        .setOnBackgroundMaskClickListener((dialog, v12) -> {
                            show();
                            return false;
                        })
                        .setCancelable(true);*/
            }
        });
    }
    private void updateFilterState() {
        if(filterState == 0){
            binding.filterText.setText("全部");
        }
        if(filterState == 1){
            binding.filterText.setText("未处理");
        }
        if(filterState == 2){
            binding.filterText.setText("真实火点");
        }
        if(filterState == 3){
            binding.filterText.setText("疑似火点");
        }
    }

    private void init_() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        binding.oneBodyRlv.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);
        //下拉刷新
        binding.oneBodySmart.setRefreshFooter(new BallPulseFooter(context).setSpinnerStyle(SpinnerStyle.Scale));
        binding.oneBodySmart.setRefreshHeader(new MaterialHeader(context).setShowBezierWave(true));
        binding.oneBodySmart.setEnableLoadMore(true);
        //设置样式后面的背景颜色
        binding.oneBodySmart.setPrimaryColorsId(R.color.back_color_f8, R.color.back_color_f8)
                .setBackgroundColor(context.getResources().getColor(R.color.back_color_f8));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.oneBodySmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                dialogListener.onOneBodyDialogRefresh();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                dialogListener.onOneBodyDialogLoadMore();
            }
        });

        OneBodyFireViewBinder oneBodyFireViewBinder = new OneBodyFireViewBinder(context);
        oneBodyFireViewBinder.setListener(this);
        adapter.register(OneBodyFire.class, oneBodyFireViewBinder);
        adapter.register(Empty.class, new EmptyViewBinder(context));
        binding.oneBodyRlv.setAdapter(adapter);
        assertHasTheSameAdapter(binding.oneBodyRlv, adapter);
    }

    @Override
    public void onItemClick(OneBodyFire oneBodyFire) {
        dialogListener.onOneBodyDialogItemClick(oneBodyFire);
    }


    public interface OneBodyDialogListener{
        void onOneBodyDialogRefresh();
        void onOneBodyDialogLoadMore();
        void onOneBodyDialogFilterState(int state);
        void onOneBodyDialogItemClick(OneBodyFire oneBodyFire);
    }
}
