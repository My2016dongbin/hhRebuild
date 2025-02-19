package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.ContactsActivity;
import com.haohai.platform.fireforestplatform.ui.activity.NewsActivity;
import com.haohai.platform.fireforestplatform.ui.bean.ContactsArea;
import com.haohai.platform.fireforestplatform.ui.multitype.Contacts;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.zhy.http.okhttp.builder.GetBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class ContactsViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<Contacts> newsList = new ArrayList<>();
    public List<ContactsArea> areaList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public int page = 1;
    public final int limit = 10;
    public String name;
    public String groupName;
    public String groupNo;
    public final MutableLiveData<Integer> loadMore = new MutableLiveData<>();
    public final MutableLiveData<Double> areaStatus = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }


    public void barLeftClick(View v) {
        ((ContactsActivity) context).finish();
    }

    public void postData() {
        loading.setValue(new LoadingEvent(true, "加载中.."));
        GetBuilder getBuilder = HhHttp.get()
                .url(URLConstant.GET_CONTACTS_PAGE)
                .addParams("pageNum", page + "")
                .addParams("pageSize", limit + "");
        if(name!=null){
           getBuilder.addParams("name",name);
        }
        if(groupName!=null){
           getBuilder.addParams("groupName",groupName);
           getBuilder.addParams("groupNo",groupNo);
        }
        getBuilder
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_CONTACTS_PAGE " + page + ", " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray rows = jsonObject.getJSONArray("rows");
                            newsList = new Gson().fromJson(String.valueOf(rows), new TypeToken<List<Contacts>>() {
                            }.getType());
                            if (page == 0) {
                                loadMore.postValue(1);
                            }
                            if (newsList.size() < limit) {
                                loadMore.postValue(0);
                            }
                            updateData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            HhLog.e("error " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        loading.setValue(new LoadingEvent(false));
                    }
                });

    }

    public void getAreas(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SPUtils.get(context, SPValue.token,""));
        HhHttp.get()
                .url(URLConstant.GET_CONTACTS_AREA)
                .headers(headers)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_CONTACTS_AREA " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray rows = jsonObject.getJSONArray("data");
                            areaList = new Gson().fromJson(String.valueOf(rows), new TypeToken<List<ContactsArea>>() {
                            }.getType());
                            areaStatus.postValue(Math.random());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            HhLog.e("error " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {

                    }
                });
    }


    public void updateData() {
        if (page == 1) {
            items.clear();
        }
        if (newsList != null && newsList.size() != 0) {
            items.addAll(newsList);
        } else {
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
