package com.haohai.platform.fireforestplatform.old.linyi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;

import me.drakeet.multitype.ItemViewProvider;


/**
 * Created by geyang on 2020/12/3.
 */
public class ResourceSearchListViewBinder extends ItemViewProvider<ResourceSearch, ResourceSearchListViewBinder.ViewHolder> {
    private String status;
    public OnPlanItemClick listener;
    private String peopleName="";
    public void setListener(OnPlanItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_resource_search_list, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final ResourceSearch checkPlanList) {
        switch (checkPlanList.getStatus()){
            case 1:
                status="未开始";
                break;
            case 2:
                status="进行中";
                break;
            case 3:
                status="已结束";
                break;
        }
        holder.textView1.setText(checkPlanList.getName());
        holder.textView2.setText(checkPlanList.getGridName());
        holder.textView3.setText(status);
        if (checkPlanList.getStartTime()!=null) {
            holder.textView4.setText("检查时间:" + checkPlanList.getEndTime());
        }
        if (checkPlanList.getEndTime()!=null) {
            holder.endtimeText.setText("任务截止:" + checkPlanList.getEndTime());
        }
        peopleName="";
        for (int i = 0; i <checkPlanList.getCheckusers().size() ; i++) {
            peopleName+=","+checkPlanList.getCheckusers().get(i).getUserName();
        }
        holder.textView5.setText("执行人:"+peopleName);
        holder.textView6.setText(checkPlanList.getDescription());
//        RxViewAction.clickNoDouble(holder.checkView)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        listener.onChecklistItemClickListener(checkPlanList);
//                    }
//                });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView1;
        private final TextView textView2;
        private final TextView textView3;
        private final TextView textView4;
        private final TextView textView5;
        private final TextView textView6;
        private final LinearLayout planView;
        private final TextView endtimeText;
        private TextView checkView;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = ((TextView) itemView.findViewById(R.id.plan_name));
            textView2 = ((TextView) itemView.findViewById(R.id.jiedao_text));
            textView3 = ((TextView) itemView.findViewById(R.id.zhuangtai_text));
            textView4 = ((TextView) itemView.findViewById(R.id.shijian_text));
            textView5 = ((TextView) itemView.findViewById(R.id.jcr_text));
            textView6 = ((TextView) itemView.findViewById(R.id.beizhu_text));
            checkView = itemView.findViewById(R.id.check_view);
            planView=  itemView.findViewById(R.id.plan_view);
            endtimeText=itemView.findViewById(R.id.endtime_text);
        }

    }
    public interface OnPlanItemClick{
        void onChecklistItemClickListener(ResourceSearch checkPlanList);
    }
}
