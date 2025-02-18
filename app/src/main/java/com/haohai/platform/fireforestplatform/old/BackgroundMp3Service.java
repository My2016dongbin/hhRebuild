package com.haohai.platform.fireforestplatform.old;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;


public class BackgroundMp3Service extends Service {
    private static final String TAG = BackgroundMp3Service.class.getSimpleName();
    private String messageWeb;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "handleMessage: 播放完毕");
                    BackgroundMp3Service.this.onDestroy();
                    break;
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: 已创建啊");
    }

    private void startVoice() {
     //   Log.e(TAG, "onCreate:  messageWeb = " + messageWeb);
        Log.e(TAG, "startVoice: ");
        //接下来根据messageWeb分类通知语音播报

        /*//方案二：网络音频
        String stringExtra = "http://zjlt.sc.chinaz.com/Files/DownLoad/sound1/201511/6553.mp3";
        Uri parse = Uri.parse(stringExtra);*/

        try {
            /*//方案二：网络音频
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(BackgroundMp3Service.this, parse);
            mediaPlayer.prepare();// 进行缓冲
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });*/

            //方案一：本地音频
            Log.e(TAG, "startVoice: 开始播放" );
            Object voice_ = SPUtils.get(this, SPValue.voice,false);
            boolean voice = false;
            if(voice_!=null){
                voice = (boolean) voice_;
            }
            Log.e(TAG, "startVoice: voice " + voice );
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int current1 = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
            Log.e(TAG, "系统音量值：-" + current1);

            int current2 = am.getStreamVolume(AudioManager.STREAM_RING);
            Log.e(TAG, "系统铃声值：-" + current2);

            if (voice && current1!=0  && current2!=0){

                Log.e(TAG, "service  play "  );
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.find_fire);
                mediaPlayer.start();
                mHandler.sendEmptyMessageDelayed(1, 3000);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand:" );
        startVoice();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}