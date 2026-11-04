package com.haohai.platform.fireforestplatform.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityFireEventInfoBinding;
import com.haohai.platform.fireforestplatform.databinding.ViewInfoLineBinding;
import com.haohai.platform.fireforestplatform.ui.bean.FireTimeLine;
import com.haohai.platform.fireforestplatform.ui.viewmodel.FireEventInfoViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.StringData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FireEventInfoActivity extends BaseLiveActivity<ActivityFireEventInfoBinding, FireEventInfoViewModel> {

    private JSONObject fireEvent;
    private final String[] fieldKeys = new String[]{
            "fireName", "provinceName", "cityName", "countyName", "address",
            "longitude", "latitude", "discoverTime", "fireArea", "reporter"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("事件详情");
        binding.timeLineTitle.setVisibility(View.GONE);
        binding.llTimeLine.setVisibility(View.GONE);
        String content = getIntent().getStringExtra("content");
        if(content == null || content.length() == 0){
            finish();
            return;
        }
        try {
            fireEvent = new JSONObject(content);
            updateUi();
            obtainViewModel().getTimeLine(fireEvent.optString("id"));
        } catch (JSONException e) {
            HhLog.e("FireEventInfoActivity error " + e.getMessage());
        }
    }

    private void bind_() {

    }

    @Override
    protected ActivityFireEventInfoBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_fire_event_info);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public FireEventInfoViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(FireEventInfoViewModel.class);
    }

    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().timeLineList.observe(this, this::updateTimeLine);
    }

    private void updateUi() {
        binding.llContent.removeAllViews();
        for (String key : fieldKeys) {
            if(fireEvent.has(key)){
                addInfoLine(parseTitle(key), parseValue(key, fireEvent.opt(key)));
            }
        }
        updateMedia();
    }

    private void addInfoLine(String title, String content) {
        ViewInfoLineBinding lineBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.view_info_line, binding.llContent, false);
        lineBinding.title.setText(title);
        lineBinding.content.setText(content);
        binding.llContent.addView(lineBinding.getRoot());
    }

    private void updateMedia() {
        binding.llMedia.removeAllViews();
        addMedia(fireEvent.optString("picPath1"), false);
        addMedia(fireEvent.optString("picPath2"), false);
        addMedia(fireEvent.optString("videoPath1"), true);
        addMedia(fireEvent.optString("videoPath2"), true);
        boolean hasMedia = binding.llMedia.getChildCount() > 0;
        binding.mediaTitle.setVisibility(hasMedia ? View.VISIBLE : View.GONE);
        binding.mediaScroll.setVisibility(hasMedia ? View.VISIBLE : View.GONE);
    }

    private void addMedia(String url, boolean video) {
        if(url == null || url.length() == 0 || "null".equals(url) || "undefined".equals(url)){
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.image, null);
        ImageView image = view.findViewById(R.id.image);
        Glide.with(this).load(url).error(R.drawable.ic_no_pic).into(image);
        image.setOnClickListener(v -> {
            Intent intent;
            if(video || url.contains("mp4")){
                intent = new Intent(FireEventInfoActivity.this, VideoStreamActivity.class);
            }else{
                intent = new Intent(FireEventInfoActivity.this, PhotoViewerActivity.class);
            }
            intent.putExtra("url", url);
            startActivity(intent);
        });
        binding.llMedia.addView(view);
    }

    private void updateTimeLine(List<FireTimeLine> timeLines) {
        binding.llTimeLine.removeAllViews();
        boolean hasTimeLine = timeLines != null && timeLines.size() > 0;
        binding.timeLineTitle.setVisibility(hasTimeLine ? View.VISIBLE : View.GONE);
        binding.llTimeLine.setVisibility(hasTimeLine ? View.VISIBLE : View.GONE);
        if(!hasTimeLine){
            return;
        }
        for (int i = 0; i < timeLines.size(); i++) {
            FireTimeLine fireTimeLine = timeLines.get(i);
            addTimeLine(fireTimeLine, i, timeLines.size());
        }
    }

    private void addTimeLine(FireTimeLine fireTimeLine, int position, int size) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_fire_time_line, binding.llTimeLine, false);
        View lineTop = view.findViewById(R.id.line_top);
        View lineBottom = view.findViewById(R.id.line_bottom);
        TextView time = view.findViewById(R.id.time);
        TextView message = view.findViewById(R.id.message);
        TextView date = view.findViewById(R.id.date);

        lineTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        lineBottom.setVisibility(position == size - 1 ? View.INVISIBLE : View.VISIBLE);
        time.setText(fireTimeLine.getTimeMinute());
        message.setText(fireTimeLine.getDisposeMessage());
        date.setText(fireTimeLine.getYearMonthDay());
        binding.llTimeLine.addView(view);
    }

    private String parseValue(String key, Object value) {
        if(value == null || value == JSONObject.NULL){
            return "";
        }
        if(value instanceof JSONObject || value instanceof JSONArray){
            return String.valueOf(value);
        }
        String str = String.valueOf(value);
        if("discoverTime".equals(key)){
            return StringData.parse19(str);
        }
        if("fireArea".equals(key)){
            return str + "公顷";
        }
        return str;
    }

    private String parseTitle(String key) {
        switch (key) {
            case "longitude": return "经度";
            case "latitude": return "纬度";
            case "fireName": return "事件名称";
            case "provinceName": return "省";
            case "cityName": return "市";
            case "countyName": return "区县";
            case "address": return "地址";
            case "discoverTime": return "发现时间";
            case "reporter": return "上报人";
            case "fireArea": return "过火面积";
            default: return key;
        }
    }
}
