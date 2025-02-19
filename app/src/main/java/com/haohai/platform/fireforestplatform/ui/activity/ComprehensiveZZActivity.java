package com.haohai.platform.fireforestplatform.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityComprehensiveZzBinding;
import com.haohai.platform.fireforestplatform.old.linyi.CommonUtils;
import com.haohai.platform.fireforestplatform.ui.bean.CheckImage;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ComprehensiveZZViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImagePagerUtil;
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

public class ComprehensiveZZActivity extends BaseLiveActivity<ActivityComprehensiveZzBinding, ComprehensiveZZViewModel>{

    private final int REQUEST_CODE_CHOOSE = 985;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().postData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init_() {
        binding.topBar.title.setText("综合检查整治");
        List<CheckImage> images = new ArrayList<>();
        images.add(new CheckImage());
        updateImages(images);
        initUI();
    }

    private void initUI() {
        binding.name.setText(CommonData.comprehensiveRes.getName());
        binding.grid.setText(CommonData.comprehensiveRes.getGridName());
        binding.type.setText(new CommonUtils().parseType(CommonData.comprehensiveRes.getResourceType()));
        binding.info.setText(CommonData.comprehensiveRes.getDescription());
    }

    private void bind_() {
        binding.submit.setOnClickListener(v -> {
            obtainViewModel().info = binding.editZz.getText().toString();
            obtainViewModel().postSubmit();
        });
    }

    @Override
    protected ActivityComprehensiveZzBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_comprehensive_zz);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ComprehensiveZZViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ComprehensiveZZViewModel.class);
    }



    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().imageList.observe(this, this::updateImages);
    }

    private void updateImages(List<CheckImage> checkImages) {
        if (checkImages.isEmpty()) {
            return;
        }
        ///正常数据显示
        binding.llImage.removeAllViews();
        for (int m = 0; m < checkImages.size(); m++) {
            HhLog.e("updateImages " + m);
            CheckImage checkImage = checkImages.get(m);
            View view = LayoutInflater.from(this).inflate(R.layout.check_image_item, null);
            ImageView icon = view.findViewById(R.id.icon);
            ImageView x = view.findViewById(R.id.x);
            if (checkImage.getUri() != null) {
                Glide.with(this).load(checkImage.getUri()).into(icon);
                icon.setOnClickListener(v -> {
                    //展示图片
                    //点击查看大图
                    ArrayList<String> picList = new ArrayList<>();
                    String oneUri = checkImage.getUri().toString();
                    picList.add(oneUri); //点击哪张 把哪张放第一个
                    for (int i = 0; i < Objects.requireNonNull(obtainViewModel().imageList.getValue()).size(); i++) {     //除去点击那张  其他放进去
                        if (Objects.requireNonNull(obtainViewModel().imageList.getValue()).get(i).getUri() == null) {
                            continue;
                        }
                        if (!oneUri.equals(Objects.requireNonNull(obtainViewModel().imageList.getValue()).get(i).getUri().toString())) {
                            picList.add(Objects.requireNonNull(obtainViewModel().imageList.getValue()).get(i).getUri().toString());
                        }
                    }
                    String content = "";     //放评论
                    ImagePagerUtil imagePagerUtil = new ImagePagerUtil(ComprehensiveZZActivity.this, picList);
                    imagePagerUtil.setContentText(content);
                    imagePagerUtil.show();
                });
                x.setVisibility(View.VISIBLE);
                int finalM = m;
                x.setOnClickListener(v -> {
                    checkImages.remove(finalM);
                    //清除添加按钮
                    List<CheckImage> listCp = new ArrayList<>();
                    for (int i = 0; i < checkImages.size(); i++) {
                        CheckImage model = checkImages.get(i);
                        if (model.getUri() != null) {
                            listCp.add(model);
                        }
                    }
                    if (listCp.size() < obtainViewModel().imageSize) {
                        listCp.add(new CheckImage());
                    }
                    obtainViewModel().imageList.postValue(listCp);

                });
            } else {
                HhLog.e("updateImages else " + m);
                Glide.with(this).load(getResources().getDrawable(R.drawable.ic_add_photo)).into(icon);
                icon.setOnClickListener(v -> {
                    //添加图片
                    RxPermissions rxPermissions = new RxPermissions(this);
                    rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                            .subscribe(new Observer<Boolean>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Boolean aBoolean) {
                                    int size = obtainViewModel().imageSize - (Objects.requireNonNull(obtainViewModel().imageList.getValue()).size()-1);
                                    if(Objects.requireNonNull(obtainViewModel().imageList.getValue()).isEmpty()){
                                        size = obtainViewModel().imageSize;
                                    }

                                    Matisse.from(ComprehensiveZZActivity.this)
                                            .choose(MimeType.ofAll())
                                            .countable(true)
                                            .capture(true)
                                            .captureStrategy(
                                                    new CaptureStrategy(false, "com.haohai.platform.fireforestplatform.fileprovider")
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
                });
                x.setVisibility(View.GONE);
            }
            binding.llImage.addView(view);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE) {
            List<Uri> uriList = Matisse.obtainResult(data);
            //去掉重复图片
            List<CheckImage> list = Objects.requireNonNull(obtainViewModel().imageList.getValue());
            for (int i = 0; i < uriList.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getUri() == null) {
                        continue;
                    }
                    if (uriList.get(i).toString().equals(list.get(j).getUri().toString())) {

                        Toast.makeText(this, "不可添加重复图片！", Toast.LENGTH_SHORT).show();
                        uriList.remove(i);
                        if (uriList.size() == 0) {
                            return;
                        }

                    }
                }
            }

            for (int i = 0; i < uriList.size(); i++) {
                CheckImage chooseImage = new CheckImage();
                chooseImage.setUri(uriList.get(i));
                list.add(chooseImage);
            }

            //清除添加按钮
            List<CheckImage> listCp = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                CheckImage checkImage = list.get(i);
                if (checkImage.getUri() != null) {
                    listCp.add(checkImage);
                }
            }
            if (listCp.size() < obtainViewModel().imageSize) {
                listCp.add(new CheckImage());
            }
            obtainViewModel().imageList.postValue(listCp);
        }
    }
}