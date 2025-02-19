package com.haohai.platform.fireforestplatform.old.linyi;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
public class ChooseImageViewBinder extends ItemViewProvider<ChooseImage, ChooseImageViewBinder.ViewHolder> {
    public Context context;
    public OnChooseImageClickListener listener;

    public ChooseImageViewBinder(Context context) {
        this.context = context;
    }

    public void setListener(OnChooseImageClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_choose_image, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final ChooseImage chooseImage) {
        if (chooseImage.isAdd.booleanValue()){
            holder.imageView.setImageResource(R.drawable.ic_bigphoto);
            holder.imageDelete.setVisibility(View.GONE);
        }else {
            Glide.with(context).load(chooseImage.getUri()).into(holder.imageView);
            holder.imageDelete.setVisibility(View.VISIBLE);
        }

        RxViewAction.clickNoDouble(holder.imageView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onImageAddClickListener(chooseImage.isAdd.booleanValue(),chooseImage.getUri(),chooseImage.getuCheckId());
                    }
                });
        RxViewAction.clickNoDouble(holder.imageDelete)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        listener.onImageDelete(chooseImage.getUri(),chooseImage.getuCheckId());
                    }
                });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final ImageView imageDelete;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = ((ImageView) itemView.findViewById(R.id.evaluate_item_image));
            imageDelete = ((ImageView) itemView.findViewById(R.id.evaluate_item_delete));

        }
    }
    public interface OnChooseImageClickListener {
        void onImageAddClickListener(boolean var1, Uri var2, String id);
        void onImageDelete(Uri var1, String id);
    }
}
