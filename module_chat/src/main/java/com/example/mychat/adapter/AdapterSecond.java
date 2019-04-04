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

public class AdapterSecond extends ItemViewBinder<String, AdapterSecond.HV> {


    @NonNull
    @Override
    protected HV onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        HV hv = new HV(inflater.inflate(R.layout.item_second, parent, false));
        return hv;
    }

    @Override
    protected void onBindViewHolder(@NonNull HV holder, @NonNull String item) {
        holder.bindData(item);
    }

    static class HV extends RecyclerView.ViewHolder {

        private final TextView tv_title;

        HV(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);

        }

        public void bindData(String s) {
            tv_title.setText(s);
        }
    }
}
