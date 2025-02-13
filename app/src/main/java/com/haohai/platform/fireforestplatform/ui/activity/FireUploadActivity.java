package com.haohai.platform.fireforestplatform.ui.activity;

import static com.haohai.platform.fireforestplatform.old.ResourceAddActivity.MAP_REUEST_CODE;
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

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityFireUploadBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityLoginBinding;
import com.haohai.platform.fireforestplatform.old.FireMapActivity;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImageViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FireUploadViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.LoginViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImagePagerUtil;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.haohai.platform.fireforestplatform.utils.StringData;
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

public class FireUploadActivity extends BaseLiveActivity<ActivityFireUploadBinding, FireUploadViewModel> implements ChooseImageViewBinder.OnItemClickListener, DatePicker.OnDateChangedListener {

    private static final int REQUEST_CODE_CHOOSE = 23;
    public static final int MAP_REQUEST_CODE = 2;
    public static final int REQUEST_CODE_VIDEO = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        initDateTime();
        updateData();
        obtainViewModel().getGridData();
        initGeo();
    }

    private String latitude;
    private String longitude;
    private String cityAddress;
    private String countryName;
    private String province;
    private String city;
    private String district;
    private String town;
    private String street;
    private int adcode;
    private void initGeo() {
        //创建新的地理编码检索实例；
        GeoCoder geoCoder = GeoCoder.newInstance();

        //创建地理编码检索监听者；
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                }

                //获取地理编码结果
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                }
                try{
                    countryName = result.getAddressDetail().countryName;
                    province = result.getAddressDetail().province;
                    city = result.getAddressDetail().city;
                    district = result.getAddressDetail().district;
                    town = result.getAddressDetail().town;
                    street = result.getAddressDetail().street;
                    adcode = result.getAddressDetail().adcode;

                    List<PoiInfo> poiList = result.getPoiList();
                    if(poiList!=null && !poiList.isEmpty()){
                        PoiInfo poiInfo = poiList.get(0);
                        cityAddress = poiInfo.address + poiInfo.name;
                    }

                    //处理显示数据
                    obtainViewModel().longitude = longitude;
                    obtainViewModel().latitude = latitude;
                    obtainViewModel().cityAddress = cityAddress;
                    obtainViewModel().currentCity = city;
                    obtainViewModel().currentQu = district;
                    LatLngChangeNew latLngChangeNew = new LatLngChangeNew();
                    double[] doubles = LatLngChangeNew.calBD09toWGS84(Double.parseDouble(obtainViewModel().latitude), Double.parseDouble(obtainViewModel().longitude));
                    if (!obtainViewModel().currentCity.isEmpty()) {
                        String currentCiryParentId = "";
                        String currentCiryId = "";

                        String currentPro = "";
                        String currentProId = "";

                        String currentQuId = "";
                        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
                            if (obtainViewModel().allAreaList.get(i).getName().equals(obtainViewModel().currentCity)) {
                                currentCiryParentId = obtainViewModel().allAreaList.get(i).getParentId();
                                currentCiryId = obtainViewModel().allAreaList.get(i).getId();
                            }
                        }
                        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
                            if (obtainViewModel().allAreaList.get(i).getName().equals(obtainViewModel().currentQu)) {
                                currentQuId = obtainViewModel().allAreaList.get(i).getId();
                            }
                        }

                        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
                            if (obtainViewModel().allAreaList.get(i).getId().equals(currentCiryParentId)) {
                                currentPro = obtainViewModel().allAreaList.get(i).getName();
                                currentProId = obtainViewModel().allAreaList.get(i).getId();
                            }
                        }
                        obtainViewModel().shengStrList.add(currentPro);
                        obtainViewModel().shiStrList.add(obtainViewModel().currentCity);
                        obtainViewModel().isChooseSheng = true;

                        binding.shengAddFireText.setText(currentPro);
                        binding.shiAddFireText.setText(obtainViewModel().currentCity);
                        binding.quText.setText(obtainViewModel().currentQu);

                        obtainViewModel().fromMap = true;
                        initAreaById(currentPro);
                        initShiByShiId(currentProId, currentCiryId);
                        initQuByQuId(currentCiryId, currentQuId);
                        obtainViewModel().currentChooseSheng = currentPro;
                        obtainViewModel().currentChooseShi = obtainViewModel().currentCity;
                        obtainViewModel().currentChooseQu = obtainViewModel().currentQu;
                        obtainViewModel().shengId = currentProId;
                        obtainViewModel().shiId = currentCiryId;
                        obtainViewModel().quId = currentQuId;
                    }

                    binding.addressView.setText(obtainViewModel().cityAddress);
                    binding.jingduView.setText(doubles[1] + "");
                    binding.weiduView.setText(doubles[0] + "");
                }catch (Exception e){
                    HhLog.e(e.getMessage());
                }

            }
        };
        //设置地理编码检索监听者；
        geoCoder.setOnGetGeoCodeResultListener(listener);

        //发起地理编码检索；
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(CommonData.lat,CommonData.lng)));
        latitude = CommonData.lat+"";
        longitude = CommonData.lng+"";
    }

    private void init_() {
        binding.topBar.title.setText("火情上报");

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
        binding.toMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FireMapActivity.class);
                intent.putExtra("longitude_double", CommonData.lng);
                intent.putExtra("latitude_double", CommonData.lat);
                startActivityForResult(intent, MAP_REQUEST_CODE);
            }
        });
        binding.addFireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseNull();
            }
        });
        binding.fireTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataDialog();
            }
        });
        binding.shengAddFireText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainViewModel().currentChooseArea = 0;
                showAreaDialog(obtainViewModel().shengStrList);
            }
        });
        binding.shiAddFireText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainViewModel().currentChooseArea = 1;
                if (obtainViewModel().fromMap) {
                    showAreaDialog(obtainViewModel().shiStrList);
                } else {
                    if (binding.shengAddFireText.getText().toString().equals("请选择省")) {
                        Toast.makeText(FireUploadActivity.this, "请先选择省", Toast.LENGTH_SHORT).show();
                    } else {
                        String currentShengId = "";
                        for (int i = 0; i < obtainViewModel().shengList.size(); i++) {
                            if (obtainViewModel().shengList.get(i).getName().equals(obtainViewModel().currentChooseSheng)) {
                                currentShengId = obtainViewModel().shengList.get(i).getId();
                            }
                        }

                        initShi(currentShengId);
                    }
                }
            }
        });
        binding.quText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainViewModel().currentChooseArea = 2;
                if (obtainViewModel().fromMap) {
                    showAreaDialog(obtainViewModel().quStrList);
                } else {
                    if (binding.shiAddFireText.getText().toString().equals("请选择市")) {
                        Toast.makeText(FireUploadActivity.this, "请先选择市", Toast.LENGTH_SHORT).show();
                    } else {
                        String currentShiId = "";
                        for (int i = 0; i < obtainViewModel().shiList.size(); i++) {
                            if (obtainViewModel().shiList.get(i).getName().equals(obtainViewModel().currentChooseShi)) {
                                currentShiId = obtainViewModel().shiList.get(i).getId();
                            }
                        }

                        initQu(currentShiId);
                    }
                }
            }
        });
    }

    private void parseNull() {
        if (obtainViewModel().list.size() == 0 && (obtainViewModel().videoPath == null || obtainViewModel().videoPath.isEmpty())) {
            Toast.makeText(this, "请至少添加一张图片或视频", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.fireNameView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入火点名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.addressView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.jingduView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入经度", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.weiduView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入纬度", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.fireTimeText.getText().toString().isEmpty()) {
            Toast.makeText(this, "请选择时间", Toast.LENGTH_SHORT).show();
            return;
        }
        obtainViewModel().fireAddress = binding.addressView.getText().toString();
        obtainViewModel().fireName = binding.fireNameView.getText().toString();
        obtainViewModel().fireLng = binding.jingduView.getText().toString();
        obtainViewModel().fireLat = binding.weiduView.getText().toString();
        obtainViewModel().fireDate = binding.fireTimeText.getText().toString();
        obtainViewModel().fireArea = binding.mianjiView.getText().toString();
        obtainViewModel().fireReporter = binding.personView.getText().toString();
        obtainViewModel().postPicToService();
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
                        Matisse.from(FireUploadActivity.this)
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

    private void initQu(String currentShiId) {
        obtainViewModel().quList.clear();
        obtainViewModel().quStrList.clear();
        obtainViewModel().quStrList.add("请选择区");
        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
            if (obtainViewModel().allAreaList.get(i).getParentId().equals(currentShiId)) {
                obtainViewModel().quList.add(obtainViewModel().allAreaList.get(i));
                obtainViewModel().quStrList.add(obtainViewModel().allAreaList.get(i).getName());
            }
        }

        showAreaDialog(obtainViewModel().quStrList);
    }

    private void initShi(String currentShengId) {
        obtainViewModel().shiList.clear();
        obtainViewModel().shiStrList.clear();
        obtainViewModel().shiStrList.add("请选择市");
        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
            if (obtainViewModel().allAreaList.get(i).getParentId().equals(currentShengId)) {
                obtainViewModel().shiList.add(obtainViewModel().allAreaList.get(i));
                obtainViewModel().shiStrList.add(obtainViewModel().allAreaList.get(i).getName());
            }
        }

        showAreaDialog(obtainViewModel().shiStrList);
    }

    @Override
    protected ActivityFireUploadBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_fire_upload);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public FireUploadViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(FireUploadViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }

    /**
     * 显示地区选择的dialog
     */
    private void showAreaDialog(List<String> strList) {
        View areaView = LayoutInflater.from(this).inflate(R.layout.dialog_area, null);
        obtainViewModel().areaWy = ((WheelView) areaView.findViewById(R.id.wheel_view_area));
        obtainViewModel().areaWy.setIsLoop(false);
        if (obtainViewModel().currentChooseArea == 0) {
            obtainViewModel().areaWy.setItems(strList, obtainViewModel().shengSelectIndex);//init selected position is 0 初始选中位置为0
        } else if (obtainViewModel().currentChooseArea == 1) {
            obtainViewModel().areaWy.setItems(strList, obtainViewModel().shiSelectIndex);//init selected position is 0 初始选中位置为0
        } else {
            obtainViewModel().areaWy.setItems(strList, obtainViewModel().quSelectIndex);//init selected position is 0 初始选中位置为0
        }

        obtainViewModel().areaWy.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                obtainViewModel().fromMap = false;
                if (obtainViewModel().currentChooseArea == 0) {   //选择省
                    obtainViewModel().isChooseSheng = true;
                    obtainViewModel().currentChooseSheng = obtainViewModel().areaWy.getSelectedItem();
                    obtainViewModel().shengSelectIndex = obtainViewModel().areaWy.getSelectedPosition();
                    binding.shengAddFireText.setText(obtainViewModel().currentChooseSheng);
                    //选择省要初始化市
                    binding.shiAddFireText.setText("请选择市");
                    obtainViewModel().currentChooseShi = "请选择市";
                    obtainViewModel().shiSelectIndex = 0;
                } else if (obtainViewModel().currentChooseArea == 1) {
                    //选择市
                    obtainViewModel().currentChooseShi = obtainViewModel().areaWy.getSelectedItem();
                    obtainViewModel().shiSelectIndex = obtainViewModel().areaWy.getSelectedPosition();
                    binding.shiAddFireText.setText(obtainViewModel().currentChooseShi);
                    //选择市要初始化区
                    binding.quText.setText("请选择区");
                    obtainViewModel().currentChooseQu = "请选择区";
                    obtainViewModel().quSelectIndex = 0;
                } else {
                    //选择区
                    obtainViewModel().currentChooseQu = obtainViewModel().areaWy.getSelectedItem();
                    obtainViewModel().quSelectIndex = obtainViewModel().areaWy.getSelectedPosition();
                    binding.quText.setText(obtainViewModel().currentChooseQu);
                }
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请选择区域")
                .setView(areaView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String areStr = "";
                        String area = obtainViewModel().areaWy.getSelectedItem();
                    }
                })
                .show();
    }

    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (obtainViewModel().date.length() > 0) { //清除上次记录的日期
                    obtainViewModel().date.delete(0, obtainViewModel().date.length());
                }
                if (obtainViewModel().endDate.length() > 0) { //清除上次记录的日期
                    obtainViewModel().endDate.delete(0, obtainViewModel().endDate.length());
                }
                obtainViewModel().date.append(obtainViewModel().year);
                if (obtainViewModel().month < 9) {
                    obtainViewModel().date.append("-0").append((obtainViewModel().month + 1));
                } else {
                    obtainViewModel().date.append("-").append((obtainViewModel().month + 1));
                }
                if (obtainViewModel().day < 10) {
                    obtainViewModel().date.append("-0").append(obtainViewModel().day);
                } else {
                    obtainViewModel().date.append("-").append(obtainViewModel().day);
                }
                binding.fireTimeText.setText(obtainViewModel().date);
                dialog.dismiss();
                showTimeDialog();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH);
        int day1 = date.get(Calendar.DATE);
        String endData = year1 - 10 + "-" + month1 + "-" + day1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(endData);
        } catch (ParseException e) {

        }
        long starTimre = date2.getTime();


        long endTimre = System.currentTimeMillis();

        datePicker.setMaxDate(endTimre);
        datePicker.setMinDate(starTimre);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(obtainViewModel().year, obtainViewModel().month, obtainViewModel().day, this);
    }

    /**
     * 日期选择控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTimeDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute < 10) {
                    binding.fireTimeText.append("  0" + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
                } else if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute > 10) {
                    binding.fireTimeText.append("  0" + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
                } else if (obtainViewModel().chooseHour > 10 && obtainViewModel().chooseMinute < 10) {
                    binding.fireTimeText.append("  " + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
                } else {
                    binding.fireTimeText.append("  " + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
                }


                dialog.dismiss();
            }
        });
        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog timeDialog = builder1.create();
        View dialogView = View.inflate(this, R.layout.dialog_time, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        timePicker.setIs24HourView(true);   //设置时间显示为24小时

        timePicker.setHour(hour);  //设置当前小时
        timePicker.setMinute(minute); //设置当前分（0-59）

        timeDialog.setTitle("设置时间");
        timeDialog.setView(dialogView);
        timeDialog.show();


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {  //获取当前选择的时间
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                obtainViewModel().chooseHour = hourOfDay;
                obtainViewModel().chooseMinute = minute;
            }
        });
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
                            Matisse.from(FireUploadActivity.this)
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
            ImagePagerUtil imagePagerUtil = new ImagePagerUtil(FireUploadActivity.this, picList);
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
        } else if (requestCode == MAP_REQUEST_CODE && resultCode == MAP_REUEST_CODE) {
            obtainViewModel().longitude = data.getStringExtra("longitude");
            obtainViewModel().latitude = data.getStringExtra("latitude");
            obtainViewModel().cityAddress = data.getStringExtra("cityAddress");
            obtainViewModel().currentCity = data.getStringExtra("city");
            obtainViewModel().currentQu = data.getStringExtra("district");
            LatLngChangeNew latLngChangeNew = new LatLngChangeNew();
            double[] doubles = LatLngChangeNew.calBD09toWGS84(Double.parseDouble(obtainViewModel().latitude), Double.parseDouble(obtainViewModel().longitude));
            if (!obtainViewModel().currentCity.isEmpty()) {
                String currentCiryParentId = "";
                String currentCiryId = "";

                String currentPro = "";
                String currentProId = "";

                String currentQuId = "";
                for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
                    if (obtainViewModel().allAreaList.get(i).getName().equals(obtainViewModel().currentCity)) {
                        currentCiryParentId = obtainViewModel().allAreaList.get(i).getParentId();
                        currentCiryId = obtainViewModel().allAreaList.get(i).getId();
                    }
                }
                for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
                    if (obtainViewModel().allAreaList.get(i).getName().equals(obtainViewModel().currentQu)) {
                        currentQuId = obtainViewModel().allAreaList.get(i).getId();
                    }
                }

                for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
                    if (obtainViewModel().allAreaList.get(i).getId().equals(currentCiryParentId)) {
                        currentPro = obtainViewModel().allAreaList.get(i).getName();
                        currentProId = obtainViewModel().allAreaList.get(i).getId();
                    }
                }
                obtainViewModel().shengStrList.add(currentPro);
                obtainViewModel().shiStrList.add(obtainViewModel().currentCity);
                obtainViewModel().isChooseSheng = true;

                binding.shengAddFireText.setText(currentPro);
                binding.shiAddFireText.setText(obtainViewModel().currentCity);
                binding.quText.setText(obtainViewModel().currentQu);

                obtainViewModel().fromMap = true;
                initAreaById(currentPro);
                initShiByShiId(currentProId, currentCiryId);
                initQuByQuId(currentCiryId, currentQuId);
                obtainViewModel().currentChooseSheng = currentPro;
                obtainViewModel().currentChooseShi = obtainViewModel().currentCity;
                obtainViewModel().currentChooseQu = obtainViewModel().currentQu;
                obtainViewModel().shengId = currentProId;
                obtainViewModel().shiId = currentCiryId;
                obtainViewModel().quId = currentQuId;
            }

            binding.addressView.setText(obtainViewModel().cityAddress);
            binding.jingduView.setText(doubles[1] + "");
            binding.weiduView.setText(doubles[0] + "");
        }/*else if (requestCode == REQUEST_CODE_VIDEO && resultCode == RESULT_OK && null != data) {//插件选择视频有问题
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
        }*/ else if (requestCode == REQUEST_CODE_VIDEO && resultCode == RESULT_OK && null != data) {//选择视频
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

    private void initAreaById(String shengName) {
        obtainViewModel().shengStrList.clear();
        obtainViewModel().shengList.clear();
        obtainViewModel().shengStrList.add("请选择省");
        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
            if (obtainViewModel().allAreaList.get(i).getAreaLevel().equals("1")) {
                obtainViewModel().shengList.add(obtainViewModel().allAreaList.get(i));
                obtainViewModel().shengStrList.add(obtainViewModel().allAreaList.get(i).getName());
            }
        }

        for (int i = 0; i < obtainViewModel().shengList.size(); i++) {
            if (obtainViewModel().shengList.get(i).getName().equals(shengName)) {
                obtainViewModel().shengSelectIndex = i + 1;
            }
        }
    }


    private void initShiByShiId(String currentShengId, String currentShiId) {
        obtainViewModel().shiList.clear();
        obtainViewModel().shiStrList.clear();
        obtainViewModel().shiStrList.add("请选择市");
        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
            if (obtainViewModel().allAreaList.get(i).getParentId().equals(currentShengId)) {
                obtainViewModel().shiList.add(obtainViewModel().allAreaList.get(i));
                obtainViewModel().shiStrList.add(obtainViewModel().allAreaList.get(i).getName());
            }
        }
        for (int i = 0; i < obtainViewModel().shiList.size(); i++) {
            if (obtainViewModel().shiList.get(i).getId().equals(currentShiId)) {
                obtainViewModel().shiSelectIndex = i + 1;
            }
        }
    }

    private void initQuByQuId(String currentShiId, String currentQuId) {
        obtainViewModel().quList.clear();
        obtainViewModel().quStrList.clear();
        obtainViewModel().quStrList.add("请选择区");
        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
            if (obtainViewModel().allAreaList.get(i).getParentId().equals(currentShiId)) {
                obtainViewModel().quList.add(obtainViewModel().allAreaList.get(i));
                obtainViewModel().quStrList.add(obtainViewModel().allAreaList.get(i).getName());
            }
        }
        for (int i = 0; i < obtainViewModel().quList.size(); i++) {
            if (obtainViewModel().quList.get(i).getId().equals(currentQuId)) {
                obtainViewModel().quSelectIndex = i + 1;
            }
        }
    }


    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        obtainViewModel().year = year;
        obtainViewModel().month = monthOfYear;
        obtainViewModel().day = dayOfMonth;
    }

    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        obtainViewModel().year = calendar.get(Calendar.YEAR);
        obtainViewModel().month = calendar.get(Calendar.MONTH);
        obtainViewModel().day = calendar.get(Calendar.DAY_OF_MONTH);
        obtainViewModel().chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        obtainViewModel().chooseMinute = calendar.get(Calendar.MINUTE);

        defaultDate();
    }

    private void defaultDate(){
        if (obtainViewModel().date.length() > 0) { //清除上次记录的日期
            obtainViewModel().date.delete(0, obtainViewModel().date.length());
        }
        if (obtainViewModel().endDate.length() > 0) { //清除上次记录的日期
            obtainViewModel().endDate.delete(0, obtainViewModel().endDate.length());
        }
        obtainViewModel().date.append(obtainViewModel().year);
        if (obtainViewModel().month < 9) {
            obtainViewModel().date.append("-0").append((obtainViewModel().month + 1));
        } else {
            obtainViewModel().date.append("-").append((obtainViewModel().month + 1));
        }
        if (obtainViewModel().day < 10) {
            obtainViewModel().date.append("-0").append(obtainViewModel().day);
        } else {
            obtainViewModel().date.append("-").append(obtainViewModel().day);
        }
        binding.fireTimeText.setText(obtainViewModel().date);



        if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute < 10) {
            binding.fireTimeText.append("  0" + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
        } else if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute > 10) {
            binding.fireTimeText.append("  0" + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
        } else if (obtainViewModel().chooseHour > 10 && obtainViewModel().chooseMinute < 10) {
            binding.fireTimeText.append("  " + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
        } else {
            binding.fireTimeText.append("  " + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
        }

    }
}