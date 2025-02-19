package com.haohai.platform.fireforestplatform.ui.banner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;



public class ImageAdapter extends BannerAdapter<BannerBean, ImageAdapter.BannerViewHolder> {

    public ImageAdapter(List<BannerBean> mData) {
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        super(mData);
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_banner,parent, false);

        return new BannerViewHolder(view);
    }

    @Override
    public void onBindView(BannerViewHolder holder, BannerBean data, int position, int size) {
        ImageView imageView = holder.view.findViewById(R.id.image);
        TextView textView = holder.view.findViewById(R.id.text);
        Glide.with(HhApplication.getInstance()).load(data.getImage()).into(imageView);
        textView.setText(data.getTitle());
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        View view;

        public BannerViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
        }
    }
}
