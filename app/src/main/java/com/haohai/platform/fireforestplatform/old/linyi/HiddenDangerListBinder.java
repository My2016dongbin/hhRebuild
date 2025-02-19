package com.haohai.platform.fireforestplatform.old.linyi;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;

import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class HiddenDangerListBinder extends ItemViewProvider<HiddenDangerList, HiddenDangerListBinder.ViewHolder> {

    public OnHiddenListItemClick listener;

    public void setListener(OnHiddenListItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_hiddendanger_list, parent, false);
        return new ViewHolder(root);
    }

    //userType 用户类型: 1:检查人 2:整治人3：综合整治人 4 : 任务发布人
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull HiddenDangerList hiddenDangerList) {
        holder.tv_title.setText(hiddenDangerList.getTitle().replace("null",""));
        holder.tv_area.setText(hiddenDangerList.getArea().replace("null",""));
        if(hiddenDangerList.getStatus() == 2 && hiddenDangerList.getUserType()==1){
            holder.tv_status.setText("待审核");
            holder.tv_status.setTextColor(Color.parseColor("#00BFFF"));
            holder.btn_shenhe.setVisibility(View.VISIBLE);
            holder.btn_zhengzhi.setVisibility(View.GONE);
            holder.btn_check.setVisibility(View.GONE);
            holder.btn_pass.setVisibility(View.GONE);
        } else if(hiddenDangerList.getStatus() == 3 && (hiddenDangerList.getUserType()==2||hiddenDangerList.getUserType()==3)){
            holder.tv_status.setText("待整治");
            holder.tv_status.setTextColor(Color.parseColor("#FF0000"));
            holder.btn_zhengzhi.setVisibility(View.VISIBLE);
            holder.btn_shenhe.setVisibility(View.GONE);
            holder.btn_check.setVisibility(View.GONE);
            holder.btn_pass.setVisibility(View.GONE);
        }else if(hiddenDangerList.getStatus() == 1 && hiddenDangerList.getUserType()==1){
            holder.tv_status.setText("待检查");
            holder.btn_check.setVisibility(View.VISIBLE);
            holder.btn_zhengzhi.setVisibility(View.GONE);
            holder.btn_shenhe.setVisibility(View.GONE);
            holder.btn_pass.setVisibility(View.GONE);
            holder.tv_status.setTextColor(Color.parseColor("#000000"));
        }else if(hiddenDangerList.getStatus() == 4){//已通过 4
            holder.tv_status.setText("已通过");
            holder.btn_pass.setVisibility(View.VISIBLE);
            holder.btn_check.setVisibility(View.GONE);
            holder.btn_zhengzhi.setVisibility(View.GONE);
            holder.btn_shenhe.setVisibility(View.GONE);
            holder.tv_status.setTextColor(Color.parseColor("#000000"));
        }else{
            if(hiddenDangerList.getStatus() == 1){
                holder.tv_status.setText("待检查");
            }else if(hiddenDangerList.getStatus() == 2){
                holder.tv_status.setText("待审核");
                holder.tv_status.setTextColor(Color.parseColor("#00BFFF"));
            }else if(hiddenDangerList.getStatus() == 3){
                holder.tv_status.setText("待整治");
                holder.tv_status.setTextColor(Color.parseColor("#FF0000"));
            }else if(hiddenDangerList.getStatus() == 4){
                holder.tv_status.setText("已通过");
            }else if(hiddenDangerList.getStatus() == 0){
                holder.tv_status.setText("未开始");
            }else{
                holder.tv_status.setText("数据异常");
            }
            holder.btn_pass.setVisibility(View.GONE);
            holder.btn_check.setVisibility(View.GONE);
            holder.btn_zhengzhi.setVisibility(View.GONE);
            holder.btn_shenhe.setVisibility(View.GONE);
            holder.tv_status.setTextColor(Color.parseColor("#000000"));
        }
        holder.tv_location.setText(hiddenDangerList.getLongitude().replace("null","")+","+hiddenDangerList.getLatitude().replace("null",""));
        holder.tv_checkdate.setText(new CommonUtils().parseDate(hiddenDangerList.getCheckDate()!=null?hiddenDangerList.getCheckDate():""));
        holder.tv_worker.setText("发起人:" + hiddenDangerList.getWorker().replace("null",""));
        if(Objects.equals(hiddenDangerList.getPlayDate(), hiddenDangerList.getCheckDate())){
            holder.tv_playdate.setText("");
        }else{
            holder.tv_playdate.setText(new CommonUtils().parseDate(hiddenDangerList.getPlayDate()!=null?hiddenDangerList.getPlayDate():""));
        }
        holder.tv_endtime.setText(new CommonUtils().parseDate(hiddenDangerList.getEndTime()!=null?hiddenDangerList.getEndTime():""));
        holder.tv_result.setText(hiddenDangerList.getResult().replace("null",""));

        RxViewAction.clickNoDouble(holder.btn_check)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onCheckClickListener(hiddenDangerList);
                    }
                });
        RxViewAction.clickNoDouble(holder.btn_zhengzhi)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onZhengzhiClickListener(hiddenDangerList);
                    }
                });
        RxViewAction.clickNoDouble(holder.btn_shenhe)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onShenheClickListener(hiddenDangerList);
                    }
                });
        RxViewAction.clickNoDouble(holder.ll_item)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onHiddenListItemClickListener(hiddenDangerList);
                    }
                });
        RxViewAction.clickNoDouble(holder.btn_guide)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onGuideClickListener(hiddenDangerList);
                    }
                });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_title;
        private final TextView tv_area;
        private final TextView tv_status;
        private final TextView tv_location;
        private final TextView tv_checkdate;
        private final TextView tv_worker;
        private final TextView tv_playdate;
        private final TextView tv_endtime;
        private final TextView tv_result;
        private final Button btn_check;
        private final Button btn_zhengzhi;
        private final Button btn_shenhe;
        private final Button btn_pass;
        private final Button btn_guide;
        private final LinearLayout ll_item;

        ViewHolder(View itemView) {
            super(itemView);
            tv_title = ((TextView) itemView.findViewById(R.id.tv_title));
            tv_area = ((TextView) itemView.findViewById(R.id.tv_area));
            tv_status = ((TextView) itemView.findViewById(R.id.tv_status));
            tv_location = ((TextView) itemView.findViewById(R.id.tv_location));
            tv_checkdate = ((TextView) itemView.findViewById(R.id.tv_checkdate));
            tv_worker = ((TextView) itemView.findViewById(R.id.tv_worker));
            tv_playdate = ((TextView) itemView.findViewById(R.id.tv_playdate));
            tv_endtime = ((TextView) itemView.findViewById(R.id.tv_endtime));
            tv_result = ((TextView) itemView.findViewById(R.id.tv_result));
            btn_check = ((Button) itemView.findViewById(R.id.btn_check));
            btn_zhengzhi = ((Button) itemView.findViewById(R.id.btn_zhengzhi));
            btn_shenhe = ((Button) itemView.findViewById(R.id.btn_shenhe));
            btn_guide = ((Button) itemView.findViewById(R.id.btn_guide));
            btn_pass = ((Button) itemView.findViewById(R.id.btn_pass));
            ll_item = ((LinearLayout) itemView.findViewById(R.id.ll_item));
        }
    }

    public interface OnHiddenListItemClick{
        void onHiddenListItemClickListener(HiddenDangerList hiddenDangerList);
        void onCheckClickListener(HiddenDangerList hiddenDangerList);
        void onZhengzhiClickListener(HiddenDangerList hiddenDangerList);
        void onShenheClickListener(HiddenDangerList hiddenDangerList);
        void onGuideClickListener(HiddenDangerList hiddenDangerList);
    }
}
