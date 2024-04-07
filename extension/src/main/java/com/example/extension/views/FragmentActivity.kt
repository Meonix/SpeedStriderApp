package com.example.extension.views

import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.inputModeAdjustResize() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.decorView.setOnApplyWindowInsetsListener { _, windowInsets ->
            val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
            window.decorView.setPadding(0, 0, 0, imeHeight)
            windowInsets
        }
    } else {
        @Suppress("DEPRECATION")
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}

fun FragmentActivity.inputModeAdjustNothing() {
    val frags = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
    window.setSoftInputMode(frags)
}


fun FragmentActivity.dismissDialog(tag: String) {
    try {
        val fragment =
            supportFragmentManager.findFragmentByTag(tag) as? DialogFragment
        fragment?.dialog?.dismiss()
    } catch (e: Exception) {
        println(e.message)
    }
}

fun FragmentActivity.dismissAllDialogs() {
    try {
        val sfm = this.supportFragmentManager
        sfm.fragments.forEach {
            if (it is DialogFragment) {
                it.dismissAllowingStateLoss()
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
}

fun FragmentActivity.dismissAllExcept(fragmentName: String) {
    try {
        supportFragmentManager.fragments.forEach {
            if (it is DialogFragment && it::class.java.name != fragmentName) {
                it.dismissAllowingStateLoss()
            }
        }
    } catch (ex: Exception) {
        println(ex.message)
    }
}