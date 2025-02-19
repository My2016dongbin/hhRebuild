package com.haohai.platform.fireforestplatform.old.linyi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;
import com.haohai.platform.fireforestplatform.ui.bean.FlameListModel;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.FlameList;
import com.haohai.platform.fireforestplatform.ui.multitype.FlameListViewBinder;
import com.haohai.platform.fireforestplatform.utils.CommonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class FlamePlanActivity extends HhBaseActivity implements FlameListViewBinder.OnFlameListItemClick {
    private ImageView iv_back;
    private ImageView iv_add;
    private String TAG = FlamePlanActivity.class.getSimpleName();
    private SwipeRefreshLayout flame_refresh_layout;
    private RecyclerView flame_listview;
    private List<Object> items = new ArrayList<>();
    private List<FlameList> flameLists = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flame_plan);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                onBackPressed();
            }
        });

        init_();
        bind_();
    }

    private void bind_() {
        RxViewAction.clickNoDouble(iv_add).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                Intent intent = new Intent(FlamePlanActivity.this,FlameAddActivity.class);
                startActivity(intent);
            }
        });
    }

    private void postData() {
        flameLists.clear();
        FlameListModel frameListModel = new FlameListModel(currentPage,10,new FlameListModel.Dto("","",""));
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "fire/api/planBurnOff/planBurnOffPage");
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType", "Internet");
        params.setBodyContent(new Gson().toJson(frameListModel));
        Log.e(TAG, "postData: " + new Gson().toJson(frameListModel) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray data = object.getJSONArray("data");
                    JSONObject model = (JSONObject) data.get(0);
                    JSONArray dataList = model.getJSONArray("dataList");
                    flameLists = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<FlameList>>() {
                    }.getType());

                    Log.e(TAG, "onSuccess: result.toString() = " + result );
                    Log.e(TAG, "onSuccess: flameLists.toString() = " + flameLists.toString() );
                    initData();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onSuccess: e" + e.toString() );
                    initData();
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

            }
        });
    }

    private void init_() {
        flame_refresh_layout = findViewById(R.id.flame_refresh_layout);
        flame_listview = findViewById(R.id.flame_listview);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        flame_listview.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);
        FlameListViewBinder flameListViewBinder = new FlameListViewBinder(this);
        flameListViewBinder.setListener(this);
        adapter.register(FlameList.class, flameListViewBinder);
        adapter.register(Empty.class, new EmptyViewBinder(this));
        flame_listview.setAdapter(adapter);
        assertHasTheSameAdapter(flame_listview, adapter);
        flame_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                flame_refresh_layout.setRefreshing(false);
                currentPage = 1;
                postData();
            }
        });
        flame_listview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentPage += 1;
                    postData();
                }
            }
        });
    }

    private void initData() {
        if(currentPage == 1){
            items.clear();
        }

        if (flameLists.size() == 0 && currentPage == 1){
            items.add(new Empty());
        }
        for (int i = 0; i < flameLists.size(); i++) {
            items.add(flameLists.get(i));
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void lookClick(FlameList flameList) {
        Intent intent = new Intent(FlamePlanActivity.this,FlameAreaActivity.class);
        intent.putExtra("look",true);
        intent.putExtra("point",flameList.getPosition());
        startActivity(intent);
    }


    @Override
    public void editClick(FlameList flameList) {
        Intent intent = new Intent(FlamePlanActivity.this,FlameAddActivity.class);
        intent.putExtra("edit",true);
        intent.putExtra("planId",flameList.getId());
        startActivity(intent);
    }

    @Override
    public void deleteClick(FlameList flameList) {
        showDeleteDialog("确定删除这条计划吗？",flameList);
    }

    private void showDeleteDialog(String msg, FlameList flameList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_player).setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        deleteing(flameList);
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
        builder.create().show();
    }

    private void deleteing(FlameList flameList) {
        String strList = "[\""+flameList.getId()+"\"]";
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "fire/api/planBurnOff/deletePlanBurnOff");
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType", "Internet");
        params.setBodyContent(strList);
        Log.e(TAG, "deleteing: flame = " + strList );
        x.http().request(HttpMethod.DELETE,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code") == 200 ){
                        Toast.makeText(FlamePlanActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        postData();
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

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );
        postData();
    }
}