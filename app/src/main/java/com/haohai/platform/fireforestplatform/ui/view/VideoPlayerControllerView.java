package com.haohai.platform.fireforestplatform.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haohai.platform.fireforestplatform.R;

/**
 * Created by qc
 * on 2026/4/22.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class VideoPlayerControllerView extends FrameLayout {

    private View playView;
    private View fullScreenView;
    private View playIcon;
    private View fullScreenIcon;
    private boolean playing = true;
    private boolean fullScreenMode = false;
    private boolean actionEnabled = true;

    public VideoPlayerControllerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoPlayerControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_video_player_controller, this, true);
        playView = findViewById(R.id.video_controller_play);
        fullScreenView = findViewById(R.id.video_controller_full_screen);
        playIcon = findViewById(R.id.video_controller_play_icon);
        fullScreenIcon = findViewById(R.id.video_controller_full_screen_icon);
        updatePlayState();
        updateFullScreenState();
        updateActionState();
    }

    public void setOnPlayClickListener(OnClickListener onClickListener) {
        playView.setOnClickListener(onClickListener);
    }

    public void setOnFullScreenClickListener(OnClickListener onClickListener) {
        fullScreenView.setOnClickListener(onClickListener);
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        updatePlayState();
    }

    public void setFullScreenMode(boolean fullScreenMode) {
        this.fullScreenMode = fullScreenMode;
        updateFullScreenState();
    }

    public void setActionEnabled(boolean actionEnabled) {
        this.actionEnabled = actionEnabled;
        updateActionState();
    }

    public boolean isShowing() {
        return getVisibility() == View.VISIBLE;
    }

    private void updatePlayState() {
        if (playIcon == null) {
            return;
        }
        playIcon.setBackgroundResource(playing ? R.drawable.ic_video_pause : R.drawable.ic_video_play);
    }

    private void updateFullScreenState() {
        if (fullScreenIcon == null) {
            return;
        }
        fullScreenIcon.setBackgroundResource(fullScreenMode ? R.drawable.ic_video_fullscreen_exit : R.drawable.ic_video_fullscreen);
    }

    private void updateActionState() {
        if (playView == null || fullScreenView == null) {
            return;
        }
        playView.setEnabled(actionEnabled);
        fullScreenView.setEnabled(actionEnabled);
        float alpha = actionEnabled ? 1f : 0.4f;
        playView.setAlpha(alpha);
        fullScreenView.setAlpha(alpha);
    }
}
