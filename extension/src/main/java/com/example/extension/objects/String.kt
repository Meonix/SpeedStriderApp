package com.example.extension.objects

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.Normalizer
import java.text.NumberFormat
import java.util.Locale
import java.util.regex.Pattern

val String?.trimText: String?
    get() {
        var s = this ?: return null
        if (s.isNullOrEmpty()) return null
        s = s.replace("\n", " ").replace("\\s+".toRegex(), " ").trim().trimStart().trimEnd()
            .trimIndent()
        return s
    }

/**
 * set string color for the special path
 * Ex:
 *   val text = "a b c"
 *   val terms = "a"
 *   val policy = "c"
 *   val spTermAndPolicy = text.toHyperText(terms).toHyperText(policy)
 */
fun String.toHyperText(
    subText: String,
    isBold: Boolean = true,
    actionClick: (() -> Unit)? = null
): SpannableString {
    if (this.isEmpty() || subText.isEmpty()) return SpannableString(this)
    val startIdx = this.indexOf(subText)
    val endIdx = startIdx + subText.length
    /*Set style*/
    return SpannableString(this).setSpannableHyperText(
        isBold = isBold,
        startIdx = startIdx,
        endIdx = endIdx,
        actionClick = actionClick
    )
}


fun SpannableString.toHyperText(
    subText: String,
    isBold: Boolean = true,
    actionClick: (() -> Unit)? = null
): SpannableString {
    if (this.isEmpty() || subText.isEmpty()) return this
    val startIdx = this.indexOf(subText)
    val endIdx = startIdx + subText.length
    /*Set style*/
    this.setSpannableHyperText(
        isBold = isBold,
        startIdx = startIdx,
        endIdx = endIdx,
        actionClick = actionClick
    )
    return this
}

