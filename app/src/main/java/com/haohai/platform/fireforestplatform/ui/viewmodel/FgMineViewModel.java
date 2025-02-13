package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.event.Ext;
import com.haohai.platform.fireforestplatform.old.AutoStartActivity;
import com.haohai.platform.fireforestplatform.ui.activity.ChangePassWordActivity;
import com.haohai.platform.fireforestplatform.ui.activity.LoginActivity;
import com.haohai.platform.fireforestplatform.ui.bean.MineMenu;
import com.haohai.platform.fireforestplatform.ui.bean.VideoDeleteModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhToast;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FgMineViewModel extends BaseViewModel {
    public Context context;
    public boolean locationUpload = true;
    public boolean voice = false;
    public List<MineMenu> mineMenuList = new ArrayList<>();
    public final MutableLiveData<List<MineMenu>> mineMenuListListener = new MutableLiveData<>();
    public final MutableLiveData<String> userName = new MutableLiveData<>();
    public final MutableLiveData<String> userRole = new MutableLiveData<>();
    public final MutableLiveData<String> headImage = new MutableLiveData<>();
    public void start(Context context){
        this.context = context;
        init_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {
        String userName_ = (String) SPUtils.get(context, SPValue.fullName, "默认用户");
        String userRole_ = (String) SPUtils.get(context, SPValue.roleName, "默认职位");
        String userHead_ = (String) SPUtils.get(context, SPValue.headUrl, "https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AAOEcdM.img");
        userName.setValue(userName_);
        userRole.setValue(Objects.equals(userRole_, "null") ?"":userRole_);
        headImage.setValue(userHead_);
        mineMenuList.add(new MineMenu(context.getResources().getDrawable(R.drawable.jiazai),"版本更新","",false,true));
        mineMenuList.add(new MineMenu(context.getResources().getDrawable(R.drawable.mima),"修改密码","",false,true));
        mineMenuList.add(new MineMenu(context.getResources().getDrawable(R.drawable.anquan),"权限开启","",false,true));
        mineMenuList.add(new MineMenu(context.getResources().getDrawable(R.drawable.ic_location),"实时位置上传","",false,true));
        mineMenuList.add(new MineMenu(context.getResources().getDrawable(R.drawable.yinliangda),"语音播报","",false,true));
        mineMenuListListener.setValue(mineMenuList);
    }

    public void outLogin(){
        SPUtils.put(context,SPValue.login,false);
        CommonData.token = "";
        CommonData.videoAddingIndex = 0;
        CommonData.videoDeleteIndex = 0;
        CommonData.videoDeleteMonitorId = "";
        CommonData.videoDeleteChannelId = "";
        CommonData.videoPlayingIndexList = new ArrayList<>();
        CommonData.videoDeleteModelList = new ArrayList<>();
        CommonData.mainTabIndex = 0;
        CommonData.walkDistance = 0;
        SPUtils.put(context,SPValue.token,"");
        context.startActivity(new Intent(context, LoginActivity.class));
        msg.setValue("已登出");
        EventBus.getDefault().post(new Ext());
    }

    public void onVersionClick(View v){
        Toast.makeText(context, "当前已是最新版本", Toast.LENGTH_SHORT).show();
    }
    public void onPasswordClick(View v){
        context.startActivity(new Intent(context, ChangePassWordActivity.class));
    }
    public void onRootClick(View v){
        context.startActivity(new Intent(context, AutoStartActivity.class));
    }
    public void onPositionClick(View v){
        locationUpload = !locationUpload;
        mineMenuList.get(3).setContent(locationUpload?"开":"关");
        mineMenuListListener.setValue(mineMenuList);
        SPUtils.put(context,SPValue.upload,locationUpload);
        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
    }
    public void onVoiceClick(View v){
        voice = !voice;
        mineMenuList.get(4).setContent(voice?"开":"关");
        mineMenuListListener.setValue(mineMenuList);
        SPUtils.put(context,SPValue.voice,voice);
        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
    }

}
