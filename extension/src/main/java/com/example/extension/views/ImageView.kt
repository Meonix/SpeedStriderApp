package com.example.extension.views

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

fun ImageView.tintRes(@ColorRes res: Int) {

    tint(if (res == 0) null else color(res))
}

fun ImageView.tint(@ColorInt color: Int?) {
    if (color == null) {
        clearColorFilter()
        colorFilter = null
        return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    } else {
        @Suppress("DEPRECATION")
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}

fun ImageView.postTint(@ColorInt color: Int?) {
    color ?: return
    post { tint(color) }
}

fun ImageView.postTintRes(@ColorRes res: Int) {
    post { tintRes(res) }
}

/**
 * @param color context.color([ID resource])*/
fun ImageView.tint(@ColorInt color: Int) {
    setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY)
}

/**
 * @param color context.color([ID resource])*/
fun ImageView.tintVector(@ColorInt color: Int) {
    setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
}
