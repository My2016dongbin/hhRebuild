package com.haohai.platform.fireforestplatform.utils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class HhSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    // 事件监听接口
    public interface OnGestureListener {
        void onSingleTap(float x, float y);
        void onDoubleTap(float x, float y);
        void onZoomIn(float scaleFactor);
        void onZoomOut(float scaleFactor);
    }

    private OnGestureListener gestureListener;

    public void setOnGestureListener(OnGestureListener listener) {
        this.gestureListener = listener;
    }

    public HhSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public HhSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        setClickable(true);
        setFocusableInTouchMode(true);
        getHolder().addCallback(this);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (gestureListener != null) {
                    gestureListener.onSingleTap(e.getX(), e.getY());
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (gestureListener != null) {
                    gestureListener.onDoubleTap(e.getX(), e.getY());
                }
                return true;
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();
                if (gestureListener != null) {
                    if (factor > 1f) {
                        gestureListener.onZoomIn(factor);
                    } else {
                        gestureListener.onZoomOut(factor);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        handled |= scaleGestureDetector.onTouchEvent(event);
        handled |= gestureDetector.onTouchEvent(event);
        return handled || super.onTouchEvent(event);
    }

    @Override public void surfaceCreated(SurfaceHolder holder) {}
    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override public void surfaceDestroyed(SurfaceHolder holder) {}
}
