package com.haohai.platform.fireforestplatform.ui.multitype;

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
import com.haohai.platform.fireforestplatform.databinding.ItemFireEventBinding;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2026/8/7.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class FireEventViewBinder extends ItemViewProvider<FireEvent, FireEventViewBinder.ViewHolder> {
    public Context context;

    public FireEventViewBinder(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_fire_event, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final FireEvent fireEvent) {
        ItemFireEventBinding binding = (ItemFireEventBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, fireEvent);
        binding.executePendingBindings(); //防止闪烁

        binding.title.setText(fireEvent.getFireName());
        binding.time.setText(parse19(getEventTime(fireEvent)));
        Glide.with(context).load(fireEvent.getPicPath1())
                .error(R.drawable.ic_no_pic)
                .into(binding.icon);
    }

    private String getEventTime(FireEvent fireEvent) {
        if (fireEvent.getReportTime() != null && fireEvent.getReportTime().length() > 0) {
            return fireEvent.getReportTime();
        }
        if (fireEvent.getFireTime() != null && fireEvent.getFireTime().length() > 0) {
            return fireEvent.getFireTime();
        }
        return fireEvent.getCreateTime();
    }

    private String parse19(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        if (str.length() > 19) {
            return str.substring(0,19).replace("T"," ");
        }
        return str.replace("T"," ");
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
}
