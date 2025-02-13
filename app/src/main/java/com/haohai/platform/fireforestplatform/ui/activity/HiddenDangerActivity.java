package com.haohai.platform.fireforestplatform.ui.activity;

import static com.haohai.platform.fireforestplatform.old.ResourceAddActivity.MAP_REUEST_CODE;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
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
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.ActivityHiddenDangerBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityLoginBinding;
import com.haohai.platform.fireforestplatform.databinding.FgMain;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.old.FireMapActivity;
import com.haohai.platform.fireforestplatform.ui.bean.Leixing;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImage;
import com.haohai.platform.fireforestplatform.ui.multitype.ChooseImageViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.MainFgMenu;
import com.haohai.platform.fireforestplatform.ui.multitype.MainFgMenuViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.HiddenDangerViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.LoginViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImagePagerUtil;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.haohai.platform.fireforestplatform.utils.StringData;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class HiddenDangerActivity extends BaseLiveActivity<ActivityHiddenDangerBinding, HiddenDangerViewModel> implements ChooseImageViewBinder.OnItemClickListener, DatePicker.OnDateChangedListener {

    private static final int REQUEST_CODE_CHOOSE = 23;
    public static final int MAP_REQUEST_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        initDateTime();
        bind_();
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
                    LatLngChangeNew latLngChangeNew = new LatLngChangeNew();
                    double[] doubles = LatLngChangeNew.calBD09toWGS84(Double.parseDouble(obtainViewModel().latitude), Double.parseDouble(obtainViewModel().longitude));
                    if (!obtainViewModel().currentCity.isEmpty()) {
                        String currentCiryParentId = "";
                        String currentCiryId = "";

                        String currentPro = "";
                        String currentProId = "";
                        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
                            if (obtainViewModel().allAreaList.get(i).getName().equals(obtainViewModel().currentCity)) {
                                currentCiryParentId = obtainViewModel().allAreaList.get(i).getParentId();
                                currentCiryId = obtainViewModel().allAreaList.get(i).getId();
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

                        obtainViewModel().fromMap = true;
                        initAreaById(currentPro);
                        initShiByShiId(currentProId, currentCiryId);
                    }

                    binding.addressView.setText(obtainViewModel().cityAddress);
                    binding.jingduView.setText(doubles[1]+"");
                    binding.weiduView.setText(doubles[0]+"");
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
        binding.topBar.title.setText("隐患排查");


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
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataIsNull();
            }
        });
        binding.liebieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainViewModel().currentLeibie = 0;
                List<String> leibieStrList = new ArrayList<String>();
                for (int i = 0; i < obtainViewModel().leiBieList.size(); i++) {
                    leibieStrList.add(obtainViewModel().leiBieList.get(i).getName());
                }
                showLeiBieDialog(leibieStrList);
            }
        });
        binding.leixingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainViewModel().currentLeibie = 1;
                List<String> leixingStrList = new ArrayList<String>();
                if (obtainViewModel().leibieSelectIndex == 0) {
                    List<Leixing> list = obtainViewModel().leiBieList.get(0).getList();
                    for (int i = 0; i < list.size(); i++) {
                        leixingStrList.add(list.get(i).getName());
                    }
                } else if (obtainViewModel().leibieSelectIndex == 1) {
                    List<Leixing> list = obtainViewModel().leiBieList.get(1).getList();
                    for (int i = 0; i < list.size(); i++) {
                        leixingStrList.add(list.get(i).getName());
                    }
                } else if (obtainViewModel().leibieSelectIndex == 2) {
                    List<Leixing> list = obtainViewModel().leiBieList.get(2).getList();
                    for (int i = 0; i < list.size(); i++) {
                        leixingStrList.add(list.get(i).getName());
                    }
                } else if (obtainViewModel().leibieSelectIndex == 3) {
                    List<Leixing> list = obtainViewModel().leiBieList.get(3).getList();
                    for (int i = 0; i < list.size(); i++) {
                        leixingStrList.add(list.get(i).getName());
                    }
                }
                showLeiBieDialog(leixingStrList);
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
                        Toast.makeText(HiddenDangerActivity.this, "请先选择省", Toast.LENGTH_SHORT).show();
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
    }

    private void dataIsNull() {
        if (obtainViewModel().list.size()==0) {
            Toast.makeText(this, "请至少添加一张图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.nameEdit.getText().toString().equals("")) {
            Toast.makeText(this, "请输入资源点名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (obtainViewModel().currentChooseLeibie.equals("")) {
            Toast.makeText(this, "请选择类别", Toast.LENGTH_SHORT).show();
            return;
        }
        if (obtainViewModel().currentChooseLeixing.equals("")) {
            Toast.makeText(this, "请选择类型", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.addressView.getText().toString().equals("")) {
            Toast.makeText(this, "请输入地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.jingduView.getText().toString().equals("")) {
            Toast.makeText(this, "请输入经度", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double longitude = Double.parseDouble(binding.jingduView.getText().toString());
            double latitude = Double.parseDouble(binding.weiduView.getText().toString());
            obtainViewModel().lng = longitude;
            obtainViewModel().lat = latitude;
        }catch (Exception e){
            Toast.makeText(this, "请输入合理的经纬度", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.weiduView.getText().toString().equals("")) {
            Toast.makeText(this, "请输入纬度", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.fireTimeText.getText().toString().equals("")) {
            Toast.makeText(this, "请选择时间", Toast.LENGTH_SHORT).show();
            return;
        }
        obtainViewModel().shengStr = binding.shengAddFireText.getText().toString();
        obtainViewModel().shiStr = binding.shiAddFireText.getText().toString();
        obtainViewModel().resourceName = binding.nameEdit.getText().toString();
        obtainViewModel().address = binding.addressView.getText().toString();
        obtainViewModel().dangerDescription = binding.fengxianEdit.getText().toString();
        obtainViewModel().remark = binding.zhengzhiEdit.getText().toString();
        obtainViewModel().postPicToService();
    }

    private void showLeiBieDialog(final List<String> strList) {
        View areaView = LayoutInflater.from(this).inflate(R.layout.dialog_area, null);
        obtainViewModel().typeWy = ((WheelView) areaView.findViewById(R.id.wheel_view_area));
        obtainViewModel().typeWy.setIsLoop(false);
        if (obtainViewModel().currentLeibie == 0) {
            obtainViewModel().typeWy.setItems(strList, obtainViewModel().leibieSelectIndex);//init selected position is 0 初始选中位置为0
        } else {
            obtainViewModel().typeWy.setItems(strList, obtainViewModel().leixingSelectIndex);//init selected position is 0 初始选中位置为0
        }

        obtainViewModel().typeWy.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                if (obtainViewModel().currentLeibie == 0) {
                    obtainViewModel().currentChooseLeibie = obtainViewModel().typeWy.getSelectedItem();
                    obtainViewModel().leibieSelectIndex = obtainViewModel().typeWy.getSelectedPosition();
                    binding.liebieView.setText(obtainViewModel().currentChooseLeibie);
                    obtainViewModel().currentChooseLeixing = obtainViewModel().leiBieList.get(obtainViewModel().leibieSelectIndex).getList().get(0).getName();
                    obtainViewModel().leixingSelectIndex = 0;
                    binding.leixingView.setText(obtainViewModel().currentChooseLeixing);
                } else {
                    obtainViewModel().currentChooseLeixing = obtainViewModel().typeWy.getSelectedItem();
                    obtainViewModel().leixingSelectIndex = obtainViewModel().typeWy.getSelectedPosition();
                    binding.leixingView.setText(obtainViewModel().currentChooseLeixing);
                }

            }
        });
        if (obtainViewModel().currentLeibie == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("请选择隐患类别")
                    .setView(areaView)
                    .setPositiveButton("确定 ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (obtainViewModel().leibieSelectIndex == 0) {
                                binding.liebieView.setText(strList.get(0));
                                obtainViewModel().currentChooseLeibie = strList.get(0);
                            }


                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("请选择隐患类型")
                    .setView(areaView)
                    .setPositiveButton("确定 ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (obtainViewModel().leixingSelectIndex == 0) {
                                binding.leixingView.setText(strList.get(0));
                                obtainViewModel().currentChooseLeixing = strList.get(0);
                            }

                        }
                    })
                    .show();
        }
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
    protected ActivityHiddenDangerBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_hidden_danger);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public HiddenDangerViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(HiddenDangerViewModel.class);
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
                            Matisse.from(HiddenDangerActivity.this)
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
            ImagePagerUtil imagePagerUtil = new ImagePagerUtil(HiddenDangerActivity.this, picList);
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
            LatLngChangeNew latLngChangeNew = new LatLngChangeNew();
            double[] doubles = LatLngChangeNew.calBD09toWGS84(Double.parseDouble(obtainViewModel().latitude), Double.parseDouble(obtainViewModel().longitude));
            if (!obtainViewModel().currentCity.isEmpty()) {
                String currentCiryParentId = "";
                String currentCiryId = "";

                String currentPro = "";
                String currentProId = "";
                for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
                    if (obtainViewModel().allAreaList.get(i).getName().equals(obtainViewModel().currentCity)) {
                        currentCiryParentId = obtainViewModel().allAreaList.get(i).getParentId();
                        currentCiryId = obtainViewModel().allAreaList.get(i).getId();
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

                obtainViewModel().fromMap = true;
                initAreaById(currentPro);
                initShiByShiId(currentProId, currentCiryId);
            }

            binding.addressView.setText(obtainViewModel().cityAddress);
            binding.jingduView.setText(doubles[1]+"");
            binding.weiduView.setText(doubles[0]+"");
        }

    }

    private void initAreaById(String shengName) {
        obtainViewModel().shengStrList.clear();
        obtainViewModel().shengList.clear();
        obtainViewModel().shengStrList.add("请选择省");
        for (int i = 0; i < obtainViewModel().allAreaList.size(); i++) {
            if ("1".equals(obtainViewModel().allAreaList.get(i).getAreaLevel())) {
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



    /**
     * 显示地区选择的dialog
     */
    private void showAreaDialog(List<String> strList) {
        View areaView = LayoutInflater.from(this).inflate(R.layout.dialog_area, null);
        obtainViewModel().areaWy = ((WheelView) areaView.findViewById(R.id.wheel_view_area));
        obtainViewModel().areaWy.setIsLoop(false);
        if (obtainViewModel().currentChooseArea == 0) {
            obtainViewModel().areaWy.setItems(strList, obtainViewModel().shengSelectIndex);//init selected position is 0 初始选中位置为0
        } else {
            obtainViewModel().areaWy.setItems(strList, obtainViewModel().shiSelectIndex);//init selected position is 0 初始选中位置为0
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
                    //选择剩要初始化市
                    binding.shiAddFireText.setText("请选择市");
                    obtainViewModel().currentChooseShi = "请选择市";
                    obtainViewModel().shiSelectIndex = 0;
                } else {                          //选择市
                    obtainViewModel().currentChooseShi = obtainViewModel().areaWy.getSelectedItem();
                    obtainViewModel().shiSelectIndex = obtainViewModel().areaWy.getSelectedPosition();
                    binding.shiAddFireText.setText(obtainViewModel().currentChooseShi);
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

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        obtainViewModel().year = year;
        obtainViewModel().month = monthOfYear;
        obtainViewModel().day = dayOfMonth;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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