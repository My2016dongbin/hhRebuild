package com.haohai.platform.fireforestplatform.ui.multitype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemTutorialBinding;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2026/6/26.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class TutorialItemViewBinder extends ItemViewProvider<TutorialItem, TutorialItemViewBinder.ViewHolder> {
    public Context context;

    public TutorialItemViewBinder(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_tutorial, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull TutorialItem tutorialItem) {
        ItemTutorialBinding binding = (ItemTutorialBinding) viewHolder.getBinding();
        binding.titleLayout.setVisibility(tutorialItem.getType() == TutorialItem.TYPE_TITLE ? View.VISIBLE : View.GONE);
        binding.contentCard.setVisibility(tutorialItem.getType() == TutorialItem.TYPE_TEXT ? View.VISIBLE : View.GONE);
        binding.imageCard.setVisibility(tutorialItem.getType() == TutorialItem.TYPE_IMAGE ? View.VISIBLE : View.GONE);
        if (tutorialItem.getType() == TutorialItem.TYPE_TITLE) {
            binding.titleText.setText(tutorialItem.getContent());
        } else if (tutorialItem.getType() == TutorialItem.TYPE_TEXT) {
            binding.contentText.setText(tutorialItem.getContent());
        } else if (tutorialItem.getType() == TutorialItem.TYPE_IMAGE) {
            binding.tutorialImage.setImageResource(tutorialItem.getImageRes());
        }
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
