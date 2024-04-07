package com.example.extension.views

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

fun View?.hideKeyboard() {
    this?.post {
        (context as? Activity)?.hideKeyboard()
    }

}

fun View?.showKeyboard() {
    this?.post {
        (context as? Activity)?.showKeyboard()
    }
}

fun Activity.hideKeyboard() {
    WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
    // DEPRECATED
    /*val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(
        findViewById(android.R.id.content),
        InputMethodManager.SHOW_IMPLICIT
    )*/
}

fun Activity.showKeyboard() {
    WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.ime())
    // DEPRECATED
    /*val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(findViewById(android.R.id.content), InputMethodManager.SHOW_IMPLICIT)*/
}

fun Fragment.hideKeyboard() {
    requireActivity().hideKeyboard()
}

fun Fragment.showKeyboard() {
    requireActivity().showKeyboard()
}

private var keypadHeight: Int = 0

fun AppCompatActivity.listenKeyboard() {
    val contentView = this.findViewById<View>(android.R.id.content)
    val layoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        contentView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = contentView.rootView.height
        val currentKeypadHeight = screenHeight - rect.bottom
        if (keypadHeight == currentKeypadHeight) return@OnGlobalLayoutListener
        keypadHeight = currentKeypadHeight
        if (keypadHeight > screenHeight * 0.15) {
            Toast.makeText(this@listenKeyboard, "Keyboard is showing", Toast.LENGTH_SHORT).show()
            return@OnGlobalLayoutListener
        }
        Toast.makeText(this@listenKeyboard, "Keyboard is closed", Toast.LENGTH_SHORT).show()
    }
    contentView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
}