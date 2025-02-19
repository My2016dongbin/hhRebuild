package com.haohai.platform.fireforestplatform.ui.cell;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogContactsAreaListBinding;
import com.haohai.platform.fireforestplatform.ui.bean.ContactsArea;
import com.haohai.platform.fireforestplatform.ui.multitype.MainDevice;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

public class ContactsAreaListDialog extends Dialog{

    private final Context context;
    private ContactsAreaListDialogListener dialogListener;
    private final DialogContactsAreaListBinding binding;
    public MultiTypeAdapter adapter;
    public List<ContactsArea> contactsAreaList = new ArrayList<>();
    public List<Object> items = new ArrayList<>();

    public ContactsAreaListDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context,themeResId);
        this.context = context;
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_contacts_area_list, null, false);
        setContentView(binding.getRoot());
    }

    public void setDialogListener(ContactsAreaListDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setContactsAreaList(List<ContactsArea> contactsAreaList) {
        this.contactsAreaList = contactsAreaList;
        updateData();
    }

    public void updateData() {
        binding.llList.removeAllViews();
        for (int m = 0; m < contactsAreaList.size(); m++) {
            ContactsArea area = contactsAreaList.get(m);
            binding.llList.addView(buildAreas(area));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private View buildAreas(ContactsArea area) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_contacts_areas_item, null);
        LinearLayout ll_out = view.findViewById(R.id.ll_out);
        ImageView image_status = view.findViewById(R.id.image_status);
        TextView text_title = view.findViewById(R.id.text_title);
        LinearLayout ll_in = view.findViewById(R.id.ll_in);
        image_status.setImageDrawable(context.getResources().getDrawable(area.isSelected()?R.drawable.icon_up:R.drawable.icon_down));

        text_title.setText(area.getLabel());
        if(area.getChildren()==null){
            image_status.setVisibility(View.GONE);
            ll_out.setOnClickListener(v -> {
                dialogListener.onContactsAreaListDialogItemClick(area);
            });
        }else{
            image_status.setVisibility(View.VISIBLE);
            ll_in.setVisibility(area.isSelected()?View.VISIBLE:View.GONE);
            ll_out.setOnClickListener(v -> {
                area.setSelected(!area.isSelected());
                image_status.setImageDrawable(context.getResources().getDrawable(area.isSelected()?R.drawable.icon_up:R.drawable.icon_down));
                ll_in.setVisibility(area.isSelected()?View.VISIBLE:View.GONE);
            });
            ll_in.removeAllViews();
            for (int m = 0; m < area.getChildren().size(); m++) {
                ContactsArea areaIn = area.getChildren().get(m);
                ll_in.addView(buildAreas(areaIn));
            }
        }

        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_();
        bind_();
    }

    private void bind_() {
        binding.textReset.setOnClickListener(v -> {
            dialogListener.onContactsAreaListDialogReset();
        });
    }

    private void init_() {

    }



    public interface ContactsAreaListDialogListener{
        void onContactsAreaListDialogReset();
        void onContactsAreaListDialogItemClick(ContactsArea area);
    }
}
