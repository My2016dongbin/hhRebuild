package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemCheckBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CheckViewBinder extends ItemViewProvider<Check, CheckViewBinder.ViewHolder> {
    public Context context;
    public CheckViewBinder(Context context) {
        this.context = context;
    }
    public OnItemClickListener listener;

    private long timeLeft = 0;
    private long timeRight = 0;
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_check, parent, false);
        return new ViewHolder(dataBinding);
    }

    @RequiresApi(api = 31)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Check message) {

        ItemCheckBinding binding = (ItemCheckBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, message);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.textTitle.setText(message.getName());
        binding.textTime.setText(parse19(message.getAlarmDatetime()));
        binding.address.setText(message.getAddress());
        binding.textStatus.setText(Objects.equals(message.getSuperviseFlag(), "2") ?"已核实":"待核实");
        Glide.with(context).load(message.getPicPath1())
                .error(R.drawable.ic_no_pic)
        .transforms(new CenterCrop(), new GranularRoundedCorners(18,0,0,18))
                .into(binding.image);
        if(Objects.equals(message.getSuperviseFlag(), "2")){
            binding.status.setImageDrawable(context.getResources().getDrawable(R.drawable.warn_state1));
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_start));
        }else{
            binding.status.setImageDrawable(context.getResources().getDrawable(R.drawable.warn_state2));
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_ing));
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


    public void onItemClick(Check message){
        listener.onItemClick(message);
    }

    public interface OnItemClickListener{
        void onItemClick(Check message);
    }
}
