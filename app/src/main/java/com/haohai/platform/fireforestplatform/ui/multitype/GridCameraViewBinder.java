package com.haohai.platform.fireforestplatform.ui.multitype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.HhHttp;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.databinding.ItemGridCameraBinding;
import com.haohai.platform.fireforestplatform.databinding.ItemGridTreesBinding;
import com.haohai.platform.fireforestplatform.permission.CommonPermission;
import com.haohai.platform.fireforestplatform.ui.bean.CommonParams;
import com.haohai.platform.fireforestplatform.ui.bean.PostStar;
import com.haohai.platform.fireforestplatform.utils.CommonData;

import org.xutils.http.RequestParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.ItemViewProvider;
import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

/**
 * Created by qc
 * on 2023/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class GridCameraViewBinder extends ItemViewProvider<GridCamera, GridCameraViewBinder.ViewHolder> {
    public Context context;
    public GridCameraViewBinder(Context context) {
        this.context = context;
    }
    public OnItemClickListener listener;
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_grid_camera, parent, false);
        return new ViewHolder(dataBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull final GridCamera gridCamera) {

        ItemGridCameraBinding binding = (ItemGridCameraBinding) viewHolder.getBinding();
        /*if(CommonData.search!=null && !CommonData.search.isEmpty() && !gridCamera.toString().contains(CommonData.search)){
            binding.click.setVisibility(View.GONE);
            return;
        }*/
        binding.name.setText(gridCamera.getName());
        binding.name.setTextColor(Objects.equals(gridCamera.getIsOnLine(), "1")?context.getResources().getColor(R.color.c7):context.getResources().getColor(R.color.c5));
        binding.state.setImageResource(Objects.equals(gridCamera.getIsOnLine(), "1")?R.mipmap.ic_online_camera:R.mipmap.ic_offline_camera);
        binding.star.setImageResource(Objects.equals(gridCamera.getCollectionState(), "1") ?R.drawable.star_sl:R.drawable.star_un);
        binding.click.setOnClickListener(v -> {
            if(!Objects.equals(gridCamera.getIsOnLine(), "1")){
                Toast.makeText(context, "设备离线", Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onItemClick(gridCamera);
        });
        binding.star.setOnClickListener(v -> {
            if(!CommonPermission.hasPermission(context,CommonPermission.VIDEO_STAR)){
                Toast.makeText(context, "当前账号没有操作权限", Toast.LENGTH_SHORT).show();
                return;
            }
            gridCamera.setCollectionState(Objects.equals(gridCamera.getCollectionState(), "1") ?"0":"1");
            listener.onStarClick(gridCamera);
            postStarStatus(gridCamera);
        });


    }

    private void postStarStatus(GridCamera gridCamera) {
        HhHttp.method(Objects.equals(gridCamera.getCollectionState(), "1") ? "POST" : "DELETE"
                , Objects.equals(gridCamera.getCollectionState(), "1") ? URLConstant.POST_VIDEO_COLLECTION : URLConstant.DELETE_VIDEO_COLLECTION
                , new Gson().toJson(new PostStar(gridCamera.getId(),gridCamera.getName(),null,gridCamera.getMonitorId())), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //Toast.makeText(context, response.body().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    static class ViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private final B mBinding;

        ViewHolder(B mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
        public B getBinding() {
            return mBinding;
        }
    }


    public interface OnItemClickListener{
        void onItemClick(GridCamera gridCamera);
        void onStarClick(GridCamera gridCamera);
    }
}
