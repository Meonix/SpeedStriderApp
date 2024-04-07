package com.example.extension.views

import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import com.example.extension.objects.integerCashFmt
import com.example.extension.worker.CardNumberWatcher
import com.example.extension.worker.OTPNumberWatcher
import com.example.extension.worker.SimpleTextWatcher
import com.example.extension.worker.WrappedTextWatcher
import java.math.BigDecimal

fun EditText.addFilterByChars(chars: CharArray) {
    val charsFilter = InputFilter { source, start, end, _, _, _ ->
        when {
            end > start -> for (index in start until end) {
                if (!String(chars).contains(source[index].toString())) {
                    return@InputFilter ""  // do not change the old string
                }
            }
        }
        return@InputFilter null // keep original input -> old string is changed
    }
    addFilter(charsFilter)
}

fun EditText.addFilter(filter: InputFilter) {
    val newFilter = mutableListOf<InputFilter>()
    newFilter.add(filter)
    if (!this.filters.isNullOrEmpty()) {
        newFilter.addAll(this.filters)
    }
    this.filters = newFilter.toTypedArray()
}

val EditText.amount: BigDecimal
    get() {
        return try {
            val s = this.text?.toString()
            if (s.isNullOrEmpty()) return BigDecimal.ZERO
            val text = s.toString().replace(",", "")
            text.toBigDecimal()
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }


/**
 * dd/MM/yy
 */
fun EditText.addShortDateWatcher(prefix: Char = '/') {
    inputType = InputType.TYPE_CLASS_NUMBER
    filters = arrayOf(InputFilter.LengthFilter(8))
    addTextChangedListener(object : WrappedTextWatcher() {

        private val sb: StringBuilder = StringBuilder("")

        override fun onTextChanged(s: String) {
            sb.clear()
            sb.append(if (s.length > 8) s.subSequence(0, 8) else s)
            if (sb.lastIndex == 2 && sb[2] != prefix) {
                sb.insert(2, prefix)
                setTextSilently(sb.toString())
            } else if (sb.lastIndex == 5 && sb[5] != prefix) {
                sb.insert(5, prefix)
                setTextSilently(sb.toString())
            }
        }
    })
}

/**
 * dd/MM/yyyy
 */
fun EditText.addDateWatcher(prefix: Char = '/') {
    inputType = InputType.TYPE_CLASS_NUMBER
    filters = arrayOf(InputFilter.LengthFilter(10))
    addTextChangedListener(object : WrappedTextWatcher() {

        private val sb: StringBuilder = StringBuilder("")

        override fun onTextChanged(s: String) {
            sb.clear()
            sb.append(if (s.length > 10) s.subSequence(0, 10) else s)
            if (sb.lastIndex == 2 && sb[2] != prefix) {
                sb.insert(2, prefix)
                setTextSilently(sb.toString())
            } else if (sb.lastIndex == 5 && sb[5] != prefix) {
                sb.insert(5, prefix)
                setTextSilently(sb.toString())
            }
        }
    })
}


fun EditText.addOTPWatcher() {
    val sFilters = arrayListOf<InputFilter>(InputFilter.LengthFilter(50))
    filters = sFilters.toTypedArray()
    addFilterByChars(charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', ' '))
    inputType = InputType.TYPE_CLASS_PHONE or InputType.TYPE_CLASS_PHONE
    transformationMethod = null
    addTextChangedListener(OTPNumberWatcher())
}

fun EditText.addCardNumberWatcher() {
    val sFilters = arrayListOf<InputFilter>(InputFilter.LengthFilter(19))
    filters = sFilters.toTypedArray()
    addFilterByChars(charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', ' '))
    inputType = InputType.TYPE_CLASS_PHONE or InputType.TYPE_CLASS_PHONE
    transformationMethod = null
    addTextChangedListener(CardNumberWatcher())
}

/**
 * apply string text to format cash input
 * Example:
 *  vb.etTest.addIntegerCashWatcher("đ")
 */

fun EditText.addIntegerCashWatcher(prefix: String = "") {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or
            InputType.TYPE_NUMBER_FLAG_SIGNED
    transformationMethod = null
    addTextChangedListener(object : WrappedTextWatcher() {
        override fun onTextChanged(s: String) {
            val formattedString = when (val value = getCleanValue(s)) {
                BigDecimal.ZERO -> ""
                else -> formatInteger(value, prefix)
            }
            setTextSilently(formattedString)
        }

        fun setTextSilently(s: String) {
            removeTextChangedListener(this)
            setText(s)
            handleSelection(prefix)
            addTextChangedListener(this)
        }
    })
}

private fun getCleanValue(s: String): BigDecimal {
    val cleanString = s.replace("[^\\.0123456789]".toRegex(), "")

    val value = when {
        cleanString.isEmpty() -> BigDecimal.ZERO
        else -> try {
            BigDecimal(cleanString)
        } catch (ignore: NumberFormatException) {
            BigDecimal.ZERO
        }
    }
    return value
}

private fun formatInteger(value: BigDecimal, prefix: String = ""): String {
    return "${integerCashFmt.format(value)} $prefix"
}

private fun EditText.handleSelection(prefix: String = "") {
    setSelection(text.toString().replace(" $prefix", "").length)
}

fun EditText.addIntegerCashWatcher(
    minValue: BigDecimal,
    maxValue: BigDecimal,
    prefix: String = ""
) {
    /*setText(when {
        minValue <= BigDecimal.ZERO -> ""
        else -> "${integerCashFmt.format(minValue)} $prefix"
    })*/
    maxEms = 256
    inputType =
        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
    addTextChangedListener(object : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            val value = getCleanValue(s.toString())
            val formattedString = when {
                value == BigDecimal.ZERO -> ""
                value > maxValue -> formatInteger(maxValue, prefix)
                else -> formatInteger(value, prefix)
            }

            setTextSilently(formattedString)
        }

        private fun setTextSilently(s: String) {
            removeTextChangedListener(this)
            setText(s)
            handleSelection(prefix)
            addTextChangedListener(this)
        }
    })
}

fun EditText.listenTextChanged(block: (String) -> Unit) {
    this.addTextChangedListener(object : WrappedTextWatcher() {
        override fun onTextChanged(s: String) {
            val text = this@listenTextChanged.text.toString()
            block(text)
        }
    })
}

