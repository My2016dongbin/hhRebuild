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
import com.haohai.platform.fireforestplatform.ui.activity.ComprehensiveZZActivity;
import com.haohai.platform.fireforestplatform.ui.bean.AddZhengzhi;
import com.haohai.platform.fireforestplatform.ui.bean.CheckImage;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

public class ComprehensiveZZViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public String id;
    public String info = "";
    public final int imageSize = 5;
    public final MutableLiveData<List<CheckImage>> imageList = new MutableLiveData<>(new ArrayList<>());
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((ComprehensiveZZActivity)context).finish();
    }

    public void postData(){
        loading.setValue(new LoadingEvent(true,"加载中.."));
        String content = new Gson().toJson(new CommonParams(id,"", (String) SPUtils.get(context, SPValue.groupId, ""),"appInternet",new ArrayList<>()));
        HhHttp.postString()
                .url(URLConstant.POST_TASK_LIST)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        //HhLog.e(response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);

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

    public void postSubmit() {
        AddZhengzhi addZhengzhi = new AddZhengzhi();
        addZhengzhi.setRegulation(info);
        addZhengzhi.setPlanResourceId(CommonData.comprehensiveRes.getId());
        List<AddZhengzhi.ImgsFirejd> list = new ArrayList<>();
        list.add(new AddZhengzhi.ImgsFirejd("http://117.132.5.139:10081/group1/M00/00/0D/wKgBCGbCmqmEACneAAAAAPbjoQU232.png",2));
        addZhengzhi.setImgs(list);
        String content = new Gson().toJson(addZhengzhi);
        HhHttp.postString()
                .url(URLConstant.POST_COMPREHENSIVE_ZZ)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_COMPREHENSIVE_SUBMIT content " + content);
                        HhLog.e("POST_COMPREHENSIVE_SUBMIT " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("code");
                            if(code == 200){
                                Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
                                ((ComprehensiveZZActivity)context).finish();
                            }else{
                                String message = jsonObject.getString("message");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
