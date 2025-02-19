package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.NaviSetting;
import com.amap.api.navi.model.AMapNaviLocation;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.DialogOneBodyDetailListBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogOneBodyListBinding;
import com.haohai.platform.fireforestplatform.helper.DialogHelper;
import com.haohai.platform.fireforestplatform.old.ResourceAddActivity;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.MonitorFireMessageInfoActivity;
import com.haohai.platform.fireforestplatform.ui.activity.PhotoViewerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.VideoActivity;
import com.haohai.platform.fireforestplatform.ui.activity.VideoStreamActivity;
import com.haohai.platform.fireforestplatform.ui.bean.FireType;
import com.haohai.platform.fireforestplatform.ui.bean.FireTypePost;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImagePagerUtil;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.StringData;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OneBodyDetailDialog extends Dialog implements INaviInfoCallback {

    private final Context context;
    private OneBodyDetailDialogListener dialogListener;
    private final DialogOneBodyDetailListBinding binding;
    private OneBodyFire oneBodyFire;
    public int isRelease = 2;  //0疑似火情  1是真实火情  2是未处理
    public List<FireType> fireTypesReal = new ArrayList<>();
    public List<FireType> fireTypesFuck = new ArrayList<>();
    private String valueReal;
    private String valueFuck;

    public OneBodyDetailDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_onebody_detail, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(OneBodyDetailDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setOneBodyFire(OneBodyFire oneBodyFire) {
        this.oneBodyFire = oneBodyFire;
        updateData();
        if (fireTypesReal == null || fireTypesReal.isEmpty()) {
            getFireTypesReal();
        }
        if (fireTypesFuck == null || fireTypesFuck.isEmpty()) {
            getFireTypesFuck();
        }
    }

    private void getFireTypesReal() {
        RequestParams params = new RequestParams(URLConstant.POST_CODE_SEARCH);
        params.setBodyContent(new Gson().toJson(new FireTypePost(1, 20, new FireTypePost.Dto("起火原因", "fire_reason"))));
        HhHttp.methodX(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("POST_CODE_SEARCH " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    JSONObject obj = (JSONObject) data.get(0);
                    JSONArray dataList = obj.getJSONArray("dataList");
                    fireTypesReal = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<FireType>>() {
                    }.getType());

                } catch (Exception e) {
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

    private void getFireTypesFuck() {
        RequestParams params = new RequestParams(URLConstant.POST_CODE_SEARCH);
        params.setBodyContent(new Gson().toJson(new FireTypePost(1, 20, new FireTypePost.Dto("误报类型", "unreal_type"))));
        HhHttp.methodX(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HhLog.e("POST_CODE_SEARCH " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    JSONObject obj = (JSONObject) data.get(0);
                    JSONArray dataList = obj.getJSONArray("dataList");
                    fireTypesFuck = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<FireType>>() {
                    }.getType());

                } catch (Exception e) {
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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateData() {
        binding.name.setText(oneBodyFire.getName());
        binding.date.setText(StringData.parse19(oneBodyFire.getAlarmDatetime()));
        binding.lngLat.setText(oneBodyFire.getAlarmLongitude() + "、" + oneBodyFire.getAlarmLatitude());
        binding.address.setText(oneBodyFire.getAddress());
        binding.real.setText(parseReal(oneBodyFire.getIsReal()));
        Glide.with(context).load(oneBodyFire.getPicPath1())
                .error(context.getResources().getDrawable(R.drawable.ic_no_pic))
                .into(binding.lightPic);
        Glide.with(context).load(oneBodyFire.getPicPath2())
                .error(context.getResources().getDrawable(R.drawable.ic_no_pic))
                .into(binding.hotPic);
        binding.lightVideo.setOnClickListener(v -> {
            if (oneBodyFire.getVideoPath1() == null || Objects.equals(oneBodyFire.getVideoPath1(), "")) {
                Toast.makeText(context, "暂无可见光视频", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, VideoStreamActivity.class);
            intent.putExtra("url", Objects.requireNonNull(oneBodyFire).getVideoPath1());
            context.startActivity(intent);
        });
        binding.hotVideo.setOnClickListener(v -> {
            if (oneBodyFire.getVideoPath2() == null || Objects.equals(oneBodyFire.getVideoPath2(), "")) {
                Toast.makeText(context, "暂无热成像视频", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, VideoStreamActivity.class);
            intent.putExtra("url", Objects.requireNonNull(oneBodyFire).getVideoPath2());
            context.startActivity(intent);
        });
        binding.lightPic.setOnClickListener(v -> {
            /*List<String> imageList = new ArrayList<>();
            imageList.add(Objects.requireNonNull(oneBodyFire).getPicPath1());
            ImagePagerUtil imagePagerUtil = new ImagePagerUtil((MainActivity)context, imageList);
            imagePagerUtil.setContentText("报警图片");
            imagePagerUtil.show();*/

            Intent intent = new Intent(context, PhotoViewerActivity.class);
            intent.putExtra("url", Objects.requireNonNull(oneBodyFire).getPicPath1());
            context.startActivity(intent);
        });
        binding.hotPic.setOnClickListener(v -> {
            /*List<String> imageList = new ArrayList<>();
            imageList.add(Objects.requireNonNull(oneBodyFire).getPicPath2());
            ImagePagerUtil imagePagerUtil = new ImagePagerUtil((MainActivity)context, imageList);
            imagePagerUtil.setContentText("报警图片");
            imagePagerUtil.show();*/

            Intent intent = new Intent(context, PhotoViewerActivity.class);
            intent.putExtra("url", Objects.requireNonNull(oneBodyFire).getPicPath2());
            context.startActivity(intent);
        });
        binding.yes.setOnClickListener(v -> {
            isRelease = 1;
            hide();
            /*TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(context.getResources().getColor(R.color.c7));
            MessageDialog.show("火情处理", "确定判定为真实火情并下发吗？","确定","取消")
                    .setButtonOrientation(LinearLayout.VERTICAL)
                    .setOkTextInfo(okTextInfo)
                    .setCancelTextInfo(okTextInfo)
                    .setOtherTextInfo(okTextInfo)
                    .setOkButtonClickListener((dialog, v1) -> {
                        show();
                        postIsReleaseFireToService();
                        return false;
                    })
                    .setCancelButtonClickListener((dialog, v2) -> {
                        show();
                        return false;
                    })
                    .setOnBackgroundMaskClickListener((dialog, v12) -> {
                        show();
                        return false;
                    })
                    .setCancelable(true);*/

            CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_fire_type_real) {
                @Override
                public void onBind(final CustomDialog dialog, View v) {
                    LinearLayout ll_type;
                    TextView text_close;
                    TextView text_confirm;
                    ll_type = v.findViewById(R.id.ll_type);
                    text_close = v.findViewById(R.id.text_close);
                    text_confirm = v.findViewById(R.id.text_confirm);
                    ll_type.removeAllViews();
                    for (int i = 0; i < fireTypesReal.size(); i++) {
                        FireType fireType = fireTypesReal.get(i);
                        View p_ = LayoutInflater.from(context).inflate(R.layout.item_fire_type_real, null);
                        LinearLayout all = p_.findViewById(R.id.all);
                        ImageView icon = p_.findViewById(R.id.icon);
                        TextView title = p_.findViewById(R.id.title);
                        title.setText(fireType.getDescription());
                        if (fireType.isChecked()) {
                            icon.setImageResource(R.drawable.yes);
                        } else {
                            icon.setImageResource(R.drawable.no);
                        }
                        all.setOnClickListener(v15 -> {
                            if (!fireType.isChecked()) {
                                valueReal = fireType.getValue();
                                for (int m = 0; m < fireTypesReal.size(); m++) {
                                    fireTypesReal.get(m).setChecked(false);
                                }
                            }
                            fireType.setChecked(!fireType.isChecked());
                            onBind(dialog, v);
                        });

                        ll_type.addView(p_);
                    }
                    text_close.setOnClickListener(v13 -> {
                        dialog.dismiss();
                        show();
                    });
                    text_confirm.setOnClickListener(v14 -> {
                        int tag = 0;
                        for (int i = 0; i < fireTypesReal.size(); i++) {
                            FireType fireType = fireTypesReal.get(i);
                            if(fireType.isChecked()){
                                tag++;
                            }
                        }
                        if(tag==0){
                            Toast.makeText(context, "请选择真实火警类型", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                        //show();
                        postIsReleaseFireToService();
                    });

                }
            }).setOnBackgroundMaskClickListener((dialog, v12) -> {
                show();
                return false;
            }).setCancelable(true);
        });
        binding.no.setOnClickListener(v -> {
            isRelease = 0;
            hide();
            /*TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(context.getResources().getColor(R.color.c7));
            MessageDialog.show("火情处理", "确定判定为疑似火情吗？","确定","取消")
                    .setButtonOrientation(LinearLayout.VERTICAL)
                    .setOkTextInfo(okTextInfo)
                    .setCancelTextInfo(okTextInfo)
                    .setOtherTextInfo(okTextInfo)
                    .setOkButtonClickListener((dialog, v1) -> {
                        show();
                        postIsReleaseFireToService();
                        return false;
                    })
                    .setCancelButtonClickListener((dialog, v2) -> {
                        show();
                        return false;
                    })
                    .setOnBackgroundMaskClickListener((dialog, v12) -> {
                        show();
                        return false;
                    })
                    .setCancelable(true);*/

            CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_fire_type_fuck) {
                @Override
                public void onBind(final CustomDialog dialog, View v) {
                    LinearLayout ll_type;
                    TextView text_close;
                    TextView text_confirm;
                    ll_type = v.findViewById(R.id.ll_type);
                    text_close = v.findViewById(R.id.text_close);
                    text_confirm = v.findViewById(R.id.text_confirm);
                    ll_type.removeAllViews();
                    for (int i = 0; i < fireTypesFuck.size(); i++) {
                        FireType fireType = fireTypesFuck.get(i);
                        View p_ = LayoutInflater.from(context).inflate(R.layout.item_fire_type_real, null);
                        LinearLayout all = p_.findViewById(R.id.all);
                        ImageView icon = p_.findViewById(R.id.icon);
                        TextView title = p_.findViewById(R.id.title);
                        title.setText(fireType.getDescription());
                        if (fireType.isChecked()) {
                            icon.setImageResource(R.drawable.yes);
                        } else {
                            icon.setImageResource(R.drawable.no);
                        }
                        all.setOnClickListener(v15 -> {
                            if (!fireType.isChecked()) {
                                valueFuck = fireType.getValue();
                                for (int m = 0; m < fireTypesFuck.size(); m++) {
                                    fireTypesFuck.get(m).setChecked(false);
                                }
                            }
                            fireType.setChecked(!fireType.isChecked());
                            onBind(dialog, v);
                        });

                        ll_type.addView(p_);
                    }
                    text_close.setOnClickListener(v13 -> {
                        dialog.dismiss();
                        show();
                    });
                    text_confirm.setOnClickListener(v14 -> {
                        int tag = 0;
                        for (int i = 0; i < fireTypesFuck.size(); i++) {
                            FireType fireType = fireTypesFuck.get(i);
                            if(fireType.isChecked()){
                                tag++;
                            }
                        }
                        if(tag==0){
                            Toast.makeText(context, "请选择疑似火警类型", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                        //show();
                        postIsReleaseFireToService();
                    });

                }
            }).setOnBackgroundMaskClickListener((dialog, v12) -> {
                show();
                return false;
            })
                    .setCancelable(true);

        });

        binding.guide.setOnClickListener(v -> {
            NaviSetting.updatePrivacyShow(context, true, true);
            NaviSetting.updatePrivacyAgree(context, true);

            //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
            //AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            //启动导航组件
            //AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);


            double[] doubles_start = LatLngChangeNew.calBD09toGCJ02(CommonData.lat, CommonData.lng);
            LatLng latLng_start = new LatLng(doubles_start[0], doubles_start[1]);
            double[] doubles_end = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(oneBodyFire.getAlarmLatitude()), Double.parseDouble(oneBodyFire.getAlarmLongitude()));
            LatLng latLng_end = new LatLng(doubles_end[0], doubles_end[1]);


            Poi start = new Poi("", latLng_start, "");
            Poi end = new Poi(oneBodyFire.getAddress() + "火点", latLng_end, "");
            AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            params.setUseInnerVoice(true);
            AmapNaviPage.getInstance().showRouteActivity(HhApplication.getInstance(), params, this);

        });
    }

    /**
     * 是否是真实火情数据提交
     */
    private void postIsReleaseFireToService() {
        if (!CommonPermission.hasPermission(context, CommonPermission.MAP_FIRE_HANDLE)) {
            Toast.makeText(context, "当前账号没有操作权限", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams(URLConstant.GET_ONE_BODY_IS_REAL);
        if(isRelease == 1){
            params = new RequestParams(URLConstant.GET_ONE_BODY_IS_REAL);
            params.addParameter("id", oneBodyFire.getId());
            params.addParameter("type", 1);
            params.addParameter("trueAlarmType", valueReal);
            params.addParameter("isAutoDelegate", 1);//0,1,2
            params.addParameter("isAndroid", 2);
        }
        if(isRelease == 0){
            params = new RequestParams(URLConstant.PUT_ONE_BODY_IS_REAL);
            params.addParameter("fireId", oneBodyFire.getId());
            params.addParameter("isReal", 0);
            params.addParameter("isHandle", 1);
            params.addParameter("isTrueNote", null);
            params.addParameter("unrealType", valueFuck);
        }
        Log.e("TAG", "resource: --" + params);
        HhHttp.methodX(isRelease==1?HttpMethod.GET:HttpMethod.PUT, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: 真实火点:" + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        Toast.makeText(getContext(), "上报成功", Toast.LENGTH_SHORT).show();
                        dialogListener.onOneBodyDetailDialogRefresh();
                        hide();
                    } else {
                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError: " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private String parseReal(String isReal) {
        String str = "";
        if (isReal == null || Objects.equals(isReal, "null")) {
            str = "未处理";
            binding.real.setVisibility(View.GONE);
            binding.yes.setVisibility(View.VISIBLE);
            binding.no.setVisibility(View.VISIBLE);
        } else {
            if (Objects.equals(isReal, "1")) {
                str = "真实火情";
            } else {
                str = "疑似火情";
            }
            binding.real.setVisibility(View.VISIBLE);
            binding.yes.setVisibility(View.GONE);
            binding.no.setVisibility(View.GONE);
        }
        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {

    }

    private void init_() {

    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public void onNaviDirectionChanged(int i) {

    }

    @Override
    public void onDayAndNightModeChanged(int i) {

    }

    @Override
    public void onBroadcastModeChanged(int i) {

    }

    @Override
    public void onScaleAutoChanged(boolean b) {

    }


    public interface OneBodyDetailDialogListener {
        void onOneBodyDetailDialogRefresh();
    }
}
