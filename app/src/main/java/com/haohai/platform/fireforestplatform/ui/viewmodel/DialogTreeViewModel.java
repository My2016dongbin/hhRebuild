package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.event.VideoStream;
import com.haohai.platform.fireforestplatform.helper.DialogHelper;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.bean.VideoDeleteModel;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.GridCamera;
import com.haohai.platform.fireforestplatform.ui.multitype.GridModel;
import com.haohai.platform.fireforestplatform.ui.multitype.GridTrees;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class DialogTreeViewModel extends BaseViewModel {
    public Context context;
    private VideoTreeCallback listener;
    public MultiTypeAdapter adapter;
    public MultiTypeAdapter adapter_KK;
    public MultiTypeAdapter adapter_Star;
    public int tabStatus = 0;//0 监控 1 卡口 2收藏
    public List<GridTrees> gridTreesList = new ArrayList<>();
    public List<GridTrees> gridTreesList_model = new ArrayList<>();
    public List<GridTrees> gridTreesList_Star = new ArrayList<>();
    public List<GridTrees> gridTreesList_Star_model = new ArrayList<>();
    public List<GridTrees> gridTreesList_KK = new ArrayList<>();
    public List<GridTrees> gridTreesList_KK_model = new ArrayList<>();
    public List<Object> items = new ArrayList<>();
    public List<Object> items_KK = new ArrayList<>();
    public List<Object> items_Star = new ArrayList<>();
    private int postIndex = 0;
    public int offset = 0;
    public String search;

    public void start(Context context) {
        this.context = context;
    }

    public void setListener(VideoTreeCallback listener) {
        this.listener = listener;
    }

    public void getTreesData(int page) {
        listener.onLoading();
        new Handler().postDelayed(() -> {
            try {
                listener.finishLoading();
            } catch (Exception e) {
                HhLog.e("e ," + e.getMessage());
            }
        }, 12000);

        RequestParams params = new RequestParams(URLConstant.GET_VIDEO_TREES);
        HhHttp.getX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //HhLog.e(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    gridTreesList.clear();
                    gridTreesList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<GridTrees>>() {
                    }.getType());
                    gridTreesList_model = new Gson().fromJson(String.valueOf(data), new TypeToken<List<GridTrees>>() {
                    }.getType());
                    //处理底部遮盖问题
                    /*if(gridTreesList!=null && !gridTreesList.isEmpty()){
                        GridTrees gridTrees = gridTreesList.get(gridTreesList.size() - 1);
                        gridTrees.setLast(true);
                    }*/
                    /*if (tabStatus == 0) {
                        updateData();
                    }*/
                    searchRefresh(page,false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e, boolean isOnCallback) {
                HhLog.e("getTreesData e" + e.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                postIndex++;
                if (postIndex > 1) {
                    listener.finishLoading();
                }
            }
        });
    }

    //0 设备  1 卡口  2 收藏
    public void getTreesDataStar(int page) {
        listener.onLoading();
        new Handler().postDelayed(() -> {
            try {
                listener.finishLoading();
            } catch (Exception e) {
                HhLog.e("e ," + e.getMessage());
            }
        }, 12000);

        RequestParams params = new RequestParams(URLConstant.GET_VIDEO_TREES);
        params.addParameter("sheetType", "collection");
        HhHttp.getX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    gridTreesList_Star.clear();
                    gridTreesList_Star = new Gson().fromJson(String.valueOf(data), new TypeToken<List<GridTrees>>() {
                    }.getType());
                    gridTreesList_Star_model = new Gson().fromJson(String.valueOf(data), new TypeToken<List<GridTrees>>() {
                    }.getType());
                    /*//处理底部遮盖问题
                    if(gridTreesList_Star!=null && !gridTreesList_Star.isEmpty()){
                        GridTrees gridTrees = gridTreesList_Star.get(gridTreesList_Star.size() - 1);
                        gridTrees.setLast(true);
                    }*/
                    /*if (tabStatus == 2) {
                        updateDataStar();
                    }*/
                    searchRefresh(page,false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e, boolean isOnCallback) {
                HhLog.e("getTreesData e" + e.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                postIndex++;
                if (postIndex > 1) {
                    listener.finishLoading();
                }
            }
        });
    }

    public void getTreesDataKK(int page) {
        listener.onLoading();
        new Handler().postDelayed(() -> {
            try {
                listener.finishLoading();
            } catch (Exception e) {
                HhLog.e("e ," + e.getMessage());
            }
        }, 12000);

        RequestParams params = new RequestParams(URLConstant.GET_VIDEO_TREES);
        params.addParameter("cameraType", "kakou");
        HhHttp.getX(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    gridTreesList_KK.clear();
                    gridTreesList_KK = new Gson().fromJson(String.valueOf(data), new TypeToken<List<GridTrees>>() {
                    }.getType());
                    gridTreesList_KK_model = new Gson().fromJson(String.valueOf(data), new TypeToken<List<GridTrees>>() {
                    }.getType());
                    /*//处理底部遮盖问题
                    if(gridTreesList_KK!=null && !gridTreesList_KK.isEmpty()){
                        GridTrees gridTrees = gridTreesList_KK.get(gridTreesList_KK.size() - 1);
                        gridTrees.setLast(true);
                    }*/
                    /*if (tabStatus == 1) {
                        updateDataKK();
                    }*/
                    searchRefresh(page,false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e, boolean isOnCallback) {
                HhLog.e("getTreesData e" + e.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                postIndex++;
                if (postIndex > 1) {
                    listener.finishLoading();
                }
            }
        });
    }

    public void getStream(String cameraId, String monitorId, String channelId, int cameraType) {
        DialogHelper.getInstance().show(context, "获取中..");
        HhHttp.get().url(URLConstant.GET_VIDEO_LIVE_URL)
                            .addParams("cameraId", cameraId)
                            .addParams("manufacturer", cameraType==1||cameraType==2?"5":"4")//5    4
                            .addParams("streamType", "2")//2     2
                            .addParams("protocolType", cameraType==1||cameraType==2?"http":"rtsp")//http    rtsp
                            .build().execute(new LoggedInStringCallback(DialogTreeViewModel.this, context) {
                        @Override
                        public void onSuccess(String response, int id) {
                            HhLog.e("getStream " + (cameraType==1||cameraType==2?"http":"rtsp"));
                            HhLog.e("getStream " + cameraId);
                            HhLog.e("getStream " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray data = jsonObject.getJSONArray("data");
                                if (data.length() > 0) {
                                    JSONObject obj = (JSONObject) data.get(0);
                                    String url = obj.getString("url");
                                    HhLog.e("getStream url " + url);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            DialogHelper.getInstance().close();
                                            CommonData.videoDeleteMonitorId = monitorId;
                                            CommonData.videoDeleteChannelId = channelId;
                                            parseVideoDeleteIds(new VideoDeleteModel(CommonData.videoAddingIndex, monitorId, channelId));
                                            EventBus.getDefault().post(new VideoStream(url, true, CommonData.videoAddingIndex));
                                            EventBus.getDefault().post(new MainTabChange((Integer) SPUtils.get(context, SPValue.videoIndex,1)));
                                        }
                                    }, 200);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, Exception e, int id) {
                            DialogHelper.getInstance().close();
                        }
                    });



        //检索大华乐橙设备序列号对应设备对讲参数 deviceSubList
        RequestParams paramsDH = new RequestParams(URLConstant.GET_VIDEO_DH_SEARCH);
        paramsDH.addBodyParameter("id",cameraId);
        HhLog.e( "onSuccess: bingo postPlayerUrl paramsDH" + paramsDH );
        HhHttp.getX(paramsDH, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    HhLog.e( "onSuccess: bingo postPlayerUrl DH" + result );
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()!=0){
                        JSONObject obj = (JSONObject) data.get(0);
                        String serialNumber = obj.getString("serialNumber");

                        CommonData.subId = serialNumber;
                        for (int m = 0; m < CommonData.deviceSubList.length(); m++) {
                            JSONObject objModel = (JSONObject) CommonData.deviceSubList.get(m);
                            if(Objects.equals(objModel.getString("deviceId"), serialNumber)){
                                CommonData.deviceSub = objModel;
                            }
                        }
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

    private void parseVideoDeleteIds(VideoDeleteModel videoDeleteModel) {
        int modelIndex = videoDeleteModel.getIndex();
        for (int i = 0; i < CommonData.videoDeleteModelList.size(); i++) {
            if (modelIndex == CommonData.videoDeleteModelList.get(i).getIndex()) {
                CommonData.videoDeleteModelList.set(i,videoDeleteModel);
                return;
            }
        }
        CommonData.videoDeleteModelList.add(videoDeleteModel);
    }

    /**
     * 本地处理搜索数据 //0 设备  1 卡口  2 收藏
     * @param page 区分设备、卡口、收藏
     * @param expand 用于搜索后的自动展开
     */
    public void searchRefresh(int page,boolean expand) {
        if(/*tabStatus*/page == 0){//0 监控 1 卡口 2收藏
            Gson gson = new Gson();
            List<GridTrees> listFrom = gson.fromJson(String.valueOf(gson.toJson(gridTreesList_model)), new TypeToken<List<GridTrees>>() {
            }.getType());
            gridTreesList = parseSearch(listFrom,expand);
            //处理底部遮盖问题
            if(gridTreesList!=null && !gridTreesList.isEmpty()){
                GridTrees gridTrees = gridTreesList.get(gridTreesList.size() - 1);
                gridTrees.setLast(true);
            }
            updateData();
        }
        if(/*tabStatus*/page == 1){//0 监控 1 卡口 2收藏
            Gson gson = new Gson();
            List<GridTrees> listFrom = gson.fromJson(String.valueOf(gson.toJson(gridTreesList_KK_model)), new TypeToken<List<GridTrees>>() {
            }.getType());
            gridTreesList_KK = parseSearch(listFrom,expand);
            //处理底部遮盖问题
            if(gridTreesList_KK!=null && !gridTreesList_KK.isEmpty()){
                GridTrees gridTrees = gridTreesList_KK.get(gridTreesList_KK.size() - 1);
                gridTrees.setLast(true);
            }
            updateDataKK();
        }
        if(/*tabStatus*/page == 2){//0 监控 1 卡口 2收藏
            Gson gson = new Gson();
            List<GridTrees> listFrom = gson.fromJson(String.valueOf(gson.toJson(gridTreesList_Star_model)), new TypeToken<List<GridTrees>>() {
            }.getType());
            gridTreesList_Star = parseSearch(listFrom,expand);
            //处理底部遮盖问题
            if(gridTreesList_Star!=null && !gridTreesList_Star.isEmpty()){
                GridTrees gridTrees = gridTreesList_Star.get(gridTreesList_Star.size() - 1);
                gridTrees.setLast(true);
            }
            updateDataStar();
        }
    }

    public List<GridTrees> parseSearch(List<GridTrees> listFrom,boolean expand){
        Gson gson = new Gson();
        List<GridTrees> list = gson.fromJson(String.valueOf(gson.toJson(listFrom)), new TypeToken<List<GridTrees>>() {
        }.getType());
        if(search==null || search.isEmpty()){
            return list;
        }
        List<GridTrees> data = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            GridTrees gridTrees = list.get(i);
            gridTrees.setChecked(expand);
            if(gridTrees.toString().contains(search)){
                //递归children
                if(gridTrees.getChildren()!=null && gridTrees.getChildren().size()>0 && gridTrees.getChildren().toString().contains(search)){
                    gridTrees.setChildren(parseSearch(gridTrees.getChildren(),expand));
                }
                //处理MonitorDetailVOs
                if(gridTrees.getMonitorDetailVOs()!=null && gridTrees.getMonitorDetailVOs().size()>0 && gridTrees.getMonitorDetailVOs().toString().contains(search)){
                    List<GridModel> monitorDetailVOs = gridTrees.getMonitorDetailVOs();
                    List<GridModel> list_gridModel = new ArrayList<>();
                    for (int m = 0; m < monitorDetailVOs.size(); m++) {
                        GridModel gridModel = monitorDetailVOs.get(m);
                        gridModel.setChecked(expand);
                        if(gridModel.toString().contains(search)){
                            //处理cameraList
                            if(gridModel.getCameraList()!=null && gridModel.getCameraList().size()>0 && gridModel.getCameraList().toString().contains(search)){
                                List<GridCamera> list_cameraList = new ArrayList<>();
                                List<GridCamera> cameraList = gridModel.getCameraList();
                                for (int n = 0; n < cameraList.size(); n++) {
                                    GridCamera gridCamera = cameraList.get(n);
                                    if(gridCamera.toString().contains(search)){
                                        list_cameraList.add(gridCamera);
                                    }
                                }
                                gridModel.setCameraList(list_cameraList);
                            }

                            list_gridModel.add(gridModel);
                        }
                    }
                    gridTrees.setMonitorDetailVOs(list_gridModel);
                }

                data.add(gridTrees);
            }
        }

        return data;
    }

    public interface VideoTreeCallback {
        void onLoading();

        void finishLoading();

        void modelRefresh();
    }

    public void updateData() {
        Log.e("TAG", "updateData: size = " + tabStatus + "," + gridTreesList.size());
        items.clear();
        if (gridTreesList != null && gridTreesList.size() != 0) {
            items.addAll(gridTreesList);
        } else {
            items.add(new Empty());
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    public void updateDataStar() {
        Log.e("TAG", "updateDataStar: size = " + tabStatus + "," + gridTreesList_Star.size());
        items_Star.clear();
        if (gridTreesList_Star != null && gridTreesList_Star.size() != 0) {
            items_Star.addAll(gridTreesList_Star);
        } else {
            items_Star.add(new Empty());
        }

        assertAllRegistered(adapter_Star, items_Star);
        adapter_Star.notifyDataSetChanged();
    }

    public void updateDataKK() {
        Log.e("TAG", "updateDataKK: size = " + tabStatus + "," + gridTreesList_KK.size());
        items_KK.clear();
        if (gridTreesList_KK != null && gridTreesList_KK.size() != 0) {
            items_KK.addAll(gridTreesList_KK);
        } else {
            items_KK.add(new Empty());
        }

        assertAllRegistered(adapter_KK, items_KK);
        adapter_KK.notifyDataSetChanged();
    }
}
