package com.haohai.platform.fireforestplatform.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseFragment;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.FgVideo;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.event.VideoStream;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.bean.VideoDeleteModel;
import com.haohai.platform.fireforestplatform.ui.cell.VideoTreeDialog;
import com.haohai.platform.fireforestplatform.ui.viewmodel.DialogTreeViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FgVideoViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.Objects;

public class VideoFragment extends BaseFragment<FgVideo, FgVideoViewModel> implements DialogTreeViewModel.VideoTreeCallback, VideoTreeDialog.DialogListener {

    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private Media media;
    private IVLCVout ivlcVout;

    private LibVLC libVLC_Four1;
    private MediaPlayer mediaPlayer_Four1;
    private Media media_Four1;
    private IVLCVout ivlcVout_Four1;

    private LibVLC libVLC_Nine1;
    private MediaPlayer mediaPlayer_Nine1;
    private Media media_Nine1;
    private IVLCVout ivlcVout_Nine1;

    private LibVLC libVLC_Four2;
    private MediaPlayer mediaPlayer_Four2;
    private Media media_Four2;
    private IVLCVout ivlcVout_Four2;

    private LibVLC libVLC_Nine2;
    private MediaPlayer mediaPlayer_Nine2;
    private Media media_Nine2;
    private IVLCVout ivlcVout_Nine2;

    private LibVLC libVLC_Four3;
    private MediaPlayer mediaPlayer_Four3;
    private Media media_Four3;
    private IVLCVout ivlcVout_Four3;

    private LibVLC libVLC_Nine3;
    private MediaPlayer mediaPlayer_Nine3;
    private Media media_Nine3;
    private IVLCVout ivlcVout_Nine3;

    private LibVLC libVLC_Four4;
    private MediaPlayer mediaPlayer_Four4;
    private Media media_Four4;
    private IVLCVout ivlcVout_Four4;

    private LibVLC libVLC_Nine4;
    private MediaPlayer mediaPlayer_Nine4;
    private Media media_Nine4;
    private IVLCVout ivlcVout_Nine4;

    private LibVLC libVLC_Nine5;
    private MediaPlayer mediaPlayer_Nine5;
    private Media media_Nine5;
    private IVLCVout ivlcVout_Nine5;

    private LibVLC libVLC_Nine6;
    private MediaPlayer mediaPlayer_Nine6;
    private Media media_Nine6;
    private IVLCVout ivlcVout_Nine6;

    private LibVLC libVLC_Nine7;
    private MediaPlayer mediaPlayer_Nine7;
    private Media media_Nine7;
    private IVLCVout ivlcVout_Nine7;

    private LibVLC libVLC_Nine8;
    private MediaPlayer mediaPlayer_Nine8;
    private Media media_Nine8;
    private IVLCVout ivlcVout_Nine8;

    private LibVLC libVLC_Nine9;
    private MediaPlayer mediaPlayer_Nine9;
    private Media media_Nine9;
    private IVLCVout ivlcVout_Nine9;


    private OrientationUtils orientationUtils;
    private VideoTreeDialog treeDialog;

