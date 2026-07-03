package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.TaskActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class TaskViewModel extends BaseViewModel {
    private static final int DEFAULT_LIMIT = 20;

    public static final String STATUS_ALL = "";
    public static final String STATUS_NOT_STARTED = "0";
    public static final String STATUS_IN_PROGRESS = "1";
    public static final String STATUS_FINISHED = "2";

    public Context context;
    public MultiTypeAdapter adapter;
    public String id;
    public List<TaskList> taskLists = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public final MutableLiveData<Integer> refreshEvent = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> loadMoreEvent = new MutableLiveData<>(0);
    public final MutableLiveData<Boolean> noMoreData = new MutableLiveData<>(false);
    ///空字符串全部，0未开始，1执行中，2已结束
    public final MutableLiveData<String> status = new MutableLiveData<>(STATUS_ALL);
    public int page = 1;
    public int limit = DEFAULT_LIMIT;
    public int totalSize = 0;
    private boolean isLoadingMore = false;

    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((TaskActivity)context).finish();
    }

    public void switchStatus(String taskStatus) {
        status.setValue(taskStatus);
        refreshData();
    }

    public void refreshData() {
        page = 1;
        totalSize = 0;
        isLoadingMore = false;
        noMoreData.postValue(false);
        postData();
    }

    public void loadMoreData() {
        if (Boolean.TRUE.equals(noMoreData.getValue())) {
            loadMoreEvent.postValue(loadMoreEvent.getValue() == null ? 1 : loadMoreEvent.getValue() + 1);
            noMoreData.postValue(true);
            return;
        }
        isLoadingMore = true;
        page++;
        postData();
    }

    public void postData(){
        final boolean currentLoadMore = isLoadingMore;
        if (!currentLoadMore) {
            loading.setValue(new LoadingEvent(true,"加载中.."));
        }
        String content = buildRequestContent();
        HhHttp.postString()
                .url(URLConstant.POST_TASK_LIST)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("TASK " + content);
                        HhLog.e("TASK " + response);
                        finishRequest(currentLoadMore);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optInt("code") == 200) {
                                List<TaskList> pageList = parsePageData(jsonObject);
                                if (page == 1) {
                                    taskLists.clear();
                                }
                                taskLists.addAll(pageList);
                                updateData();
                                noMoreData.postValue(isNoMoreData(pageList));
                            } else {
                                rollbackPage(currentLoadMore);
                                msg.postValue(jsonObject.optString("message", "获取任务列表失败"));
                            }

                        } catch (JSONException e) {
                            rollbackPage(currentLoadMore);
                            msg.postValue("获取任务列表失败");
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        finishRequest(currentLoadMore);
                        rollbackPage(currentLoadMore);
                        msg.postValue("获取任务列表失败");
                    }
                });
    }

    private String buildRequestContent() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto = new JSONObject();
            dto.put("groupId", (String) SPUtils.get(context, SPValue.groupId, "001"));
            dto.put("startTime", "");
            dto.put("endTime", "");
            dto.put("taskContent", "");
            if (TextUtils.isEmpty(status.getValue())) {
                dto.put("status", "");
            } else {
                dto.put("status", Integer.parseInt(status.getValue()));
            }
            jsonObject.put("page", page);
            jsonObject.put("limit", limit);
            jsonObject.put("dto", dto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private List<TaskList> parsePageData(JSONObject root) throws JSONException {
        List<TaskList> pageList = new ArrayList<>();
        Object dataObject = root.opt("data");
        if (dataObject instanceof JSONArray) {
            JSONArray dataArray = (JSONArray) dataObject;
            pageList = new Gson().fromJson(String.valueOf(dataArray), new TypeToken<List<TaskList>>() {
            }.getType());
            totalSize = 0;
            return pageList == null ? new ArrayList<>() : pageList;
        }
        if (dataObject instanceof JSONObject) {
            JSONObject pageObject = (JSONObject) dataObject;
            totalSize = pageObject.optInt("totalSize", 0);
            JSONArray dataList = pageObject.optJSONArray("dataList");
            if (dataList == null) {
                return pageList;
            }
            pageList = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<TaskList>>() {
            }.getType());
        }
        return pageList == null ? new ArrayList<>() : pageList;
    }

    private void finishRequest(boolean currentLoadMore) {
        loading.setValue(new LoadingEvent(false));
        if (currentLoadMore) {
            loadMoreEvent.postValue(loadMoreEvent.getValue() == null ? 1 : loadMoreEvent.getValue() + 1);
        } else {
            refreshEvent.postValue(refreshEvent.getValue() == null ? 1 : refreshEvent.getValue() + 1);
        }
        isLoadingMore = false;
    }

    private void rollbackPage(boolean currentLoadMore) {
        if (currentLoadMore && page > 1) {
            page--;
        }
    }

    private boolean isNoMoreData(List<TaskList> pageList) {
        if (totalSize > 0) {
            return taskLists.size() >= totalSize;
        }
        return pageList == null || pageList.size() < limit;
    }

    public void updateData() {
        items.clear();
        if (taskLists != null && taskLists.size()!=0) {
            items.addAll(taskLists);
        }else{
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
}
