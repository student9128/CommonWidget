package com.kevin.commonwidgetlib

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class WaveXView : View {
    enum class ShapeType {
        CIRCLE, SQUARE
    }

    // if true, the shader will display the wave
    var isShowWave = false

    // shader containing repeated waves
    private var mWaveShader: BitmapShader? = null

    // shader matrix
    private var mShaderMatrix: Matrix? = null

    // paint to draw wave
    private var mViewPaint: Paint? = null

    // paint to draw border
    private var mBorderPaint: Paint? = null
    private var mDefaultAmplitude = 0f
    private var mDefaultWaterLevel = 0f
    private var mDefaultWaveLength = 0f
    private var mDefaultAngularFrequency = 0.0
    private var mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO

    /**
     * Set horizontal size of wave according to `waveLengthRatio`
     *
     * @param waveLengthRatio Default to be 1.
     * Ratio of wave length to width of WaveView.
     */
    var waveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO
    private var mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO
    private var mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO
    private var mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR
    private var mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR
    private var mShapeType = DEFAULT_WAVE_SHAPE

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    private fun init() {
        mShaderMatrix = Matrix()
        mViewPaint = Paint()
        mViewPaint!!.isAntiAlias = true
    }

    private fun init(attrs: AttributeSet?) {
        init()
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WaveXView, 0, 0
        )
        mAmplitudeRatio =
            typedArray.getFloat(R.styleable.WaveXView_amplitudeRatio, DEFAULT_AMPLITUDE_RATIO)
        mWaterLevelRatio =
            typedArray.getFloat(R.styleable.WaveXView_waveWaterLevel, DEFAULT_WATER_LEVEL_RATIO)
        waveLengthRatio =
            typedArray.getFloat(R.styleable.WaveXView_waveLengthRatio, DEFAULT_WAVE_LENGTH_RATIO)
        mWaveShiftRatio =
            typedArray.getFloat(R.styleable.WaveXView_waveShiftRatio, DEFAULT_WAVE_SHIFT_RATIO)
        mFrontWaveColor =
            typedArray.getColor(R.styleable.WaveXView_frontWaveColor, DEFAULT_FRONT_WAVE_COLOR)
        mBehindWaveColor =
            typedArray.getColor(R.styleable.WaveXView_behindWaveColor, DEFAULT_BEHIND_WAVE_COLOR)
        mShapeType = if (typedArray.getInt(
                R.styleable.WaveXView_waveXShape,
                0
            ) == 0
        ) ShapeType.CIRCLE else ShapeType.SQUARE
        isShowWave = typedArray.getBoolean(R.styleable.WaveXView_showWave, true)
        typedArray.recycle()
    }

    /**
     * Shift the wave horizontally according to `waveShiftRatio`.
     *
     * @param waveShiftRatio Should be 0 ~ 1. Default to be 0.
     * Result of waveShiftRatio multiples width of WaveView is the length to shift.
     */
    var waveShiftRatio: Float
        get() = mWaveShiftRatio
        set(waveShiftRatio) {
            if (mWaveShiftRatio != waveShiftRatio) {
                mWaveShiftRatio = waveShiftRatio
                invalidate()
            }
        }

    /**
     * Set water level according to `waterLevelRatio`.
     *
     * @param waterLevelRatio Should be 0 ~ 1. Default to be 0.5.
     * Ratio of water level to WaveView height.
     */
    var waterLevelRatio: Float
        get() = mWaterLevelRatio
        set(waterLevelRatio) {
            if (mWaterLevelRatio != waterLevelRatio) {
                mWaterLevelRatio = waterLevelRatio
                invalidate()
            }
        }

    /**
     * Set vertical size of wave according to `amplitudeRatio`
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     * Ratio of amplitude to height of WaveView.
     */
    var amplitudeRatio: Float
        get() = mAmplitudeRatio
        set(amplitudeRatio) {
            if (mAmplitudeRatio != amplitudeRatio) {
                mAmplitudeRatio = amplitudeRatio
                invalidate()
            }
        }

    fun setBorder(width: Int, color: Int) {
        if (mBorderPaint == null) {
            mBorderPaint = Paint()
            mBorderPaint!!.isAntiAlias = true
            mBorderPaint!!.style = Paint.Style.STROKE
        }
        mBorderPaint!!.color = color
        mBorderPaint!!.strokeWidth = width.toFloat()
        invalidate()
    }

    fun setWaveColor(behindWaveColor: Int, frontWaveColor: Int) {
        mBehindWaveColor = behindWaveColor
        mFrontWaveColor = frontWaveColor
        if (width > 0 && height > 0) {
            // need to recreate shader when color changed
            mWaveShader = null
            createShader()
            invalidate()
        }
    }

    fun setShapeType(shapeType: ShapeType) {
        mShapeType = shapeType
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createShader()
    }

    /**
     * Create the shader with default waves which repeat horizontally, and clamp vertically
     */
    private fun createShader() {
        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / width
        mDefaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO
        mDefaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO
        mDefaultWaveLength = width.toFloat()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val wavePaint = Paint()
        wavePaint.strokeWidth = 2f
        wavePaint.isAntiAlias = true

        // Draw default waves into the bitmap
        // y=Asin(ωx+φ)+h
        val endX = width + 1
        val endY = height + 1
        val waveY = FloatArray(endX)
        wavePaint.color = mBehindWaveColor
        for (beginX in 0 until endX) {
            val wx = beginX * mDefaultAngularFrequency
            val beginY = (mDefaultWaterLevel + mDefaultAmplitude * Math.sin(wx)).toFloat()
            canvas.drawLine(beginX.toFloat(), beginY, beginX.toFloat(), endY.toFloat(), wavePaint)
            waveY[beginX] = beginY
        }
        wavePaint.color = mFrontWaveColor
        val wave2Shift = (mDefaultWaveLength / 4).toInt()
        for (beginX in 0 until endX) {
            canvas.drawLine(
                beginX.toFloat(),
                waveY[(beginX + wave2Shift) % endX],
                beginX.toFloat(),
                endY.toFloat(),
                wavePaint
            )
        }

        // use the bitamp to create the shader
        mWaveShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
        mViewPaint!!.shader = mWaveShader
    }

    override fun onDraw(canvas: Canvas) {
        // modify paint shader according to mShowWave state
        if (isShowWave && mWaveShader != null) {
            // first call after mShowWave, assign it to our paint
            if (mViewPaint!!.shader == null) {
                mViewPaint!!.shader = mWaveShader
            }

            // sacle shader according to mWaveLengthRatio and mAmplitudeRatio
            // this decides the size(mWaveLengthRatio for width, mAmplitudeRatio for height) of waves
            mShaderMatrix!!.setScale(
                waveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0f,
                mDefaultWaterLevel
            )
            // translate shader according to mWaveShiftRatio and mWaterLevelRatio
            // this decides the start position(mWaveShiftRatio for x, mWaterLevelRatio for y) of waves
            mShaderMatrix!!.postTranslate(
                mWaveShiftRatio * width,
                (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * height
            )

            // assign matrix to invalidate the shader
            mWaveShader!!.setLocalMatrix(mShaderMatrix)
            val borderWidth = if (mBorderPaint == null) 0f else mBorderPaint!!.strokeWidth
            when (mShapeType) {
                ShapeType.CIRCLE -> {
                    if (borderWidth > 0) {
                        canvas.drawCircle(
                            width / 2f, height / 2f,
                            (width - borderWidth) / 2f - 1f, mBorderPaint!!
                        )
                    }
                    val radius = width / 2f - borderWidth
                    canvas.drawCircle(width / 2f, height / 2f, radius, mViewPaint!!)
                }
                ShapeType.SQUARE -> {
                    if (borderWidth > 0) {
                        canvas.drawRect(
                            borderWidth / 2f,
                            borderWidth / 2f,
                            width - borderWidth / 2f - 0.5f,
                            height - borderWidth / 2f - 0.5f,
                            mBorderPaint!!
                        )
                    }
                    canvas.drawRect(
                        borderWidth, borderWidth, width - borderWidth,
                        height - borderWidth, mViewPaint!!
                    )
                }
            }
        } else {
            mViewPaint!!.shader = null
        }
    }

    companion object {
        /**
         * +------------------------+
         * |<--wave length->        |______
         * |   /\          |   /\   |  |
         * |  /  \         |  /  \  | amplitude
         * | /    \        | /    \ |  |
         * |/      \       |/      \|__|____
         * |        \      /        |  |
         * |         \    /         |  |
         * |          \  /          |  |
         * |           \/           | water level
         * |                        |  |
         * |                        |  |
         * +------------------------+__|____
         */
        private const val DEFAULT_AMPLITUDE_RATIO = 0.05f
        private const val DEFAULT_WATER_LEVEL_RATIO = 0.5f
        private const val DEFAULT_WAVE_LENGTH_RATIO = 1.0f
        private const val DEFAULT_WAVE_SHIFT_RATIO = 0.0f
        val DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#28FFFFFF")
        val DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#3CFFFFFF")
        val DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE
    }
}