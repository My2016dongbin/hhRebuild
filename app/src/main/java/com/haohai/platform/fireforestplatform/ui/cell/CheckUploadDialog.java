package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogCheckUploadBinding;
import com.haohai.platform.fireforestplatform.ui.bean.DisposeResult;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;

import java.util.List;

public class CheckUploadDialog extends Dialog {

    private final Context context;
    private DisposeDialogListener dialogListener;
    private final DialogCheckUploadBinding binding;
    private DisposeResult disposeResult;
    private boolean isReal = false;

    public CheckUploadDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_check_upload, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(DisposeDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setDisposeResult(DisposeResult disposeResult) {
        this.disposeResult = disposeResult;
        updateData();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateData() {
        binding.remarkView.setText(CommonUtil.parseNullString(disposeResult.getRemark(),""));
        updatePictures();
    }

    private void updatePictures() {
        binding.llImage.removeAllViews();
        LayoutInflater mInflater = LayoutInflater.from(context);
        List<DisposeResult.Picture> pictures = disposeResult.getPictures();
        for (int m = 0; m < pictures.size(); m++) {
            DisposeResult.Picture picture = pictures.get(m);
            View mView = mInflater.inflate(R.layout.item_picture, null);
            ImageView image = mView.findViewById(R.id.image);
            ImageView imageDelete = mView.findViewById(R.id.image_delete);

            if (picture.getUri() == null && picture.getUrl() == null) {
                //新增 默认logo
                imageDelete.setVisibility(View.GONE);

            } else if (picture.getUrl() != null) {
                //显示线上图片Url
                Glide.with(context).load(picture.getUrl()).into(image);
                imageDelete.setVisibility(View.VISIBLE);
            } else {
                //更换 显示上次选择的Uri
                Glide.with(context).load(picture.getUri()).into(image);
                imageDelete.setVisibility(View.VISIBLE);
            }

            image.setOnClickListener(v -> {
                if (picture.getUri() == null && picture.getUrl() == null) {
                    //新增
                    dialogListener.addPicture(null);
                } else if (picture.getUrl() != null) {
                    //不支持更换线上图片
                    //Toast.makeText(context, "已上传图片暂不支持更换", Toast.LENGTH_SHORT).show();
                } else {
                    //更换
                    dialogListener.addPicture(picture.getId());
                }
            });
            imageDelete.setOnClickListener(v -> {
                dialogListener.deletePicture(picture.getId());
            });

            binding.llImage.addView(mView);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {
        binding.right.setOnClickListener(v -> {
            if(binding.remarkView.getText().toString().isEmpty()){
                Toast.makeText(context, "请输入核实意见", Toast.LENGTH_SHORT).show();
                return;
            }
            disposeResult.setRemark(binding.remarkView.getText().toString());
            dialogListener.onDisposeDialogResult(disposeResult);
            dismiss();
        });
        binding.left.setOnClickListener(v -> {
            dismiss();
        });
        binding.close.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void init_() {

    }

    public interface DisposeDialogListener {
        void onDisposeDialogResult(DisposeResult disposeResult);
        void addPicture(String id);
        void deletePicture(String id);
    }
}
