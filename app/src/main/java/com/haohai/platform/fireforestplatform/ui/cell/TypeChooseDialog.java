package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogGridChooseBinding;
import java.util.ArrayList;
import java.util.List;

public class TypeChooseDialog extends Dialog {

    private final Context context;
    private TypeChooseDialogListener dialogListener;
    private final DialogGridChooseBinding binding;
    private List<String> treeList = new ArrayList<>();
    private int treeIndex = 0;
    private int code = -1;

    public TypeChooseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_choose_grid, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(TypeChooseDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setTreeList(List<String> treeList,int index,int code) {
        this.treeList = treeList;
        treeIndex = index;
        this.code = code;
        binding.wheel.setItems(treeList, treeIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    @SuppressLint("SetTextI18n")
    private void bind_() {
        binding.confirm.setOnClickListener(v -> {
            try{
                dialogListener.onTypeChoose(treeList.get(treeIndex),treeIndex,code);
            }catch (Exception e3){
                Toast.makeText(context, "请选择类型", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });
        binding.wheel.setOnItemSelectedListener((selectedIndex, item) -> {
            if(treeList==null || treeList.isEmpty()){
                return;
            }

            treeIndex = selectedIndex;
        });
    }

    @SuppressLint("SetTextI18n")
    private void init_() {

    }


    public interface TypeChooseDialogListener{
        void onTypeChooseDialogRefresh();
        void onTypeChoose(String type,int index,int code);
    }
}
