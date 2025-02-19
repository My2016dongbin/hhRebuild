package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityTbsBinding;
import com.haohai.platform.fireforestplatform.ui.viewmodel.TbsViewModel;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class TbsActivity extends BaseLiveActivity<ActivityTbsBinding, TbsViewModel> implements TbsReaderView.ReaderCallback {

    private String url;
    private String title;
    private TbsReaderView mTbsReaderView;

    private File file;
    String fileName = "";
    String fileType = "";
    private String tbsReaderTemp = Environment.getExternalStorageDirectory() + "/download/TBSTemp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        if(url==null || url.isEmpty()){
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        init_();
        bind_();
    }

    private void init_() {
        binding.topBar.title.setText("文件预览");
        binding.title.setText(title);


        mTbsReaderView = new TbsReaderView(this, this);
        binding.tbsView.addView(mTbsReaderView, new RelativeLayout.LayoutParams(-1, -1));

        initX5();
        downloadFile();
    }

    private void downloadFile() {
        String fileT = url;
        while(fileT.contains(".")){
            int indexOf = fileT.indexOf(".");
            int length = fileT.length();
            fileT = fileT.substring(indexOf+1,length);
        }
        fileType = fileT;
        fileName = "tbs" + new Date().getTime();
        final DownloadTask downloadTask = new DownloadTask(
                TbsActivity.this);
        downloadTask.execute(url);
    }

    private void bind_() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            mTbsReaderView.onStop();
            if(file.exists()){
                file.delete();
            }
        }catch(Exception e){
            HhLog.e("onDestroy: " + e.getMessage() );
        }
    }

    /**
     * 下载文件
     *
     * @author Administrator
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            file = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error
                // report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    file = new File(TbsActivity.this.getObbDir().getAbsolutePath(),
                            fileName + "." + fileType);

                    if (!file.exists()) {
                        // 判断父文件夹是否存在
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }

                } else {
                    Toast.makeText(TbsActivity.this, "sd卡未挂载",
                            Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return e.toString();

            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            try{
                binding.progress.setText(progress[0]+"%");
            }catch (Exception e){
                HhLog.e(e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Toast.makeText(context, "您未打开SD卡权限" + result, Toast.LENGTH_LONG).show();
            } else {
                //展示文件
                //showFile();
                displayFile(file.getPath(),fileName+"."+fileType);
            }

        }
    }

    private void displayFile(String filePath, String fileName) {

        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        String bsReaderTemp = tbsReaderTemp;
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            HhLog.e("准备创建/TbsReaderTemp！！");
            boolean mkdir = bsReaderTempFile.mkdir();
            if (!mkdir) {
                HhLog.e("创建/TbsReaderTemp失败！！！！！");
            }
        }
        Bundle bundle = new Bundle();
        HhLog.e( "filePath" + filePath);//可能是路径错误
        HhLog.e( "tempPath" + tbsReaderTemp);
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", tbsReaderTemp);
        boolean result = mTbsReaderView.preOpen(getFileType(url), false);
        HhLog.e("查看文档---" + result);

        /*if (result) {
            mTbsReaderView.openFile(bundle);
        } else {
            QbSdk.clearAllWebViewCache(this,true);
            boolean result2 = mTbsReaderView.preOpen(getFileType(url), false);
            HhLog.e( "查看文档else---" + result2);

            mTbsReaderView.openFile(bundle);
        }*/
        //跳转选择文件打开方式
        openFileWith(filePath);
    }

    private void openFileWith(String filePath) {
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + filePath);

        // 根据文件类型设置合适的MIME类型
        //String mimeType = getMimeType(filePath);
        intent.setDataAndType(uri, "application/pdf");

        // 提示用户选择打开方式
        Intent chooser = Intent.createChooser(intent, "选择打开方式");
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(chooser);*/

        if (!file.exists()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Uri contentUri = FileProvider.getUriForFile(this, "com.haohai.platform.fireforestplatform.fileprovider", file);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, parseType(url));

        }else{
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), parseType(url));

        }

        startActivity(intent);
        load = true;

    }

    private boolean load = false;
    @Override
    protected void onResume() {
        super.onResume();
        if(load){
            finish();
        }
    }

    private String parseType(String url) {
        String type = "text/plain";
        if(url.endsWith(".pdf")){
            type = "application/pdf";
        }
        if(url.endsWith(".avi")){
            type = "video/x-msvideo";
        }
        if(url.endsWith(".doc")){
            type = "application/msword";
        }
        if(url.endsWith(".docx")){
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        if(url.endsWith(".xls")){
            type = "application/vnd.ms-excel";
        }
        if(url.endsWith(".xlsx")){
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        if(url.endsWith(".gif")){
            type = "image/gif";
        }
        if(url.endsWith(".htm")){
            type = "text/html";
        }
        if(url.endsWith(".html")){
            type = "text/html";
        }
        if(url.endsWith(".jpeg")){
            type = "image/jpeg";
        }
        if(url.endsWith(".jpg")){
            type = "image/jpeg";
        }
        if(url.endsWith(".log")){
            type = "text/plain";
        }
        if(url.endsWith(".txt")){
            type = "text/plain";
        }
        if(url.endsWith(".xml")){
            type = "text/plain";
        }
        if(url.endsWith(".mp3")){
            type = "audio/x-mpeg";
        }
        if(url.endsWith(".mp4")){
            type = "video/mp4";
        }
        if(url.endsWith(".png")){
            type = "image/png";
        }
        if(url.endsWith(".pps")){
            type = "application/vnd.ms-powerpoint";
        }
        if(url.endsWith(".ppt")){
            type = "application/vnd.ms-powerpoint";
        }
        if(url.endsWith(".pptx")){
            type = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        }
        if(url.endsWith(".z")){
            type = "application/x-compress";
        }
        if(url.endsWith(".zip")){
            type = "application/x-zip-compressed";
        }
        if(url.endsWith(".apk")){
            type = "application/vnd.android.package-archive";
        }

        return type;
    }

    private void initX5() {
        try{
            //初始化X5内核
            QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
                @Override
                public void onCoreInitFinished() {
                    //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
                    HhLog.e("加载内核是否成功:");
                }

                @Override
                public void onViewInitFinished(boolean b) {
                    //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                    HhLog.e("加载内核是否成功:"+b);
                    //Toast.makeText(MyApplication.this, "内核加载完成", Toast.LENGTH_SHORT).show();
                    if(!b){
                        initX5();
                    }
                }
            });
        }catch(Exception e){
            HhLog.e("加载内核是否成功:"+e.toString());
        }
    }

    private String getFileType(String paramString) {
        String str = "";
        if (TextUtils.isEmpty(paramString)) {
            HhLog.e("paramString---->null");
            return str;
        }
        HhLog.e("paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            HhLog.e( "i <= -1");
            return str;
        }
        str = paramString.substring(i + 1);
        HhLog.e("paramString.substring(i + 1)------>" + str);
        return str;
    }

    @Override
    protected ActivityTbsBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_tbs);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public TbsViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(TbsViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }
}