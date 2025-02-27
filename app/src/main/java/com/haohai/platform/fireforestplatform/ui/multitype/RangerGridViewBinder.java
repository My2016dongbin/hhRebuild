package com.haohai.platform.fireforestplatform.ui.multitype;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemRangerGridBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemResourceTypeBinding;
import com.haohai.platform.fireforestplatform.ui.activity.RangerActivity;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.ItemViewProvider;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class RangerGridViewBinder extends ItemViewProvider<RangerGrid, RangerGridViewBinder.ViewHolder> {
    public Context context;
    public RangerGridViewBinder(Context context) {
        this.context = context;
    }
    public OnItemClickListener listener;
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MultiTypeAdapter adapter;
    public List<RangerGrid> rangerGridList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();

    public MultiTypeAdapter adapter_ranger;
    public List<RangerGrid.Ranger> rangerList = new ArrayList<>();
    public List<Object> items_ranger = new ArrayList<>();

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_ranger_grid, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final RangerGrid rangerGrid) {

        ItemRangerGridBinding binding = (ItemRangerGridBinding) viewHolder.getBinding();
        binding.name.setText(rangerGrid.getName());
        //防火员列表
        if(rangerGrid.getData()!=null && !rangerGrid.getData().isEmpty()){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            binding.rlvRanger.setLayoutManager(linearLayoutManager);
            adapter_ranger = new MultiTypeAdapter(items_ranger);
            RangerViewBinder rangerViewBinder = new RangerViewBinder(context);
            rangerViewBinder.setListener((RangerActivity)context);
            adapter_ranger.register(RangerGrid.Ranger.class, rangerViewBinder);
            adapter_ranger.register(Empty.class, new EmptyViewBinder(context));
            binding.rlvRanger.setAdapter(adapter_ranger);
            assertHasTheSameAdapter(binding.rlvRanger, adapter_ranger);
        }
        //网格数据递归
        if(rangerGrid.getChildren()!=null && !rangerGrid.getChildren().isEmpty()){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            binding.rlv.setLayoutManager(linearLayoutManager);
            adapter = new MultiTypeAdapter(items);
            RangerGridViewBinder rangerGridViewBinder = new RangerGridViewBinder(context);
            rangerGridViewBinder.setListener((RangerActivity)context);
            adapter.register(RangerGrid.class, rangerGridViewBinder);
            adapter.register(Empty.class, new EmptyViewBinder(context));
            binding.rlv.setAdapter(adapter);
            assertHasTheSameAdapter(binding.rlv, adapter);
        }
        if(rangerGrid.isChecked()){
            binding.state.setImageResource(R.drawable.ic_red_fire);
            binding.rlv.setVisibility(View.VISIBLE);
            binding.rlvRanger.setVisibility(View.VISIBLE);

            if(rangerGrid.getData()!=null && !rangerGrid.getData().isEmpty()){
                rangerList = rangerGrid.getData();
                updateDataRanger();
            }
            if(rangerGrid.getChildren()!=null && !rangerGrid.getChildren().isEmpty()){
                rangerGridList = rangerGrid.getChildren();
                updateData();
            }
        }else{
            binding.state.setImageResource(R.drawable.ic_blue_fire);
            binding.rlv.setVisibility(View.GONE);
            binding.rlvRanger.setVisibility(View.GONE);
        }
        binding.click.setOnClickListener(v -> {
            rangerGrid.setChecked(!rangerGrid.isChecked());
            listener.onItemClick(rangerGrid,rangerGrid.isChecked());
        });


    }

    private void updateData() {
        items.clear();
        if (rangerGridList != null && rangerGridList.size()!=0) {
            items.addAll(rangerGridList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    private void updateDataRanger() {
        items_ranger.clear();
        if (rangerList != null && rangerList.size()!=0) {
            items_ranger.addAll(rangerList);
        }else{
            items_ranger.add(new Empty());
        }

        assertAllRegistered(adapter_ranger, items_ranger);
        adapter_ranger.notifyDataSetChanged();
    }

    static class ViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private final B mBinding;

        ViewHolder(B mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
        public B getBinding() {
            return mBinding;
        }
    }


    public interface OnItemClickListener{
        void onItemClick(RangerGrid rangerGrid,boolean state);
    }
}
