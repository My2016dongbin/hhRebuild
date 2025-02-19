package com.haohai.platform.fireforestplatform.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityEmergencyDetailBinding;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.bean.Audit;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyDetail;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyFile;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyIndex;
import com.haohai.platform.fireforestplatform.ui.bean.TypeBean;
import com.haohai.platform.fireforestplatform.ui.viewmodel.EmergencyDetailViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.huantansheng.easyphotos.constant.Type;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBackgroundMaskClickListener;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialogx.util.TextInfo;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmergencyDetailActivity extends BaseLiveActivity<ActivityEmergencyDetailBinding, EmergencyDetailViewModel> {

    private boolean examine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel().id = getIntent().getStringExtra("id");
        examine = getIntent().getBooleanExtra("examine", false);
        init_();
        bind_();
//        obtainViewModel().postData();
        obtainViewModel().getInfoNew();
        obtainViewModel().getAuditInfo();
    }

    private void init_() {
        binding.topBar.title.setText("突发事件详情");

        if(examine){
            binding.examine.setVisibility(View.VISIBLE);
        }

        initList();
    }

    private void initList() {
        obtainViewModel().areaList = new ArrayList<>();
        obtainViewModel().areaList.add(new TypeBean("370203005", "辽宁路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203008", "延安路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203011", "登州路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203013", "宁夏路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203014", "敦化路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203015", "辽源路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203016", "合肥路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203019", "大港街道"));
        obtainViewModel().areaList.add(new TypeBean("370203020", "即墨路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203021", "台东街道"));
        obtainViewModel().areaList.add(new TypeBean("370203022", "镇江路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203025", "浮山新区街道"));
        obtainViewModel().areaList.add(new TypeBean("370203026", "阜新路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203027", "海伦路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203028", "四方街道"));
        obtainViewModel().areaList.add(new TypeBean("370203029", "兴隆路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203030", "水清沟街道"));
        obtainViewModel().areaList.add(new TypeBean("370203031", "洛阳路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203032", "河西街道"));
        obtainViewModel().areaList.add(new TypeBean("370203033", "湖岛街道"));
        obtainViewModel().areaList.add(new TypeBean("370203034", "开平路街道"));
        obtainViewModel().areaList.add(new TypeBean("370203035", "双山街道"));


        obtainViewModel().levelList = new ArrayList<>();
        obtainViewModel().levelList.add(new TypeBean("5","暂未确认等级"));
        obtainViewModel().levelList.add(new TypeBean("4","Ⅳ级（一般）"));
        obtainViewModel().levelList.add(new TypeBean("3","Ⅲ级（较大）"));
        obtainViewModel().levelList.add(new TypeBean("2","Ⅱ级（重大）"));
        obtainViewModel().levelList.add(new TypeBean("1","Ⅰ级（特别重大）"));
    }

    private final String examineStr = "请输入审核意见";
    private void bind_() {
        binding.examine.setOnClickListener(v -> {
            new InputDialog("审核结果", "请问审核是否通过？", "通过", "驳回", "取消","")
                    .setCancelable(true)
                    .setOkButtonClickListener(new OnInputDialogButtonClickListener<InputDialog>() {
                        @Override
                        public boolean onClick(InputDialog dialog, View v, String inputStr) {
                            if(inputStr.isEmpty()){
                                Toast.makeText(EmergencyDetailActivity.this, examineStr, Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            obtainViewModel().pass_(inputStr,"pass");
                            return false;
                        }
                    })
                    .setCancelButtonClickListener(new OnInputDialogButtonClickListener<InputDialog>() {
                        @Override
                        public boolean onClick(InputDialog dialog, View v, String inputStr) {
                            if(inputStr.isEmpty()){
                                Toast.makeText(EmergencyDetailActivity.this, examineStr, Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            obtainViewModel().pass_(inputStr,"reject");
                            return false;
                        }
                    })
                    .setInputHintText(examineStr)
                    .setOnBackgroundMaskClickListener(new OnBackgroundMaskClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            return false;
                        }
                    })
                    .show();
        });
        binding.textTitle.setOnClickListener(v -> {
//            chooseEmergency();
        });
    }

    private void chooseEmergency() {
        CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_emergency_choose) {
            @Override
            public void onBind(final CustomDialog dialog, View v) {
                LinearLayout ll_type;
                TextView text_close;
                TextView text_confirm;
                ll_type = v.findViewById(R.id.ll_type);
                text_close = v.findViewById(R.id.text_close);
                text_confirm = v.findViewById(R.id.text_confirm);
                ll_type.removeAllViews();
                for (int i = 0; i < obtainViewModel().dataList.size(); i++) {
                    EmergencyIndex emergencyIndex = obtainViewModel().dataList.get(i);
                    View p_ = LayoutInflater.from(EmergencyDetailActivity.this).inflate(R.layout.item_emergency_choose, null);
                    LinearLayout all = p_.findViewById(R.id.all);
                    ImageView icon = p_.findViewById(R.id.icon);
                    TextView title = p_.findViewById(R.id.title);
                    title.setText(emergencyIndex.getIncidentTitle());
                    if (emergencyIndex.isChecked()) {
                        icon.setImageResource(R.drawable.yes);
                    } else {
                        icon.setImageResource(R.drawable.no);
                    }
                    all.setOnClickListener(v15 -> {
                        if (!emergencyIndex.isChecked()) {
                            obtainViewModel().emergencyIndex = emergencyIndex;
                            for (int m = 0; m < obtainViewModel().dataList.size(); m++) {
                                obtainViewModel().dataList.get(m).setChecked(false);
                            }
                        }
                        emergencyIndex.setChecked(!emergencyIndex.isChecked());
                        onBind(dialog, v);
                    });

                    ll_type.addView(p_);
                }
                text_close.setOnClickListener(v13 -> {
                    dialog.dismiss();
                });
                text_confirm.setOnClickListener(v14 -> {
                    int tag = 0;
                    for (int i = 0; i < obtainViewModel().dataList.size(); i++) {
                        EmergencyIndex emergencyIndex = obtainViewModel().dataList.get(i);
                        if (emergencyIndex.isChecked()) {
                            tag++;
                        }
                    }
                    dialog.dismiss();
                    if (tag > 0 && obtainViewModel().emergencyIndex != null) {
                        binding.textTitle.setText(obtainViewModel().emergencyIndex.getIncidentTitle());
                        obtainViewModel().getInfo();
                    }
                });

            }
        }).setOnBackgroundMaskClickListener((dialog, v12) -> {
            return false;
        }).setMaskColor(getResources().getColor(R.color.transparent_dialog))
                .setCancelable(true);
    }

    @Override
    protected ActivityEmergencyDetailBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_emergency_detail);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public EmergencyDetailViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(EmergencyDetailViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().examineTag.observe(this, integer -> binding.examine.setVisibility(View.GONE));
        obtainViewModel().detailChanged.observe(this, this::detailUi);
        obtainViewModel().auditData.observe(this, this::updateAudits);
    }

    private void updateAudits(List<Audit> audits) {
        if(audits!=null && audits.size()>0){
            binding.llAuditAll.setVisibility(View.VISIBLE);
            binding.llAudit.removeAllViews();
            for (int m = 0; m < audits.size(); m++) {
                Audit audit = audits.get(m);
                View item = LayoutInflater.from(EmergencyDetailActivity.this).inflate(R.layout.item_audit, null);
                TextView person = item.findViewById(R.id.text_person);
                TextView time = item.findViewById(R.id.text_time);
                TextView info = item.findViewById(R.id.text_info);
                person.setText(audit.getAuditName());
                time.setText(audit.getUpdateTime());
                info.setText(audit.getAuditOpinion());
                binding.llAudit.addView(item);
            }
        }else{
            binding.llAuditAll.setVisibility(View.GONE);
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void detailUi(EmergencyDetail emergencyDetail) {
        HhLog.e("detailUi " + emergencyDetail.getIncidentTitle());
        if (/*obtainViewModel().emergencyIndex != null*/true) {
            binding.textTitle.setText(CommonUtil.parseNullString(emergencyDetail.getIncidentTitle(), "-"));
            binding.textType.setText(CommonUtil.parseNullString(emergencyDetail.getIncidentTypeName(), "-"));
            binding.textDate.setText(CommonUtil.parseDate(emergencyDetail.getEmergIncidentVersion().getReportingTime()));
            //binding.textArea.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getGroupName(), "-"));
            binding.textArea.setText(CommonUtil.parseNullString(parseAreaCode(emergencyDetail.getIncidentArea()), emergencyDetail.getGroupName()));
            binding.textAddress.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getIncidentAddr(), "-"));
            binding.textProperty.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getPropertyLoss(), "-"));
            binding.textDead.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getDeath(), "-"));
            binding.textInjured.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getInjured(), "-"));
            binding.textSeries.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getSeriousInjured(), "-"));
            binding.textDanger.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getMissing(), "-"));
            binding.textLevel.setText(parseLevel(emergencyDetail.getEmergIncidentVersion().getIncidentLevel()));
            binding.textSpecial.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getSensitive(), "-"));

            binding.textReporter.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getReporter(), "-"));
            binding.textPhone.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getReporterPhone(), "-"));
            binding.textLeader.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getCharger(), "-"));
            binding.textLeaderPhone.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getChargerPhone(), "-"));
            binding.textDetail.setText(CommonUtil.parseNullString(emergencyDetail.getEmergIncidentVersion().getIncidentDetails(), "-"));


            try{
                List<EmergencyFile> fileList = emergencyDetail.getFileList();
                binding.llFiles.removeAllViews();
                for (int m = 0; m < fileList.size(); m++) {
                    EmergencyFile file = fileList.get(m);
                    View view = LayoutInflater.from(this).inflate(R.layout.file,null);
                    TextView fileTitle = view.findViewById(R.id.file_title);
                    TextView fileReview = view.findViewById(R.id.file_review);
                    ImageView fileImage = view.findViewById(R.id.file_image);
                    fileImage.setImageDrawable(parseDrawable(file.getName()));
                    fileTitle.setText(file.getName());
                    fileReview.setOnClickListener(v -> {
                        jumpFile(file);
                    });
                    binding.llFiles.addView(view);
                }
                if(fileList.isEmpty()){
                    binding.fileImage.setImageDrawable(getResources().getDrawable(R.drawable.file_download_un));
                    binding.fileText.setTextColor(getResources().getColor(R.color.c4));
                }else{
                    binding.fileImage.setImageDrawable(getResources().getDrawable(R.drawable.file_download));
                    binding.fileText.setTextColor(getResources().getColor(R.color.theme_color_blue));
                }
                binding.llDownload.setOnClickListener(v -> {
                    downloadFiles(fileList);
                });
            }catch (Exception e){
                HhLog.e(e.getMessage());
            }
        }
    }

    private void downloadFiles(List<EmergencyFile> fileList) {
        if(fileList==null || fileList.size()==0){
            return;
        }
        MessageDialog.show("温馨提示", "确定下载附件吗？","下载","取消")
                .setButtonOrientation(LinearLayout.HORIZONTAL)
                .setOkTextInfo(new TextInfo().setFontColor(getResources().getColor(R.color.text_color_red)))
                .setOkButtonClickListener((dialog, v1) -> {
                    Toast.makeText(this, "文件即将下载至目录"+ Environment.getExternalStorageDirectory().getPath()+"/,请稍后查看...", Toast.LENGTH_SHORT).show();
                    for (int m = 0; m < fileList.size(); m++) {
                        download(fileList.get(m).getUrl());
                    }
                    return false;
                })
                .setCancelable(true);
    }


    private String parseLevel(String incidentLevel) {
        for (int m = 0; m < obtainViewModel().levelList.size(); m++) {
            if(Objects.equals(obtainViewModel().levelList.get(m).getId(), incidentLevel)){
                return obtainViewModel().levelList.get(m).getTitle();
            }
        }
        return "-";
    }

    private void download(String fileUrl) {
        FileDownloader.getImpl().create(fileUrl)
                .setPath(Environment.getExternalStorageDirectory().getPath())
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        HhLog.e("下载完成:" + fileUrl);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable parseDrawable(String name) {
        if(name.contains("doc")){
            return getResources().getDrawable(R.drawable.file_doc);
        }
        if(name.contains("jpg")){
            return getResources().getDrawable(R.drawable.file_jpg);
        }
        return getResources().getDrawable(R.drawable.file_default);
    }

    private String parseArea(String areaName) {
        String areaNo = "";
        for (int m = 0; m < obtainViewModel().areaList.size(); m++) {
            TypeBean typeBean = obtainViewModel().areaList.get(m);
            if (Objects.equals(typeBean.getTitle(), areaName)) {
                areaNo = typeBean.getId();
            }
        }
        return areaNo;
    }

    private String parseAreaCode(String areaCode) {
        String areaName = "";
        for (int m = 0; m < obtainViewModel().areaList.size(); m++) {
            TypeBean typeBean = obtainViewModel().areaList.get(m);
            HhLog.e("parseAreaCode " + areaCode + " , " + typeBean.getId());
            if (Objects.equals(typeBean.getId(), areaCode)) {
                areaName = typeBean.getTitle();
            }
        }
        return areaName;
    }

    private void jumpFile(EmergencyFile file) {
        Intent intent = new Intent(this,TbsActivity.class);
        intent.putExtra("url",file.getUrl());
        intent.putExtra("title",file.getName());
        startActivity(intent);
    }
}