package com.haohai.platform.fireforestplatform.old;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.TrunkBranchAnnals;

import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.MainTabChange;
import com.haohai.platform.fireforestplatform.event.WalkEvent;
import com.haohai.platform.fireforestplatform.old.calendar.EnglishWeekBar;
import com.haohai.platform.fireforestplatform.old.calendar.meizu.MeiZuMonthView;
import com.haohai.platform.fireforestplatform.old.calendar.meizu.MeizuWeekView;
import com.haohai.platform.fireforestplatform.ui.activity.SignStatisticsActivity;
import com.haohai.platform.fireforestplatform.ui.bean.GeoPoint;
import com.haohai.platform.fireforestplatform.ui.bean.SignModel;
import com.haohai.platform.fireforestplatform.ui.cell.MNCTransparentDialog;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rx.functions.Action1;

public class SignMonthActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnCalendarLongClickListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener,
        CalendarView.OnWeekChangeListener,
        CalendarView.OnViewChangeListener,
        CalendarView.OnCalendarInterceptListener,
        CalendarView.OnYearViewChangeListener,
        DialogInterface.OnClickListener{

    TextView mTextMonthDay;

    TextView mTextYear;
    LinearLayout ll_title;
    TextView tv_title;
    TextView tv_early;
    TextView tv_night;
    LinearLayout ll_body;
    LinearLayout bottom;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;

    private AlertDialog mMoreDialog;

    private AlertDialog mFuncDialog;
    private TextView left;
    private TextView right;
    private TextView tv_today;
    private TextView sign;
    private TextView un_sign;
    private TextView walk;
    private final String TAG = SignMonthActivity.class.getSimpleName();

    private int currentYear = 2022;
    private int currentMonth = 1;
    private String id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackText();
        setContentView(R.layout.activity_sign_list);
        id = getIntent().getStringExtra("id");//有id为查询下属考勤无id查自己并签到签退
        name = getIntent().getStringExtra("name");//有id为查询下属考勤无id查自己并签到签退
        EventBus.getDefault().register(this);
        initView();
        initData();
        //获取签到信息
        signInfo();
        //获取当日巡护里程
        walkInfo();
    }

    ///巡护距离更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(WalkEvent event) {
        updateWalkDistance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void walkInfo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        RequestParams params = new RequestParams(URLConstant.GET_USER_DISTANCE);
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.addParameter("time",format);
        params.addParameter("userId",SPUtils.get(this, SPValue.id,""));
        Log.e(TAG, "onSuccess: bingo params = walkInfo " +  params.toString());
        //showDY3();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo result = walkInfo" +  result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    CommonData.walkDistance = jsonObject.getInt("data");
                    CommonData.hasGet = true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: walkInfo " + ex.getMessage() );
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                //hideDY3();
                updateWalkDistance();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateWalkDistance() {
        Log.e(TAG, "updateWalkDistance: " );
        walk.setText("今日巡护距离：" + CommonData.walkDistance + "米");
    }

    private void sign(String causeStr) {
        SignModel signModel = new SignModel();
        GeoPoint workPosition = new GeoPoint();
        workPosition.setLat(CommonData.lat);
        workPosition.setLng(CommonData.lng);
        signModel.setWorkPosition(workPosition);
        if(causeStr!=null){
            signModel.setCause(causeStr);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        signModel.setSignInTime(formatter.format(curDate));
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "oa/api/attendance/earlySign");
        params.setBodyContent(new Gson().toJson(signModel));
        params.addHeader("Authorization", "bearer " + CommonData.token);
        Log.e(TAG, "onSuccess: bingo params = " +  params.toString());
        Log.e(TAG, "onSuccess: bingo new Gson().toJson(signModel) = " +  new Gson().toJson(signModel));
        showDY3();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo result = " +  result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    String cause = data.getString("cause");
                    if(cause.contains("不在考勤范围内")){
                        Toast.makeText(SignMonthActivity.this, cause, Toast.LENGTH_SHORT).show();
                    }else if(cause.contains("无匹配考勤规则")){
                        Toast.makeText(SignMonthActivity.this, cause, Toast.LENGTH_SHORT).show();
                    }else if(cause.contains("原因")){
                        showCauseDialog("请填写异常签到原因",0);
                    }else{
                        Toast.makeText(SignMonthActivity.this, cause, Toast.LENGTH_SHORT).show();
                        signStatus = 1;
                        updateSignStatus();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                HhLog.e("e " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDY3();
                    }
                },1000);
            }
        });
    }
    private void signOut(String causeStr) {
        SignModel signModel = new SignModel();
        GeoPoint workPosition = new GeoPoint();
        workPosition.setLat(CommonData.lat);
        workPosition.setLng(CommonData.lng);
        signModel.setWorkPosition(workPosition);
        if(causeStr!=null){
            signModel.setCause(causeStr);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        signModel.setSignInTime(formatter.format(curDate));
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "oa/api/attendance/nightSign");
        params.setBodyContent(new Gson().toJson(signModel));
        params.addHeader("Authorization", "bearer "  + CommonData.token);
        Log.e(TAG, "onSuccess: bingo params = " +  params.toString());
        Log.e(TAG, "onSuccess: bingo new Gson().toJson(signModel) = " +  new Gson().toJson(signModel));
        showDY3();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo result = " +  result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    String cause = data.getString("cause");
                    if(cause.contains("不在考勤范围内")){
                        Toast.makeText(SignMonthActivity.this, cause, Toast.LENGTH_SHORT).show();
                    }else if(cause.contains("无匹配考勤规则")){
                        Toast.makeText(SignMonthActivity.this, cause, Toast.LENGTH_SHORT).show();
                    }else if(cause.contains("原因")){
                        showCauseDialog("请填写异常签退原因",1);
                    }else{
                        Toast.makeText(SignMonthActivity.this, cause, Toast.LENGTH_SHORT).show();
                        signStatus = 2;
                        updateSignStatus();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDY3();
                    }
                },1000);
            }
        });
    }

    public void showCauseDialog(String msg,int status) {
        final MNCTransparentDialog mncTransDialog = new MNCTransparentDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cause, null, false);
        TextView message_text = (TextView) dialogView.findViewById(R.id.message_text);
        EditText et_cause = (EditText) dialogView.findViewById(R.id.et_cause);
        message_text.setText(msg);
        final TextView tv_queren = (TextView) dialogView.findViewById(R.id.tv_right);
        final TextView tv_left = (TextView) dialogView.findViewById(R.id.tv_left);
        //确认
        tv_queren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mncTransDialog.dismiss();
                if(status == 0){
                    sign(et_cause.getText().toString());
                }else{
                    signOut(et_cause.getText().toString());
                }
            }
        });
        //取消
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mncTransDialog.dismiss();
            }
        });
        mncTransDialog.show();
        Window window = mncTransDialog.getWindow();//对话框窗口
        window.setGravity(Gravity.CENTER);//设置对话框显示在屏幕中间
        window.setWindowAnimations(R.style.dialog_style);//添加动画
        window.setContentView(dialogView);
    }
    private int signStatus = 0; // 0 未签到 1 已签到未签退 2 已签到签退
    private void updateSignStatus(){
        if(signStatus == 0){//未签到
            CommonData.hasSign = false;
            un_sign.setClickable(false);
            un_sign.setBackground(getResources().getDrawable(R.drawable.bg_sign_out_no));
        }
        else if(signStatus == 1){//已签到未签退
            CommonData.hasSign = true;
            sign.setText("已签到");
            sign.setClickable(false);
            sign.setBackground(getResources().getDrawable(R.drawable.bg_sign_no));
            un_sign.setText("签退");
            un_sign.setClickable(true);
            un_sign.setBackground(getResources().getDrawable(R.drawable.bg_sign_out));
        }
        else{//已签到签退
            CommonData.hasSign = false;
            sign.setText("已签到");
            sign.setClickable(false);
            sign.setBackground(getResources().getDrawable(R.drawable.bg_sign_no));
            un_sign.setText("已签退");
            un_sign.setClickable(false);
            un_sign.setBackground(getResources().getDrawable(R.drawable.bg_sign_out_no));
        }
        //更新考勤表信息
        initData();
    }
    private void signInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "oa/api/attendance/myAttRecordByTime");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("time",formatter.format(curDate));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.setBodyContent(jsonObject.toString());
        params.addBodyParameter("time",formatter.format(curDate));
        params.addHeader("Authorization", "bearer " + CommonData.token);
        Log.e(TAG, "onSuccess: bingo params = " +  params.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo result = signInfo" +  result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data!=null && data.length()!=0){
                        //有记录
                        //1上班 2下班 //1.正常上班考勤 2.迟到上班考勤 3.出勤节点签到时间未在规定节点中 6.正常下班考勤 7.早退下班考勤
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = (JSONObject) data.get(i);
                            if(Objects.equals(obj.getString("signInState"), "1")||Objects.equals(obj.getString("signInState"), "2")||Objects.equals(obj.getString("signInState"), "3")){
                                signStatus = 1;
                            }
                            if(Objects.equals(obj.getString("signInState"), "6") || Objects.equals(obj.getString("signInState"), "7")){
                                signStatus = 2;
                                break;
                            }
                        }
                    }else{
                        //未签到
                        signStatus = 0;
                    }
                    updateSignStatus();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    @SuppressLint("SetTextI18n")
    protected void initView() {
        left = findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right = findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignMonthActivity.this, SignStatisticsActivity.class));
            }
        });
        walk = findViewById(R.id.walk);
        sign = findViewById(R.id.sign);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign(null);
            }
        });
        un_sign = findViewById(R.id.un_sign);
        un_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut(null);
            }
        });

        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        tv_early = findViewById(R.id.tv_early);
        tv_night = findViewById(R.id.tv_night);
        ll_body = findViewById(R.id.ll_body);
        bottom = findViewById(R.id.bottom);
        ll_title = findViewById(R.id.ll_title);
        tv_title = findViewById(R.id.tv_title);
        mTextLunar = findViewById(R.id.tv_lunar);

        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);
        tv_today = findViewById(R.id.tv_today);
        tv_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
                ll_body.setVisibility(View.GONE);
            }
        });

        //mCalendarView.setRange(2018, 7, 1, 2019, 4, 28);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreDialog == null) {
                    mMoreDialog = new AlertDialog.Builder(SignMonthActivity.this)
                            .setTitle(R.string.list_dialog_title)
                            .setItems(R.array.list_dialog_items, SignMonthActivity.this)
                            .create();
                }
                mMoreDialog.show();
            }
        });

        final DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mCalendarLayout.expand();
                                break;
                            case 1:
                                boolean result = mCalendarLayout.shrink();
                                Log.e("shrink", " --  " + result);
                                break;
                            case 2:
                                mCalendarView.scrollToPre(false);
                                break;
                            case 3:
                                mCalendarView.scrollToNext(false);
                                break;
                            case 4:
                                //mCalendarView.scrollToCurrent(true);
                                mCalendarView.scrollToCalendar(2018, 12, 30);
                                break;
                            case 5:
                                mCalendarView.setRange(2018, 7, 1, 2019, 4, 28);
