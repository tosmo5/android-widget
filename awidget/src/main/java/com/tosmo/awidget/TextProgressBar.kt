package com.tosmo.awidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.icu.text.DecimalFormat
import android.util.AttributeSet
import android.widget.ProgressBar
import java.math.RoundingMode

class TextProgressBar(context: Context?, attrs: AttributeSet?) :
    ProgressBar(context, attrs) {
    
    constructor(context: Context?) : this(context, null)
    
    
    var description = ""
    private var textColor = Color.BLACK
    private var textSize = 32f
    private var verticalGap = 15f
    
    
    private var mPercentText = "0%"
    private var mCountText = "0/0"
    private var mCompletion = 0f
    private val mTextPaint = Paint()
    private val mBounds = Rect()
    private var mPreTime = 0L
    private var mPreProgress = 0
    private var mSpeedPerSecond = "0"
    
    init {
        attrs?.let { setAttrs(it) }
    }
    
    private fun setAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.TextProgressBar, 0, 0).let {
            description = it.getString(R.styleable.TextProgressBar_description).orEmpty()
            textColor = it.getColor(R.styleable.TextProgressBar_textColor, Color.BLACK)
            textSize = it.getDimension(R.styleable.TextProgressBar_textSize, 32f)
            verticalGap = it.getFloat(R.styleable.TextProgressBar_verticalGap, 5f)
            it.recycle()
        }
    }
    
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mTextPaint.isAntiAlias = true
        mTextPaint.color = textColor
        mTextPaint.textSize = textSize
        // 绘制百分比
        mTextPaint.getTextBounds(mPercentText, 0, mPercentText.length, mBounds)
        var x = width * mCompletion
        var y = height / 2f - mBounds.centerY()
        canvas?.drawText(mPercentText, x, y, mTextPaint)
        
        // 绘制描述
        mTextPaint.getTextBounds(description, 0, description.length, mBounds)
        x = 10f
        y = height + verticalGap - mBounds.height() - 2
        canvas?.drawText(description, x, y, mTextPaint)
        
        // 绘制右下角的数量进度
        mTextPaint.getTextBounds(mCountText, 0, mCountText.length, mBounds)
        x = width * 1f - mBounds.width() - 10
        y = height + verticalGap - mBounds.height() - 2
        canvas?.drawText(mCountText, x, y, mTextPaint)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            var mostHeight = height
            mTextPaint.getTextBounds(mPercentText, 0, mPercentText.length, mBounds)
            if (mostHeight < mBounds.height()) mostHeight += (mBounds.height() - mostHeight) * 2
            mTextPaint.getTextBounds(mCountText, 0, mCountText.length, mBounds)
            mostHeight += (verticalGap + mBounds.height()).toInt() * 8
            setMeasuredDimension(widthMeasureSpec, mostHeight)
        }
    }
    
    override fun setProgress(progress: Int) {
        if (mPreTime == 0L) {
            mPreTime = System.currentTimeMillis()
            mPreProgress = progress
        }
        mCompletion = progress * 1f / max
        // 百分比文本
        mPercentText = DecimalFormat("#.##").apply {
            roundingMode = RoundingMode.HALF_UP.ordinal
        }.format(mCompletion * 100).let { "$it%" }
        
        // 计数文本
        ((System.currentTimeMillis() - mPreTime) / 1000f).let { second ->
            if (second >= 1) {
                mSpeedPerSecond = ((progress - mPreProgress) / second).toInt().toString()
                mPreTime = System.currentTimeMillis()
                mPreProgress = progress
            }
        }
        mCountText = "$mSpeedPerSecond/s  $progress/$max"
        super.setProgress(progress)
    }
}