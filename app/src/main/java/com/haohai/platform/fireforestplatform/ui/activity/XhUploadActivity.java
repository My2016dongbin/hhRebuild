package com.haohai.platform.fireforestplatform.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.haohai.platform.fireforestplatform.databinding.ActivityXhUploadBinding;
import com.haohai.platform.fireforestplatform.old.FireMapActivity;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImageViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.XhUploadViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
import com.haohai.platform.fireforestplatform.utils.HhLog;
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

import static com.haohai.platform.fireforestplatform.old.ResourceAddActivity.MAP_REUEST_CODE;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class XhUploadActivity extends BaseLiveActivity<ActivityXhUploadBinding, XhUploadViewModel> implements ChooseImageViewBinder.OnItemClickListener, DatePicker.OnDateChangedListener {

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

            @SuppressLint("SetTextI18n")
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
                    double[] doubles = LatLngChangeNew.calBD09toWGS84(Double.parseDouble(obtainViewModel().latitude), Double.parseDouble(obtainViewModel().longitude));
                    if (!obtainViewModel().currentCity.isEmpty()) {
                        //
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
        binding.topBar.title.setText("巡护上报");

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
    }

    private void parseNull() {
        if (obtainViewModel().list.size() == 0) {
            Toast.makeText(this, "请至少添加一张图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.fireNameView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入事件名称", Toast.LENGTH_SHORT).show();
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
        if (binding.haibaView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入海拔", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.maView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入马的数量", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.niuView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入牛的数量", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.yangView.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入羊的数量", Toast.LENGTH_SHORT).show();
            return;
        }
        /*if (binding.fireTimeText.getText().toString().isEmpty()) {
            Toast.makeText(this, "请选择时间", Toast.LENGTH_SHORT).show();
            return;
        }*/
        obtainViewModel().fireAddress = binding.addressView.getText().toString();
        obtainViewModel().fireName = binding.fireNameView.getText().toString();
        obtainViewModel().fireLng = binding.jingduView.getText().toString();
        obtainViewModel().fireLat = binding.weiduView.getText().toString();
        obtainViewModel().fireHb = binding.haibaView.getText().toString();
        obtainViewModel().fireMa = binding.maView.getText().toString();
        obtainViewModel().fireNiu = binding.niuView.getText().toString();
        obtainViewModel().fireYang = binding.yangView.getText().toString();
        obtainViewModel().fireDate = binding.fireTimeText.getText().toString();
        obtainViewModel().postPicToService();
    }

    @Override
    protected ActivityXhUploadBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_xh_upload);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public XhUploadViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(XhUploadViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

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
                            Matisse.from(XhUploadActivity.this)
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
            ImagePagerUtil imagePagerUtil = new ImagePagerUtil(XhUploadActivity.this, picList);
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


    @SuppressLint("SetTextI18n")
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
            double[] doubles = LatLngChangeNew.calBD09toWGS84(Double.parseDouble(obtainViewModel().latitude), Double.parseDouble(obtainViewModel().longitude));
            if (!obtainViewModel().currentCity.isEmpty()) {
                obtainViewModel().fromMap = true;
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
            ///obtainViewModel().videoPath = cursor.getString(columnIndex);
            cursor.close();
            ///binding.shipinView.setText("重新选择视频");
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
        if (obtainViewModel().month <= 9) {
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
            binding.fireTimeText.append(" 0" + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
        } else if (obtainViewModel().chooseHour < 10 && obtainViewModel().chooseMinute > 10) {
            binding.fireTimeText.append(" 0" + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
        } else if (obtainViewModel().chooseHour >= 10 && obtainViewModel().chooseMinute < 10) {
            binding.fireTimeText.append(" " + obtainViewModel().chooseHour + ":0" + obtainViewModel().chooseMinute + ":00");
        } else {
            binding.fireTimeText.append(" " + obtainViewModel().chooseHour + ":" + obtainViewModel().chooseMinute + ":00");
        }

    }
}