package com.example.extension.views

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.TypefaceSpan
import android.widget.TextView
import androidx.annotation.ColorRes
import com.example.extension.worker.WrappedTextWatcher

fun TextView.textColorRes(@ColorRes res: Int) {
    setTextColor(color(res))
}

fun TextView.onTextChanged(block: (String) -> Unit) {
    addTextChangedListener(object : WrappedTextWatcher() {
        override fun onTextChanged(s: String) {
            block(s)
        }
    })
}

fun TextView.setSpannableHyperText(spannable: Spannable, @ColorRes color: Int) {
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
    setLinkTextColor(color(color))
    text = spannable
}

class CustomTypefaceSpan(family: String?, private val newType: Typeface) : TypefaceSpan(family) {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    companion object {
        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            val old = paint.typeface
            oldStyle = old?.style ?: 0
            val fake = oldStyle and tf.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.typeface = tf
        }
    }
}
