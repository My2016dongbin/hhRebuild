package com.haohai.platform.fireforestplatform.old.linyi;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class ResourceHistoryViewBinder extends ItemViewProvider<JSONObject, ResourceHistoryViewBinder.ViewHolder> {

    public OnResourceHistoryItemClick listener;
    LayoutInflater myInflater;
    Context context;

    public void setListener(OnResourceHistoryItemClick listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_resource_history, parent, false);
        myInflater = inflater;
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull JSONObject obj) {
        try {
            holder.tv_createUser.setText(new CommonUtils().parseNull(obj.getString("createUser"),""));
            holder.tv_userName.setText(new CommonUtils().parseNull(obj.getString("userName"),""));
            holder.tv_description.setText(new CommonUtils().parseNull(obj.getString("description"),""));
            holder.tv_regulation.setText(new CommonUtils().parseNull(obj.getString("regulation"),""));
            try{
                holder.tv_createTime.setText(obj.getString("createTime").replace("T"," ").substring(0,19));
                holder.tv_updateTime.setText(obj.getString("regulationTime").replace("T"," ").substring(0,19));
            }catch (Exception e){

            }
            List<String> checkList = new ArrayList<>();
            List<String> zhengzhiList = new ArrayList<>();
            JSONArray auditImgs = obj.getJSONArray("auditImgs");
            JSONArray regulationImgs = obj.getJSONArray("regulationImgs");
            for (int i = 0; i < auditImgs.length(); i++) {
                String url = (String) auditImgs.get(i);
                checkList.add(url);
            }
            for (int i = 0; i < regulationImgs.length(); i++) {
                String url = (String) regulationImgs.get(i);
                zhengzhiList.add(url);
            }
            Log.e("bingo", "onBindViewHolder: checkList" + checkList.toString() );
            Log.e("bingo", "onBindViewHolder: zhengzhiList" + zhengzhiList.toString() );


            for (int i = 0; i < checkList.size(); i++) {
                final int index = i;
                View item = myInflater.inflate(R.layout.resource_info_picture_item, null, false);
                ImageView iv_picture = item.findViewById(R.id.iv_picture);
                Glide.with(context).load(checkList.get(index)).into(iv_picture);
                RxViewAction.clickNoDouble(iv_picture).subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void unused) {
                        Intent intent = new Intent(context, PicActivity.class);
                        intent.putExtra("pic",checkList.get(index));
                        context.startActivity(intent);
                    }
                });
                holder.ll_check.addView(item);
            }
            for (int i = 0; i < zhengzhiList.size(); i++) {
                int index = i;
                View item = myInflater.inflate(R.layout.resource_info_picture_item, null, false);
                ImageView iv_picture = item.findViewById(R.id.iv_picture);
                Glide.with(context).load(zhengzhiList.get(index)).into(iv_picture);
                RxViewAction.clickNoDouble(iv_picture).subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void unused) {
                        Intent intent = new Intent(context, PicActivity.class);
                        intent.putExtra("pic",zhengzhiList.get(index));
                        context.startActivity(intent);
                    }
                });
                holder.ll_zhengzhi.addView(item);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_createUser;
        private final TextView tv_userName;
        private final TextView tv_description;
        private final TextView tv_regulation;
        private final TextView tv_createTime;
        private final TextView tv_updateTime;
        private final LinearLayout ll_check;
        private final LinearLayout ll_zhengzhi;

        ViewHolder(View itemView) {
            super(itemView);
            tv_createUser = ((TextView) itemView.findViewById(R.id.tv_createUser));
            tv_userName = ((TextView) itemView.findViewById(R.id.tv_userName));
            tv_description = ((TextView) itemView.findViewById(R.id.tv_description));
            tv_regulation = ((TextView) itemView.findViewById(R.id.tv_regulation));
            tv_createTime = ((TextView) itemView.findViewById(R.id.tv_createTime));
            tv_updateTime = ((TextView) itemView.findViewById(R.id.tv_updateTime));
            ll_check = ((LinearLayout) itemView.findViewById(R.id.ll_check));
            ll_zhengzhi = ((LinearLayout) itemView.findViewById(R.id.ll_zhengzhi));
        }
    }

    public interface OnResourceHistoryItemClick{
        void onImageClick(JSONObject object,String url);
    }
}
