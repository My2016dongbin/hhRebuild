package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.haohai.platform.fireforestplatform.databinding.ItemDangerBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemSafetyBinding;
import com.haohai.platform.fireforestplatform.ui.bean.TypeBean;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SafetyViewBinder extends ItemViewProvider<Safety, SafetyViewBinder.ViewHolder> {
    public Context context;
    public SafetyViewBinder(Context context) {
        this.context = context;
    }
    public OnItemClickListener listener;
    private long timeLeft = 0;
    private long timeRight = 0;
    private List<TypeBean> levelList = new ArrayList<>();
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_safety, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Safety message) {

        levelList = new ArrayList<>();
        levelList.add(new TypeBean("5","暂未确认等级"));
        levelList.add(new TypeBean("4","Ⅳ级（一般）"));
        levelList.add(new TypeBean("3","Ⅲ级（较大）"));
        levelList.add(new TypeBean("2","Ⅱ级（重大）"));
        levelList.add(new TypeBean("1","Ⅰ级（特别重大）"));

        ItemSafetyBinding binding = (ItemSafetyBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, message);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.textTitle.setText(message.getRiskName());
        //binding.textTime.setText(CommonUtil.parseDate(message.getDataUpdateTime()));
        binding.address.setText(message.getAddress());
        binding.textStatus.setText(parseLevel(message.getRiskLevel()));
        binding.status.setVisibility(View.GONE);
        if("1".equals(message.getRiskLevel())){
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_ing));
//            Glide.with(context).load(context.getDrawable(R.drawable.warn_state2))
//                    .into(binding.status);
            binding.status.setBackground(context.getResources().getDrawable(R.drawable.warn_state2));
        }else if("2".equals(message.getRiskLevel())){
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_start));
//            Glide.with(context).load(context.getDrawable(R.drawable.warn_state1))
//                    .into(binding.status);
            binding.status.setBackground(context.getResources().getDrawable(R.drawable.warn_state1));
        }else{
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_complete));
//            Glide.with(context).load(context.getDrawable(R.drawable.warn_state3))
//                    .into(binding.status);
            binding.status.setBackground(context.getResources().getDrawable(R.drawable.warn_state3));
        }
        /*if(message.getRiskType()!=null&& Objects.equals(message.getRiskType(), "1")){
            binding.textType1.setText("自然");
            binding.textType2.setText("灾害");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_1))
                    .into(binding.type);
        }else if(message.getRiskType()!=null&& Objects.equals(message.getRiskType(), "2")){
            binding.textType1.setText("地灾");
            binding.textType2.setText("隐患");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_2))
                    .into(binding.type);
        }else{
            binding.textType1.setText("其它");
            binding.textType2.setText("隐患");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_3))
                    .into(binding.type);
        }*/
        binding.textType1.setText("安全");
        binding.textType2.setText("生产");
        Glide.with(context).load(context.getDrawable(R.drawable.warn_1))
                .into(binding.type);

        binding.scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollX - oldScrollX > 1 && new Date().getTime() - timeLeft > 500){
                    //Toast.makeText(context, "左滑", Toast.LENGTH_SHORT).show();
                    binding.scroll.fullScroll(View.FOCUS_LEFT);
                    timeLeft = new Date().getTime();
                }
                if(oldScrollX - scrollX > 1 && new Date().getTime() - timeRight > 500){
                    //Toast.makeText(context, "右滑", Toast.LENGTH_SHORT).show();
                    binding.scroll.fullScroll(View.FOCUS_RIGHT);
                    timeRight = new Date().getTime();
                }
            }
        });
        binding.edit.setOnClickListener(v -> {
            listener.onEditClick(message);
        });
        binding.delete.setOnClickListener(v -> {
            listener.onDeleteClick(message);
        });
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

    private String parseLevel(String incidentLevel) {
        for (int m = 0; m < levelList.size(); m++) {
            if(Objects.equals(levelList.get(m).getId(), incidentLevel)){
                return levelList.get(m).getTitle();
            }
        }
        return "-";
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


    public void onItemClick(Safety message){
        listener.onItemClick(message);
    }

    public interface OnItemClickListener{
        void onItemClick(Safety message);
        void onEditClick(Safety message);
        void onDeleteClick(Safety message);
    }
}
