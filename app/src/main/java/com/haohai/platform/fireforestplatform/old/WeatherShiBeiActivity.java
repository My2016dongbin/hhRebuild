package com.haohai.platform.fireforestplatform.old;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.BaseActivity;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.bean.WeatherBean;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;

public class WeatherShiBeiActivity extends BaseActivity {
    LinearLayout ll_7day;
    ImageView icon_today;
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
        setContentView(R.layout.activity_weather_shibei);
        init();
    }

    private void init() {
        date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        dateStr = format.format(date);
        ll_7day = findViewById(R.id.ll_7day);
        icon_today = findViewById(R.id.icon_today);
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
    double lng = 120.34080104;
    double lat = 36.07300150;

    private void getData() {
        //获取当前城市信息
        QWeather.getGeoCityLookup(this, /*CommonData.lng*/lng + "," + /*CommonData.lat*/lat, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                String name = geoBean.getLocationBean().get(0).getName();
                String up = geoBean.getLocationBean().get(0).getAdm2();
                String upGov = geoBean.getLocationBean().get(0).getAdm1().replace("省","");
                Log.e("TAG", "onSuccess: " + upGov + "," + up + "," + name);
                tv_title.setText(upGov + " " + up + "·" + name);
            }
        });
        //获取实况天气
        /*QWeather.getWeatherNow(this, *//*CommonData.lng*//*lng + "," + *//*CommonData.lat*//*lat, new QWeather.OnResultWeatherNowListener() {
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
        });*/
        //获取七天天气
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SPUtils.get(this, SPValue.token,""));
        HhHttp.post()
                .url(URLConstant.POST_WEATHER_7DAY)
                .headers(headers)
                .build().execute(new LoggedInStringCallback(null,this) {
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            @Override
            public void onSuccess(String response, int id) {
                HhLog.e("POST_WEATHER_7DAY " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    List<WeatherBean> list = new Gson().fromJson(String.valueOf(data), new TypeToken<List<WeatherBean>>() {
                    }.getType());
                    List<WeatherBean> listCopy = new ArrayList<>();
                    //处理数据
                    for (int i = 0; i < list.size(); i++) {
                        WeatherBean weatherBean = list.get(i);
                        if(listCopy.isEmpty()){
                            listCopy.add(weatherBean);
                        }else{
                            if(Objects.equals(listCopy.get(listCopy.size() - 1).getYbDate(), weatherBean.getYbDate())){
                                listCopy.get(listCopy.size() - 1).setMaxTemp(Math.max(listCopy.get(listCopy.size() - 1).getMaxTemp(),weatherBean.getMaxTemp()));
                                listCopy.get(listCopy.size() - 1).setMinTemp(Math.min(listCopy.get(listCopy.size() - 1).getMinTemp(),weatherBean.getMinTemp()));
                            }else{
                                listCopy.add(weatherBean);
                            }
                        }
                    }
                    list = listCopy;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    Date dateC = calendar.getTime();
                    String todayStr = format.format(dateC);
                    List<String> dateStrList = new ArrayList<>();
                    for (int i = 0; i < 7; i++) {
                        dateStrList.add(format.format(dateC));
                        calendar.add(Calendar.DAY_OF_MONTH,1);
                        dateC = calendar.getTime();
                    }

                    ll_7day.removeAllViews();
                    for (int m = 0; m < dateStrList.size(); m++) {
                        String strDate = dateStrList.get(m);
                        if(!list.isEmpty()){
                            for (int i = 0; i < list.size(); i++) {
                                WeatherBean weatherBean = list.get(i);
                                //today
                                if(weatherBean.getYbDate()!=null && weatherBean.getYbDate().contains(todayStr)){
                                    int maxTemp_today = -999;
                                    int minTemp_today = 999;
                                    String desc_today = "";
                                    Drawable drawableIcon_today = null;
                                    minTemp_today = Math.min(minTemp_today,weatherBean.getMinTemp());
                                    maxTemp_today = Math.max(maxTemp_today,weatherBean.getMaxTemp());
                                    desc_today = weatherBean.getTianQi();
                                    try{
                                        if(weatherBean.getTianQi().contains("阵雨")){
                                            drawableIcon_today = getDrawable(R.drawable.zhenyu);
                                        }
                                        if(weatherBean.getTianQi().contains("雹")){
                                            drawableIcon_today = getDrawable(R.drawable.bingbao);
                                        }
                                        if(weatherBean.getTianQi().contains("晴")){
                                            drawableIcon_today = getDrawable(R.drawable.qing);
                                        }
                                        if(weatherBean.getTianQi().contains("多云")){
                                            drawableIcon_today = getDrawable(R.drawable.yun);
                                        }
                                        if(Objects.equals(weatherBean.getTianQi(), "多云")){
                                            drawableIcon_today = getDrawable(R.drawable.yin);
                                        }
                                        if(weatherBean.getTianQi().contains("沙")){
                                            drawableIcon_today = getDrawable(R.drawable.shachn);
                                        }
                                        if(weatherBean.getTianQi().contains("雪")){
                                            drawableIcon_today = getDrawable(R.drawable.xue);
                                        }
                                        if(weatherBean.getTianQi().contains("阴")){
                                            drawableIcon_today = getDrawable(R.drawable.yin);
                                        }
                                        if(weatherBean.getTianQi().contains("雨")){
                                            drawableIcon_today = getDrawable(R.drawable.yu);
                                        }
                                        if(weatherBean.getTianQi().contains("雨夹雪")){
                                            drawableIcon_today = getDrawable(R.drawable.yujiaxue);
                                        }
                                        if(weatherBean.getTianQi().contains("多云转晴")||weatherBean.getTianQi().contains("晴转多云")){
                                            drawableIcon_today = getDrawable(R.drawable.yun);
                                        }
                                        if(weatherBean.getTianQi().contains("阵雨")){
                                            drawableIcon_today = getDrawable(R.drawable.zhenyu);
                                        }
                                        desc_today = weatherBean.getTianQi().substring(0,4);
                                    }catch (Exception e){
                                        HhLog.e("weather error subString 0-4 ");
                                    }

                                    icon_today.setImageDrawable(drawableIcon_today);
                                    tv_current.setText(maxTemp_today + "°");
                                    tv_date.setText(dateStr + " " + CommonUtil.getWeekOfDate(date));
                                    tv_text.setText(desc_today);
                                }
                                //7day
                                if(weatherBean.getYbDate()!=null && weatherBean.getYbDate().contains(strDate)){
                                    HhLog.e("------- " + weatherBean.getYbDate() + " , " + strDate);
                                    int maxTemp = -999;
                                    int minTemp = 999;
                                    String desc = "";
                                    Drawable drawableIcon = null;
                                    minTemp = Math.min(minTemp,weatherBean.getMinTemp());
                                    maxTemp = Math.max(maxTemp,weatherBean.getMaxTemp());
                                    desc = weatherBean.getTianQi();
                                    try{
                                        //desc = weatherBean.getTianQi().substring(0,4);
                                        if(weatherBean.getTianQi().contains("阵雨")){
                                            drawableIcon = getDrawable(R.drawable.zhenyu);
                                        }
                                        if(weatherBean.getTianQi().contains("雹")){
                                            drawableIcon = getDrawable(R.drawable.bingbao);
                                        }
                                        if(weatherBean.getTianQi().contains("晴")){
                                            drawableIcon = getDrawable(R.drawable.qing);
                                        }
                                        if(weatherBean.getTianQi().contains("多云")){
                                            drawableIcon = getDrawable(R.drawable.yun);
                                        }
                                        if(Objects.equals(weatherBean.getTianQi(), "多云")){
                                            drawableIcon = getDrawable(R.drawable.yin);
                                        }
                                        if(weatherBean.getTianQi().contains("沙")){
                                            drawableIcon = getDrawable(R.drawable.shachn);
                                        }
                                        if(weatherBean.getTianQi().contains("雪")){
                                            drawableIcon = getDrawable(R.drawable.xue);
                                        }
                                        if(weatherBean.getTianQi().contains("阴")){
                                            drawableIcon = getDrawable(R.drawable.yin);
                                        }
                                        if(weatherBean.getTianQi().contains("雨")){
                                            drawableIcon = getDrawable(R.drawable.yu);
                                        }
                                        if(weatherBean.getTianQi().contains("雨夹雪")){
                                            drawableIcon = getDrawable(R.drawable.yujiaxue);
                                        }
                                        if(weatherBean.getTianQi().contains("多云转晴")||weatherBean.getTianQi().contains("晴转多云")){
                                            drawableIcon = getDrawable(R.drawable.yun);
                                        }
                                        if(weatherBean.getTianQi().contains("阵雨")){
                                            drawableIcon = getDrawable(R.drawable.zhenyu);
                                        }
                                    }catch (Exception e){
                                        HhLog.e("weather error subString 0-4 ");
                                    }

                                    View view = LayoutInflater.from(WeatherShiBeiActivity.this).inflate(R.layout.item_7day_shibei,null,false);
                                    TextView tv_week = view.findViewById(R.id.tv_week);
                                    WebView wb_7day = view.findViewById(R.id.wb_7day);
                                    ImageView icon = view.findViewById(R.id.icon);
                                    TextView tv_temp = view.findViewById(R.id.tv_temp);
                                    try {
                                        Date date = format.parse(weatherBean.getYbDate());
                                        tv_week.setText(CommonUtil.getWeekOfDateShort(date));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                /*String url = CommonUtil.getHeFengIcon((dailyBean.getTextDay().equals("晴")?"FFF68F":"FFFFFF"),dailyBean.getIconDay(),"30");
                                Log.e("TAG", "onSuccess: url = " + url );*/
                                    //设置背景色
                                    wb_7day.setBackgroundColor(0);
                                    //设置填充透明度
                                    wb_7day.getBackground().setAlpha(0);
                                    wb_7day.setScrollBarSize(0);
                                /*wb_7day.loadUrl(url);
                                wb_7day.setVisibility(View.VISIBLE);*/
                                    icon.setImageDrawable(drawableIcon);
                                    tv_temp.setText(maxTemp + "°");

                                    ll_7day.addView(view);
                                }
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    HhLog.e("error -------" + e.toString());
                }
            }

            @Override
            public void onFailure(Call call, Exception e, int id) {

            }
        });
    }
}