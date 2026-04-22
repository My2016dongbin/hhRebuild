package com.haohai.platform.fireforestplatform.ui.activity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_FULL_SCREEN = "extra_full_screen";

    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private Media media;
    private IVLCVout ivlcVout;
    private String url;
    private boolean isFullScreenMode;
    private boolean isPlayerPaused;
    private final Handler controllerHandler = new Handler();
    private final Runnable hideControllerRunnable = new Runnable() {
        @Override
        public void run() {
            binding.videoController.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra(EXTRA_URL);
        isFullScreenMode = getIntent().getBooleanExtra(EXTRA_FULL_SCREEN, false);
        HhLog.e("url " + url);
        updateScreenMode();
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("视频");
        binding.topBar.getRoot().setVisibility(isFullScreenMode ? View.GONE : View.VISIBLE);
        binding.videoController.setVisibility(View.GONE);
        isPlayerPaused = false;
        binding.videoController.setPlaying(true);
        binding.videoController.setFullScreenMode(isFullScreenMode);
        updateVideoLayout();
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
        binding.videoContainer.post(() -> startPlayer(url));
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
        binding.videoController.setOnPlayClickListener(v -> togglePlay());
        binding.videoController.setOnFullScreenClickListener(v -> toggleFullScreen());
        binding.sfVideo.setOnClickListener(v -> toggleController());
        binding.sfBack.setOnClickListener(v -> toggleController());
    }

    void startPlayer(String playUrl) {
        final ArrayList<String> options = new ArrayList<>();
        isPlayerPaused = false;
        binding.videoController.setPlaying(true);
        int width = binding.videoContainer.getWidth();
        int height = binding.videoContainer.getHeight();
        if (width <= 0 || height <= 0) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            width = dm.widthPixels;
            height = isFullScreenMode ? dm.heightPixels : (int) (width * 1f);
        }
        releasePlayer();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC = new LibVLC(this, options);
        mediaPlayer = new MediaPlayer(libVLC);
        //设置vlc视频铺满布局
        mediaPlayer.setScale(0f);

        mediaPlayer.getVLCVout().setWindowSize(width, height);//宽，高  播放窗口的大小
        mediaPlayer.setAspectRatio(null);//保留原视频比例
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
                        isPlayerPaused = true;
                        binding.videoController.setPlaying(false);
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        binding.videoPlay.setVisibility(View.VISIBLE);
                        isPlayerPaused = true;
                        binding.videoController.setPlaying(false);
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
                        isPlayerPaused = false;
                        binding.videoController.setPlaying(true);
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfBack.setVisibility(View.GONE);
                                    showControllerTemporarily();
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

    private void togglePlay() {
        if (mediaPlayer == null) {
            return;
        }
        if (isPlayerPaused) {
            mediaPlayer.play();
            isPlayerPaused = false;
            binding.videoPlay.setVisibility(View.GONE);
            binding.videoController.setPlaying(true);
        } else {
            mediaPlayer.pause();
            isPlayerPaused = true;
            binding.videoController.setPlaying(false);
        }
        showControllerTemporarily();
    }

    private void toggleFullScreen() {
        if (isFullScreenMode) {
            finish();
            return;
        }
        if (url == null || url.isEmpty()) {
            return;
        }
        startActivity(new android.content.Intent(this, VideoStreamActivity.class)
                .putExtra(EXTRA_URL, url)
                .putExtra(EXTRA_FULL_SCREEN, true));
    }

    private void updateScreenMode() {
        if (isFullScreenMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void updateVideoLayout() {
        ViewGroup.LayoutParams layoutParams = binding.sfVideo.getLayoutParams();
        if (isFullScreenMode) {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            layoutParams.height = (int) (300 * getResources().getDisplayMetrics().density);
        }
        binding.sfVideo.setLayoutParams(layoutParams);
    }

    private void toggleController() {
        if (binding.videoController.isShowing()) {
            hideController();
        } else {
            showControllerTemporarily();
        }
    }

    private void showControllerTemporarily() {
        binding.videoController.bringToFront();
        binding.videoController.setVisibility(View.VISIBLE);
        controllerHandler.removeCallbacks(hideControllerRunnable);
        controllerHandler.postDelayed(hideControllerRunnable, 3000);
    }

    private void hideController() {
        controllerHandler.removeCallbacks(hideControllerRunnable);
        binding.videoController.setVisibility(View.GONE);
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
        controllerHandler.removeCallbacks(hideControllerRunnable);

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
