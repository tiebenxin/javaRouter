package com.example.common.wight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.common.utils.DensityUtil
import java.security.SecureRandom

/**
 * Created by Liszt on 2019/6/5.
 *
 */
class VerifyKTView : View {
    private val CHARS: CharArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
            'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')

    private var textCount: Int = 4
    private var lineCount: Int = 3
    private var textSize: Int = DensityUtil.dip2px(context, 20F)

    //lateinit只能修饰能赋值为null的数据，不能赋值为null的数据如int，boolean,不能用此修饰
    private lateinit var code: String
    private lateinit var mBound: Rect
    private lateinit var mPaint: Paint
    private lateinit var random: SecureRandom
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    //一维数组
    private var textColors = arrayOfNulls<Int>(textCount)
    private var textBolds = arrayOfNulls<Boolean>(textCount)
    private var textSkews = arrayOfNulls<Float>(textCount)
    private var lineColors = arrayOfNulls<Int>(lineCount)
    //定义二维数组，两种都可以 it->0 表示int类型， it->"" 表示String类型
    //    private var linePosition = Array(lineCount, { Array(4, { it -> 0 }) })
    private var linePositions = Array(lineCount, { arrayOfNulls<Int>(4) })
    private var textPositions = Array(textCount, { arrayOfNulls<Int>(2) })
    private var isFirst: Boolean = true
    private var isRefresh: Boolean = false


    //两种构造函数都可以
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }


    /*  constructor(context: Context) : super(context) {
          initView()
      }

      constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
          initView()
      }

      constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
          initView()
      }*/


    private fun initView() {
        random = SecureRandom()
        code = createCode()
        mPaint = Paint()
        mPaint.textSize = textSize.toFloat()
        mBound = Rect()
        mPaint.getTextBounds(code, 0, code.length, mBound)


    }

    @SuppressLint("SwitchIntDef")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //设置宽度
        var specMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        var specSize: Int = MeasureSpec.getSize(widthMeasureSpec)
        when (specMode) {
            MeasureSpec.EXACTLY -> mWidth = paddingLeft + paddingRight + specSize
            MeasureSpec.AT_MOST -> mWidth = paddingLeft + paddingRight + mBound.width()
            MeasureSpec.UNSPECIFIED -> mWidth = paddingLeft + paddingRight + mBound.width()
        }

        //设置高度
        var specModeHeight: Int = MeasureSpec.getMode(heightMeasureSpec)
        var specSizeHeight: Int = MeasureSpec.getSize(heightMeasureSpec)
        when (specModeHeight) {
            MeasureSpec.EXACTLY -> mHeight = paddingBottom + paddingTop + specSizeHeight
            MeasureSpec.AT_MOST -> mHeight = paddingBottom + paddingTop + mBound.height()
            MeasureSpec.UNSPECIFIED -> mHeight = paddingBottom + paddingTop + mBound.height()
        }
        setMeasuredDimension(mWidth, mHeight)


    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
        mPaint.color = Color.parseColor("#c5c8cb")
        println("背景颜色=" + mPaint.color)
        canvas.drawRect(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat(), mPaint)
        if (!isFirst && !isRefresh) {
            var tenDp = 0
            var dx: Int = tenDp
            var dy = 0
//            if (false) {
            tenDp = DensityUtil.dip2px(context, 10F)
            dx = tenDp
//            }

            for (i in 0 until textCount) {
                randomTextStyle(mPaint, i, false)
//                if (textPositions != null) {
                dx = textPositions[i][0] as Int
                dy = textPositions[i][1] as Int
//                } else {
//                    dy = getDrawY(height, DensityUtil.dip2px(context, 5F), textSize)
//                }
                dy = getDrawY(height, DensityUtil.dip2px(context, 5F), textSize)

//                System.out.println("不随机--" + "X=" + dx + "--Y=" + dy);
                canvas.drawText(code[i] + "", dx.toFloat(), dy.toFloat(), mPaint)
                if (textPositions != null) {
                    dx += (width - tenDp * 2) / textCount
                }
            }

            for (i in 0 until lineCount) {
                drawLine(canvas, mPaint, i, false)
            }

        } else {
            val tenDp = DensityUtil.dip2px(context, 10F)
            var dx = tenDp
//            var arr: CharArray = code?.toCharArray()
            for (i: Int in 0 until textCount) {
                randomTextStyle(mPaint, i, true)
                val dy = getDrawY(height, DensityUtil.dip2px(context, 5F), textSize)
                //                System.out.println("随机--" + "X=" + dx + "--Y=" + dy);
                println("文字位置：" + i + "--文字=" + code[i] + "--size=" + mPaint.textSize)
                textPositions[i][0] = dx
                textPositions[i][1] = dy
                canvas.drawText(code[i] + "", dx.toFloat(), dy.toFloat(), mPaint)
                dx += (width - tenDp * 2) / textCount
            }
            for (i in 0 until lineCount) {
                drawLine(canvas, mPaint, i, true)
            }
            if (isRefresh) {
                isRefresh = false
            }
        }
        isFirst = false
    }

    private fun createCode(): String {
        var buffer = StringBuffer()
        //until
        for (i in 0 until textCount) {
            buffer.append(CHARS[random.nextInt(CHARS.size)])
        }
        return buffer.toString()
    }

    private fun randomTextStyle(paint: Paint, position: Int, canRandom: Boolean) {
        var color: Int
        var isFakeBold: Boolean
        var skewX: Float
        if (canRandom) {
            color = randomColor(1)
            textColors[position] = color
            isFakeBold = random.nextBoolean()
            textBolds[position] = isFakeBold
            skewX = random.nextFloat()
            skewX = if (random.nextBoolean()) {
                skewX
            } else {
                -skewX
            }
            textSkews[position] = skewX
        } else {
//            if (textColors != null) {
            color = textColors[position] as Int
//            } else {
//                color = randomColor(1)
//                textColors[position] = color
//            }

//            if (textBolds != null) {
            isFakeBold = textBolds[position] as Boolean
//            } else {
//                isFakeBold = random.nextBoolean()
//                textBolds[position] = isFakeBold
//            }

//            if (textSkews != null) {
            skewX = textSkews[position] as Float
//            } else {
//                skewX = random.nextFloat()
//                skewX = if (random.nextBoolean()) {
//                    skewX
//                } else {
//                    -skewX
//                }
//                textSkews[position] = skewX
//            }
        }
        println("文字颜色=" + color)
        paint.color = color
        paint.isFakeBoldText = isFakeBold
        paint.textSkewX = skewX
    }

    private fun randomColor(rate: Int): Int {
        var red: Int = random.nextInt(256) / rate
        var green: Int = random.nextInt(256) / rate
        var blue: Int = random.nextInt(256) / rate
        return Color.rgb(red, green, blue)
    }

    private fun getDrawY(height: Int, offsetY: Int, testHeight: Int): Int {
        var tempY: Double = Math.random() * (height - testHeight - offsetY * 2) + testHeight
        return tempY.toInt()
    }

    public fun refresh() {
        initView()
        invalidate()
    }

    private fun drawLine(canvas: Canvas, paint: Paint, position: Int, canRandom: Boolean) {
        var color: Int
        var startX: Int
        var startY: Int
        var stopX: Int
        var stopY: Int
        if (canRandom) {
            color = randomColor(1)
            lineColors[position] = color
//            println("宽度=" + mWidth + "--高度=" + mHeight)
            startX = random.nextInt(mWidth)
            startY = random.nextInt(mHeight)
            stopX = random.nextInt(mWidth)
            stopY = random.nextInt(mHeight)
            linePositions[position][0] = startX
            linePositions[position][1] = startY
            linePositions[position][2] = stopX
            linePositions[position][3] = stopY
        } else {
//            if (lineColors != null) {
            color = lineColors[position] as Int
//            } else {
//                color = randomColor(1)
//                lineColors[position] = color
//
//            }
//            if (linePositions != null) {
            startX = linePositions[position][0] as Int
            startY = linePositions[position][1] as Int
            stopX = linePositions[position][2] as Int
            stopY = linePositions[position][3] as Int
//            } else {
//                startX = random.nextInt(mWidth)
//                startY = random.nextInt(mHeight)
//                stopX = random.nextInt(mWidth)
//                stopY = random.nextInt(mHeight)
//                linePositions[position][0] = startX
//                linePositions[position][1] = startY
//                linePositions[position][2] = stopX
//                linePositions[position][3] = stopY
//            }
        }
        paint.strokeWidth = 3F
        paint.color = color
        canvas.drawLine(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), paint)
    }

    public fun setRefresh(boolean: Boolean) {
        isRefresh = boolean
    }

}