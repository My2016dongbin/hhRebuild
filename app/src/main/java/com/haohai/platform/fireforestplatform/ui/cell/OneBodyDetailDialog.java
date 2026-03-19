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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.haohai.platform.fireforestplatform.old.bean.Reason;
import com.haohai.platform.fireforestplatform.old.bean.Tree;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.MonitorFireMessageInfoActivity;
import com.haohai.platform.fireforestplatform.ui.activity.PhotoViewerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.VideoActivity;
import com.haohai.platform.fireforestplatform.ui.activity.VideoStreamActivity;
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
    private List<Reason> reasonListWuBao = new ArrayList<>();
    private List<Reason> reasonListWeiGui = new ArrayList<>();
    private boolean autoValue = true;

    public OneBodyDetailDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
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
        getReasonList();
    }

    private void getReasonList() {
        //误报
        JSONObject jsWuBao = new JSONObject();
        try {
            //误报 suspected_fire_type， 违规用火 illegally_fire_type， 真实 fire_reason，
            jsWuBao.put("type", "suspected_fire_type");
        } catch (JSONException e) {
        }
        RequestParams paramsWuBao = new RequestParams(URLConstant.POST_ONE_BODY_IS_REAL_REASON_LIST);
        Log.e("TAG", "getReasonList: " + jsWuBao.toString());
        paramsWuBao.addHeader("Authorization", "bearer " + CommonData.token);
        paramsWuBao.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        paramsWuBao.setBodyContent(jsWuBao.toString());
        Log.e("TAG", "getReasonList: --"  + paramsWuBao);
        x.http().request(HttpMethod.POST,paramsWuBao, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: getReasonList:误报 " + result );
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("code").equals("200")) {
                        JSONArray data = obj.getJSONArray("data");
                        reasonListWuBao = new Gson().fromJson(String.valueOf(data),new TypeToken<List<Reason>>(){}.getType());
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
        ///违规用火
        JSONObject jsWeiGui = new JSONObject();
        try {
            //误报 suspected_fire_type， 违规用火 illegally_fire_type， 真实 fire_reason，
            jsWeiGui.put("type", "illegally_fire_type");
        } catch (JSONException e) {
        }
        RequestParams paramsWeiGui = new RequestParams(URLConstant.POST_ONE_BODY_IS_REAL_REASON_LIST);
        Log.e("TAG", "getReasonList: " + jsWeiGui.toString());
        paramsWeiGui.addHeader("Authorization", "bearer " + CommonData.token);
        paramsWeiGui.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        paramsWeiGui.setBodyContent(jsWeiGui.toString());
        Log.e("TAG", "getReasonList: --"  + paramsWeiGui);
        x.http().request(HttpMethod.POST,paramsWeiGui, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: getReasonList:违规 " + result );
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("code").equals("200")) {
                        JSONArray data = obj.getJSONArray("data");
                        reasonListWeiGui = new Gson().fromJson(String.valueOf(data),new TypeToken<List<Reason>>(){}.getType());
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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateData() {
        binding.name.setText(oneBodyFire.getName());
        binding.date.setText(StringData.parse19(oneBodyFire.getAlarmDatetime()));
        binding.lngLat.setText(oneBodyFire.getAlarmLongitude()+"、"+oneBodyFire.getAlarmLatitude());
        binding.address.setText(oneBodyFire.getAddress());
        binding.real.setText(parseReal(oneBodyFire.getIsReal()));
        Glide.with(context).load(oneBodyFire.getPicPath1())
                .error(context.getResources().getDrawable(R.drawable.ic_no_pic))
                .into(binding.lightPic);
        Glide.with(context).load(oneBodyFire.getPicPath2())
                .error(context.getResources().getDrawable(R.drawable.ic_no_pic))
                .into(binding.hotPic);
        binding.lightVideo.setOnClickListener(v -> {
            if(oneBodyFire.getVideoPath1()==null || Objects.equals(oneBodyFire.getVideoPath1(), "")){
                Toast.makeText(context, "暂无可见光视频", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, VideoStreamActivity.class);
            intent.putExtra("url", Objects.requireNonNull(oneBodyFire).getVideoPath1());
            context.startActivity(intent);
        });
        binding.hotVideo.setOnClickListener(v -> {
            if(oneBodyFire.getVideoPath2()==null || Objects.equals(oneBodyFire.getVideoPath2(), "")){
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
            intent.putExtra("url",Objects.requireNonNull(oneBodyFire).getPicPath1());
            context.startActivity(intent);
        });
        binding.hotPic.setOnClickListener(v -> {
            /*List<String> imageList = new ArrayList<>();
            imageList.add(Objects.requireNonNull(oneBodyFire).getPicPath2());
            ImagePagerUtil imagePagerUtil = new ImagePagerUtil((MainActivity)context, imageList);
            imagePagerUtil.setContentText("报警图片");
            imagePagerUtil.show();*/

            Intent intent = new Intent(context, PhotoViewerActivity.class);
            intent.putExtra("url",Objects.requireNonNull(oneBodyFire).getPicPath2());
            context.startActivity(intent);
        });
        binding.yes.setOnClickListener(v -> {
            isRelease = 1;
            hide();
            TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(context.getResources().getColor(R.color.c7));
            CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_fire_handle_real) {
                @Override
                public void onBind(final CustomDialog dialog, View v) {
                    RadioButton radio_auto;
                    RadioButton radio_no_auto;
                    TextView auto;
                    TextView no_auto;
                    TextView confirm;
                    TextView dismiss;
                    LinearLayout ll_complete;
                    LinearLayout ll_feedback;
                    EditText edit_remark;
                    EditText edit_complete;
                    EditText edit_feedback;

                    auto = v.findViewById(R.id.auto);
                    no_auto = v.findViewById(R.id.no_auto);
                    radio_auto = v.findViewById(R.id.radio_auto);
                    radio_no_auto = v.findViewById(R.id.radio_no_auto);
                    ll_complete = v.findViewById(R.id.ll_complete);
                    ll_feedback = v.findViewById(R.id.ll_feedback);
                    confirm = v.findViewById(R.id.confirm);
                    dismiss = v.findViewById(R.id.dismiss);
                    edit_remark = v.findViewById(R.id.edit_remark);
                    edit_complete = v.findViewById(R.id.edit_complete);
                    edit_feedback = v.findViewById(R.id.edit_feedback);

                    radio_auto.setChecked(true);
                    radio_auto.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            autoValue = true;
                            auto.setTextColor(context.getResources().getColor(R.color.theme_color_blue));
                            radio_no_auto.setChecked(false);
                            no_auto.setTextColor(context.getResources().getColor(R.color.text_color3));

                            ll_complete.setVisibility(View.VISIBLE);
                            ll_feedback.setVisibility(View.VISIBLE);
                        }
                    });

                    radio_no_auto.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            autoValue = false;
                            no_auto.setTextColor(context.getResources().getColor(R.color.theme_color_blue));
                            radio_auto.setChecked(false);
                            auto.setTextColor(context.getResources().getColor(R.color.text_color3));

                            ll_complete.setVisibility(View.GONE);
                            ll_feedback.setVisibility(View.GONE);
                        }
                    });

                    confirm.setOnClickListener(v1 -> {
                        if (edit_complete.getText().toString().isEmpty() && autoValue) {
                            Toast.makeText(context, "请先输入完成时间", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (edit_feedback.getText().toString().isEmpty() && autoValue) {
                            Toast.makeText(context, "请先输入反馈时间", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        show();
                        postRealFireToService(autoValue,edit_remark.getText().toString(),edit_complete.getText().toString(),edit_feedback.getText().toString());
                        dialog.dismiss();
                    });

                    dismiss.setOnClickListener(v12 -> {
                        show();
                        dialog.dismiss();
                    });

                }
            }).setOnBackgroundMaskClickListener((dialog, v12) -> {
                show();
                return false;
            }).setCancelable(true).setMaskColor(context.getResources().getColor(R.color.trans_mask));
        });
        binding.no.setOnClickListener(v -> {
            isRelease = 0;
            hide();
            TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(context.getResources().getColor(R.color.c7));
            CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_fire_handle_wubao) {
                @Override
                public void onBind(final CustomDialog dialog, View v) {
                    TextView confirm;
                    TextView dismiss;
                    LinearLayout ll_list;
                    EditText edit_remark;

                    confirm = v.findViewById(R.id.confirm);
                    dismiss = v.findViewById(R.id.dismiss);
                    ll_list = v.findViewById(R.id.ll_list);
                    edit_remark = v.findViewById(R.id.edit_remark);

                    if (reasonListWuBao != null && reasonListWuBao.size() > 0) {
                        ll_list.removeAllViews();

                        for (int i = 0; i < reasonListWuBao.size(); i++) {
                            int index = i;
                            Reason reason = reasonListWuBao.get(i);

                            View typeView = LayoutInflater.from(context)
                                    .inflate(R.layout.dialog_type, ll_list, false);

                            RadioButton radio = typeView.findViewById(R.id.radio);
                            TextView text = typeView.findViewById(R.id.text);

                            radio.setChecked(reason.isCheck());
                            text.setText(reason.getDescription());
                            text.setTextColor(context.getResources().getColor(
                                    reason.isCheck() ? R.color.theme_color_blue : R.color.text_color3
                            ));

                            radio.setOnClickListener(view -> {
                                for (int j = 0; j < reasonListWuBao.size(); j++) {
                                    Reason r = reasonListWuBao.get(j);
                                    r.setCheck(j == index);

                                    View child = ll_list.getChildAt(j);
                                    if (child != null) {
                                        RadioButton rb = child.findViewById(R.id.radio);
                                        TextView tv = child.findViewById(R.id.text);

                                        rb.setChecked(j == index);
                                        tv.setTextColor(context.getResources().getColor(
                                                j == index ? R.color.theme_color_blue : R.color.text_color3
                                        ));
                                    }
                                }
                            });

                            ll_list.addView(typeView);
                        }
                    }

                    confirm.setOnClickListener(v1 -> {
                        String unrealType = "";
                        for (int i = 0; i < reasonListWuBao.size(); i++) {
                            Reason reason = reasonListWuBao.get(i);
                            if(reason.isCheck()){
                                unrealType = reason.getValue();
                            }
                        }
                        if (unrealType.isEmpty()) {
                            Toast.makeText(context, "请先选择误报类型", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        show();
                        postWuBaoFireToService(edit_remark.getText().toString());
                        dialog.dismiss();
                    });

                    dismiss.setOnClickListener(v12 -> {
                        show();
                        dialog.dismiss();
                    });

                }
            }).setOnBackgroundMaskClickListener((dialog, v12) -> {
                show();
                return false;
            }).setCancelable(true).setMaskColor(context.getResources().getColor(R.color.trans_mask));
        });
        binding.mid.setOnClickListener(v -> {
            isRelease = 0;
            hide();
            TextInfo okTextInfo = new TextInfo();
            okTextInfo.setFontColor(context.getResources().getColor(R.color.c7));
            CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_fire_handle_weigui) {
                @Override
                public void onBind(final CustomDialog dialog, View v) {
                    RadioButton radio_auto;
                    RadioButton radio_no_auto;
                    TextView auto;
                    TextView no_auto;
                    TextView confirm;
                    TextView dismiss;
                    LinearLayout ll_list;
                    LinearLayout ll_complete;
                    LinearLayout ll_feedback;
                    EditText edit_remark;
                    EditText edit_complete;
                    EditText edit_feedback;

                    auto = v.findViewById(R.id.auto);
                    no_auto = v.findViewById(R.id.no_auto);
                    radio_auto = v.findViewById(R.id.radio_auto);
                    radio_no_auto = v.findViewById(R.id.radio_no_auto);
                    confirm = v.findViewById(R.id.confirm);
                    dismiss = v.findViewById(R.id.dismiss);
                    ll_list = v.findViewById(R.id.ll_list);
                    ll_complete = v.findViewById(R.id.ll_complete);
                    ll_feedback = v.findViewById(R.id.ll_feedback);
                    edit_remark = v.findViewById(R.id.edit_remark);
                    edit_complete = v.findViewById(R.id.edit_complete);
                    edit_feedback = v.findViewById(R.id.edit_feedback);

                    if (reasonListWeiGui != null && reasonListWeiGui.size() > 0) {
                        ll_list.removeAllViews();

                        for (int i = 0; i < reasonListWeiGui.size(); i++) {
                            int index = i;
                            Reason reason = reasonListWeiGui.get(i);

                            View typeView = LayoutInflater.from(context)
                                    .inflate(R.layout.dialog_type, ll_list, false);

                            RadioButton radio = typeView.findViewById(R.id.radio);
                            TextView text = typeView.findViewById(R.id.text);

                            radio.setChecked(reason.isCheck());
                            text.setText(reason.getDescription());
                            text.setTextColor(context.getResources().getColor(
                                    reason.isCheck() ? R.color.theme_color_blue : R.color.text_color3
                            ));

                            radio.setOnClickListener(view -> {
                                for (int j = 0; j < reasonListWeiGui.size(); j++) {
                                    Reason r = reasonListWeiGui.get(j);
                                    r.setCheck(j == index);

                                    View child = ll_list.getChildAt(j);
                                    if (child != null) {
                                        RadioButton rb = child.findViewById(R.id.radio);
                                        TextView tv = child.findViewById(R.id.text);

                                        rb.setChecked(j == index);
                                        tv.setTextColor(context.getResources().getColor(
                                                j == index ? R.color.theme_color_blue : R.color.text_color3
                                        ));
                                    }
                                }
                            });

                            ll_list.addView(typeView);
                        }
                    }

                    radio_auto.setChecked(true);
                    radio_auto.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            autoValue = true;
                            auto.setTextColor(context.getResources().getColor(R.color.theme_color_blue));
                            radio_no_auto.setChecked(false);
                            no_auto.setTextColor(context.getResources().getColor(R.color.text_color3));

                            ll_complete.setVisibility(View.VISIBLE);
                            ll_feedback.setVisibility(View.VISIBLE);
                        }
                    });

                    radio_no_auto.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            autoValue = false;
                            no_auto.setTextColor(context.getResources().getColor(R.color.theme_color_blue));
                            radio_auto.setChecked(false);
                            auto.setTextColor(context.getResources().getColor(R.color.text_color3));

                            ll_complete.setVisibility(View.GONE);
                            ll_feedback.setVisibility(View.GONE);
                        }
                    });

                    confirm.setOnClickListener(v1 -> {
                        String illegallyFireType = "";
                        for (int i = 0; i < reasonListWeiGui.size(); i++) {
                            Reason reason = reasonListWeiGui.get(i);
                            if(reason.isCheck()){
                                illegallyFireType = reason.getValue();
                            }
                        }
                        if (illegallyFireType.isEmpty()) {
                            Toast.makeText(context, "请先选择用火类型", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (edit_complete.getText().toString().isEmpty() && autoValue) {
                            Toast.makeText(context, "请先输入完成时间", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (edit_feedback.getText().toString().isEmpty() && autoValue) {
                            Toast.makeText(context, "请先输入反馈时间", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        show();
                        postWeiGuiFireToService(autoValue,edit_remark.getText().toString(),edit_complete.getText().toString(),edit_feedback.getText().toString());
                        dialog.dismiss();
                    });

                    dismiss.setOnClickListener(v12 -> {
                        show();
                        dialog.dismiss();
                    });

                }
            }).setOnBackgroundMaskClickListener((dialog, v12) -> {
                show();
                return false;
            }).setCancelable(true).setMaskColor(context.getResources().getColor(R.color.trans_mask));
        });

        binding.guide.setOnClickListener(v -> {
            NaviSetting.updatePrivacyShow(context, true, true);
            NaviSetting.updatePrivacyAgree(context, true);

            //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
            //AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            //启动导航组件
            //AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);


            double[] doubles_start = LatLngChangeNew.calBD09toGCJ02(CommonData.lat, CommonData.lng);
            LatLng latLng_start = new LatLng(doubles_start[0],doubles_start[1]);
            double[] doubles_end = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(oneBodyFire.getAlarmLatitude()), Double.parseDouble(oneBodyFire.getAlarmLongitude()));
            LatLng latLng_end = new LatLng(doubles_end[0],doubles_end[1]);


            Poi start = new Poi("", latLng_start, "");
            Poi end = new Poi(oneBodyFire.getAddress()+"火点", latLng_end, "");
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fireId", oneBodyFire.getId());
            jsonObject.put("type", isRelease);
            jsonObject.put("isAndroid", 2);
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.POST_ONE_BODY_IS_REAL);

        // params.setBodyContent(jsonObject.toString());
        params.addParameter("id", oneBodyFire.getId());
        params.addParameter("type", isRelease);
        params.addParameter("trueAlarmType", 7);//真实报警类型 1 农事用火、2生活用火、3垃圾焚烧、4民俗用火、5施工用火、6吸烟、7林火 8草原火；
        params.addParameter("isAutoDelegate", 1);//0,1,2
        params.addParameter("isAndroid", 2);
        Log.e("TAG", "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.e("TAG", "resource: --"  + params);
        x.http().request(HttpMethod.GET,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: 真实火点:" + result );
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

    /**
     * 违规用火升级&&真实火情降级
     */
    private void putFireLevelUpdate(boolean upOrDown) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", oneBodyFire.getId());
            jsonObject.put("isReal", upOrDown?1:2);
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(URLConstant.PUT_UPDATE_FIRE_LEVEL);
        params.setBodyContent(jsonObject.toString());
        Log.e("TAG", "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.e("TAG", "resource: --"  + params);
        x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: putFireLevelUpdate:" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        Toast.makeText(getContext(), "操作成功", Toast.LENGTH_SHORT).show();
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


    /**
     * 误报处理
     */
    private void postWuBaoFireToService(String note) {
        if (!CommonPermission.hasPermission(context, CommonPermission.MAP_FIRE_HANDLE)) {
            Toast.makeText(context, "当前账号没有操作权限", Toast.LENGTH_SHORT).show();
            return;
        }
        String unrealType = "";
        for (int i = 0; i < reasonListWuBao.size(); i++) {
            Reason reason = reasonListWuBao.get(i);
            if(reason.isCheck()){
                unrealType = reason.getValue();
            }
        }
        RequestParams params = new RequestParams(URLConstant.PUT_FIRE_HANDLE_WU_BAO);
        params.addParameter("fireId", oneBodyFire.getId());
        params.addParameter("isReal", 0);
        params.addParameter("unrealType", unrealType);
        params.addParameter("isTrueNote", note);
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.e("TAG", "resource: --"  + params);
        x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: postWuBaoFireToService:" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        postWuBao2FireToService(note);
                        /*Toast.makeText(getContext(), "上报成功", Toast.LENGTH_SHORT).show();
                        dialogListener.onOneBodyDetailDialogRefresh();
                        hide();*/
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
    private void postWuBao2FireToService(String note) {
        if (!CommonPermission.hasPermission(context, CommonPermission.MAP_FIRE_HANDLE)) {
            Toast.makeText(context, "当前账号没有操作权限", Toast.LENGTH_SHORT).show();
            return;
        }
        String unrealType = "";
        for (int i = 0; i < reasonListWuBao.size(); i++) {
            Reason reason = reasonListWuBao.get(i);
            if(reason.isCheck()){
                unrealType = reason.getValue();
            }
        }
        RequestParams params = new RequestParams(URLConstant.PUT_FIRE_HANDLE_UPDATE);
        oneBodyFire.setIsReal("0");
        oneBodyFire.setIsHandle("1");
        oneBodyFire.setIsTrueNote(note);
        oneBodyFire.setFireNote(note);
        oneBodyFire.setUnrealType(unrealType);
        params.setBodyContent(new Gson().toJson(oneBodyFire));
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.e("TAG", "resource: --"  + params);
        x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: postWuBao2FireToService:" + result );
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
    /**
     * 违规用火处理
     */
    private void postWeiGuiFireToService(boolean auto, String note, String complete, String feedback) {
        if (!CommonPermission.hasPermission(context, CommonPermission.MAP_FIRE_HANDLE)) {
            Toast.makeText(context, "当前账号没有操作权限", Toast.LENGTH_SHORT).show();
            return;
        }
        String illegallyFireType = "";
        for (int i = 0; i < reasonListWeiGui.size(); i++) {
            Reason reason = reasonListWeiGui.get(i);
            if(reason.isCheck()){
                illegallyFireType = reason.getValue();
            }
        }
        RequestParams params = new RequestParams(URLConstant.GET_FIRE_HANDLE_WEI_GUI_AUTO);
        if(auto){
            params.addParameter("id", oneBodyFire.getId());
            params.addParameter("type", 1);
            params.addParameter("isReal", 2);
            params.addParameter("trueAlarmType", "");
            params.addParameter("illegallyFireType", illegallyFireType);
            params.addParameter("isTrueNote", note);
            params.addParameter("isAndroid", 1);
            params.addParameter("taskDeadline", complete);
            params.addParameter("feedbackDeadline", feedback);
            params.addParameter("isAutoDelegate", 1);
        }else{
            params = new RequestParams(URLConstant.GET_FIRE_HANDLE_WEI_GUI_NO_AUTO);
            params.addParameter("fireId", oneBodyFire.getId());
            params.addParameter("isReal", 2);
            params.addParameter("isTrueNote", note);
            params.addParameter("trueAlarmType", "");
            params.addParameter("illegallyFireType", illegallyFireType);
            params.addParameter("isHandle", 1);
        }
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.e("TAG", "resource: --"  + params);
        x.http().request(auto?HttpMethod.GET:HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: postWeiGuiFireToService:" + result );
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
    /**
     * 真实火情处理
     */
    private void postRealFireToService(boolean auto, String note, String complete, String feedback) {
        if (!CommonPermission.hasPermission(context, CommonPermission.MAP_FIRE_HANDLE)) {
            Toast.makeText(context, "当前账号没有操作权限", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams(URLConstant.GET_FIRE_HANDLE_REAL_AUTO);
        /*params.addParameter("id", oneBodyFire.getId());
        params.addParameter("type", 1);
        params.addParameter("isReal", 1);
        params.addParameter("trueAlarmType", "");
        params.addParameter("illegallyFireType", "");
        params.addParameter("isTrueNote", note);
        if(auto){
            params.addParameter("isAutoDelegate", 1);
        }
        params.addParameter("isAndroid", 1);
        params.addParameter("taskDeadline", complete);
        params.addParameter("feedbackDeadline", feedback);*/

        if(auto){
            params.addParameter("id", oneBodyFire.getId());
            params.addParameter("type", 1);
            params.addParameter("isReal", 1);
            params.addParameter("trueAlarmType", "");
            params.addParameter("illegallyFireType", "");
            params.addParameter("isTrueNote", note);
            params.addParameter("isAndroid", 1);
            params.addParameter("taskDeadline", complete);
            params.addParameter("feedbackDeadline", feedback);
            params.addParameter("isAutoDelegate", 1);
        }else{
            params = new RequestParams(URLConstant.GET_FIRE_HANDLE_REAL_NO_AUTO);
            params.addParameter("fireId", oneBodyFire.getId());
            params.addParameter("isReal", 1);
            params.addParameter("isTrueNote", note);
            params.addParameter("trueAlarmType", "");
            params.addParameter("isHandle", 1);
        }
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.e("TAG", "resource: --"  + params);
        x.http().request(HttpMethod.GET,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: postRealFireToService:" + result );
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
        binding.realBtnUp.setVisibility(View.GONE);
        binding.realBtnDown.setVisibility(View.GONE);
        String str = "";
        if(isReal == null || Objects.equals(isReal, "null")){
            str = "未处理";
            binding.real.setVisibility(View.GONE);
            binding.yes.setVisibility(View.VISIBLE);
            binding.no.setVisibility(View.VISIBLE);
            binding.mid.setVisibility(View.VISIBLE);
        }else{
            if(Objects.equals(isReal, "1")){
                str = "真实火情";
                if(oneBodyFire.getIllegallyFireType()!=null && !oneBodyFire.getIllegallyFireType().isEmpty()){
                    binding.realBtnDown.setVisibility(View.VISIBLE);
                    binding.realBtnDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hide();
                            TextInfo okTextInfo = new TextInfo();
                            okTextInfo.setFontColor(context.getResources().getColor(R.color.c7));
                            MessageDialog.show("火情处理", "确定降级为违规用火吗？","确定","取消")
                                    .setButtonOrientation(LinearLayout.VERTICAL)
                                    .setOkTextInfo(okTextInfo)
                                    .setCancelTextInfo(okTextInfo)
                                    .setOtherTextInfo(okTextInfo)
                                    .setOkButtonClickListener((dialog, v1) -> {
                                        show();
                                        putFireLevelUpdate(false);
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
                                    .setCancelable(true);
                        }
                    });
                }
            }else if(Objects.equals(isReal, "2")){
                str = "违规用火";
                binding.realBtnUp.setVisibility(View.VISIBLE);
                binding.realBtnUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hide();
                        TextInfo okTextInfo = new TextInfo();
                        okTextInfo.setFontColor(context.getResources().getColor(R.color.c7));
                        MessageDialog.show("火情处理", "确定升级为真实火情吗？","确定","取消")
                                .setButtonOrientation(LinearLayout.VERTICAL)
                                .setOkTextInfo(okTextInfo)
                                .setCancelTextInfo(okTextInfo)
                                .setOtherTextInfo(okTextInfo)
                                .setOkButtonClickListener((dialog, v1) -> {
                                    show();
                                    putFireLevelUpdate(true);
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
                                .setCancelable(true);
                    }
                });
            }else{
                str = "误报";
            }
            binding.real.setVisibility(View.VISIBLE);
            binding.yes.setVisibility(View.GONE);
            binding.no.setVisibility(View.GONE);
            binding.mid.setVisibility(View.GONE);
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


    public interface OneBodyDetailDialogListener{
        void onOneBodyDetailDialogRefresh();
    }
}
