package com.haohai.platform.fireforestplatform.ui.multitype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemExamineBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ExamineViewBinder extends ItemViewProvider<Examine, ExamineViewBinder.ViewHolder> {
    public Context context;
    public ExamineViewBinder(Context context) {
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
                R.layout.item_examine, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Examine message) {

        ItemExamineBinding binding = (ItemExamineBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, message);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁


        binding.textTitle.setText(message.getIncidentTitle());
        binding.textTime.setText(parse19(message.getReportingTime()));
        binding.address.setText(message.getEmergIncidentVersion().getIncidentAddr());
        if("1".equals(message.getHandleStatus())){
            binding.textStatus.setText("处置中");
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_ing));
            Glide.with(context).load(context.getDrawable(R.drawable.warn_state2))
                    .into(binding.status);
        }else if("2".equals(message.getHandleStatus())){
            binding.textStatus.setText("已完成");
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_complete));
            Glide.with(context).load(context.getDrawable(R.drawable.warn_state3))
                    .into(binding.status);
        }else{
            binding.textStatus.setText("未开始");
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_start));
            Glide.with(context).load(context.getDrawable(R.drawable.warn_state1))
                    .into(binding.status);
        }
        //审核 未开始
        binding.textStatus.setText("未开始");
        binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_start));
        Glide.with(context).load(context.getDrawable(R.drawable.warn_state1))
                .into(binding.status);

        /*if(message.getIncidentType()!=null&&message.getIncidentType().startsWith("01/")){
            binding.textType1.setText("自然");
            binding.textType2.setText("灾害");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_1))
                    .into(binding.type);
        }else if(message.getIncidentType()!=null&&message.getIncidentType().startsWith("02/")){
            binding.textType1.setText("事故");
            binding.textType2.setText("灾害");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_2))
                    .into(binding.type);
        }else{
            binding.textType1.setText("其它");
            binding.textType2.setText("事件");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_3))
                    .into(binding.type);
        }*/
        if ((message.getIncidentType() != null && message.getIncidentType().startsWith("01")) || (message.getIncidentTypeName() != null && message.getIncidentTypeName().startsWith("自然灾害"))) {
            binding.textType1.setText("自然");
            binding.textType2.setText("灾害");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_2))
                    .into(binding.type);
        } else if ((message.getIncidentType() != null && message.getIncidentType().startsWith("02")) || (message.getIncidentTypeName() != null && message.getIncidentTypeName().startsWith("事故灾害"))) {
            binding.textType1.setText("事故");
            binding.textType2.setText("灾害");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_2))
                    .into(binding.type);
        }else if ((message.getIncidentType() != null && message.getIncidentType().startsWith("03")) || (message.getIncidentTypeName() != null && message.getIncidentTypeName().startsWith("公共卫生"))) {
            binding.textType1.setText("公共");
            binding.textType2.setText("卫生");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_1))
                    .into(binding.type);
        }else if ((message.getIncidentType() != null && message.getIncidentType().startsWith("04")) || (message.getIncidentTypeName() != null && message.getIncidentTypeName().startsWith("社会安全"))) {
            binding.textType1.setText("社会");
            binding.textType2.setText("安全");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_3))
                    .into(binding.type);
        } else {
            binding.textType1.setText("其它");
            binding.textType2.setText("隐患");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_3))
                    .into(binding.type);
        }

        binding.textExamine.setOnClickListener(v -> {
            listener.onExamineClick(message);
        });
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


    public void onItemClick(Examine message){
        listener.onItemClick(message);
    }

    public interface OnItemClickListener{
        void onItemClick(Examine message);
        void onExamineClick(Examine message);
    }
}
