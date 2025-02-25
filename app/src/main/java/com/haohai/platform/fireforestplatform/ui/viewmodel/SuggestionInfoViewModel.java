package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.SuggestionInfoActivity;
import com.haohai.platform.fireforestplatform.ui.bean.PostSuggestion;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.Suggestion;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class SuggestionInfoViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> webStr = new MutableLiveData<>();
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((SuggestionInfoActivity)context).finish();
    }

    /*public void postData(String id){
            loading.setValue(new LoadingEvent(true,"加载中.."));
        HhHttp.get()
                .url(URLConstant.GET_NEWS_INFO)
                .addParams("id",id)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            if(data.length()>0){
                                JSONObject obj = (JSONObject) data.get(0);
                                Suggestion news = new Gson().fromJson(obj.toString(),Suggestion.class);

                                webStr.postValue(news.getContent()==null?"<p>暂无数据</p>":news.getContent());
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
    }*/

    public void submit(String content) {
        String toJson = new Gson().toJson(new PostSuggestion("投诉建议", content, (String) SPUtils.get(context, SPValue.id, "1")));
        HhLog.e("POST_SUGGESTION toJson" + toJson);
        HhHttp.postString().url(URLConstant.POST_SUGGESTION).content(toJson)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        loading.setValue(new LoadingEvent(false));
                        try {
                            HhLog.e("POST_SUGGESTION response" + response);
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getInt("code") == 200){
                                Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
                                ((SuggestionInfoActivity)context).finish();
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
}
