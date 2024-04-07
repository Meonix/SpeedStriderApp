package com.example.extension.worker

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.extension.objects.integerCashFmt
import java.math.BigDecimal

abstract class SimpleTextWatcher : TextWatcher {

    override fun afterTextChanged(s: Editable) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}

abstract class WrappedTextWatcher : SimpleTextWatcher() {

    private var saveText: String? = null

    private var ignore: Boolean = false

    open fun EditText.setTextSilently(s: String?) {
        ignore = true
        setText(s)
        handleSelection()
        ignore = false
    }

    open fun EditText.handleSelection() {
        setSelection(text.length)
    }

    final override fun afterTextChanged(s: Editable) {
        if (saveText == s.toString()) return
        if (ignore) return
        onTextChanged(s.toString())
    }

    final override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        saveText = s.toString()
    }

    abstract fun onTextChanged(s: String)
}

fun BigDecimal?.getCashString(prefix: String = ""): String {
    this ?: return " $prefix"
    return "${integerCashFmt.format(this)} $prefix"
}

/**
 *
 */

class CardNumberWatcher : SimpleTextWatcher() {
    private val space get() = ' '
    override fun afterTextChanged(s: Editable) {
        // Remove spacing char
        if (s.isNotEmpty() && s.length % 5 == 0) {
            val c = s[s.length - 1]
            if (space == c) {
                s.delete(s.length - 1, s.length)
            }
        }
        // Insert char where needed.
        if (s.isNotEmpty() && s.length % 5 == 0) {
            val c = s[s.length - 1]
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(s.toString(), space.toString()).size <= 3) {
                s.insert(s.length - 1, space.toString())
            }
        }
    }
}

class OTPNumberWatcher : SimpleTextWatcher() {
    private val space get() = "     "
    override fun afterTextChanged(s: Editable) {
        // Remove spacing char'
        val realLength = s.toString().replace(space, "").length
        if (s.isNotEmpty() && (s.length - realLength) % space.length == 0) {
            val c = s[s.length - 1]
            if (c == ' ') {
                if (s.length - space.length < 0)
                    return
                else
                    s.delete(s.length - space.length, s.length)
            }
        }
        // Insert char where needed. 1 5
        val bool = (s.length - 2) % (space.length + 1) == 0
        if (s.isNotEmpty() && bool) {
            val c = s[s.length - 1]
            if (Character.isDigit(c) && TextUtils.split(s.toString(), space).size <= 5) {
                s.insert(s.length - 1, space)
            } else {
                s.delete(s.length - 1, s.length)
            }
        }
    }
}

/**
 *  addTextWatcher to listener one time
 */
fun EditText.addTextWatcherListener(lifecycle: Lifecycle, textWatcher: TextWatcher) {
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            addTextChangedListener(textWatcher)
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            removeTextChangedListener(textWatcher)
        }
    })
}

