package com.haohai.platform.fireforestplatform.old.linyi;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.old.FireMapActivity;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;
import com.haohai.platform.fireforestplatform.ui.bean.Area;
import com.haohai.platform.fireforestplatform.ui.cell.MNCTransparentDialog;
import com.haohai.platform.fireforestplatform.ui.cell.TypeChooseDialog;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImageUtils;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import rx.functions.Action1;


public class AddResourceCheckingActivity extends HhBaseActivity implements DatePicker.OnDateChangedListener, TypeChooseDialog.TypeChooseDialogListener {
    private static final String TAG = AddResourceCheckingActivity.class.getSimpleName();
    public static final int MAP_REUEST_CODE = 2;
    private TextView tv_name;
    private EditText et_name;
    private TextView text_grid;
    private TextView text_grid2;
    private TextView text_grid3;
    private TextView tv_rowscreen;
    private LinearLayout ll_items;
    private LinearLayout ll_pictures;
    private FrameLayout fl_worker;
    private LinearLayout ll_worker_small;
    private FrameLayout fl_enddate;
    private ScrollView sv_out;
    private TextView tv_enddate;
    private EditText et_remark;
    private TextView tv_worker;
    private Button btn_left;
    private Button btn_right;
    private Button btn_submit;
    private LinePathView path_view;
    private ProgressDialog progressDialog;
    private LinearLayout ll_tomap;
    public int quSelectIndex = 0;
    public int jieDaoSelectIndex = 0;
    public boolean isChooseQu = false;
    private TextView tvAddress;
    private int year;
    private int month;
    private int day;
    private String CurrentTime;
    private StringBuffer date;
    private List<AddResourceCheck.ImgsFirejd> imgsFirejds1 =new ArrayList<>();
    private List<checkuser> checkusers =new ArrayList<>();

    public List<Area> gridAllList = new ArrayList<>();
    public List<Area> gridList = new ArrayList<>();
    public List<Area> grid2List = new ArrayList<>();
    public List<Area> grid3List = new ArrayList<>();
    public int gridIndex = 0;
    public int grid2Index = 0;
    public int grid3Index = 0;
    public int chooseType = 1;
    private TypeChooseDialog typeChooseDialog;

