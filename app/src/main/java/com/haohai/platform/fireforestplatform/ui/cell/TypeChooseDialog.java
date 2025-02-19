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
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogGridChooseBinding;
import com.haohai.platform.fireforestplatform.ui.bean.TypeTree;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TypeChooseDialog extends Dialog {

    private final Context context;
    private TypeChooseDialogListener dialogListener;
    private final DialogGridChooseBinding binding;
    private List<TypeTree> treeList = new ArrayList<>();
    private List<TypeTree> treeListSecond = new ArrayList<>();
    private List<TypeTree> treeListThird = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private List<String> titleListSecond = new ArrayList<>();
    private List<String> titleListThird = new ArrayList<>();
    private int treeIndex = 0;
    private int treeIndexSecond = 0;
    private int treeIndexThird = 0;

    public TypeChooseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_choose_grid, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(TypeChooseDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setTreeList(List<TypeTree> treeList) {
        this.treeList = treeList;
        titleList = new ArrayList<>();
        for (int m = 0; m < treeList.size(); m++) {
            TypeTree typeTree = treeList.get(m);
            titleList.add(typeTree.getTypeName());
        }
        treeIndex = 0;
        binding.wheel.setItems(titleList, treeIndex);

        if(!treeList.isEmpty()){
            TypeTree typeTree = treeList.get(0);
            List<TypeTree> children = typeTree.getChildren();
            if(children!=null && !children.isEmpty()){
                treeListSecond = new ArrayList<>();
                treeListSecond.add(new TypeTree("222","-"));
                treeListSecond.addAll(children);
                titleListSecond = new ArrayList<>();
                for (int m = 0; m < treeListSecond.size(); m++) {
                    TypeTree model = treeListSecond.get(m);
                    titleListSecond.add(model.getTypeName());
                }
            }
        }
        binding.wheelSecond.setItems(titleListSecond, treeIndexSecond);
        binding.wheelThird.setItems(titleListThird, treeIndexThird);
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
                dialogListener.onTypeChoose(treeList.get(treeIndex),treeListSecond.get(treeIndexSecond),treeListThird.get(treeIndexThird));
            }catch (Exception e){
                try{
                    dialogListener.onTypeChoose(treeList.get(treeIndex),treeListSecond.get(treeIndexSecond),null);
                }catch (Exception e2){
                    try{
                        dialogListener.onTypeChoose(treeList.get(treeIndex),null,null);
                    }catch (Exception e3){
                        Toast.makeText(context, "请选择类型", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            dismiss();
        });
        binding.reset.setOnClickListener(v -> {
            treeIndex = 0;
            binding.wheel.setItems(titleList, treeIndex);
            dismiss();
        });
        binding.wheel.setOnItemSelectedListener((selectedIndex, item) -> {
            if(treeList==null || treeList.isEmpty()){
                return;
            }
            titleListSecond = new ArrayList<>();
            titleListThird = new ArrayList<>();
            treeIndexSecond = 0;
            treeIndexThird = 0;

            treeIndex = selectedIndex;
            TypeTree typeTree = treeList.get(treeIndex);
            if(typeTree.getChildren()!=null && !typeTree.getChildren().isEmpty()){
                titleListSecond = new ArrayList<>();
                treeListSecond = new ArrayList<>();
                treeListSecond.add(new TypeTree("222","-"));
                treeListSecond.addAll(typeTree.getChildren());
                for (int m = 0; m < treeListSecond.size(); m++) {
                    TypeTree model = treeListSecond.get(m);
                    titleListSecond.add(model.getTypeName());
                }
                treeIndexSecond = 0;
                binding.wheelSecond.setItems(titleListSecond, treeIndexSecond);
                HhLog.e("Wheel " + treeIndexSecond) ;
                HhLog.e("Wheel " + titleListSecond.toString()) ;

            }else{
                //end and output
                treeListSecond = new ArrayList<>();
                titleListSecond = new ArrayList<>();
                treeIndexSecond = 0;
                binding.wheelSecond.setItems(titleListSecond, treeIndexSecond);
                treeListThird = new ArrayList<>();
                titleListThird = new ArrayList<>();
                treeIndexThird = 0;
                binding.wheelThird.setItems(titleListThird, treeIndexThird);
            }
        });
        binding.wheelSecond.setOnItemSelectedListener((selectedIndex, item) -> {
            if(treeListSecond==null || treeListSecond.isEmpty()){
                return;
            }
            titleListThird = new ArrayList<>();
            treeIndexThird = 0;

            treeIndexSecond = selectedIndex;
            TypeTree typeTree = treeListSecond.get(treeIndexSecond);
            //HhLog.e("Second => " + typeTree.getChildren());
            if(typeTree.getChildren()!=null && !typeTree.getChildren().isEmpty()){
                titleListThird = new ArrayList<>();
                treeListThird = new ArrayList<>();
                treeListThird.add(new TypeTree("333","-"));
                treeListThird.addAll(typeTree.getChildren());
                for (int m = 0; m < treeListThird.size(); m++) {
                    TypeTree model = treeListThird.get(m);
                    titleListThird.add(model.getTypeName());
                }
                treeIndexThird = 0;
                binding.wheelThird.setItems(titleListThird, treeIndexThird);
            }else{
                //end and output
                treeListThird = new ArrayList<>();
                titleListThird = new ArrayList<>();
                treeIndexThird = 0;
                binding.wheelThird.setItems(titleListThird, treeIndexThird);

            }
        });
        binding.wheelThird.setOnItemSelectedListener((selectedIndex, item) -> {
            if(treeListThird==null || treeListThird.isEmpty()){
                return;
            }
            treeIndexThird = selectedIndex;
            TypeTree typeTree = treeListThird.get(treeIndexThird);
            //end and output

        });
    }

    @SuppressLint("SetTextI18n")
    private void init_() {

    }


    public interface TypeChooseDialogListener{
        void onTypeChooseDialogRefresh();
        void onTypeChoose(TypeTree typeTree,TypeTree typeTreeSecond,TypeTree typeTreeThird);
    }
}
