package com.example.main.wight

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout

/**
 * Created by LL130386 on 2019/6/6.
 */
class CustomRelativeLayout(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    var draglayout: DragLayoutKt? = null

    fun setDragLayout(parent: DragLayoutKt) {
        draglayout = parent
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (draglayout!!.isOpen()) {
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (draglayout!!.isOpen()) {
            if (event?.action == MotionEvent.ACTION_UP) {
                draglayout?.close()
            }
        }
        return super.onTouchEvent(event)
    }

}