    private double currentLongitude;
    private double currentLatitude;
    private String currentStreeNo = "";
    private String currentGroupId;
    private ImageView left_icon;
    private ImageView right_image;
    private TextView left;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主题色白色 黑字
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.whiteColor));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.add_activity_resource_checking);
        progressDialog = new ProgressDialog(this);

        date=new StringBuffer();
        initView();
        postData();
        initPictures();
        getLocation();
        initDateTime();
    }

    private List<Pictures> picturesList = new ArrayList<>();
    private int maxPicture = 5;
    private int picNumber = 0;//当前有几张
    private void initPictures() {
        ll_pictures.removeAllViews();
        //去空
        for (int i = 0; i < picturesList.size(); i++) {
            if(picturesList.get(i).getUri() == null){
                picturesList.remove(i);
            }
        }
        //补空
        if(picturesList.size() < maxPicture){
            picturesList.add(new Pictures());
        }
        //判断有几张图片
        if(picturesList.size() < maxPicture){
            picNumber = picturesList.size()-1;
        }else{
            if(picturesList.get(maxPicture-1).getUri() == null){
                picNumber = maxPicture-1;
            }else{
                picNumber = maxPicture;
            }
        }

        for (int i = 0; i < picturesList.size(); i++) {
            int current = i;
            if(picturesList.get(i).getUri() == null){
                //空添加
                View view = View.inflate(this, R.layout.pictures_item_empty, null);
                ImageView iv_empty = view.findViewById(R.id.iv_empty);
                RxViewAction.clickNoDouble(iv_empty).subscribe(unused -> {
                    int size = maxPicture - picNumber;
                    Matisse.from(AddResourceCheckingActivity.this)
                            .choose(MimeType.ofAll())
                            .countable(true)
                            .capture(true)
                            .captureStrategy(
                                    new CaptureStrategy(true,"com.haohai.platform.fireforestplatform")
                            )
                            .maxSelectable(size)
                            .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                            .gridExpectedSize(
                                    getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .forResult(123);
                });
                ll_pictures.addView(view);
                return;
            }else{
                //满展示
                View view = View.inflate(this, R.layout.pictures_item, null);
                ImageView iv_show = view.findViewById(R.id.iv_show);
                ImageView iv_delete = view.findViewById(R.id.iv_delete);
                iv_show.setImageURI(picturesList.get(current).getUri());
                RxViewAction.clickNoDouble(iv_show).subscribe(unused -> {
                    //show
                });
                RxViewAction.clickNoDouble(iv_delete).subscribe(unused -> {
                    picturesList.remove(current);
                    initPictures();
                });
                ll_pictures.addView(view);
            }
        }
    }

    /*
     * activity在配置改变时执行
     * 比如横竖屏幕的切换，键盘有无的切换，屏幕大小的改变
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("TAG", "onConfigurationChanged");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            List<Uri> uriList = Matisse.obtainResult(data);
            Log.e(TAG, "onActivityResult: = uriList.size()"   +uriList.size());
            Log.e(TAG, "onActivityResult: uriList.toString() = "   +uriList.toString());
            for (int i = 0; i < uriList.size(); i++) {
                Pictures picture = new Pictures();
                picture.setUri(uriList.get(i));
                picturesList.add(picture);
            }
            initPictures();
        }else if (requestCode == MAP_REUEST_CODE && resultCode == MAP_REUEST_CODE) {
            /*double[] doubles = new LatLngChangeNew().calBD09toWGS84(Double.parseDouble(data.getStringExtra("latitude")), Double.parseDouble(data.getStringExtra("longitude")));
            currentLongitude = doubles[1];
            currentLatitude = doubles[0];*/
            currentLatitude = Double.parseDouble(data.getStringExtra("latitude"));
            currentLongitude = Double.parseDouble(data.getStringExtra("longitude"));

            String lat = currentLatitude+"";
            String lng = currentLongitude+"";
            try{
                lat = lat.substring(0,10);
                lng = lng.substring(0,10);
            }catch (Exception e){
            }
            tvAddress.setText(lng+","+lat);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean cityRoot;
    private void initView() {
        cityRoot = true;//账号权限
        left_icon = findViewById(R.id.left_icon);
        left = findViewById(R.id.left);
        title = findViewById(R.id.title);
        right_image = findViewById(R.id.right_image);
        title.setText("隐患排查");
        right_image.setVisibility(View.VISIBLE);
        right_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
        left_icon.setOnClickListener(v -> {
            finish();
        });
        left.setOnClickListener(v -> {
            finish();
        });
        right_image.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), HiddenDangerListActivity.class));
        });
        tv_name = findViewById(R.id.tv_name);
        et_name = findViewById(R.id.et_name);
        text_grid = findViewById(R.id.text_grid);
        text_grid2 = findViewById(R.id.text_grid2);
        text_grid3 = findViewById(R.id.text_grid3);
        tv_rowscreen = findViewById(R.id.tv_rowscreen);
        ll_pictures = findViewById(R.id.ll_pictures);
        tv_enddate = findViewById(R.id.tv_enddate);
        et_remark = findViewById(R.id.et_remark);
        tv_worker = findViewById(R.id.tv_worker);
        btn_left = findViewById(R.id.btn_left);
        btn_right = findViewById(R.id.btn_right);
        btn_submit = findViewById(R.id.btn_submit);
        path_view = findViewById(R.id.path_view);
        fl_worker = findViewById(R.id.fl_worker);
        ll_worker_small = findViewById(R.id.ll_worker_small);
        fl_enddate = findViewById(R.id.fl_enddate);
        sv_out = findViewById(R.id.sv_out);
        tvAddress = findViewById(R.id.tv_address);
        ll_tomap = findViewById(R.id.ll_tomap);

        RxViewAction.clickNoDouble(btn_left).subscribe(unused -> {
            path_view.clear();
            path_view.setOnlyWatch(false);

            btn_right.setClickable(true);
            btn_right.setBackground(getDrawable(R.drawable.btn_theme));
        });
        RxViewAction.clickNoDouble(btn_right).subscribe(unused -> {
            if(path_view.getTouched()){
                path_view.setOnlyWatch(true);
                Toast.makeText(this, "签名已确认", Toast.LENGTH_SHORT).show();

                btn_right.setClickable(false);
                btn_right.setBackground(getDrawable(R.drawable.btn_theme_gray));

               /* try {
                    path_view.save(getCacheDir().getAbsolutePath() + "/signs.png");
                    Uri parse = Uri.parse(getCacheDir().getAbsolutePath() + "/signs.png");
                    Log.e(TAG, "bingo getObbDir initView: " + parse.toString() );
                    signPictures.setType(2);
                    signPictures.setUri(parse);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                Bitmap bitMap = path_view.getBitMap();
                Log.e(TAG, "initView: MediaStore.Images.Media.insertImage(getContentResolver(), bitMap, null,null) = " + MediaStore.Images.Media.insertImage(getContentResolver(), bitMap, null,null) );
                Uri uris = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitMap, "title"+new Date().getTime(),"description"+new Date().getTime()));
                signPictures.setType(2);
                signPictures.setUri(uris);
            }else{
                Toast.makeText(this, "您还没有签名", Toast.LENGTH_SHORT).show();
            }
        });
        RxViewAction.clickNoDouble(tv_rowscreen).subscribe(unused -> {

        });


        RxViewAction.clickNoDouble(ll_tomap)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "call: " + currentLongitude);
                        Log.e(TAG, "call: " + currentLatitude);
                        /*Intent intent = new Intent(getApplicationContext(), FireMapActivity.class);
                        String listStr = new CoordinateConversion().wgs84tobd09(currentLongitude,currentLatitude);
                        String[] split = listStr.split(",");
                        Log.e(TAG, "call: bingo split = " +  split );
                        intent.putExtra("longitude_double", Double.parseDouble(split[0]));
                        intent.putExtra("latitude_double", Double.parseDouble(split[1]));
                        startActivityForResult(intent, MAP_REUEST_CODE);*/
                        Intent intent = new Intent(getApplicationContext(), FireMapActivity.class);
                        intent.putExtra("longitude_double", currentLongitude);
                        intent.putExtra("latitude_double", currentLatitude);
                        startActivityForResult(intent, MAP_REUEST_CODE);
                    }
                });

        RxViewAction.clickNoDouble(ll_worker_small).subscribe(unused -> {
//            if((!cityRoot) && (currentChooseQu.equals("")||currentChooseJiedao.equals("")||currentChooseQu.contains("请选择")||currentChooseJiedao.contains("请选择"))){
//                Toast.makeText(this, "请完善所属网格信息", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if(checkUserStrList.size()==0){
                Toast.makeText(this, "当前网格暂无整治人", Toast.LENGTH_SHORT).show();
                return;
            }
            showBottomDialog();
        });
        RxViewAction.clickNoDouble(btn_submit).subscribe(unused -> {
            if(!tv_worker.getText().toString().equals("点击选择")){
                showMessageDialog("确认提交审核吗？");
            }else {
                Toast.makeText(this, "请选择整治人", Toast.LENGTH_SHORT).show();
            }
        });
        RxViewAction.clickNoDouble(text_grid).subscribe(unused -> {
            if(gridList==null || gridList.isEmpty()){
                Toast.makeText(this, "网格数据加载中..", Toast.LENGTH_SHORT).show();
                return;
            }
            chooseType = 1;
            chooseGrid();
        });
        RxViewAction.clickNoDouble(text_grid2).subscribe(unused -> {
            if(grid2List==null || grid2List.isEmpty()){
                Toast.makeText(this, "请先选择省", Toast.LENGTH_SHORT).show();
                return;
            }
            chooseType = 2;
            chooseGrid();
        });
        RxViewAction.clickNoDouble(text_grid3).subscribe(unused -> {
            if(grid3List==null || grid3List.isEmpty()){
                Toast.makeText(this, "请先选择市", Toast.LENGTH_SHORT).show();
                return;
            }
            chooseType = 3;
            chooseGrid();
        });
        RxViewAction.clickNoDouble(tv_enddate).subscribe(unused -> {
            //showBottomDialog();
            showDataDialog(tv_enddate);
        });
    }


    private void chooseGrid(){
        typeChooseDialog = new TypeChooseDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = typeChooseDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        typeChooseDialog.setDialogListener(this);
        typeChooseDialog.setTreeList(parseStrings(),parseIndex(),1);
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

    private int parseIndex() {
        int index = 0;
        if(chooseType==1){
            index = gridIndex;
        }
        if(chooseType==2){
            index = grid2Index;
        }
        if(chooseType==3){
            index = grid3Index;
        }
        return index;
    }

    private List<String> parseStrings() {
        int type = chooseType;
        HhLog.e("parseStrings " + type);
        List<String> list = new ArrayList<>();
        if(type==1){
            for (int i = 0; i < gridList.size(); i++) {
                list.add(gridList.get(i).getName());
            }
        }
        if(type==2){
            for (int i = 0; i < grid2List.size(); i++) {
                list.add(grid2List.get(i).getName());
            }
        }
        if(type==3){
            for (int i = 0; i < grid3List.size(); i++) {
                list.add(grid3List.get(i).getName());
            }
        }
        HhLog.e("parseStrings " + type);
        HhLog.e("parseStrings " + list.toString());
        return list;
    }

    public void postData() {
        DbConfig dbConfig = new DbConfig(this);
        gridAllList = dbConfig.getGridList();
        if(gridAllList!=null){
            initArea();
        }
        else{
            HhHttp.get()
                    .url(URLConstant.GET_GRID)
                    .build()
                    .connTimeOut(10000)
                    .execute(new LoggedInStringCallback(null, this) {
                        @Override
                        public void onSuccess(String response, int id) {
                            HhLog.e("getGridData: " + URLConstant.GET_GRID);
                            HhLog.e("getGridData: " + CommonData.token);
                            HhLog.e("getGridData: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray data = jsonObject.getJSONArray("data");
                                gridAllList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<Area>>() {
                                }.getType());
                                //存储网格信息
                                DbManager db = dbConfig.getDbManager();
                                try {
                                    db.saveOrUpdate(gridList);
                                } catch (DbException e) {

                                }
                                initArea();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, Exception e, int id) {
                            HhLog.e("onFailure: " + e.toString());
                        }
                    });
        }
    }

    public void initArea() {
        gridList.clear();
        for (int i = 0; i < gridAllList.size(); i++) {
            if ("1".equals(gridAllList.get(i).getLevel())) {
                gridList.add(gridAllList.get(i));
            }
        }
    }
    public void initArea2() {
        HhLog.e("initArea2 " + gridList.get(gridIndex).toString());
        grid2List.clear();
        for (int i = 0; i < gridAllList.size(); i++) {
            if ("2".equals(gridAllList.get(i).getLevel()) && gridAllList.get(i).getParentId().equals(gridList.get(gridIndex).getId())) {
                grid2List.add(gridAllList.get(i));
            }
        }
    }
    public void initArea3() {
        grid3List.clear();
        HhLog.e("initArea3 " + grid2List.get(grid2Index).toString());
        for (int i = 0; i < gridAllList.size(); i++) {
            if ("3".equals(gridAllList.get(i).getLevel()) && gridAllList.get(i).getParentId().equals(grid2List.get(grid2Index).getId())) {
                grid3List.add(gridAllList.get(i));
            }
        }
    }


    private int postIndex = 0;
    public void showMessageDialog(String msg) {
        final MNCTransparentDialog mncTransDialog = new MNCTransparentDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tokendown, null, false);
        TextView message_text = (TextView) dialogView.findViewById(R.id.message_text);
        message_text.setText(msg);
        final TextView tv_right = (TextView) dialogView.findViewById(R.id.tv_right);
        final TextView tv_left = (TextView) dialogView.findViewById(R.id.tv_left);
        //确认
        RxViewAction.clickNoDouble(tv_right).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mncTransDialog.dismiss();
                if(picNumber == 0){
                    Toast.makeText(AddResourceCheckingActivity.this, "请至少上传一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(signPictures.getUri()==null){
                    Toast.makeText(AddResourceCheckingActivity.this, "您还没有签名", Toast.LENGTH_SHORT).show();
                    return;
                }

                postPicturesList.clear();
                postIndex = 0;
                for (int i = 0; i < picturesList.size(); i++) {
                    if(picturesList.get(i).getUri() == null ){
                        picturesList.remove(i);
                        break;
                    }
                }
                for (int i = 0; i < picturesList.size(); i++) {
                    postMoreImagesService(picturesList.get(i).getUri(),false);
                }
                if(signPictures.getUri()!=null){
                    postMoreImagesService(signPictures.getUri(),true);
                }
            }
        });
        //取消
        RxViewAction.clickNoDouble(tv_left).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mncTransDialog.dismiss();
            }
        });
        mncTransDialog.show();
        Window window = mncTransDialog.getWindow();//对话框窗口
        window.setGravity(Gravity.CENTER);//设置对话框显示在屏幕中间
        window.setWindowAnimations(R.style.dialog_style);//添加动画
        window.setContentView(dialogView);
    }




    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }


    public static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text, String name, String ctx,
                                           String text1, String name1, String ctx1,
                                           Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, paddingLeft, paddingTop, paint);
        canvas.drawText(name, paddingLeft, paddingTop+100, paint);
        canvas.drawText(ctx, paddingLeft, paddingTop+200, paint);

        canvas.drawText(text1, paddingLeft, paddingTop+300, paint);
        canvas.drawText(name1, paddingLeft, paddingTop+400, paint);
        canvas.drawText(ctx1, paddingLeft, paddingTop+500, paint);

        return bitmap;
    }

    private Bitmap evaluate;
    private String checkTime;
    private String name;
    private Paint paint;

    private void postMoreImagesService(Uri uri,boolean isSign){
        Log.e(TAG, "postMoreImagesService: bingo uri = " + uri);
        showDialogProgress(progressDialog,"请稍候...");

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);

        name = "post";
        checkTime = dateFormat.format(date);
        try {
            int degree = ImageUtils.readPictureDegree(uri.toString());
            Bitmap photo = ImageUtils.getBitmapFormUri(getApplicationContext(), uri);
            Bitmap shuiYinPhoto = drawTextToBitmap(this, photo,checkTime.replace("T"," ") + "  " + name, "", "", "", "", "", paint, 10, 40);
            evaluate = rotaingImageView(degree, shuiYinPhoto);

        } catch (IOException e) {

        }
        String savePhoto = ImageUtils.savePhoto(this.evaluate, this.getObbDir().getAbsolutePath(), checkTime + "pic" + new Random().nextInt(1000));

        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "oa/api/workReport/fileUploadAnByNotToken");
        params.setAsJsonContent(true);
        params.setMultipart(true);
        // params.setBodyContent(jsonObject.toString());
        params.addBodyParameter("file", new File(savePhoto),null,savePhoto);
        params.addHeader("Authorization","bearer " + CommonData.token);
        Log.e(TAG, "postimage: " + params );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "postimage: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    String message = jsonObject1.getString("message");
                    if (code.equals("200")){
                        JSONObject data = jsonObject1.getJSONObject("data");
                        JSONArray imgStrArray = data.getJSONArray("img");
                        String imageUrl = imgStrArray.get(0).toString();
                        if(!isSign){
                            Pictures pictures = new Pictures();
                            pictures.setUrl(imageUrl);
                            pictures.setType(1);
                            postPicturesList.add(pictures);
                            imgsFirejds1.add(new AddResourceCheck.ImgsFirejd(imageUrl,1));
                        }else {
                            imgsFirejds1.add(new AddResourceCheck.ImgsFirejd(imageUrl,2));
                        }
                        postIndex++;
                        if(postIndex >= picNumber+1){
//                            submit();
                            postDataToServiceFromDb1();
                        }
                    }else {
                        Toast.makeText(AddResourceCheckingActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {


            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }
    private List<Pictures> postPicturesList = new ArrayList<>();

    private String signStr = "";
    private Pictures signPictures = new Pictures();
    private List<String> imgStrList = new ArrayList<>();
    private void postDataToServiceFromDb1() {
        for (int i = 0; i <checkUserList.length() ; i++) {
            try {
                if (tv_worker.getText().toString().equals(checkUserList.getJSONObject(i).getString("fullName"))){
                    Log.e(TAG, "postDataToServiceFromDb1: "+ checkUserList.getJSONObject(i).getString("id"));
                    checkusers.add(new checkuser(null,2,2,checkUserList.getJSONObject(i).getString("id"),tv_worker.getText().toString()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AddResourceCheck addResourceCheck = new AddResourceCheck();
        addResourceCheck.setDescription(et_remark.getText().toString());
        //addResourceCheck.setResourceId(id);
        addResourceCheck.setEndTime(tv_enddate.getText().toString()+" 00:00:00");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        addResourceCheck.setStartTime(format);
        addResourceCheck.setImgs(imgsFirejds1);
        addResourceCheck.setCheckusers(checkusers);
        addResourceCheck.setCheckType(6);
        addResourceCheck.setStatus(3);
        addResourceCheck.setGroupId(currentGroupId);
        Area grid = new Area();
        if(!text_grid.getText().toString().contains("请选择")){
            grid = gridList.get(gridIndex);
        }
        if(!text_grid2.getText().toString().contains("请选择")){
            grid = grid2List.get(grid2Index);
        }
        if(!text_grid3.getText().toString().contains("请选择")){
            grid = grid3List.get(grid3Index);
        }
        addResourceCheck.setGridNo(grid.getId());
        addResourceCheck.setGridName(grid.getName());
        addResourceCheck.setName(et_name.getText().toString());
        //addResourceCheck.setResourceType(planResourceType);
        addResourceCheck.setLatitude(currentLatitude);
        addResourceCheck.setLongitude(currentLongitude);
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(addResourceCheck);
        Log.e(TAG, "postDataToServiceFromDb: "+json1 );
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/planResource/saveHiddenPerils" );
        params.setBodyContent(json1);
        params.addHeader("Authorization","bearer " + CommonData.token);
        params.setConnectTimeout(10000);
        Log.e(TAG, "postDataToServiceFromDb---" + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --数据上传成功--" + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    String message = jsonObject1.getString("message");
                    if (code.equals("200")){
                        Toast.makeText(AddResourceCheckingActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (code.equals("403")&&message.equals("没有指定资源项的特权")){
                        Toast.makeText(AddResourceCheckingActivity.this, "您没有权限", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddResourceCheckingActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: 请求失败" +ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }
    JSONArray checkUserList = new JSONArray();
    List<String> checkUserStrList = new ArrayList<>();
    private void getUsers() {
        JSONObject jsonObject = new JSONObject();
        try {
            /*if(cityRoot){
                jsonObject.put("gridNo", (currentStreeNo==null||currentStreeNo.equals(""))?currentQuNo:currentStreeNo);
            }else{
                jsonObject.put("gridNo", currentStreeNo);
            }
            Log.e(TAG, "bingo currentQuId = : " + currentQuId + "currentStreeNo = " +currentStreeNo );*/
            jsonObject.put("gridNo", "");
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "auth/api/auth/user/list");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + CommonData.token);
        Log.e(TAG, "resource: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --1-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        checkUserList = jsonObject1.getJSONArray("data");
                        for (int i = 0; i < checkUserList.length(); i++) {
                            JSONObject model = (JSONObject) checkUserList.get(i);
                            checkUserStrList.add(model.getString("fullName"));
                        }


                        if(checkUserStrList.size()==0){
                            Toast.makeText(AddResourceCheckingActivity.this, "当前网格暂无整治人", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private int checkUserIndex = 0;
    private void showBottomDialog() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.bottom_list, null);
        LinearLayout ll_list = view.findViewById(R.id.ll_list);
        for (int i = 0; i < checkUserStrList.size(); i++) {
            String str = checkUserStrList.get(i);
            View item = View.inflate(this, R.layout.bottom_list_item, null);
            TextView tv_title = item.findViewById(R.id.tv_title);
            tv_title.setText(str);
            RxViewAction.clickNoDouble(tv_title).subscribe(unused -> {
                tv_worker.setText(str);
                for (int j = 0; j < checkUserList.length(); j++) {
                    try {
                        JSONObject obj = (JSONObject) checkUserList.get(j);
                        if(Objects.equals(obj.getString("fullName"), str)){
                            checkUserIndex = j;
                            currentGroupId = obj.getString("groupId");
                            break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            });
            ll_list.addView(item);
        }

        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.AppTheme);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
        dialog.show();

    }
    public void getLocation() {
        //获得位置服务
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_HIGH);//低功耗

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.0001f, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double longitude = 0.00;
                double latitude = 0.00;
                try {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                } catch (Exception e) {

                }

                currentLongitude = longitude;
                currentLatitude = latitude;
                //   Toast.makeText(MainActivity.this, "经纬度发生改变了,经度" +longitude + "纬度" +latitude, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "GPS已开启", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "请打开GPS", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Toast.makeText(this, "请打开GPS和使用网络定位以提高精度", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        // 获取最好的定位方式
        String provider = locationManager.getBestProvider(criteria, true); // true 代表从打开的设备中查找

        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        // 测试一般都在室内，这里颠倒了书上的判断顺序
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            // 当没有可用的位置提供器时，弹出Toast提示用户
            Toast.makeText(this, "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();
            return;
        }


        //有位置提供器的情况
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // return null;
            }
            Location location= locationManager.getLastKnownLocation(provider);
            double longitude = 0.00;
            double latitude = 0.00;
            try {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }catch (Exception e){

            }


            currentLongitude = longitude ;
            currentLatitude = latitude ;
            Log.e(TAG, "getLocation: --" + longitude);
            Log.e(TAG, "getLocation: *--" + latitude);
         /*   BigDecimal   la   =   new BigDecimal(latitude);
            double   lat = la.setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue();*/
            //    return longitude + "," + latitude;
            //   return "0.00,0.00";
        }else {
            //  return "0.00,0.00";
        }
        tvAddress.setText(currentLongitude+","+currentLatitude);
    }
    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        int monthnow=month+1;
        CurrentTime = year + "/" + monthnow + "/" + day;
        Log.e(TAG, "initDateTime: "+CurrentTime );
//        tv_name.setText(CurrentTime+"随手拍");
    }
    private void showDataDialog(final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }

                date.append(String.valueOf(year));
                if (month < 9){
                    date.append("-0").append(String.valueOf(month + 1));
                }else {
                    date.append("-").append(String.valueOf(month + 1));
                }
                if (day <10){
                    date.append("-0").append(String.valueOf(day));
                } else {
                    date.append("-").append(String.valueOf(day));
                }
                textView.setText(date);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog  dialog = builder.create();
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

        //datePicker.setMaxDate(endTimre);
        datePicker.setMinDate(starTimre);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month, day, this);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    @Override
    public void onTypeChooseDialogRefresh() {

    }

    @Override
    public void onTypeChoose(String type, int index, int code) {
        if(code == 1){
            if(chooseType==1){
                //初始化市区
                text_grid2.setText("请选择市");
                grid2List.clear();
                grid2Index = 0;
                text_grid3.setText("请选择区");
                grid3List.clear();
                grid3Index = 0;


                text_grid.setText(type);
                gridIndex = index;
                getUsers();

                initArea2();
            }
            if(chooseType==2){
                //初始化区
                text_grid3.setText("请选择区");
                grid3List.clear();
                grid3Index = 0;


                text_grid2.setText(type);
                grid2Index = index;
                getUsers();

                initArea3();
            }
            if(chooseType==3){
                text_grid3.setText(type);
                grid3Index = index;
                getUsers();
            }
        }
    }
}