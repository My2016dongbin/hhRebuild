package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogCheckBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogOneBodyDetailListBinding;
import com.haohai.platform.fireforestplatform.ui.bean.CheckResult;
import com.haohai.platform.fireforestplatform.ui.bean.HandleResult;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;

import java.util.Objects;

public class CheckDialog extends Dialog {

    private final Context context;
    private CheckDialogListener dialogListener;
    private final DialogCheckBinding binding;
    private CheckResult checkResult;

    public CheckDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_check, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(CheckDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setCheckResult(CheckResult checkResult) {
        this.checkResult = checkResult;
        updateData();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateData() {
        binding.remarkView.setText(CommonUtil.parseNullString(checkResult.getRemark(),""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {

    }

    private void init_() {
        binding.left.setOnClickListener(v -> {
            dismiss();
        });
        binding.right.setOnClickListener(v -> {
            if(binding.remarkView.getText().toString().isEmpty()){
                Toast.makeText(context, "请输入督导意见", Toast.LENGTH_SHORT).show();
                return;
            }
            checkResult.setRemark(binding.remarkView.getText().toString());
            dialogListener.onCheckDialogResult(checkResult);
            dismiss();
        });
        binding.close.setOnClickListener(v -> {
            dismiss();
        });
    }

    public interface CheckDialogListener {
        void onCheckDialogResult(CheckResult checkResult);
    }
}
