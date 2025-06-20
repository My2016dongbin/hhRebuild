package com.haohai.platform.fireforestplatform.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.HhSurfaceView;
import com.haohai.platform.fireforestplatform.utils.MoveSurfaceView;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

import okhttp3.Call;

public class FullVideoActivity extends BaseActivity {
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private Media media;
    private IVLCVout ivlcVout;
    private String url;
    private MoveSurfaceView sfVideoOne1;
    private SurfaceView sfBack;
    private SurfaceView sfBackAll;
    private FrameLayout back;
    private ImageView moveUp;
    private ImageView moveDown;
    private ImageView moveLeft;
    private ImageView moveRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        setContentView(R.layout.activity_full_video);

        initView();
        bindView();
    }

    private void bindView() {
        sfVideoOne1.setOnGestureListener(new MoveSurfaceView.OnGestureListener() {
            @Override
            public void onSingleTap(float x, float y) {
                HhLog.e("外部监听", "单击：" + x + ", " + y);
            }

            @Override
            public void onDoubleTap(float x, float y) {
                HhLog.e("外部监听", "双击：" + x + ", " + y);
            }

            @Override
            public void onZoomInStart(float scaleFactor) {
                HhLog.e("外部监听", "onZoomInStart：" );
                postMove(11, 5, 0);
            }

            @Override
            public void onZoomInStop() {
                HhLog.e("外部监听", "onZoomInStop：" );
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postMove(11, 1, 1);
                    }
                },200);
            }

            @Override
            public void onZoomOutStart(float scaleFactor) {
                HhLog.e("外部监听", "onZoomOutStart：" );
                postMove(12, 5, 0);
            }

            @Override
            public void onZoomOutStop() {
                HhLog.e("外部监听", "onZoomOutStop：" );
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postMove(12, 1, 1);
                    }
                },200);
            }

            @Override
            public void onSwipeLeftStart() {
                HhLog.e("外部监听", "onSwipeLeftStart：" );
                postMove(23, 5, 0);
                moveLeft.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSwipeLeftStop() {
                HhLog.e("外部监听", "onSwipeLeftStop：" );
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postMove(23, 1, 1);
                        moveLeft.setVisibility(View.GONE);
                    }
                },200);
            }

            @Override
            public void onSwipeRightStart() {
                HhLog.e("外部监听", "onSwipeRightStart：" );
                postMove(24, 5, 0);
                moveRight.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSwipeRightStop() {
                HhLog.e("外部监听", "onSwipeRightStop：" );
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postMove(24, 1, 1);
                        moveRight.setVisibility(View.GONE);
                    }
                },200);
            }

            @Override
            public void onSwipeUpStart() {
                HhLog.e("外部监听", "onSwipeUpStart：" );
                postMove(21, 5, 0);
                moveUp.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSwipeUpStop() {
                HhLog.e("外部监听", "onSwipeUpStop：" );
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postMove(21, 1, 1);
                        moveUp.setVisibility(View.GONE);
                    }
                },200);
            }

            @Override
            public void onSwipeDownStart() {
                HhLog.e("外部监听", "onSwipeDownStart：" );
                postMove(22, 5, 0);
                moveDown.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSwipeDownStop() {
                HhLog.e("外部监听", "onSwipeDownStop：" );
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postMove(22, 1, 1);
                        moveDown.setVisibility(View.GONE);
                    }
                },200);
            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        url = getIntent().getStringExtra("url");
        sfVideoOne1 = findViewById(R.id.sf_video_one1);
        sfBack = findViewById(R.id.sf_back);
        sfBackAll = findViewById(R.id.sf_back_all);
        back = findViewById(R.id.back_fl);
        moveUp = findViewById(R.id.move_up);
        moveDown = findViewById(R.id.move_down);
        moveLeft = findViewById(R.id.move_left);
        moveRight = findViewById(R.id.move_right);

        startPlayer();
    }


    void startPlayer() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        releasePlayer();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC = new LibVLC(this, options);
        mediaPlayer = new MediaPlayer(libVLC);
        //设置vlc视频铺满布局
        mediaPlayer.setScale(0f);

        mediaPlayer.getVLCVout().setWindowSize(width, (int) (height * 1));//宽，高  播放窗口的大小
        mediaPlayer.setAspectRatio("${" + width + "}:${" + (int) (height * 1) + "}");//宽，高  画面大小
        mediaPlayer.setVolume(0);
        ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.setVideoView(sfVideoOne1);
        ivlcVout.attachViews();

        media = new Media(libVLC, Uri.parse(url));
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
                        startPlayer();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayer();
                            }catch (Exception e){
                                HhLog.e(e.getMessage());
                            }
                        },1000);
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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    sfVideoOne1.setVisibility(View.VISIBLE);
                                    sfBack.setVisibility(View.GONE);
                                    sfBackAll.setVisibility(View.GONE);
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
        mediaPlayer.release();
        libVLC.release();

        libVLC = null;
        mediaPlayer = null;
        ivlcVout = null;
        media = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }


    public void postMove(int type, int speed, int stop) {
        HhHttp.get()
                .url(URLConstant.GET_CONTROL)
                .addParams("monitorId", CommonData.videoDeleteMonitorId)
                .addParams("channelId", CommonData.videoDeleteChannelId)
                .addParams("speed", speed+"")
                .addParams("stop", stop+"")
                //.addParams("controlId", CommonData.videoDeleteControlId)
                .addParams("controlType", type+"")
                .addParams("gridNo", (String) SPUtils.get(FullVideoActivity.this, SPValue.gridNo,"370214"))
                //.addParams("groupId", (String) SPUtils.get(context, SPValue.groupId,"001021"))
//                .addParams("gridNo", "370214")
                //.addParams("groupId", "001021")
                .build()
                .execute(new LoggedInStringCallback(null,FullVideoActivity.this) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_CONTROL " + URLConstant.GET_CONTROL
                                + "?monitorId=" +CommonData.videoDeleteMonitorId
                                +"&channelId=" +CommonData.videoDeleteChannelId
                                +"&speed=" +speed
                                +"&stop=" +stop
                                +"&controlType=" +type
                                +"&gridNo=" +(String) SPUtils.get(FullVideoActivity.this, SPValue.gridNo,"370214")
                        );
                        HhLog.e("GET_CONTROL " + response);
                        HhLog.e("GET_CONTROL monitorId " + CommonData.videoDeleteMonitorId);
                        HhLog.e("GET_CONTROL channelId " + CommonData.videoDeleteChannelId);
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("GET_CONTROL e" + e.getMessage());
                    }
                });


        HhHttp.get()
                .url(URLConstant.GET_CONTROL_GB)
                .addParams("deviceId", CommonData.videoDeleteDeviceId)
                .addParams("serial", CommonData.videoDeleteDeviceIdSerial)
                .addParams("speed", stop==1?"1":speed+"")
                .addParams("controlType", stop==1?"stop":parseType(type))
                .build()
                .execute(new LoggedInStringCallback(null,FullVideoActivity.this) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_CONTROL_GB " + URLConstant.GET_CONTROL_GB
                                + "?deviceId=" +CommonData.videoDeleteDeviceId
                                +"&serial=" +CommonData.videoDeleteDeviceIdSerial
                                +"&speed=" +(stop==1?"1":speed+"")
                                +"&controlType=" +(stop==1?"stop":parseType(type))
                        );
                        HhLog.e("GET_CONTROL_GB " + response);
                        HhLog.e("GET_CONTROL_GB monitorId " + CommonData.videoDeleteDeviceId);
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("GET_CONTROL e" + e.getMessage());
                    }
                });

    }

    private String parseType(int type) {
        String str = "left";
        if(type == 21){
            str = "up";
        }
        if(type == 26){
            str = "upright";
        }
        if(type == 24){
            str = "right";
        }
        if(type == 28){
            str = "downright";
        }
        if(type == 22){
            str = "down";
        }
        if(type == 27){
            str = "downleft";
        }
        if(type == 23){
            str = "left";
        }
        if(type == 25){
            str = "upleft";
        }
        if(type == 11){
            str = "zoomin";
        }
        if(type == 12){
            str = "zoomout";
        }
        return str;
    }
}