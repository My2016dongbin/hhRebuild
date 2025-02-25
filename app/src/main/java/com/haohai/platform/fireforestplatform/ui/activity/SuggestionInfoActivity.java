package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivitySuggestionInfoBinding;
import com.haohai.platform.fireforestplatform.generated.callback.OnClickListener;
import com.haohai.platform.fireforestplatform.ui.viewmodel.SuggestionInfoViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Objects;

public class SuggestionInfoActivity extends BaseLiveActivity<ActivitySuggestionInfoBinding, SuggestionInfoViewModel>{
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        init_();
        bind_();
        //obtainViewModel().postData(id);
    }

    private void init_() {
        binding.topBar.title.setText("投诉建议");
        if(CommonData.suggestion!=null && id!=null && Objects.equals(CommonData.suggestion.getId(), id)){
            binding.content.setEnabled(false);
            binding.content.setText(CommonData.suggestion.getContent());
            binding.submit.setVisibility(View.GONE);
        }
    }

    private void bind_() {
        binding.submit.setOnClickListener(view -> {
            closeInput(binding.content);
            if(binding.content.getText().toString().isEmpty()){
                Toast.makeText(this, "请输入内容描述", Toast.LENGTH_SHORT).show();
                return;
            }
            obtainViewModel().submit(binding.content.getText().toString());
        });
    }

    @Override
    protected ActivitySuggestionInfoBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_suggestion_info);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public SuggestionInfoViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(SuggestionInfoViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        //Banner数据
        obtainViewModel().webStr.observe(this, this::initWeb);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb(String webStr) {

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