package com.example.mychat.multi;

import android.support.annotation.NonNull;

/**
 * Created by Liszt on 2019/4/4.
 */

public interface TypePool {
    <T> void register(@NonNull Class<? extends T> clazz,@NonNull ItemViewBinder<T,?> binder);

    boolean unregister(@NonNull Class<?> clazz);

    boolean checkType(@NonNull Class<?> clazz);


    int size();


    int firstIndexOf(@NonNull Class<?> clazz);


    @NonNull
    Class<?> getClass(int index);


    @NonNull
    ItemViewBinder<?, ?> getItemViewBinder(int index);


}