private fun SpannableString.setSpannableHyperText(
    isBold: Boolean = true,
    startIdx: Int,
    endIdx: Int,
    actionClick: (() -> Unit)?
): SpannableString {
    apply {
        setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    actionClick?.invoke()
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
        if (isBold) {
            setSpan(
                StyleSpan(Typeface.BOLD),
                startIdx,
                endIdx,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return this
    }
}

val integerCashFmt = DecimalFormat("#,###,###,###", DecimalFormatSymbols(Locale.US))

fun String?.integerCash(): String {
    this ?: return ""
    return try {
        var originalString = this.replace(".", "")
        if (originalString.contains(",")) {
            originalString = originalString.replace(",".toRegex(), "")
        }
        val value = originalString.toLong()
        integerCashFmt.format(value)
    } catch (ignore: Exception) {
        ""
    }
}

fun String.getCashDecimal(prefix: String = ""): BigDecimal {
    return try {
        if (this.isEmpty()) return BigDecimal.ZERO
        val text = this
            .replace(",", "")
            .replace(" ", "")
            .replace(prefix, "")
        text.toBigDecimal()
    } catch (ignore: Exception) {
        BigDecimal.ZERO
    }
}

/**
 * search the keyword in the list collection
 * //Example:
 *   vb.etTest.addTextChangedListener(object: WrappedTextWatcher(){
 *        override fun onTextChanged(s: String) {
 *            val list  = s.normalizer().absoluteSearch(array){
 *                it
 *            }
 *            list?.toMutableList()?.let {
 *                if(it.size>1){
 *                    vb.tvTest.text = it[1]
 *                }
 *            }
 *        }
 *    })
 */
fun <T> String?.absoluteSearch(collection: Collection<T>?, block: (T) -> String): Collection<T>? {
    if (collection.isNullOrEmpty()) return null
    if (this.isNullOrEmpty()) return collection
    return collection.filter {
        block(it).trim().contains(this.trim(), true)
    }
}

/**
convert accent string text to latin
 */
fun String?.normalizer(): String? {
    this ?: return null
    return try {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        pattern.matcher(temp)
            .replaceAll("")
            .lowercase()
            //.replace(" ", "-")
            .replace("đ", "d", true)

    } catch (e: IllegalStateException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun String?.like(vararg strings: String?): Boolean {
    val left: String = this.normalizer() ?: return false
    for (s in strings) {
        val right: String = s.normalizer() ?: continue
        if (left.contains(right) || right.contains(left)) {
            return true
        }
    }
    return false
}

fun String.toMaskBankCardNumber(): String {
    // 0539xxxxxxxxxxxxx4616
    Log.d("basic", "toMaskCardNumber $this")
    return if (isNullOrEmpty()) {
        ""
    } else {
        val splitResult = lowercase().takeLast(16).chunked(4)
        StringBuilder()
            .append(splitResult.first()).append(" ")
            .append("****").append(" ")
            .append("****").append(" ")
            .append(splitResult.last())
            .toString()
    }
}

fun String.showMaskBankCardNumber(): String {
    return if (isNullOrEmpty()) {
        ""
    } else {
        val splitResult = lowercase().takeLast(16).chunked(4)
        return StringBuilder()
            .append(splitResult.first()).append(" ")
            .append(splitResult[1]).append(" ")
            .append(splitResult[2]).append(" ")
            .append(splitResult.last())
            .toString()
    }
}

fun String.toLowerCase(): String {
    val locator = Locale.getDefault()
    return this.lowercase(locator)
}

private val decimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat

// Convert money to Long or Double
fun String.moneyToLong(): Long {
    return try {
        this.replace(Regex("[^0-9]"), "").toLong()
    } catch (e: java.lang.Exception) {
        0
    }
}

fun String.moneyToDouble(): Double {
    return try {
        this.replace(Regex("[^0-9]"), "").toDouble()
    } catch (e: java.lang.Exception) {
        0.0
    }
}

fun Number?.moneyFormat(currency: String? = "VND"): String? {
    return this?.toString()?.moneyFormat(currency)
}

fun String?.moneyFormat(currency: String? = "VND"): String {
    this ?: return ""
    return try {
        if (currency != null && currency != "VND") {

            if (last().toString() == ".") return this

            val lgt = length
            if (lgt > 1 && substring(lgt - 2, lgt) == ".0") return this
            if (lgt > 2 && substring(lgt - 3, lgt) == ".00") return this

            val docId = indexOf(".")
            if (docId != -1 && substring(docId, length).length > 3) return substring(0, docId + 3)
        }
        var originalString = when (currency) {
            null, "VND" -> this.replace(".", "")
            else -> this
        }
        if (originalString.contains(",")) {
            originalString = originalString.replace(",".toRegex(), "")
        }
        when (currency) {
            null, "VND" -> {
                val value = originalString.toLong()
                decimalFormat.applyPattern("#,###,###,###")
                decimalFormat.format(value)
            }

            "đ" -> {
                val value = originalString.toLong()
                decimalFormat.applyPattern("#,###,###,###")
                "${decimalFormat.format(value)} đ"
            }

            "₫" -> {
                val value = originalString.toLong()
                decimalFormat.applyPattern("#,###,###,###")
                "${decimalFormat.format(value)} ₫"
            }

            else -> {
                val value = originalString.toDouble()
                decimalFormat.applyPattern("#,###,###,###.##")
                decimalFormat.format(value)
            }
        }

    } catch (nfe: Exception) {
        ""
    }
}

private fun cashText(numText: String): String {
    val n = numText.toLong()
    val n100 = n / 100
    val n10 = n / 10 % 10
    val n1 = n % 10

    if (n100 == 0L && n10 == 0L && n1 == 0L) return ""

    val s100 = if (numText.length < 3) "" else when (n100) {
        0L -> "không trăm"
        1L -> "một trăm"
        2L -> "hai trăm"
        3L -> "ba trăm"
        4L -> "bốn trăm"
        5L -> "năm trăm"
        6L -> "sáu trăm"
        7L -> "bảy trăm"
        8L -> "tám trăm"
        else -> "chín trăm"
    }

    val s10 = if (numText.length < 2) "" else when (n10) {
        0L -> if (n1 == 0L) "" else " lẻ"
        1L -> " mười"
        2L -> " hai mươi"
        3L -> " ba mươi"
        4L -> " bốn mươi"
        5L -> " năm mươi"
        6L -> " sáu mươi"
        7L -> " bảy mươi"
        8L -> " tám mươi"
        else -> " chín mươi"
    }

    val s1 = when (n1) {
        0L -> ""
        1L -> if (n10 < 2) " một" else " mốt"
        2L -> " hai"
        3L -> " ba"
        4L -> " bốn"
        5L -> if (n10 == 0L) " năm" else " lăm"
        6L -> " sáu"
        7L -> " bảy"
        8L -> " tám"
        else -> " chín"
    }
    return "$s100$s10$s1"

}

fun Number.cashToText(): String {
    val number = this.toString()

    var text = ""
    var startIndex = number.length - 3
    var endIndex = number.length
    var unit = " đồng"

    while (startIndex >= -2) {

        val sCash = number.substring(if (startIndex > -1) startIndex else 0, endIndex)

        text = " ${cashText(sCash)}$unit$text"
        startIndex -= 3
        endIndex -= 3
        unit = when (unit) {
            " nghìn" -> " triệu"
            " triệu" -> " tỷ"
            " đồng" -> " nghìn"
            else -> " nghìn"
        }
    }

    text = text.replace("  ", " ")
        .trim()
        .replace("tỷ triệu nghìn đồng", "tỷ đồng")
        .replace("triệu nghìn đồng", "triệu đồng")

    return text.substring(0, 1).uppercase() + text.substring(1, text.length)
}


fun String.removeEscapesFromJson(): String {
    val regex = Regex("\\\\(.)")
    return this.replace(regex) {
        when (val escapedChar = it.groupValues[1]) {
            "n" -> "\n" // Replace "\n" with newline character
            "r" -> "\r" // Replace "\r" with carriage return character
            "t" -> "\t" // Replace "\t" with tab character
            else -> escapedChar // Keep other escaped characters unchanged
        }

    }

}

