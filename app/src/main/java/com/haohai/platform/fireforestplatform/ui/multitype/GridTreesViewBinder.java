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
public class GridTreesViewBinder extends ItemViewProvider<GridTrees, GridTreesViewBinder.ViewHolder> {
    public Context context;
    public GridTreesViewBinder(Context context) {
        this.context = context;
    }
    public OnItemClickListener listener;
    public GridModelViewBinder.OnItemClickListener listenerGridModel;
    public GridCameraViewBinder.OnItemClickListener listenerGridCamera;
    public void setListener(OnItemClickListener listener,GridModelViewBinder.OnItemClickListener listenerGridModel,GridCameraViewBinder.OnItemClickListener listenerGridCamera) {
        this.listener = listener;
        this.listenerGridModel = listenerGridModel;
        this.listenerGridCamera = listenerGridCamera;
    }

    //Children
    public MultiTypeAdapter adapter;
    public List<GridTrees> gridTreesList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    //设备Model{monitor,camera}
    public MultiTypeAdapter adapterModel;
    public List<GridModel> gridModelList = new ArrayList<>();
    public List<Object> itemsModel = new ArrayList<>();

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_grid_trees, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final GridTrees gridTrees) {

        ItemGridTreesBinding binding = (ItemGridTreesBinding) viewHolder.getBinding();
        /*if(CommonData.search!=null && !CommonData.search.isEmpty() && !gridTrees.toString().contains(CommonData.search)){
            binding.click.setVisibility(View.GONE);
            return;
        }*/
        binding.name.setText(gridTrees.getName());
        //当前网格下的设备数据
        if(gridTrees.getMonitorDetailVOs()!=null && !gridTrees.getMonitorDetailVOs().isEmpty()){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            binding.rlvModel.setLayoutManager(linearLayoutManager);
            adapterModel = new MultiTypeAdapter(itemsModel);
            GridModelViewBinder gridModelViewBinder = new GridModelViewBinder(context);
            gridModelViewBinder.setListener(listenerGridModel,listenerGridCamera);
            adapterModel.register(GridModel.class, gridModelViewBinder);
            adapterModel.register(Empty.class, new EmptyViewBinder(context));
            binding.rlvModel.setAdapter(adapterModel);
            assertHasTheSameAdapter(binding.rlvModel, adapterModel);
        }
        //Children网格递归
        if(gridTrees.getChildren()!=null && !gridTrees.getChildren().isEmpty()){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            binding.rlv.setLayoutManager(linearLayoutManager);
            adapter = new MultiTypeAdapter(items);
            GridTreesViewBinder gridTreesViewBinder = new GridTreesViewBinder(context);
            gridTreesViewBinder.setListener(listener,listenerGridModel,listenerGridCamera);
            adapter.register(GridTrees.class, gridTreesViewBinder);
            adapter.register(Empty.class, new EmptyViewBinder(context));
            binding.rlv.setAdapter(adapter);
            assertHasTheSameAdapter(binding.rlv, adapter);
        }
        if(gridTrees.isChecked()){
            binding.state.setImageResource(R.mipmap.ic_down_);
            binding.rlv.setVisibility(View.VISIBLE);
            binding.rlvModel.setVisibility(View.VISIBLE);

            if(gridTrees.getChildren()!=null && !gridTrees.getChildren().isEmpty()){
                gridTreesList = gridTrees.getChildren();
                updateData();
            }
            if(gridTrees.getMonitorDetailVOs()!=null && !gridTrees.getMonitorDetailVOs().isEmpty()){
                gridModelList = gridTrees.getMonitorDetailVOs();
                updateDataModel();
            }
        }else{
            binding.state.setImageResource(R.mipmap.ic_more_);
            binding.rlv.setVisibility(View.GONE);
            binding.rlvModel.setVisibility(View.GONE);
        }
        binding.click.setOnClickListener(v -> {
            gridTrees.setChecked(!gridTrees.isChecked());
            listener.onItemClick(gridTrees,gridTrees.isChecked());
        });



        //处理底部遮盖问题
        if(gridTrees.isLast()){
            binding.bottom.setVisibility(View.VISIBLE);
            binding.bottom.setOnClickListener(v -> {

            });
        }else{
            binding.bottom.setVisibility(View.GONE);
        }
    }

    private void updateData() {
        items.clear();
        if (gridTreesList != null && gridTreesList.size()!=0) {
            items.addAll(gridTreesList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    private void updateDataModel() {
        itemsModel.clear();
        if (gridModelList != null && gridModelList.size()!=0) {
            itemsModel.addAll(gridModelList);
        }else{
            itemsModel.add(new Empty());
        }

        assertAllRegistered(adapterModel, itemsModel);
        adapterModel.notifyDataSetChanged();
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
        void onItemClick(GridTrees gridTrees,boolean state);
    }
}
