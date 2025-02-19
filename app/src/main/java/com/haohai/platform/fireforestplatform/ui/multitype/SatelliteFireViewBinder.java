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
import com.haohai.platform.fireforestplatform.databinding.ItemSatelliteFireBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SatelliteFireViewBinder extends ItemViewProvider<SatelliteFire, SatelliteFireViewBinder.ViewHolder> {
    public Context context;
    public SatelliteFireViewBinder(Context context) {
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
                R.layout.item_satellite_fire, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final SatelliteFire satelliteFire) {

        ItemSatelliteFireBinding binding = (ItemSatelliteFireBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, satelliteFire);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.time.setText(parse19(satelliteFire.getObservationDatetime()));
        binding.no.setText(satelliteFire.getFireNo());
        binding.location.setText(satelliteFire.getFormattedAddress());

        if(satelliteFire.isShowNo()){
            binding.llNo.setVisibility(View.VISIBLE);
        }else{
            binding.llNo.setVisibility(View.GONE);
        }
        if(satelliteFire.isShowTime()){
            binding.llTime.setVisibility(View.VISIBLE);
        }else{
            binding.llTime.setVisibility(View.GONE);
        }
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


    public void onItemClick(SatelliteFire satelliteFire){
        listener.onItemClick(satelliteFire);
    }

    public interface OnItemClickListener{
        void onItemClick(SatelliteFire satelliteFire);
    }
}
