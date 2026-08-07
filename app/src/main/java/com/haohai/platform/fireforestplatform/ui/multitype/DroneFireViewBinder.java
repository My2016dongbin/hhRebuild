package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemDroneFireBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2026/8/3.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class DroneFireViewBinder extends ItemViewProvider<DroneFire, DroneFireViewBinder.ViewHolder> {
    public Context context;
    public DroneFireViewBinder(Context context) {
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
                R.layout.item_drone_fire, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final DroneFire droneFire) {

        ItemDroneFireBinding binding = (ItemDroneFireBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, droneFire);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.name.setText(parseName(droneFire));
        binding.date.setText(parse19(droneFire.getWriteTime()));
        binding.lngLat.setText(droneFire.getLongitude()+"、"+droneFire.getLatitude());
        binding.address.setText(droneFire.getAddress());
    }

    private String parseName(DroneFire droneFire) {
        if(!TextUtils.isEmpty(droneFire.getDeviceName())){
            return droneFire.getDeviceName();
        }
        if(!TextUtils.isEmpty(droneFire.getEventName())){
            return droneFire.getEventName();
        }
        return "";
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


    public void onItemClick(DroneFire droneFire){
        listener.onItemClick(droneFire);
    }

    public interface OnItemClickListener{
        void onItemClick(DroneFire droneFire);
    }
}
