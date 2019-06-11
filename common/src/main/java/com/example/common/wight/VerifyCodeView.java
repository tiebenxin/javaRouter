package com.example.common.wight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.example.common.utils.DensityUtil;

import java.security.SecureRandom;

/**
 * Created by LL130386 on 2019/6/4.
 */

public class VerifyCodeView extends View {
    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
            'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    private int textCount = 4;
    private int lineCount = 3;
    private float textSize = DensityUtil.dip2px(getContext(), 20);
    private String code;
    //绘制时控制文本绘制的范围
    private Rect mBound;
    private Paint mPaint;
    private SecureRandom random;
    private int width;
    private int height;
    private int[] textColors = new int[textCount];//字体颜色
    private boolean[] textBolds = new boolean[textCount];//字体是否粗体
    private float[] textSkews = new float[textCount];//字体是否斜体
    private int[] lineColors = new int[lineCount];//干扰线颜色
    private int[][] linePositions = new int[lineCount][4];//干扰线位置
    private int[][] textPositions = new int[textCount][2];//干扰线位置
    boolean isFirst = true;
    boolean isRefresh = false;


    public VerifyCodeView(Context context) {
        this(context, null);
    }

    public VerifyCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        random = new SecureRandom();

        code = createCode();

        /**
         * 获得绘制文本的宽和高
         */

        mPaint = new Paint();
        mPaint.setTextSize(textSize);
        mBound = new Rect();
        mPaint.getTextBounds(code, 0, code.length(), mBound);
    }


    private String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < textCount; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = 0;
        height = 0;

        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:// 明确指定了
                width = getPaddingLeft() + getPaddingRight() + specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                width = getPaddingLeft() + getPaddingRight() + mBound.width();
                break;
        }

        /**
         * 设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:// 明确指定了
                height = getPaddingTop() + getPaddingBottom() + specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                height = getPaddingTop() + getPaddingBottom() + mBound.height();
                break;
        }
        setMeasuredDimension(width, height);
    }

    //随机生成文字样式，颜色，粗细，倾斜度
    private void randomTextStyle(Paint paint, int position, boolean canRandom) {
        int color;
        boolean isFakeBold;
        float skewX;
        if (canRandom) {
            color = randomColor(1);
            textColors[position] = color;
            isFakeBold = random.nextBoolean();
            textBolds[position] = isFakeBold;
            skewX = random.nextFloat();
            skewX = random.nextBoolean() ? skewX : -skewX;
            textSkews[position] = skewX;
        } else {
            if (textColors != null) {
                color = textColors[position];
            } else {
                color = randomColor(1);
                textColors[position] = color;
            }

            if (textBolds != null) {
                isFakeBold = textBolds[position];
            } else {
                isFakeBold = random.nextBoolean();
                textBolds[position] = isFakeBold;
            }

            if (textSkews != null) {
                skewX = textSkews[position];
            } else {
                skewX = random.nextFloat();
                skewX = random.nextBoolean() ? skewX : -skewX;
                textSkews[position] = skewX;
            }
        }
        paint.setColor(color);
        paint.setFakeBoldText(isFakeBold);  //true为粗体，false为非粗体
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，正数左斜
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.parseColor("#c5c8cb"));
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        if (!isFirst && !isRefresh) {
            int tenDp = 0;
            int dx = tenDp;
            int dy = 0;
            if (textPositions == null) {
                tenDp = DensityUtil.dip2px(getContext(), 10);
                dx = tenDp;
            }
            for (int i = 0; i < textCount; i++) {
                randomTextStyle(mPaint, i, false);
                if (textPositions != null) {
                    dx = textPositions[i][0];
                    dy = textPositions[i][1];
                } else {
                    dy = getDrawY(height, DensityUtil.dip2px(getContext(), 5), (int) textSize);
                }
//                System.out.println("不随机--" + "X=" + dx + "--Y=" + dy);
                canvas.drawText(code.charAt(i) + "", dx, dy, mPaint);
                if (textPositions != null) {
                    dx += (width - tenDp * 2) / textCount;
                }
            }
            for (int i = 0; i < lineCount; i++) {
                drawLine(canvas, mPaint, i, false);
            }
        } else {
            int tenDp = DensityUtil.dip2px(getContext(), 10);
            int dx = tenDp;
            for (int i = 0; i < textCount; i++) {
                randomTextStyle(mPaint, i, true);
                int dy = getDrawY(height, DensityUtil.dip2px(getContext(), 5), (int) textSize);
//                System.out.println("随机--" + "X=" + dx + "--Y=" + dy);
                textPositions[i][0] = dx;
                textPositions[i][1] = dy;
                canvas.drawText(code.charAt(i) + "", dx, dy, mPaint);
                dx += (width - tenDp * 2) / textCount;
            }
            for (int i = 0; i < lineCount; i++) {
                drawLine(canvas, mPaint, i, true);
            }
            if (isRefresh) {
                isRefresh = false;
            }
        }
        isFirst = false;
    }

    private int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }

    private void drawLine(Canvas canvas, Paint paint, int position, boolean canRandom) {
        int color, startX, startY, stopX, stopY;
        if (canRandom) {
            color = randomColor(1);
            lineColors[position] = color;
            startX = random.nextInt(width);
            startY = random.nextInt(height);
            stopX = random.nextInt(width);
            stopY = random.nextInt(height);
            linePositions[position][0] = startX;
            linePositions[position][1] = startY;
            linePositions[position][2] = stopX;
            linePositions[position][3] = stopY;
        } else {
            if (lineColors != null) {
                color = lineColors[position];
            } else {
                color = randomColor(1);
                lineColors[position] = color;

            }
            if (linePositions != null) {
                startX = linePositions[position][0];
                startY = linePositions[position][1];
                stopX = linePositions[position][2];
                stopY = linePositions[position][3];
            } else {
                startX = random.nextInt(width);
                startY = random.nextInt(height);
                stopX = random.nextInt(width);
                stopY = random.nextInt(height);
                linePositions[position][0] = startX;
                linePositions[position][1] = startY;
                linePositions[position][2] = stopX;
                linePositions[position][3] = stopY;
            }
        }
        paint.setStrokeWidth(3);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private int getDrawY(int height, int offsetY, int textHeight) {
        double tempY = Math.random() * (height - textHeight - offsetY * 2) + textHeight;
        return (int) tempY;
    }

    public String getCode() {
        if (TextUtils.isEmpty(code))
            return "";
        return code;
    }

    public void setRefresh(boolean b) {
        isRefresh = b;
    }

    public void refresh() {
        init();
        invalidate();
    }

}
