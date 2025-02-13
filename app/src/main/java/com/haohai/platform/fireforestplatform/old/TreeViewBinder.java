package com.haohai.platform.fireforestplatform.old;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.constant.URLConstant;
import com.haohai.platform.fireforestplatform.old.bean.Person;
import com.haohai.platform.fireforestplatform.old.bean.Tree;
import com.haohai.platform.fireforestplatform.ui.multitype.Empty;
import com.haohai.platform.fireforestplatform.ui.multitype.EmptyViewBinder;
import com.haohai.platform.fireforestplatform.utils.CommonData;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.ItemViewProvider;
import me.drakeet.multitype.MultiTypeAdapter;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TreeViewBinder extends ItemViewProvider<Tree, TreeViewBinder.ViewHolder> {

    protected OnTreeClick listener;
    protected PersonViewBinder.OnPersonClick listener_person;
    protected Context context;
    protected MultiTypeAdapter adapter;
    protected List<Object> items;

    public void setListener(OnTreeClick listener,Context context) {
        this.listener = listener;
        this.context = context;
    }
    public void setPersonListener(PersonViewBinder.OnPersonClick listener) {
        this.listener_person = listener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_tree, parent, false);
        ViewHolder viewHolder = new ViewHolder(root);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Tree tree) {
        holder.tv_title.setText(tree.getName());
        if(tree.isStatus()){
            holder.iv_title.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_open_bk));
            holder.rlv_item.setVisibility(View.VISIBLE);
        }else{
            holder.iv_title.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_close_bk));
            holder.rlv_item.setVisibility(View.GONE);
        }
        holder.ll_item.setOnClickListener(v -> {
            tree.setStatus(!tree.isStatus());
            if(tree.isStatus()){
                holder.iv_title.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_open_bk));
                holder.rlv_item.setVisibility(View.VISIBLE);
                if(tree.getChildren()==null || tree.getChildren().isEmpty()){
                    //2.人员列表展示
                    //这一步写接口回调
                    postPerson(tree.getNo());
                }
            }else{
                holder.iv_title.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_close_bk));
                holder.rlv_item.setVisibility(View.GONE);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.rlv_item.setLayoutManager(linearLayoutManager);
        items = new ArrayList<>();
        //items.addAll(TreeUtils.parseLevel(tree.getChildren()));//加载数据
        adapter = new MultiTypeAdapter(items);
        TreeViewBinder binder = new TreeViewBinder();
        binder.setListener(listener,context);
        binder.setPersonListener(listener_person);//待优化
        adapter.register(Tree.class, binder);
        PersonViewBinder binder_person = new PersonViewBinder();
        binder_person.setListener(listener_person);
        adapter.register(Person.class, binder_person);
        adapter.register(Empty.class, new EmptyViewBinder(context));
        holder.rlv_item.setAdapter(adapter);
        assertHasTheSameAdapter(holder.rlv_item, adapter);


        if(tree.getChildren()!=null && !tree.getChildren().isEmpty()){
            //1.递归网格展示
            initTreeData(TreeUtils.parseLevel(tree.getChildren()));
        }
    }

    private void postPerson(String no) {
        JSONObject object = new JSONObject();
        try {
            object.put("gridNo",no);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/auth/user/list");//所有权限
        RequestParams params = new RequestParams(URLConstant.BASE_PATH + "auth/api/auth/user/listByGrid");//下属权限
        params.addHeader("Authorization", "bearer " + CommonData.token);
        params.setBodyContent(object.toString());
        Log.e("TAG", "postPerson: postPerson params " + params );
        Log.e("TAG", "postPerson: postPerson params " + object.toString() );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "postPerson: postPerson + no : " + no + "," + result );
                try {
                    JSONObject object = new JSONObject(result);
                    List<Person> personList = new Gson().fromJson(String.valueOf(object.getJSONArray("data")),new TypeToken<List<Person>>(){}.getType());
                    initPersonData(personList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError: " + ex.toString() );
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void initTreeData(List<Tree> treeList) {
        items.clear();
        if (treeList.size() == 0) {
            items.add(new Empty("暂无数据"));
        } else {
            items.addAll(treeList);
        }
        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }
    private void initPersonData(List<Person> personList) {
        Log.e("TAG", "initPersonData: postPerson personList" + personList.size() );
        items.clear();
        if (personList.size() == 0) {
            items.add(new Empty("暂无数据"));
        } else {
            items.addAll(personList);
        }
        Log.e("TAG", "initPersonData: postPerson item" + items.size() );
        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout ll_item;
        private final ImageView iv_title;
        private final TextView tv_title;
        private final RecyclerView rlv_item;

        ViewHolder(View itemView) {
            super(itemView);
            ll_item = ((LinearLayout) itemView.findViewById(R.id.ll_item));
            iv_title = ((ImageView) itemView.findViewById(R.id.iv_title));
            tv_title = ((TextView) itemView.findViewById(R.id.tv_title));
            rlv_item = ((RecyclerView) itemView.findViewById(R.id.rlv_item));
        }
    }
    public interface OnTreeClick{
        void onTreeClickListener();
        void onTreeItemClickListener(Tree tree);
    }
}
