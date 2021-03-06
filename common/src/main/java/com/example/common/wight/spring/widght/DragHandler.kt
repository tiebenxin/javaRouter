package com.example.common.wight.spring.widght

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Liszt on 2019/6/11.
 */

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//    ┃　　　┃   神兽保佑
//    ┃　　　┃   代码无BUG！
//    ┃　　　┗━━━┓
//    ┃　　　　　　　┣┓
//    ┃　　　　　　　┏┛
//    ┗┓┓┏━┳┓┏┛
//      ┃┫┫　┃┫┫
//      ┗┻┛　┗┻┛

interface DragHandler {
    abstract fun getView(inflater: LayoutInflater, viewGroup: ViewGroup): View

    abstract fun getDragLimitHeight(rootView: View): Int

    abstract fun getDragMaxHeight(rootView: View): Int

    abstract fun getDragSpringHeight(rootView: View): Int

    abstract fun onPreDrag(rootView: View)

    /**
     * 手指拖动控件过程中的回调，用户可以根据拖动的距离添加拖动过程动画
     *
     * @param dy 拖动距离，下拉为+，上拉为-
     */
    abstract fun onDropAnim(rootView: View, dy: Int)

    /**
     * 手指拖动控件过程中每次抵达临界点时的回调，用户可以根据手指方向设置临界动画
     *
     * @param upOrDown 是上拉还是下拉
     */
    abstract fun onLimitDes(rootView: View, upOrDown: Boolean)

    /**
     * 拉动超过临界点后松开时回调
     */
    abstract fun onStartAnim()

    /**
     * 头(尾)已经全部弹回时回调
     */
    abstract fun onFinishAnim()

    /**
     * 刷新成功
     */
    abstract fun onFinishRefresh()
}