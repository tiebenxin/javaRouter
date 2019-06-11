package com.example.main.wight.drag

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout

/**
 * Created by Liszt on 2019/6/6.
 */
class CustomLinearLayout : LinearLayout {


    var draglayout: DragLayoutKt? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setDragLayout(parent: DragLayoutKt) {
        draglayout = parent
    }

    init {

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