package com.example.extension.worker

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation

object AnimationHelper {
    const val PIVOT_X_START_LEFT = 0f
    const val PIVOT_Y_TOP_LEFT = 0f
    const val PIVOT_X_CENTER = 0.5f
    const val PIVOT_Y_CENTER = 0.5f

    private const val ANIMATION_FAST_MILLIS = 50L
    private const val ANIMATION_SLOW_MILLIS = 100L

    fun getScaleAnimation(
        fromX: Float,
        toX: Float,
        fromY: Float,
        toY: Float,
        pivotXValue: Float,
        pivotYValue: Float,
        duration: Long = 300,
        onAnimationEnd: (() -> Unit?)? = null
    ): ScaleAnimation {
        val anim = ScaleAnimation(
            fromX, toX,  // Start and end values for the X axis scaling
            fromY, toY,  // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, pivotXValue,  // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, pivotYValue
        )
        anim.duration = duration

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                onAnimationEnd?.invoke()
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })
        return anim
    }


    fun captureAnimation(view: View) {
        view.postDelayed({
            view.foreground = ColorDrawable(Color.WHITE)
            view.postDelayed(
                { view.foreground = null }, ANIMATION_FAST_MILLIS
            )
        }, ANIMATION_SLOW_MILLIS)
    }
}