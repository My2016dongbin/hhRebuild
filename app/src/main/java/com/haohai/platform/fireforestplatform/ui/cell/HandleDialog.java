package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.DialogHandleBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogOneBodyDetailListBinding;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.activity.PhotoViewerActivity;
import com.haohai.platform.fireforestplatform.ui.activity.VideoStreamActivity;
import com.haohai.platform.fireforestplatform.ui.bean.FireType;
import com.haohai.platform.fireforestplatform.ui.bean.FireTypePost;
import com.haohai.platform.fireforestplatform.ui.bean.HandleResult;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;
import com.haohai.platform.fireforestplatform.utils.StringData;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HandleDialog extends Dialog {

    private final Context context;
    private HandleDialogListener dialogListener;
    private final DialogHandleBinding binding;
    private HandleResult handleResult;
    private boolean isReal = false;

    public HandleDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_handle, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(HandleDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setHandleResult(HandleResult handleResult) {
        this.handleResult = handleResult;
        updateData();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateData() {
        if(handleResult.isReal()){
            binding.imageReal.setImageDrawable(context.getResources().getDrawable(R.drawable.yes));
            binding.imageFuck.setImageDrawable(context.getResources().getDrawable(R.drawable.no));
            isReal = true;
        }else{
            binding.imageFuck.setImageDrawable(context.getResources().getDrawable(R.drawable.yes));
            binding.imageReal.setImageDrawable(context.getResources().getDrawable(R.drawable.no));
            isReal = false;
        }
        binding.remarkView.setText(CommonUtil.parseNullString(handleResult.getRemark(),""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {
        binding.llFuck.setOnClickListener(v -> {
            binding.imageFuck.setImageDrawable(context.getResources().getDrawable(R.drawable.yes));
            binding.imageReal.setImageDrawable(context.getResources().getDrawable(R.drawable.no));
            isReal = false;
        });
        binding.llReal.setOnClickListener(v -> {
            binding.imageReal.setImageDrawable(context.getResources().getDrawable(R.drawable.yes));
            binding.imageFuck.setImageDrawable(context.getResources().getDrawable(R.drawable.no));
            isReal = true;
        });
        binding.right.setOnClickListener(v -> {
            if(binding.remarkView.getText().toString().isEmpty()){
                Toast.makeText(context, "请输入备注", Toast.LENGTH_SHORT).show();
                return;
            }
            handleResult.setRemark(binding.remarkView.getText().toString());
            handleResult.setReal(isReal);
            dialogListener.onHandleDialogResult(handleResult);
            dismiss();
        });
        binding.left.setOnClickListener(v -> {
            dismiss();
        });
        binding.close.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void init_() {

    }

    public interface HandleDialogListener {
        void onHandleDialogResult(HandleResult handleResult);
    }
}
