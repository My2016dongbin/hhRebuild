package com.haohai.platform.fireforestplatform.ui.multitype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;

public class AroundViewBinder extends ItemViewProvider<Resource, AroundViewBinder.ViewHolder> {

    public AroundItemClick listener;

    public void setListener(AroundItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_one_body_fire2, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final Resource resource) {
        holder.textView1.setText("资源点名称 : " + resource.getName());
        holder.textView2.setText("资源类型 : " + parseType(resource.getResourceType()+""));
        holder.textView3.setText("经度、纬度 : " + resource.getPosition().getLng() +"、" + resource.getPosition().getLat());
        if (resource.getAddress()==null) {
            holder.textView4.setVisibility(View.GONE);
        }else {
            holder.textView4.setVisibility(View.VISIBLE);
            holder.textView4.setText("详细地址 : " +  resource.getAddress() );
        }
        RxViewAction.clickNoDouble(holder.oneBodyLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onAroundItemClickListener(resource);
                    }
                });
    }

    private String parseType(String resourceType) {
        String type = "";
        if (resourceType.equals("monitor")) {
            type = "视频监控点";
        }else if (resourceType.equals("team")){
            type = "消防专业队";
        }else if (resourceType.equals("helicopterPoint")){
            type = "直升机升降点";
        }else if (resourceType.equals("dangerSource")){
            type = "危险源";
        }else if (resourceType.equals("foreastRoom")){
            type = "物资储备库";
        }else if (resourceType.equals("materialRepository")){
            type = "物资储备库";
        }else if (resourceType.equals("cemetery")){
            type = "墓地";
        }else if (resourceType.equals("watchTower")){
            type = "新建水源地";
        }else if (resourceType.equals("fireCommand")){
            type = "改造水源地";
        }else if (resourceType.equals("waterSource")){
            type = "现有水源地";
        }else if (resourceType.equals("checkStation")){
            type = "护林检查站";
        }else if (resourceType.equals("foreastCenter")){
            type = "森林防火监测中心";
        }
        return type;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView1;
        private final TextView textView2;
        private final TextView textView3;
        private final TextView textView4;
        private final FrameLayout oneBodyLayout;

        ViewHolder(View itemView) {
            super(itemView);
            textView1 = ((TextView) itemView.findViewById(R.id.text_view1));
            textView2 = ((TextView) itemView.findViewById(R.id.text_view2));
            textView3 = ((TextView) itemView.findViewById(R.id.text_view3));
            textView4 = ((TextView) itemView.findViewById(R.id.text_view4));
            oneBodyLayout = ((FrameLayout) itemView.findViewById(R.id.one_body_layout));
        }
    }
    public interface AroundItemClick{
        void onAroundItemClickListener(Resource resource);
    }

}
