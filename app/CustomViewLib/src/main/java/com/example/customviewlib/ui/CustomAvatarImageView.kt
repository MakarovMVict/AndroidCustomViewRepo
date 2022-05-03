package com.example.customviewlib.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnRepeat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.example.customviewlib.R
import com.example.customviewlib.extensions.dpToPx
import kotlin.math.max
import kotlin.math.truncate


class CustomAvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_SIZE = 40

        private val bgColors = arrayOf(
            Color.parseColor("#7BC862"),
            Color.parseColor("#E17076"),
            Color.parseColor("#FAA774"),
            Color.parseColor("#6EC9CB"),
            Color.parseColor("#65AADD"),
            Color.parseColor("#A695E7"),
            Color.parseColor("#EE7AAE"),
            Color.parseColor("#2196F3"),
        )
    }

    //properties of imageView
    @Px
    var borderWidth: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)

    @ColorInt
    private var borderColor: Int = Color.WHITE

    private var initials: String = "??"

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val initialsPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val viewRect = Rect()//Rectangle for our view
    private val borderRect =
        Rect()//coordinates of border(needs to prevent makind our view less after clicks)
    private var size = 0


    private var isAvatarMode = true


    init {
        if (attrs != null) {
            val typeArray: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CustomAvatarImageView)
            borderWidth = typeArray.getDimension(
                R.styleable.CustomAvatarImageView_caiv_borderWidth,
                context.dpToPx(DEFAULT_BORDER_WIDTH)
            )
            borderColor = typeArray.getColor(
                R.styleable.CustomAvatarImageView_caiv_borderColor,
                DEFAULT_BORDER_COLOR
            )
            initials = typeArray.getString(R.styleable.CustomAvatarImageView_caiv_initials) ?: "??"
            typeArray.recycle()// needs to be recycled after use to efficient use of resources
        }
        scaleType = ScaleType.CENTER_CROP
        setup()
        setOnLongClickListener { handleLongClick() }
    }

    private fun setup() {

        with(borderPaint) {
            style = Paint.Style.STROKE// for border only !
            strokeWidth = borderWidth
            color = borderColor
        }
    }

    private fun prepareShader(w: Int, h: Int) {
        //prepare shader here
        if (w == 0 || drawable == null) return
        val srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    }

    private fun drawOval(canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), avatarPaint)
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
        setMeasuredDimension(max(initSize, size), max(initSize, size))
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

    override fun onDraw(canvas: Canvas) {
        //do not init objects here!!! just on previous ondraw methods!
//        super.onDraw(canvas)// comment if need to draw custom shape
        Log.e("CustomImageViewMask", "onDraw")
        //not allocate, only draw
        if (drawable != null && isAvatarMode) {
            drawAvatar(canvas)
        } else {
            drawInitials(canvas)
        }
        /**
         *draw oval and paint it with avatarPaint(shader with our image)
         */
        //canvas.drawOval(viewRect.toRectF(), avatarPaint)
        //resize rect
        val half = (borderWidth / 2).toInt()
        borderRect.set(viewRect)//don't allow our view become more small after clicks!
        /**
         * makes border width / 2 , so our image rect is less by the half of border's width
         */
        borderRect.inset(half, half)
        canvas.drawOval(borderRect.toRectF(), borderPaint) // drawing border
    }

//    override fun onLayout(
//        changed: Boolean,
//        left: Int,
//        top: Int,
//        right: Int,
//        bottom: Int
//    ) {//needs if has child Views
//        super.onLayout(changed, left, top, right, bottom)
//        Log.e("CustomImageViewMask", "onLayout")
//    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isAvatarMode = isAvatarMode
        savedState.borderWidth = borderWidth
        savedState.borderColor = borderColor
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if(state is SavedState){
            super.onRestoreInstanceState(state)
            isAvatarMode = state.isAvatarMode
            borderWidth = state.borderWidth
            borderColor = state.borderColor

        }else{
            super.onRestoreInstanceState(state)
        }

    }

    fun setInitials(initials: String) {
        this.initials = initials
        if (!isAvatarMode) {
            invalidate()
        }
    }

    fun setBorderColor(@ColorInt color: Int) {
        borderColor = color
        borderPaint.color = borderColor
        invalidate()
    }

    fun setupBorderWidth(@Dimension width: Int) {
        borderWidth = context.dpToPx(width)
        borderPaint.strokeWidth = borderWidth
        invalidate()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (isAvatarMode) prepareShader(width, height)
        Log.e("CustomImageViewMask", "setImageBitmap")
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        if (isAvatarMode) prepareShader(width, height)
        Log.e("CustomImageViewMask", "setImageResource")
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isAvatarMode) prepareShader(width, height)
        Log.e("CustomImageViewMask", "setImageDrawable")
    }

    private fun drawAvatar(canvas: Canvas) {
        Log.e("CAIV_Avatar", "drawAvatar")
        canvas.drawOval(viewRect.toRectF(), avatarPaint)
    }

    private fun drawInitials(canvas: Canvas) {//draw text initials
        Log.e("CAIV_Initials", "drawInitials")
        initialsPaint.color = initialsToColors(initials)
        canvas.drawOval(viewRect.toRectF(), initialsPaint)
        with(initialsPaint) {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = height * 0.33f
        }
        val offsetY = (initialsPaint.descent() + initialsPaint.ascent()) / 2
        canvas.drawText(
            initials,
            viewRect.exactCenterX(),
            viewRect.exactCenterY() - offsetY,
            initialsPaint
        )
    }

    private fun initialsToColors(letters: String): Int {
        val b: Byte = letters[0].code.toByte()
        val len: Int = bgColors.size
        val d: Double = b / len.toDouble()
        val index: Int = ((d - truncate(d)) * len).toInt()
        return bgColors[index]
    }

    private fun handleLongClick(): Boolean {
        val va = ValueAnimator.ofInt(width, (width*1.25).toInt()).apply {//adds animation
            duration = 200
            interpolator = OvershootInterpolator()
            repeatMode = ValueAnimator.REVERSE//for moving image back to size
            repeatCount = 1// 0 by default

        }
        va.addUpdateListener {
            size = it.animatedValue as Int
            requestLayout()
        }
        va.doOnRepeat { toggleMode() }//calls toggleMode() exactly in the mid of animation
        va.start()

        return true
    }

    private fun toggleMode() {
        isAvatarMode = !isAvatarMode
        invalidate()
    }

    private class SavedState : BaseSavedState, Parcelable {
        var isAvatarMode: Boolean = true
        var borderWidth: Float = 0f
        var borderColor: Int = 0

        constructor(superState:Parcelable?): super(superState)

        constructor(src: Parcel) : super(src) {

            //restore state from parcel
            var isAvatarMode = src.readInt() == 1
            var borderWidth = src.readFloat()
            var borderColor = src.readInt()
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            //write state to parcel

            super.writeToParcel(dst, flags)
            dst.writeInt(if (isAvatarMode) 1 else 0)
            dst.writeFloat(borderWidth)
            dst.writeInt(borderColor)
        }

        override fun describeContents() = 0


        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }

    }

}