package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
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
import com.haohai.platform.fireforestplatform.databinding.ItemAlarmBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class AlarmViewBinder extends ItemViewProvider<Alarm, AlarmViewBinder.ViewHolder> {
    public Context context;
    public AlarmViewBinder(Context context) {
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
                R.layout.item_alarm, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Alarm message) {

        ItemAlarmBinding binding = (ItemAlarmBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, message);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.title.setText(message.getTitle());
        binding.textStatus.setText(parseStatus(message.getStatus()));
        //未反馈noFeedback 待上传waitUpload 按时反馈onTimeFeedback 超时反馈overTimeFeedback
        if(Objects.equals(message.getStatus(), "onTimeFeedback")){
            Glide.with(context).load(context.getDrawable(R.drawable.warn_state3))
                    .into(binding.status);
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.c5));
        }else if(Objects.equals(message.getStatus(), "overTimeFeedback")){
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.text_blue));
            Glide.with(context).load(context.getDrawable(R.drawable.warn_state1))
                    .into(binding.status);
        } else{//未反馈noFeedback 待上传waitUpload
            Glide.with(context).load(context.getDrawable(R.drawable.warn_state2))
                    .into(binding.status);
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.text_green));
        }
        binding.time.setText(parseDate(message.getStartTime()) + "/" + parseDate(message.getEndTime()));
        if(Objects.equals(message.getType(), "warning")){//任务类型 type字段 通知notice  通知需反馈feedback  预警预报warning
            binding.imgWarn.setVisibility(View.VISIBLE);
            binding.imgNotice.setVisibility(View.GONE);
        }else{
            binding.imgWarn.setVisibility(View.GONE);
            binding.imgNotice.setVisibility(View.VISIBLE);
        }
    }

    private String parseStatus(String str) {
        String r = "";
        if(Objects.equals(str, "overTimeFeedback")){
            r = "超时反馈";
        }
        if(Objects.equals(str, "noFeedback")){
            r = "未反馈";
        }
        if(Objects.equals(str, "onTimeFeedback")){
            r = "按时反馈";
        }
        if(Objects.equals(str, "waitUpload")){
            r = "待上传";
        }
        return r;
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
    private String parseDate(String str) {
        String format = str;
        try{
            Date date = new Date(Long.parseLong(str));
            @SuppressLint("SimpleDateFormat") SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format = smf.format(date);
        }catch (Exception e){
            HhLog.e(e.getMessage());
        }
        return format;
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


    public void onItemClick(Alarm message){
        listener.onItemClick(message);
    }

    public interface OnItemClickListener{
        void onItemClick(Alarm message);
    }
}
