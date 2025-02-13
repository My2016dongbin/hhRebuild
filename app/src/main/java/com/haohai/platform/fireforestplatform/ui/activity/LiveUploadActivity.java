package com.haohai.platform.fireforestplatform.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityFireUploadBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityLiveUploadBinding;
import com.haohai.platform.fireforestplatform.old.FireMapActivity;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImageViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FireUploadViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.LiveUploadViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
import com.haohai.platform.fireforestplatform.utils.ImagePagerUtil;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.drakeet.multitype.MultiTypeAdapter;

public class LiveUploadActivity extends BaseLiveActivity<ActivityLiveUploadBinding, LiveUploadViewModel> implements ChooseImageViewBinder.OnItemClickListener{

    private static final int REQUEST_CODE_CHOOSE = 23;
    public static final int REQUEST_CODE_VIDEO = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel().taskId = getIntent().getStringExtra("id");
        init_();
        bind_();
        updateData();
    }

    private void init_() {
        binding.topBar.title.setText("现场上报");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        binding.photeRecycle.setLayoutManager(gridLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.photeRecycle.setHasFixedSize(true);
        binding.photeRecycle.setNestedScrollingEnabled(false);

        ChooseImageViewBinder chooseImageViewBinder = new ChooseImageViewBinder(this);
        chooseImageViewBinder.setListener(this);
        obtainViewModel().adapter.register(ChooseImage.class, chooseImageViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.photeRecycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.photeRecycle, obtainViewModel().adapter);
    }

    private void bind_() {
        binding.shipinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addVideo();


                TextInfo okTextInfo = new TextInfo();
                okTextInfo.setFontColor(getResources().getColor(R.color.c7));
                MessageDialog.show("视频选择", "", "录制视频", "相册选择")
                        .setButtonOrientation(LinearLayout.VERTICAL)
                        .setOkTextInfo(okTextInfo)
                        .setCancelTextInfo(okTextInfo)
                        .setOtherTextInfo(okTextInfo)
                        .setOkButtonClickListener((dialog, v1) -> {
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);    //  录视频动作
                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);       //   视频质量：0表示低，1表示高
                            //if(videoFile.exists()) videoFile.delete();  //  若视频文件已存在，需先删除
                            //intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);     //  录制的视频保存到videoUri指定的文件
                            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //  授予对方写该文件的权限

                            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 50 * 1024 * 1024L);//限制录制大小(10M=10 * 1024 * 1024L)
                            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);//限制录制时间(10秒=10)

                            startActivityForResult(intent, REQUEST_CODE_VIDEO);   //  调用录视频应用
                            return false;
                        })
                        .setCancelButtonClickListener((dialog, v2) -> {
                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, REQUEST_CODE_VIDEO);
                            return false;
                        })
                        .setOnBackgroundMaskClickListener((dialog, v12) -> {

                            return false;
                        })
                        .setCancelable(true);
            }
        });
        binding.addFireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(obtainViewModel().list.size()==0){
                    Toast.makeText(LiveUploadActivity.this, "至少选择一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                obtainViewModel().liveState = binding.liveView.getText().toString();
                obtainViewModel().otherInfo = binding.otherView.getText().toString();
                obtainViewModel().postPicToService();
            }
        });
    }

    private void addVideo() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        int size = 1;
                        Matisse.from(LiveUploadActivity.this)
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
                                .forResult(REQUEST_CODE_VIDEO);
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
    protected ActivityLiveUploadBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_live_upload);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public LiveUploadViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(LiveUploadViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }

    /**
     * 图片添加
     */
    @Override
    public void onImageAddClickListener(boolean add, Uri uri, String id) {
        if (add) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            int size = 2 - obtainViewModel().list.size();
                            Matisse.from(LiveUploadActivity.this)
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
        } else {
            //点击查看大图
            ArrayList<String> picList = new ArrayList<>();
            String oneUri = uri.toString();
            picList.add(oneUri); //点击哪张 把哪张放第一个
            for (int i = 0; i < obtainViewModel().list.size(); i++) {     //除去点击那张  其他放进去
                if (!oneUri.equals(obtainViewModel().list.get(i).getUri().toString())) {
                    picList.add(obtainViewModel().list.get(i).getUri().toString());
                }
            }
            String content = "";     //放评论
            ImagePagerUtil imagePagerUtil = new ImagePagerUtil(LiveUploadActivity.this, picList);
            imagePagerUtil.setContentText(content);
            imagePagerUtil.show();
        }
    }

    /**
     * 图片删除
     */
    @Override
    public void onImageDelete(Uri uri, String id) {
        for (int i = 0; i < obtainViewModel().list.size(); i++) {
            if (obtainViewModel().list.get(i).getUri().equals(uri)) {
                obtainViewModel().list.remove(i);
            }
        }
        updateData();
    }

    private void updateData() {
        obtainViewModel().items.clear();
        if (obtainViewModel().list == null) {
            ChooseImage chooseImage = new ChooseImage();
            chooseImage.setAdd(true);
            obtainViewModel().items.add(chooseImage);
        } else {
            if (obtainViewModel().list.size() < 2) {
                for (int i = 0; i < obtainViewModel().list.size(); i++) {
                    obtainViewModel().items.add(obtainViewModel().list.get(i));
                }
                ChooseImage chooseImage = new ChooseImage();
                chooseImage.setAdd(true);
                obtainViewModel().items.add(chooseImage);
            } else {
                for (int i = 0; i < obtainViewModel().list.size(); i++) {
                    obtainViewModel().items.add(obtainViewModel().list.get(i));
                }
            }
        }

        assertAllRegistered(obtainViewModel().adapter, obtainViewModel().items);
        obtainViewModel().adapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {

            List<Uri> uriList = Matisse.obtainResult(data);
            //去掉重复图片
            int uriSize = uriList.size();
            int listSize = obtainViewModel().list.size();
            int index = 0;
            for (int i = 0; i < uriList.size(); i++) {
                for (int j = 0; j < obtainViewModel().list.size(); j++) {
                    if (uriList.get(i).toString().equals(obtainViewModel().list.get(j).getUri().toString())) {

                        Toast.makeText(this, "不可添加重复图片！", Toast.LENGTH_SHORT).show();
                        uriList.remove(i);
                        if (uriList.size() == 0) {
                            return;
                        }

                    }
                }
            }


            // 判断只能添加五张图片
            if ((uriList.size() + obtainViewModel().list.size()) > 9) {
                Toast.makeText(this, "最多只能添加2张", Toast.LENGTH_SHORT).show();
                int size = 9 - obtainViewModel().list.size();
                for (int i = 0; i < size; i++) {
                    ChooseImage chooseImage = new ChooseImage();
                    chooseImage.setUri(uriList.get(i));
                    chooseImage.setAdd(false);
                    obtainViewModel().list.add(chooseImage);
                    // items.add(evaluateImage);
                }
            } else {
                //不足5张的添加  添加图片按钮
                int size = uriList.size();
                for (int i = 0; i < size; i++) {
                    ChooseImage chooseImage = new ChooseImage();
                    chooseImage.setUri(uriList.get(i));
                    chooseImage.setAdd(false);
                    obtainViewModel().list.add(chooseImage);
                }
                ChooseImage chooseImage = new ChooseImage();
                chooseImage.setAdd(true);
            }
            updateData();
        }/*else if (requestCode == REQUEST_CODE_VIDEO && resultCode == RESULT_OK && null != data) {
            List<Uri> uriList = Matisse.obtainResult(data);
            Uri selectedVideo;
            if(uriList!=null && !uriList.isEmpty()){
                selectedVideo = uriList.get(0);
                String[] filePathColumn = {MediaStore.Video.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedVideo,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                obtainViewModel().videoPath = cursor.getString(columnIndex);
                cursor.close();
                Log.e("TAG", "onActivityResult: " + obtainViewModel().videoPath);
                binding.shipinView.setText("重新选择视频");
            }
        }*/else if (requestCode == REQUEST_CODE_VIDEO && resultCode == RESULT_OK && null != data) {//选择视频
            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedVideo,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            obtainViewModel().videoPath = cursor.getString(columnIndex);
            cursor.close();
            binding.shipinView.setText("重新选择视频");
        }

    }
}