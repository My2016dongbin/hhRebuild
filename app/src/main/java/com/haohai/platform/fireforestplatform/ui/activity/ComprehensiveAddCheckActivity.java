package com.haohai.platform.fireforestplatform.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.haohai.platform.fireforestplatform.databinding.ActivityComprehensiveAddCheckBinding;
import com.haohai.platform.fireforestplatform.old.linyi.Res;
import com.haohai.platform.fireforestplatform.ui.bean.CheckImage;
import com.haohai.platform.fireforestplatform.ui.bean.CheckResource;
import com.haohai.platform.fireforestplatform.ui.bean.ResInfo;
import com.haohai.platform.fireforestplatform.ui.cell.TypeChooseDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ComprehensiveAddCheckViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
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

public class ComprehensiveAddCheckActivity extends BaseLiveActivity<ActivityComprehensiveAddCheckBinding, ComprehensiveAddCheckViewModel> implements TypeChooseDialog.TypeChooseDialogListener {

    private final int REQUEST_CODE_CHOOSE = 985;
    private TypeChooseDialog typeChooseDialog;
    private TypeChooseDialog resourceChooseDialog;

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
        binding.topBar.title.setText("添加检查点");
        updateStatus();
        List<CheckImage> images = new ArrayList<>();
        images.add(new CheckImage());
        updateImages(images);
    }

    private void chooseType(){
        typeChooseDialog = new TypeChooseDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = typeChooseDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        typeChooseDialog.setDialogListener(this);
        typeChooseDialog.setTreeList(parseStrings(),obtainViewModel().resIndex,RES_TYPE_CODE);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        typeChooseDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            typeChooseDialog.create();
        }
        typeChooseDialog.show();
    }
    private void chooseResource(){
        resourceChooseDialog = new TypeChooseDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = resourceChooseDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        resourceChooseDialog.setDialogListener(this);
        resourceChooseDialog.setTreeList(parseResStrings(),obtainViewModel().resInfoIndex,RES_LIST_CODE);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        resourceChooseDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resourceChooseDialog.create();
        }
        resourceChooseDialog.show();
    }

    private List<String> parseStrings() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < obtainViewModel().resList.size(); i++) {
            list.add(obtainViewModel().resList.get(i).getName());
        }
        return list;
    }
    private List<String> parseResStrings() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < obtainViewModel().resInfoList.size(); i++) {
            list.add(obtainViewModel().resInfoList.get(i).getName());
        }
        return list;
    }

    private void bind_() {
        binding.textType.setOnClickListener(v -> {
            if(obtainViewModel().resList==null || obtainViewModel().resList.isEmpty()){
                Toast.makeText(this, "资源类型加载中..", Toast.LENGTH_SHORT).show();
                return;
            }
            chooseType();
        });
        binding.textTitle.setOnClickListener(v -> {
            if(obtainViewModel().resInfoList==null || obtainViewModel().resInfoList.isEmpty()){
                Toast.makeText(this, "资源加载中..", Toast.LENGTH_SHORT).show();
                return;
            }
            chooseResource();
        });
        binding.submit.setOnClickListener(v -> {
            
            try{
                CheckResource checkResource = new CheckResource();
                checkResource.setCheckType(2);
                checkResource.setDescription(binding.editZz.getText().toString());
                ResInfo resInfo = obtainViewModel().resInfoList.get(obtainViewModel().resInfoIndex);
                checkResource.setLatitude(Double.parseDouble(resInfo.getPosition().getLat()));
                checkResource.setLongitude(Double.parseDouble(resInfo.getPosition().getLng()));
                checkResource.setName(resInfo.getName());
                checkResource.setResourceType(obtainViewModel().apiCode);
                checkResource.setStatus(4);
                List<CheckResource.ImgsBean> images = new ArrayList<>();
                checkResource.setImgs(images);
                CommonData.checkResourceList.add(checkResource);
                setResult(1);
                finish();
            }catch (Exception e){
                HhLog.e(e.toString());
            }
        });
        binding.imagePass.setOnClickListener(v -> {
            obtainViewModel().pass = true;
            updateStatus();
        });
        binding.imageRefuse.setOnClickListener(v -> {
            obtainViewModel().pass = false;
            updateStatus();
        });
    }

    private void updateStatus() {
        if (obtainViewModel().pass) {
            binding.imagePass.setImageDrawable(getResources().getDrawable(R.drawable.yes));
            binding.imageRefuse.setImageDrawable(getResources().getDrawable(R.drawable.no));
        } else {
            binding.imagePass.setImageDrawable(getResources().getDrawable(R.drawable.no));
            binding.imageRefuse.setImageDrawable(getResources().getDrawable(R.drawable.yes));
        }
    }

    @Override
    protected ActivityComprehensiveAddCheckBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_comprehensive_add_check);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ComprehensiveAddCheckViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ComprehensiveAddCheckViewModel.class);
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
                    ImagePagerUtil imagePagerUtil = new ImagePagerUtil(ComprehensiveAddCheckActivity.this, picList);
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
                                    Matisse.from(ComprehensiveAddCheckActivity.this)
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

    @Override
    public void onTypeChooseDialogRefresh() {

    }

    final int RES_TYPE_CODE = 1;
    final int RES_LIST_CODE = 2;
    @Override
    public void onTypeChoose(String type, int index,int code) {
        if(code == RES_TYPE_CODE){
            binding.textType.setText(type);
            obtainViewModel().resIndex = index;
            Res res = obtainViewModel().resList.get(index);
            obtainViewModel().apiCodeString = res.getName();
            obtainViewModel().getRes(res.getCode());
        }else if(code == RES_LIST_CODE){
            binding.textTitle.setText(type);
            obtainViewModel().resInfoIndex = index;
        }
    }
}