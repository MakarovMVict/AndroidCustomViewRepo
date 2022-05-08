package com.example.customviewlib.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customviewlib.R
import com.facebook.shimmer.ShimmerFrameLayout

class CustomIosShimmerLoaderView(
    context: Context,
    attrSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrSet) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, 0, 0)

    private var shimmerLayout: ShimmerFrameLayout =
        ShimmerFrameLayout(context, attrSet)

    init {
        val attributes: TypedArray = context.theme.obtainStyledAttributes(
            attrSet,
            R.styleable.ShimmerLoaderConstraintLayout,
            defStyleAttr,
            defStyleRes
        )
        try {
            val shimmerLayout = attributes.getResourceId(
                R.styleable.ShimmerLoaderConstraintLayout_shimmerLayout, 0
            )
            val shimmerVisibility = attributes.getBoolean(
                R.styleable.ShimmerLoaderConstraintLayout_shimmerVisibility, false
            )

            setShimmerLayout(shimmerLayout)
            setShimmerVisibility(shimmerVisibility)
            addView(this.shimmerLayout)
        } catch (ex: Exception) {
            Log.d(TAG, "setLayout: $ex")
        } finally {
            attributes.recycle()
        }
    }

    private fun handleVisibility(
        shimmerVisibility: Boolean = false
    ) {
        if (shimmerVisibility) {
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.showShimmer(true)
            shimmerLayout.startShimmer()
        } else {
            shimmerLayout.visibility = View.GONE
            shimmerLayout.stopShimmer()
            shimmerLayout.hideShimmer()
        }
    }

    fun setShimmerVisibility(shimmerVisibility: Boolean) {
        handleVisibility(shimmerVisibility = shimmerVisibility)
    }

    fun setShimmerLayout(shimmerLayout: Int) {
        this.shimmerLayout.removeAllViews()
        inflate(context, shimmerLayout, this.shimmerLayout)
    }

    companion object {
        private const val TAG = "ShimmerLoader"
    }
}