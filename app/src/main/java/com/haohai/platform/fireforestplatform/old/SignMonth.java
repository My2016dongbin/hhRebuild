package com.haohai.platform.fireforestplatform.old;

/**
 * Created by dongbin on 2022/1/12.
 */

public class SignMonth {
    String time;
    String earlyTime;
    String nightTime;
    String earlyStart;
    String nightStart;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEarlyTime() {
        return earlyTime;
    }

    public void setEarlyTime(String earlyTime) {
        this.earlyTime = earlyTime;
    }

    public String getNightTime() {
        return nightTime;
    }

    public void setNightTime(String nightTime) {
        this.nightTime = nightTime;
    }

    public String getEarlyStart() {
        return earlyStart;
    }

    public void setEarlyStart(String earlyStart) {
        this.earlyStart = earlyStart;
    }

    public String getNightStart() {
        return nightStart;
    }

    public void setNightStart(String nightStart) {
        this.nightStart = nightStart;
    }

    @Override
    public String toString() {
        return "time:"+time+",earlyTime:"+earlyTime+",nightTime:"+nightTime+",earlyStart:"+earlyStart+",nightStart:"+nightStart;
    }
}
