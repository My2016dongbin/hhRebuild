package com.haohai.platform.fireforestplatform.ui.multitype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemSignModelBinding;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SignModelViewBinder extends ItemViewProvider<SignModel, SignModelViewBinder.ViewHolder> {
    public Context context;
    public SignModelViewBinder(Context context) {
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
                R.layout.item_sign_model, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final SignModel signModel) {

        ItemSignModelBinding binding = (ItemSignModelBinding) viewHolder.getBinding();
        binding.account.setText(getValue(signModel.getUserCode()));
        binding.name.setText(getValue(signModel.getFullName()));
        binding.count.setText(String.valueOf(signModel.getTotalAttendance()));
        binding.alarm.setText(String.valueOf(signModel.getTotalAlarm()));
        binding.area.setText(getValue(signModel.getManageArea()));
        if(signModel.getIndex()%2==1){
            binding.background.setBackgroundColor(context.getResources().getColor(R.color.c));
        }else{
            binding.background.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    private String getValue(String value) {
        return value == null || value.length() == 0 ? "--" : value;
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
        void onItemClick(SignModel signModel);
    }
}
