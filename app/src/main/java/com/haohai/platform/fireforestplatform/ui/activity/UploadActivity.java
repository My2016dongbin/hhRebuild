package com.haohai.platform.fireforestplatform.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityTaskBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityUploadBinding;
import com.haohai.platform.fireforestplatform.event.MessageRefresh;
import com.haohai.platform.fireforestplatform.ui.multitype.Danger;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskList;
import com.haohai.platform.fireforestplatform.ui.multitype.TaskListViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Upload;
import com.haohai.platform.fireforestplatform.ui.multitype.UploadViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.TaskViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.UploadViewModel;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class UploadActivity extends BaseLiveActivity<ActivityUploadBinding, UploadViewModel> implements UploadViewBinder.OnItemClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtainViewModel().postData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init_() {
        binding.topBar.title.setText("事件上报");
        binding.topBar.right.setText("添加");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色

        binding.monitorFireSmart.setRefreshHeader(new ClassicsHeader(this));
        binding.monitorFireSmart.setRefreshFooter(new ClassicsFooter(this));
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return 0;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
            }
        });
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.monitorFireSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page = 1;
                obtainViewModel().postData();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page++;
                obtainViewModel().postData();
                refreshLayout.finishLoadMore(1000);
            }
        });

        UploadViewBinder taskListViewBinder = new UploadViewBinder(this);
        taskListViewBinder.setListener(this);
        obtainViewModel().adapter.register(Upload.class, taskListViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);
    }

    private void bind_() {
        binding.topBar.right.setOnClickListener(v -> {
            startActivity(new Intent(this,AddingUploadActivity.class));
        });
    }

    @Override
    protected ActivityUploadBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_upload);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public UploadViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(UploadViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

    }

    @Override
    public void onItemClick(Upload message) {
        CommonData.upload = message;
        Intent intent = new Intent(this, AddingUploadActivity.class);
        intent.putExtra("edit",true);
        intent.putExtra("read",true);
        startActivity(intent);
    }
    @Override
    public void onEditClick(Upload message) {
        CommonData.upload = message;
        Intent intent = new Intent(this, AddingUploadActivity.class);
        intent.putExtra("edit",true);
        intent.putExtra("read",false);
        startActivity(intent);
    }
    @Override
    public void onDeleteClick(Upload message) {
        TextInfo okTextInfo = new TextInfo();
        okTextInfo.setFontColor(this.getResources().getColor(R.color.c7));
        MessageDialog.show("温馨提示", "确定要删除这条数据吗？","确定","取消")
                .setButtonOrientation(LinearLayout.VERTICAL)
                .setOkTextInfo(okTextInfo)
                .setCancelTextInfo(okTextInfo)
                .setOtherTextInfo(okTextInfo)
                .setOkButtonClickListener((dialog, v1) -> {
                    obtainViewModel().delete(message);
                    return false;
                })
                .setCancelButtonClickListener((dialog, v2) -> {

                    return false;
                })
                .setOnBackgroundMaskClickListener((dialog, v12) -> {
                    return false;
                })
                .setCancelable(true);
    }
}