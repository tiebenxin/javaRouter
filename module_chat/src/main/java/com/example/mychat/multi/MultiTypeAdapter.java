package com.example.mychat.multi;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

/**
 * Created by Liszt on 2019/4/4.
 */

public class MultiTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MultiTypeAdapter";

    private @NonNull
    List<?> items;
    private @NonNull
    TypePool typePool;

    public MultiTypeAdapter() {
        this(Collections.emptyList());
    }

    public MultiTypeAdapter(@NonNull List<?> items) {
        this(items, new MultiTypePool());
    }

    public MultiTypeAdapter(@NonNull List<?> items, int initialCapacity) {
        this(items, new MultiTypePool(initialCapacity));
    }


    public MultiTypeAdapter(@NonNull List<?> items, @NonNull TypePool pool) {
        this.items = items;
        this.typePool = pool;
    }

    public <T> void register(@NonNull Class<? extends T> clazz, @NonNull ItemViewBinder<T, ?> binder) {
//        checkAndRemoveAllTypesIfNeeded(clazz);
        checkType(clazz);
        typePool.register(clazz, binder);
        binder.adapter = this;
    }

    public void setItems(@NonNull List<?> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    public @NonNull
    List<?> getItems() {
        return items;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        return indexInTypesOf(item);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemViewBinder<?, ?> binder = typePool.getItemViewBinder(viewType);
        return binder.onCreateViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        ItemViewBinder binder = typePool.getItemViewBinder(holder.getItemViewType());
        binder.onBindViewHolder(holder, item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final long getItemId(int position) {
        Object item = items.get(position);
        int itemViewType = getItemViewType(position);
        ItemViewBinder binder = typePool.getItemViewBinder(itemViewType);
        return binder.getItemId(item);
    }


    @Override
    @SuppressWarnings("unchecked")
    public final void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        getRawBinderByViewHolder(holder).onViewRecycled(holder);
    }



    @Override
    @SuppressWarnings("unchecked")
    public final boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        return getRawBinderByViewHolder(holder).onFailedToRecycleView(holder);
    }



    @Override
    @SuppressWarnings("unchecked")
    public final void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        getRawBinderByViewHolder(holder).onViewAttachedToWindow(holder);
    }



    @Override
    @SuppressWarnings("unchecked")
    public final void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        getRawBinderByViewHolder(holder).onViewDetachedFromWindow(holder);
    }


    private @NonNull
    ItemViewBinder getRawBinderByViewHolder(@NonNull RecyclerView.ViewHolder holder) {
        return typePool.getItemViewBinder(holder.getItemViewType());
    }



    private int indexInTypesOf(@NonNull Object item) {
        int index = typePool.firstIndexOf(item.getClass());
        if (index != -1) {
            return index;
        }
        throw new BinderNotFoundException(item.getClass());
    }






    private void checkType(@NonNull Class<?> clazz) {
        if (typePool.checkType(clazz)) {
            throw new TypesNotSameException(clazz);
        }
    }


}
