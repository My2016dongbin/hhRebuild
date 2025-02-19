package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.old.linyi.AddCheck;
import com.haohai.platform.fireforestplatform.ui.activity.ComprehensiveCKActivity;
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

public class ComprehensiveCKViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public String id;
    public boolean pass = true;
    public String info = "";
    public final int imageSize = 5;
    public final MutableLiveData<List<CheckImage>> imageList = new MutableLiveData<>(new ArrayList<>());
    public void start(Context context){
        this.context = context;
    }


    public void barLeftClick(View v){
        ((ComprehensiveCKActivity)context).finish();
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
        AddCheck addCheck = new AddCheck();
        addCheck.setPlanResourceId(CommonData.comprehensiveRes.getId());
        List<AddCheck.ItemsFirejd> itemsFirejds = new ArrayList<>();
        addCheck.setItems(itemsFirejds);
        List<AddCheck.ImgsFirejd> list = new ArrayList<>();
        list.add(new AddCheck.ImgsFirejd("http://117.132.5.139:10081/group1/M00/00/0D/wKgBCGbCmqmEACneAAAAAPbjoQU232.png",2));
        addCheck.setImgs(list);
        addCheck.setStatus(pass?5:4);//4:3
        addCheck.setDescription(info);
        String content = new Gson().toJson(addCheck);
        HhHttp.postString()
                .url(URLConstant.POST_COMPREHENSIVE_CK)
                .content(content)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(this,context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("POST_COMPREHENSIVE_CK content " + content);
                        HhLog.e("POST_COMPREHENSIVE_CK " + response);
                        loading.setValue(new LoadingEvent(false));
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("code");
                            if(code == 200){
                                Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
                                ((ComprehensiveCKActivity)context).finish();
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
