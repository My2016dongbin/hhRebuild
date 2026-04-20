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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemTaskListBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final TaskList taskList) {

        ItemTaskListBinding binding = (ItemTaskListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, taskList);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.title.setText(taskList.getTaskContent());
        binding.time.setText(parse19(taskList.getTaskStartTime()));
        //未开始显示分配人，开始后显示更新人
        if(taskList.getStatus()==null || taskList.getStatus().equals("0")){
            binding.recordNo.setText(parseOperatorName(taskList.getOperatorName()));
        }else{
            binding.recordNo.setText(parseOperatorName(taskList.getUpdateUser()));
        }
        Glide.with(context).load(parseFirst(taskList.getTaskImg()))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_no_pic)
                        .error(R.drawable.ic_no_pic)
                        .transform(new CenterCrop(), new RoundedCorners(20)))
                .into(binding.icon);
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

    private String parseOperatorName(String operatorName) {
        if (operatorName == null || operatorName.trim().isEmpty()) {
            return "-";
        }
        return operatorName;
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
