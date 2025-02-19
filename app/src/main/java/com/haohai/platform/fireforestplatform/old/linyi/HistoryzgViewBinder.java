package com.haohai.platform.fireforestplatform.old.linyi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;

import me.drakeet.multitype.ItemViewProvider;
import rx.functions.Action1;


/**
 * Created by geyang on 2020/2/6.
 */
public class HistoryzgViewBinder extends ItemViewProvider<Historyzg, HistoryzgViewBinder.ViewHolder> {
    public Context context;
    public OnChooseImageClickListener listener;
    public HistoryzgViewBinder(Context context) {
        this.context = context;
    }

    public void setListener(OnChooseImageClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_history_zg, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final Historyzg historyzg) {
        Glide.with(context).load(historyzg.getImgurl()).into(holder.imageView);
        holder.beizhuView.setText(historyzg.getDescription());
        holder.beizhuName.setText("第"+historyzg.getCishu()+"整治");
        RxViewAction.clickNoDouble(holder.imageView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onZZImageClick(historyzg.getImgurl());
                    }
                });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView beizhuView;
        private final ImageView imageView;
        private final TextView beizhuName;
        ViewHolder(View itemView) {
            super(itemView);
            beizhuView = itemView.findViewById(R.id.beizhu_edit);
            imageView = ((ImageView) itemView.findViewById(R.id.evaluate_item_image));
            beizhuName=itemView.findViewById(R.id.beizhu_name);
        }
    }
    public interface OnChooseImageClickListener {
        void onZZImageClick(String imgurl);
    }
}
