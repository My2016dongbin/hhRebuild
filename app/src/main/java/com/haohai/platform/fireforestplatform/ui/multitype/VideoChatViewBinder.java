package com.haohai.platform.fireforestplatform.ui.multitype;

import static com.netease.lava.nertc.sdk.video.NERtcVideoStreamType.kNERtcVideoStreamTypeMain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemNewsBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemVideoChatBinding;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.netease.lava.api.IVideoRender;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;

import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class VideoChatViewBinder extends ItemViewProvider<VideoChat, VideoChatViewBinder.ViewHolder> {
    public Context context;
    public VideoChatViewBinder(Context context) {
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
                R.layout.item_video_chat, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final VideoChat videoChat) {

        ItemVideoChatBinding binding = (ItemVideoChatBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, videoChat);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings(); //防止闪烁


        if(Objects.equals(SPUtils.get(context, SPValue.phone,"")+"", videoChat.getId()+"")){
            //把自己视频放到画面
            NERtcEx.getInstance().setupLocalVideoCanvas(binding.video);
            NERtcEx.getInstance().startVideoPreview(kNERtcVideoStreamTypeMain);
        }else{
            //对方开启视频，按需设置画布及订阅视频
            NERtcEx.getInstance().setupRemoteVideoCanvas(binding.video,videoChat.getId());
            NERtcEx.getInstance().subscribeRemoteVideoStream(videoChat.getId(), NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh,true);
        }
        binding.video.setMirror(true);
        binding.video.setScalingType(IVideoRender.ScalingType.SCALE_ASPECT_BALANCED);
        if(videoChat.isVideo()){
            binding.header.setVisibility(View.GONE);
            binding.video.setVisibility(View.VISIBLE);
        }else{
            binding.header.setVisibility(View.VISIBLE);
            binding.video.setVisibility(View.GONE);
            Glide.with(context).load(videoChat.getHeader())
                    .error(R.drawable.ic_no_pic)
                    .circleCrop()
                    .into(binding.header);
        }
        if(videoChat.isAudio()){
            binding.voice.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_audio_open));
        }else{
            binding.voice.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_audio_close));
        }
        binding.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(videoChat);
            }
        });

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

    public interface OnItemClickListener{
        void onItemClick(VideoChat videoChat);
    }
}
