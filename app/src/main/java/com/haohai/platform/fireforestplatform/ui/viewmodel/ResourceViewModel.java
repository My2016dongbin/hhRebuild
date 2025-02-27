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
import com.haohai.platform.fireforestplatform.ui.activity.ResourceSearchActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.Grid;
import com.haohai.platform.fireforestplatform.ui.multitype.Resource;
import com.haohai.platform.fireforestplatform.ui.multitype.ResourceType;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class ResourceViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<Resource> newsList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public int page = 1;
    public final int limit = 5;
    public final MutableLiveData<Integer> loadMore = new MutableLiveData<>();
    public List<Grid> gridGridList = new ArrayList<>();
    public String gridNo;
    public String level = "4";//区3/街道4/社区5
    public String apiUrl = "";
    public ResourceType resourceType;
    public final MutableLiveData<List<Grid>> gridList = new MutableLiveData<>();
    public final MutableLiveData<List<ResourceType>> resourceTypeList = new MutableLiveData<>();
    public final MutableLiveData<List<Resource>> resourceList = new MutableLiveData<>();
    public List<Resource> resources = new ArrayList<>();
    public List<ResourceType> resourceTypes = new ArrayList<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((ResourceSearchActivity)context).finish();
    }

    public void postType(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        resourceTypeList.postValue(new ArrayList<>());
        resourceTypes = new ArrayList<>();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("isDisplay", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HhLog.e("resourceType params " + jsonObject.toString());
        HhHttp.postString()
                .url(URLConstant.POST_MAP_RESOURCE_TYPE)
                .content(jsonObject.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            List<ResourceType> value = resourceTypeList.getValue();
                            if(value == null ){
                                value = new ArrayList<>();
                            }
                            List<ResourceType> c = new Gson().fromJson(String.valueOf(data),
                                    new TypeToken<List<ResourceType>>() {
                                    }.getType());
                            c.addAll(value);
                            resourceTypeList.postValue(c);
                            resourceTypes = c;




                            /*//新增资源点类型
                            final JSONObject obj = new JSONObject();
                            try {
                                obj.put("groupId", SPUtils.get(context, SPValue.groupId,"001021"));
                                obj.put("isDisplay", "1");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            HhLog.e("resourceTypeNew params " + obj.toString());
                            HhHttp.postString()
                                    .url(URLConstant.POST_MAP_RESOURCE_TYPE_NEW)
                                    .content(obj.toString())
                                    .build()
                                    .connTimeOut(10000)
                                    .execute(new LoggedInStringCallback(ResourceViewModel.this, context) {
                                        @Override
                                        public void onSuccess(String response, int id) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                JSONArray data = jsonObject.getJSONArray("data");
                                                List<ResourceType> value = resourceTypeList.getValue();
                                                if(value == null ){
                                                    value = new ArrayList<>();
                                                }
                                                List<ResourceType> c = new Gson().fromJson(String.valueOf(data),
                                                        new TypeToken<List<ResourceType>>() {
                                                        }.getType());
                                                value.addAll(c);
                                                resourceTypeList.postValue(value);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call call, Exception e, int id) {
                                            HhLog.e("onFailure: " + e.toString());
                                            msg.setValue(e.getMessage());
                                        }
                                    });*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                    }
                });
    }
    public void initGrid(){
        HhHttp.get()
                .url(URLConstant.GET_GRID_BY_LEVEL)
                .addParams("level",level)
//                .addParams("gridNo","370214")
                .build().execute(new LoggedInStringCallback(this,context) {
            @Override
            public void onSuccess(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()>0){
                        gridGridList = (new Gson().fromJson(String.valueOf(data),
                                new TypeToken<List<com.haohai.platform.fireforestplatform.ui.multitype.Grid>>(){}.getType()));
                        gridList.postValue(gridGridList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {

            }
        });
    }

    public void postResourceByApiUrl() {
        if(resourceType==null){
            return;
        }
        loading.setValue(new LoadingEvent(true, "正在获取资源信息.."));
        final JSONObject jsonObject = new JSONObject();
        JSONObject dto = new JSONObject();
        try {
            dto.put("gridNo",gridNo);
            jsonObject.put("dto",dto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = URLConstant.POST_MAP_RESOURCE_LIST_START + resourceType.getApiUrl() + URLConstant.POST_MAP_RESOURCE_LIST_END;
        HhHttp.postString()
                .url(url)
                .content(jsonObject.toString())
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        //HhLog.e(resourceType.getApiUrl() + " , " + response);
                        try {
                            loading.postValue(new LoadingEvent(false, ""));
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            List<Resource> list = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = (JSONObject) data.get(i);
                                Resource resource = new Gson().fromJson(obj.toString(), Resource.class);
                                resource.setObj(obj);
                                resource.setType(resourceType.getApiUrl().replace("/api/",""));
                                list.add(resource);
                            }
                            if(!list.isEmpty()){
                                for (Resource res:list) {
                                    res.setApiUrl(resourceType.getApiUrl());
                                }

                                /*List<Resource> value = resourceList.getValue();
                                if(value == null){
                                    value = new ArrayList<>();
                                }
                                value.addAll(list);*/
                                resourceList.postValue(list);
                                resources = list;
                                updateData();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }


    public void updateData() {
        if(page==1){
            items.clear();
        }
        if (resources != null && resources.size()!=0) {
            items.addAll(resources);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
