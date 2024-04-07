package com.example.extension.views

import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.extension.objects.colorStateList
import kotlin.math.roundToInt

fun View.onClickThrottled(skipDurationMillis: Long = 850, action: (view: View) -> Unit) {
    var isEnabled = true
    this.setOnClickListener {
        if (isEnabled) {
            action(it)
            isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({ isEnabled = true }, skipDurationMillis)
        }
    }
}

val View?.lifecycleScope: LifecycleCoroutineScope?
    get() = this?.findViewTreeLifecycleOwner()?.lifecycleScope

fun View.getPixels(@DimenRes res: Int): Float {
    return context.resources.getDimensionPixelSize(res).toFloat()
}

fun View.createDrawable(@DrawableRes res: Int): Drawable? {
    return drawable(res)?.constantState?.newDrawable()?.mutate()
}

fun View.pixels(@DimenRes res: Int): Float {
    return context.resources.getDimensionPixelSize(res).toFloat()
}

fun View.color(@ColorRes res: Int): Int {
    return ContextCompat.getColor(context, res)
}

fun View.string(@StringRes res: Int): String {
    return context.getString(res)
}

fun View.string(@StringRes res: Int, vararg args: Any?): String {
    return try {
        String.format(context.getString(res), *args)
    } catch (ignore: Exception) {
        ""
    }
}

fun View.typedValue(@AttrRes attrRes: Int): TypedValue {
    val outValue = TypedValue()
    context.theme.resolveAttribute(attrRes, outValue, true)
    return outValue
}

fun View.dimen(@DimenRes dimenRes: Int) = context.resources.getDimension(dimenRes)

fun View.anim(@AnimRes res: Int): Animation {
    return AnimationUtils.loadAnimation(context, res)
}

fun View.drawable(@DrawableRes res: Int): Drawable? {
    return ContextCompat.getDrawable(context, res)
}

fun View.dpToPixel(dp: Int): Int {

    val resources = context.resources
    val dimen = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources?.displayMetrics
    )
    return dimen.roundToInt() + 0
}

fun View.spToPixel(sp: Int): Int {
    val resources = context.resources
    val dimen = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp.toFloat(),
        resources?.displayMetrics
    )
    return dimen.roundToInt() + 0
}


fun View.backgroundTint(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        background?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    } else {
        @Suppress("DEPRECATION")
        background?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}

fun View.backgroundTintList(@ColorRes colorRes: Int) {
    backgroundTintList = context.colorStateList(colorRes)
}

fun View.backgroundTint(colorStateList: ColorStateList?) {
    backgroundTintList = colorStateList
}

fun View.backgroundTintRes(@ColorRes res: Int) {
    backgroundTint(color(res))
}

fun View.postBackgroundTint(@ColorInt color: Int) {
    post { backgroundTint(color) }
}

fun View.postBackgroundTintRes(@ColorRes res: Int) {
    postBackgroundTint(color(res))
}

