package com.haohai.platform.fireforestplatform.ui.cell;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.databinding.DataBindingUtil;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.DialogTeamMateDetailBinding;
import com.haohai.platform.fireforestplatform.ui.bean.TeamMate;
import com.haohai.platform.fireforestplatform.utils.CommonUtil;

public class TeamMateDetailDialog extends Dialog {

    private final DialogTeamMateDetailBinding binding;
    private TeamMate teamMate;

    public TeamMateDetailDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_team_mate_detail, null, false);
        setContentView(binding.getRoot());
    }

    public void setTeamMate(TeamMate teamMate) {
        this.teamMate = teamMate;
        updateData();
    }

    private void updateData() {
        if (teamMate == null) {
            return;
        }
        binding.name.setText(CommonUtil.parseNullString(teamMate.getFullName(), "暂无数据"));
        binding.phone.setText(CommonUtil.parseNullString(teamMate.getPhone(), "暂无数据"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
