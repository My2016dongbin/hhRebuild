package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityTaskListInfoBinding;
import com.haohai.platform.fireforestplatform.old.HistoryLineActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.ui.viewmodel.TaskListInfoViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImagePagerUtil;
import com.haohai.platform.fireforestplatform.utils.StringData;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskListInfoActivity extends BaseLiveActivity<ActivityTaskListInfoBinding, TaskListInfoViewModel> {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        init_();
        bind_();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtainViewModel().postData(id);
    }

    private void init_() {
        binding.topBar.title.setText("任务详情");

        binding.refresh.setRefreshHeader(new ClassicsHeader(this));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().postData(id);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
    }

    private void bind_() {
        binding.textStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainViewModel().changeState(id);
            }
        });
        binding.textUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListInfoActivity.this, LiveUploadActivity.class);
                intent.putExtra("id",id);
                CommonData.taskLists = obtainViewModel().taskLists.getValue();
                startActivity(intent);
            }
        });
    }

    @Override
    protected ActivityTaskListInfoBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_task_list_info);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public TaskListInfoViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(TaskListInfoViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().taskLists.observe(this, this::updateUi);
    }

    @SuppressLint("SetTextI18n")
    private void updateUi(TaskList taskList) {
        binding.mapContentView.setVisibility(View.GONE);
        binding.lineName.title.setText("任务名称");
        binding.lineName.content.setText(taskList.getTaskName());
        binding.lineType.title.setText("任务类型");
        binding.lineType.content.setText(taskList.getIncidentTypeName());
        binding.lineTime.title.setText("开始时间");
        binding.lineTime.content.setText(CommonUtil.parseDate(taskList.getTaskStartTime()));
        binding.lineLngLat.title.setText("任务经纬度");
        try{
            binding.lineLngLat.content.setText(taskList.getLongtitude() + "," + taskList.getLatitude());
        }catch (Exception e){
            Log.e("TAG", "updateUi: " + e.getMessage() );
        }
        binding.lineAddress.title.setText("任务状态");
        binding.lineAddress.content.setText(parseState(taskList.getTaskStatus()));
        /*binding.lineHandle.title.setText("火警类型");
        binding.lineHandle.content.setText(taskList.getIncidentTypeName());*/
        binding.lineHandleTime.title.setText("结束时间");
        binding.lineHandleTime.content.setText(CommonUtil.parseDate(taskList.getTaskEndTime()));
        binding.lineHandleUser.title.setText("发起人");
        try{
            binding.lineHandleUser.content.setText(taskList.getPersonDTOList().get(0).getPersonName());
        }catch (Exception e){
            HhLog.e(e.getMessage());
        }
        binding.lineHandleOptions.title.setText("任务描述");
        binding.lineHandleOptions.content.setText(taskList.getTaskDesc());
        List<TaskList.TaskFileListBean> taskFileList = taskList.getTaskFileList();
        List<String> urlList = new ArrayList<>();
        for (int i = 0; i < taskFileList.size(); i++) {
            TaskList.TaskFileListBean taskFileListBean = taskFileList.get(i);
            urlList.add(taskFileListBean.getFileUrl());
        }
        binding.llPictures.removeAllViews();
        for (int i = 0; i < urlList.size(); i++) {
            String urls = urlList.get(i);
            View p_ = LayoutInflater.from(this).inflate(R.layout.image, null);
            ImageView image = p_.findViewById(R.id.image);
            Glide.with(this).load(urls).error(R.drawable.ic_no_pic).into(image);
            image.setOnClickListener(v -> {
                Intent intent;
                if(urls!=null && urls.contains("mp4")){
                    //视频
                    intent = new Intent(TaskListInfoActivity.this, VideoStreamActivity.class);
                }else{
                    //图片
                    intent = new Intent(this, PhotoViewerActivity.class);
                }
                intent.putExtra("url",urls);
                startActivity(intent);
            });
            binding.llPictures.addView(p_);
        }
        binding.textStatus.setText(parseState(taskList.getTaskStatus()));

        List<TaskList.ReportDTOListBean> taskDetailDTOList = taskList.getReportDTOList();
        binding.llUpload.removeAllViews();
        for (int i = 0; i < taskDetailDTOList.size(); i++) {
            TaskList.ReportDTOListBean model = taskDetailDTOList.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.upload_info, null);
            TextView user = view.findViewById(R.id.user);
            TextView time = view.findViewById(R.id.time);
            TextView live_detail = view.findViewById(R.id.live_detail);
            TextView live_other = view.findViewById(R.id.live_other);
            LinearLayout ll_pictures = view.findViewById(R.id.ll_pictures);
            LinearLayout ll_videos = view.findViewById(R.id.ll_videos);
            user.setText(model.getReportPersonName());
            time.setText(CommonUtil.parseDate(model.getReportTime()));
            live_detail.setText(model.getReportDesc());
            live_other.setText("第" + model.getReportOrder() + "次上报");
            List<TaskList.TaskFileListBean> reportFileList = model.getReportFileList();
            if(reportFileList == null){
                reportFileList = new ArrayList<>();
            }
            List<String> pictures = new ArrayList<>();
            for (int m = 0; m < reportFileList.size(); m++) {
                TaskList.TaskFileListBean taskFileListBean = reportFileList.get(m);
                pictures.add(taskFileListBean.getFileUrl());
            }
            //List<String> pictures = CommonUtil.parseListString(model.getImgUrl());
            //List<String> videos = CommonUtil.parseListString(model.getVideoUrl());
            ll_pictures.removeAllViews();
            for (int m = 0; m < pictures.size(); m++) {
                String s = pictures.get(m);
                View p_ = LayoutInflater.from(this).inflate(R.layout.image, null);
                ImageView image = p_.findViewById(R.id.image);
                Glide.with(this).load(s).error(R.drawable.ic_no_pic).into(image);
                image.setOnClickListener(v -> {
                    Intent intent;
                    if(s!=null && s.contains("mp4")){
                        //视频
                        intent = new Intent(TaskListInfoActivity.this, VideoStreamActivity.class);
                    }else{
                        //图片
                        intent = new Intent(this, PhotoViewerActivity.class);
                    }
                    intent.putExtra("url",s);
                    startActivity(intent);
                });
                ll_pictures.addView(p_);
            }
            ll_videos.removeAllViews();
            /*for (int m = 0; m < videos.size(); m++) {
                String s = videos.get(m);
                View v_ = LayoutInflater.from(this).inflate(R.layout.image, null);
                ImageView image = v_.findViewById(R.id.image);
                Glide.with(this).load(s).error(R.drawable.ic_no_pic).into(image);
                image.setOnClickListener(v -> {
                    Intent intent;
                    if(s!=null && s.contains("mp4")){
                        //视频
                        intent = new Intent(TaskListInfoActivity.this, VideoStreamActivity.class);
                    }else{
                        //图片
                        intent = new Intent(this, PhotoViewerActivity.class);
                    }
                    intent.putExtra("url",s);
                    startActivity(intent);
                });
                ll_videos.addView(v_);
            }*/

            binding.llUpload.addView(view);
        }
    }

    private String parseFireType(String fireType) {
        String type = fireType;
        if(Objects.equals(fireType, "monitor")){
            type = "监控报警";
        }
        if(Objects.equals(fireType, "MONITOR")){
            type = "监控报警";
        }
        if(Objects.equals(fireType, "SATELLITE")){
            type = "卫星报警";
        }
        if(Objects.equals(fireType, "REPORT")){
            type = "人员上报";
        }
        return type;
    }

    private String parseTaskType(String taskType) {
        String type = taskType;
        if(Objects.equals(taskType, "3")){
            type = "扑救任务";
        }
        if(Objects.equals(taskType, "2")){
            type = "火情排查任务";
        }
        return type;
    }

    private String parseState(String status) {
        if(Objects.equals(status, "wait")){
            return "未开始";
        }
        if(Objects.equals(status, "progress")){
            return "执行中";
        }
        if(Objects.equals(status, "completed")){
            return "已结束";
        }
        return "未开始";
    }
}