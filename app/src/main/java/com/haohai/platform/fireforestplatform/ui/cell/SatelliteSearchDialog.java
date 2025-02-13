package com.haohai.platform.fireforestplatform.ui.cell;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteDetailListBinding;
import com.haohai.platform.fireforestplatform.databinding.DialogSatelliteSearchBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SatelliteSearchDialog extends Dialog {

    private final Context context;
    private SatelliteSearchDialogListener dialogListener;
    private final DialogSatelliteSearchBinding binding;

    public SatelliteSearchDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_satellite_search, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(SatelliteSearchDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {
        binding.oneHour.setOnClickListener(v -> {
            hide();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String endTime = format.format(c.getTime()).replace(" ", "T");
            c.add(Calendar.HOUR, -1);//获取默认小时之前的时间
            String startTime = format.format(c.getTime()).replace(" ", "T");
            dialogListener.onSatelliteSearchDialogRefresh(startTime,endTime);
        });
        binding.threeHour.setOnClickListener(v -> {
            hide();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String endTime = format.format(c.getTime()).replace(" ", "T");
            c.add(Calendar.HOUR, -3);//获取默认小时之前的时间
            String startTime = format.format(c.getTime()).replace(" ", "T");
            dialogListener.onSatelliteSearchDialogRefresh(startTime,endTime);
        });
        binding.oneDay.setOnClickListener(v -> {
            hide();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String endTime = format.format(c.getTime()).replace(" ", "T");
            c.add(Calendar.DAY_OF_MONTH, -1);//获取默认小时之前的时间
            String startTime = format.format(c.getTime()).replace(" ", "T");
            dialogListener.onSatelliteSearchDialogRefresh(startTime,endTime);
        });
        binding.threeDay.setOnClickListener(v -> {
            hide();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String endTime = format.format(c.getTime()).replace(" ", "T");
            c.add(Calendar.DAY_OF_MONTH, -3);//获取默认小时之前的时间
            String startTime = format.format(c.getTime()).replace(" ", "T");
            dialogListener.onSatelliteSearchDialogRefresh(startTime,endTime);
        });
        binding.fiveDay.setOnClickListener(v -> {
            hide();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String endTime = format.format(c.getTime()).replace(" ", "T");
            c.add(Calendar.DAY_OF_MONTH, -5);//获取默认小时之前的时间
            String startTime = format.format(c.getTime()).replace(" ", "T");
            dialogListener.onSatelliteSearchDialogRefresh(startTime,endTime);
        });
        binding.advanced.setOnClickListener(v -> {
            hide();
            dialogListener.onSatelliteSearchDialogRefresh(null,null);
        });
    }

    private void init_() {

    }



    public interface SatelliteSearchDialogListener{
        void onSatelliteSearchDialogRefresh(String startTime,String endTime);
    }
}
