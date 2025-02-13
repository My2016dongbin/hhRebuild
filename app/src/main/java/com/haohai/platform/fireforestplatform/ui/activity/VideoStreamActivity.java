package com.haohai.platform.fireforestplatform.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityVideoStreamBinding;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.viewmodel.VideoStreamViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class VideoStreamActivity extends BaseLiveActivity<ActivityVideoStreamBinding, VideoStreamViewModel>  {

    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private Media media;
    private IVLCVout ivlcVout;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        HhLog.e("url " + url);
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("视频");
        obtainViewModel().loading.postValue(new LoadingEvent(true, "加载中"));
        //parseProgress(event.getIndex());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                obtainViewModel().loading.postValue(new LoadingEvent(false));
                //binding.sfBack.setVisibility(View.GONE);
            }
        }, 10000);

        binding.sfBack.setVisibility(View.VISIBLE);
        startPlayer(url);
    }

    private void bind_() {
        binding.videoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.videoPlay.setVisibility(View.GONE);
                startPlayer(url);
                obtainViewModel().loading.postValue(new LoadingEvent(true, "加载中"));
                //parseProgress(event.getIndex());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        //binding.sfBack.setVisibility(View.GONE);
                    }
                }, 10000);
            }
        });
    }

    void startPlayer(String playUrl) {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC = new LibVLC(this, options);
        mediaPlayer = new MediaPlayer(libVLC);
        //设置vlc视频铺满布局
        mediaPlayer.setScale(0f);

        mediaPlayer.getVLCVout().setWindowSize(width, (int) (width * 1));//宽，高  播放窗口的大小
        mediaPlayer.setAspectRatio("${" + width + "}:${" + (int) (width * 1) + "}");//宽，高  画面大小
        mediaPlayer.setVolume(0);
        ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.setVideoView(binding.sfVideo);
        ivlcVout.attachViews();

        media = new Media(libVLC, Uri.parse(playUrl));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media.addOption(":network-caching=" + cache);
        media.addOption(":file-caching=" + cache);
        media.addOption(":live-cacheing=" + cache);
        media.addOption(":sout-mux-caching=" + cache);
        media.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer.setMedia(media);
        mediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {

                switch (event.type) {
                    case MediaPlayer.Event.Buffering:
                        // 处理缓冲事件
                        HhLog.e("Buffering");
                        break;
                    case MediaPlayer.Event.EndReached:
                        // 处理播放结束事件
                        HhLog.e("EndReached");
                        binding.videoPlay.setVisibility(View.VISIBLE);
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        binding.videoPlay.setVisibility(View.VISIBLE);
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        break;
                    case MediaPlayer.Event.TimeChanged:
                        // 处理播放进度变化事件
                        HhLog.e("TimeChanged");
                        break;
                    case MediaPlayer.Event.PositionChanged:
                        // 处理播放位置变化事件
                        HhLog.e("PositionChanged");
                        break;
                    case MediaPlayer.Event.Vout:
                        //在视频开始播放之前，视频的宽度和高度可能还没有被确定，因此我们需要在MediaPlayer.Event.Vout事件发生后才能获取到正确的宽度和高度
                        HhLog.e("Vout1");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfBack.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer.play();
    }

    void releasePlayer() {
        if (libVLC == null || mediaPlayer == null ||
                ivlcVout == null || media == null) {
            return;
        }
        mediaPlayer.stop();
        ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.detachViews();
        libVLC.release();

        libVLC = null;
        mediaPlayer = null;
        ivlcVout = null;
        media = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null && libVLC != null) {
            mediaPlayer.release();
            libVLC.release();
        }
    }

    @Override
    protected ActivityVideoStreamBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_video_stream);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public VideoStreamViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(VideoStreamViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }
}