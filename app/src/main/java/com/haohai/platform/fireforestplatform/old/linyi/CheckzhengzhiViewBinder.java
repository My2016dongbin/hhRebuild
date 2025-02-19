package com.haohai.platform.fireforestplatform.old.linyi;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;

import me.drakeet.multitype.ItemViewProvider;


/**
 * Created by geyang on 2019/11/25.
 */
public class CheckzhengzhiViewBinder extends ItemViewProvider<CheckField, CheckzhengzhiViewBinder.ViewHolder> {

    public OnCheckFieldItemClick listener;

    public void setListener(OnCheckFieldItemClick listener) {
        this.listener = listener;
    }
    public String codename;
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_check_zhengzhi, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull final CheckField checkField) {
        Log.e("TAG", "onBindViewHolder: "+checkField.getCode() );
        switch (checkField.getCode()){
            case "isMaterialAdequate":
                codename="是否按标准储备足够数量的物资";
                break;
            case "isCollectFire":
                codename="各卡口是否配备火种收集箱，严格火种收缴";
                break;
            case "isCombustiblesClear":
                codename="道路两侧、林缘地带和墓地周边可燃物是否按规定清理";
                break;
            case "isPropagandaSlogan":
                codename="是否在重要区域张贴、悬挂宣传标语";
                break;
            case "isWarmSafety":
                codename="护林检查站内是否配备取暖设备并保障取暖安全";
                break;
            case "isEquipmentAdequate":
                codename="护林员防护装备是否配备齐全充足";
                break;
            case "isFireEquipment":
                codename="护林检查站是否配备基本的灭火装备";
                break;
            case "isMaterialTidy":
                codename="仓库内物资和装备是否摆放整齐有序";
                break;
            case "isEquipmentUsable":
                codename="防灭火装备是否能够正常使用";
                break;
            case "isPeopleUseEquipment":
                codename="护林员是否会正确使用防灭火装备";
                break;
            case "isEquipmentTidy":
                codename="护林检查站内防火装备是否摆放整齐有序" ;
                break;
            case "isCivilizedSacrifice":
                codename="是否广泛开展用鲜花换烧纸等文明祭祀、无火祭祀 ";
                break;
            case "isDeploy":
                codename="是否对森林防灭火工作进行了部署 ";
                break;
            case "isOilSafety":
                codename="各类油料是否单独存放，并确保安全 ";
                break;
            case "isEquipmentOverhaul":
                codename="是否全面检修和维护保养扑火装备，确保完好率100% ";
                break;
            case "isLedgerComplete":
                codename="采购、入库、调用、销毁等台账是否齐全 ";
                break;
            case "isWaterBag":
                codename="是否在道路两侧、墓地等重点部位布设水囊（水罐）等水灭火设施 ";
                break;
            case "isWaterBagFilled":
                codename="水囊(水罐)是否已经注满水 ";
                break;
            case "isWaterBagPump":
                codename="水囊(水罐)是否配备高压水泵 ";
                break;
            case "isAllDuty":
                codename="是否全员值班备勤，集中食宿 ";
                break;
            case "isDrill":
                codename="是否组织了培训演练 ";
                break;
            case "isPatrol":
                codename="是否开展动态巡逻 ";
                break;
            case "isEquipmentComplete":
                codename="个人防护装备是否齐全充足 ";
                break;
            case "isCarComplete":
                codename="森林防火车辆是否配齐 ";
                break;
            case "isCarOverhaul":
                codename = "森林防火车辆是否开展全面检修，确保安全运行 ";
                break;
            case "isCarSpecialDrive":
                codename = "森林防火车辆是否安排专人驾驶，驾驶员证件与所驾车型是否相符 ";
                break;
            case "isPropagandaSign":
                codename = "是否在进山主要路口检查站位置设立了宣传标牌 ";
                break;
            case "isOrganizeCheck":
                codename="是否作出安排并组织开展了检查 ";
                break;
            case "isNofireNotice":
                codename="是否在进山主要路口张贴禁火通告";
                break;
            case "isFireDangerFlag":
                codename="在林内景点、主要进山路口等是否悬挂相应森林火险预警旗 ";
                break;
            case "isStaffOnduty":
                codename="护林检查站等卡口执勤人员是否在岗 ";
                break;
            case "isWearArmband":
                codename="护林员是否佩带防火检查袖标 ";
                break;
            case "isPeopleRegister":
                codename="各卡口是否做好进山人员、车辆登记 ";
                break;
            case "isInstitutionWall":
                codename="护林检查站内工作制度是否上墙";
                break;
            case "isGridWall":
                codename="护林检查站责任网格名单是否上墙 ";
                break;
            case "isPropaganda":
                codename="是否协调电视、广播、报刊、网络等新闻媒体和电信部门广泛开展全民森林防火宣传教育 ";
                break;
            case "isSmoke":
                codename="护林员没有在岗吸烟现象 ";
                break;
            case "isButt":
                codename="护林检查站附近没有烟头 ";
                break;
            case "isWaterBagWarningSign":
                    codename="水囊(水罐)是否悬挂安全警示标志";
                    break;
        }
        holder.nameView.setText(codename);
        if (checkField.state == 2){
            holder.checkTxt.setText("否");
            holder.checkTxt.setTextColor(Color.RED);
        }else {
            holder.checkTxt.setText("是");
            holder.checkTxt.setTextColor(Color.GREEN);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView checkTxt;
        private final TextView nameView;

        ViewHolder(View itemView) {
            super(itemView);
            nameView = ((TextView) itemView.findViewById(R.id.check_field_name_view));
            checkTxt = ((TextView) itemView.findViewById(R.id.check_txt));
        }
    }

    public interface OnCheckFieldItemClick{
        void onCheckFieldItemClickLinstener(String id, int state);
    }
}
