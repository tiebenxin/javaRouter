package com.example.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by LL130386 on 2019/6/3.
 */

public class RandomVerifyCode {
    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
            'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private Context mContext;
    private ImageView imgContainer;
    private int textCount = 4;
    private int lineCount = 3;
    private float textSize = DensityUtil.dip2px(ContextHelper.getContext(), 20);
    private int width = 100, height = 40;
    private String code;

    private Random random = new Random();
    private Paint paint = new Paint();
    private Rect mBound;

    public RandomVerifyCode(Context context, @NonNull ImageView imgContainer, int textCount, int lineCount, float textSizeDp) {
        this.mContext = context;
        this.textCount = textCount;
        this.lineCount = lineCount;
        this.textSize = DensityUtil.dip2px(mContext, textSizeDp);
        this.imgContainer = imgContainer;
        imgContainer.setOnClickListener(v -> {
            createCodeImage();
        });
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        if (mBound == null) {
            mBound = new Rect();
        }
        code = createCode();
        paint.getTextBounds(code, 0, code.length(), mBound);
        createCodeImage();//初始化code
    }

    public void createCodeImage() {
        imgContainer.post(() -> {
            width = imgContainer.getMeasuredWidth();
            height = imgContainer.getMeasuredHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            //画背景
            paint.setColor(Color.parseColor("#c5c8cb"));
            canvas.drawRect(0, 0, width, height, paint);

            //字体
            int tenDp = DensityUtil.dip2px(mContext, 10);
            int dx = tenDp;
            for (int i = 0; i < textCount; i++) {
                randomTextStyle(paint);
                canvas.drawText(code.charAt(i) + "", dx, getDrawY(height, DensityUtil.dip2px(mContext, 5), (int) textSize), paint);
                dx += (width - tenDp * 2) / textCount;
            }

            //干扰线
            for (int i = 0; i < lineCount; i++) {
                drawLine(canvas, paint);
            }
            canvas.save();
            canvas.restore();
            imgContainer.setImageBitmap(bitmap);
        });
    }

    public String getCode() {
        if (TextUtils.isEmpty(code))
            return "";
        return code;
    }


    //随机生成文字样式，颜色，粗细，倾斜度
    private void randomTextStyle(Paint paint) {
//        int color = Color.rgb(255, 255, 255);
        int color = randomColor(1);
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean());  //true为粗体，false为非粗体
        float skewX = random.nextFloat();
        skewX = random.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，正数左斜
    }

    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor(1);
        int startX = random.nextInt(width);
        int startY = random.nextInt(height);
        int stopX = random.nextInt(width);
        int stopY = random.nextInt(height);
        paint.setStrokeWidth(3);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }

    private String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < textCount; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }

    private int getDrawY(int height, int offsetY, int textHeight) {
        double tempY = Math.random() * (height - textHeight - offsetY * 2) + textHeight;
        return (int) tempY;
    }

    public void refresh() {
        code = createCode();
        createCodeImage();
    }

}
