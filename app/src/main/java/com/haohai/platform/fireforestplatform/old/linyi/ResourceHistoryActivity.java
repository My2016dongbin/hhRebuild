package com.haohai.platform.fireforestplatform.old.linyi;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.rx.rxbinding.RxViewAction;
import com.haohai.platform.fireforestplatform.utils.CommonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class ResourceHistoryActivity extends AppCompatActivity implements ResourceHistoryViewBinder.OnResourceHistoryItemClick {
    String TAG = ResourceHistoryActivity.class.getSimpleName();
    ImageView back_button;
    RecyclerView rlv;
    String resourceId;
    private List<Object> historyItems = new ArrayList<>();
    private List<JSONObject> historyList =new ArrayList<JSONObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_history);

        resourceId = getIntent().getStringExtra("resourceId");

        initView();
        bindView();
        initData();
    }

    private void bindView() {
        RxViewAction.clickNoDouble(back_button).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                finish();
            }
        });
    }

    private void initData() {
        postData();
    }


    private void postData() {
        historyList.clear();
        historyItems.clear();
        adapter.notifyDataSetChanged();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resourceId", resourceId);
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/planResourceItemHistory/selectHistoryAndImg");
        params.addParameter("resourceId", resourceId);
        Log.e(TAG, "onSuccess: --bingo- params: " + jsonObject.toString() );
        params.addHeader("Authorization", "bearer " + CommonData.token);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --bingo-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = (JSONObject) data.get(i);
                            historyList.add(obj);
                        }
                        upDataData();
                        Log.e(TAG, "onSuccess: bingo" + historyList.toString());
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

    private void upDataData() {
        historyItems.clear();
        for (int i = 0; i < historyList.size(); i++) {
            historyItems.add(historyList.get(i));
        }
        assertAllRegistered(adapter,historyItems);
        adapter.notifyDataSetChanged();
    }

    private MultiTypeAdapter adapter;
    private ResourceHistoryViewBinder resourceHistoryViewBinder;
    private void initView() {
        back_button = findViewById(R.id.back_button);
        rlv = findViewById(R.id.rlv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rlv.setLayoutManager(linearLayoutManager);
        rlv.setHasFixedSize(true);
        rlv.setNestedScrollingEnabled(false);
        adapter = new MultiTypeAdapter(historyItems);


        resourceHistoryViewBinder = new ResourceHistoryViewBinder();
        resourceHistoryViewBinder.setListener(this,ResourceHistoryActivity.this);
        adapter.register(JSONObject.class, resourceHistoryViewBinder);

        rlv.setAdapter(adapter);
        assertHasTheSameAdapter(rlv, adapter);
    }

    @Override
    public void onImageClick(JSONObject object, String url) {

    }
}