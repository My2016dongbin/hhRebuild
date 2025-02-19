package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemDangerBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemUploadBinding;
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
public class UploadViewBinder extends ItemViewProvider<Upload, UploadViewBinder.ViewHolder> {
    public Context context;

    public UploadViewBinder(Context context) {
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
                R.layout.item_upload, parent, false);
        return new ViewHolder(dataBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final Upload message) {

        ItemUploadBinding binding = (ItemUploadBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, message);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.textTitle.setText(message.getIncidentTitle());
        binding.textTime.setText(parse19(message.getReportingTime()));
        try {
            binding.address.setText(message.getEmergIncidentVersion().getIncidentAddr());
        } catch (Exception e) {
            HhLog.e(e.getMessage());
        }
        if ("0".equals(message.getHandleStatus())) {
            binding.textStatus.setText("未处置");
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_ing));
            Glide.with(context).load(context.getDrawable(R.drawable.warn_state2))
                    .into(binding.status);
        } else if ("1".equals(message.getHandleStatus())) {
            binding.textStatus.setText("处置中");
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_start));
            Glide.with(context).load(context.getDrawable(R.drawable.warn_state1))
                    .into(binding.status);
        } else {
            binding.textStatus.setText("处置完成");
            binding.textStatus.setTextColor(context.getResources().getColor(R.color.status_complete));
            Glide.with(context).load(context.getDrawable(R.drawable.warn_state3))
                    .into(binding.status);
        }
        if ((message.getIncidentType() != null && message.getIncidentType().startsWith("01")) || (message.getIncidentTypeName() != null && message.getIncidentTypeName().startsWith("自然灾害"))) {
            binding.textType1.setText("自然");
            binding.textType2.setText("灾害");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_2))
                    .into(binding.type);
        } else if ((message.getIncidentType() != null && message.getIncidentType().startsWith("02")) || (message.getIncidentTypeName() != null && message.getIncidentTypeName().startsWith("事故灾害"))) {
            binding.textType1.setText("事故");
            binding.textType2.setText("灾害");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_2))
                    .into(binding.type);
        }else if ((message.getIncidentType() != null && message.getIncidentType().startsWith("03")) || (message.getIncidentTypeName() != null && message.getIncidentTypeName().startsWith("公共卫生"))) {
            binding.textType1.setText("公共");
            binding.textType2.setText("卫生");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_1))
                    .into(binding.type);
        }else if ((message.getIncidentType() != null && message.getIncidentType().startsWith("04")) || (message.getIncidentTypeName() != null && message.getIncidentTypeName().startsWith("社会安全"))) {
            binding.textType1.setText("社会");
            binding.textType2.setText("安全");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_3))
                    .into(binding.type);
        } else {
            binding.textType1.setText("其它");
            binding.textType2.setText("隐患");
            Glide.with(context).load(context.getDrawable(R.drawable.warn_3))
                    .into(binding.type);
        }

        /*if(message.isSlide()){
            timeRight = new Date().getTime();
            binding.scroll.fullScroll(View.FOCUS_RIGHT);
        }else{
            timeLeft = new Date().getTime();
            binding.scroll.fullScroll(View.FOCUS_LEFT);
        }*/
        binding.scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollX - oldScrollX > 1 && new Date().getTime() - timeLeft > 500) {
                    //Toast.makeText(context, "左滑", Toast.LENGTH_SHORT).show();
                    binding.scroll.fullScroll(View.FOCUS_LEFT);
                    timeLeft = new Date().getTime();
                    /*message.setSlide(false);*/
                }
                if (oldScrollX - scrollX > 1 && new Date().getTime() - timeRight > 500) {
                    //Toast.makeText(context, "右滑", Toast.LENGTH_SHORT).show();
                    binding.scroll.fullScroll(View.FOCUS_RIGHT);
                    timeRight = new Date().getTime();
                    /*message.setSlide(true);*/
                }
            }
        });

        if (Objects.equals(message.getReportingFlag(), "1") || Objects.equals(message.getReportingFlag(), "3")) {
            binding.edit.setBackground(context.getDrawable(R.color.back_blue));
            binding.edit.setOnClickListener(v -> {
                listener.onEditClick(message);
            });
        }else{
            binding.edit.setBackground(context.getDrawable(R.color.c2));
        }
        if (Objects.equals(message.getReportingFlag(), "0")) {
            binding.delete.setBackground(context.getDrawable(R.drawable.red_coner_6));
            binding.delete.setOnClickListener(v -> {
                listener.onDeleteClick(message);
            });
        } else {
            binding.delete.setBackground(context.getDrawable(R.drawable.gray_coner_6));
        }
    }

    private String parseFirst(String taskImg) {
        String str = taskImg;
        try {
            int i = taskImg.indexOf(",");
            str = taskImg.substring(0, i);
        } catch (Exception e) {
            Log.e("TAG", "parseFirst: " + e.getMessage());
        }
        return str;
    }

    private String parse19(String str) {
        String r = str;
        try {
            r = str.substring(0, 19).replace("T", " ");
        } catch (Exception e) {
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


    public void onItemClick(Upload upload) {
        HhLog.e("onItemClick " + new Gson().toJson(upload));
        listener.onItemClick(upload);
    }

    public interface OnItemClickListener {
        void onItemClick(Upload upload);

        void onEditClick(Upload message);

        void onDeleteClick(Upload message);
    }
}
