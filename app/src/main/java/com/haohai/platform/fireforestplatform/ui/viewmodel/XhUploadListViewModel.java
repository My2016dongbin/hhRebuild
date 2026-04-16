package com.haohai.platform.fireforestplatform.ui.viewmodel;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.XhUploadListActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.XhUploadRecord;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

public class XhUploadListViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public final List<XhUploadRecord> records = new ArrayList<>();
    public final List<Object> items = new ArrayList<>();
    public final MutableLiveData<Boolean> isMockRequesting = new MutableLiveData<>(false);
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void start(Context context) {
        this.context = context;
    }

    public void barLeftClick(View v) {
        ((XhUploadListActivity) context).finish();
    }

    public void postData() {
        loading.setValue(new LoadingEvent(true, "加载中.."));
        isMockRequesting.setValue(true);
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            records.clear();
            records.add(new XhUploadRecord(
                    "1",
                    "事件名称",
                    "2026-03-26 17:30:00",
                    "上海庙镇芒哈图嘎查藏锦鸡自然保护区",
                    "",
                    "DSQ-001"
            ));
            records.add(new XhUploadRecord(
                    "2",
                    "草场巡护记录",
                    "2026-03-25 15:20:00",
                    "鄂托克前旗上海庙镇牧区样地巡护点",
                    "",
                    "DSQ-002"
            ));
            records.add(new XhUploadRecord(
                    "3",
                    "边界巡查上报",
                    "2026-03-24 10:08:00",
                    "内蒙古鄂尔多斯市上海庙能源化工基地周边区域",
                    "",
                    "DSQ-003"
            ));
            updateData();
            loading.setValue(new LoadingEvent(false));
            isMockRequesting.setValue(false);
        }, 600);
    }

    private void updateData() {
        items.clear();
        if (records.isEmpty()) {
            items.add(new Empty());
        } else {
            items.addAll(records);
        }
        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacksAndMessages(null);
    }
}
