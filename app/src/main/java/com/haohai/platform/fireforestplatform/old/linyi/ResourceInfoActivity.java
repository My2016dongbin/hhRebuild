package com.haohai.platform.fireforestplatform.old.linyi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.utils.CommonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceInfoActivity extends HhBaseActivity {
    private static final String TAG = ResourceInfoActivity.class.getSimpleName();
    private TextView tv_name;
    private TextView tv_grid;
    private TextView tv_location;
    private TextView tv_checkdate;
    private TextView tv_worker;
    private TextView tv_playdate;
    private TextView tv_desc;
    private ImageView iv_sign1;
    private ImageView iv_sign2;
    private LinearLayout ll_hidden;
    private LinearLayout ll_play;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private final boolean isShowDialog = true;
    private ImageView left_icon;
    private ImageView right_image;
    private TextView left;
    private TextView title;

    private String id;

    private JSONObject data = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_info);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        progressDialog = new ProgressDialog(this);

        initView();
        getDataFromService();
    }


    private void getDataFromService() {
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObjectBody.put("resourceId",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/planResource/getResourceData");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObjectBody.toString());
        params.addBodyParameter("resourceId",id);

        Log.e(TAG, "getDataFromService: params = " + params);
        Log.e(TAG, "getDataFromService: jsonObjectBody = " + jsonObjectBody.toString());
        params.addHeader("Authorization", "bearer " + CommonData.token);

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: hiddenDangerList" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code")==200){
                        JSONArray array = object.getJSONArray("data");
                        data = (JSONObject) array.get(0);
                        String createUser = data.getString("createUser");
                        tv_name.setText(data.getString("name"));
                        tv_grid.setText(data.getString("gridName"));
                        tv_location.setText(data.getString("longitude")+","+data.getString("latitude"));
                        tv_checkdate.setText(data.getString("createTime").substring(0,19).replace("T"," ").replace("null",""));
                        JSONArray checkusers = data.getJSONArray("checkusers");
                        if(checkusers!=null && checkusers.length()!=0){
                            for (int i = 0; i < checkusers.length(); i++) {
                                JSONObject users = (JSONObject) checkusers.get(i);
                                if(!Objects.equals(users.getString("userName"), createUser)){
                                    tv_worker.setText(users.getString("userName"));
                                }
                            }
                        }else{
                            tv_worker.setText("暂无");
                        }
                        JSONArray images = data.getJSONArray("imgs");
                        for (int i = 0; i < images.length(); i++) {
                            JSONObject obj = (JSONObject) images.get(i);
                            if(Objects.equals(obj.getString("type"), "1")){//检查
                                picturesHiddenList.add(obj.getString("img"));
                            }else if(Objects.equals(obj.getString("type"), "2")){//签名
                                picturesSignList.add(obj.getString("img"));
                            }
                            else{//整改
                                picturesPlayList.add(obj.getString("img"));
                            }
                        }
                        initTest();

                        tv_playdate.setText(new CommonUtils().parseDate(data.getString("regulationTime")));
                        JSONArray items = data.getJSONArray("items");
                        JSONObject item = (JSONObject) items.get(0);
                        tv_desc.setText(item.getString("description").replace("null",""));

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
                progressDialog.dismiss();
            }
        });
    }

    private List<String> picturesSignList = new ArrayList<>();
    private List<String> picturesHiddenList = new ArrayList<>();
    private List<String> picturesPlayList = new ArrayList<>();
    private void initView() {
        left_icon = findViewById(R.id.left_icon);
        left = findViewById(R.id.left);
        title = findViewById(R.id.title);
        title.setText("资源点详情");
        left_icon.setOnClickListener(v -> {
            finish();
        });
        left.setOnClickListener(v -> {
            finish();
        });
        tv_name = findViewById(R.id.tv_name);
        tv_grid = findViewById(R.id.tv_grid);
        tv_location = findViewById(R.id.tv_location);
        tv_checkdate = findViewById(R.id.tv_checkdate);
        tv_worker = findViewById(R.id.tv_worker);
        tv_playdate = findViewById(R.id.tv_playdate);
        tv_desc = findViewById(R.id.tv_desc);
        iv_sign1 = findViewById(R.id.iv_sign1);
        iv_sign2 = findViewById(R.id.iv_sign2);

        ll_hidden = findViewById(R.id.ll_hidden);
        ll_play = findViewById(R.id.ll_play);

        swipeRefreshLayout = findViewById(R.id.sr_resourceinfo);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(() -> {
            ll_hidden.removeAllViews();
            ll_play.removeAllViews();
            picturesSignList.clear();
            picturesPlayList.clear();
            picturesHiddenList.clear();
            getDataFromService();
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTest() {

        for (int i = 0; i < picturesSignList.size(); i++) {
            if(i == 0){
                Glide.with(this).load(picturesSignList.get(i)).into(iv_sign1);
            }
            if(i == 1){
                Glide.with(this).load(picturesSignList.get(i)).into(iv_sign2);
            }
        }
        for (int i = 0; i < picturesHiddenList.size(); i++) {
            View item = LayoutInflater.from(this).inflate(R.layout.resource_info_picture_item, null, false);
            ImageView iv_picture = item.findViewById(R.id.iv_picture);
            Glide.with(this).load(picturesHiddenList.get(i)).into(iv_picture);
            ll_hidden.addView(item);
        }
        for (int i = 0; i < picturesPlayList.size(); i++) {
            View item = LayoutInflater.from(this).inflate(R.layout.resource_info_picture_item, null, false);
            ImageView iv_picture = item.findViewById(R.id.iv_picture);
            Glide.with(this).load(picturesPlayList.get(i)).into(iv_picture);
            ll_play.addView(item);
        }
    }
}