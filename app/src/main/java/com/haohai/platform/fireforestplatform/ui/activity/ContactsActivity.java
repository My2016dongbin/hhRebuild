package com.haohai.platform.fireforestplatform.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseLiveActivity;
import com.haohai.platform.fireforestplatform.base.ViewModelFactory;
import com.haohai.platform.fireforestplatform.databinding.ActivityContactsBinding;
import com.haohai.platform.fireforestplatform.databinding.ActivityNewsBinding;
import com.haohai.platform.fireforestplatform.ui.bean.ContactsArea;
import com.haohai.platform.fireforestplatform.ui.cell.ContactsAreaListDialog;
import com.haohai.platform.fireforestplatform.ui.cell.MainDeviceListDialog;
import com.haohai.platform.fireforestplatform.ui.multitype.Contacts;
import com.haohai.platform.fireforestplatform.ui.multitype.ContactsViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.News;
import com.haohai.platform.fireforestplatform.ui.multitype.NewsViewBinder;
import com.haohai.platform.fireforestplatform.ui.viewmodel.ContactsViewModel;
import com.haohai.platform.fireforestplatform.ui.viewmodel.NewsViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class ContactsActivity extends BaseLiveActivity<ActivityContactsBinding, ContactsViewModel> implements ContactsViewBinder.OnItemClickListener, ContactsAreaListDialog.ContactsAreaListDialogListener {

    private ContactsAreaListDialog contactsAreaListDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
        obtainViewModel().postData();
        obtainViewModel().getAreas();
    }

    private void init_() {
        binding.topBar.title.setText("通讯录");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.newsSmart.setRefreshHeader(new ClassicsHeader(this));
        binding.newsSmart.setEnableLoadMore(true);
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.newsSmart.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().page=1;
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

        ContactsViewBinder newsViewBinder = new ContactsViewBinder(this);
        newsViewBinder.setListener(this);
        obtainViewModel().adapter.register(Contacts.class, newsViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);

        initAreaDialog();
    }

    private void initAreaDialog() {
        contactsAreaListDialog = new ContactsAreaListDialog(this, R.style.ActionSheetDialogStyle);
        Window dialogWindow = contactsAreaListDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        contactsAreaListDialog.setDialogListener(this);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        lp.height = (int) (height * 0.8);
        dialogWindow.setAttributes(lp);
        contactsAreaListDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contactsAreaListDialog.create();
        }
    }

    private void bind_() {
        binding.editFind.setOnEditorActionListener((v, actionId, event) -> {
            obtainViewModel().name = binding.editFind.getText().toString();
            obtainViewModel().postData();
            return true;
        });
        binding.llRole.setOnClickListener(v -> {
            if(obtainViewModel().areaList!=null&&!obtainViewModel().areaList.isEmpty()){
                contactsAreaListDialog.show();
            }else{
                Toast.makeText(this, "数据加载中，请稍候", Toast.LENGTH_SHORT).show();
                obtainViewModel().getAreas();
            }
        });
    }

    @Override
    protected ActivityContactsBinding dataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_contacts);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public ContactsViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(ContactsViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();
        obtainViewModel().loadMore.observe(this, integer -> {
            if(integer == 0){
                binding.newsSmart.setEnableLoadMore(false);
            }else if(integer == 1){
                binding.newsSmart.setEnableLoadMore(true);
            }
        });
        obtainViewModel().areaStatus.observe(this, doubles -> {
            if(obtainViewModel().areaList!=null&&!obtainViewModel().areaList.isEmpty()){
                //区域树数据更新
                contactsAreaListDialog.setContactsAreaList(obtainViewModel().areaList);
            }
        });
    }

    @Override
    public void onItemClick(Contacts contacts) {

    }

    @Override
    public void onContactsAreaListDialogReset() {
        obtainViewModel().groupName = null;
        obtainViewModel().groupNo = null;
        obtainViewModel().postData();
        binding.textRole.setText("全部");
        contactsAreaListDialog.dismiss();
    }

    @Override
    public void onContactsAreaListDialogItemClick(ContactsArea area) {
        obtainViewModel().groupName = area.getLabel();
        obtainViewModel().groupNo = area.getNo();
        obtainViewModel().postData();
        binding.textRole.setText(area.getLabel());
        contactsAreaListDialog.dismiss();
    }
}