package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.old.bean.Grid;
import com.haohai.platform.fireforestplatform.ui.bean.Resource;
import com.haohai.platform.fireforestplatform.ui.bean.SatelliteParams;
import com.haohai.platform.fireforestplatform.ui.multitype.Emergency;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.SheQu;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.ui.multitype.ResourceType;
import com.haohai.platform.fireforestplatform.ui.multitype.SatelliteFire;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.DbConfig;
import com.haohai.platform.fireforestplatform.utils.GetJsonDataUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.zhy.http.okhttp.builder.GetBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class FgMapViewModel extends BaseViewModel {
    public Context context;
    public int currentPage = 1;
    public int isReal = 2;
    public final int USER_LOCATION = 100;
    public final int ONE_BODY = 101;
    public final int SATELLITE = 102;
    public final int RESOURCE = 103;
    public final int defaultFindTime = 24;//默认查询距离当前时间前N小时数据
    public String startTime = "";
    public String endTime = "";
    public String search = "";
    public int oneBodyFilterState = 1;//0全部  1未处理  2真实火点  3疑似火点
    public List<Grid> gridList = new ArrayList<>();
    public final MutableLiveData<List<SheQu>> sheQuGridList = new MutableLiveData<>();
    public final MutableLiveData<List<ArrayList<Double>>> sheQuCurrentList = new MutableLiveData<>();
    public final MutableLiveData<List<OneBodyFire>> oneBodyList = new MutableLiveData<>();
    public final MutableLiveData<List<ResourceType>> resourceTypeList = new MutableLiveData<>();
    public final MutableLiveData<List<Resource>> resourceList = new MutableLiveData<>();
    public final MutableLiveData<List<SatelliteFire>> satelliteList = new MutableLiveData<>();

    public void start(Context context) {
        this.context = context;
    }

    public void getData() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        endTime = format.format(c.getTime()).replace(" ", "T");
        c.add(Calendar.HOUR, -defaultFindTime);//获取默认小时之前的时间
        startTime = format.format(c.getTime()).replace(" ", "T");
        getOneBodyData();
        getResourceTypeData();
        getSatelliteData(startTime, endTime);
        initGridIntoDb();
        initSheQuGrid();
    }

    public void initSheQuGrid() {
        //社区网格数据json解析
        try {
            JSONObject jsonObject = new JSONObject(new GetJsonDataUtil().getJson(context, "sheQu.json"));
            sheQuGridList.postValue(new Gson().fromJson(String.valueOf(jsonObject.getJSONArray("features")),
                    new TypeToken<List<SheQu>>() {
                    }.getType()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initGridIntoDb() {
        DbManager db = new DbConfig(context).getDbManager();
        List<Grid> list = new ArrayList<>();
        try {
            list = db.selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .findAll();
        } catch (Exception e) {
        }
        if (list == null) {
            list = new ArrayList<>();
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("updateTime", "2010-11-21T08:36:31.420Z");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "resource/api/grid/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization", "bearer " + CommonData.token);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    HhLog.e("grid " + result);
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        gridList.clear();
                        Gson gson = new Gson();
                        gridList = gson.fromJson(String.valueOf(data), new TypeToken<List<Grid>>() {
                        }.getType());
                        DbConfig dbConfig = new DbConfig(context);
                        DbManager db = dbConfig.getDbManager();
                        try {
                            db.saveOrUpdate(gridList);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError: materialRepository请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }


    public void getOneBodyData() {
        loading.setValue(new LoadingEvent(true, "加载中.."));
        GetBuilder getBuilder = HhHttp.get()
                .url(URLConstant.GET_EMERGENCY_PAGE)
                .addParams("pageNum", currentPage + "")
                .addParams("pageSize", "100")
                .addParams("incidentQryType", "2");
        if (oneBodyFilterState - 1 != -1) {
            getBuilder.addParams("handleStatus", oneBodyFilterState - 1 + "");//null全部 0未处置 1处置中 2处置完成
        }
        getBuilder
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_EMERGENCY_PAGE handleStatus " + (oneBodyFilterState - 1));
                        HhLog.e("GET_EMERGENCY_PAGE " + currentPage + "," + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray rows = jsonObject.getJSONArray("rows");
                            List<OneBodyFire> value = oneBodyList.getValue();
                            if (value == null) {
                                value = new ArrayList<>();
                            }
                            List<OneBodyFire> list = new Gson().fromJson(String.valueOf(rows), new TypeToken<List<OneBodyFire>>() {
                            }.getType());


                            if (currentPage == 1) {
                                value.clear();
                            }
                            if (list != null && list.size() != 0) {
                                value.addAll(list);
                                oneBodyList.postValue(value);
                            }


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

    public void getResourceTypeData() {
        resourceTypeList.postValue(new ArrayList<>());
        final JSONObject jsonObject = new JSONObject();
        //新增资源点类型
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SPUtils.get(context, SPValue.token,""));
        HhHttp.postString()
                .url(URLConstant.POST_MAP_RESOURCE_TYPE_NEW)
                .content(jsonObject.toString())
                .headers(headers)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(FgMapViewModel.this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_MAP_RESOURCE_TYPE_NEW response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            List<ResourceType> value = resourceTypeList.getValue();
                            if (value == null) {
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
                    }
                });

    }

    public void getSatelliteData(String startTimeStr, String endTimeStr) {
        String start = CommonUtil.parse10String(startTimeStr, "");
        String end = CommonUtil.parse10String(endTimeStr, "");
        HhHttp.get()
                .url(URLConstant.GET_MAP_SATELLITE)
                .addParams("countyCode","370203")
                .addParams("satellites","NPP,FY-4,FY-3,Himawari-8,NOAA-0,NOAA-15,NOAA-18,NOAA-19,GK2a,NOAA-20")
                .addParams("startTime", start)
                .addParams("endTime", end)
                .addParams("landTypes", "woodland,grassland,farmland,otherland")
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("Satellite GET_MAP_SATELLITE params " + start + " , " + end );
                        HhLog.e("Satellite GET_MAP_SATELLITE params " + startTimeStr + " , " + endTimeStr );
                        HhLog.e("Satellite GET_MAP_SATELLITE response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            satelliteList.postValue(new Gson().fromJson(String.valueOf(data), new TypeToken<List<SatelliteFire>>() {
                            }.getType()));

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

    public void clickModel(ResourceType resourceType, boolean state) {
        if (state) {
            //移除选中类型数据
            removeResourceByApiUrl(resourceType);
            //获取选中类型数据
            postResourceByApiUrlNew(resourceType);
        } else {
            //移除选中类型数据
            removeResourceByApiUrl(resourceType);
        }
        List<ResourceType> value = resourceTypeList.getValue();
        if (value == null) {
            value = new ArrayList<>();
        }
        for (int i = 0; i < value.size(); i++) {
            ResourceType type = value.get(i);
            if (Objects.equals(type.getResourceType(), resourceType.getResourceType())) {
                value.get(i).setChecked(state);
                resourceTypeList.postValue(value);
                return;
            }
        }
    }

    public void clickModelShequ(SheQu sheQu, boolean state) {
        if (state) {
            //移除选中数据
            sheQuCurrentList.postValue(new ArrayList<>());
            //获取选中数据
            sheQuCurrentList.postValue(sheQu.getGeometry().getCoordinates());
        } else {
            //移除选中数据
            sheQuCurrentList.postValue(new ArrayList<>());
        }
        List<SheQu> value = sheQuGridList.getValue();
        for (int i = 0; i < value.size(); i++) {
            SheQu type = value.get(i);
            if (Objects.equals(type.getProperties().getGridNo(), sheQu.getProperties().getGridNo())) {
                value.get(i).setChecked(state);
                sheQuGridList.postValue(value);
                if (!state) {
                    return;
                }
            } else {
                if (state) {
                    value.get(i).setChecked(false);
                }
            }
        }
        sheQuGridList.postValue(value);
    }

    public void updateResEvent(ResourceType resourceType) {
        //移除选中类型数据
        removeResourceByApiUrl(resourceType);
        //获取选中类型数据
        postResourceByApiUrlNew(resourceType);
        List<ResourceType> value = resourceTypeList.getValue();
        for (int i = 0; i < value.size(); i++) {
            ResourceType type = value.get(i);
            if (Objects.equals(type.getResourceType(), resourceType.getResourceType())) {
                value.get(i).setChecked(true);
                resourceTypeList.postValue(value);
                return;
            }
        }
    }

    public void removeResourceByApiUrl(ResourceType resourceType) {
        List<Resource> value = resourceList.getValue();
        if (value != null && !value.isEmpty()) {
            List<Resource> value_C = new ArrayList<>(value);

            for (Resource res : value) {
                if (Objects.equals(res.getResourceType(), resourceType.getResourceType()) || (res.getResourceType() == null || res.getResourceType().isEmpty())) {
                    value_C.remove(res);
                }
            }
            resourceList.postValue(value_C);
        }
    }

    public void postResourceByName(String name) {
        loading.setValue(new LoadingEvent(true, "正在获取资源信息.."));
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resourceName", name);
            //jsonObject.put("resourceType", "carrier");
            jsonObject.put("judgeCode", "All");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = URLConstant.POST_MAP_RESOURCE_LIST_NEW;
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SPUtils.get(context, SPValue.token,""));
        HhHttp.postString()
                .url(url)
                .content(jsonObject.toString())
                .headers(headers)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_MAP_RESOURCE_LIST_NEW params " + jsonObject.toString() );
                        HhLog.e("POST_MAP_RESOURCE_LIST_NEW response " + response );
                        try {
                            loading.postValue(new LoadingEvent(false, ""));
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            List<Resource> list = new ArrayList<>();

                            for (int m = 0; m < data.length(); m++) {
                                JSONObject object = (JSONObject) data.get(m);
                                JSONArray detailDTOList = object.getJSONArray("detailDTOList");
                                for (int i = 0; i < detailDTOList.length(); i++) {
                                    JSONObject obj = (JSONObject) detailDTOList.get(i);
                                    Resource resource = new Gson().fromJson(obj.toString(), Resource.class);
                                    resource.setObj(obj);
                                    resource.setType(obj.getString("resourceType"));
                                    list.add(resource);
                                }
                            }
                            if (!list.isEmpty()) {
                                List<Resource> value = resourceList.getValue();
                                if (value == null) {
                                    value = new ArrayList<>();
                                }
                                value.addAll(list);
                                resourceList.postValue(value);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        //msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

    public void postResourceByApiUrlNew(ResourceType resourceType) {
        loading.setValue(new LoadingEvent(true, "正在获取资源信息.."));
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resourceName", "");
            jsonObject.put("resourceType", resourceType.getResourceType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = URLConstant.POST_MAP_RESOURCE_LIST_NEW;
        HhLog.e("POST_MAP_RESOURCE_LIST_NEW: " + url);
        HhLog.e("POST_MAP_RESOURCE_LIST_NEW: jsonObject " + jsonObject.toString());
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SPUtils.get(context, SPValue.token,""));
        HhHttp.postString()
                .url(url)
                .content(jsonObject.toString())
                .headers(headers)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("post POST_MAP_RESOURCE_LIST_NEW: response " + response);
                        try {
                            loading.postValue(new LoadingEvent(false, ""));
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0) {
                                JSONObject object = (JSONObject) data.get(0);
                                JSONArray detailDTOList = object.getJSONArray("detailDTOList");

                                List<Resource> list = new ArrayList<>();
                                for (int i = 0; i < detailDTOList.length(); i++) {
                                    JSONObject obj = (JSONObject) detailDTOList.get(i);
                                    Resource resource = new Gson().fromJson(obj.toString(), Resource.class);
                                    resource.setObj(obj);
                                    resource.setType(resourceType.getResourceType());
                                    list.add(resource);
                                }
                                if (!list.isEmpty()) {
                                    List<Resource> value = resourceList.getValue();
                                    if (value == null) {
                                        value = new ArrayList<>();
                                    }
                                    value.addAll(list);
                                    resourceList.postValue(value);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        HhLog.e("onFailure: " + e.toString());
                        //msg.setValue(e.getMessage());
                        loading.setValue(new LoadingEvent(false, ""));
                    }
                });
    }

    public void search() {
        if (!search.isEmpty()) {
            postResourceByName(search);
        } else {
            Toast.makeText(context, "请输入搜索内容", Toast.LENGTH_SHORT).show();
        }
    }
}
