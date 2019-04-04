package com.example.mychat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mychat.multi.IItemClickListener;

import java.util.List;

/**
 * Created by LL130386 on 2019/3/25.
 */

public class AdapterTest extends RecyclerView.Adapter {

    private final Context mContext;
    private IItemClickListener listener;

    public AdapterTest(Context context) {
        mContext = context;
    }

    private List<String> list;

    public void setData(List<String> l) {
        list = l;
        notifyDataSetChanged();
    }

    private void setListener(IItemClickListener l){
        listener = l;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolderTest viewHolder = new ViewHolderTest(View.inflate(mContext, R.layout.item_text, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderTest viewHodlerTest = (ViewHolderTest) holder;
        viewHodlerTest.bindData(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolderTest extends RecyclerView.ViewHolder {

        private final TextView tv_content;

        public ViewHolderTest(View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
        }

        public void bindData(String s) {
            tv_content.setText(s);
        }

    }
}
