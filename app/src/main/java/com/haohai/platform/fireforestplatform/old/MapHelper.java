package com.haohai.platform.fireforestplatform.old;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MapHelper {
 
    /**
     * 地球半径，单位：公里/千米
     */
    private static final double EARTH_RADIUS = 6378.137;
 
    /**
     * 经纬度转化成弧度
     * @param d  经度/纬度
     * @return  经纬度转化成的弧度
     */
    private static double radian(double d) {
        return d * Math.PI / 180.0;
    }
 
    /**
     * 返回两个地理坐标之间的距离
     * @param firsLongitude 第一个坐标的经度
     * @param firstLatitude 第一个坐标的纬度
     * @param secondLongitude 第二个坐标的经度
     * @param secondLatitude  第二个坐标的纬度
     * @return 两个坐标之间的距离，单位：公里/千米
     */
    public static double distance(double firsLongitude, double firstLatitude,
                              double secondLongitude, double secondLatitude) {
        double firstRadianLongitude = radian(firsLongitude);
        double firstRadianLatitude = radian(firstLatitude);
        double secondRadianLongitude = radian(secondLongitude);
        double secondRadianLatitude = radian(secondLatitude);
 
        double a = firstRadianLatitude - secondRadianLatitude;
        double b = firstRadianLongitude - secondRadianLongitude;
        double cal = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(firstRadianLatitude) * Math.cos(secondRadianLatitude)
                * Math.pow(Math.sin(b / 2), 2)));
        cal = cal * EARTH_RADIUS;
 
        return Math.round(cal * 10000d) / 10000d;
    }
    public static double allDistance(List<LatLng> list) {
        double result = 0;
        for (int i = 0; i < list.size(); i++) {
            if( (i+1) < list.size() ){
                result+=distance(list.get(i).longitude,list.get(i).latitude,list.get(i+1).longitude,list.get(i+1).latitude);
            }
        }
        Log.e("bingo", "allDistance: size = " + list.size() + " allDistance = " + result );

        return result;
    }
 
    /**
     * 返回两个地理坐标之间的距离
     * @param firstPoint 第一个坐标 例如："23.100919, 113.279868"
     * @param secondPoint 第二个坐标 例如："23.149286, 113.347584"
     * @return 两个坐标之间的距离，单位：公里/千米
     */
    public static double distance(String firstPoint, String secondPoint){
        String[] firstArray = firstPoint.split(",");
        String[] secondArray = secondPoint.split(",");
        double firstLatitude = Double.valueOf(firstArray[0].trim());
        double firstLongitude = Double.valueOf(firstArray[1].trim());
        double secondLatitude = Double.valueOf(secondArray[0].trim());
        double secondLongitude = Double.valueOf(secondArray[1].trim());
        return distance(firstLatitude, firstLongitude, secondLatitude, secondLongitude);
    }
    public static long[] timeDistance(long start, long end){
        //获取结束的时间戳
        long expirationTime = end;
        //获得当前时间戳
        long timeStamp = start;
        //格式
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //转换为String类型
        String endDate = formatter.format(expirationTime);//结束的时间戳
        String startDate = formatter.format(timeStamp);//开始的时间戳
        // 获取服务器返回的时间戳 转换成"yyyy-MM-dd HH:mm:ss"
        // 计算的时间差
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(endDate);//后的时间
            Date d2 = df.parse(startDate); //前的时间
            Long diff = d1.getTime() - d2.getTime(); //两时间差，精确到毫秒
            Long day = diff / (1000 * 60 * 60 * 24); //以天数为单位取整
            Long hour=(diff/(60*60*1000)-day*24); //以小时为单位取整
            Long min=((diff/(60*1000))-day*24*60-hour*60); //以分钟为单位取整
            Long second=(diff/1000-day*24*60*60-hour*60*60-min*60);//秒
            Log.e("tag","day =" +day);
            Log.e("tag","hour =" +hour);
            Log.e("tag","min =" +min);
            Log.e("tag","second =" +second);
            long[] longs = new long[5];
            longs[0] = day;
            longs[1] = hour;
            longs[2] = min;
            longs[3] = second;
            longs[4] = diff;
            return longs;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new long[5];
    }
}