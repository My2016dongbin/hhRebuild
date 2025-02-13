package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemIdentifyBinding;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.util.Date;
import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class IdentifyViewBinder extends ItemViewProvider<Identify, IdentifyViewBinder.ViewHolder> {
    public Context context;
    public IdentifyViewBinder(Context context) {
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
                R.layout.item_identify, parent, false);
        return new ViewHolder(dataBinding);
    }

    @RequiresApi(api = 31)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Identify message) {

        ItemIdentifyBinding binding = (ItemIdentifyBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, message);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.textTitle.setText(message.getName());
        binding.textTime.setText(parse19(message.getAlarmDatetime()));
        binding.address.setText(message.getAddress());
        binding.textStatus.setText(parseStatus(message));
        Glide.with(context).load(message.getPicPath1())
                .error(R.drawable.ic_no_pic)
        .transforms(new CenterCrop(), new GranularRoundedCorners(18,0,0,18))
                .into(binding.image);

        if(Objects.equals(message.getIsHandle(), "1")){
            if(Objects.equals(message.getIsReal(), "1")){
                //result = "真实";
                binding.status.setImageDrawable(context.getResources().getDrawable(R.drawable.warn_state1));
                binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_start));
            }else{
                //result = "疑似";
                binding.status.setImageDrawable(context.getResources().getDrawable(R.drawable.warn_state3));
                binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_complete));
            }
        }else{
            //result = "未处理";
            binding.status.setImageDrawable(context.getResources().getDrawable(R.drawable.warn_state2));
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_ing));
        }
    }

    private String parseStatus(Identify message) {
        String result;
        if(Objects.equals(message.getIsHandle(), "1")){
            if(Objects.equals(message.getIsReal(), "1")){
                result = "真实";
            }else{
                result = "疑似";
            }
        }else{
            result = "未处理";
        }
        return result;
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


    public void onItemClick(Identify message){
        listener.onItemClick(message);
    }

    public interface OnItemClickListener{
        void onItemClick(Identify message);
    }
}
