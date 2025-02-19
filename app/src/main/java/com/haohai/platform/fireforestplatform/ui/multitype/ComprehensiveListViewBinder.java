package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemComprehensiveListBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemTaskListBinding;
import com.haohai.platform.fireforestplatform.ui.activity.ComprehensiveCKActivity;
import com.haohai.platform.fireforestplatform.ui.activity.ComprehensiveZZActivity;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ComprehensiveListViewBinder extends ItemViewProvider<ComprehensiveList, ComprehensiveListViewBinder.ViewHolder> {
    public Context context;
    public ComprehensiveListViewBinder(Context context) {
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
                R.layout.item_comprehensive_list, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final ComprehensiveList taskList) {

        ItemComprehensiveListBinding binding = (ItemComprehensiveListBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, taskList);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁

        binding.title.setText(taskList.getName());
        binding.grid.setText(taskList.getGridName());
        binding.status.setText(parseStatus(taskList.getStatus()));
        binding.person.setText(taskList.getCreateUser());
        binding.checkTime.setText(parse19(taskList.getStartTime()));
        binding.endTime.setText(parse19(taskList.getEndTime()));

        List<ComprehensiveList.PlanResourceDTOSBean> resourceList = taskList.getPlanResourceDTOS();
        binding.llResource.removeAllViews();
        if(resourceList!=null){
            for (int m = 0; m < resourceList.size(); m++) {
                ComprehensiveList.PlanResourceDTOSBean res = resourceList.get(m);
                View view = LayoutInflater.from(context).inflate(R.layout.comprehensive_res, null);
                TextView name = view.findViewById(R.id.name);
                TextView type = view.findViewById(R.id.type);
                TextView button = view.findViewById(R.id.button);
                name.setText(res.getName());
                type.setText(parseType(res.getResourceType()));
                //0 1待整治 2待检查/审核 3已完成 blue_coner_6 gray_coner_6
                if(Objects.equals(res.getStatus(), "4")){
                    button.setText("整治");
                    button.setBackground(context.getResources().getDrawable(R.drawable.blue_coner_6));
                    button.setOnClickListener(v -> {
                        CommonData.comprehensiveRes = res;
                        context.startActivity(new Intent(context, ComprehensiveZZActivity.class));
                    });
                }else if(Objects.equals(res.getStatus(), "2")){
                    button.setText("审核");
                    button.setBackground(context.getResources().getDrawable(R.drawable.blue_coner_6));
                    button.setOnClickListener(v -> {
                        CommonData.comprehensiveRes = res;
                        context.startActivity(new Intent(context, ComprehensiveCKActivity.class));
                    });
                }else{
                    button.setText("完成");
                    button.setBackground(context.getResources().getDrawable(R.drawable.gray_coner_6));

                }
                binding.llResource.addView(view);
            }
        }

    }

    private String parseType(String resourceType) {
        String type = "";
        if(Objects.equals(resourceType, "helicopterPoint")){
            type = "直升机机降点";
        }else if(Objects.equals(resourceType, "team")){
            type = "消防专业队";
        }else if(Objects.equals(resourceType, "dangerSource")){
            type = "危险源";
        }else if(Objects.equals(resourceType, "isolationBelt")){
            type = "隔离带";
        }else if(Objects.equals(resourceType, "fireEscape")){
            type = "防火通道";
        }else if(Objects.equals(resourceType, "chemicalEnterprises")){
            type = "危化品企业";
        }else if(Objects.equals(resourceType, "isolationNet")){
            type = "隔离网";
        }else if(Objects.equals(resourceType, "miningEnterprises")){
            type = "采矿企业";
        }else if(Objects.equals(resourceType, "materialRepository")){
            type = "物资库";
        }else if(Objects.equals(resourceType, "waterSource")){
            type = "水源地";
        }else if(Objects.equals(resourceType, "cemetery")){
            type = "墓地";
        }else if(Objects.equals(resourceType, "checkStation")){
            type = "护林检查站";
        }else if(Objects.equals(resourceType, "kakou")){
            type = "卡口监控点";
        }else if(Objects.equals(resourceType, "monitor")){
            type = "视频监控点";
        }else if(Objects.equals(resourceType, "fireCommand")){
            type = "防火指挥部";
        }
        return type;
    }

    private String parseStatus(String status) {
        String str = "进行中";
        if(Objects.equals(status, "5")){
            str = "已结束";
        }
        return str;
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


    public void onItemClick(ComprehensiveList taskList){
        listener.onItemClick(taskList);
    }

    public interface OnItemClickListener{
        void onItemClick(ComprehensiveList taskList);
    }
}
