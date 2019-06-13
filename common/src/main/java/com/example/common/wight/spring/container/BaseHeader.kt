package com.example.common.wight.spring.container

import android.view.View
import com.example.common.wight.spring.widght.DragHandler

/**
 * Created by LL130386 on 2019/6/11.
 */
abstract class BaseHeader : DragHandler {
    /*
   * 这个方法用于设置当前View的临界高度(limit height)，即拉动到多少会被认定为刷新超作，而没到达该高度则不会执行刷新
   * 返回值大于0才有效，如果<=0 则设置为默认header的高度
   * 默认返回0
   */
    override fun getDragLimitHeight(rootView: View): Int {
        return 0
    }

    /*
   * 这个方法用于设置下拉最大高度(max height)，无论怎么拉动都不会超过这个高度返回值大于0才有效，如果<=0 则默认600px
   * 默认返回0
   */
    override fun getDragMaxHeight(rootView: View): Int {
        return 0
    }

    /*
   * 这个方法用于设置下拉弹动高度(spring height)，即弹动后停止状态的高度
   * 返回值大于0才有效，如果<=0 则设置为默认header的高度
   */
    override fun getDragSpringHeight(rootView: View): Int {
        return 0
    }
}