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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityTaskListInfoBinding;
import com.haohai.platform.fireforestplatform.old.HistoryLineActivity;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.ui.viewmodel.TaskListInfoViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.ImagePagerUtil;
import com.haohai.platform.fireforestplatform.utils.StringData;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
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
        binding.mapContent.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapSnapActivity.class);
            intent.putExtra("roomId", Objects.requireNonNull(obtainViewModel().taskLists.getValue()).getRoomId());
            startActivity(intent);
        });
        binding.textConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog.show("温馨提示", "确定要接受当前任务吗？","接受","取消")
                        .setButtonOrientation(LinearLayout.HORIZONTAL)
                        .setCancelTextInfo(new TextInfo().setFontColor(getResources().getColor(R.color.c6)))
                        .setOkTextInfo(new TextInfo().setFontColor(getResources().getColor(R.color.theme_color_blue)))
                        .setOkButtonClickListener((dialog, v1) -> {
                            obtainViewModel().confirmRefuse(id,"1");
                            return false;
                        })
                        .setCancelable(true);
            }
        });
        binding.textRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog.show("温馨提示", "确定要拒绝当前任务吗？","拒绝","取消")
                        .setButtonOrientation(LinearLayout.HORIZONTAL)
                        .setCancelTextInfo(new TextInfo().setFontColor(getResources().getColor(R.color.c6)))
                        .setOkTextInfo(new TextInfo().setFontColor(getResources().getColor(R.color.theme_color_blue)))
                        .setOkButtonClickListener((dialog, v1) -> {
                            obtainViewModel().confirmRefuse(id,"5");
                            return false;
                        })
                        .setCancelable(true);
            }
        });
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
        if(Objects.equals(taskList.getStatus(), "0")){
            binding.textStatus.setVisibility(View.GONE);
            binding.textUpload.setVisibility(View.GONE);
            binding.textConfirm.setVisibility(View.VISIBLE);
            binding.textRefuse.setVisibility(View.VISIBLE);
        }else{
            binding.textStatus.setVisibility(View.VISIBLE);
            binding.textUpload.setVisibility(View.VISIBLE);
            binding.textConfirm.setVisibility(View.GONE);
            binding.textRefuse.setVisibility(View.GONE);
            if(Objects.equals(taskList.getStatus(), "5")){
                binding.textUpload.setVisibility(View.GONE);
            }
        }
        if(obtainViewModel().taskLists.getValue()!=null &&
                Objects.requireNonNull(obtainViewModel().taskLists.getValue()).getRoomId()!=null){
            binding.mapContentView.setVisibility(View.VISIBLE);
        }else{
            binding.mapContentView.setVisibility(View.GONE);
        }
        binding.lineName.title.setText("任务名称");
        binding.lineName.content.setText(taskList.getTaskContent());
        binding.lineType.title.setText("任务类型");
        binding.lineType.content.setText(parseTaskType(taskList.getTaskType()));
        binding.lineTime.title.setText("开始时间");
        binding.lineTime.content.setText(StringData.parse19(taskList.getTaskStartTime()));
        binding.lineLngLat.title.setText("任务经纬度");
        try{
            binding.lineLngLat.content.setText(taskList.getPosition().getLng() + "," + taskList.getPosition().getLat());
        }catch (Exception e){
            Log.e("TAG", "updateUi: " + e.getMessage() );
        }
        binding.lineAddress.title.setText("任务状态");
        binding.lineAddress.content.setText(parseState(taskList.getStatus()));
        binding.lineHandle.title.setText("火警类型");
        binding.lineHandle.content.setText(parseFireType(taskList.getFireType()));
        binding.lineHandleTime.title.setText("结束时间");
        binding.lineHandleTime.content.setText(StringData.parse19(taskList.getTaskEndTime()));
        binding.lineHandleUser.title.setText("发起人");
        binding.lineHandleUser.content.setText(taskList.getOperatorName());
        binding.lineHandleOptions.title.setText("任务描述");
        binding.lineHandleOptions.content.setText(taskList.getDescription());
        List<String> urlList = StringData.parseList(taskList.getTaskImg());
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
        binding.textStatus.setText(parseState(taskList.getStatus()));

        List<TaskList.TaskDetailDTOList> taskDetailDTOList = taskList.getTaskDetailDTOList();
        binding.llUpload.removeAllViews();
        for (int i = 0; i < taskDetailDTOList.size(); i++) {
            TaskList.TaskDetailDTOList model = taskDetailDTOList.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.upload_info, null);
            TextView user = view.findViewById(R.id.user);
            TextView time = view.findViewById(R.id.time);
            TextView live_detail = view.findViewById(R.id.live_detail);
            TextView live_other = view.findViewById(R.id.live_other);
            LinearLayout ll_pictures = view.findViewById(R.id.ll_pictures);
            LinearLayout ll_videos = view.findViewById(R.id.ll_videos);
            user.setText(model.getCreateUser());
            time.setText(model.getCreateTime());
            live_detail.setText(model.getSiteConditions());
            live_other.setText(model.getOtherConditions());
            List<String> pictures = CommonUtil.parseListString(model.getImgUrl());
            List<String> videos = CommonUtil.parseListString(model.getVideoUrl());
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
            for (int m = 0; m < videos.size(); m++) {
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
            }

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
        if(Objects.equals(status, "0")){
            return "未开始";
        }
        if(Objects.equals(status, "1")){
            return "执行中";
        }
        if(Objects.equals(status, "2")){
            return "已结束";
        }
        if(Objects.equals(status, "3")){
            return "已延期";
        }
        if(Objects.equals(status, "4")){
            return "延期完成";
        }
        if(Objects.equals(status, "5")){
            return "已拒绝";
        }
        return "";
    }
}