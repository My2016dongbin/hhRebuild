package com.haohai.platform.fireforestplatform.old;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;
import com.tencent.smtt.sdk.WebView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WeatherActivity extends BaseActivity {
    LinearLayout ll_7day;
    SwipeRefreshLayout swipe;
    TextView tv_title;
    TextView tv_gas;
    TextView tv_wet;
    TextView tv_date;
    TextView tv_wind;
    TextView tv_text;
    TextView tv_current;
    ImageView iv_back;
    WebView wb_current;
    private Date date;
    private String dateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        setContentView(R.layout.activity_weather_old);
        init();
    }

    private void init() {
        date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        dateStr = format.format(date);
        ll_7day = findViewById(R.id.ll_7day);
        swipe = findViewById(R.id.swipe);
        tv_title = findViewById(R.id.tv_title);
        tv_gas = findViewById(R.id.tv_gas);
        tv_wet = findViewById(R.id.tv_wet);
        tv_wind = findViewById(R.id.tv_wind);
        tv_date = findViewById(R.id.tv_date);
        tv_text = findViewById(R.id.tv_text);
        tv_current = findViewById(R.id.tv_current);
        iv_back = findViewById(R.id.iv_back);
        wb_current = findViewById(R.id.wb_current);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getData();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                    }
                },1000);
            }
        });
    }

    private void getData() {
        //获取当前城市信息
        QWeather.getGeoCityLookup(this, CommonData.lng + "," + CommonData.lat, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                String name = geoBean.getLocationBean().get(0).getName();
                String up = geoBean.getLocationBean().get(0).getAdm2();
                String upGov = geoBean.getLocationBean().get(0).getAdm1().replace("省","");
                Log.e("TAG", "onSuccess: " + upGov + "," + up + "," + name);
                tv_title.setText(upGov + " " + up);
            }
        });
        //获取实况天气
        QWeather.getWeatherNow(this, CommonData.lng + "," + CommonData.lat, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                String url = CommonUtil.getHeFengIcon((now.getText().equals("晴")?"FFF68F":"FFFFFF"),now.getIcon(),"80");
                Log.e("TAG", "onSuccess: url = " + url );
                //设置背景色
                wb_current.setBackgroundColor(0);
                //设置填充透明度
                wb_current.getBackground().setAlpha(0);
                wb_current.setScrollBarSize(0);
                wb_current.loadUrl(url);
                wb_current.setVisibility(View.VISIBLE);
                tv_current.setText(now.getTemp() + "°");
                tv_date.setText(dateStr + " " + CommonUtil.getWeekOfDate(date));
                tv_text.setText(now.getText());
                tv_wind.setText(now.getWindScale());
                tv_gas.setText(now.getPressure());
                tv_wet.setText(now.getHumidity());
            }
        });
        //获取七天天气
        QWeather.getWeather7D( this, CommonData.lng + "," + CommonData.lat, new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e("TAG", "onError: throwable" + throwable.getMessage() );
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                ll_7day.removeAllViews();
                List<WeatherDailyBean.DailyBean> dailyList = weatherDailyBean.getDaily();
                Log.e("TAG", "onSuccess: dailyList.size() = " +dailyList.size() );
                for (int i = 0; i < dailyList.size(); i++) {
                    WeatherDailyBean.DailyBean dailyBean = dailyList.get(i);
                    View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.item_7day,null,false);
                    TextView tv_week = view.findViewById(R.id.tv_week);
                    WebView wb_7day = view.findViewById(R.id.wb_7day);
                    TextView tv_temp = view.findViewById(R.id.tv_temp);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = format.parse(dailyBean.getFxDate());
                        tv_week.setText(CommonUtil.getWeekOfDateShort(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String url = CommonUtil.getHeFengIcon((dailyBean.getTextDay().equals("晴")?"FFF68F":"FFFFFF"),dailyBean.getIconDay(),"30");
                    Log.e("TAG", "onSuccess: url = " + url );
                    //设置背景色
                    wb_7day.setBackgroundColor(0);
                    //设置填充透明度
                    wb_7day.getBackground().setAlpha(0);
                    wb_7day.setScrollBarSize(0);
                    wb_7day.loadUrl(url);
                    wb_7day.setVisibility(View.VISIBLE);
                    tv_temp.setText(dailyBean.getTempMax() + "°");

                    ll_7day.addView(view);
                }
            }
        });
    }
}