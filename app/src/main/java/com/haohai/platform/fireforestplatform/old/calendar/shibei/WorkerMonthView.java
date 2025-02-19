package com.haohai.platform.fireforestplatform.old.calendar.shibei;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.ui.multitype.WorkerDetail;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class WorkerMonthView extends MonthView {

    private int mRadius;

    /**
     * 自定义魅族标记的文本画笔
     */
    private final Paint mTextPaint = new Paint();

    private final Paint mLinePaint = new Paint();


    /**
     * 24节气画笔
     */
    private final Paint mSolarTermTextPaint = new Paint();

    /**
     * 背景圆点
     */
    private final Paint mPointPaint = new Paint();
    private final Paint mTodayPaint = new Paint();

    /**
     * 今天的背景色
     */
    private final Paint mCurrentDayPaint = new Paint();
    private final Paint mDayTextPaint = new Paint();
    private final Paint mNightTextPaint = new Paint();
    private final Paint mDayBackPaint = new Paint();
    private final Paint mNightBackPaint = new Paint();

    /**
     * 圆点半径
     */
    private final float mPointRadius;

    private final int mPadding;

    private final float mCircleRadius;
    /**
     * 自定义魅族标记的圆形背景
     */
    private final Paint mSchemeBasicPaint = new Paint();

    private final float mSchemeBaseLine;

    private int theme_blue = 0xff3170FF;
    private int theme_today = 0xffB0C4DE;
    private int back_gray = 0xeeeeeeee;
    private int back_line = 0xffdddddd;
    private int back_white = 0xffffffff;
    private int back_black = 0xff000000;
    private int back_day = 0xffE0F1FF;
    private int back_night = 0xff11427A;

    public WorkerMonthView(Context context) {
        super(context);
        /*mCurDayTextPaint.setTextSize(26);
        mCurMonthTextPaint.setTextSize(26);
        mOtherMonthTextPaint.setTextSize(26);*/

        mTextPaint.setTextSize(dipToPx(context, 8));
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);


        mSolarTermTextPaint.setColor(0xff489dff);
        mSolarTermTextPaint.setAntiAlias(true);
        mSolarTermTextPaint.setTextAlign(Paint.Align.CENTER);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setFakeBoldText(true);
        mSchemeBasicPaint.setColor(Color.WHITE);


        mCurrentDayPaint.setAntiAlias(true);
        mCurrentDayPaint.setStyle(Paint.Style.FILL);
        mCurrentDayPaint.setColor(0xFFeaeaea);

        mLinePaint.setAntiAlias(true);
        mLinePaint.setTextSize(30);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(back_line);
        mDayTextPaint.setAntiAlias(true);
        mDayTextPaint.setTextSize(30);
        mDayTextPaint.setStyle(Paint.Style.FILL);
        mDayTextPaint.setColor(back_black);
        mNightTextPaint.setAntiAlias(true);
        mNightTextPaint.setStyle(Paint.Style.FILL);
        mNightTextPaint.setTextSize(30);
        mNightTextPaint.setColor(back_white);
        mDayBackPaint.setAntiAlias(true);
        mDayBackPaint.setStyle(Paint.Style.FILL);
        mDayBackPaint.setColor(back_day);
        mNightBackPaint.setAntiAlias(true);
        mNightBackPaint.setStyle(Paint.Style.FILL);
        mNightBackPaint.setColor(back_night);

        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);
        mPointPaint.setColor(theme_blue);

        mTodayPaint.setAntiAlias(true);
        mTodayPaint.setStyle(Paint.Style.FILL);
        mTodayPaint.setTextAlign(Paint.Align.CENTER);
        mTodayPaint.setColor(theme_today);

        mCircleRadius = dipToPx(getContext(), 7);

        mPadding = dipToPx(getContext(), 3);

        mPointRadius = dipToPx(context, 2);

        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine = mCircleRadius - metrics.descent + (metrics.bottom - metrics.top) / 2 + dipToPx(getContext(), 1);
    }


    @Override
    protected void onPreviewHook() {
        mSolarTermTextPaint.setTextSize(mCurMonthLunarTextPaint.getTextSize());
        mRadius = Math.min(mItemWidth, mItemHeight) / 130 * 39;
    }


    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        float cx = x+mItemWidth/3*2;
        float cy = y+mTextBaseLine/4;
        canvas.drawCircle(cx, cy, mRadius, mPointPaint);
        //canvas.drawRect(x,y,x+mItemWidth,y+mItemHeight,mSolarTermTextPaint);//
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        float cx = x+mItemWidth/3*2;
        float cy = y+mTextBaseLine/4;
        boolean isSelected = isSelected(calendar);
        if (isSelected) {
            mPointPaint.setColor(Color.WHITE);
        } else {
            mPointPaint.setColor(theme_blue);
        }

        canvas.drawCircle(cx, cy, mRadius, mPointPaint);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float cx = x+mItemWidth/3*2;
        float cy = y+mTextBaseLine/4;
        int cx_x = x + mItemWidth / 4;
        int cx_x_real = x + mItemWidth / 2;
        int cy_y = y + mItemHeight / 2;
        float top = y+mTextBaseLine/3;

        if (calendar.isCurrentDay() && !isSelected) {
            canvas.drawCircle(cx, cy, mRadius, mTodayPaint);
        }

        //当然可以换成其它对应的画笔就不麻烦
        mCurMonthTextPaint.setColor(0xff333333);
        mCurMonthLunarTextPaint.setColor(0xffCFCFCF);
        mSchemeTextPaint.setColor(0xff333333);
        mSchemeLunarTextPaint.setColor(0xffCFCFCF);
        mOtherMonthTextPaint.setColor(0xFFe1e1e1);
        mOtherMonthLunarTextPaint.setColor(0xFFe1e1e1);

        if (isSelected) {
            mCurDayTextPaint.setColor(back_white);
            canvas.drawText(String.valueOf(calendar.getDay()), cx, top,
                    mCurDayTextPaint);
        } else {
            if(calendar.isCurrentDay()){
                mCurDayTextPaint.setColor(back_gray);
            }
            canvas.drawText(String.valueOf(calendar.getDay()), cx, top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                    calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }

        float day1_y = y+mItemHeight/13*6;
        float day2_y = y+mItemHeight/13*8;
        float day3_y = y+mItemHeight/13*10;
        float day4_y = y+mItemHeight/13*12;
        canvas.drawRect(x+10,day1_y-30,x+mItemWidth-10,day1_y+10,mDayBackPaint);
        canvas.drawRect(x+10,day2_y-30,x+mItemWidth-10,day2_y+10,mDayBackPaint);
        canvas.drawRect(x+10,day3_y-30,x+mItemWidth-10,day3_y+10,mNightBackPaint);
        canvas.drawRect(x+10,day4_y-30,x+mItemWidth-10,day4_y+10,mNightBackPaint);

        Bitmap bitmapDay = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.star_leader);
        Bitmap bitmapNight = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.star_leader);
        canvas.drawBitmap(bitmapDay,x+15,day1_y-23,new Paint());
        canvas.drawBitmap(bitmapNight,x+15,day3_y-23,new Paint());

        //网格线
        canvas.drawLine(0,y,getWidth(),y,mLinePaint);
        canvas.drawLine(0,y+getHeight(),getWidth(),y+getHeight(),mLinePaint);
        canvas.drawLine(x,0,x,getHeight(),mLinePaint);

        /*canvas.drawText("张晓华", cx_x, day1_y, mDayTextPaint);
        canvas.drawText("周毅", cx_x, day2_y, mDayTextPaint);
        canvas.drawText("李白武", cx_x, day3_y, mNightTextPaint);
        canvas.drawText("万松", cx_x, day4_y, mNightTextPaint);*/

        int year = calendar.getYear();
        int month = calendar.getMonth();
        int day = calendar.getDay();
        for (int i = 0; i < CommonData.workerDetails.size(); i++) {
            WorkerDetail workerDetail = CommonData.workerDetails.get(i);
            List<WorkerDetail.Worker> list = workerDetail.getList();
            if(list!=null && !list.isEmpty()){
                WorkerDetail.Worker worker_ = list.get(0);
                try {
                    Date date_ = new Date(Long.parseLong(worker_.getDutyDate()));
                    java.util.Calendar calendar_ = java.util.Calendar.getInstance();
                    calendar_.setTime(date_);
                    int year_ = calendar_.get(java.util.Calendar.YEAR);
                    int month_ = calendar_.get(java.util.Calendar.MONTH)+1;
                    int day_ = calendar_.get(java.util.Calendar.DAY_OF_MONTH);
//                    HhLog.e(year + "-" + month + "-" + day);
//                    HhLog.e(year_ + "+" + month_ + "+" + day_);
                    if(year == year_ && month == month_ && day == day_){
                        canvas.drawText(worker_.getArrangeName(), cx_x, day1_y, mDayTextPaint);
                        if(list.size()>1){
                            WorkerDetail.Worker worker = list.get(1);
                            canvas.drawText(worker.getArrangeName(), cx_x, day2_y, mDayTextPaint);
                        }
                        if(list.size()>2){
                            WorkerDetail.Worker worker = list.get(2);
                            canvas.drawText(worker.getArrangeName(), cx_x, day3_y, mNightTextPaint);
                        }
                        if(list.size()>3){
                            WorkerDetail.Worker worker = list.get(3);
                            canvas.drawText(worker.getArrangeName(), cx_x, day4_y, mNightTextPaint);
                        }
                    }/*else{
                        canvas.drawText("未排班", cx_x, day1_y, mDayTextPaint);
                        canvas.drawText("未排班", cx_x, day2_y, mDayTextPaint);
                        canvas.drawText("未排班", cx_x, day3_y, mNightTextPaint);
                        canvas.drawText("未排班", cx_x, day4_y, mNightTextPaint);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                    HhLog.e("monthView error " + e.toString());
                }
            }
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
