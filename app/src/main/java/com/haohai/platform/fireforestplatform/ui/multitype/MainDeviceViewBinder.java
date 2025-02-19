package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemMainDeviceBinding;

import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class MainDeviceViewBinder extends ItemViewProvider<MainDevice, MainDeviceViewBinder.ViewHolder> {
    public Context context;
    public MainDeviceViewBinder(Context context) {
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
                R.layout.item_main_device, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final MainDevice mainDevice) {

        ItemMainDeviceBinding binding = (ItemMainDeviceBinding) viewHolder.getBinding();
        binding.index.setText(mainDevice.getIndex()+"");
        binding.name.setText(mainDevice.getName());
        binding.area.setText(mainDevice.getGridName()+"");
        binding.state.setText(Objects.equals(mainDevice.getIsOnline(), "1") ?"在线":"离线");
        if(mainDevice.getIndex()%2==1){
            binding.click.setBackgroundColor(context.getResources().getColor(R.color.c));
        }else{
            binding.click.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        binding.clickIn.setOnClickListener(v -> {
            listener.onItemClick(mainDevice);
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
        void onItemClick(MainDevice resourceType);
    }
}
