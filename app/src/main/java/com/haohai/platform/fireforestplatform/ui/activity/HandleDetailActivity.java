package com.haohai.platform.fireforestplatform.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityHandleDetailBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityIdentifyDetailBinding;
import com.haohai.platform.fireforestplatform.ui.bean.CheckResult;
import com.haohai.platform.fireforestplatform.ui.bean.DisposeResult;
import com.haohai.platform.fireforestplatform.ui.bean.HandleResult;
import com.haohai.platform.fireforestplatform.ui.cell.CheckDialog;
import com.haohai.platform.fireforestplatform.ui.cell.DisposeDialog;
import com.haohai.platform.fireforestplatform.ui.cell.HandleDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.Handle;
import com.haohai.platform.fireforestplatform.ui.multitype.Identify;
import com.haohai.platform.fireforestplatform.ui.viewmodel.HandleDetailViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.haohai.platform.fireforestplatform.ui.activity.FireUploadActivity.MAP_REQUEST_CODE;

public class HandleDetailActivity extends BaseLiveActivity<ActivityHandleDetailBinding, HandleDetailViewModel> implements DisposeDialog.DisposeDialogListener, CheckDialog.CheckDialogListener {

    private static final int REQUEST_CODE_CHOOSE = 23;
    public static final int REQUEST_CODE_VIDEO = 66;
    private DisposeDialog handleDialog;
    private CheckDialog checkDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel().id = getIntent().getStringExtra("id");
        init_();
        bind_();
        obtainViewModel().getInfo();
    }

    private void init_() {
        binding.topBar.title.setText("处置详情");

        //处置Dialog
        initHandleDialog();
        //督导Dialog
        initCheckDialog();
    }

    private void initHandleDialog() {
        handleDialog = new DisposeDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = handleDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        handleDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.6);
        dialogWindow.setAttributes(lp);
        handleDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            handleDialog.create();
        }
    }
    private void initCheckDialog() {
        checkDialog = new CheckDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = checkDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        checkDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.6);
        dialogWindow.setAttributes(lp);
        checkDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkDialog.create();
        }
    }

    private void bind_() {
        binding.handle.setOnClickListener(v -> {
            if(obtainViewModel().pictureList==null || obtainViewModel().pictureList.size()==0 ){
                obtainViewModel().pictureList.add(new DisposeResult.Picture(null, null));
            }
            obtainViewModel().disposeResult.setPictures(obtainViewModel().pictureList);
            handleDialog.setDisposeResult(obtainViewModel().disposeResult);
            handleDialog.show();
        });
        binding.check.setOnClickListener(v -> {
            checkDialog.setCheckResult(obtainViewModel().checkResult);
            checkDialog.show();
        });
    }

    private boolean isNew = false;
    private String pictureId;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE) {
                //选择图片
                List<Uri> uriList = Matisse.obtainResult(data);
                if (isNew) {
                    //新增 移除添加按钮
                    for (int m = 0; m < uriList.size(); m++) {
                        obtainViewModel().pictureList.add(new DisposeResult.Picture(null, uriList.get(m)));
                        if (m == uriList.size() - 1 /*&& uriList.size() < obtainViewModel().maxPicture*/) {
                            //添加新添加按钮
                            if (obtainViewModel().pictureList.size() - 1 < obtainViewModel().maxPicture) {
                                obtainViewModel().pictureList.add(new DisposeResult.Picture(null, null));
                                //移除旧添加按钮
                                obtainViewModel().pictureList.remove(obtainViewModel().pictureList.size() - uriList.size() - 2);
                            } else {
                                //移除旧添加按钮
                                obtainViewModel().pictureList.remove(obtainViewModel().pictureList.size() - uriList.size() - 1);
                            }
                            //updatePictures();
                            obtainViewModel().disposeResult.setPictures(obtainViewModel().pictureList);
                            handleDialog.setDisposeResult(obtainViewModel().disposeResult);
                        }
                    }
                } else {
                    //替换
                    for (int m = 0; m < obtainViewModel().pictureList.size(); m++) {
                        DisposeResult.Picture picture = obtainViewModel().pictureList.get(m);
                        if (Objects.equals(picture.getId(), pictureId) && uriList != null && !uriList.isEmpty()) {
                            obtainViewModel().pictureList.get(m).setUri(uriList.get(0));
                            //updatePictures();
                            obtainViewModel().disposeResult.setPictures(obtainViewModel().pictureList);
                            handleDialog.setDisposeResult(obtainViewModel().disposeResult);
                            return;
                        }
                    }

                }


            } else if (requestCode == REQUEST_CODE_VIDEO) {
                //选择视频

            }
        }
    }



    @Override
    public void addPicture(String id) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        int size = 1;
                        if (id == null) {
                            //新增
                            isNew = true;
                            pictureId = null;
                            size = obtainViewModel().maxPicture - obtainViewModel().pictureList.size() + 1;
                        } else {
                            //更换
                            isNew = false;
                            pictureId = id;

                        }
                        Matisse.from(HandleDetailActivity.this)
                                .choose(MimeType.ofAll())
                                .countable(true)
                                .capture(true)
                                .captureStrategy(
                                        new CaptureStrategy(false, "com.haohai.platform.fireforestplatforms.fileprovider")
                                )
                                .maxSelectable(size)
                                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                .gridExpectedSize(
                                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                .thumbnailScale(0.85f)
                                .imageEngine(new GlideEngine())
                                .forResult(REQUEST_CODE_CHOOSE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void deletePicture(String id) {
        for (int m = 0; m < obtainViewModel().pictureList.size(); m++) {
            if (Objects.equals(obtainViewModel().pictureList.get(m).getId(), id)) {
                obtainViewModel().pictureList.remove(m);
                DisposeResult.Picture picture = obtainViewModel().pictureList.get(obtainViewModel().pictureList.size() - 1);
                if (picture.getUri() == null && picture.getUrl() == null) {
                    //移除旧添加按钮
                    obtainViewModel().pictureList.remove(obtainViewModel().pictureList.size() - 1);
                }
                //添加新添加按钮
                obtainViewModel().pictureList.add(new DisposeResult.Picture(null, null));
                //updatePictures();
                obtainViewModel().disposeResult.setPictures(obtainViewModel().pictureList);
                handleDialog.setDisposeResult(obtainViewModel().disposeResult);
                return;
            }
        }
    }

    @Override
    protected ActivityHandleDetailBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_handle_detail);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public HandleDetailViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(HandleDetailViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().handleDetail.observe(this, this::detailUi);
    }

    @SuppressLint("SetTextI18n")
    private void detailUi(Handle handle) {
        if (handle != null) {
            binding.textMonitor.setText(CommonUtil.parseNullString(handle.getName(), "-"));
            binding.textType.setText(CommonUtil.parseNullString(handle.getAlarmType(), "-"));
            binding.textDate.setText(CommonUtil.parse19String(handle.getAlarmDatetime(), "-"));
            binding.textLngLat.setText(CommonUtil.parseNullString(handle.getAlarmLongitude()+","+handle.getAlarmLatitude(), "-"));
            binding.textAddress.setText(CommonUtil.parseNullString(handle.getAddress(), "-"));
            binding.textAngle.setText(CommonUtil.parseNullString(handle.getHorizonAngle()+","+handle.getVerticalAngle(), "-"));
            String status = obtainViewModel().parseStatus(handle);
            binding.textReal.setText(CommonUtil.parseNullString(status, "-"));
            binding.textRemark.setText(CommonUtil.parseNullString(handle.getHandleOpinions(), "-"));
            binding.llWarnPic.removeAllViews();
            List<String> images = new ArrayList<>();
            images.add(handle.getPicPath1());
            images.add(handle.getPicPath2());
            for (int m = 0; m < images.size(); m++) {
                String image = images.get(m);
                View p_ = LayoutInflater.from(this).inflate(R.layout.item_image, null);
                ImageView imageView = p_.findViewById(R.id.image);
                imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(this,TbsActivity.class);
                    intent.putExtra("url",image);
                    intent.putExtra("title","报警图片");
                    startActivity(intent);
                });
                Glide.with(this).load(image).error(getResources().getDrawable(R.drawable.ic_no_pic)).into(imageView);
                binding.llWarnPic.addView(p_);
            }

            binding.llWarnVideo.removeAllViews();
            List<String> videos = new ArrayList<>();
            videos.add(handle.getVideoPath1());
            videos.add(handle.getVideoPath2());
            for (int m = 0; m < videos.size(); m++) {
                String video = videos.get(m);
                View p_ = LayoutInflater.from(this).inflate(R.layout.item_image, null);
                ImageView imageView = p_.findViewById(R.id.image);
                ImageView play = p_.findViewById(R.id.play);
                play.setVisibility(View.VISIBLE);
                play.setOnClickListener(v -> {
                    Intent intent = new Intent(this,TbsActivity.class);
                    intent.putExtra("url",video);
                    intent.putExtra("title","报警视频");
                    startActivity(intent);
                });
                Glide.with(this).load(video).error(getResources().getDrawable(R.drawable.ic_no_pic)).into(imageView);
                binding.llWarnVideo.addView(p_);
            }
        }
    }


    @Override
    public void onCheckDialogResult(CheckResult checkResult) {
        obtainViewModel().checkResult = checkResult;
        obtainViewModel().postCheck();
    }

    @Override
    public void onDisposeDialogResult(DisposeResult disposeResult) {
        obtainViewModel().disposeResult = disposeResult;
        obtainViewModel().postDispose();
    }
}