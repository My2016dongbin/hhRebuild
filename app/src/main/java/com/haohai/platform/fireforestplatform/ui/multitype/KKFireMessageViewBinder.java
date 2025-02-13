package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemKkFireBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemMonitorFireBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class KKFireMessageViewBinder extends ItemViewProvider<KKFireMessage, KKFireMessageViewBinder.ViewHolder> {
    public Context context;
    public KKFireMessageViewBinder(Context context) {
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
                R.layout.item_kk_fire, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final KKFireMessage kkFireMessage) {

        ItemKkFireBinding binding = (ItemKkFireBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, kkFireMessage);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        if(!Objects.equals(kkFireMessage.getIsRead(), "1")){
            binding.title.setTextColor(context.getResources().getColor(R.color.text_color3));
            binding.time.setTextColor(context.getResources().getColor(R.color.text_color6));
        }else{
            binding.title.setTextColor(context.getResources().getColor(R.color.text_color8));
            binding.time.setTextColor(context.getResources().getColor(R.color.text_color8));
        }
        binding.title.setText(kkFireMessage.getName()+kkFireMessage.getAlarmType());
        binding.time.setText(parse19(kkFireMessage.getCreateTime()));
        Glide.with(context).load(kkFireMessage.getPicPath1())
                .error(R.drawable.ic_no_pic)
                .circleCrop()
                .into(binding.icon);
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


    public void onItemClick(KKFireMessage kkFireMessage){
        listener.onItemClick(kkFireMessage);
    }

    public interface OnItemClickListener{
        void onItemClick(KKFireMessage kkFireMessage);
    }
}
