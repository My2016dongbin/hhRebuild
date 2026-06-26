package com.haohai.platform.fireforestplatform.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.haohai.platform.fireforestplatform.R;

public class VideoPlayerControllerView extends FrameLayout {

    private ImageView ivPlayAction;
    private ImageView ivFullScreenAction;

    public VideoPlayerControllerView(Context context) {
        this(context, null);
    }

    public VideoPlayerControllerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayerControllerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_video_player_controller, this, true);
        ivPlayAction = findViewById(R.id.iv_play_action);
        ivFullScreenAction = findViewById(R.id.iv_full_screen_action);
    }

    public void setPlaying(boolean playing) {
        ivPlayAction.setImageResource(playing ? R.drawable.ic_video_pause : R.drawable.ic_video_play);
    }

    public void setFullScreenMode(boolean fullScreenMode) {
        ivFullScreenAction.setImageResource(fullScreenMode ? R.drawable.ic_video_fullscreen_exit : R.drawable.ic_video_fullscreen);
    }

    public void setOnPlayClickListener(@Nullable OnClickListener listener) {
        ivPlayAction.setOnClickListener(listener);
    }

    public void setOnFullScreenClickListener(@Nullable OnClickListener listener) {
        ivFullScreenAction.setOnClickListener(listener);
    }

    public void setActionEnabled(boolean enabled) {
        ivPlayAction.setEnabled(enabled);
        ivFullScreenAction.setEnabled(enabled);
        float alpha = enabled ? 1f : 0.4f;
        ivPlayAction.setAlpha(alpha);
        ivFullScreenAction.setAlpha(alpha);
    }

    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }
}
