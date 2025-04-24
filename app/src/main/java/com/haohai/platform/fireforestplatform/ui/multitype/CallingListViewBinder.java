package com.haohai.platform.fireforestplatform.ui.multitype;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemCallingListBinding;
import com.haohai.platform.fireforestplatform.ui.activity.RangerActivity;
import com.haohai.platform.fireforestplatform.utils.Action;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.ItemViewProvider;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CallingListViewBinder extends ItemViewProvider<CallingList, CallingListViewBinder.ViewHolder> {
    public Context context;
    public CallingListViewBinder(Context context) {
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
                R.layout.item_calling_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final CallingList callingList) {

        ItemCallingListBinding binding = (ItemCallingListBinding) viewHolder.getBinding();
        binding.name.setText(callingList.getFullName());
        if(callingList.isState()){
            binding.state.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_yes));
        }else{
            binding.state.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_un));
        }

        CommonUtil.click(binding.click, new Action() {
            @Override
            public void click() {
//                callingList.setState(!callingList.isState());
                listener.onItemClick(callingList,!callingList.isState());
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
        void onItemClick(CallingList callingList,boolean state);
    }
}
