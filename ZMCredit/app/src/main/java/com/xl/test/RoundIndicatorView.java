package com.xl.test;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by hushendian on 2017/12/28.
 */

public class RoundIndicatorView extends View {
    private Context context;
    private int maxNum;
    private int startAngle;
    private int sweepAngle;
    private int sweepInWidth;//内圆的宽度
    private int sweepOutWidth;//外圆的宽度
    private Paint paint;//画内圆、外圆、刻度、刻度值、刻度文字
    private Paint paint_2;// 圆弧
    private Paint paint_3;//小圆点
    private Paint paint_4;//信用分数+信用评级
    private int mWidth;//view宽
    private int mHeight;//view 高
    private int radius;//圆弧半径
    private int currentNum = 0;//需设置setter、getter 供属性动画使用
    private int[] indicatorColor = {0xffffffff, 0x00ffffff, 0x99ffffff, 0xffffffff};

    public RoundIndicatorView(Context context) {
        this(context, null);
    }

    public RoundIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setBackgroundColor(0xFFFF6347);
        initAttr(attrs);
        initPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        if (wMode == MeasureSpec.EXACTLY) {
            mWidth = wSize;
        } else {
            mWidth = dp2px(300);
        }
        if (hMode == MeasureSpec.EXACTLY) {
            mHeight = hSize;
        } else {
            mHeight = dp2px(400);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radius = getMeasuredWidth() / 4;
        canvas.save();
        //画布平移,对于计算坐标点比较方便，简单
        canvas.translate(mWidth / 2, mWidth / 2);
        drawRound(canvas);
        drawScale(canvas);
        drawIndicator(canvas);
        drawCenterText(canvas);
    }

