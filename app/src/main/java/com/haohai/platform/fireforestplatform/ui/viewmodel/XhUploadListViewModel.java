package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.XhUploadListActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.XhUploadRecord;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class XhUploadListViewModel extends BaseViewModel {
    private static final int DEFAULT_LIMIT = 20;

    public Context context;
    public MultiTypeAdapter adapter;
    public final List<XhUploadRecord> records = new ArrayList<>();
    public final List<Object> items = new ArrayList<>();
    public final MutableLiveData<Integer> refreshEvent = new MutableLiveData<>(0);
    public final MutableLiveData<Integer> loadMoreEvent = new MutableLiveData<>(0);
    public final MutableLiveData<Boolean> noMoreData = new MutableLiveData<>(false);
    public int page = 1;
    public int limit = DEFAULT_LIMIT;
    public int totalSize = 0;
    private boolean isLoadingMore = false;

    public void start(Context context) {
        this.context = context;
    }

    public void barLeftClick(View v) {
        ((XhUploadListActivity) context).finish();
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

    public void postData() {
        final boolean currentLoadMore = isLoadingMore;
        if (!isLoadingMore) {
            loading.setValue(new LoadingEvent(true, "加载中.."));
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("page", page);
            jsonObject.put("limit", limit);
            jsonObject.put("dto", new JSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String content = jsonObject.toString();
        HhHttp.postString()
                .url(URLConstant.POST_XH_UPLOAD_LIST)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        finishRequest(currentLoadMore);
                        HhLog.e(URLConstant.POST_XH_UPLOAD_LIST + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.optInt("code") == 200) {
                                List<XhUploadRecord> pageList = parsePageData(obj);
                                if (page == 1) {
                                    records.clear();
                                }
                                records.addAll(pageList);
                                updateData();
                                noMoreData.postValue(isNoMoreData(pageList));
                            } else {
                                rollbackPage(currentLoadMore);
                                msg.postValue(obj.optString("message", "获取巡护上报列表失败"));
                            }
                        } catch (JSONException e) {
                            rollbackPage(currentLoadMore);
                            msg.postValue("获取巡护上报列表失败");
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                        finishRequest(currentLoadMore);
                        rollbackPage(currentLoadMore);
                        msg.postValue("获取巡护上报列表失败");
                    }
                });
    }

    private void updateData() {
        items.clear();
        if (records.isEmpty()) {
            items.add(new Empty());
        } else {
            items.addAll(records);
        }
        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    private List<XhUploadRecord> parsePageData(JSONObject root) throws JSONException {
        List<XhUploadRecord> pageList = new ArrayList<>();
        Object dataObject = root.opt("data");
        JSONObject pageObject = null;
        if (dataObject instanceof JSONArray) {
            JSONArray dataArray = (JSONArray) dataObject;
            if (dataArray.length() > 0) {
                pageObject = dataArray.optJSONObject(0);
            }
        } else if (dataObject instanceof JSONObject) {
            pageObject = (JSONObject) dataObject;
        }
        if (pageObject == null) {
            totalSize = 0;
            return pageList;
        }
        totalSize = pageObject.optInt("totalSize", 0);
        JSONArray dataList = pageObject.optJSONArray("dataList");
        if (dataList == null) {
            return pageList;
        }
        for (int i = 0; i < dataList.length(); i++) {
            JSONObject item = dataList.optJSONObject(i);
            if (item == null) {
                continue;
            }
            pageList.add(new XhUploadRecord(
                    item.optString("id"),
                    item.optString("name"),
                    CommonUtil.parse19String(item.optString("alarmDatetime"), ""),
                    item.optString("address"),
                    parseImageUrl(item),
                    item.optString("createUser")
            ));
        }
        return pageList;
    }

    private String parseImageUrl(JSONObject item) {
        String imageUrl = item.optString("picPath1");
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = item.optString("picPath2");
        }
        return imageUrl;
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

    private boolean isNoMoreData(List<XhUploadRecord> pageList) {
        if (totalSize > 0) {
            return records.size() >= totalSize;
        }
        return pageList == null || pageList.size() < limit;
    }
}
