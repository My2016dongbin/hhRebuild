package com.haohai.platform.fireforestplatform.ui.cell;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteDetailListBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteSettingBinding;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

public class SatelliteSettingDialog extends Dialog {

    private final Context context;
    private SatelliteSettingDialogListener dialogListener;
    private final DialogSatelliteSettingBinding binding;
    private final List<String> satelliteList = new ArrayList<>();
    private final List<String> landformsList = new ArrayList<>();
    private final List<String> fireCountsList = new ArrayList<>();
    private final List<String> otherList = new ArrayList<>();
    private Set<Integer> satelliteSet = new HashSet<>();
    private Set<Integer> landformsSet = new HashSet<>();
    private Set<Integer> fireCountsSet = new HashSet<>();
    private Set<Integer> otherSet = new HashSet<>();
    private TagAdapter<String> satelliteAdapter;
    private TagAdapter<String> landformsAdapter;
    private TagAdapter<String> fireCountsAdapter;
    private TagAdapter<String> otherAdapter;

    public SatelliteSettingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_satellite_setting, null, false);
        setContentView(binding.getRoot());
    }

    public void setSatelliteSet(Set<Integer> satelliteSet) {
        this.satelliteSet = satelliteSet;
        if(satelliteAdapter!=null){
            satelliteAdapter.setSelectedList(satelliteSet);
        }
    }

    public void setLandformsSet(Set<Integer> landformsSet) {
        this.landformsSet = landformsSet;
        if(landformsAdapter!=null){
            landformsAdapter.setSelectedList(landformsSet);
        }
    }

    public void setFireCountsSet(Set<Integer> fireCountsSet) {
        this.fireCountsSet = fireCountsSet;
        if(fireCountsAdapter!=null){
            fireCountsAdapter.setSelectedList(fireCountsSet);
        }
    }

    public void setOtherSet(Set<Integer> otherSet) {
        this.otherSet = otherSet;
        if(otherAdapter!=null){
            otherAdapter.setSelectedList(otherSet);
        }
    }

    public void setDialogListener(SatelliteSettingDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        init_();
        bind_();
    }

    private void bind_() {
        binding.satelliteFlowlayout.setOnSelectListener(selectPosSet -> {
            boolean adding = selectPosSet.size()>satelliteSet.size();
            satelliteSet = selectPosSet;
            if(adding){
                //增且含全部
                if(selectPosSet.contains(0)){
                    Set<Integer> set = new HashSet<>();
                    for (int i = 0; i < satelliteList.size(); i++) {
                        set.add(i);
                    }
                    satelliteAdapter.setSelectedList(set);
                    satelliteSet = set;
                }
                //增且只差全部
                if(selectPosSet.size()==satelliteList.size()-1 && !selectPosSet.contains(0)){
                    Set<Integer> set = new HashSet<>();
                    for (int i = 0; i < satelliteList.size(); i++) {
                        set.add(i);
                    }
                    satelliteAdapter.setSelectedList(set);
                    satelliteSet = set;
                }
            }else{
                //减且减完含有全部
                if(selectPosSet.contains(0)){
                    HashSet<Integer> set = new HashSet<>(selectPosSet);
                    set.remove(0);
                    satelliteAdapter.setSelectedList(set);
                    satelliteSet = set;
                }
                //减且只去除全部
                if(selectPosSet.size()==satelliteList.size()-1 && !selectPosSet.contains(0)){
                    HashSet<Integer> set = new HashSet<>();
                    satelliteAdapter.setSelectedList(set);
                    satelliteSet = set;
                }

            }
//                Toast.makeText(context, satelliteSet.toString(), Toast.LENGTH_SHORT).show();

        });
        binding.landformsFlowlayout.setOnSelectListener(selectPosSet -> {
            boolean adding = selectPosSet.size()>landformsSet.size();
            landformsSet = selectPosSet;
            if(adding){
                //增且含全部
                if(selectPosSet.contains(0)){
                    Set<Integer> set = new HashSet<>();
                    for (int i = 0; i < landformsList.size(); i++) {
                        set.add(i);
                    }
                    landformsAdapter.setSelectedList(set);
                    landformsSet = set;
                }
                //增且只差全部
                if(selectPosSet.size()==landformsList.size()-1 && !selectPosSet.contains(0)){
                    Set<Integer> set = new HashSet<>();
                    for (int i = 0; i < landformsList.size(); i++) {
                        set.add(i);
                    }
                    landformsAdapter.setSelectedList(set);
                    landformsSet = set;
                }
            }else{
                //减且减完含有全部
                if(selectPosSet.contains(0)){
                    HashSet<Integer> set = new HashSet<>(selectPosSet);
                    set.remove(0);
                    landformsAdapter.setSelectedList(set);
                    landformsSet = set;
                }
                //减且只去除全部
                if(selectPosSet.size()==landformsList.size()-1 && !selectPosSet.contains(0)){
                    HashSet<Integer> set = new HashSet<>();
                    landformsAdapter.setSelectedList(set);
                    landformsSet = set;
                }

            }

        });
        binding.fireCountsFlowlayout.setOnSelectListener(selectPosSet -> fireCountsSet = selectPosSet);
        binding.otherFlowlayout.setOnSelectListener(selectPosSet -> otherSet = selectPosSet);
        binding.reset.setOnClickListener(v -> {
            satelliteSet.clear();
            satelliteAdapter.setSelectedList(satelliteSet);
            landformsSet.clear();
            landformsAdapter.setSelectedList(landformsSet);
            fireCountsSet.clear();
            fireCountsAdapter.setSelectedList(fireCountsSet);
            otherSet.clear();
            otherAdapter.setSelectedList(otherSet);
            Toast.makeText(context, "已重置", Toast.LENGTH_SHORT).show();
        });
        binding.confirm.setOnClickListener(v -> {
            Set<String> set_satellite = new HashSet<>();
            for (Integer i:satelliteSet) {
                set_satellite.add(i.toString());
            }
            SPUtils.put(context, SPValue.satelliteSet,set_satellite);
            Set<String> set_landforms = new HashSet<>();
            for (Integer i:landformsSet) {
                set_landforms.add(i.toString());
            }
            SPUtils.put(context, SPValue.landformsSet,set_landforms);
            Set<String> set_fireCounts = new HashSet<>();
            for (Integer i:fireCountsSet) {
                set_fireCounts.add(i.toString());
            }
            SPUtils.put(context, SPValue.fireCountsSet,set_fireCounts);
            Set<String> set_other = new HashSet<>();
            for (Integer i:otherSet) {
                set_other.add(i.toString());
            }
            SPUtils.put(context, SPValue.otherSet,set_other);
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
            hide();
        });
    }

    private void init_() {
        satelliteAdapter = new TagAdapter<String>(satelliteList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.tv,
                        binding.satelliteFlowlayout, false);
                tv.setText(s);
                return tv;
            }
        };
        binding.satelliteFlowlayout.setAdapter(satelliteAdapter);
        landformsAdapter = new TagAdapter<String>(landformsList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.tv,
                        binding.landformsFlowlayout, false);
                tv.setText(s);
                return tv;
            }
        };
        binding.landformsFlowlayout.setAdapter(landformsAdapter);
        fireCountsAdapter = new TagAdapter<String>(fireCountsList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.tv,
                        binding.fireCountsFlowlayout, false);
                tv.setText(s);
                return tv;
            }
        };
        binding.fireCountsFlowlayout.setAdapter(fireCountsAdapter);
        otherAdapter = new TagAdapter<String>(otherList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.tv,
                        binding.otherFlowlayout, false);
                tv.setText(s);
                return tv;
            }
        };
        binding.otherFlowlayout.setAdapter(otherAdapter);
    }

    private void initData() {
        satelliteList.add("全部");
        satelliteList.add("NPP");
        satelliteList.add("FY-4");
        satelliteList.add("FY-3");
        satelliteList.add("Himawari-8");
        satelliteList.add("NOAA-8");
        satelliteList.add("NOAA-19");

        landformsList.add("全部");
        landformsList.add("林地");
        landformsList.add("草地");
        landformsList.add("农田");
        landformsList.add("其他");

        fireCountsList.add("100");
        fireCountsList.add("500");
        fireCountsList.add("1000");
        fireCountsList.add("2000");
        fireCountsList.add("5000");

        otherList.add("查询境外热源");
        otherList.add("包含缓冲区");
        otherList.add("持续报警");
    }


    public interface SatelliteSettingDialogListener{
        void onSatelliteSettingDialogRefresh();
    }
}
