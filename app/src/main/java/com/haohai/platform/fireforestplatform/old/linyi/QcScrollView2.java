package com.haohai.platform.fireforestplatform.old.linyi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by qc
 * on 2022/6/21.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class QcScrollView2 extends ScrollView {
    public QcScrollView2(Context context) {
        super(context);
    }

    public QcScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QcScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public QcScrollView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override

    protected int computeVerticalScrollExtent() {

        return 1;

    }

    @Override

    protected int computeVerticalScrollOffset() {

        int sRange = super.computeVerticalScrollRange();

        int sExtent = super.computeVerticalScrollExtent();

        int range = sRange - sExtent;

        if(range == 0){

            return 0;

        }

        return (int) (sRange * super.computeVerticalScrollOffset() * 1.0f / range);

    }
}
