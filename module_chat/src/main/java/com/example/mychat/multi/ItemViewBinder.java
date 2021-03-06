package com.example.mychat.multi;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by Liszt on 2019/4/4.
 */

public abstract class ItemViewBinder<T,VH extends RecyclerView.ViewHolder> {
    /* internal */
    MultiTypeAdapter adapter;

    protected abstract @NonNull
    VH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);


    protected abstract void onBindViewHolder(@NonNull VH holder, @NonNull T item);




    protected final int getPosition(@NonNull final RecyclerView.ViewHolder holder) {
        return holder.getAdapterPosition();
    }


    protected final @NonNull
    MultiTypeAdapter getAdapter() {
        if (adapter == null) {
            throw new IllegalStateException("ItemViewBinder " + this + " not attached to MultiTypeAdapter. " +
                    "You should not call the method before registering the binder.");
        }
        return adapter;
    }



    protected long getItemId(@NonNull T item) {
        return RecyclerView.NO_ID;
    }


    protected void onViewRecycled(@NonNull VH holder) {}



    protected boolean onFailedToRecycleView(@NonNull VH holder) {
        return false;
    }



    protected void onViewAttachedToWindow(@NonNull VH holder) {}



    protected void onViewDetachedFromWindow(@NonNull VH holder) {}
}
