package com.kevin.commonwidgetlib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.kevin.commonwidget.util.LogUtils;

/**
 * Created by Kevin on 2020/11/21<br/>
 * Blog:http://student9128.top/
 * 公众号：前线开发者Kevin
 * Describe:<br/>
 */
public class WaveView extends View implements View.OnClickListener {
    private Paint mPaint;
    private Paint mPaint2;
    private int waveLength = 400;
    private int offset, offset1;
    private int waveCount;
    private int screenWidth, screenHeight;
    private static final String TAG = "WaveView";
    private int progress;
    private Bitmap bitmap;
    private Canvas mCanvas;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        setOnClickListener(this);
        bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        waveCount = (int) Math.round(w / waveLength + 1.5);//因为要从负轴开始移动，所以要加1.5
        Log.i(TAG, "waveCount==" + waveCount+",w / waveLength="+w / waveLength);
        Log.i(TAG, "screenWidth==" + screenWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(Color.parseColor("#aa74b49b"));
        mPaint2.setStyle(Paint.Style.FILL);
        mPaint2.setStrokeWidth(6);
        mPaint2.setColor(Color.parseColor("#a7d7c5"));

        Path path3 = new Path();
        path3.moveTo(400,0);
        path3.rLineTo(0,200);
        Path path = new Path();
        Path path2 = new Path();
        path.moveTo(0, 200);
        path2.moveTo(0, 200);
        for (int i = 0; i < waveCount; i++) {
            LogUtils.INSTANCE.logD(TAG,"-300 + i * waveLength + offset="+(-300 + i * waveLength + offset));
            path.quadTo(-300 + i * waveLength + offset, 150, -200 + i * waveLength + offset, 200);
            path.quadTo(-100 + i * waveLength + offset, 250, 0 + i * waveLength + offset, 200);
            //waveLength= 400
//            path2.quadTo(-300 + i * waveLength + offset1, 250, -200 + i * waveLength + offset1, 200);
//            path2.quadTo(-100 + i * waveLength + offset1, 150, 0 + i * waveLength + offset1, 200);
        }
        path.lineTo(screenWidth, 200);
        path.rLineTo(0, 300);
        path.rLineTo(-screenWidth, 0);
        path.close();
        path2.lineTo(screenWidth, 200);
        path2.rLineTo(0, 300);
        path2.rLineTo(-screenWidth, 0);
        path2.close();

        Paint paint1 = new Paint();
        paint1.setColor(Color.RED);
        paint1.setStrokeWidth(2);
//        paint1.setStyle(Paint.Style.STROKE);

//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(5);
//        mPaint.setColor(Color.BLUE);
//        mCanvas.drawCircle(200, 200, 200, mPaint);
        mPaint.setColor(Color.GREEN);
//        mPaint.setStyle(Paint.Style.STROKE);
        mCanvas.drawCircle(200, 200, 200, mPaint);
        int xx = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

//        canvas.drawPath(path2, mPaint2);
//        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawBitmap(bitmap, 0, 0, null);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mPaint.setColor(Color.parseColor("#aaDB3951"));
        mCanvas.drawPath(path, mPaint);
        mPaint.setColor(Color.parseColor("#DB3951"));
        mCanvas.drawPath(path2,mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(xx);
        canvas.drawLine(400,0,400,400,mPaint);
        canvas.drawLine(200,0,200,400,mPaint);


    }


    @Override
    public void onClick(View v) {

        ValueAnimator animator = ValueAnimator.ofInt(0, waveLength);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
        ValueAnimator animator1 = ValueAnimator.ofInt(0, waveLength);
        animator1.setDuration(1200);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset1 = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
        animator1.start();

    }
}