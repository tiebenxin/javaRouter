package com.example.main.wight

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.common.utils.ContextHelper
import com.example.main.R
import com.nineoldandroids.view.ViewHelper

/**
 * Created by LL130386 on 2019/6/6.
 */
class DragLayoutKt(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private val TAG: String = DragLayoutKt::class.java.simpleName + "::"
    private val RATE = 0.8F//屏幕宽度百分比
    private val isShowShadow = true

    private var gestureDetector: GestureDetectorCompat//手势处理类
    private var dragHelper: ViewDragHelper//视图拖拽移动帮助类
    private var dragListener: DragListener? = null//滑动监听类
    //水平拖拽距离
    private var range: Int = 0
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    //main视图距离左边的距离
    private var mainLeft: Int = 0
    private lateinit var iv_shadow: ImageView
    private lateinit var vg_left: LinearLayout
    private lateinit var vg_main: CustomLinearLayout

    var isDrag: Boolean = false
    private var status: Status = Status.Close
    private var screenWidth: Int = 0

    /**
     * 实现子View的拖拽滑动，实现Callback当中相关的方法
     */
    private val dragHelperCallback = object : ViewDragHelper.Callback() {
        /**
         * 水平方向移动
         * @param child Child view being dragged
         * @param left Attempted motion along the X axis
         * @param dx Proposed change in position for left
         * @return
         */
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return when {
                mainLeft + dx < 0 -> 0
                mainLeft + dx > range -> range
                else -> left
            }
        }

        /**
         * 拦截所有的子View
         * @param child Child the user is attempting to capture
         * @param pointerId ID of the pointer attempting the capture
         * @return
         */
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        /**
         * 设置水平方向滑动的最远距离
         * @param child Child view to check  屏幕宽度
         * @return
         */
        override fun getViewHorizontalDragRange(child: View): Int {
            return mWidth
        }

        /**
         * 当拖拽的子View，手势释放的时候回调的方法， 然后根据左滑或者右滑的距离进行判断打开或者关闭
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            if (xvel > 0) {
                open()
            } else if (xvel < 0) {
                close()
            } else if (releasedChild === vg_main && mainLeft > range * 0.3) {
                open()
            } else if (releasedChild === vg_left && mainLeft > range * 0.7) {
                open()
            } else {
                close()
            }
        }

        /**
         * 子View被拖拽 移动的时候回调的方法
         * @param changedView View whose position changed
         * @param left New X coordinate of the left edge of the view
         * @param top New Y coordinate of the top edge of the view
         * @param dx Change in X position from the last call
         * @param dy Change in Y position from the last call
         */
        override fun onViewPositionChanged(changedView: View, left: Int, top: Int,
                                           dx: Int, dy: Int) {
            if (changedView === vg_main) {
                mainLeft = left
            } else {
                mainLeft += left
            }
            if (mainLeft < 0) {
                mainLeft = 0
            } else if (mainLeft > range) {
                mainLeft = range
            }

            if (isShowShadow) {
                iv_shadow.layout(mainLeft, 0, mainLeft + width, height)
            }
            if (changedView === vg_left) {
                vg_left.layout(0, 0, width, height)
                vg_main.layout(mainLeft, 0, mainLeft + width, height)
            }

            dispatchDragEvent(mainLeft)
        }
    }

    init {
        gestureDetector = GestureDetectorCompat(context, YScrollDetector())
        dragHelper = ViewDragHelper.create(this, dragHelperCallback)
    }


    public fun isOpen(): Boolean {
        return status == Status.Open
    }

    public fun close() {
        println(TAG + "close")
        close(true)
    }

    private fun close(animate: Boolean) {
        if (animate) {
            if (dragHelper.smoothSlideViewTo(vg_main, 0, 0))
                ViewCompat.postInvalidateOnAnimation(this)
        } else {
            vg_main.layout(0, 0, width, height)
            dispatchDragEvent(0)
        }
        if (status == Status.Open) {
            status = Status.Close
        }
    }

    fun open() {
        println(TAG + "open")
        open(true)
    }

    private fun open(animate: Boolean) {
        if (animate) {
            //继续滑动
            if (dragHelper.smoothSlideViewTo(vg_main, range, 0)) {
                ViewCompat.postInvalidateOnAnimation(this)
            }
        } else {
            vg_main.layout(range, 0, range * 2, height)
            dispatchDragEvent(range)
        }
        if (status == Status.Close) {
            status = Status.Open
        }
        updateMenuWidth(status)
    }

    inner class YScrollDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//            return Math.abs(distanceY) <= Math.abs(distanceX) && this@DragLayoutKt.isDrag != false
            return Math.abs(distanceY) <= Math.abs(distanceX) && this@DragLayoutKt.isDrag
        }
    }

    /**
     * 进行处理拖拽事件
     *
     * @param mainLeft
     */
    private fun dispatchDragEvent(mainLeft: Int) {
        if (dragListener == null) {
            return
        }
        println(TAG + "dispatchDragEvent")
        val percent = mainLeft / range.toFloat()
        //根据滑动的距离的比例,进行带有动画的缩小和放大View
        animateView(percent)
        //进行回调滑动的百分比
        dragListener?.onDrag(percent)
        val lastStatus = status
        if (lastStatus != computeStatus() && status == Status.Close) {
            dragListener?.onClose()
        } else if (lastStatus != computeStatus() && status == Status.Open) {
            dragListener?.onOpen()
        }
    }

    /**
     * 页面状态设置
     *
     * @return
     */
    private fun computeStatus(): Status {
        if (mainLeft == 0) {
            status = Status.Close
        } else if (mainLeft == range) {
            status = Status.Open
        } else {
            status = Status.Drag
        }
        return status
    }


    interface DragListener {
        fun onOpen()

        fun onClose()

        fun onDrag(percent: Float)
    }


    enum class Status {
        Drag, Open, Close
    }

    private fun updateMenuWidth(status: Status) {
        screenWidth = getScreenWidth()
        if (status == Status.Open) {
            val paddingRight = (screenWidth - screenWidth * RATE).toInt()
            vg_left.setPadding(0, 0, paddingRight, 0)
        }
        invalidate()
    }

    private fun getScreenWidth(): Int {
        var displayMetrics = DisplayMetrics()
        (ContextHelper.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 根据滑动的距离的比例,进行带有动画的缩小和放大View
     *
     * @param percent
     */
    private fun animateView(percent: Float) {
        val f1 = 1 - percent * 0.3f
        //vg_main水平方向 根据百分比缩放
        ViewHelper.setScaleX(vg_main, f1)
        //vg_main垂直方向，根据百分比缩放
        ViewHelper.setScaleY(vg_main, f1)
        //沿着水平X轴平移
        ViewHelper.setTranslationX(vg_left, -vg_left.width / 2.3f + vg_left.width / 2.3f * percent)
        //vg_left水平方向 根据百分比缩放
        ViewHelper.setScaleX(vg_left, 0.5f + 0.5f * percent)
        //vg_left垂直方向 根据百分比缩放
        ViewHelper.setScaleY(vg_left, 0.5f + 0.5f * percent)
        //vg_left根据百分比进行设置透明度
        ViewHelper.setAlpha(vg_left, percent)
        if (isShowShadow) {
            //阴影效果视图大小进行缩放
            ViewHelper.setScaleX(iv_shadow, f1 * 1.4f * (1 - percent * 0.12f))
            ViewHelper.setScaleY(iv_shadow, f1 * 1.85f * (1 - percent * 0.12f))
        }
        background?.setColorFilter(evaluate(percent, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER)
    }

    private fun evaluate(fraction: Float, startValue: Any, endValue: Int?): Int {
        val startInt = startValue as Int
        val startA = startInt shr 24 and 0xff
        val startR = startInt shr 16 and 0xff
        val startG = startInt shr 8 and 0xff
        val startB = startInt and 0xff
        val endInt = endValue!!
        val endA = endInt shr 24 and 0xff
        val endR = endInt shr 16 and 0xff
        val endG = endInt shr 8 and 0xff
        val endB = endInt and 0xff
        return ((startA + (fraction * (endA - startA)).toInt() shl 24).toInt()
                or (startR + (fraction * (endR - startR)).toInt() shl 16).toInt()
                or (startG + (fraction * (endG - startG)).toInt() shl 8).toInt()
                or (startB + (fraction * (endB - startB)).toInt()).toInt())
    }

    /**
     * 有加速度,当我们停止滑动的时候，该不会立即停止动画效果
     */
    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (isShowShadow) {
            iv_shadow = ImageView(context)
            iv_shadow.setImageResource(R.mipmap.shadow)
            var lp: LayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(iv_shadow, 1, lp)
        }
        vg_left = getChildAt(0) as LinearLayout
        vg_main = getChildAt(if (isShowShadow) 2 else 1) as CustomLinearLayout
        vg_main.draglayout = this
        vg_left.isClickable = true
        vg_main.isClickable = true

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = vg_left.measuredWidth
        mHeight = vg_left.measuredHeight
        range = (mWidth * RATE).toInt()
        println(TAG + "onSizeChanged--" + "range=" + range)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        vg_left.layout(0, 0, mWidth, mHeight)
        vg_main.layout(mainLeft, 0, mainLeft + mWidth, mHeight)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
//            println(TAG + "onInterceptTouchEvent--" + "--dragHelper==" + dragHelper.shouldInterceptTouchEvent(ev) + "--gestureDetector==" + gestureDetector.onTouchEvent(ev))
            return dragHelper.shouldInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        println(TAG + "onInterceptTouchEvent--" + "不拦截")
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        try {
            dragHelper.processTouchEvent(event)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }

    fun toggle() {
        if (status == Status.Open) {
            close()
        } else {
            open()
        }
    }

}