    public static VideoFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        init_();
        click_();
        binding.sfVideoNine1.setZOrderOnTop(true);
        binding.sfVideoNine2.setZOrderOnTop(true);
        binding.sfVideoNine3.setZOrderOnTop(true);
        binding.sfVideoNine4.setZOrderOnTop(true);
        binding.sfVideoNine5.setZOrderOnTop(true);
        binding.sfVideoNine6.setZOrderOnTop(true);
        binding.sfVideoNine7.setZOrderOnTop(true);
        binding.sfVideoNine8.setZOrderOnTop(true);
        binding.sfVideoNine9.setZOrderOnTop(true);
        binding.sfVideoFour1.setZOrderOnTop(true);
        binding.sfVideoFour2.setZOrderOnTop(true);
        binding.sfVideoFour3.setZOrderOnTop(true);
        binding.sfVideoFour4.setZOrderOnTop(true);
        binding.sfVideoOne1.setZOrderOnTop(true);

        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void click_() {
        binding.videoMenu.setOnClickListener(v -> showTreeDialog());
        binding.videoAdd.setOnClickListener(v -> showTreeDialog());

        binding.sfVideoOne1.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 1;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusOne1.setVisibility(View.VISIBLE);
            binding.statusFour1.setVisibility(View.VISIBLE);
            binding.statusNine1.setVisibility(View.VISIBLE);
        });
        binding.sfVideoFour1.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 1;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusOne1.setVisibility(View.VISIBLE);
            binding.statusFour1.setVisibility(View.VISIBLE);
            binding.statusNine1.setVisibility(View.VISIBLE);
        });
        binding.sfVideoFour2.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 2;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusFour2.setVisibility(View.VISIBLE);
            binding.statusNine2.setVisibility(View.VISIBLE);
        });
        binding.sfVideoFour3.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 3;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusFour3.setVisibility(View.VISIBLE);
            binding.statusNine3.setVisibility(View.VISIBLE);
        });
        binding.sfVideoFour4.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 4;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusFour4.setVisibility(View.VISIBLE);
            binding.statusNine4.setVisibility(View.VISIBLE);
        });
        binding.sfVideoNine1.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 1;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusOne1.setVisibility(View.VISIBLE);
            binding.statusFour1.setVisibility(View.VISIBLE);
            binding.statusNine1.setVisibility(View.VISIBLE);
        });
        binding.sfVideoNine2.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 2;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusFour2.setVisibility(View.VISIBLE);
            binding.statusNine2.setVisibility(View.VISIBLE);
        });
        binding.sfVideoNine3.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 3;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusFour3.setVisibility(View.VISIBLE);
            binding.statusNine3.setVisibility(View.VISIBLE);
        });
        binding.sfVideoNine4.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 4;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusFour4.setVisibility(View.VISIBLE);
            binding.statusNine4.setVisibility(View.VISIBLE);
        });
        binding.sfVideoNine5.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 5;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusNine5.setVisibility(View.VISIBLE);
        });
        binding.sfVideoNine6.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 6;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusNine6.setVisibility(View.VISIBLE);
        });
        binding.sfVideoNine7.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 7;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusNine7.setVisibility(View.VISIBLE);
        });
        binding.sfVideoNine8.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 8;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusNine8.setVisibility(View.VISIBLE);
        });
        binding.sfVideoNine9.setOnClickListener(v -> {
            CommonData.videoDeleteIndex = 9;
            parseVideoDeleteChanged(CommonData.videoDeleteIndex);
            hideStatus();
            binding.statusNine9.setVisibility(View.VISIBLE);
        });

        binding.viewZoomIn.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(11, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(11, 1, 1);
            }
            return false;
        });
        binding.viewZoomOut.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(12, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(12, 1, 1);
            }
            return false;
        });
        binding.viewUpLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(25, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(25, 1, 1);
            }
            return false;
        });
        binding.viewUp.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(21, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(21, 1, 1);
            }
            return false;
        });
        binding.viewUpRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(26, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(26, 1, 1);
            }
            return false;
        });
        binding.viewRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(24, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(24, 1, 1);
            }
            return false;
        });
        binding.viewDownRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(28, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(28, 1, 1);
            }
            return false;
        });
        binding.viewDown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(22, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(22, 1, 1);
            }
            return false;
        });
        binding.viewDownLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(27, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(27, 1, 1);
            }
            return false;
        });
        binding.viewLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                postMove(23, 5, 0);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                postMove(23, 1, 1);
            }
            return false;
        });
    }

    private void parseVideoDeleteChanged(int changedIndex) {
        for (int i = 0; i < CommonData.videoDeleteModelList.size(); i++) {
            VideoDeleteModel model = CommonData.videoDeleteModelList.get(i);
            if (changedIndex == model.getIndex()) {
                CommonData.videoDeleteMonitorId = model.getMonitorId();
                CommonData.videoDeleteChannelId = model.getChannelId();
                return;
            }
        }
    }

    private void postMove(int type, int speed, int stop) {
        if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.VIDEO_CONTROL)){
            Toast.makeText(requireActivity(), "当前账号没有控制权限", Toast.LENGTH_SHORT).show();
            return;
        }
        obtainViewModel().postMove(type, speed, stop);
    }

    private void hideStatus() {
        binding.statusOne1.setVisibility(View.GONE);
        binding.statusFour1.setVisibility(View.GONE);
        binding.statusFour2.setVisibility(View.GONE);
        binding.statusFour3.setVisibility(View.GONE);
        binding.statusFour4.setVisibility(View.GONE);
        binding.statusNine1.setVisibility(View.GONE);
        binding.statusNine2.setVisibility(View.GONE);
        binding.statusNine3.setVisibility(View.GONE);
        binding.statusNine4.setVisibility(View.GONE);
        binding.statusNine5.setVisibility(View.GONE);
        binding.statusNine6.setVisibility(View.GONE);
        binding.statusNine7.setVisibility(View.GONE);
        binding.statusNine8.setVisibility(View.GONE);
        binding.statusNine9.setVisibility(View.GONE);
    }

    private void showTreeDialog() {
        CommonData.videoAddingIndex = 1;
        treeDialog.show();
    }

    @Override
    protected void setupViewModel() {
        binding.setLifecycleOwner(this);
        binding.setFragmentModel(obtainViewModel());
        obtainViewModel().start(getContext());
    }

    @Override
    public int bindLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    public FgVideoViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(FgVideoViewModel.class);
    }


    private void init_() {
        initTreeDialog();
        initBindingListener();
    }

    private String url1 = "";
    private String url2 = "";
    private String url3 = "";
    private String url4 = "";
    private String url5 = "";
    private String url6 = "";
    private String url7 = "";
    private String url8 = "";
    private String url9 = "";

    ///视频流播放
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(VideoStream event) {
        //initGSY();
        HhLog.e("VideoStream " + event.getUrl());
        CommonData.videoPlayingIndexList.add(event.getIndex());
        if (event.getIndex() > 0) {
            obtainViewModel().loading.postValue(new LoadingEvent(true, "加载中"));
            //parseProgress(event.getIndex());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    obtainViewModel().loading.postValue(new LoadingEvent(false));
                    //binding.sfBack.setVisibility(View.GONE);
                }
            }, 6000);
        }
        if (event.getIndex() == 1) {
            url1 = event.getUrl();
            if(obtainViewModel().viewCount.getValue() == 1){
                releaseVLC_ONE();
                binding.sfBack.setVisibility(View.VISIBLE);
                startPlayer();
                binding.addOne1.setVisibility(View.GONE);
            }
            if(obtainViewModel().viewCount.getValue() == 4){
                releaseVLC_FOUR();
                binding.sfBackFour1.setVisibility(View.VISIBLE);
                startPlayerFour1();
                binding.addFour1.setVisibility(View.GONE);
            }
            if(obtainViewModel().viewCount.getValue() == 9){
                releaseVLC_NINE();
                binding.sfBackNine1.setVisibility(View.VISIBLE);
                startPlayerNine1();
                binding.addNine1.setVisibility(View.GONE);
            }
        }
        if (event.getIndex() == 2) {
            url2 = event.getUrl();
            if(obtainViewModel().viewCount.getValue() == 4){
                releaseVLC_FOUR();
                binding.sfBackFour2.setVisibility(View.VISIBLE);
                startPlayerFour2();
                binding.addFour2.setVisibility(View.GONE);
            }
            if(obtainViewModel().viewCount.getValue() == 9){
                releaseVLC_NINE();
                binding.sfBackNine2.setVisibility(View.VISIBLE);
                startPlayerNine2();
                binding.addNine2.setVisibility(View.GONE);
            }
        }
        if (event.getIndex() == 3) {
            url3 = event.getUrl();
            if(obtainViewModel().viewCount.getValue() == 4){
                releaseVLC_FOUR();
                binding.sfBackFour3.setVisibility(View.VISIBLE);
                startPlayerFour3();
                binding.addFour3.setVisibility(View.GONE);
            }
            if(obtainViewModel().viewCount.getValue() == 9){
                releaseVLC_NINE();
                binding.sfBackNine3.setVisibility(View.VISIBLE);
                startPlayerNine3();
                binding.addNine3.setVisibility(View.GONE);
            }
        }
        if (event.getIndex() == 4) {
            url4 = event.getUrl();
            if(obtainViewModel().viewCount.getValue() == 4){
                releaseVLC_FOUR();
                binding.sfBackFour4.setVisibility(View.VISIBLE);
                startPlayerFour4();
                binding.addFour4.setVisibility(View.GONE);
            }
            if(obtainViewModel().viewCount.getValue() == 9){
                releaseVLC_NINE();
                binding.sfBackNine4.setVisibility(View.VISIBLE);
                startPlayerNine4();
                binding.addNine4.setVisibility(View.GONE);
            }
        }
        if (event.getIndex() == 5) {
            url5 = event.getUrl();

            releaseVLC_NINE();
            binding.sfBackNine5.setVisibility(View.VISIBLE);
            startPlayerNine5();
            binding.addNine5.setVisibility(View.GONE);
        }
        if (event.getIndex() == 6) {
            url6 = event.getUrl();

            releaseVLC_NINE();
            binding.sfBackNine6.setVisibility(View.VISIBLE);
            startPlayerNine6();
            binding.addNine6.setVisibility(View.GONE);
        }
        if (event.getIndex() == 7) {
            url7 = event.getUrl();

            releaseVLC_NINE();
            binding.sfBackNine7.setVisibility(View.VISIBLE);
            startPlayerNine7();
            binding.addNine7.setVisibility(View.GONE);
        }
        if (event.getIndex() == 8) {
            url8 = event.getUrl();

            releaseVLC_NINE();
            binding.sfBackNine8.setVisibility(View.VISIBLE);
            startPlayerNine8();
            binding.addNine8.setVisibility(View.GONE);
        }
        if (event.getIndex() == 9) {
            url9 = event.getUrl();

            releaseVLC_NINE();
            binding.sfBackNine9.setVisibility(View.VISIBLE);
            startPlayerNine9();
            binding.addNine9.setVisibility(View.GONE);
        }
    }

    ///Tab切换
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MainTabChange event) {
        int index = event.getIndex();
        if (index == 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(CommonData.videoPlayingIndexList == null || CommonData.videoPlayingIndexList.isEmpty()){
                        binding.sfBackAll.setVisibility(View.GONE);
                    }
                    onViewCountChanged();
                    showSurfaceView();
                }
            },500);

        } else {
            binding.sfBackAll.setVisibility(View.VISIBLE);
            hideSurfaceView();
            releaseVLC();
        }
    }

    private void showSurfaceView() {
        binding.sfVideoOne1.setVisibility(View.VISIBLE);
        binding.sfVideoFour1.setVisibility(View.VISIBLE);
        binding.sfVideoFour2.setVisibility(View.VISIBLE);
        binding.sfVideoFour3.setVisibility(View.VISIBLE);
        binding.sfVideoFour4.setVisibility(View.VISIBLE);
        binding.sfVideoNine1.setVisibility(View.VISIBLE);
        binding.sfVideoNine2.setVisibility(View.VISIBLE);
        binding.sfVideoNine3.setVisibility(View.VISIBLE);
        binding.sfVideoNine4.setVisibility(View.VISIBLE);
        binding.sfVideoNine5.setVisibility(View.VISIBLE);
        binding.sfVideoNine6.setVisibility(View.VISIBLE);
        binding.sfVideoNine7.setVisibility(View.VISIBLE);
        binding.sfVideoNine8.setVisibility(View.VISIBLE);
        binding.sfVideoNine9.setVisibility(View.VISIBLE);
    }

    private void hideSurfaceView() {
        binding.sfVideoOne1.setVisibility(View.GONE);
        binding.sfVideoFour1.setVisibility(View.GONE);
        binding.sfVideoFour2.setVisibility(View.GONE);
        binding.sfVideoFour3.setVisibility(View.GONE);
        binding.sfVideoFour4.setVisibility(View.GONE);
        binding.sfVideoNine1.setVisibility(View.GONE);
        binding.sfVideoNine2.setVisibility(View.GONE);
        binding.sfVideoNine3.setVisibility(View.GONE);
        binding.sfVideoNine4.setVisibility(View.GONE);
        binding.sfVideoNine5.setVisibility(View.GONE);
        binding.sfVideoNine6.setVisibility(View.GONE);
        binding.sfVideoNine7.setVisibility(View.GONE);
        binding.sfVideoNine8.setVisibility(View.GONE);
        binding.sfVideoNine9.setVisibility(View.GONE);
    }

    private void initBindingListener() {
        //监控数量切换
        obtainViewModel().viewCount.observe(requireActivity(), integer -> onViewCountChanged());
        //监控添加
        obtainViewModel().viewNum.observe(requireActivity(), this::onViewNumChanged);
        //控制事件
        obtainViewModel().turnType.observe(requireActivity(), this::onTurnChanged);
        //事件
        obtainViewModel().event.observe(requireActivity(), this::onEventChanged);
    }

    private void initTreeDialog() {
        treeDialog = new VideoTreeDialog(requireActivity(), R.style.ActionSheetDialogStyleLeft, this);
        Window videoListDialogWindow = treeDialog.getWindow();
        videoListDialogWindow.setGravity(Gravity.START);
        treeDialog.setDialogListener(this);
        WindowManager.LayoutParams lpVideo = videoListDialogWindow.getAttributes();
        WindowManager wm = (WindowManager) requireActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lpVideo.width = (int) (width * 0.85);
        lpVideo.height = height;
        videoListDialogWindow.setAttributes(lpVideo);
        treeDialog.setCanceledOnTouchOutside(true);
    }

    //分别点击添加
    private void onViewNumChanged(int num) {
        if (num > 0) {
            CommonData.videoAddingIndex = num;
            treeDialog.show();
        }
    }

    //分别控制事件
    private void onTurnChanged(int type) {
        /*if(!CommonPermission.hasPermission(requireActivity(),CommonPermission.VIDEO_CONTROL)){
            Toast.makeText(requireActivity(), "当前账号没有控制权限", Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (type == 106) {
            HhLog.e("delete -");
            //移除
            try {
                if (CommonData.videoDeleteIndex == 1) {
                    url1 = "";
                    if (obtainViewModel().viewCount.getValue() == 1) {
                        releasePlayer();
                    }
                    if (obtainViewModel().viewCount.getValue() == 4) {
                        releasePlayer_Four1();
                    }
                    if (obtainViewModel().viewCount.getValue() == 9) {
                        releasePlayer_Nine1();
                    }
                    hideStatus();
                    binding.addOne1.setVisibility(View.VISIBLE);
                    binding.addFour1.setVisibility(View.VISIBLE);
                    binding.addNine1.setVisibility(View.VISIBLE);
                    binding.sfVideoOne1.setVisibility(View.GONE);
                    binding.sfVideoFour1.setVisibility(View.GONE);
                    binding.sfVideoNine1.setVisibility(View.GONE);
                }
                if (CommonData.videoDeleteIndex == 2) {
                    url2 = "";
                    if (obtainViewModel().viewCount.getValue() == 4) {
                        releasePlayer_Four2();
                    }
                    if (obtainViewModel().viewCount.getValue() == 9) {
                        releasePlayer_Nine2();
                    }
                    hideStatus();
                    binding.addFour2.setVisibility(View.VISIBLE);
                    binding.addNine2.setVisibility(View.VISIBLE);
                    binding.sfVideoFour2.setVisibility(View.GONE);
                    binding.sfVideoNine2.setVisibility(View.GONE);
                }
                if (CommonData.videoDeleteIndex == 3) {
                    url3 = "";
                    if (obtainViewModel().viewCount.getValue() == 4) {
                        releasePlayer_Four3();
                    }
                    if (obtainViewModel().viewCount.getValue() == 9) {
                        releasePlayer_Nine3();
                    }
                    hideStatus();
                    binding.addFour3.setVisibility(View.VISIBLE);
                    binding.addNine3.setVisibility(View.VISIBLE);
                    binding.sfVideoFour3.setVisibility(View.GONE);
                    binding.sfVideoNine3.setVisibility(View.GONE);
                }
                if (CommonData.videoDeleteIndex == 4) {
                    url4 = "";
                    if (obtainViewModel().viewCount.getValue() == 4) {
                        releasePlayer_Four4();
                    }
                    if (obtainViewModel().viewCount.getValue() == 9) {
                        releasePlayer_Nine4();
                    }
                    hideStatus();
                    binding.addFour4.setVisibility(View.VISIBLE);
                    binding.addNine4.setVisibility(View.VISIBLE);
                    binding.sfVideoFour4.setVisibility(View.GONE);
                    binding.sfVideoNine4.setVisibility(View.GONE);
                }
                if (CommonData.videoDeleteIndex == 5) {
                    url5 = "";
                    releasePlayer_Nine5();
                    binding.addNine5.setVisibility(View.VISIBLE);
                    binding.sfVideoNine5.setVisibility(View.GONE);
                    hideStatus();
                }
                if (CommonData.videoDeleteIndex == 6) {
                    url6 = "";
                    releasePlayer_Nine6();
                    binding.addNine6.setVisibility(View.VISIBLE);
                    binding.sfVideoNine6.setVisibility(View.GONE);
                    hideStatus();
                }
                if (CommonData.videoDeleteIndex == 7) {
                    url7 = "";
                    releasePlayer_Nine7();
                    binding.addNine7.setVisibility(View.VISIBLE);
                    binding.sfVideoNine7.setVisibility(View.GONE);
                    hideStatus();
                }
                if (CommonData.videoDeleteIndex == 8) {
                    url8 = "";
                    releasePlayer_Nine8();
                    binding.addNine8.setVisibility(View.VISIBLE);
                    binding.sfVideoNine8.setVisibility(View.GONE);
                    hideStatus();
                }
                if (CommonData.videoDeleteIndex == 9) {
                    url9 = "";
                    releasePlayer_Nine9();
                    binding.addNine9.setVisibility(View.VISIBLE);
                    binding.sfVideoNine9.setVisibility(View.GONE);
                    hideStatus();
                }
                CommonData.videoPlayingIndexList.remove(Integer.valueOf(CommonData.videoDeleteIndex));
            } catch (Exception e) {
                HhLog.e("error " + e.getMessage());
            }
        } else if (type != 0) {
            //单击暂时未用
        }
    }

    private void onEventChanged(int tag) {

    }

    private void onViewCountChanged() {
        releaseVLC();
        if (CommonData.videoPlayingIndexList != null && !CommonData.videoPlayingIndexList.isEmpty()) {
            binding.sfBackAll.setVisibility(View.VISIBLE);
            obtainViewModel().loading.postValue(new LoadingEvent(true, "加载中"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.sfBackAll.setVisibility(View.GONE);
                    obtainViewModel().loading.postValue(new LoadingEvent(false));
                    //binding.sfBack.setVisibility(View.GONE);
                }
            }, 6000);
        }
        if (obtainViewModel().viewCount.getValue() == 1) {
            binding.imageOne.setImageResource(R.drawable.ic_one_selected);
            binding.imageFour.setImageResource(R.drawable.ic_four);
            binding.imageNine.setImageResource(R.drawable.ic_nine);
            binding.groupOne.setVisibility(View.VISIBLE);
            binding.groupFour.setVisibility(View.GONE);
            binding.groupNine.setVisibility(View.GONE);


            binding.sfVideoOne1.setVisibility(View.VISIBLE);
            binding.sfVideoFour1.setVisibility(View.GONE);
            binding.sfVideoFour2.setVisibility(View.GONE);
            binding.sfVideoFour3.setVisibility(View.GONE);
            binding.sfVideoFour4.setVisibility(View.GONE);
            binding.sfVideoNine1.setVisibility(View.GONE);
            binding.sfVideoNine2.setVisibility(View.GONE);
            binding.sfVideoNine3.setVisibility(View.GONE);
            binding.sfVideoNine4.setVisibility(View.GONE);
            binding.sfVideoNine5.setVisibility(View.GONE);
            binding.sfVideoNine6.setVisibility(View.GONE);
            binding.sfVideoNine7.setVisibility(View.GONE);
            binding.sfVideoNine8.setVisibility(View.GONE);
            binding.sfVideoNine9.setVisibility(View.GONE);
            if (url1 != null && !url1.isEmpty()) {
                binding.sfBack.setVisibility(View.VISIBLE);
                startPlayer();
                binding.addOne1.setVisibility(View.GONE);
            }
        }
        if (obtainViewModel().viewCount.getValue() == 4) {
            binding.imageOne.setImageResource(R.drawable.ic_one);
            binding.imageFour.setImageResource(R.drawable.ic_four_selected);
            binding.imageNine.setImageResource(R.drawable.ic_nine);
            binding.groupOne.setVisibility(View.GONE);
            binding.groupFour.setVisibility(View.VISIBLE);
            binding.groupNine.setVisibility(View.GONE);


            binding.sfVideoOne1.setVisibility(View.GONE);
            binding.sfVideoFour1.setVisibility(View.VISIBLE);
            binding.sfVideoFour2.setVisibility(View.VISIBLE);
            binding.sfVideoFour3.setVisibility(View.VISIBLE);
            binding.sfVideoFour4.setVisibility(View.VISIBLE);
            binding.sfVideoNine1.setVisibility(View.GONE);
            binding.sfVideoNine2.setVisibility(View.GONE);
            binding.sfVideoNine3.setVisibility(View.GONE);
            binding.sfVideoNine4.setVisibility(View.GONE);
            binding.sfVideoNine5.setVisibility(View.GONE);
            binding.sfVideoNine6.setVisibility(View.GONE);
            binding.sfVideoNine7.setVisibility(View.GONE);
            binding.sfVideoNine8.setVisibility(View.GONE);
            binding.sfVideoNine9.setVisibility(View.GONE);
            if (url1 != null && !url1.isEmpty()) {
                binding.sfBackFour1.setVisibility(View.VISIBLE);
                startPlayerFour1();
                binding.addFour1.setVisibility(View.GONE);
            }
            if (url2 != null && !url2.isEmpty()) {
                binding.sfBackFour2.setVisibility(View.VISIBLE);
                startPlayerFour2();
                binding.addFour2.setVisibility(View.GONE);
            }
            if (url3 != null && !url3.isEmpty()) {
                binding.sfBackFour3.setVisibility(View.VISIBLE);
                startPlayerFour3();
                binding.addFour3.setVisibility(View.GONE);
            }
            if (url4 != null && !url4.isEmpty()) {
                binding.sfBackFour4.setVisibility(View.VISIBLE);
                startPlayerFour4();
                binding.addFour4.setVisibility(View.GONE);
            }
        }
        if (obtainViewModel().viewCount.getValue() == 9) {
            binding.imageOne.setImageResource(R.drawable.ic_one);
            binding.imageFour.setImageResource(R.drawable.ic_four);
            binding.imageNine.setImageResource(R.drawable.ic_nine_selected);
            binding.groupOne.setVisibility(View.GONE);
            binding.groupFour.setVisibility(View.GONE);
            binding.groupNine.setVisibility(View.VISIBLE);


            binding.sfVideoOne1.setVisibility(View.GONE);
            binding.sfVideoFour1.setVisibility(View.GONE);
            binding.sfVideoFour2.setVisibility(View.GONE);
            binding.sfVideoFour3.setVisibility(View.GONE);
            binding.sfVideoFour4.setVisibility(View.GONE);
            binding.sfVideoNine1.setVisibility(View.VISIBLE);
            binding.sfVideoNine2.setVisibility(View.VISIBLE);
            binding.sfVideoNine3.setVisibility(View.VISIBLE);
            binding.sfVideoNine4.setVisibility(View.VISIBLE);
            binding.sfVideoNine5.setVisibility(View.VISIBLE);
            binding.sfVideoNine6.setVisibility(View.VISIBLE);
            binding.sfVideoNine7.setVisibility(View.VISIBLE);
            binding.sfVideoNine8.setVisibility(View.VISIBLE);
            binding.sfVideoNine9.setVisibility(View.VISIBLE);

            if (url1 != null && !url1.isEmpty()) {
                binding.sfBackNine1.setVisibility(View.VISIBLE);
                startPlayerNine1();
                binding.addNine1.setVisibility(View.GONE);
            }
            if (url2 != null && !url2.isEmpty()) {
                binding.sfBackNine2.setVisibility(View.VISIBLE);
                startPlayerNine2();
                binding.addNine2.setVisibility(View.GONE);
            }
            if (url3 != null && !url3.isEmpty()) {
                binding.sfBackNine3.setVisibility(View.VISIBLE);
                startPlayerNine3();
                binding.addNine3.setVisibility(View.GONE);
            }
            if (url4 != null && !url4.isEmpty()) {
                binding.sfBackNine4.setVisibility(View.VISIBLE);
                startPlayerNine4();
                binding.addNine4.setVisibility(View.GONE);
            }
            if (url5 != null && !url5.isEmpty()) {
                binding.sfBackNine5.setVisibility(View.VISIBLE);
                startPlayerNine5();
                binding.addNine5.setVisibility(View.GONE);
            }
            if (url6 != null && !url6.isEmpty()) {
                binding.sfBackNine6.setVisibility(View.VISIBLE);
                startPlayerNine6();
                binding.addNine6.setVisibility(View.GONE);
            }
            if (url7 != null && !url7.isEmpty()) {
                binding.sfBackNine7.setVisibility(View.VISIBLE);
                startPlayerNine7();
                binding.addNine7.setVisibility(View.GONE);
            }
            if (url8 != null && !url8.isEmpty()) {
                binding.sfBackNine8.setVisibility(View.VISIBLE);
                startPlayerNine8();
                binding.addNine8.setVisibility(View.GONE);
            }
            if (url9 != null && !url9.isEmpty()) {
                binding.sfBackNine9.setVisibility(View.VISIBLE);
                startPlayerNine9();
                binding.addNine9.setVisibility(View.GONE);
            }
        }
    }

    private void initGSY() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(requireActivity(), binding.gsyPlayerOne1);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption//.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl("playUrl")
                .setCacheWithPlay(false)
                .setVideoTitle("")
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        //orientationUtils.setEnable(true);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
                        Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//当前非全屏player
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                }).setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        }).build(binding.gsyPlayerOne1);

        if ("playUrl".contains("rtsp")) {
            PlayerFactory.setPlayManager(SystemPlayerManager.class);//系统模式 rtsp
        } else {
            PlayerFactory.setPlayManager(IjkPlayerManager.class);//ijk模式 rtmp
        }

        binding.gsyPlayerOne1.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                binding.gsyPlayerOne1.startWindowFullscreen(requireActivity(), true, true);
            }
        });
        binding.gsyPlayerOne1.getFullscreenButton().setVisibility(View.GONE);
        binding.gsyPlayerOne1.getBackButton().setVisibility(View.GONE);
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

    void releasePlayer_Four1() {
        if (libVLC_Four1 == null || mediaPlayer_Four1 == null ||
                ivlcVout_Four1 == null || media_Four1 == null) {
            return;
        }
        mediaPlayer_Four1.stop();
        ivlcVout_Four1 = mediaPlayer_Four1.getVLCVout();
        ivlcVout_Four1.detachViews();
        mediaPlayer_Four1.release();
        libVLC_Four1.release();

        libVLC_Four1 = null;
        mediaPlayer_Four1 = null;
        ivlcVout_Four1 = null;
        media_Four1 = null;
    }

    void releasePlayer_Nine1() {
        if (libVLC_Nine1 == null || mediaPlayer_Nine1 == null ||
                ivlcVout_Nine1 == null || media_Nine1 == null) {
            return;
        }
        mediaPlayer_Nine1.stop();
        ivlcVout_Nine1 = mediaPlayer_Nine1.getVLCVout();
        ivlcVout_Nine1.detachViews();
        mediaPlayer_Nine1.release();
        libVLC_Nine1.release();

        libVLC_Nine1 = null;
        mediaPlayer_Nine1 = null;
        ivlcVout_Nine1 = null;
        media_Nine1 = null;
    }

    void releasePlayer_Four2() {
        if (libVLC_Four2 == null || mediaPlayer_Four2 == null ||
                ivlcVout_Four2 == null || media_Four2 == null) {
            return;
        }
        mediaPlayer_Four2.stop();
        ivlcVout_Four2 = mediaPlayer_Four2.getVLCVout();
        ivlcVout_Four2.detachViews();
        mediaPlayer_Four2.release();
        libVLC_Four2.release();

        libVLC_Four2 = null;
        mediaPlayer_Four2 = null;
        ivlcVout_Four2 = null;
        media_Four2 = null;
    }

    void releasePlayer_Nine2() {
        if (libVLC_Nine2 == null || mediaPlayer_Nine2 == null ||
                ivlcVout_Nine2 == null || media_Nine2 == null) {
            return;
        }
        mediaPlayer_Nine2.stop();
        ivlcVout_Nine2 = mediaPlayer_Nine2.getVLCVout();
        ivlcVout_Nine2.detachViews();
        mediaPlayer_Nine2.release();
        libVLC_Nine2.release();

        libVLC_Nine2 = null;
        mediaPlayer_Nine2 = null;
        ivlcVout_Nine2 = null;
        media_Nine2 = null;
    }

    void releasePlayer_Four3() {
        if (libVLC_Four3 == null || mediaPlayer_Four3 == null ||
                ivlcVout_Four3 == null || media_Four3 == null) {
            return;
        }
        mediaPlayer_Four3.stop();
        ivlcVout_Four3 = mediaPlayer_Four3.getVLCVout();
        ivlcVout_Four3.detachViews();
        mediaPlayer_Four3.release();
        libVLC_Four3.release();

        libVLC_Four3 = null;
        mediaPlayer_Four3 = null;
        ivlcVout_Four3 = null;
        media_Four3 = null;
    }

    void releasePlayer_Nine3() {
        if (libVLC_Nine3 == null || mediaPlayer_Nine3 == null ||
                ivlcVout_Nine3 == null || media_Nine3 == null) {
            return;
        }
        mediaPlayer_Nine3.stop();
        ivlcVout_Nine3 = mediaPlayer_Nine3.getVLCVout();
        ivlcVout_Nine3.detachViews();
        mediaPlayer_Nine3.release();
        libVLC_Nine3.release();

        libVLC_Nine3 = null;
        mediaPlayer_Nine3 = null;
        ivlcVout_Nine3 = null;
        media_Nine3 = null;
    }

    void releasePlayer_Four4() {
        if (libVLC_Four4 == null || mediaPlayer_Four4 == null ||
                ivlcVout_Four4 == null || media_Four4 == null) {
            return;
        }
        mediaPlayer_Four4.stop();
        ivlcVout_Four4 = mediaPlayer_Four4.getVLCVout();
        ivlcVout_Four4.detachViews();
        mediaPlayer_Four4.release();
        libVLC_Four4.release();

        libVLC_Four4 = null;
        mediaPlayer_Four4 = null;
        ivlcVout_Four4 = null;
        media_Four4 = null;
    }

    void releasePlayer_Nine4() {
        if (libVLC_Nine4 == null || mediaPlayer_Nine4 == null ||
                ivlcVout_Nine4 == null || media_Nine4 == null) {
            return;
        }
        mediaPlayer_Nine4.stop();
        ivlcVout_Nine4 = mediaPlayer_Nine4.getVLCVout();
        ivlcVout_Nine4.detachViews();
        mediaPlayer_Nine4.release();
        libVLC_Nine4.release();

        libVLC_Nine4 = null;
        mediaPlayer_Nine4 = null;
        ivlcVout_Nine4 = null;
        media_Nine4 = null;
    }

    void releasePlayer_Nine5() {
        if (libVLC_Nine5 == null || mediaPlayer_Nine5 == null ||
                ivlcVout_Nine5 == null || media_Nine5 == null) {
            return;
        }
        mediaPlayer_Nine5.stop();
        ivlcVout_Nine5 = mediaPlayer_Nine5.getVLCVout();
        ivlcVout_Nine5.detachViews();
        mediaPlayer_Nine5.release();
        libVLC_Nine5.release();

        libVLC_Nine5 = null;
        mediaPlayer_Nine5 = null;
        ivlcVout_Nine5 = null;
        media_Nine5 = null;
    }

    void releasePlayer_Nine6() {
        if (libVLC_Nine6 == null || mediaPlayer_Nine6 == null ||
                ivlcVout_Nine6 == null || media_Nine6 == null) {
            return;
        }
        mediaPlayer_Nine6.stop();
        ivlcVout_Nine6 = mediaPlayer_Nine6.getVLCVout();
        ivlcVout_Nine6.detachViews();
        mediaPlayer_Nine6.release();
        libVLC_Nine6.release();

        libVLC_Nine6 = null;
        mediaPlayer_Nine6 = null;
        ivlcVout_Nine6 = null;
        media_Nine6 = null;
    }

    void releasePlayer_Nine7() {
        if (libVLC_Nine7 == null || mediaPlayer_Nine7 == null ||
                ivlcVout_Nine7 == null || media_Nine7 == null) {
            return;
        }
        mediaPlayer_Nine7.stop();
        ivlcVout_Nine7 = mediaPlayer_Nine7.getVLCVout();
        ivlcVout_Nine7.detachViews();
        mediaPlayer_Nine7.release();
        libVLC_Nine7.release();

        libVLC_Nine7 = null;
        mediaPlayer_Nine7 = null;
        ivlcVout_Nine7 = null;
        media_Nine7 = null;
    }

    void releasePlayer_Nine8() {
        if (libVLC_Nine8 == null || mediaPlayer_Nine8 == null ||
                ivlcVout_Nine8 == null || media_Nine8 == null) {
            return;
        }
        mediaPlayer_Nine8.stop();
        ivlcVout_Nine8 = mediaPlayer_Nine8.getVLCVout();
        ivlcVout_Nine8.detachViews();
        mediaPlayer_Nine8.release();
        libVLC_Nine8.release();

        libVLC_Nine8 = null;
        mediaPlayer_Nine8 = null;
        ivlcVout_Nine8 = null;
        media_Nine8 = null;
    }

    void releasePlayer_Nine9() {
        if (libVLC_Nine9 == null || mediaPlayer_Nine9 == null ||
                ivlcVout_Nine9 == null || media_Nine9 == null) {
            return;
        }
        mediaPlayer_Nine9.stop();
        ivlcVout_Nine9 = mediaPlayer_Nine9.getVLCVout();
        ivlcVout_Nine9.detachViews();
        mediaPlayer_Nine9.release();
        libVLC_Nine9.release();

        libVLC_Nine9 = null;
        mediaPlayer_Nine9 = null;
        ivlcVout_Nine9 = null;
        media_Nine9 = null;
    }

    void startPlayer() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC = new LibVLC(requireActivity(), options);
        mediaPlayer = new MediaPlayer(libVLC);
        //设置vlc视频铺满布局
        mediaPlayer.setScale(0f);

        mediaPlayer.getVLCVout().setWindowSize(width, (int) (width * 1));//宽，高  播放窗口的大小
        mediaPlayer.setAspectRatio("${" + width + "}:${" + (int) (width * 1) + "}");//宽，高  画面大小
        mediaPlayer.setVolume(0);
        ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.setVideoView(binding.sfVideoOne1);
        ivlcVout.attachViews();

        media = new Media(libVLC, Uri.parse(url1));
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
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoOne1.setVisibility(View.VISIBLE);
                                    binding.sfBack.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
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

    void startPlayerFour1() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Four1();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Four1 = new LibVLC(requireActivity(), options);
        mediaPlayer_Four1 = new MediaPlayer(libVLC_Four1);
        //设置vlc视频铺满布局
        mediaPlayer_Four1.setScale(0f);

        mediaPlayer_Four1.getVLCVout().setWindowSize((int) (width * 0.5), (int) (width * 0.5));//宽，高  播放窗口的大小
        mediaPlayer_Four1.setAspectRatio("${" + (int) (width * 0.5) + "}:${" + (int) (width * 0.5) + "}");//宽，高  画面大小

        mediaPlayer_Four1.setVolume(0);
        ivlcVout_Four1 = mediaPlayer_Four1.getVLCVout();
        ivlcVout_Four1.setVideoView(binding.sfVideoFour1);
        ivlcVout_Four1.attachViews();

        media_Four1 = new Media(libVLC_Four1, Uri.parse(url1));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Four1.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Four1.addOption(":network-caching=" + cache);
        media_Four1.addOption(":file-caching=" + cache);
        media_Four1.addOption(":live-cacheing=" + cache);
        media_Four1.addOption(":sout-mux-caching=" + cache);
        media_Four1.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Four1.setMedia(media_Four1);
        mediaPlayer_Four1.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerFour1();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerFour1();
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
                        HhLog.e("Vout41");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoFour1.setVisibility(View.VISIBLE);
                                    binding.sfBackFour1.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Four1.play();
    }

    void startPlayerNine1() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Nine1();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Nine1 = new LibVLC(requireActivity(), options);
        mediaPlayer_Nine1 = new MediaPlayer(libVLC_Nine1);
        //设置vlc视频铺满布局
        mediaPlayer_Nine1.setScale(0f);

        mediaPlayer_Nine1.getVLCVout().setWindowSize((int) (width * 0.33), (int) (width * 0.33));//宽，高  播放窗口的大小
        mediaPlayer_Nine1.setAspectRatio("${" + (int) (width * 0.33) + "}:${" + (int) (width * 0.33) + "}");//宽，高  画面大小

        mediaPlayer_Nine1.setVolume(0);
        ivlcVout_Nine1 = mediaPlayer_Nine1.getVLCVout();
        ivlcVout_Nine1.setVideoView(binding.sfVideoNine1);
        ivlcVout_Nine1.attachViews();

        media_Nine1 = new Media(libVLC_Nine1, Uri.parse(url1));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Nine1.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Nine1.addOption(":network-caching=" + cache);
        media_Nine1.addOption(":file-caching=" + cache);
        media_Nine1.addOption(":live-cacheing=" + cache);
        media_Nine1.addOption(":sout-mux-caching=" + cache);
        media_Nine1.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Nine1.setMedia(media_Nine1);
        mediaPlayer_Nine1.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerNine1();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerNine1();
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
                        HhLog.e("Vout91");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoNine1.setVisibility(View.VISIBLE);
                                    binding.sfBackNine1.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Nine1.play();
    }

    void startPlayerFour2() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Four2();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Four2 = new LibVLC(requireActivity(), options);
        mediaPlayer_Four2 = new MediaPlayer(libVLC_Four2);
        //设置vlc视频铺满布局
        mediaPlayer_Four2.setScale(0f);

        mediaPlayer_Four2.getVLCVout().setWindowSize((int) (width * 0.5), (int) (width * 0.5));//宽，高  播放窗口的大小
        mediaPlayer_Four2.setAspectRatio("${" + (int) (width * 0.5) + "}:${" + (int) (width * 0.5) + "}");//宽，高  画面大小

        mediaPlayer_Four2.setVolume(0);
        ivlcVout_Four2 = mediaPlayer_Four2.getVLCVout();
        ivlcVout_Four2.setVideoView(binding.sfVideoFour2);
        ivlcVout_Four2.attachViews();

        media_Four2 = new Media(libVLC_Four2, Uri.parse(url2));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Four2.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Four2.addOption(":network-caching=" + cache);
        media_Four2.addOption(":file-caching=" + cache);
        media_Four2.addOption(":live-cacheing=" + cache);
        media_Four2.addOption(":sout-mux-caching=" + cache);
        media_Four2.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Four2.setMedia(media_Four2);
        mediaPlayer_Four2.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerFour2();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerFour2();
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
                        HhLog.e("Vout42");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoFour2.setVisibility(View.VISIBLE);
                                    binding.sfBackFour2.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Four2.play();
    }

    void startPlayerNine2() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Nine2();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Nine2 = new LibVLC(requireActivity(), options);
        mediaPlayer_Nine2 = new MediaPlayer(libVLC_Nine2);
        //设置vlc视频铺满布局
        mediaPlayer_Nine2.setScale(0f);

        mediaPlayer_Nine2.getVLCVout().setWindowSize((int) (width * 0.33), (int) (width * 0.33));//宽，高  播放窗口的大小
        mediaPlayer_Nine2.setAspectRatio("${" + (int) (width * 0.33) + "}:${" + (int) (width * 0.33) + "}");//宽，高  画面大小

        mediaPlayer_Nine2.setVolume(0);
        ivlcVout_Nine2 = mediaPlayer_Nine2.getVLCVout();
        ivlcVout_Nine2.setVideoView(binding.sfVideoNine2);
        ivlcVout_Nine2.attachViews();

        media_Nine2 = new Media(libVLC_Nine2, Uri.parse(url2));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Nine2.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Nine2.addOption(":network-caching=" + cache);
        media_Nine2.addOption(":file-caching=" + cache);
        media_Nine2.addOption(":live-cacheing=" + cache);
        media_Nine2.addOption(":sout-mux-caching=" + cache);
        media_Nine2.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Nine2.setMedia(media_Nine2);
        mediaPlayer_Nine2.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerNine2();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerNine2();
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
                        HhLog.e("Vout92");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoNine2.setVisibility(View.VISIBLE);
                                    binding.sfBackNine2.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Nine2.play();
    }

    void startPlayerFour3() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Four3();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Four3 = new LibVLC(requireActivity(), options);
        mediaPlayer_Four3 = new MediaPlayer(libVLC_Four3);
        //设置vlc视频铺满布局
        mediaPlayer_Four3.setScale(0f);

        mediaPlayer_Four3.getVLCVout().setWindowSize((int) (width * 0.5), (int) (width * 0.5));//宽，高  播放窗口的大小
        mediaPlayer_Four3.setAspectRatio("${" + (int) (width * 0.5) + "}:${" + (int) (width * 0.5) + "}");//宽，高  画面大小

        mediaPlayer_Four3.setVolume(0);
        ivlcVout_Four3 = mediaPlayer_Four3.getVLCVout();
        ivlcVout_Four3.setVideoView(binding.sfVideoFour3);
        ivlcVout_Four3.attachViews();

        media_Four3 = new Media(libVLC_Four3, Uri.parse(url3));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Four3.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Four3.addOption(":network-caching=" + cache);
        media_Four3.addOption(":file-caching=" + cache);
        media_Four3.addOption(":live-cacheing=" + cache);
        media_Four3.addOption(":sout-mux-caching=" + cache);
        media_Four3.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Four3.setMedia(media_Four3);
        mediaPlayer_Four3.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerFour3();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerFour3();
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
                        HhLog.e("Vout43");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoFour3.setVisibility(View.VISIBLE);
                                    binding.sfBackFour3.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Four3.play();
    }

    void startPlayerNine3() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Nine3();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Nine3 = new LibVLC(requireActivity(), options);
        mediaPlayer_Nine3 = new MediaPlayer(libVLC_Nine3);
        //设置vlc视频铺满布局
        mediaPlayer_Nine3.setScale(0f);

        mediaPlayer_Nine3.getVLCVout().setWindowSize((int) (width * 0.33), (int) (width * 0.33));//宽，高  播放窗口的大小
        mediaPlayer_Nine3.setAspectRatio("${" + (int) (width * 0.33) + "}:${" + (int) (width * 0.33) + "}");//宽，高  画面大小

        mediaPlayer_Nine3.setVolume(0);
        ivlcVout_Nine3 = mediaPlayer_Nine3.getVLCVout();
        ivlcVout_Nine3.setVideoView(binding.sfVideoNine3);
        ivlcVout_Nine3.attachViews();

        media_Nine3 = new Media(libVLC_Nine3, Uri.parse(url3));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Nine3.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Nine3.addOption(":network-caching=" + cache);
        media_Nine3.addOption(":file-caching=" + cache);
        media_Nine3.addOption(":live-cacheing=" + cache);
        media_Nine3.addOption(":sout-mux-caching=" + cache);
        media_Nine3.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Nine3.setMedia(media_Nine3);
        mediaPlayer_Nine3.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerNine3();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerNine3();
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
                        HhLog.e("Vout93");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoNine3.setVisibility(View.VISIBLE);
                                    binding.sfBackNine3.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Nine3.play();
    }

    void startPlayerFour4() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Four4();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Four4 = new LibVLC(requireActivity(), options);
        mediaPlayer_Four4 = new MediaPlayer(libVLC_Four4);
        //设置vlc视频铺满布局
        mediaPlayer_Four4.setScale(0f);

        mediaPlayer_Four4.getVLCVout().setWindowSize((int) (width * 0.5), (int) (width * 0.5));//宽，高  播放窗口的大小
        mediaPlayer_Four4.setAspectRatio("${" + (int) (width * 0.5) + "}:${" + (int) (width * 0.5) + "}");//宽，高  画面大小

        mediaPlayer_Four4.setVolume(0);
        ivlcVout_Four4 = mediaPlayer_Four4.getVLCVout();
        ivlcVout_Four4.setVideoView(binding.sfVideoFour4);
        ivlcVout_Four4.attachViews();

        media_Four4 = new Media(libVLC_Four4, Uri.parse(url4));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Four4.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Four4.addOption(":network-caching=" + cache);
        media_Four4.addOption(":file-caching=" + cache);
        media_Four4.addOption(":live-cacheing=" + cache);
        media_Four4.addOption(":sout-mux-caching=" + cache);
        media_Four4.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Four4.setMedia(media_Four4);
        mediaPlayer_Four4.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerFour4();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerFour4();
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
                        HhLog.e("Vout44");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoFour4.setVisibility(View.VISIBLE);
                                    binding.sfBackFour4.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Four4.play();
    }

    void startPlayerNine4() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Nine4();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Nine4 = new LibVLC(requireActivity(), options);
        mediaPlayer_Nine4 = new MediaPlayer(libVLC_Nine4);
        //设置vlc视频铺满布局
        mediaPlayer_Nine4.setScale(0f);

        mediaPlayer_Nine4.getVLCVout().setWindowSize((int) (width * 0.33), (int) (width * 0.33));//宽，高  播放窗口的大小
        mediaPlayer_Nine4.setAspectRatio("${" + (int) (width * 0.33) + "}:${" + (int) (width * 0.33) + "}");//宽，高  画面大小

        mediaPlayer_Nine4.setVolume(0);
        ivlcVout_Nine4 = mediaPlayer_Nine4.getVLCVout();
        ivlcVout_Nine4.setVideoView(binding.sfVideoNine4);
        ivlcVout_Nine4.attachViews();

        media_Nine4 = new Media(libVLC_Nine4, Uri.parse(url4));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Nine4.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Nine4.addOption(":network-caching=" + cache);
        media_Nine4.addOption(":file-caching=" + cache);
        media_Nine4.addOption(":live-cacheing=" + cache);
        media_Nine4.addOption(":sout-mux-caching=" + cache);
        media_Nine4.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Nine4.setMedia(media_Nine4);
        mediaPlayer_Nine4.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerNine4();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerNine4();
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
                        HhLog.e("Vout94");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoNine4.setVisibility(View.VISIBLE);
                                    binding.sfBackNine4.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Nine4.play();
    }

    void startPlayerNine5() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Nine5();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Nine5 = new LibVLC(requireActivity(), options);
        mediaPlayer_Nine5 = new MediaPlayer(libVLC_Nine5);
        //设置vlc视频铺满布局
        mediaPlayer_Nine5.setScale(0f);

        mediaPlayer_Nine5.getVLCVout().setWindowSize((int) (width * 0.33), (int) (width * 0.33));//宽，高  播放窗口的大小
        mediaPlayer_Nine5.setAspectRatio("${" + (int) (width * 0.33) + "}:${" + (int) (width * 0.33) + "}");//宽，高  画面大小

        mediaPlayer_Nine5.setVolume(0);
        ivlcVout_Nine5 = mediaPlayer_Nine5.getVLCVout();
        ivlcVout_Nine5.setVideoView(binding.sfVideoNine5);
        ivlcVout_Nine5.attachViews();

        media_Nine5 = new Media(libVLC_Nine5, Uri.parse(url5));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Nine5.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Nine5.addOption(":network-caching=" + cache);
        media_Nine5.addOption(":file-caching=" + cache);
        media_Nine5.addOption(":live-cacheing=" + cache);
        media_Nine5.addOption(":sout-mux-caching=" + cache);
        media_Nine5.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Nine5.setMedia(media_Nine5);
        mediaPlayer_Nine5.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerNine5();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerNine5();
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
                        HhLog.e("Vout95");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoNine5.setVisibility(View.VISIBLE);
                                    binding.sfBackNine5.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Nine5.play();
    }

    void startPlayerNine6() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Nine6();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Nine6 = new LibVLC(requireActivity(), options);
        mediaPlayer_Nine6 = new MediaPlayer(libVLC_Nine6);
        //设置vlc视频铺满布局
        mediaPlayer_Nine6.setScale(0f);

        mediaPlayer_Nine6.getVLCVout().setWindowSize((int) (width * 0.33), (int) (width * 0.33));//宽，高  播放窗口的大小
        mediaPlayer_Nine6.setAspectRatio("${" + (int) (width * 0.33) + "}:${" + (int) (width * 0.33) + "}");//宽，高  画面大小

        mediaPlayer_Nine6.setVolume(0);
        ivlcVout_Nine6 = mediaPlayer_Nine6.getVLCVout();
        ivlcVout_Nine6.setVideoView(binding.sfVideoNine6);
        ivlcVout_Nine6.attachViews();

        media_Nine6 = new Media(libVLC_Nine6, Uri.parse(url6));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Nine6.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Nine6.addOption(":network-caching=" + cache);
        media_Nine6.addOption(":file-caching=" + cache);
        media_Nine6.addOption(":live-cacheing=" + cache);
        media_Nine6.addOption(":sout-mux-caching=" + cache);
        media_Nine6.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Nine6.setMedia(media_Nine6);
        mediaPlayer_Nine6.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerNine6();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerNine6();
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
                        HhLog.e("Vout96");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoNine6.setVisibility(View.VISIBLE);
                                    binding.sfBackNine6.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Nine6.play();
    }

    void startPlayerNine7() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Nine7();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Nine7 = new LibVLC(requireActivity(), options);
        mediaPlayer_Nine7 = new MediaPlayer(libVLC_Nine7);
        //设置vlc视频铺满布局
        mediaPlayer_Nine7.setScale(0f);

        mediaPlayer_Nine7.getVLCVout().setWindowSize((int) (width * 0.33), (int) (width * 0.33));//宽，高  播放窗口的大小
        mediaPlayer_Nine7.setAspectRatio("${" + (int) (width * 0.33) + "}:${" + (int) (width * 0.33) + "}");//宽，高  画面大小

        mediaPlayer_Nine7.setVolume(0);
        ivlcVout_Nine7 = mediaPlayer_Nine7.getVLCVout();
        ivlcVout_Nine7.setVideoView(binding.sfVideoNine7);
        ivlcVout_Nine7.attachViews();

        media_Nine7 = new Media(libVLC_Nine7, Uri.parse(url7));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Nine7.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Nine7.addOption(":network-caching=" + cache);
        media_Nine7.addOption(":file-caching=" + cache);
        media_Nine7.addOption(":live-cacheing=" + cache);
        media_Nine7.addOption(":sout-mux-caching=" + cache);
        media_Nine7.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Nine7.setMedia(media_Nine7);
        mediaPlayer_Nine7.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerNine7();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerNine7();
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
                        HhLog.e("Vout97");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoNine7.setVisibility(View.VISIBLE);
                                    binding.sfBackNine7.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Nine7.play();
    }

    void startPlayerNine8() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Nine8();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Nine8 = new LibVLC(requireActivity(), options);
        mediaPlayer_Nine8 = new MediaPlayer(libVLC_Nine8);
        //设置vlc视频铺满布局
        mediaPlayer_Nine8.setScale(0f);

        mediaPlayer_Nine8.getVLCVout().setWindowSize((int) (width * 0.33), (int) (width * 0.33));//宽，高  播放窗口的大小
        mediaPlayer_Nine8.setAspectRatio("${" + (int) (width * 0.33) + "}:${" + (int) (width * 0.33) + "}");//宽，高  画面大小

        mediaPlayer_Nine8.setVolume(0);
        ivlcVout_Nine8 = mediaPlayer_Nine8.getVLCVout();
        ivlcVout_Nine8.setVideoView(binding.sfVideoNine8);
        ivlcVout_Nine8.attachViews();

        media_Nine8 = new Media(libVLC_Nine8, Uri.parse(url8));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Nine8.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Nine8.addOption(":network-caching=" + cache);
        media_Nine8.addOption(":file-caching=" + cache);
        media_Nine8.addOption(":live-cacheing=" + cache);
        media_Nine8.addOption(":sout-mux-caching=" + cache);
        media_Nine8.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Nine8.setMedia(media_Nine8);
        mediaPlayer_Nine8.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerNine8();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerNine8();
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
                        HhLog.e("Vout98");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoNine8.setVisibility(View.VISIBLE);
                                    binding.sfBackNine8.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Nine8.play();
    }

    void startPlayerNine9() {
        final ArrayList<String> options = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        releasePlayer_Nine9();
        //options.add("--aout=opensles");//音频输出模块opensles模式
        //options.add(" --audio-time-stretch");
        //options.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");
        libVLC_Nine9 = new LibVLC(requireActivity(), options);
        mediaPlayer_Nine9 = new MediaPlayer(libVLC_Nine9);
        //设置vlc视频铺满布局
        mediaPlayer_Nine9.setScale(0f);

        mediaPlayer_Nine9.getVLCVout().setWindowSize((int) (width * 0.33), (int) (width * 0.33));//宽，高  播放窗口的大小
        mediaPlayer_Nine9.setAspectRatio("${" + (int) (width * 0.33) + "}:${" + (int) (width * 0.33) + "}");//宽，高  画面大小

        mediaPlayer_Nine9.setVolume(0);
        ivlcVout_Nine9 = mediaPlayer_Nine9.getVLCVout();
        ivlcVout_Nine9.setVideoView(binding.sfVideoNine9);
        ivlcVout_Nine9.attachViews();

        media_Nine9 = new Media(libVLC_Nine9, Uri.parse(url9));
        //media?.addOption(":network-caching=500")//网络缓存
        //media?.addOption(":rtsp-tcp")//RTSP采用TCP传输方式
        media_Nine9.setHWDecoderEnabled(true, true);
        int cache = 1500;
        media_Nine9.addOption(":network-caching=" + cache);
        media_Nine9.addOption(":file-caching=" + cache);
        media_Nine9.addOption(":live-cacheing=" + cache);
        media_Nine9.addOption(":sout-mux-caching=" + cache);
        media_Nine9.addOption(":codec=mediacodec,iomx,all");
        mediaPlayer_Nine9.setMedia(media_Nine9);
        mediaPlayer_Nine9.setEventListener(new MediaPlayer.EventListener() {
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
                        startPlayerNine9();
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        // 处理播放错误事件
                        HhLog.e("EncounteredError");
                        new Handler().postDelayed(() -> {
                            try{
                                startPlayerNine9();
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
                        HhLog.e("Vout99");
                        obtainViewModel().loading.postValue(new LoadingEvent(false));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.sfVideoNine9.setVisibility(View.VISIBLE);
                                    binding.sfBackNine9.setVisibility(View.GONE);
                                    binding.sfBackAll.setVisibility(View.GONE);
                                } catch (Exception e) {

                                }
                            }
                        },500);
                        break;
                }
            }
        });
        mediaPlayer_Nine9.play();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.gsyPlayerOne1.getCurrentPlayer().onVideoPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.gsyPlayerOne1.getCurrentPlayer().onVideoResume(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonData.videoPlayingIndexList.clear();
        EventBus.getDefault().unregister(this);

        releaseVLC();

        try {
            binding.gsyPlayerOne1.getCurrentPlayer().release();
        } catch (Exception e) {
            Log.e("TAG", "onDestroy: e" + e.getMessage());
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
    }

    private void releaseVLC() {
        releasePlayer();
        releasePlayer_Four1();
        releasePlayer_Four2();
        releasePlayer_Four3();
        releasePlayer_Four4();
        releasePlayer_Nine1();
        releasePlayer_Nine2();
        releasePlayer_Nine3();
        releasePlayer_Nine4();
        releasePlayer_Nine5();
        releasePlayer_Nine6();
        releasePlayer_Nine7();
        releasePlayer_Nine8();
        releasePlayer_Nine9();
    }
    private void releaseVLC_ONE(){
        releasePlayer_Four1();
        releasePlayer_Four2();
        releasePlayer_Four3();
        releasePlayer_Four4();
        releasePlayer_Nine1();
        releasePlayer_Nine2();
        releasePlayer_Nine3();
        releasePlayer_Nine4();
        releasePlayer_Nine5();
        releasePlayer_Nine6();
        releasePlayer_Nine7();
        releasePlayer_Nine8();
        releasePlayer_Nine9();
    }
    private void releaseVLC_FOUR(){
        releasePlayer();
        releasePlayer_Nine1();
        releasePlayer_Nine2();
        releasePlayer_Nine3();
        releasePlayer_Nine4();
        releasePlayer_Nine5();
        releasePlayer_Nine6();
        releasePlayer_Nine7();
        releasePlayer_Nine8();
        releasePlayer_Nine9();
    }
    private void releaseVLC_NINE(){
        releasePlayer();
        releasePlayer_Four1();
        releasePlayer_Four2();
        releasePlayer_Four3();
        releasePlayer_Four4();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLoading() {
        obtainViewModel().loading.setValue(new LoadingEvent(true, "加载中.."));
    }

    @Override
    public void finishLoading() {
        obtainViewModel().loading.setValue(new LoadingEvent(false));
    }

    @Override
    public void onDialogRefresh() {

    }

    @Override
    public void hideTreeDialog() {
        treeDialog.hide();
    }

    @Override
    public void closeInputListener(View view) {
        closeInput(view);
    }

    @Override
    public void modelRefresh() {

    }
}
