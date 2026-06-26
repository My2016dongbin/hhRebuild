package com.haohai.platform.fireforestplatform.ui.viewmodel;

import android.content.Context;
import android.view.View;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseViewModel;
import com.haohai.platform.fireforestplatform.ui.activity.StudyActivity;
import com.haohai.platform.fireforestplatform.ui.multitype.TutorialItem;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by qc
 * on 2026/6/26.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class StudyViewModel extends BaseViewModel {
    public Context context;
    public MultiTypeAdapter adapter;
    public List<Object> items = new ArrayList<>();

    public void start(Context context) {
        this.context = context;
        initItems();
    }

    private void initItems() {
        items.clear();
        items.add(TutorialItem.title("任务处理"));
        items.add(TutorialItem.text("①点击任务单"));
        items.add(TutorialItem.image(R.drawable.tutorial_01));
        items.add(TutorialItem.text("②新增右上角处理账号，这样就知道已完成的任务是哪个账号处理的"));
        items.add(TutorialItem.image(R.drawable.tutorial_02));

        items.add(TutorialItem.title("巡护上报"));
        items.add(TutorialItem.text("①点击巡护上报"));
        items.add(TutorialItem.image(R.drawable.tutorial_03));
        items.add(TutorialItem.text("②点击右上角“列表”按钮"));
        items.add(TutorialItem.image(R.drawable.tutorial_04));
        items.add(TutorialItem.text("③巡护上报列表增加了巡护上报人"));
        items.add(TutorialItem.image(R.drawable.tutorial_05));

        items.add(TutorialItem.title("考勤管理"));
        items.add(TutorialItem.text("①点击考勤管理"));
        items.add(TutorialItem.image(R.drawable.tutorial_06));
        items.add(TutorialItem.text("②点击“统计”按钮"));
        items.add(TutorialItem.image(R.drawable.tutorial_07));
        items.add(TutorialItem.text("③市级总账号和各旗区账号可以看到下面网格员的所有考勤"));
        items.add(TutorialItem.image(R.drawable.tutorial_08));
    }

    public void barLeftClick(View v) {
        ((StudyActivity) context).finish();
    }
}
