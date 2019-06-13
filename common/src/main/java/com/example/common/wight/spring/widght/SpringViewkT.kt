package com.example.common.wight.spring.widght

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ListView
import android.widget.OverScroller
import com.example.common.R
import com.example.common.wight.spring.listener.OnFreshListener

@Suppress("DEPRECATION")
/**
 * Created by LL130386 on 2019/6/12.
 */
class SpringViewkT(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private var inflater: LayoutInflater
    private var mScroller: OverScroller
    private lateinit var listener: OnFreshListener
    private var isCallDown = false
    private var isCallUp = false
    private var isFirst = true
    private var needChange = true
    private var needResetAnim = true
    private var isFullEnable = true
    private var isMoveNow = true
    private var enable = true
    private var lastMoveTime: Long = 0

    private var MOVE_TIME = 400
    private var MOVE_TIME_OVER = 200
    private var hasTop = 0

    enum class Give {
        BOTH, TOP, BOTTOM, NONE
    }

    enum class Type {
        OVERLAP, FOLLOW
    }

    private var give = Give.BOTH
    private var type = Type.OVERLAP
    private var _type: Type? = null

    //最大拉动距离，拉动距离越靠近这个值拉动就越缓慢
    private var MAX_HEADER_PULL_HEIGHT = 600
    private var MAX_FOOTER_PULL_HEIGHT = 600
    //拉动多少距离被认定为刷新(加载)动作
    private var HEADER_LIMIT_HEIGHT: Int = 0
    private var FOOTER_LIMIT_HEIGHT: Int = 0
    private var HEADER_SPRING_HEIGHT: Int = 0
    private var FOOTER_SPRING_HEIGHT: Int = 0
    //储存上次的Y坐标
    private var mLastY: Float = 0.toFloat()
    private var mLastX: Float = 0.toFloat()
    //储存第一次的Y坐标
    private var mFirstY: Float = 0.toFloat()
    //储存手指拉动的总距离
    private var dsY: Float = 0.toFloat()
    //滑动事件目前是否在本控件的控制中
    private var isInControl = false
    //存储拉动前的位置
    private val mRect = Rect()

    //头尾内容布局
    private var header: View? = null
    private var footer: View? = null
    private var contentView: View? = null

    private var headerResourceId: Int = 0
    private var footerResourceId: Int = 0
//    private var isTopScrolling = false

    //移动参数：计算手指移动量的时候会用到这个值，值越大，移动量越小，若值为1则手指移动多少就滑动多少px
    private val MOVE_PARA = 2.0

    private var needChangeHeader = false
    private var needChangeFooter = false
    private var _headerHandler: DragHandler? = null
    private var _footerHandler: DragHandler? = null
    private var headerHandler: DragHandler? = null
    private var footerHandler: DragHandler? = null

    private var dy: Float = 0.toFloat()
    private var dx: Float = 0.toFloat()
    private var isNeedMyMove: Boolean = false

    private var callFreshOrLoad = 0
    private var isFullAnim: Boolean = false
    private var hasCallFull = false
    private var hasCallRefresh = false
    private var _firstDrag = true


    /**
     * 处理多点触控的情况，准确地计算Y坐标和移动距离dy
     * 同时兼容单点触控的情况
     */
    private var mActivePointerId = MotionEvent.INVALID_POINTER_ID


    init {
        inflater = LayoutInflater.from(context)
        mScroller = OverScroller(context)
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SpringView)
        var typeInt = ta.getInt(R.styleable.SpringView_svType, 0)
        type = Type.values()[typeInt]

        var giveInt = ta.getInt(R.styleable.SpringView_give, 0)
        give = Give.values()[giveInt]
        headerResourceId = ta.getResourceId(R.styleable.SpringView_header, 0)
        footerResourceId = ta.getResourceId(R.styleable.SpringView_footer, 0)
        ta.recycle()
    }

    override fun onFinishInflate() {
        if (contentView == null) {
            contentView = getChildAt(0)
        }
        if (contentView == null) {
            return
        }
        setPadding(0, 0, 0, 0)
        contentView!!.setPadding(0, contentView!!.paddingTop, 0, contentView!!.paddingBottom)
        if (headerResourceId != 0) {
            inflater.inflate(headerResourceId, this, true)
            header = getChildAt(childCount - 1)
        }
        if (footerResourceId != 0) {
            inflater.inflate(footerResourceId, this, true)
            footer = getChildAt(childCount - 1)
            footer!!.visibility = View.INVISIBLE
        }
        contentView!!.bringToFront()
        super.onFinishInflate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount > 0) {
            (0 until childCount)
                    .map { getChildAt(it) }
                    .forEach { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        }

        //如果是动态设置的头部，则使用动态设置的参数
        if (headerHandler != null) {
            //设置下拉最大高度，只有在>0时才生效，否则使用默认值
            var xh: Int = headerHandler!!.getDragMaxHeight(header!!)
            if (xh > 0) {
                MAX_HEADER_PULL_HEIGHT = xh
            }
            //设置下拉临界高度，只有在>0时才生效，否则默认为header的高度
            var h: Int = headerHandler!!.getDragLimitHeight(header!!)
            HEADER_LIMIT_HEIGHT = if (h > 0) h else header!!.measuredHeight
            //设置下拉弹动高度，只有在>0时才生效，否则默认和临界高度一致
            var sh: Int = headerHandler!!.getDragSpringHeight(header!!)
            HEADER_SPRING_HEIGHT = if (sh > 0) sh else HEADER_LIMIT_HEIGHT
        } else {
            //不是动态设置的头部，设置默认值
            if (header != null) {
                HEADER_LIMIT_HEIGHT = header!!.measuredHeight
            }
            HEADER_SPRING_HEIGHT = HEADER_LIMIT_HEIGHT
        }

        //设置尾部参数，和上面一样
        if (footerHandler != null) {
            val xh = footerHandler!!.getDragMaxHeight(footer!!)
            if (xh > 0) {
                MAX_FOOTER_PULL_HEIGHT = xh
            }
            val h = footerHandler!!.getDragLimitHeight(footer!!)
            FOOTER_LIMIT_HEIGHT = if (h > 0) h else footer!!.measuredHeight
            val sh = footerHandler!!.getDragSpringHeight(footer!!)
            FOOTER_SPRING_HEIGHT = if (sh > 0) sh else FOOTER_LIMIT_HEIGHT
        } else {
            if (footer != null) {
                FOOTER_LIMIT_HEIGHT = header!!.measuredHeight
            }
            FOOTER_SPRING_HEIGHT = FOOTER_LIMIT_HEIGHT
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (contentView != null) {
            if (type === Type.OVERLAP) {
                if (header != null) {
                    header!!.layout(0, 0, width, header!!.measuredHeight)
                }
                if (footer != null) {
                    footer!!.layout(0, height - footer!!.measuredHeight, width, height)
                }
            } else if (type === Type.FOLLOW) {
                if (header != null) {
                    header!!.layout(0, -header!!.measuredHeight, width, 0)
                }
                if (footer != null) {
                    footer!!.layout(0, height, width, height + footer!!.measuredHeight)
                }
            }
            contentView!!.layout(0, 0, contentView!!.measuredWidth, contentView!!.measuredHeight)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        dealMulTouchEvent(ev)
        var action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                hasCallFull = false
                hasCallRefresh = false
                mFirstY = ev.y
                var isTop: Boolean = isChildScrollToTop()
                var isBottom: Boolean = isChildScrollToBottomFull(isFullEnable)
                if (isTop || isBottom) {
                    isNeedMyMove = false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                dsY += dy
                isMoveNow = true
                isNeedMyMove = isNeedMuMove()
                if (isNeedMyMove && !isInControl) {
                    isInControl = true
                    ev.action = MotionEvent.ACTION_CANCEL
                    println("重新设置ACTION_CANCEL")
                    val ev2 = MotionEvent.obtain(ev)
                    dispatchTouchEvent(ev)
                    ev2.action = MotionEvent.ACTION_DOWN
                    println("重新设置ACTION_DOWN")
                    return dispatchTouchEvent(ev2)
                }
            }
            MotionEvent.ACTION_UP -> {
                isMoveNow = false
            }
            MotionEvent.ACTION_CANCEL -> {
                isMoveNow = false
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return isNeedMyMove && enable
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (contentView == null) {
            return false
        }
        var action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                isFirst = true
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                if (isNeedMyMove) {
                    needResetAnim = false//按下时关闭回弹
                    doMove()
                    if (isTop()) {
                        if (header != null && header!!.visibility != View.VISIBLE) {
                            header!!.visibility = View.VISIBLE
                        }
                        if (footer != null && footer!!.visibility != View.INVISIBLE) {
                            footer!!.visibility = View.INVISIBLE
                        }
                    } else if (isBottom()) {
                        if (header != null && header!!.visibility != View.INVISIBLE) {
                            header!!.visibility = View.INVISIBLE
                        }

                        if (footer != null && header!!.visibility != View.VISIBLE) {
                            footer!!.visibility = View.VISIBLE
                        }
                    }
                    //回调onDropAnim接口
                    callOnDropAnim()
                    //回调callOnPreDrag接口
                    callOnPreDrag()
                    //回调onLimitDes接口
                    callOnLimitDes()
                } else {
                    //手指在产生移动的时候dy!= 0 才重置位置
                    if (dy != 0F && isFlow()) {
                        resetPosition()
                        event.action = MotionEvent.ACTION_DOWN
                        dispatchTouchEvent(event)
                        isInControl = false
                    }
                }

            }
            MotionEvent.ACTION_UP -> {
                needResetAnim = true
                isFirst = true
                _firstDrag = true
                restSmartPosition()
                dsY = 0F
                dy = 0F
            }
        }
        return true
    }

    private fun doMove() {
        if (type == Type.OVERLAP) {
            if (mRect.isEmpty) {
                mRect.set(contentView!!.left, contentView!!.top, contentView!!.right, contentView!!.bottom)
            }
            val movedy: Int
            if (dy > 0) {
                movedy = ((MAX_HEADER_PULL_HEIGHT - contentView!!.top).toFloat() / (MAX_HEADER_PULL_HEIGHT.toFloat() * dy / MOVE_PARA)).toInt()
            } else {
                movedy = (((MAX_FOOTER_PULL_HEIGHT - (height - contentView!!.bottom)) / MAX_FOOTER_PULL_HEIGHT.toFloat()).toFloat() * dy / MOVE_PARA).toInt()
            }
            var top: Int = contentView!!.top + movedy
            contentView!!.layout(contentView!!.left, top, contentView!!.right, top + contentView!!.measuredHeight)
        } else if (type == Type.FOLLOW) {
            //根据下拉高度计算位移距离，（越拉越慢）
            val movedx: Int
            if (dy > 0) {
                movedx = (((MAX_HEADER_PULL_HEIGHT + scrollY) / MAX_HEADER_PULL_HEIGHT.toFloat()).toFloat() * dy / MOVE_PARA).toInt()
            } else {
                movedx = (((MAX_FOOTER_PULL_HEIGHT - scrollY) / MAX_FOOTER_PULL_HEIGHT.toFloat()).toFloat() * dy / MOVE_PARA).toInt()
            }
            scrollBy(0, (-movedx).toInt())
        }

    }

    private fun callOnDropAnim() {
        if (type == Type.OVERLAP) {
            if (contentView!!.top > 0) {
                if (headerHandler != null) {
                    headerHandler!!.onDropAnim(header!!, contentView!!.top)
                }
            } else if (contentView!!.top < 0) {
                if (footerHandler != null) {
                    footerHandler!!.onDropAnim(footer!!, contentView!!.top)
                }
            }
        } else if (type == Type.FOLLOW) {
            if (scrollY < 0) {
                if (headerHandler != null) {
                    headerHandler!!.onDropAnim(header!!, -scrollY)
                }
            } else if (scrollY > 0) {
                if (footerHandler != null) {
                    footerHandler!!.onDropAnim(footer!!, -scrollY)
                }
            }
        }
    }

    private fun callOnPreDrag() {
        if (_firstDrag) {
            if (isTop()) {
                if (headerHandler != null) {
                    headerHandler!!.onPreDrag(header!!)
                }
                _firstDrag = false
            } else if (isBottom()) {
                if (footerHandler != null) {
                    footerHandler!!.onPreDrag(footer!!)
                }
                _firstDrag = false
            }
        }
    }

    private fun callOnLimitDes() {
        var topOrBottom = false
        if (type == Type.OVERLAP) {
            topOrBottom = contentView!!.top >= 0 && isChildScrollToTop()
        } else if (type == Type.FOLLOW) {
            topOrBottom = scrollY <= 0 && isChildScrollToTop()
        }
        if (isFirst) {
            if (topOrBottom) {
                isCallUp = true
                isCallDown = false
            } else {
                isCallUp = false
                isCallDown = true
            }
        }
        if (dy == 0F) {
            return
        }
        var upOrDown: Boolean = dy < 0
        if (topOrBottom) {
            if (!upOrDown) {
                if (isTopOverFarm() && !isCallDown) {
                    isCallDown = true
                    headerHandler!!.onLimitDes(header!!, upOrDown)
                }
                isCallUp = false
            } else {
                if (!isTopOverFarm() && !isCallUp) {
                    isCallUp = true
                    headerHandler!!.onLimitDes(header!!, upOrDown)
                }
                isCallDown = false
            }
        } else {
            if (upOrDown) {
                if (isBottomOverFarm() && !isCallUp) {
                    isCallUp = true
                    if (footerHandler != null) {
                        footerHandler!!.onLimitDes(footer!!, upOrDown)
                    }
                    isCallDown = false
                }
            } else {
                if (!isBottomOverFarm() && !isCallDown) {
                    isCallDown = true
                    if (footerHandler != null) {
                        footerHandler!!.onLimitDes(footer!!, upOrDown)
                    }
                    isCallUp = false
                }
            }
        }
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.currY)
            invalidate()
        }

        if (!isMoveNow && type == Type.FOLLOW && mScroller.isFinished) {
            if (isFullAnim) {
                if (!hasCallFull) {
                    hasCallFull = true
                    callOnAfterFullAnim()
                }
            } else {
                if (!hasCallRefresh) {
                    hasCallRefresh = true
                    callOnAfterRefreshAnim()
                }
            }
        }
    }

    private fun callOnAfterFullAnim() {
        if (callFreshOrLoad != 0) {
            callOnFinishAnim()
        }
        if (needChangeHeader) {
            needChangeHeader = false
            setHeaderIn(_headerHandler)
        }
        if (needChangeFooter) {
            needChangeFooter = false
            setFooterIn(_footerHandler)
        }
        //动画完成后检查是否需要切换type，是则切换
        if (needChange) {
            changeType(_type)
        }
    }


    private fun callOnAfterRefreshAnim() {
        if (type == Type.FOLLOW) {
            if (isTop()) {
                listener.onRefresh()
            } else if (isBottom()) {
                listener.onLoadMore()
            }
        } else if (type == Type.OVERLAP) {
            if (!isMoveNow) {
                val nowTime = System.currentTimeMillis()
                if (nowTime - lastMoveTime >= MOVE_TIME_OVER) {
                    if (callFreshOrLoad == 1) {
                        listener.onRefresh()
                    }
                    if (callFreshOrLoad == 2) {
                        listener.onLoadMore()
                    }
                }
            }
        }
    }

    private fun isTop(): Boolean {
        if (type == Type.OVERLAP) {
            return contentView!!.top > 0 && isChildScrollToTop()
        } else if (type == Type.FOLLOW) {
            return scrollY < 0 && isChildScrollToTop()
        }
        return false
    }

    private fun isBottom(): Boolean {
        if (type == Type.OVERLAP) {
            return contentView!!.top < 0 && isChildScrollToBottom()
        } else if (type == Type.FOLLOW) {
            return scrollY > 0 && isChildScrollToBottom()
        }
        return false
    }

    private fun isFlow(): Boolean {
        return when (type) {
            Type.OVERLAP -> contentView!!.top < 30 && contentView!!.top > -30
            Type.FOLLOW -> scrollY > -30 && scrollY < 30
            else -> false
        }
    }

    private fun isNeedMuMove(): Boolean {
        if (contentView == null) {
            return false
        }
        if (Math.abs(dy) < Math.abs(dx)) {
            return false
        }

        var isTop = isChildScrollToTop()
        var isBottom = isChildScrollToBottomFull(isFullEnable)
        if (type == Type.OVERLAP) {
            if (header != null) {
                if (isTop && dy > 0 || contentView!!.top > 0 + 20) {
                    return true
                }
            }

            if (footer != null) {
                if (isBottom && dy < 0 || contentView!!.bottom < mRect.bottom - 20) {
                    return true
                }
            }
        } else if (type == Type.FOLLOW) {
            if (header != null) {
                //其中的20是一个防止触摸误差的偏移量
                if (isTop && dy > 0 || scrollY < 0 - 20) {
                    return true
                }
            }
            if (footer != null) {
                if (isBottom && dy < 0 || scrollY > 0 + 20) {
                    return true
                }
            }
        }
        return false
    }

    private fun isChildScrollToBottomFull(fullEnable: Boolean): Boolean {
        return !ViewCompat.canScrollVertically(contentView, 1)
    }

    private fun isChildScrollToBottom(): Boolean {
        return isChildScrollToBottomFull(true)
    }

    private fun isChildScrollToTop(): Boolean {
        return ViewCompat.canScrollVertically(contentView, 1)
    }

    private fun dealMulTouchEvent(ev: MotionEvent?) = try {
        var action = MotionEventCompat.getActionMasked(ev)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val pointerIndex = MotionEventCompat.getActionIndex(ev)
                val x = MotionEventCompat.getX(ev, pointerIndex)
                val y = MotionEventCompat.getY(ev, pointerIndex)
                mLastX = x
                mLastY = y
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                println("初始化：ACTION_DOWN--" + "mActivePointerId=" + mActivePointerId)

            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId != MotionEvent.INVALID_POINTER_ID) {
                    val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                    val x = MotionEventCompat.getX(ev, pointerIndex)
                    val y = MotionEventCompat.getY(ev, pointerIndex)
                    dx = x - mLastX
                    dy = y - mLastY
                    mLastY = y
                    mLastX = x
                } else {
                    println("异常--ACTION_MOVE--mActivePointerId==" + mActivePointerId)
                }

            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = MotionEvent.INVALID_POINTER_ID
                println("初始化：ACTION_UP or ACTION_CANCEL--" + "mActivePointerId=" + mActivePointerId)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerIndex = MotionEventCompat.getActionIndex(ev)
                val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
                if (pointerId != mActivePointerId) {
                    mLastX = MotionEventCompat.getX(ev, pointerIndex)
                    mLastY = MotionEventCompat.getY(ev, pointerIndex)
                    mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
                    println("初始化：ACTION_POINTER_DOWN--" + "mActivePointerId=" + mActivePointerId)

                } else {

                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = MotionEventCompat.getActionIndex(ev)
                val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastX = MotionEventCompat.getX(ev, newPointerIndex)
                    mLastY = MotionEventCompat.getY(ev, newPointerIndex)
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
                    println("初始化：ACTION_POINTER_UP--" + "mActivePointerId=" + mActivePointerId)

                } else {

                }
            }
            else -> {
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    /**
     * 判断顶部拉动是否超过临界值
     */
    private fun isTopOverFarm(): Boolean {
        return when (type) {
            Type.OVERLAP -> contentView!!.top > HEADER_LIMIT_HEIGHT
            Type.FOLLOW -> -scrollY > HEADER_LIMIT_HEIGHT
            else -> false
        }
    }

    /**
     * 判断底部拉动是否超过临界值
     */
    private fun isBottomOverFarm(): Boolean {
        return if (type == Type.OVERLAP) {
            height - contentView!!.bottom > FOOTER_LIMIT_HEIGHT
        } else if (type == Type.FOLLOW) {
            scrollY > FOOTER_LIMIT_HEIGHT
        } else {
            false
        }
    }

    /**
     * 重置控件位置到初始状态
     */
    private fun resetPosition() {
        isFullAnim = true
        isInControl = false    //重置位置的时候，滑动事件已经不在控件的控制中了
        if (type == Type.OVERLAP) {
            if (mRect.bottom == 0 || mRect.right == 0) {
                return
            }
            //根据下拉高度计算弹回时间，时间最小100，最大400
            var time = 0
            if (contentView?.height!! > 0) {
                time = Math.abs(400 * contentView!!.top / contentView!!.height)
            }
            if (time < 100) {
                time = 100
            }

            val animation = TranslateAnimation(0f, 0f, contentView!!.top.toFloat(), mRect.top.toFloat())
            animation.duration = time.toLong()
            animation.fillAfter = true
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    callOnAfterFullAnim()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            contentView!!.startAnimation(animation)
            contentView!!.layout(mRect.left, mRect.top, mRect.right, mRect.bottom)
        } else if (type == Type.FOLLOW) {
            mScroller.startScroll(0, scrollY, 0, -scrollY, MOVE_TIME)
            invalidate()
        }
        //mRect.setEmpty();
    }

    /**
     * 智能判断是重置控件位置到初始状态还是到刷新/加载状态
     */
    private fun restSmartPosition() {
        if (listener == null) {
            resetPosition()
        } else {
            if (isTopOverFarm()) {
                callFreshOrLoad()
                if (give == Give.BOTH || give == Give.TOP) {
                    resetRefreshPosition()
                } else {
                    resetPosition()
                }
            } else if (isBottomOverFarm()) {
                callFreshOrLoad()
                if (give == Give.BOTH || give == Give.BOTTOM) {
                    resetRefreshPosition()
                } else {
                    resetPosition()
                }
            } else {
                resetPosition()
            }
        }
    }

    /**
     * 重置控件位置到刷新状态（或加载状态）
     */
    private fun resetRefreshPosition() {
        isFullAnim = false
        isInControl = false    //重置位置的时候，滑动事件已经不在控件的控制中了
        if (type == Type.OVERLAP) {
            if (mRect.bottom == 0 || mRect.right == 0) {
                return
            }
            if (contentView!!.top > mRect.top) {    //下拉
                val animation = TranslateAnimation(0f, 0f, (contentView!!.top - HEADER_SPRING_HEIGHT).toFloat(), mRect.top.toFloat())
                animation.duration = MOVE_TIME_OVER.toLong()
                animation.fillAfter = true
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        callOnAfterRefreshAnim()
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
                contentView!!.startAnimation(animation)
                contentView!!.layout(mRect.left, mRect.top + HEADER_SPRING_HEIGHT, mRect.right, mRect.bottom + HEADER_SPRING_HEIGHT)
            } else {     //上拉
                val animation = TranslateAnimation(0f, 0f, (contentView!!.top + FOOTER_SPRING_HEIGHT).toFloat(), mRect.top.toFloat())
                animation.duration = MOVE_TIME_OVER.toLong()
                animation.fillAfter = true
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        callOnAfterRefreshAnim()
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
                contentView!!.startAnimation(animation)
                contentView!!.layout(mRect.left, mRect.top - FOOTER_SPRING_HEIGHT, mRect.right, mRect.bottom - FOOTER_SPRING_HEIGHT)
            }
        } else if (type == Type.FOLLOW) {
            if (scrollY < 0) {     //下拉
                mScroller.startScroll(0, scrollY, 0, -scrollY - HEADER_SPRING_HEIGHT, MOVE_TIME)
                invalidate()
            } else {       //上拉
                mScroller.startScroll(0, scrollY, 0, -scrollY + FOOTER_SPRING_HEIGHT, MOVE_TIME)
                invalidate()
            }
        }
    }

    private fun callFreshOrLoad() {
        if (isTop()) {  //下拉
            callFreshOrLoad = 1
            if (type == Type.OVERLAP) {
                if (dsY > 200 || HEADER_LIMIT_HEIGHT >= HEADER_SPRING_HEIGHT) {
                    if (headerHandler != null) {
                        headerHandler!!.onStartAnim()
                    }
                }
            } else if (type == Type.FOLLOW) {
                if (headerHandler != null) {
                    headerHandler!!.onStartAnim()
                }
            }
        } else if (isBottom()) {
            callFreshOrLoad = 2
            if (type == Type.OVERLAP) {
                if (dsY < -200 || FOOTER_LIMIT_HEIGHT >= FOOTER_SPRING_HEIGHT) {
                    if (footerHandler != null) {
                        footerHandler!!.onStartAnim()
                    }
                }
            } else if (type == Type.FOLLOW) {
                if (footerHandler != null) {
                    footerHandler!!.onStartAnim()
                }
            }
        }
    }

    private fun callOnFinishAnim() {
        if (callFreshOrLoad != 0) {
            if (callFreshOrLoad == 1) {
                if (headerHandler != null) {
                    headerHandler!!.onFinishAnim()
                }
                if (give == Give.BOTTOM || give == Give.NONE) {
                    listener.onRefresh()
                }
            } else if (callFreshOrLoad == 2) {
                if (footerHandler != null) {
                    footerHandler!!.onFinishAnim()
                }
                if (give == Give.TOP || give == Give.NONE) {
                    listener.onLoadMore()
                }
            }
            callFreshOrLoad = 0
        }
    }

    /**
     * 切换Type的方法，之所以不暴露在外部，是防止用户在拖动过程中调用造成布局错乱
     * 所以在外部方法中设置标志，然后在拖动完毕后判断是否需要调用，是则调用
     */
    private fun changeType(type: Type?) {
        if (type == null) {
            return
        }
        this.type = type
        if (header != null && header!!.visibility != View.INVISIBLE) {
            header!!.visibility = View.INVISIBLE
        }
        if (footer != null && footer!!.visibility != View.INVISIBLE) {
            footer!!.visibility = View.INVISIBLE
        }
        requestLayout()
        needChange = false
    }

    private fun setHeaderIn(headerHandler: DragHandler?) {
        this.headerHandler = headerHandler
        if (header != null) {
            removeView(this.header)
        }
        headerHandler!!.getView(inflater, this)
        this.header = getChildAt(childCount - 1)
        if (contentView == null) {
            throw RuntimeException("Your contentView is null and you can setHeader before setContentView")
        }
        contentView!!.bringToFront() //把内容放在最前端
        requestLayout()
    }

    private fun setFooterIn(footerHandler: DragHandler?) {
        this.footerHandler = footerHandler
        if (footer != null) {
            removeView(footer)
        }
        footerHandler!!.getView(inflater, this)
        this.footer = getChildAt(childCount - 1)
        if (contentView == null) {
            throw RuntimeException("Your contentView is null and you can setFooter before setContentView")
        }
        contentView!!.bringToFront() //把内容放在最前端
        requestLayout()
    }

    fun setHeader(headerHandler: DragHandler) {
        if (this.headerHandler != null && isTop()) {
            needChangeHeader = true
            _headerHandler = headerHandler
            resetPosition()
        } else {
            setHeaderIn(headerHandler)
        }
    }

    fun setFooter(footerHandler: DragHandler) {
        if (this.footerHandler != null && isBottom()) {
            needChangeFooter = true
            _footerHandler = footerHandler
            resetPosition()
        } else {
            setFooterIn(footerHandler)
        }
    }


    /**
     * 设置监听
     */
    fun setListener(listener: OnFreshListener) {
        this.listener = listener
    }

    /**
     * 重置控件位置，暴露给外部的方法，用于在刷新或者加载完成后调用
     */
    fun onFinishFreshAndLoad() {
        if (headerHandler != null) {//结束动画
            headerHandler!!.onFinishAnim()
        }
        handler.postDelayed(Runnable {
            if (!isMoveNow && needResetAnim) {
                val needTop = isTop() && (give == Give.TOP || give == Give.BOTH)
                val needBottom = isBottom() && (give == Give.BOTTOM || give == Give.BOTH)
                if (needTop || needBottom) {
                    if (contentView is ListView) {
                    }
                    resetPosition()
                }
            }
            if (headerHandler != null) {//结束刷新
                headerHandler!!.onFinishRefresh()
            }
            if (header != null && header!!.visibility != View.INVISIBLE) {
                header!!.visibility = View.INVISIBLE
            }
            if (footer != null && footer!!.visibility != View.INVISIBLE) {
                footer!!.visibility = View.INVISIBLE
            }
        }, 300)
    }


}