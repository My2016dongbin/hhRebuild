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
import com.haohai.platform.fireforestplatform.databinding.ItemMessageBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemNewsBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class MessageViewBinder extends ItemViewProvider<Message, MessageViewBinder.ViewHolder> {
    public Context context;
    public MessageViewBinder(Context context) {
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
                R.layout.item_message, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Message message) {

        ItemMessageBinding binding = (ItemMessageBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, message);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.title.setText(message.getName());
        binding.time.setText(parse19(message.getUpdateTime()));
        binding.count.setText(message.getUnreadCount());
        if(Objects.equals(message.getUnreadCount(), "0")){
            binding.count.setVisibility(View.GONE);
        }else{
            binding.count.setVisibility(View.VISIBLE);
        }
        Glide.with(context).load(context.getDrawable(R.drawable.msg))
                .error(R.drawable.ic_no_pic)
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


    public void onItemClick(Message message){
        listener.onItemClick(message);
    }

    public interface OnItemClickListener{
        void onItemClick(Message message);
    }
}
