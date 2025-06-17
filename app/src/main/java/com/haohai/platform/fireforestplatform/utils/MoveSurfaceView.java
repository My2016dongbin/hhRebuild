package com.haohai.platform.fireforestplatform.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MoveSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private OnGestureListener gestureListener;

    private boolean isZooming = false;
    private String currentSwipe = null;

    public interface OnGestureListener {
        void onSingleTap(float x, float y);
        void onDoubleTap(float x, float y);
        void onZoomInStart(float scaleFactor);
        void onZoomInStop();
        void onZoomOutStart(float scaleFactor);
        void onZoomOutStop();
        void onSwipeLeftStart();
        void onSwipeLeftStop();
        void onSwipeRightStart();
        void onSwipeRightStop();
        void onSwipeUpStart();
        void onSwipeUpStop();
        void onSwipeDownStart();
        void onSwipeDownStop();
    }

    public void setOnGestureListener(OnGestureListener listener) {
        this.gestureListener = listener;
    }

    public MoveSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public MoveSurfaceView(Context context, AttributeSet attrs) {
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

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (gestureListener == null) return false;

                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (diffX > 0) {
                        if (!"right".equals(currentSwipe)) {
                            stopAllSwipe();
                            gestureListener.onSwipeRightStart();
                            currentSwipe = "right";
                        }
                    } else {
                        if (!"left".equals(currentSwipe)) {
                            stopAllSwipe();
                            gestureListener.onSwipeLeftStart();
                            currentSwipe = "left";
                        }
                    }
                } else {
                    if (diffY > 0) {
                        if (!"down".equals(currentSwipe)) {
                            stopAllSwipe();
                            gestureListener.onSwipeDownStart();
                            currentSwipe = "down";
                        }
                    } else {
                        if (!"up".equals(currentSwipe)) {
                            stopAllSwipe();
                            gestureListener.onSwipeUpStart();
                            currentSwipe = "up";
                        }
                    }
                }
                return true;
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                isZooming = true;
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (gestureListener == null) return false;
                float factor = detector.getScaleFactor();
                if (factor > 1f) {
                    gestureListener.onZoomInStart(factor);
                } else {
                    gestureListener.onZoomOutStart(factor);
                }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                if (gestureListener == null) return;
                if (detector.getScaleFactor() > 1f) {
                    gestureListener.onZoomInStop();
                } else {
                    gestureListener.onZoomOutStop();
                }
                isZooming = false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        // 处理滑动停止
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (gestureListener != null && currentSwipe != null) {
                switch (currentSwipe) {
                    case "left":
                        gestureListener.onSwipeLeftStop();
                        break;
                    case "right":
                        gestureListener.onSwipeRightStop();
                        break;
                    case "up":
                        gestureListener.onSwipeUpStop();
                        break;
                    case "down":
                        gestureListener.onSwipeDownStop();
                        break;
                }
                currentSwipe = null;
            }
        }

        return true;
    }

    private void stopAllSwipe() {
        if (gestureListener == null) return;
        if (currentSwipe == null) return;
        switch (currentSwipe) {
            case "left":
                gestureListener.onSwipeLeftStop();
                break;
            case "right":
                gestureListener.onSwipeRightStop();
                break;
            case "up":
                gestureListener.onSwipeUpStop();
                break;
            case "down":
                gestureListener.onSwipeDownStop();
                break;
        }
        currentSwipe = null;
    }

    @Override public void surfaceCreated(SurfaceHolder holder) {}
    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override public void surfaceDestroyed(SurfaceHolder holder) {}
}
