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

class CustomImageViewMask @JvmOverloads constructor(
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

    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val viewRect = Rect()//Rectangle for our view
    private lateinit var resultBm: Bitmap
    private lateinit var maskBm: Bitmap
    private lateinit var srcBm: Bitmap


    init {
        if (attrs != null) {
            val typeArray: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CustomImageViewMask)
            borderWith = typeArray.getDimension(
                R.styleable.CustomImageViewMask_civ_borderWidth,
                context.dpToPx(DEFAULT_BORDER_WIDTH)
            )
            borderColor = typeArray.getColor(
                R.styleable.CustomImageViewMask_civ_borderColor,
                DEFAULT_BORDER_COLOR
            )
            initials = typeArray.getString(R.styleable.CustomImageViewMask_civ_initials) ?: "??"
            typeArray.recycle()// needs to be recycled after use to efficient use of resources
        }
        scaleType = ScaleType.CENTER_CROP
        setup()
    }

    private fun setup() {
        with(maskPaint) {
            color = Color.RED
            style = Paint.Style.FILL
        }

        with(borderPaint){
            style = Paint.Style.STROKE// for border only !
            strokeWidth = borderWith
            color = borderColor
        }
    }

    private fun prepareBitMaps(w: Int, h: Int) {
        //prepare buffer here

        /**
         * can take argb and alpha channel if ARGB_8888 ,
         * ALPHA_8 - when dont care for other colors - less memory usage
        **/
        maskBm = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        resultBm = maskBm.copy(Bitmap.Config.ARGB_8888, true)

        val maskCanvas = Canvas(maskBm)
        maskCanvas.drawOval(viewRect.toRectF(), maskPaint)//draws oval
        /**
         * overlaps oval mask and image bitmap
         * PorterDuff.Mode - use different modes for different types of overlapping
         */
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        srcBm = drawable.toBitmap(w,h,Bitmap.Config.ARGB_8888)//convert drawable to bitmap(on the top )

        val resultCanvas = Canvas(resultBm)//oval mask bitmap

        resultCanvas.drawBitmap(maskBm,viewRect,viewRect,null)
        resultCanvas.drawBitmap(srcBm,viewRect,viewRect,maskPaint)//use null instead maskpaint for no shape
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

        prepareBitMaps(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        //do not init objects here!!! just on previous ondraw methods!
//        super.onDraw(canvas)
        Log.e("CustomImageViewMask", "onDraw")
        //not allocate, only draw
        canvas?.drawBitmap(resultBm, viewRect, viewRect, null)
        //resize rect
        val half = (borderWith / 2).toInt()
        /**
         * makes border width / 2 , so our image rect is less by the half of border's width
         */
        viewRect.inset(half,half)
        canvas?.drawOval(viewRect.toRectF(),borderPaint) // drawing border
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
