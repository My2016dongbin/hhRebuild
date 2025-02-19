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
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.FireMapActivity;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;
import com.haohai.platform.fireforestplatform.ui.cell.MNCTransparentDialog;
import com.haohai.platform.fireforestplatform.ui.cell.WheelView;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.GifSizeFilter;
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

import rx.functions.Action1;


public class AddResourceCheckingActivity extends HhBaseActivity implements DatePicker.OnDateChangedListener{
    private static final String TAG = AddResourceCheckingActivity.class.getSimpleName();
    public static final int MAP_REUEST_CODE = 2;
    private TextView tv_name;
    private EditText et_name;
    private TextView tv_grid;
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
    private final boolean isShowDialog = true;
    private String endDateStr = "";
    private Dialog shaixuanDialog;
    private View shaixuanInflater;
    private TextView okButton;
    private ImageView to_map_view;
    private LinearLayout ll_tomap;
    private LinearLayout quLayout;
    private ImageView iv_city;
    private LinearLayout jiedaoLayout;
    public int currentChooseArea = 0;  //当前在选择区还是街道   0选择区  1选择街道
    public String currentChooseQu = "";
    public String currentChooseJiedao = "";
    public String currentQuId;
    public String currentQuNo;
    public List<Grid> quList;
    private TextView quText;
    private TextView jiedaoText;
    public List<String> quStrList;
    public List<Grid> jiedaoList;
    public List<String> jiedaoStrList;
    private WheelView areaWy;
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

    private String id;
    private String endDate;
    private int checkType;
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

        quList = new ArrayList<>();
        quStrList=new ArrayList<>();
        jiedaoList=new ArrayList<>();
        jiedaoStrList=new ArrayList<>();
        date=new StringBuffer();
        initView();
        getAllQu();
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
        tv_grid = findViewById(R.id.tv_grid);
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
        to_map_view = findViewById(R.id.to_map_view);
        ll_tomap = findViewById(R.id.ll_tomap);

