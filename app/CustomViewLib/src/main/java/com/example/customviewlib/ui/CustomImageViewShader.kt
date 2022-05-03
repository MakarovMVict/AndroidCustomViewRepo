package com.example.customviewlib.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.example.customviewlib.R
import com.example.customviewlib.extensions.dpToPx

/**
 * shader is more productive if compare with mask version
 */

class CustomImageViewShader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_SIZE = 40
    }

    //properties of imageView
    @Px
    var borderWith: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)

    @ColorInt
    private var borderColor: Int = Color.WHITE
    private var initials: String = "??"

    private val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val viewRect = Rect()//Rectangle for our view


    init {
        if (attrs != null) {
            val typeArray: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CustomImageViewMask)
            borderWith = typeArray.getDimension(
                R.styleable.CustomImageViewShader_civs_borderWidth,
                context.dpToPx(DEFAULT_BORDER_WIDTH)
            )
            borderColor = typeArray.getColor(
                R.styleable.CustomImageViewShader_civs_borderColor,
                DEFAULT_BORDER_COLOR
            )
            initials = typeArray.getString(R.styleable.CustomImageViewShader_civs_initials) ?: "??"
        }
        scaleType = ScaleType.CENTER_CROP
        setup()
    }

    private fun setup() {

        with(borderPaint) {
            style = Paint.Style.STROKE// for border only !
            strokeWidth = borderWith
            color = borderColor
        }
    }

    private fun prepareShader(w: Int, h: Int) {
        //prepare shader here

        val srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e("CustomImageViewMask", "onAttachedToWindow")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(
            "CustomImageViewMask", """
            onMeasure
            width: ${MeasureSpec.toString(widthMeasureSpec)}
            height: ${MeasureSpec.toString(heightMeasureSpec)}
            """
        )

        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)
        Log.e("CustomImageViewMask", "On measure after set size: $measuredWidth $measuredHeight")
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.dpToPx(DEFAULT_SIZE).toInt()//resolveDefaultSize
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e("CustomImageViewMask", "onSizeChanged")
        if (w == 0) return
        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }

        prepareShader(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        //do not init objects here!!! just on previous ondraw methods!
//        super.onDraw(canvas)
        Log.e("CustomImageViewMask", "onDraw")
        //not allocate, only draw
        /**
         *draw oval and paint it with avatarPaint(shader with our image)
         */
        canvas?.drawOval(viewRect.toRectF(), avatarPaint)
        //resize rect
        val half = (borderWith / 2).toInt()
        /**
         * makes border width / 2 , so our image rect is less by the half of border's width
         */
        viewRect.inset(half, half)
        canvas?.drawOval(viewRect.toRectF(), borderPaint) // drawing border
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {//needs if has child Views
        super.onLayout(changed, left, top, right, bottom)
        Log.e("CustomImageViewMask", "onLayout")
    }

}
