package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
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
import com.haohai.platform.fireforestplatform.databinding.ItemEmptyBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemMenuBinding;

import java.io.File;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class MainFgMenuViewBinder extends ItemViewProvider<MainFgMenu, MainFgMenuViewBinder.ViewHolder> {
    public Context context;
    public MainFgMenuViewBinder(Context context) {
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
                R.layout.item_menu, parent, false);
        return new ViewHolder(dataBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"UseCompatLoadingForDrawables", "CheckResult"})
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final MainFgMenu mainFgMenu) {

        ItemMenuBinding binding = (ItemMenuBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, mainFgMenu);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.title.setText(mainFgMenu.getTitle());
        binding.icon.setImageDrawable(context.getResources().getDrawable(mainFgMenu.getRes()));
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


    public void onItemClick(MainFgMenu mainFgMenu){
        listener.onItemClick(mainFgMenu);
    }

    public interface OnItemClickListener{
        void onItemClick(MainFgMenu mainFgMenu);
    }
}
