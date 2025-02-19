package com.haohai.platform.fireforestplatform.ui.cell;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogTreeBinding;
import com.haohai.platform.fireforestplatform.ui.multitype.GridCamera;
import com.haohai.platform.fireforestplatform.ui.multitype.GridCameraViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.GridModel;
import com.haohai.platform.fireforestplatform.ui.multitype.GridModelViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.GridTrees;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.GridTreesViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.RangerGridViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.DialogTreeViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class VideoTreeDialog extends Dialog implements GridTreesViewBinder.OnItemClickListener, GridModelViewBinder.OnItemClickListener, GridCameraViewBinder.OnItemClickListener {

    private final Context context;
    private DialogListener dialogListener;
    private final DialogTreeViewModel.VideoTreeCallback listener;
    private final DialogTreeBinding binding;
    private DialogTreeViewModel viewModel;

    public VideoTreeDialog(@NonNull Context context, @StyleRes int themeResId,  DialogTreeViewModel.VideoTreeCallback listener) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_video_tree, null, false);
        setContentView(binding.getRoot());
        binding.setTree(viewModel);
        viewModel = new DialogTreeViewModel();
        viewModel.start(context);
        viewModel.setListener(listener);
        this.listener = listener;
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        binding.mainSmart.autoRefresh();
        binding.mainSmartKk.autoRefresh();
        binding.mainSmartStar.autoRefresh();
    }

    private void bind_() {
        binding.editFind.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search();
                return true;
            }
        });
        binding.editFind.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.search = s.toString();
                CommonData.search = viewModel.search;
            }
        });
        binding.llLeft.setOnClickListener(v -> {
            binding.tvLeft.setTextColor(context.getResources().getColor(R.color.c7));
            binding.tvMid.setTextColor(context.getResources().getColor(R.color.c5));
            binding.tvRight.setTextColor(context.getResources().getColor(R.color.c5));
            binding.vLeft.setVisibility(View.VISIBLE);
            binding.vMid.setVisibility(View.GONE);
            binding.vRight.setVisibility(View.GONE);
            viewModel.tabStatus = 0;
            viewModel.updateData();

            binding.mainSmart.setVisibility(View.VISIBLE);
            binding.mainSmartKk.setVisibility(View.GONE);
            binding.mainSmartStar.setVisibility(View.GONE);
        });
        binding.llMid.setOnClickListener(v -> {
            binding.tvMid.setTextColor(context.getResources().getColor(R.color.c7));
            binding.tvLeft.setTextColor(context.getResources().getColor(R.color.c5));
            binding.tvRight.setTextColor(context.getResources().getColor(R.color.c5));
            binding.vMid.setVisibility(View.VISIBLE);
            binding.vLeft.setVisibility(View.GONE);
            binding.vRight.setVisibility(View.GONE);
            viewModel.tabStatus = 1;
            viewModel.updateDataKK();

            binding.mainSmart.setVisibility(View.GONE);
            binding.mainSmartKk.setVisibility(View.VISIBLE);
            binding.mainSmartStar.setVisibility(View.GONE);
        });
        binding.llRight.setOnClickListener(v -> {
            binding.tvRight.setTextColor(context.getResources().getColor(R.color.c7));
            binding.tvLeft.setTextColor(context.getResources().getColor(R.color.c5));
            binding.tvMid.setTextColor(context.getResources().getColor(R.color.c5));
            binding.vRight.setVisibility(View.VISIBLE);
            binding.vLeft.setVisibility(View.GONE);
            binding.vMid.setVisibility(View.GONE);
            viewModel.tabStatus = 2;
            viewModel.updateDataStar();

            binding.mainSmart.setVisibility(View.GONE);
            binding.mainSmartKk.setVisibility(View.GONE);
            binding.mainSmartStar.setVisibility(View.VISIBLE);
        });
    }

    private void search() {
        dialogListener.closeInputListener(binding.editFind);
        if(viewModel.search==null || viewModel.search.isEmpty()){
            //Toast.makeText(context, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            if (viewModel.tabStatus == 0) {
                viewModel.getTreesData(viewModel.tabStatus);
            }else if(viewModel.tabStatus == 1){
                viewModel.getTreesDataKK(viewModel.tabStatus);
            }else if(viewModel.tabStatus == 2){
                viewModel.getTreesDataStar(viewModel.tabStatus);
            }
            return;
        }
        viewModel.searchRefresh(viewModel.tabStatus,true);
    }

    private void init_() {
        //监控
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        binding.rlv.setLayoutManager(linearLayoutManager);
        viewModel.adapter = new MultiTypeAdapter(viewModel.items);
        //下拉刷新
        binding.mainSmart.setRefreshFooter(new BallPulseFooter(context).setSpinnerStyle(SpinnerStyle.Scale));
        binding.mainSmart.setRefreshHeader(new MaterialHeader(context).setShowBezierWave(true));
        binding.mainSmart.setEnableLoadMore(false);
        //设置样式后面的背景颜色
        binding.mainSmart.setPrimaryColorsId(R.color.theme_color, android.R.color.white)
                .setBackgroundColor(context.getResources().getColor(R.color.background_colorf3));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.mainSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                dialogListener.onDialogRefresh();
                viewModel.getTreesData(viewModel.tabStatus);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });

        GridTreesViewBinder gridTreesViewBinder = new GridTreesViewBinder(context);
        gridTreesViewBinder.setListener(this,this,this);
        viewModel.adapter.register(GridTrees.class, gridTreesViewBinder);
        viewModel.adapter.register(Empty.class, new EmptyViewBinder(context));
        binding.rlv.setAdapter(viewModel.adapter);
        assertHasTheSameAdapter(binding.rlv, viewModel.adapter);
        viewModel.adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.ALLOW);


        //卡口
        LinearLayoutManager linearLayoutManager_KK = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        binding.rlvKk.setLayoutManager(linearLayoutManager_KK);
        viewModel.adapter_KK = new MultiTypeAdapter(viewModel.items_KK);
        //下拉刷新
        binding.mainSmartKk.setRefreshFooter(new BallPulseFooter(context).setSpinnerStyle(SpinnerStyle.Scale));
        binding.mainSmartKk.setRefreshHeader(new MaterialHeader(context).setShowBezierWave(true));
        binding.mainSmartKk.setEnableLoadMore(false);
        //设置样式后面的背景颜色
        binding.mainSmartKk.setPrimaryColorsId(R.color.theme_color, android.R.color.white)
                .setBackgroundColor(context.getResources().getColor(R.color.background_colorf3));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.mainSmartKk.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                dialogListener.onDialogRefresh();
                viewModel.getTreesDataKK(viewModel.tabStatus);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });

        GridTreesViewBinder gridTreesViewBinder_KK = new GridTreesViewBinder(context);
        gridTreesViewBinder_KK.setListener(this,this,this);
        viewModel.adapter_KK.register(GridTrees.class, gridTreesViewBinder_KK);
        viewModel.adapter_KK.register(Empty.class, new EmptyViewBinder(context));
        binding.rlvKk.setAdapter(viewModel.adapter_KK);
        assertHasTheSameAdapter(binding.rlvKk, viewModel.adapter_KK);
        viewModel.adapter_KK.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.ALLOW);


        //收藏
        LinearLayoutManager linearLayoutManager_Star = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        binding.rlvStar.setLayoutManager(linearLayoutManager_Star);
        viewModel.adapter_Star = new MultiTypeAdapter(viewModel.items_Star);
        //下拉刷新
        binding.mainSmartStar.setRefreshFooter(new BallPulseFooter(context).setSpinnerStyle(SpinnerStyle.Scale));
        binding.mainSmartStar.setRefreshHeader(new MaterialHeader(context).setShowBezierWave(true));
        binding.mainSmartStar.setEnableLoadMore(false);
        //设置样式后面的背景颜色
        binding.mainSmartStar.setPrimaryColorsId(R.color.theme_color, android.R.color.white)
                .setBackgroundColor(context.getResources().getColor(R.color.background_colorf3));

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.mainSmartStar.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                dialogListener.onDialogRefresh();
                viewModel.getTreesDataStar(viewModel.tabStatus);
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });

        GridTreesViewBinder gridTreesViewBinder_Star = new GridTreesViewBinder(context);
        gridTreesViewBinder_Star.setListener(this,this,this);
        viewModel.adapter_Star.register(GridTrees.class, gridTreesViewBinder_Star);
        viewModel.adapter_Star.register(Empty.class, new EmptyViewBinder(context));
        binding.rlvStar.setAdapter(viewModel.adapter_Star);
        assertHasTheSameAdapter(binding.rlvStar, viewModel.adapter_Star);
        viewModel.adapter_Star.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.ALLOW);
    }

    @Override
    public void onItemClick(GridTrees gridTrees, boolean state) {
        if(viewModel.tabStatus==0){
            int offset = binding.rlv.computeVerticalScrollOffset();
            viewModel.updateData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlv.offsetChildrenVertical(-1*offset);
                }
            },2);
        }else if(viewModel.tabStatus==1){
            int offset = binding.rlvKk.computeVerticalScrollOffset();
            viewModel.updateDataKK();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlvKk.offsetChildrenVertical(-1*offset);
                }
            },2);
        }else if(viewModel.tabStatus==2){
            int offset = binding.rlvStar.computeVerticalScrollOffset();
            viewModel.updateDataStar();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlvStar.offsetChildrenVertical(-1*offset);
                }
            },2);
        }
    }

    @Override
    public void onItemClick(GridModel gridModel, boolean state) {
        if(viewModel.tabStatus==0){
            int offset = binding.rlv.computeVerticalScrollOffset();
            viewModel.updateData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlv.offsetChildrenVertical(-1*offset);
                }
            },2);
        }else if(viewModel.tabStatus==1){
            int offset = binding.rlvKk.computeVerticalScrollOffset();
            viewModel.updateDataKK();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlvKk.offsetChildrenVertical(-1*offset);
                }
            },2);
        }else if(viewModel.tabStatus==2){
            int offset = binding.rlvStar.computeVerticalScrollOffset();
            viewModel.updateDataStar();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlvStar.offsetChildrenVertical(-1*offset);
                }
            },2);
        }
    }

    @Override
    public void onItemClick(GridCamera gridCamera) {
        //获取流后跳转播放 monitorId->getVideoId channelId->getMonitorId
        //viewModel.getStream(gridCamera.getId(),  "be1d755c-ceab-46cc-b6dc-046c933d8360","89e8c6f4-8d0c-4415-9802-37c60f7cd9de",gridCamera.getControlDeviceId());
        viewModel.getStream(gridCamera.getId(), gridCamera.getMonitorId(), gridCamera.getId());
        dialogListener.hideTreeDialog();
    }

    @Override
    public void onStarClick(GridCamera gridCamera) {

        if(viewModel.tabStatus==0){
            //刷新收藏列表
            viewModel.getTreesDataStar(2);
            int offset = binding.rlv.computeVerticalScrollOffset();
            viewModel.updateData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlv.offsetChildrenVertical(-1*offset);
                }
            },2);
        }else if(viewModel.tabStatus==1){
            //刷新收藏列表
            viewModel.getTreesDataStar(2);
            int offset = binding.rlvKk.computeVerticalScrollOffset();
            viewModel.updateDataKK();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlvKk.offsetChildrenVertical(-1*offset);
                }
            },2);
        }else if(viewModel.tabStatus==2){
            //刷新设备&&卡口列表
            viewModel.getTreesData(0);
            viewModel.getTreesDataKK(1);
            int offset = binding.rlvStar.computeVerticalScrollOffset();
            viewModel.updateDataStar();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.rlvStar.offsetChildrenVertical(-1*offset);
                }
            },2);
        }
    }


    public interface DialogListener{
        void onDialogRefresh();
        void hideTreeDialog();
        void closeInputListener(View view);
    }
}
