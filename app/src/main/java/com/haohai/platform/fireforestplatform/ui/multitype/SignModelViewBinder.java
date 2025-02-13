package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemSignModelBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SignModelViewBinder extends ItemViewProvider<SignModel, SignModelViewBinder.ViewHolder> {
    public Context context;
    public SignModelViewBinder(Context context) {
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
                R.layout.item_sign_model, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final SignModel signModel) {

        ItemSignModelBinding binding = (ItemSignModelBinding) viewHolder.getBinding();
        binding.index.setText(signModel.getIndex()+"");
        binding.name.setText(signModel.getName());
        binding.time.setText(parse9(signModel.getLastPatrolDate()));
        binding.distance.setText(signModel.getTotalPatrolLength()+"");
        binding.count.setText(signModel.getAttendanceTimes()+"");
        if(signModel.getIndex()%2==1){
            binding.background.setBackgroundColor(context.getResources().getColor(R.color.c));
        }else{
            binding.background.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        binding.backViewIn.setOnClickListener(v -> {
            onItemClick(signModel);
        });

    }

    private String parseFirst(String taskImg) {
        String str = taskImg;
        try{
            int i = taskImg.indexOf(",");
            str = taskImg.substring(0,i);
        }catch (Exception e){
            Log.e("TAG", "parseFirst: " + e.getMessage() );
        }
        return str;
    }

    private String parse9(String str) {
        if(str == null){
            return "暂无记录";
        }
        String r = str;
        try{
            r = str.substring(0,10).replace("T"," ");
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


    public void onItemClick(SignModel signModel){
        listener.onItemClick(signModel);
    }

    public interface OnItemClickListener{
        void onItemClick(SignModel signModel);
    }
}
