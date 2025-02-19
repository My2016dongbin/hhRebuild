package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityAlarmInfoBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityNewsInfoBinding;
import com.haohai.platform.fireforestplatform.ui.viewmodel.AlarmInfoViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.NewsInfoViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Objects;

public class AlarmInfoActivity extends BaseLiveActivity<ActivityAlarmInfoBinding, AlarmInfoViewModel>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getIntent().getStringExtra("id");
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("通知详情");
        if(CommonData.alarm!=null && CommonData.alarm.getType()!=null){
            binding.topBar.title.setText(Objects.equals(CommonData.alarm.getType(), "warning") ?"预警预报详情":"通知详情");
            binding.textTitle.setText(CommonData.alarm.getTitle());
            binding.textTitle.setText(CommonData.alarm.getTitle());
            binding.textType.setText(parseType(CommonData.alarm.getType()));
            binding.textDateStart.setText(CommonUtil.parseDate(CommonData.alarm.getStartTime()));
            binding.textDateEnd.setText(CommonUtil.parseDate(CommonData.alarm.getEndTime()));
            initWeb(CommonData.alarm.getDescription());
            /*binding.textReceive.setText(CommonData.alarm.getDelFlag());*/
        }
    }

    private String parseType(String type) {
        if(Objects.equals(type, "notice")){
            return "通知";
        }
        if(Objects.equals(type, "feedback")){
            return "通知";
        }
        if(Objects.equals(type, "warning")){
            return "预警预报";
        }
        return type;
    }

    private void bind_() {
        binding.confirm.setOnClickListener(v -> {
            obtainViewModel().pass_();
        });
    }

    @Override
    protected ActivityAlarmInfoBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_alarm_info);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public AlarmInfoViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(AlarmInfoViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        //数据
        //obtainViewModel().webStr.observe(this, this::initWeb);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb(String webStr) {
        binding.web.loadDataWithBaseURL(null, webStr, "text/html", "UTF-8", null);
        binding.web.getSettings().setJavaScriptEnabled(true);
        binding.web.addJavascriptInterface(new WebViewImageClickInterface(this), "ImageClickInterface");

        binding.web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String javascriptCode = "(function() { " +
                        "var images = document.getElementsByTagName('img');" +
                        "for(var i = 0; i < images.length; i++) {" +
                        "   images[i].onclick = function() {" +
                        "       ImageClickInterface.onImageClick(this.src);" +
                        "   }" +
                        "}" +
                        "var videos = document.getElementsByTagName('video');" +
                        "for (var i = 0; i < videos.length; i++) {" +
                        "    var video = videos[i];" +
                        "    (function(i) {" +
                        "        video.currentTime = 0.1;" +
                        "        video.addEventListener('seeked', function() {" +
                        "            var canvas = document.createElement('canvas');" +
                        "            canvas.width = video.videoWidth;" +
                        "            canvas.height = video.videoHeight;" +
                        "            canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);" +
                        "            var dataURL = canvas.toDataURL('image/png');" +
                        "            video.poster = dataURL;" +
                        "            video.pause();" +
                        "        });" +
                        //"        video.play();" +
                        "    })(i);" +
                        "}" +
                        "})()";

                view.loadUrl("javascript:" + javascriptCode);
            }
        });

        binding.web.loadDataWithBaseURL(null, getHtmlData(webStr), "text/html", "UTF-8", null);
    }
    /**
     * 拼接html字符串片段
     *
     * @param bodyHTML
     * @return
     */
    public static String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{display: block; width:100%; height:auto;}" +
                "video{ width:100%; height:auto;}" +
                "html, body {margin: 0px;padding: 0px;}" +
                "p{color:#666666;font-size: 14px!important;word-break: break-word;}" +
                "span{color: #666;font-size: 14px!important;}" +
                "</style>" +
                "</head>";
        return "<html>" + head + "<body style:'height:auto; width:100%;'>" + bodyHTML + "</body></html>";
    }
    class WebViewImageClickInterface {
        private final Context mCxt;

        public WebViewImageClickInterface(Context context) {
            this.mCxt = context;
        }

        @JavascriptInterface
        public void onImageClick(String imageUrl) {
            // 在这里处理图片点击事件
            HhLog.e("--------->  " + imageUrl);
        }
    }
}