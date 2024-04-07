package com.example.extension.objects

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.example.extension.views.CustomTypefaceSpan


fun SpannableString.toHyperText(
    context: Context,
    subText: String,
    @DimenRes textSize: Int = 0,
    @FontRes idFontFamily: Int?,
    @ColorRes idColor: Int?,
    actionClick: (() -> Unit)? = null
): SpannableString {
    if (this.isEmpty() || subText.isEmpty()) return this
    val startIdx = this.indexOf(subText)
    val endIdx = startIdx + subText.length
    /*Set style*/
    return this.apply {
        if (textSize != 0) {
            setSpan(
                AbsoluteSizeSpan(context.resources.getDimensionPixelSize(textSize)),
                startIdx,
                endIdx,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && idColor != null) {
            setSpan(
                ForegroundColorSpan(context.getColor(idColor)),
                startIdx,
                endIdx,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
        if (idFontFamily != null) {
            setSpan(
                CustomTypefaceSpan(
                    null,
                    Typeface.create(ResourcesCompat.getFont(context, idFontFamily), Typeface.NORMAL)
                ),
                startIdx,
                endIdx,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (actionClick != null) {
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        actionClick.invoke()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                },
                startIdx,
                endIdx,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}