package com.kevin.commonwidget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.kevin.commonwidget.util.DisplayUtils
import com.kevin.commonwidget.util.LogUtils
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Created by Kevin on 2021/11/21<br/>
 *
 * Describe:<br/>
 * 圆形水波纹进度球
 */
class CircleWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    companion object {
        val circleFill = 100
        val circleStroke = 101
        val single = 1000
        val dual = 1001
    }

    private val mDefaultBackgroundColor = 0xff00E0FF.toInt()
    var mBackgroundColor: Int = 0xff00E0FF.toInt()
        set(value) {
            field = value
            invalidate()
        }
    var mWaveColor: Int = 0xFF74F9FF.toInt()
        set(value) {
            field = value
            invalidate()
        }
    var mWaveColorSecond: Int = 0xAAA6FFF2.toInt()
        set(value) {
            field = value
            invalidate()
        }
    var mProgressColor: Int = 0xFFFFFFFF.toInt()
        set(value) {
            field = value
            invalidate()
        }
    var mCircleType = circleFill
        set(value) {
            field = value
            invalidate()
        }
    var mWaveType = single
        set(value) {
            field = value
            invalidate()
        }
    var mTextSize = 0
        set(value) {
            field = value
            invalidate()
        }
    var mCircleStrokeWidth = 5
        set(value) {
            field = value
            invalidate()
        }
    var mWaveCount = 2
        set(value) {
            field = value
            invalidate()
        }
    var mProgress = 50
        set(value) {
            field = value
            invalidate()
        }
    var mShowProgressText = false
        set(value) {
            field = value
            invalidate()
        }
    var mShowStroke = false
        set(value) {
            field = value
            invalidate()
        }
    var mWaveDuration = 3000
        set(value) {
            field = value
            invalidate()
        }
    var mProgressDuration = 5000
        set(value) {
            field = value
            invalidate()
        }
    var mAutoAnim = true
        set(value) {
            field = value
            invalidate()
        }
    private var mDefaultWidth = 10
    private var mDefaultHeight = 10
    private var mR = 5f
    private var mWaveLength = 0
    private var mPaddingLeft = 0
    private var mPaddingTop = 0
    private var mPaddingRight = 0
    private var mPaddingBottom = 0
    private var mLeft = 0
    private var mTop = 0
    private var mRight = 0
    private var mBottom = 0
    private val mBackgroundPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val mWavePaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val mProgressPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private var offset = 0
    private var offset1 = 0
    private var animator: ValueAnimator? = null
    private var animator1: ValueAnimator? = null
    private var mSwing = 0
    private var percent = 0f
    private val mPath: Path by lazy { Path() }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleWaveView)
        typedArray?.let {
            mBackgroundColor =
                it.getColor(R.styleable.CircleWaveView_backgroundColor, mDefaultBackgroundColor)
            mWaveColor = it.getColor(R.styleable.CircleWaveView_waveColor, 0xFF74F9FF.toInt())
            mWaveColorSecond =
                it.getColor(R.styleable.CircleWaveView_waveColorSecond, 0xAAA6FFF2.toInt())
            mWaveCount = it.getInt(R.styleable.CircleWaveView_waveCount, 2)
            mProgressColor = it.getColor(R.styleable.CircleWaveView_textColor, 0xFFFFFFFF.toInt())
            mCircleType = it.getInt(R.styleable.CircleWaveView_circleType, circleFill)
            mWaveType = it.getInt(R.styleable.CircleWaveView_waveType, single)
            mCircleStrokeWidth = it.getDimensionPixelSize(
                R.styleable.CircleWaveView_circleStrokeWidth,
                DisplayUtils.dip2px(getContext(), 2f)
            )
            mTextSize = it.getDimensionPixelSize(
                R.styleable.CircleWaveView_textSize,
                DisplayUtils.sp2px(getContext(), 14)
            )
            mProgress = it.getInt(R.styleable.CircleWaveView_progress, 50)
            mShowProgressText = it.getBoolean(R.styleable.CircleWaveView_showProgressText, false)
            mShowStroke = it.getBoolean(R.styleable.CircleWaveView_showStroke, false)
            mWaveDuration = it.getInt(R.styleable.CircleWaveView_waveDuration, 3000)
            mProgressDuration = it.getInt(R.styleable.CircleWaveView_progressDuration, 5000)
            mAutoAnim = it.getBoolean(R.styleable.CircleWaveView_autoAnim, true)
            it.recycle()
        }
        mDefaultWidth = DisplayUtils.dip2px(getContext(), 50f)
        mDefaultHeight = DisplayUtils.dip2px(getContext(), 50f)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = getSmartSize(mDefaultWidth, widthMeasureSpec)
        val height = getSmartSize(mDefaultHeight, heightMeasureSpec)
        val size = min(width, height)
        setMeasuredDimension(size, size)
    }

    //    @JvmName("getSmartSize")
    private fun getSmartSize(defaultSize: Int, measureSpec: Int): Int {
        var result = defaultSize
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.EXACTLY -> result = specSize
            MeasureSpec.UNSPECIFIED or MeasureSpec.AT_MOST -> result = min(result, specSize)
        }
        return result
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mPaddingLeft = paddingLeft
        mPaddingTop = paddingTop
        mPaddingRight = paddingRight
        mPaddingBottom = paddingBottom
        mLeft = left + paddingLeft
        mTop = top + paddingTop
        mRight = right - paddingRight
        mBottom = bottom - paddingBottom
        mR = ((height - mPaddingTop - mPaddingBottom) / 2).toFloat()
        mWaveLength = ((width - paddingLeft - paddingRight) / (mWaveCount * 1f)).roundToInt()
        val width = width - paddingLeft - paddingRight
        val height = height - paddingTop - paddingBottom
        mSwing = width / 10
        animator = ValueAnimator.ofInt(0, mWaveLength * 2)
        animator1 = ValueAnimator.ofInt(0, mWaveLength * 2)
        if (mAutoAnim) startAnim()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        LogUtils.logW("CircleX", "onDraw waveColor=$mWaveColor,radio=$radio")
        mBackgroundPaint.color = mBackgroundColor
        mWavePaint.color = mWaveColor
        mBackgroundPaint.style =
            if (mCircleType == circleFill) Paint.Style.FILL else Paint.Style.STROKE
        mBackgroundPaint.strokeWidth = mCircleStrokeWidth.toFloat()
        mWavePaint.style = Paint.Style.FILL
        mProgressPaint.textSize = mTextSize.toFloat()
        mProgressPaint.color = mProgressColor
        val centerY = ((bottom - top) / 2).toFloat()
        val centerX = ((right - left) / 2).toFloat()
        val circlePath = Path()
        circlePath.addCircle(
            centerX.toFloat(),
            centerY.toFloat(),
            mR.toFloat(), Path.Direction.CW
        )
        canvas?.clipPath(circlePath)
        if (!mShowStroke) {
            canvas?.let {
                it.drawCircle(
                    centerX.toFloat(),
                    centerY.toFloat(),
                    mR.toFloat(),
                    mBackgroundPaint
                )
            }
        }
        if (mWaveType == dual) {
            drawWave(canvas, centerX, centerY, true)
        }
        drawWave(canvas, centerX, centerY, false)

        if (mShowStroke) {
            canvas?.let {
                it.drawCircle(
                    centerX.toFloat(),
                    centerY.toFloat(),
                    mR.toFloat(),
                    mBackgroundPaint
                )
            }
        }
        drawText(canvas, centerX, centerY)
    }

    private fun drawWave(canvas: Canvas?, centerX: Float, centerY: Float, isSecond: Boolean) {
        mWavePaint.color = if (isSecond) mWaveColorSecond else mWaveColor
        mPath.reset()
        val endY: Int =
            if (percent >= 100) -mSwing else ((mBottom - mTop) * (1 - percent / 100)).toInt()
        mPath.moveTo(0f, endY.toFloat())
        for (i in 0..(mWaveCount * 2)) {
            val y = if (i % 2 == 0) {
                if (isSecond) endY - mSwing
                else endY + mSwing
            } else {
                if (isSecond) endY + mSwing
                else endY - mSwing
            }
            val x =
                (i * mWaveLength + mWaveLength / 2 - mWaveLength * 2 + if (isSecond) offset1 else offset).toFloat()
            mPath.quadTo(
                x,
                y.toFloat(),
                ((i + 1) * mWaveLength - mWaveLength * 2 + if (isSecond) offset1 else offset).toFloat(),
                endY.toFloat()
            )
        }
        mPath.lineTo(mRight.toFloat(), centerY)
        mPath.rLineTo(0f, centerY)
        mPath.rLineTo(-mRight.toFloat(), 0f)
        mPath.close()
        if (percent > 0) {
            canvas!!.drawPath(mPath, mWavePaint)
        }
        mPath.reset()
    }

    private fun drawText(canvas: Canvas?, centerX: Float, centerY: Float) {
        if (!mShowProgressText) return
        val text = "${percent.toInt()}%"
        val bounds = Rect()
        mProgressPaint.getTextBounds(text, 0, text.length, bounds)
        val textLeft = centerX - bounds.width() / 2
        val textBottom = centerY + bounds.height() / 2
        canvas!!.drawText(text, textLeft, textBottom, mProgressPaint)
    }

    fun startAnim() {
        animator?.let {
            it.duration = mWaveDuration.toLong()
            it.repeatCount = ValueAnimator.INFINITE
            it.interpolator = LinearInterpolator()
            it.addUpdateListener { animation ->
                offset = animation?.animatedValue as Int
//                LogUtils.logD("CircleWave", "offset-$offset,i======${++i}")
                invalidate()
                if (percent >= 100) {
                    if (it.isRunning) {
                        it.cancel()
                    }
                }
            }
            it.start()
        }
        animator1?.let {
            it.duration = (mWaveDuration * 1.5).toLong()
            it.repeatCount = ValueAnimator.INFINITE
            it.interpolator = LinearInterpolator()
            it.addUpdateListener { animation ->
                offset1 = animation?.animatedValue as Int
//                LogUtils.logD("CircleWave", "offset-$offset,i======${++i}")
                invalidate()
                if (percent >= 100) {
                    if (it.isRunning) {
                        it.cancel()
                    }
                }
            }
            it.start()
        }
        val a = ValueAnimator.ofFloat(0f, mProgress.toFloat())
        a.duration = mProgressDuration.toLong()
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

    fun stopAnim() {
        animator?.let {
            if (it.isRunning) {
                it.pause()
            }
        }
        animator1?.let {
            if (it.isRunning) {
                it.pause()
            }
        }
    }

    fun destroy() {
        animator = null
        animator1 = null
    }

    fun setProgress(progress: Int) {
        this.percent = progress.toFloat()
        invalidate()
    }

    fun getProgress(): Int {
        return percent.toInt()
    }

}