//                                mCalendarView.setRange(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), 6,
//                                        mCalendarView.getCurYear(), mCalendarView.getCurMonth(), 23);
                                break;
                            case 6:
                                Log.e("scheme", "  " + mCalendarView.getSelectedCalendar().getScheme() + "  --  "
                                        + mCalendarView.getSelectedCalendar().isCurrentDay());
                                List<Calendar> weekCalendars = mCalendarView.getCurrentWeekCalendars();
                                for (Calendar calendar : weekCalendars) {
                                    Log.e("onWeekChange", calendar.toString() + "  --  " + calendar.getScheme());
                                }
                                new AlertDialog.Builder(SignMonthActivity.this)
                                        .setMessage(String.format("Calendar Range: \n%s —— %s",
                                                mCalendarView.getMinRangeCalendar(),
                                                mCalendarView.getMaxRangeCalendar()))
                                        .show();
                                break;
                        }
                    }
                };

        findViewById(R.id.iv_func).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFuncDialog == null) {
                    mFuncDialog = new AlertDialog.Builder(SignMonthActivity.this)
                            .setTitle(R.string.func_dialog_title)
                            .setItems(R.array.func_dialog_items, listener)
                            .create();
                }
                mFuncDialog.show();
            }
        });

        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarLongClickListener(this, true);
        mCalendarView.setOnWeekChangeListener(this);
        mCalendarView.setOnYearViewChangeListener(this);

        //设置日期拦截事件，仅适用单选模式，当前无效
        mCalendarView.setOnCalendarInterceptListener(this);

        mCalendarView.setOnViewChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        //下属
        if(id==null){
            bottom.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
        }else{
            bottom.setVisibility(View.GONE);
            right.setVisibility(View.GONE);
            TextView title = findViewById(R.id.title);
            title.setText(name+"考勤记录");
        }
    }

    protected void initData() {
        currentYear = mCalendarView.getCurYear();
        currentMonth = mCalendarView.getCurMonth();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Date curDate = new Date(System.currentTimeMillis());
        postData(formatter.format(curDate));
    }


    protected void parseData() {

        Map<String, Calendar> map = new HashMap<>();
        Log.e(TAG, "parseData: bingo date = " + currentYear + "-" + currentMonth );

        /*int monthCount = 28;//暂未用（现使用接口返回总天数）
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        if(month == 12){
            calendar.set(java.util.Calendar.YEAR,year+1);
            calendar.set(java.util.Calendar.MONTH,1);
            calendar.set(java.util.Calendar.DAY_OF_MONTH,1);
        }else{
            calendar.set(java.util.Calendar.YEAR,year);
            calendar.set(java.util.Calendar.MONTH,month+1);
            calendar.set(java.util.Calendar.DAY_OF_MONTH,1);
        }
        calendar.add(java.util.Calendar.DAY_OF_MONTH,-1);
        monthCount = calendar.get(java.util.Calendar.DAY_OF_MONTH);*/

        /*上班：earlyStart 1 正常上班考勤 2 迟到上班考勤 3 出勤节点或签到时间未在规定节点中 -3 旷工 -2 休息日*/
        /*下班：nightStart 6 正常下班考勤 7 早退下班考勤 3 出勤节点或签到时间未在规定节点中 -3 旷工 -2 休息日*/
        for (int d = 1; d <= signMonthList2.size(); d++) {
            Log.e(TAG, "parseData: bingo ~~~~~~~~" );
            SignMonth2 today = signMonthList2.get(d-1);
            /*if(Objects.equals(today.getEarlyStart(), "1") && Objects.equals(today.getNightStart(), "6")){
                map.put(getSchemeCalendar(currentYear, currentMonth, d, 0xFF40db25, "常").toString(),
                        getSchemeCalendar(currentYear, currentMonth, d, 0xFF40db25, "常"));
            }else if(Objects.equals(today.getEarlyStart(), "-3") && Objects.equals(today.getNightStart(), "-3")){
                map.put(getSchemeCalendar(currentYear, currentMonth, d, 0xFFdf1356, "旷").toString(),
                        getSchemeCalendar(currentYear, currentMonth, d, 0xFFdf1356, "旷"));
            }else if(Objects.equals(today.getEarlyStart(), "-3") || Objects.equals(today.getNightStart(), "-3")){
                map.put(getSchemeCalendar(currentYear, currentMonth, d, 0xFFdf1356, "缺").toString(),
                        getSchemeCalendar(currentYear, currentMonth, d, 0xFFdf1356, "缺"));
            }*//*3 或者 既迟到又早退*//*else if(Objects.equals(today.getEarlyStart(), "3") || Objects.equals(today.getNightStart(), "3") || (Objects.equals(today.getEarlyStart(), "2") && Objects.equals(today.getNightStart(), "7"))){
                map.put(getSchemeCalendar(currentYear, currentMonth, d, 0xFFcda1af, "异").toString(),
                        getSchemeCalendar(currentYear, currentMonth, d, 0xFFcda1af, "异"));
            }else if(Objects.equals(today.getEarlyStart(), "2")){
                map.put(getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, "迟").toString(),
                        getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, "迟"));
            }else if(Objects.equals(today.getNightStart(), "7")){
                map.put(getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, "退").toString(),
                        getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, "退"));
            }else{//null 未来日期
                map.put(getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, " ").toString(),
                        getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, " "));
            }*/
            if(today.getCheckInTime()!=null && today.getCheckOutTime()!=null && today.getCheckInTime()!="null" && today.getCheckOutTime()!="null" && today.getCheckInTime()!="" && today.getCheckOutTime()!="" ){
                map.put(getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, "°").toString(),//
                        getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, "°"));
            }else{
                map.put(getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, " ").toString(),//
                        getSchemeCalendar(currentYear, currentMonth, d, 0xFFe69138, " "));
            }
            Log.e(TAG, "parseData: bingo map size" + map.size() + "~~" + currentYear + currentMonth + d + today);
        }

        Log.e(TAG, "parseData: bingo map list" + signMonthList2.size());//
        Log.e(TAG, "parseData: bingo map list = " + signMonthList2);//
        Log.e(TAG, "parseData: bingo map" + map.size());
        Log.e(TAG, "parseData: bingo map = " + map);
        //28560 数据量增长不会影响UI响应速度，请使用这个API替换
        mCalendarView.setSchemeDate(map);

    }

    void showDY3(){

    }
    void hideDY3(){

    }

    private final List<SignMonth> signMonthList = new ArrayList<>();
    private List<SignMonth2> signMonthList2 = new ArrayList<>();

    private void postData(String date) {
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "oa/api/attendance/record/month/detail");//-2休息  -3旷工
        params.addParameter("month",date);
        params.addParameter("userId", id==null?SPUtils.get(this, SPValue.id,""):id);
        params.addHeader("Authorization", "bearer " + CommonData.token);
        showDY3();
        Log.e(TAG, "postData: params = " + params );
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo info new = " + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray list = data.getJSONArray("dayInfoList");
                    signMonthList2 = new ArrayList<>();
                    for (int i = 0; i < list.length(); i++) {
                        SignMonth2 signMonth2 = new Gson().fromJson(list.get(i).toString(), SignMonth2.class);
                        signMonthList2.add(signMonth2);
                    }

                    parseData();
                    Log.e(TAG, "onSuccess: bingo signMonthList2 = " + signMonthList2.toString() );

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDY3();
                    }
                },1000);
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                mCalendarView.setWeekStarWithSun();
                break;
            case 1:
                mCalendarView.setWeekStarWithMon();
                break;
            case 2:
                mCalendarView.setWeekStarWithSat();
                break;
            case 3:
                if (mCalendarView.isSingleSelectMode()) {
                    mCalendarView.setSelectDefaultMode();
                } else {
                    mCalendarView.setSelectSingleMode();
                }
                break;
            case 4:
                mCalendarView.setWeekView(MeizuWeekView.class);
                mCalendarView.setMonthView(MeiZuMonthView.class);
                mCalendarView.setWeekBar(EnglishWeekBar.class);
                break;
            case 5:
                mCalendarView.setAllMode();
                break;
            case 6:
                mCalendarView.setOnlyCurrentMode();
                break;
            case 7:
                mCalendarView.setFixMode();
                break;
        }
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        if (isClick) {
            int index = calendar.getDay()-1;
            if(signMonthList2.size()==0){//
                return;
            }
            SignMonth2 today = signMonthList2.get(index);
            //Toast.makeText(this, getCalendarText(calendar)+today.toString(), Toast.LENGTH_SHORT).show();
            ll_body.setVisibility(View.VISIBLE);
            if(!signMonthList2.get(0).getDate().contains(mYear+"")){//当前月数据加载异常，防止显示旧数据//
                ll_body.setVisibility(View.GONE);
            }
            ll_title.setVisibility(View.GONE);
            tv_early.setText(today.getCheckInTime()==null?"未签到":today.getCheckInTime());
            tv_night.setText(today.getCheckOutTime()==null?"未签退":today.getCheckOutTime());
            /*if(Objects.equals(today.getEarlyStart(), "1") && Objects.equals(today.getNightStart(), "6")){
                tv_title.setText("正常签到、正常签退");
            }else if(Objects.equals(today.getEarlyStart(), "-3") && Objects.equals(today.getNightStart(), "-3")){
                tv_title.setText("旷工");
            }else if(Objects.equals(today.getEarlyStart(), "-3") || Objects.equals(today.getNightStart(), "-3")){
                tv_title.setText("缺卡");
            }*//*3 或者 既迟到又早退*//*else if(Objects.equals(today.getEarlyStart(), "3") || Objects.equals(today.getNightStart(), "3") || (Objects.equals(today.getEarlyStart(), "2") && Objects.equals(today.getNightStart(), "7"))){
                if(Objects.equals(today.getEarlyStart(), "2") && Objects.equals(today.getNightStart(), "7")){
                    tv_title.setText("迟到、早退");
                }else{
                    tv_title.setText("异常考勤");
                }
            }else if(Objects.equals(today.getEarlyStart(), "2")){
                tv_title.setText("迟到");
            }else if(Objects.equals(today.getNightStart(), "7")){
                tv_title.setText("早退");
            }else{//null 未来日期
                ll_body.setVisibility(View.GONE);
            }*/
        }
        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
        Log.e("onDateSelected", "  " + mCalendarView.getSelectedCalendar().getScheme() +
                "  --  " + mCalendarView.getSelectedCalendar().isCurrentDay());
        Log.e("干支年纪 ： ", " -- " + TrunkBranchAnnals.getTrunkBranchYear(calendar.getLunarCalendar().getYear()));
    }

    @Override
    public void onCalendarLongClickOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : LongClickOutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarLongClick(Calendar calendar) {
        Toast.makeText(this, "长按不选择日期\n" + getCalendarText(calendar), Toast.LENGTH_SHORT).show();
    }

    private static String getCalendarText(Calendar calendar) {
        return String.format("新历%s \n 农历%s \n 公历节日：%s \n 农历节日：%s \n 节气：%s \n 是否闰月：%s",
                calendar.getMonth() + "月" + calendar.getDay() + "日",
                calendar.getLunarCalendar().getMonth() + "月" + calendar.getLunarCalendar().getDay() + "日",
                TextUtils.isEmpty(calendar.getGregorianFestival()) ? "无" : calendar.getGregorianFestival(),
                TextUtils.isEmpty(calendar.getTraditionFestival()) ? "无" : calendar.getTraditionFestival(),
                TextUtils.isEmpty(calendar.getSolarTerm()) ? "无" : calendar.getSolarTerm(),
                calendar.getLeapMonth() == 0 ? "否" : String.format("闰%s月", calendar.getLeapMonth()));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMonthChange(int year, int month) {
        Log.e("onMonthChange", "  -- " + year + "  --  " + month);
        currentYear = year;
        currentMonth = month;
        ll_body.setVisibility(View.GONE);
        String date = "";
        if(month > 9){
            date = year+"-"+month;
        }else{
            date = year+"-0"+month;
        }
        postData(date);
        Calendar calendar = mCalendarView.getSelectedCalendar();
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
    }

    @Override
    public void onViewChange(boolean isMonthView) {
        Log.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图"));
    }


    @Override
    public void onWeekChange(List<Calendar> weekCalendars) {
        for (Calendar calendar : weekCalendars) {
            Log.e("onWeekChange", calendar.toString());
        }
    }

    @Override
    public void onYearViewChange(boolean isClose) {
        Log.e("onYearViewChange", "年视图 -- " + (isClose ? "关闭" : "打开"));
    }

    /**
     * 屏蔽某些不可点击的日期，可根据自己的业务自行修改
     *
     * @param calendar calendar
     * @return 是否屏蔽某些不可点击的日期，MonthView和WeekView有类似的API可调用
     */
    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        Log.e("onCalendarIntercept", calendar.toString());
        int day = calendar.getDay();
        return day == 1 || day == 3 || day == 6 || day == 11 || day == 12 || day == 15 || day == 20 || day == 26;
    }

    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
        Toast.makeText(this, calendar.toString() + "拦截不可点击", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
        Log.e("onYearChange", " 年份变化 " + year);
    }

}


