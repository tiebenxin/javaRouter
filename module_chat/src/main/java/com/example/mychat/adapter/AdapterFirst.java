package com.example.mychat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mychat.R;
import com.example.mychat.bean.FirstBean;
import com.example.mychat.multi.ItemViewBinder;

/**
 * Created by LL130386 on 2019/4/4.
 */

public class AdapterFirst extends ItemViewBinder<FirstBean, AdapterFirst.HV> {


    @NonNull
    @Override
    protected HV onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        HV hv = new HV(inflater.inflate(R.layout.item_first, parent,false));
        return hv;
    }

    @Override
    protected void onBindViewHolder(@NonNull HV holder, @NonNull FirstBean item) {
        holder.bindData(item);
    }

    static class HV extends RecyclerView.ViewHolder {

        private final TextView tv_name, tv_sex, tv_age;

        HV(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_sex = itemView.findViewById(R.id.tv_sex);
            tv_age = itemView.findViewById(R.id.tv_age);
        }

        public void bindData(FirstBean bean) {
            tv_name.setText(bean.getName());
            tv_sex.setText(bean.getSex() == 1 ? "男" : "女");
            tv_age.setText(bean.getAge() + "");
        }
    }
}