    private void drawRound(Canvas canvas) {
        //内圆
        canvas.save();
        paint.setAlpha(0x40);
        paint.setStrokeWidth(sweepInWidth);
        Log.d("RoundIndicatorView", "drawRound: " + radius);
        RectF rectF = new RectF(-radius, -radius, radius, radius);
        //测试感觉坐标（0，0）点位于屏幕的中心
//        RectF rectF1 = new RectF(0, 0, radius, radius);
//        RectF rectF3 = new RectF(-radius-sweepInWidth/2, -radius-sweepInWidth/2,
// radius+sweepInWidth/2, radius+sweepInWidth/2);
//        canvas.drawRect(rectF3,paint);
//        canvas.drawRect(rectF1,paint);

        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);

//        canvas.drawRect(rectF, paint);
        //外圆
        paint.setStrokeWidth(sweepOutWidth);
        int w = dp2px(10);
        RectF rectF2 = new RectF(-radius - w, -radius - w, radius + w, radius + w);
        Log.d("RoundIndicatorView", "drawRound: " + startAngle + "------" + sweepAngle);
        canvas.drawArc(rectF2, startAngle, sweepAngle, false, paint);
        canvas.restore();
    }

    private String[] text = {"较差", "中等", "良好", "优秀", "极好"};

    private void drawScale(Canvas canvas) {
        //画刻度
        canvas.save();
        float angle = (float) sweepAngle / 30;
        Log.d("RoundIndicatorView", "drawScale: " + (-270 + startAngle));
        canvas.rotate(-270 + startAngle);//扭转画布来实现画线
        for (int i = 0; i <= 30; i++) {
            if (i % 6 == 0) {
                paint.setStrokeWidth(dp2px(2));
                paint.setAlpha(0x70);
                //没想明白，不知道为啥是线宽的1/2，如果是线宽，则线出去了
                canvas.drawLine(0, -radius - sweepInWidth / 2, 0, -radius + sweepInWidth / 2
                        , paint);
                drawText(canvas, i * maxNum / 30 + "", paint);
            } else {
                paint.setStrokeWidth(dp2px(1));
                paint.setAlpha(0x50);
                canvas.drawLine(0, -radius - sweepInWidth / 2, 0, -radius + sweepInWidth / 2,
                        paint);
            }
            if (i == 3 || i == 9 || i == 15 || i == 21 || i == 27) {
                paint.setStrokeWidth(dp2px(2));
                paint.setAlpha(0x50);
                drawText(canvas, text[(i - 3) / 6], paint);
            }
            canvas.rotate(angle);
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas, String text, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(sp2px(8));
        float width = paint.measureText(text);
        canvas.drawText(text, -width / 2, -radius + dp2px(15), paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    protected int sp2px(int sp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics());
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundIndicatorView);
        maxNum = array.getInt(R.styleable.RoundIndicatorView_maxNum, 500);
        startAngle = array.getInt(R.styleable.RoundIndicatorView_startAngle, 160);
        sweepAngle = array.getInt(R.styleable.RoundIndicatorView_sweepAngle, 220);
        //内外圆的宽度
        sweepInWidth = dp2px(8);
        sweepOutWidth = dp2px(3);
        array.recycle();
    }

    private void drawIndicator(Canvas canvas) {
        //小圆点
        canvas.save();
        paint_2.setStyle(Paint.Style.STROKE);
        int sweep;
        if (currentNum <= maxNum) {
            sweep = (int) ((float) currentNum / (float) maxNum * sweepAngle);
        } else {
            sweep = sweepAngle;
        }
        paint_2.setStrokeWidth(sweepOutWidth);
        Shader shader = new SweepGradient(0, 0, indicatorColor, null);
        paint_2.setShader(shader);
        int w = dp2px(10);
        RectF rectF = new RectF(-radius - w, -radius - w, radius + w, radius + w);
        if (sweep > 0) {
            canvas.drawArc(rectF, startAngle, sweep, false, paint_2);
        }
        float x = (float) ((radius + dp2px(10)) * Math.cos(Math.toRadians(startAngle + sweep)));
        float y = (float) ((radius + dp2px(10)) * Math.sin(Math.toRadians(startAngle + sweep)));
        paint_3.setStyle(Paint.Style.FILL);
        paint_3.setColor(0xffffffff);
        paint_3.setMaskFilter(new BlurMaskFilter(dp2px(3), BlurMaskFilter.Blur.SOLID)); //需关闭硬件加速
        canvas.drawCircle(x, y, dp2px(3), paint_3);
        canvas.restore();
    }

    private void drawCenterText(Canvas canvas) {
        //中心的文字显示：信用部分
        canvas.save();
        paint_4.setStyle(Paint.Style.FILL);
        paint_4.setTextSize(radius / 2);
        paint_4.setColor(0xffffffff);
        canvas.drawText(currentNum + "", -paint_4.measureText(currentNum + "")/2 , 0, paint_4);
        paint_4.setTextSize(radius / 4);
        String content = "信用";
        if (currentNum < maxNum * 1 / 5) {
            content += text[0];
        } else if (currentNum >= maxNum * 1 / 5 && currentNum < maxNum * 2 / 5) {
            content += text[1];
        } else if (currentNum >= maxNum * 2 / 5 && currentNum < maxNum * 3 / 5) {
            content += text[2];
        } else if (currentNum >= maxNum * 3 / 5 && currentNum < maxNum * 4 / 5) {
            content += text[3];
        } else if (currentNum >= maxNum * 4 / 5) {
            content += text[4];
        }
        Rect r = new Rect();
        paint_4.getTextBounds(content, 0, content.length(), r);
        canvas.drawText(content, -r.width() / 2, r.height() + 20, paint_4);
        canvas.restore();
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
        invalidate();
    }

    public void setCurrentNumAnim(int num) {
        float duration = (float) Math.abs(num - currentNum) / maxNum * 1500 + 500;
		 //此处内部调用了此处内部调用了setCurrentNum方法
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "currentNum", num);
        animator.setDuration((long) Math.min(duration, 2000));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                int color = calculateColor(value);
                setBackgroundColor(color);
            }
        });
        animator.start();
    }

    private int calculateColor(int value) {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        float fraction = 0;
        int color = 0;
        if (value <= maxNum / 2) {
            fraction = (float) value / (maxNum / 2);
            color = (int) evaluator.evaluate(fraction, 0xffff6347, 0xffff8c00);
        } else {
            fraction = ((float) value - maxNum / 2) / (maxNum / 2);
            color = (int) evaluator.evaluate(fraction, 0xFFFF8C00, 0xFF00CED1); //由橙到蓝
        }
        return color;
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffffffff);
        paint_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_4 = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    //一些工具方法
    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics());
    }
}
