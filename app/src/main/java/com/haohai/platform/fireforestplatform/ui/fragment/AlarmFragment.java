package com.haohai.platform.fireforestplatform.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseFragment;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.FgAlarm;
import com.haohai.platform.fireforestplatform.ui.activity.AddingUploadActivity;
import com.haohai.platform.fireforestplatform.ui.activity.AlarmInfoActivity;
import com.haohai.platform.fireforestplatform.ui.activity.NewsInfoActivity;
import com.haohai.platform.fireforestplatform.ui.bean.TypeBean;
import com.haohai.platform.fireforestplatform.ui.multitype.Alarm;
import com.haohai.platform.fireforestplatform.ui.multitype.AlarmViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.AlarmViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class AlarmFragment extends BaseFragment<FgAlarm, AlarmViewModel> implements AlarmViewBinder.OnItemClickListener {

    public static AlarmFragment newInstance(String param1) {
        Bundle args = new Bundle();
        args.putString("args", param1);
        AlarmFragment fragment = new AlarmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        init_();
        bind_();

        obtainViewModel().postData(true);
        return binding.getRoot();
    }

    private void bind_() {

    }

    @Override
    protected void setupViewModel() {
        binding.setLifecycleOwner(this);
        binding.setFragmentModel(obtainViewModel());
        obtainViewModel().start(requireActivity());
    }

    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
    }

    @Override
    public int bindLayoutId() {
        return R.layout.fragment_alarm;
    }

    @Override
    public AlarmViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(AlarmViewModel.class);
    }


    private void init_() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.messageSmart.setRefreshHeader(new ClassicsHeader(requireActivity()));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.messageSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page=1;
                obtainViewModel().postData(true);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page++;
                obtainViewModel().postData(true);
                refreshLayout.finishLoadMore(1000);
            }
        });

        AlarmViewBinder messageViewBinder = new AlarmViewBinder(requireActivity());
        messageViewBinder.setListener(this);
        obtainViewModel().adapter.register(Alarm.class, messageViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(requireActivity()));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    @Override
    public void onItemClick(Alarm message) {
        /*showNoticeDialog(message);*/
        CommonData.alarm = message;
        Intent intent = new Intent(requireActivity(), AlarmInfoActivity.class);
        startActivity(intent);
    }

    private void showNoticeDialog(Alarm message) {
        CustomDialog.show(new OnBindView<CustomDialog>(R.layout.dialog_notice) {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onBind(final CustomDialog dialog, View v) {
                WebView web = v.findViewById(R.id.web);
                TextView title = v.findViewById(R.id.title);
                title.setText(message.getTitle());
                ImageView close = v.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                web.loadDataWithBaseURL(null, message.getDescription(), "text/html", "UTF-8", null);
                web.getSettings().setJavaScriptEnabled(true);
                web.addJavascriptInterface(new WebViewImageClickInterface(getActivity()), "ImageClickInterface");

                web.setWebViewClient(new WebViewClient() {
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

                web.loadDataWithBaseURL(null, getHtmlData(message.getDescription()), "text/html", "UTF-8", null);

            }
        }).setOnBackgroundMaskClickListener((dialog, v12) -> {
            dialog.dismiss();
            return true;
        }).setMaskColor(getResources().getColor(R.color.transparent_dialog))
                .setCancelable(true);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
