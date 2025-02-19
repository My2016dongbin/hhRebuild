package com.haohai.platform.fireforestplatform.ui.bean;

/**
 * Created by qc
 * on 2024/6/14.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class WeatherBean {
    /**
     * MaxTemp : 22
     * YbStartTime : 2024-06-13 20:00:00
     * WindDir : 东南风
     * YbDate : 2024-06-13 00:00:00
     * MinTemp : 21
     * ReleaseTime : 2024-06-13 16:00:00
     * DayOrNight : 夜晚
     * TianQi : 多云转阴 沿海有雾
     * WindSpe : 3-4阵风6
     */

    private int MaxTemp;
    private String YbStartTime;
    private String WindDir;
    private String YbDate;
    private int MinTemp;
    private String ReleaseTime;
    private String DayOrNight;
    private String TianQi;
    private String WindSpe;

    public int getMaxTemp() {
        return MaxTemp;
    }

    public void setMaxTemp(int MaxTemp) {
        this.MaxTemp = MaxTemp;
    }

    public String getYbStartTime() {
        return YbStartTime;
    }

    public void setYbStartTime(String YbStartTime) {
        this.YbStartTime = YbStartTime;
    }

    public String getWindDir() {
        return WindDir;
    }

    public void setWindDir(String WindDir) {
        this.WindDir = WindDir;
    }

    public String getYbDate() {
        return YbDate;
    }

    public void setYbDate(String YbDate) {
        this.YbDate = YbDate;
    }

    public int getMinTemp() {
        return MinTemp;
    }

    public void setMinTemp(int MinTemp) {
        this.MinTemp = MinTemp;
    }

    public String getReleaseTime() {
        return ReleaseTime;
    }

    public void setReleaseTime(String ReleaseTime) {
        this.ReleaseTime = ReleaseTime;
    }

    public String getDayOrNight() {
        return DayOrNight;
    }

    public void setDayOrNight(String DayOrNight) {
        this.DayOrNight = DayOrNight;
    }

    public String getTianQi() {
        return TianQi;
    }

    public void setTianQi(String TianQi) {
        this.TianQi = TianQi;
    }

    public String getWindSpe() {
        return WindSpe;
    }

    public void setWindSpe(String WindSpe) {
        this.WindSpe = WindSpe;
    }
}
