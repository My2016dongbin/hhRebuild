package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.NaviSetting;
import com.amap.api.navi.model.AMapNaviLocation;
import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.DialogGridChooseBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteDetailListBinding;
import com.haohai.platform.fireforestplatform.ui.activity.PhotoViewerActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Grid;
import com.haohai.platform.fireforestplatform.ui.multitype.SatelliteFire;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.LatLngChangeNew;

import java.util.ArrayList;
import java.util.List;

public class GridChooseDialog extends Dialog {

    private final Context context;
    private GridChooseDialogListener dialogListener;
    private final DialogGridChooseBinding binding;
    private List<Grid> gridList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private int index = 0;

    public GridChooseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_choose_grid, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(GridChooseDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setGridList(List<Grid> gridList) {
        this.gridList = gridList;
        updateUi();
    }

    @SuppressLint("SetTextI18n")
    private void updateUi() {
        HhLog.e("size = " + gridList.size());

        titleList = new ArrayList<>();
        for (int m = 0; m < gridList.size(); m++) {
            titleList.add(gridList.get(m).getName());
        }
        binding.wheel.setIsLoop(true);
        binding.wheel.setItems(titleList, index);

        binding.wheel.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                index = selectedIndex;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {
        binding.confirm.setOnClickListener(v -> {
            dialogListener.onGridChoose(index);
            dismiss();
        });
        binding.dismiss.setOnClickListener(v -> {
            index = 0;
            binding.wheel.setItems(titleList, index);
            dialogListener.onGridChoose(-1);
            dismiss();
        });
    }

    private void init_() {

    }

    public interface GridChooseDialogListener{
        void onGridChooseDialogRefresh();
        void onGridChoose(int index);
    }
}
