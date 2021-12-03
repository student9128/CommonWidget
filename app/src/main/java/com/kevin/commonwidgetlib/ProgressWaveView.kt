package com.kevin.commonwidgetlib

import kotlin.jvm.JvmOverloads
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.kevin.commonwidget.R
import com.kevin.commonwidget.util.DisplayUtils

class ProgressWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), View.OnClickListener {
    private val mPaint: Paint
    private val mTextPaint: Paint
    private val mColor = 0
    private val mBackgroundColor = 0
    private val textSize = 0
    private val textColor = 0
    private val bitmapCanvas: Canvas
    private val bitmap: Bitmap
    private val mDefaultWidth: Int
    private val mDefaultHeight: Int
    private var waveLength = 0
    private var viewWidth = 0
    private var viewHeight = 0
    private var waveCount = 0
    private var viewSize = 0
    private var mPath: Path
    private var horizontalOffset = 0f
    private var centerY = 0
    private var mPaddingLeft = 0
    private var mPaddingRight = 0
    private var mPaddingTop = 0
    private var mPaddingBottom = 0
    private val defaultPadding: Int
    private var mLeft = 0
    private var mTop = 0
    private var mRight = 0
    private var mBottom = 0
    private var animator: ValueAnimator? = null
    private var mR = 0
    private val mCurrentProgress = 0
    private val mTotalProgress = 0
    private var percent = 0f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun measureWidth(measureSpec: Int): Int {
        var result = mDefaultWidth
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.UNSPECIFIED -> result = specSize
            MeasureSpec.AT_MOST -> result = Math.min(result, specSize)
            MeasureSpec.EXACTLY -> result = specSize
        }
        return result
    }

    private fun measureHeight(measureSpec: Int): Int {
        var result = mDefaultHeight
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.UNSPECIFIED -> result = Math.min(result, specSize)
            MeasureSpec.AT_MOST -> result = Math.min(result, specSize)
            MeasureSpec.EXACTLY -> result = specSize
        }
        return result
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.i(TAG, "left=$left,\t top=$top,\t right=$right,\t bottom=$bottom")
        mPaddingLeft = getPaddingLeft()
        mPaddingRight = getPaddingRight()
        mPaddingTop = getPaddingTop()
        mPaddingBottom = getPaddingBottom()
        if (mPaddingLeft <= defaultPadding) {
            mPaddingLeft = defaultPadding
        }
        if (mPaddingRight <= defaultPadding) {
            mPaddingRight = defaultPadding
        }
        if (mPaddingBottom <= defaultPadding) {
            mPaddingBottom = defaultPadding
        }
        if (mPaddingTop <= defaultPadding) {
            mPaddingTop = defaultPadding
        }
        mLeft = left + mPaddingLeft
        mTop = top + mPaddingTop
        mRight = right - mPaddingRight
        mBottom = bottom - mPaddingBottom
        Log.i(TAG, "mleft=$mLeft,\t mtop=$mTop,\t mright=$mRight,\t mbottom=$mBottom")
        mR = Math.min(width - mPaddingLeft - mPaddingRight, height - mPaddingTop - mPaddingBottom) / 2
        Log.w(TAG, "mR==$mR")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i(TAG, "w=$w,\t h=$h")
        viewWidth = w
        viewHeight = h
        waveLength = Math.min(w, h)
        viewSize = Math.min(w, h)
        waveCount = (Math.ceil((w / waveLength).toDouble()) + 1).toInt()
        Log.i(TAG, "waveCount=$waveCount")
        Log.i(TAG, "viewSize=$viewSize")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //        canvas.drawColor(Color.RED);
        mTextPaint.color = Color.RED
        mTextPaint.textSize = 30f
        val x: String ="${percent as Int}%"
        val bound = Rect()
        mTextPaint.getTextBounds(x, 0, x.length, bound)
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.GREEN
        centerY = (mTop + mBottom) / 2
        val y = (mTop + (mBottom - mTop) * (1 - percent / 100)).toInt()
        val centerX = (mLeft + mRight) / 2
        //        centerY = viewSize / 2;
        mPath = Path()
        //        mPath.reset();
        mPath.moveTo(mLeft.toFloat(), y.toFloat())
        for (i in 0 until waveCount) {
            mPath.quadTo(
                -waveLength * 3 / 4 + i * waveLength + horizontalOffset,
                (y + 30).toFloat(),
                -waveLength / 2 + i * waveLength + horizontalOffset,
                y.toFloat()
            )
            mPath.quadTo(
                -waveLength / 4 + i * waveLength + horizontalOffset,
                (y - 30).toFloat(),
                i * waveLength + horizontalOffset,
                y.toFloat()
            )
        }
        mPath.lineTo(viewSize.toFloat(), (viewSize / 2).toFloat())
        mPath.rLineTo(0f, (viewSize / 2).toFloat())
        mPath.rLineTo(-viewSize.toFloat(), 0f)
        mPath.close()
        canvas.save()
        canvas.translate((-mLeft + mPaddingLeft).toFloat(), (-mTop + mPaddingTop).toFloat())
        Log.d(TAG, "centerY=$centerY,mR=$mR")
        //        bitmapCanvas.drawCircle(centerX, centerY, mR, mPaint);
//        bitmapCanvas.drawCircle(centerX, centerY, mR, mPaint);
        val saveLayer = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        bitmapCanvas.drawPath(mPath, mPaint)
        mPaint.xfermode = null
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), mR.toFloat(), mPaint)
        mPaint.color = Color.RED
        //        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawText(
            x,
            (centerX - (bound.left + bound.right) / 2).toFloat(),
            (centerY - (bound.top + bound.bottom) / 2).toFloat(),
            mTextPaint
        )
        canvas.restoreToCount(saveLayer)
        canvas.restore()
    }

    private fun startAnimation() {
        animator = ValueAnimator.ofFloat(0f, waveLength.toFloat())
        animator?.setDuration(1000)
        animator?.setRepeatCount(ValueAnimator.INFINITE)
        animator?.setInterpolator(LinearInterpolator())
        animator?.addUpdateListener(AnimatorUpdateListener { animation ->
            horizontalOffset = animation.animatedValue as Float
            invalidate()
        })
        animator?.start()
        val a = ValueAnimator.ofFloat(0f, 80f)
        a.duration = 5000
        a.interpolator = AccelerateDecelerateInterpolator()
        a.addUpdateListener { animation ->
            percent = animation.animatedValue as Float
            invalidate()
        }
        a.start()
        if (percent >= 1) {
            if (a.isRunning) {
                a.cancel()
            }
        }
    }

    override fun onClick(v: View) {
        startAnimation()
    }

    companion object {
        private const val TAG = "ProgressWaveView"
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressWaveView)
        if (typedArray != null) {
        }
        mDefaultWidth = DisplayUtils.dp2px(context, 100f)
        mDefaultHeight = DisplayUtils.dp2px(context, 100f)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPath = Path()
        defaultPadding = DisplayUtils.dp2px(getContext(), 5f)
        bitmap = Bitmap.createBitmap(mDefaultWidth, mDefaultHeight, Bitmap.Config.ARGB_8888)
        bitmapCanvas = Canvas(bitmap)
        setOnClickListener(this)
    }
}