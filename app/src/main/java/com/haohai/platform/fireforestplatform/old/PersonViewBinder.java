package com.haohai.platform.fireforestplatform.old;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.old.bean.Person;

import me.drakeet.multitype.ItemViewProvider;


public class PersonViewBinder extends ItemViewProvider<Person, PersonViewBinder.ViewHolder> {
    public OnPersonClick listener;

    public void setListener(OnPersonClick listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_person, parent, false);
        ViewHolder viewHolder = new ViewHolder(root);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Person person) {
        holder.ll_item.setOnClickListener(v -> {
            listener.onPersonClickListener(person);
        });
        holder.tv_title.setText(person.getFullName());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout ll_item;
        private final TextView tv_title;

        ViewHolder(View itemView) {
            super(itemView);
            ll_item = ((LinearLayout) itemView.findViewById(R.id.ll_item));
            tv_title = ((TextView) itemView.findViewById(R.id.tv_title));
        }
    }

    public interface OnPersonClick{
        void onPersonClickListener(Person person);
    }
}
