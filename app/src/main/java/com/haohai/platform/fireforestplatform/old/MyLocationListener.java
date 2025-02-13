package com.haohai.platform.fireforestplatform.old;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.haohai.platform.fireforestplatform.HhApplication;
import com.haohai.platform.fireforestplatform.utils.CommonData;
import com.haohai.platform.fireforestplatform.utils.HhToast;
import com.haohai.platform.fireforestplatform.utils.SPUtils;
import com.haohai.platform.fireforestplatform.utils.SPValue;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

public class MyLocationListener extends BDAbstractLocationListener {
    private static final String TAG = MyLocationListener.class.getSimpleName();
    private Date date;

    @Override
    public void onReceiveLocation(BDLocation location){
        date = new Date();
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f

        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

        if(latitude != 0 && longitude != 0){
            CommonData.dis_int = 0;
            Log.e(TAG, "onReceiveLocation: getTime() " + date.getTime() );
            if( !String.valueOf(latitude).contains("E") ){
                /*CommonData.lng_old = CommonData.lng;
                CommonData.lat_old = CommonData.lat;*/
                CommonData.lat = latitude;
                CommonData.lng = longitude;
                Log.e(TAG, "onReceiveLocation:经纬度是 in" +  latitude +"," +longitude);
                SPUtils.put(HhApplication.getInstance(), SPValue.latitude,latitude);
                SPUtils.put(HhApplication.getInstance(), SPValue.longitude,longitude);
            }else{
                Log.e(TAG, "onReceiveLocation: distance else " );
            }
        }

        Log.e(TAG, "onReceiveLocation:经纬度是 " +  latitude +"," +longitude);
        //Toast.makeText(HhApplication.getInstance(), "百度地图 onReceiveLocation: " +  latitude +"," +longitude, Toast.LENGTH_LONG).show();
    }
}
