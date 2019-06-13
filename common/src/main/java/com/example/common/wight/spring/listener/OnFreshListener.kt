package com.example.common.wight.spring.listener

/**
 * Created by LL130386 on 2019/6/11.
 */
interface OnFreshListener {

    /**
     * 下拉刷新，回调接口
     */
    abstract fun onRefresh()

    /**
     * 上拉加载，回调接口
     */
    abstract fun onLoadMore()
}