        shaixuanDialog=new Dialog(this, R.style.ActionSheetDialogStyle);
        shaixuanInflater= LayoutInflater.from(this).inflate(R.layout.dialog_yh_wangge,null);
        shaixuanInflater.setMinimumWidth(10000);
        shaixuanDialog.setContentView(shaixuanInflater);
        Window gaojiDialogWindow = shaixuanDialog.getWindow();
        gaojiDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams gaojiLp = gaojiDialogWindow.getAttributes();
        WindowManager wmGaoji = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int heightGaoji = wmGaoji.getDefaultDisplay().getHeight();
        gaojiLp.height = (int) (heightGaoji * 0.8);
        gaojiDialogWindow.setAttributes(gaojiLp);
        shaixuanDialog.setCanceledOnTouchOutside(true);
        okButton=shaixuanInflater.findViewById(R.id.ok_button);
        quLayout=shaixuanInflater.findViewById(R.id.qu_layout);
        iv_city=shaixuanInflater.findViewById(R.id.iv_city);
        jiedaoLayout=shaixuanInflater.findViewById(R.id.jiedao_layout);
        quText=shaixuanInflater.findViewById(R.id.qu_text);
        jiedaoText=shaixuanInflater.findViewById(R.id.jiedao_text);
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
            if((!cityRoot) && (currentChooseQu.equals("")||currentChooseJiedao.equals("")||currentChooseQu.contains("请选择")||currentChooseJiedao.contains("请选择"))){
                Toast.makeText(this, "请完善所属网格信息", Toast.LENGTH_SHORT).show();
                return;
            }
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
        RxViewAction.clickNoDouble(quLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 0;

                        showAreaDialog(quStrList);
                    }
                });
        RxViewAction.clickNoDouble(jiedaoLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 1;
                        for (int i = 0; i < quList.size(); i++) {
                            if (quList.get(i).getName().equals(currentChooseQu)) {
                                currentQuId = quList.get(i).getId();
                                currentQuNo = quList.get(i).getGridNo();
                            }
                        }
                        if (quText.getText().equals("请选择区")){
                            Toast.makeText(AddResourceCheckingActivity.this, "请先选择区", Toast.LENGTH_SHORT).show();

                        }else {
                            getAllJieDao();
                        }

                    }
                });
        RxViewAction.clickNoDouble(okButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if((currentChooseQu==null||currentChooseQu.equals("")||currentChooseQu.contains("请选择")) && (currentChooseJiedao==null||currentChooseJiedao.equals("")||currentChooseJiedao.contains("请选择"))){
                            //tv_grid.setText("临沂市");
                            Toast.makeText(AddResourceCheckingActivity.this, "请选择区", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(cityRoot){
                            tv_grid.setText(currentChooseQu);
                        }else{
                            tv_grid.setText(currentChooseQu+"/"+currentChooseJiedao);
                        }
                        for (int i = 0; i < jiedaoList.size(); i++) {
                            Log.e(TAG, "call: " + jiedaoList.get(i).getName() );
                            Log.e(TAG, "call: " + jiedaoList.get(i).getGridNo());
                            if (jiedaoList.get(i).getName().equals(currentChooseJiedao)) {
                                currentStreeNo = jiedaoList.get(i).getGridNo();
                            }
                        }

                        checkUserStrList.clear();
                        checkUserList = new JSONArray();
                        //根据账号权限获取整治人列表
                        if(cityRoot){
                            getPerpleListFromService1();
                        }else{
                            if((!currentChooseQu.contains("请选择")) && (!currentChooseJiedao.contains("请选择")) && (!currentChooseQu.equals("")) && (!currentChooseJiedao.equals(""))){
                                getPerpleListFromService1();
                            }else{
                                Toast.makeText(AddResourceCheckingActivity.this, "请完善所属网格信息", Toast.LENGTH_SHORT).show();
                            }
                        }
                        shaixuanDialog.dismiss();
                    }
                });
        RxViewAction.clickNoDouble(tv_grid).subscribe(unused -> {
            //showBottomDialog();
            shaixuanDialog.show();
        });
        RxViewAction.clickNoDouble(tv_enddate).subscribe(unused -> {
            //showBottomDialog();
            showDataDialog(tv_enddate);
        });
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
                    checkusers.add(new checkuser(id,2,2,checkUserList.getJSONObject(i).getString("id"),tv_worker.getText().toString()));
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
        if(cityRoot){
            addResourceCheck.setGridNo((currentStreeNo==null||currentStreeNo.equals(""))?currentQuNo:currentStreeNo);
        }else{
            addResourceCheck.setGridNo(currentStreeNo);
        }
        addResourceCheck.setGroupId(currentGroupId);
        if(cityRoot){
            addResourceCheck.setGridName((currentStreeNo==null||currentStreeNo.equals(""))?currentChooseQu:currentChooseJiedao);
        }else{
            addResourceCheck.setGridName(currentChooseJiedao);
        }
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
    private void getPerpleListFromService1() {
        JSONObject jsonObject = new JSONObject();
        try {
            if(cityRoot){
                jsonObject.put("gridNo", (currentStreeNo==null||currentStreeNo.equals(""))?currentQuNo:currentStreeNo);
            }else{
                jsonObject.put("gridNo", currentStreeNo);
            }
            Log.e(TAG, "bingo currentQuId = : " + currentQuId + "currentStreeNo = " +currentStreeNo );
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
    /**
     * 获取所有区的数据
     */
    private void getAllQu() {

        quList.clear();
        DbManager db = new DbConfig(getApplicationContext()).getDbManager();
        try {
            quList = db.selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .and("level", "=", "2")
                    .and("gridno", "like", "3702%")
                    .findAll();

            quStrList.clear();

            //Log.e(TAG, "getAllQu: "+quList.size() );
            quStrList.add("请选择区");
            for (int i = 0; i < quList.size(); i++) {
                if (!quList.get(i).getName().equals("高新区")) {
                    quStrList.add(quList.get(i).getName());
                }
            }


            if(!cityRoot){
                for (int i = 0; i < quList.size(); i++) {
                    if(quList.get(i).getGridNo().contains((CharSequence) SPUtils.get(this, SPValue.gridNo,""))){
                        currentQuNo = quList.get(i).getGridNo();
                        currentQuId = quList.get(i).getId();
                        currentChooseQu = quList.get(i).getName();
                        quText.setText(currentChooseQu);
                        quText.setTextColor(getResources().getColor(R.color.text_color9));
                        quLayout.setClickable(false);
                        iv_city.setVisibility(View.GONE);
                    }
                }
            }else{
                jiedaoLayout.setVisibility(View.GONE);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取所有街道数据
     */
    private void getAllJieDao() {
        jiedaoList.clear();
        DbManager db = new DbConfig(getApplicationContext()).getDbManager();
        try {
            jiedaoList = db.selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .where("level", "=", "1")
                    .where("parentid", "=", currentQuId)
                    .findAll();

            jiedaoStrList.clear();
            jiedaoStrList.add("请选择街道");
            for (int i = 0; i < jiedaoList.size(); i++) {
                jiedaoStrList.add(jiedaoList.get(i).getName());
            }
            showAreaDialog(jiedaoStrList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    private void showAreaDialog(List<String> strList) {
        View areaView = LayoutInflater.from(this).inflate(R.layout.dialog_area, null);
        areaWy = ((WheelView) areaView.findViewById(R.id.wheel_view_area));
        areaWy.setIsLoop(false);
        if (currentChooseArea == 0){
            areaWy.setItems(strList, quSelectIndex);//init selected position is 0 初始选中位置为0
        }else {
            areaWy.setItems(strList, jieDaoSelectIndex);//init selected position is 0 初始选中位置为0
        }

        areaWy.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                if (currentChooseArea == 0){   //选择区
                    isChooseQu = true;
                    currentChooseQu = areaWy.getSelectedItem();
                    quSelectIndex = areaWy.getSelectedPosition();
                    quText.setText(currentChooseQu);
                    currentChooseJiedao = "";
                    jiedaoText.setText("请选择街道");
                    jieDaoSelectIndex = 0;
                    if(quSelectIndex-1 >= 0){
                        currentQuNo = quList.get(quSelectIndex-1).getGridNo();
                        currentQuId = quList.get(quSelectIndex-1).getId();
                    }else{
                        currentQuNo = "";
                    }
                }else {                          //选择街道
                    currentChooseJiedao = areaWy.getSelectedItem();
                    jieDaoSelectIndex = areaWy.getSelectedPosition();
                    jiedaoText.setText(currentChooseJiedao);
                    if(jieDaoSelectIndex-1 >= 0){
                        currentStreeNo = jiedaoList.get(jieDaoSelectIndex-1).getGridNo();
                    }else{
                        currentStreeNo = "";
                    }
                }

            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请选择区域")
                .setView(areaView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String areStr = "";
                        String area = areaWy.getSelectedItem();

                    }
                })
                .show();
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
}