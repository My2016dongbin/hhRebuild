package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogWorkerDetail2ListBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.WorkerDetail;

import java.util.List;

public class WorkerDetail2Dialog extends Dialog{

    private final Context context;
    private WorkerDetailDialogListener dialogListener;
    private final DialogWorkerDetail2ListBinding binding;
    private WorkerDetail.Worker worker;

    public WorkerDetail2Dialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_worker_detail2, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(WorkerDetailDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setWorkerDetail(WorkerDetail.Worker worker, int index) {
        this.worker = worker;
        updateData(index);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateData(int index) {
        if(index < 0){
            binding.dayLeaderName.setText("");
            binding.dayLeaderPhone.setText("");
            binding.dayWorkerName.setText("");
            binding.dayWorkerPhone.setText("");
            binding.nightLeaderName.setText("");
            binding.nightLeaderPhone.setText("");
            binding.nightWorkerName.setText("");
            binding.nightWorkerPhone.setText("");
            return;
        }
        binding.ll1.setVisibility(View.GONE);
        binding.ll2.setVisibility(View.GONE);
        binding.ll3.setVisibility(View.GONE);
        binding.ll4.setVisibility(View.GONE);
        binding.llDay.setVisibility(View.GONE);
        binding.llNight.setVisibility(View.GONE);
        if(index == 0 ){
            binding.dayLeaderName.setText(worker.getArrangeName());
            binding.dayLeaderPhone.setText(worker.getArrangePhone());
            binding.dayLeaderCall.setOnClickListener(v -> {
                call_(worker.getArrangePhone());
            });

            binding.ll1.setVisibility(View.VISIBLE);
            binding.llDay.setVisibility(View.VISIBLE);
        }
        if(index == 1 ){
            binding.dayWorkerName.setText(worker.getArrangeName());
            binding.dayWorkerPhone.setText(worker.getArrangePhone());
            binding.dayWorkerCall.setOnClickListener(v -> {
                call_(worker.getArrangePhone());
            });
            binding.ll2.setVisibility(View.VISIBLE);
            binding.llDay.setVisibility(View.VISIBLE);
        }
        if(index == 2 ){
            binding.nightLeaderName.setText(worker.getArrangeName());
            binding.nightLeaderPhone.setText(worker.getArrangePhone());
            binding.nightLeaderCall.setOnClickListener(v -> {
                call_(worker.getArrangePhone());
            });
            binding.ll3.setVisibility(View.VISIBLE);
            binding.llNight.setVisibility(View.VISIBLE);
        }
        if(index == 3 ){
            binding.nightWorkerName.setText(worker.getArrangeName());
            binding.nightWorkerPhone.setText(worker.getArrangePhone());
            binding.nightWorkerCall.setOnClickListener(v -> {
                call_(worker.getArrangePhone());
            });
            binding.ll4.setVisibility(View.VISIBLE);
            binding.llNight.setVisibility(View.VISIBLE);
        }
    }

    private void call_(String phone) {
        try{
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (Exception e){
            Toast.makeText(context, "您的设备不支持拨打电话", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {
        binding.close.setOnClickListener(v -> {
            hide();
        });
    }

    private void init_() {

    }


    public interface WorkerDetailDialogListener{
        void onWorkerDetailDialogRefresh();
    }
}
