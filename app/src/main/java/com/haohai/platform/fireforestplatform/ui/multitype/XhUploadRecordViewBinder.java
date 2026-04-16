package com.haohai.platform.fireforestplatform.ui.multitype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haohai.platform.fireforestplatform.BR;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.databinding.ItemXhUploadRecordBinding;

import me.drakeet.multitype.ItemViewProvider;

public class XhUploadRecordViewBinder extends ItemViewProvider<XhUploadRecord, XhUploadRecordViewBinder.ViewHolder> {

    private final Context context;

    public XhUploadRecordViewBinder(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_xh_upload_record, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, @NonNull XhUploadRecord item) {
        ItemXhUploadRecordBinding binding = (ItemXhUploadRecordBinding) viewHolder.getBinding();
        binding.setVariable(BR.item, item);
        binding.setVariable(BR.adapter, this);
        binding.executePendingBindings();

        binding.eventName.setText(item.getEventName());
        binding.eventTime.setText(item.getEventTime());
        binding.eventAddress.setText(item.getEventAddress());
        binding.recordNo.setText(item.getRecordNo());
        Glide.with(context)
                .load(item.getImageUrl())
                .error(R.drawable.ic_no_pic)
                .placeholder(R.drawable.ic_no_pic)
                .into(binding.icon);
    }

    static class ViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private final B binding;

        ViewHolder(B binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public B getBinding() {
            return binding;
        }
    }
}
