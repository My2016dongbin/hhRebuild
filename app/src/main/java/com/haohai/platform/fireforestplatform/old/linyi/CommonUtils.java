package com.haohai.platform.fireforestplatform.old.linyi;

import android.content.Context;

import java.util.Objects;

public class CommonUtils {
    public String parseType(String resourceType) {
        String type = "";
        if(Objects.equals(resourceType, "helicopterPoint")){
            type = "直升机机降点";
        }else if(Objects.equals(resourceType, "team")){
            type = "消防专业队";
        }else if(Objects.equals(resourceType, "dangerSource")){
            type = "危险源";
        }else if(Objects.equals(resourceType, "isolationBelt")){
            type = "隔离带";
        }else if(Objects.equals(resourceType, "fireEscape")){
            type = "防火通道";
        }else if(Objects.equals(resourceType, "chemicalEnterprises")){
            type = "危化品企业";
        }else if(Objects.equals(resourceType, "isolationNet")){
            type = "隔离网";
        }else if(Objects.equals(resourceType, "miningEnterprises")){
            type = "采矿企业";
        }else if(Objects.equals(resourceType, "materialRepository")){
            type = "物资库";
        }else if(Objects.equals(resourceType, "waterSource")){
            type = "水源地";
        }else if(Objects.equals(resourceType, "cemetery")){
            type = "墓地";
        }else if(Objects.equals(resourceType, "checkStation")){
            type = "护林检查站";
        }else if(Objects.equals(resourceType, "kakou")){
            type = "卡口监控点";
        }else if(Objects.equals(resourceType, "monitor")){
            type = "视频监控点";
        }else if(Objects.equals(resourceType, "fireCommand")){
            type = "防火指挥部";
        }
        return type;
    }
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

    /**
     * 权限判断
     * @param context 上下文
     * @param permissionCode 权限编码
     * @return
     */
    public static boolean hasPermission(Context context , String permissionCode){
        /*String permissions = new DbConfig(context).getPermissions();
        Log.e("TAG", "hasPermission: " + permissions );
        if(permissions == null){
            return false;
        }

        return permissions.contains(permissionCode+"_");*/
        return true;
    }
    public String parseZero(int num){
        if(num > 9){
           return num +"";
        }else {
            return "0" + num;
        }
    }
}
