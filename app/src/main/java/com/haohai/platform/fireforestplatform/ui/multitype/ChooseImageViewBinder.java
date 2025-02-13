package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemChooseImageBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemMenuBinding;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ChooseImageViewBinder extends ItemViewProvider<ChooseImage, ChooseImageViewBinder.ViewHolder> {
    public Context context;
    public ChooseImageViewBinder(Context context) {
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
                R.layout.item_choose_image, parent, false);
        return new ViewHolder(dataBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"UseCompatLoadingForDrawables", "CheckResult"})
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final ChooseImage chooseImage) {

        ItemChooseImageBinding binding = (ItemChooseImageBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, chooseImage);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        if (chooseImage.isAdd){
            binding.evaluateItemImage.setImageResource(R.drawable.ic_add_photo);
            binding.evaluateItemDelete.setVisibility(View.GONE);
        }else {
            Glide.with(context).load(chooseImage.getUri()).into(binding.evaluateItemImage);
            binding.evaluateItemDelete.setVisibility(View.VISIBLE);
        }

        binding.evaluateItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageAddClickListener(chooseImage.isAdd,chooseImage.getUri(),chooseImage.getuCheckId());
            }
        });
        binding.evaluateItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageDelete(chooseImage.getUri(),chooseImage.getuCheckId());
            }
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
        void onImageAddClickListener(boolean isAdd, Uri uri, String id);
        void onImageDelete(Uri uri,String id);
    }
}
