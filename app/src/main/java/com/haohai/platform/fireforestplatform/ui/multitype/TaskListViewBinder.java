package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemMonitorFireBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemTaskListBinding;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class TaskListViewBinder extends ItemViewProvider<TaskList, TaskListViewBinder.ViewHolder> {
    public Context context;
    public TaskListViewBinder(Context context) {
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
                R.layout.item_task_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final TaskList taskList) {

        ItemTaskListBinding binding = (ItemTaskListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, taskList);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.title.setText(taskList.getTaskName());
        binding.status.setText(parseStatus(taskList.getTaskStatus()));
        binding.time.setText(CommonUtil.parseDate(taskList.getTaskStartTime()));
        try{
            Glide.with(context).load(taskList.getTaskFileList().get(0).getFileUrl())
                    .error(R.drawable.ic_no_pic)
                    .into(binding.icon);
        }catch (Exception e){
            Glide.with(context).load(context.getDrawable(R.drawable.ic_no_pic))
                    .into(binding.icon);
        }
    }

    private String parseStatus(String taskStatus) {
        String statusStr = "已结束";
        if(Objects.equals(taskStatus, "wait")){
            statusStr = "未开始";
        }
        if(Objects.equals(taskStatus, "progress")){
            statusStr = "执行中";
        }
        if(Objects.equals(taskStatus, "completed")){
            statusStr = "已结束";
        }
        return statusStr;
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


    public void onItemClick(TaskList taskList){
        listener.onItemClick(taskList);
    }

    public interface OnItemClickListener{
        void onItemClick(TaskList taskList);
    }
}
