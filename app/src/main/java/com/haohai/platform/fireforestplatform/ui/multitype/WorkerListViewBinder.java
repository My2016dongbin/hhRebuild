package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemWorkerListBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.util.List;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class WorkerListViewBinder extends ItemViewProvider<WorkerDetail, WorkerListViewBinder.ViewHolder> {
    public Context context;
    public WorkerListViewBinder(Context context) {
        this.context = context;
    }
    public OnItemClickListener listener;
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_worker_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final WorkerDetail workerDetail) {

        ItemWorkerListBinding binding = (ItemWorkerListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, workerDetail);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.title.setText(workerDetail.getGridName());
        List<WorkerDetail.Worker> list = workerDetail.getList();
        if(list!=null && list.size()>0){
            WorkerDetail.Worker worker = list.get(0);
            binding.dayLeaderName.setText(worker.getArrangeName());
            binding.dayLeaderName.setOnClickListener(v -> {
                HhLog.e("onPersonClick 0");
                listener.onPersonClick(workerDetail,0);
            });
        }
        if(list!=null && list.size()>1){
            WorkerDetail.Worker worker = list.get(1);
            binding.dayWorkerName.setText(worker.getArrangeName());
            binding.dayWorkerName.setOnClickListener(v -> {
                HhLog.e("onPersonClick 1");
                listener.onPersonClick(workerDetail,1);
            });
        }
        if(list!=null && list.size()>2){
            WorkerDetail.Worker worker = list.get(2);
            binding.nightLeaderName.setText(worker.getArrangeName());
            binding.nightLeaderName.setOnClickListener(v -> {
                HhLog.e("onPersonClick 2");
                listener.onPersonClick(workerDetail,2);
            });
        }
        if(list!=null && list.size()>3){
            WorkerDetail.Worker worker = list.get(3);
            binding.nightWorkerName.setText(worker.getArrangeName());
            binding.nightWorkerName.setOnClickListener(v -> {
                HhLog.e("onPersonClick 3");
                listener.onPersonClick(workerDetail,3);
            });
        }
    }

    private String parseFirst(String taskImg) {
        String str = taskImg;
        try{
            int i = taskImg.indexOf(",");
            str = taskImg.substring(0,i);
        }catch (Exception e){
            Log.e("TAG", "parseFirst: " + e.getMessage() );
        }
        return str;
    }

    private String parse19(String str) {
        String r = str;
        try{
            r = str.substring(0,19).replace("T"," ");
        }catch (Exception e){
            HhLog.e(e.getMessage());
        }
        return r;
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


    /*public void onItemClick(WorkerDetail workerDetail){
        listener.onItemClick(workerDetail);
    }*/

    public interface OnItemClickListener{
        void onItemClick(WorkerDetail workerDetail);
        void onPersonClick(WorkerDetail workerDetail,int index);
    }
}
