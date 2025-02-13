package com.haohai.platform.fireforestplatform.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityNewsBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityNewsInfoBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.NewsViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.NewsInfoViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.NewsViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import me.drakeet.multitype.MultiTypeAdapter;

public class NewsInfoActivity extends BaseLiveActivity<ActivityNewsInfoBinding, NewsInfoViewModel>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getIntent().getStringExtra("id");
        init_();
        bind_();
        obtainViewModel().postData(id);
    }

    private void init_() {
        binding.topBar.title.setText("新闻详情");
    }

    private void bind_() {

    }

    @Override
    protected ActivityNewsInfoBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_news_info);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public NewsInfoViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(NewsInfoViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        //Banner数据
        obtainViewModel().webStr.observe(this, this::initWeb);
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