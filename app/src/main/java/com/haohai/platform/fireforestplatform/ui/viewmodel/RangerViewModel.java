package com.haohai.platform.fireforestplatform.ui.viewmodel;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.old.HistoryLineActivity;
import com.haohai.platform.fireforestplatform.ui.activity.RangerActivity;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.RangerGrid;
import com.haohai.platform.fireforestplatform.ui.multitype.ResourceType;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class RangerViewModel extends BaseViewModel {
    public Context context;
    public void start(Context context){
        this.context = context;
    }
    public MultiTypeAdapter adapter;
    public List<RangerGrid> rangerGridList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();


    public void barLeftClick(View v){
        ((RangerActivity)context).finish();
    }

    public void getTrees(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
//        HhHttp.get()
        HhHttp.postString()
                .url(URLConstant.POST_GRID_TREES)
                .content(new Gson().toJson(new CommonParams("1",new ArrayList<>())))
                .build()
                .connTimeOut(20000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        Log.e("","getTrees " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            rangerGridList = new Gson().fromJson(String.valueOf(data),
                                    new TypeToken<List<RangerGrid>>(){}.getType());
                            updateData();
                            test_();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                    }
                });
    }

    private void test_() {
//        context.startActivity(new Intent(context, HistoryLineActivity.class));
    }

    public void updateData() {
        items.clear();
        if (rangerGridList != null && rangerGridList.size()!=0) {
            items.addAll(rangerGridList);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
