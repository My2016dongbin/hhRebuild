package com.haohai.platform.fireforestplatform.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;

/**
 * Des: 拖拽、自吸边控件
 * Created on 2024/1/6
 */
public class MoveFrameLayout extends FrameLayout {
    private boolean isScaled;//是否已经放大
    private AnimatorSet mAnimatorSet;//动画集合
    private OnClickListener click;

    public void setClick(OnClickListener click) {
        this.click = click;
    }

    /**
     * 执行动画
     *
     * @param valueFrom
     * @param valueTo
     * @param duration
     */
    private void startScaleAnimator(float valueFrom, float valueTo, int duration) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", valueFrom, valueTo);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", valueFrom, valueTo);
        if (null == mAnimatorSet) {
            mAnimatorSet = new AnimatorSet();
        }
        mAnimatorSet.play(scaleX).with(scaleY);
        mAnimatorSet.setDuration(duration);
        mAnimatorSet.start();
    }

    public MoveFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public MoveFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoveFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private boolean isAttach;//是否自动吸边
    private boolean isDrag;//是否可拖动

    /**
     * 初始化自定义属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedAttay = context.obtainStyledAttributes(attrs, R.styleable.MoveFrameLayout);
        isAttach = mTypedAttay.getBoolean(R.styleable.MoveFrameLayout_IsAttach, true);
        isDrag = mTypedAttay.getBoolean(R.styleable.MoveFrameLayout_IsDrag, true);
        mTypedAttay.recycle();
    }

    private int mParentWidth = 0;//父控件的宽
    private int mParentHeight = 0;//父控件的高

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取父控件宽高
        ViewGroup mViewGroup = (ViewGroup) getParent();
        if (mViewGroup != null) {
            int[] location = new int[2];
            mViewGroup.getLocationOnScreen(location);
            //获取父布局的高度
            mParentHeight = mViewGroup.getMeasuredHeight();
            mParentWidth = mViewGroup.getMeasuredWidth();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //点击点在控件区域内，接受事件
        boolean inRangeOfView = inRangeOfView(this, ev);
        return inRangeOfView;
    }

    private float mLastX;//按下位置x
    private float mLastY;//按下位置Y
    private boolean isDrug = false;//是否是拖动
    private long clickTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isDrag) {
            //请求父控件将事件交由自己处理
            getParent().requestDisallowInterceptTouchEvent(true);
            float mX = ev.getX();
            float mY = ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    HhLog.e("move ACTION_DOWN");
                    clickTime = new Date().getTime();
                    //重置拖动状态
                    isDrug = false;
                    //记录按下的位置
                    mLastX = mX;
                    mLastY = mY;

                    isScaled = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    HhLog.e("move ACTION_MOVE");
                    //手指X轴滑动距离
                    float differenceValueX = mX - mLastX;
                    //手指Y轴滑动距离
                    float differenceValueY = mY - mLastY;
                    //判断是否为拖动操作
                    if (!isDrug) {
                        if (Math.sqrt(differenceValueX * differenceValueX + differenceValueY * differenceValueY) < 2) {
                            isDrug = false;
                        } else {
                            isDrug = true;
                        }
                    }
                    //获取手指按下的距离与控件本身X轴的距离
                    float ownX = getX();
                    //获取手指按下的距离与控件本身Y轴的距离
                    float ownY = getY();
                    //理论中X轴拖动的距离
                    float endX = ownX + differenceValueX;
                    //理论中Y轴拖动的距离
                    float endY = ownY + differenceValueY;
                    //X轴可以拖动的最大距离
                    float maxX = mParentWidth - getWidth();
                    //Y轴可以拖动的最大距离
                    float maxY = mParentHeight - getHeight() - SizeUtils.dp2px(getPaddingBottom() + 15);
                    //X轴边界限制---这里对X轴拖动不做限制
                    //endX = endX < 0 ? 0 : endX > maxX ? maxX : endX;
                    //Y轴边界限制
                    endY = endY < 0 ? 0 : endY > maxY ? maxY : endY;
                    //开始移动
                    setX(endX);
                    setY(endY);

                    if (isDrug && !isScaled) {
                        //放大动画
                        startScaleAnimator(1.0f, 1.1f, 300);
                        //Animator scaleAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.anim_scale_up);
                        //scaleAnimator.setTarget(this);
                        //scaleAnimator.start();
                        isScaled = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    HhLog.e("move ACTION_UP");
                    long time = new Date().getTime();
                    if(time - clickTime < 200){
                        if(click!=null){
                            click.onClick(this);
                        }
                    }
                    //根据自定义属性判断是否需要贴边
                    if (isAttach) {
                        //判断是否为点击事件
                        float center = mParentWidth / 2;
                        //自动贴边
                        if (ev.getRawX() <= center) {
                            //向左贴边
                            animate()
                                    //.setInterpolator(new BounceInterpolator())
                                    .setInterpolator(new OvershootInterpolator())
                                    .setDuration(400)
                                    .x(SizeUtils.dp2px(0))
                                    .start();
                        } else {
                            //向右贴边
                            animate()
                                    .setInterpolator(new OvershootInterpolator())
                                    .setDuration(400)
                                    .x(mParentWidth - getWidth() - SizeUtils.dp2px(0))
                                    .start();
                        }
                    }

                    if (isScaled) {
                        //缩小动画
                        startScaleAnimator(1.1f, 1.0f, 300);
                        //Animator scaleAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.anim_scale_down);
                        //scaleAnimator.setTarget(this);
                        //scaleAnimator.start();
                        isScaled = false;
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    HhLog.e("move ACTION_CANCEL");
                    break;
            }
        }
        return true;
    }

    /**
     * 点击点是否在控件区域
     *
     * @param view
     * @param ev
     * @return
     */
    private boolean inRangeOfView(View view, MotionEvent ev) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int rawX = (int) ev.getRawX();
        int rawY = (int) ev.getRawY();
        if (rect.contains(rawX, rawY)) {
            return true;
        }
        return false;
    }
}