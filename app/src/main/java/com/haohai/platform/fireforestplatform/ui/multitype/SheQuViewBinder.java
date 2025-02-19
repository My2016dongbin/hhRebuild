package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemShequBinding;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SheQuViewBinder extends ItemViewProvider<SheQu, SheQuViewBinder.ViewHolder> {
    public Context context;
    public SheQuViewBinder(Context context) {
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
                R.layout.item_shequ, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final SheQu sheQu) {

        ItemShequBinding binding = (ItemShequBinding) viewHolder.getBinding();
        binding.name.setText(sheQu.getProperties().getGridName());
        if(sheQu.isChecked()){
            binding.state.setImageResource(R.drawable.ic_red_fire);
        }else{
            binding.state.setImageResource(R.drawable.ic_blue_fire);
        }
        binding.click.setOnClickListener(v -> {
            sheQu.setChecked(!sheQu.isChecked());
            listener.onItemClick(sheQu,sheQu.isChecked());
        });
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
        void onItemClick(SheQu sheQu,boolean state);
    }
}
