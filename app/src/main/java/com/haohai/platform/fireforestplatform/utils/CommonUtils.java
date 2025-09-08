package com.haohai.platform.fireforestplatform.utils;

import android.content.Context;
import android.util.Log;

import java.util.Objects;

public class CommonUtils {
    public String parseNull(String str,String defStr){
        boolean result = false;
        if(str == null){
            result = true;
        }
        if(Objects.equals(str, "null")){
            result = true;
        }
        if(Objects.equals(str, "")){
            result = true;
        }
        return result?defStr:str;
    }
    public String parseDate(String str){
        if(str.length()<19){
            return str.replace("null","").replace("T"," ");
        }else{
            return str.substring(0,19).replace("null","").replace("T"," ");
        }
    }

    public String parseZero(int num){
        if(num > 9){
            return num +"";
        }else {
            return "0" + num;
        }
    }
}
