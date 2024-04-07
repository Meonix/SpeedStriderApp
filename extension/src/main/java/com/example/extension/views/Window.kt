package com.example.extension.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.WindowMetrics
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

/**
 * [Activity] extensions
 */
val Activity.screenWidth: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

val Activity.screenHeight: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

val Activity.screenRatio: Float
    get() {
        return screenHeight.toFloat() / screenWidth
    }

/**
 * Set status bar color
 */
@SuppressLint("ObsoleteSdkInt")
fun Window.statusBarColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = color
    }
}

fun Activity.statusBarColor(@ColorInt color: Int) {
    window.statusBarColor(color)
}

fun Fragment.statusBarColor(@ColorInt color: Int) {
    activity?.statusBarColor(color)
}

fun DialogFragment.statusBarColor(@ColorInt color: Int) {
    dialog?.window?.statusBarColor(color)
}

/**
 * Set navigation bar color
 */

fun Window.navBarColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        navigationBarColor = color
    }
}

fun Activity.navBarColor(@ColorInt color: Int) {
    window.navBarColor(color)
}

fun Fragment.navBarColor(@ColorInt color: Int) {
    activity?.navBarColor(color)
}

fun DialogFragment.navBarColor(@ColorInt color: Int) {
    dialog?.window?.navBarColor(color)
}

/**
 * Set light status bar widgets
 */
fun Window.lightStatusBarWidgets() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
        return
    }
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val flags = decorView.systemUiVisibility
        decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}

fun Activity.lightStatusBarWidgets() {
    window.lightStatusBarWidgets()
}

fun Fragment.lightStatusBarWidgets() {
    activity?.lightStatusBarWidgets()
}

fun DialogFragment.lightStatusBarWidgets() {
    dialog?.window?.lightStatusBarWidgets()
}

/**
 * Set dark status bar widgets
 */
fun Window.darkStatusBarWidgets() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
        return
    }
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val flags = decorView.systemUiVisibility
        decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity.darkStatusBarWidgets() {
    window.darkStatusBarWidgets()
}

fun Fragment.darkStatusBarWidgets() {
    activity?.darkStatusBarWidgets()
}

fun DialogFragment.darkStatusBarWidgets() {
    dialog?.window?.darkStatusBarWidgets()
}

/**
 * Set light navigation bar widgets
 */
fun Window.lightNavBarWidgets() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
        return
    }
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val flags = decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }
}

fun Activity.lightNavBarWidgets() {
    window.lightNavBarWidgets()
}

fun Fragment.lightNavBarWidgets() {
    activity?.lightNavBarWidgets()
}

fun DialogFragment.lightNavBarWidgets() {
    dialog?.window?.lightNavBarWidgets()
}

/**
 * Set dark navigation bar widgets
 */
fun Window.darkNavBarWidgets() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
        return
    }
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val flags = decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }
}

fun Activity.darkNavBarWidgets() {
    window?.darkNavBarWidgets()
}

fun Fragment.darkNavBarWidgets() {
    activity?.darkNavBarWidgets()
}

fun DialogFragment.darkNavBarWidgets() {
    dialog?.window?.darkNavBarWidgets()
}

/**
 *  override fun onWindowFocusChanged(hasFocus: Boolean) {
 *      super.onWindowFocusChanged(hasFocus)
 *  }
 */
/**
 * Using for notch or non-Notch Device
 */
fun Activity.safeHideSystemUI(typeUI: Int = WindowInsetsCompat.Type.systemBars()) {
    val supportsAPILevel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    // Configure the behavior of the hidden system bars.
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    // Configure the layout to fit system windows.
    WindowCompat.setDecorFitsSystemWindows(
        window,
        !supportsAPILevel
    )

    // Hide the status bar.
    val windowInsetsCompat = WindowInsetsControllerCompat(window, window.decorView)
    windowInsetsCompat.hide(typeUI)

    // Configure the fullscreen mode.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        val attrib = window.attributes
        attrib.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    } else {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}

fun Activity.showSystemUI(typeUI: Int = WindowInsetsCompat.Type.systemBars()) {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    // Configure the layout to fit system windows.
    WindowCompat.setDecorFitsSystemWindows(
        window,
        true
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        val attrib = window.attributes
        attrib.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
    }
    window.configScreenToFitSystemWindows()

    val windowInsetsCompat = WindowInsetsControllerCompat(window, window.decorView)
    windowInsetsCompat.show(typeUI)
}

fun Window.configScreenToFitSystemWindows() {
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}

fun Activity.windowFullScreen() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
    )
}

fun Activity.windowSafeArea() {
    window.setFlags(
        0,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
    )
}

/**
 * [Fragment] extensions
 */
fun Fragment.windowFullScreen() {
    activity?.windowFullScreen()
}

fun Fragment.windowSafeArea() {
    activity?.windowSafeArea()
}

/**
 * [Context] extensions
 */
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

// used for hide keyboard in BottomSheetFragment
fun Context.hideKeyboardToggleInput() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}