package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemGridModelBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemGridTreesBinding;
import com.haohai.platform.fireforestplatform.utils.CommonData;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.ItemViewProvider;
import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class GridModelViewBinder extends ItemViewProvider<GridModel, GridModelViewBinder.ViewHolder> {
    public Context context;
    public GridModelViewBinder(Context context) {
        this.context = context;
    }
    public OnItemClickListener listener;
    public GridCameraViewBinder.OnItemClickListener listenerGridCamera;
    public void setListener(OnItemClickListener listener,GridCameraViewBinder.OnItemClickListener listenerGridCamera) {
        this.listener = listener;
        this.listenerGridCamera = listenerGridCamera;
    }

    public MultiTypeAdapter adapter;
    public List<GridCamera> gridCameraList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_grid_model, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final GridModel gridModel) {

        ItemGridModelBinding binding = (ItemGridModelBinding) viewHolder.getBinding();
        /*if(CommonData.search!=null && !CommonData.search.isEmpty() && !gridModel.toString().contains(CommonData.search)){
            binding.click.setVisibility(View.GONE);
            return;
        }*/
        binding.name.setText(gridModel.getMonitor().getName());
        if(gridModel.getCameraList()!=null && !gridModel.getCameraList().isEmpty()){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            binding.rlv.setLayoutManager(linearLayoutManager);
            adapter = new MultiTypeAdapter(items);
            GridCameraViewBinder gridCameraViewBinder = new GridCameraViewBinder(context);
            gridCameraViewBinder.setListener(listenerGridCamera);
            adapter.register(GridCamera.class, gridCameraViewBinder);
            adapter.register(Empty.class, new EmptyViewBinder(context));
            binding.rlv.setAdapter(adapter);
            assertHasTheSameAdapter(binding.rlv, adapter);
        }
        if(gridModel.isChecked()){
            binding.state.setImageResource(R.mipmap.ic_down_);
            binding.rlv.setVisibility(View.VISIBLE);

            if(gridModel.getCameraList()!=null && !gridModel.getCameraList().isEmpty()){
                gridCameraList = gridModel.getCameraList();
                updateData();
            }
        }else{
            binding.state.setImageResource(R.mipmap.ic_more_);
            binding.rlv.setVisibility(View.GONE);
        }
        binding.click.setOnClickListener(v -> {
            gridModel.setChecked(!gridModel.isChecked());
            listener.onItemClick(gridModel,gridModel.isChecked());
        });


    }

    private void updateData() {
        items.clear();
        if (gridCameraList != null && gridCameraList.size()!=0) {
            items.addAll(gridCameraList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
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
        void onItemClick(GridModel gridModel,boolean state);
    }
}
