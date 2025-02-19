package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.base.LoggedInStringCallback;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.DialogOneBodyListBinding;
import com.haohai.platform.fireforestplatform.databinding.EmergencyFilterBinding;
import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.ui.activity.AddingUploadActivity;
import com.haohai.platform.fireforestplatform.ui.bean.EmergencyFilter;
import com.haohai.platform.fireforestplatform.ui.bean.TypeBean;
import com.haohai.platform.fireforestplatform.ui.bean.TypeTree;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFire;
import com.haohai.platform.fireforestplatform.ui.multitype.OneBodyFireViewBinder;
import com.haohai.platform.fireforestplatform.utils.HhLog;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class EmergencyFilterDialog extends Dialog implements TypeChooseDialog.TypeChooseDialogListener, DatePicker.OnDateChangedListener {

    private final Context context;
    private EmergencyFilterListener listener;
    private final EmergencyFilterBinding binding;
    private List<TypeBean> areaList;
    private List<TypeBean> levelList;
    private TypeBean areaBean;
    private TypeBean levelBean;
    private String typeIds;
    private TypeChooseDialog typeChooseDialog;
    public List<TypeTree> typeTrees = new ArrayList<>();
    public StringBuffer date = new StringBuffer();//"2023-12-01"
    public int year = 2023;
    public int month = 12;
    public int day = 1;

    public EmergencyFilterDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_emergency_filter, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(EmergencyFilterListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postTypeTree();
        init_();
        bind_();
    }


    public void postTypeTree() {
        HhHttp.get()
                .url(URLConstant.GET_TYPE_TREE)
                .build()
                .connTimeOut(10000)
                .execute(new LoggedInStringCallback(null, context) {
                    @Override
                    public void onSuccess(String response, int id) {
                        HhLog.e("GET_TYPE_TREE " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            typeTrees = new Gson().fromJson(String.valueOf(data), new TypeToken<List<TypeTree>>() {
                            }.getType());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e, int id) {
                    }
                });
    }

    private void bind_() {
        binding.reset.setOnClickListener(v -> {
            binding.title.setText("");
            binding.area.setText("");
            areaBean = null;
            binding.time.setText("");
            binding.level.setText("");
            levelBean = null;
            binding.type.setText("");
            typeIds = "";
            Toast.makeText(context, "已重置", Toast.LENGTH_SHORT).show();
        });
        binding.close.setOnClickListener(v -> {
            dismiss();
        });
        binding.llArea.setOnClickListener(v -> {
            dismiss();
            chooseArea();
        });
        binding.llTime.setOnClickListener(v -> {
            dismiss();
            showDataDialog(true);
        });
        binding.llLevel.setOnClickListener(v -> {
            dismiss();
            chooseLevel();
        });
        binding.llType.setOnClickListener(v -> {
            dismiss();
            chooseType();
        });
        binding.dismiss.setOnClickListener(v -> {
            dismiss();
        });
        binding.confirm.setOnClickListener(v -> {
            dismiss();
            EmergencyFilter filter = new EmergencyFilter();
            if (!binding.title.getText().toString().isEmpty()) {
                filter.setTitle(binding.title.getText().toString());
            }
            if(areaBean!=null){
                filter.setArea(areaBean.getTitle());
                filter.setAreaId(areaBean.getId());
            }
            if(!binding.time.getText().toString().isEmpty()){
                filter.setTime(binding.time.getText().toString());
                String[] strings = binding.time.getText().toString().split("至");
                filter.setStartTime(strings[0] + " 00:00:00");
                filter.setEndTime(strings[1] + " 00:00:00");
            }
            if(levelBean!=null){
                filter.setLevel(levelBean.getId());
            }
            if(binding.type.getText().toString().isEmpty()){
                filter.setType(binding.type.getText().toString());
                filter.setTypeId(typeIds);
            }
            listener.onEmergencyFilterResult(filter);
        });
    }

    private void init_() {
        initLocalData();
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void initLocalData() {
        areaList = new ArrayList<>();
        areaList.add(new TypeBean("370203005", "辽宁路街道"));
        areaList.add(new TypeBean("370203008", "延安路街道"));
        areaList.add(new TypeBean("370203011", "登州路街道"));
        areaList.add(new TypeBean("370203013", "宁夏路街道"));
        areaList.add(new TypeBean("370203014", "敦化路街道"));
        areaList.add(new TypeBean("370203015", "辽源路街道"));
        areaList.add(new TypeBean("370203016", "合肥路街道"));
        areaList.add(new TypeBean("370203019", "大港街道"));
        areaList.add(new TypeBean("370203020", "即墨路街道"));
        areaList.add(new TypeBean("370203021", "台东街道"));
        areaList.add(new TypeBean("370203022", "镇江路街道"));
        areaList.add(new TypeBean("370203025", "浮山新区街道"));
        areaList.add(new TypeBean("370203026", "阜新路街道"));
        areaList.add(new TypeBean("370203027", "海伦路街道"));
        areaList.add(new TypeBean("370203028", "四方街道"));
        areaList.add(new TypeBean("370203029", "兴隆路街道"));
        areaList.add(new TypeBean("370203030", "水清沟街道"));
        areaList.add(new TypeBean("370203031", "洛阳路街道"));
        areaList.add(new TypeBean("370203032", "河西街道"));
        areaList.add(new TypeBean("370203033", "湖岛街道"));
        areaList.add(new TypeBean("370203034", "开平路街道"));
        areaList.add(new TypeBean("370203035", "双山街道"));

        levelList = new ArrayList<>();
        levelList.add(new TypeBean("5", "暂未确认等级"));
        levelList.add(new TypeBean("4", "Ⅳ级（一般）"));
        levelList.add(new TypeBean("3", "Ⅲ级（较大）"));
        levelList.add(new TypeBean("2", "Ⅱ级（重大）"));
        levelList.add(new TypeBean("1", "Ⅰ级（特别重大）"));

    }


    private void chooseLevel() {
        CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_level_choose) {
            @Override
            public void onBind(final CustomDialog dialog, View v) {
                LinearLayout ll_type;
                TextView text_close;
                TextView text_confirm;
                ll_type = v.findViewById(R.id.ll_type);
                text_close = v.findViewById(R.id.text_close);
                text_confirm = v.findViewById(R.id.text_confirm);
                ll_type.removeAllViews();
                for (int i = 0; i < levelList.size(); i++) {
                    TypeBean typeBean = levelList.get(i);
                    View p_ = LayoutInflater.from(context).inflate(R.layout.item_emergency_choose, null);
                    LinearLayout all = p_.findViewById(R.id.all);
                    ImageView icon = p_.findViewById(R.id.icon);
                    TextView title = p_.findViewById(R.id.title);
                    title.setText(typeBean.getTitle());
                    if (typeBean.isChecked()) {
                        icon.setImageResource(R.drawable.yes);
                    } else {
                        icon.setImageResource(R.drawable.no);
                    }
                    all.setOnClickListener(v15 -> {
                        if (!typeBean.isChecked()) {
                            levelBean = typeBean;
                            for (int m = 0; m < levelList.size(); m++) {
                                levelList.get(m).setChecked(false);
                            }
                        }
                        typeBean.setChecked(!typeBean.isChecked());
                        onBind(dialog, v);
                    });

                    ll_type.addView(p_);
                }
                text_close.setOnClickListener(v13 -> {
                    dialog.dismiss();
                    show();
                });
                text_confirm.setOnClickListener(v14 -> {
                    int tag = 0;
                    for (int i = 0; i < levelList.size(); i++) {
                        TypeBean typeBean = levelList.get(i);
                        if (typeBean.isChecked()) {
                            tag++;
                        }
                    }
                    dialog.dismiss();
                    show();
                    if (tag > 0 && levelBean != null) {
                        binding.level.setText(levelBean.getTitle());
                    } else {
                        binding.level.setText("");
                        levelBean = null;
                    }
                });

            }
        }).setOnBackgroundMaskClickListener((dialog, v12) -> {
            return false;
        }).setMaskColor(context.getResources().getColor(R.color.transparent_dialog))
                .setCancelable(true);
    }

    private void chooseArea() {
        CustomDialog.show(new OnBindView<CustomDialog>(R.layout.layout_level_choose) {
            @Override
            public void onBind(final CustomDialog dialog, View v) {
                LinearLayout ll_type;
                TextView text_close;
                TextView text_confirm;
                ll_type = v.findViewById(R.id.ll_type);
                text_close = v.findViewById(R.id.text_close);
                text_confirm = v.findViewById(R.id.text_confirm);
                ll_type.removeAllViews();
                for (int i = 0; i < areaList.size(); i++) {
                    TypeBean typeBean = areaList.get(i);
                    View p_ = LayoutInflater.from(context).inflate(R.layout.item_emergency_choose, null);
                    LinearLayout all = p_.findViewById(R.id.all);
                    ImageView icon = p_.findViewById(R.id.icon);
                    TextView title = p_.findViewById(R.id.title);
                    title.setText(typeBean.getTitle());
                    if (typeBean.isChecked()) {
                        icon.setImageResource(R.drawable.yes);
                    } else {
                        icon.setImageResource(R.drawable.no);
                    }
                    all.setOnClickListener(v15 -> {
                        if (!typeBean.isChecked()) {
                            areaBean = typeBean;
                            for (int m = 0; m < areaList.size(); m++) {
                                areaList.get(m).setChecked(false);
                            }
                        }
                        typeBean.setChecked(!typeBean.isChecked());
                        onBind(dialog, v);
                    });

                    ll_type.addView(p_);
                }
                text_close.setOnClickListener(v13 -> {
                    dialog.dismiss();
                    show();
                });
                text_confirm.setOnClickListener(v14 -> {
                    int tag = 0;
                    for (int i = 0; i < areaList.size(); i++) {
                        TypeBean typeBean = areaList.get(i);
                        if (typeBean.isChecked()) {
                            tag++;
                        }
                    }
                    dialog.dismiss();
                    show();
                    if (tag > 0 && areaBean != null) {
                        binding.area.setText(areaBean.getTitle());
                    } else {
                        binding.area.setText("");
                        areaBean = null;
                    }
                });

            }
        }).setOnBackgroundMaskClickListener((dialog, v12) -> {
            return false;
        }).setMaskColor(context.getResources().getColor(R.color.transparent_dialog))
                .setCancelable(true);
    }

    private void chooseType() {
        if (typeTrees == null || typeTrees.isEmpty()) {
            Toast.makeText(context, "数据加载中..", Toast.LENGTH_SHORT).show();
            return;
        }

        typeChooseDialog = new TypeChooseDialog(context, R.style.ActionSheetDialogStyle);
        Window dialogWindow = typeChooseDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        typeChooseDialog.setDialogListener(this);
        typeChooseDialog.setTreeList(typeTrees);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lp.width = width;
        //lp.height = (int) (height * 0.7);
        dialogWindow.setAttributes(lp);
        typeChooseDialog.setCanceledOnTouchOutside(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            typeChooseDialog.create();
        }
        typeChooseDialog.show();
    }

    /**
     * 日期选择控件
     */
    private void showDataDialog(boolean start) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0 && start) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                if (!start) {
                    date.append("至");
                }
                date.append(year);
                if (month <= 9) {
                    date.append("-0").append((month));
                } else {
                    date.append("-").append((month));
                }
                if (day < 10) {
                    date.append("-0").append(day);
                } else {
                    date.append("-").append(day);
                }
                dialog.dismiss();
                if (start) {
                    showDataDialog(false);
                } else {
                    show();
                    binding.time.setText(date);
                }
                //选择时间
                /*showTimeDialog();*/
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                show();
            }
        });


        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(context, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH) + 1;
        int day1 = date.get(Calendar.DATE);
        String startData = year1 - 10 + "-" + month1 + "-" + day1;
        String endData = year1 + 10 + "-" + month1 + "-" + day1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date_s = null;
        Date date_e = null;
        try {
            date_s = simpleDateFormat.parse(startData);
            date_e = simpleDateFormat.parse(endData);
        } catch (ParseException e) {
            Log.e("TAG", "e: " + e.getMessage());
        }
        long startTime = date_s.getTime();
        long endTime = date_e.getTime();

        datePicker.setMaxDate(endTime);
        datePicker.setMinDate(startTime);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month - 1, day, this);
    }

    @Override
    public void onTypeChooseDialogRefresh() {

    }

    @Override
    public void onTypeChoose(TypeTree typeTree, TypeTree typeTreeSecond, TypeTree typeTreeThird) {
        String type = typeTree.getTypeName();
        typeIds = typeTree.getTypeCode();
        if (typeTreeSecond != null) {
            type = type + "/" + typeTreeSecond.getTypeName();
            typeIds = typeIds + "/" + typeTreeSecond.getTypeCode();
        }
        if (typeTreeThird != null) {
            type = type + "/" + typeTreeThird.getTypeName();
            typeIds = typeIds + "/" + typeTreeThird.getTypeCode();
        }
        binding.type.setText(type);
        HhLog.e("typeIds " + typeIds);
        show();
    }

    @Override
    public void onDateChanged(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
        year = years;
        month = monthOfYear + 1;
        day = dayOfMonth;
    }

    public interface EmergencyFilterListener {
        void onEmergencyFilterRefresh();

        void onEmergencyFilterResult(EmergencyFilter filter);
    }
}
