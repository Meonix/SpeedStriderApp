package com.example.extension.views

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.extension.objects.color

fun Activity.isAppRunning(packageName: String): Boolean {
    return (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.runningAppProcesses?.any {
        it.processName == packageName
    } ?: false
}

fun Activity.requestPermissionsApp(lstPermission: Array<String>, requestCode: Int) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(this, lstPermission, requestCode)
    }
}

@Suppress("DEPRECATION")
fun Activity?.setDarkColorStatusBar(isDark: Boolean = true) {
    this?.window?.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        statusBarColor = Color.TRANSPARENT
        if (isDark) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                return
            }
            this.apply {
                statusBarColor = Color.BLACK
            }
        }
    }
}

fun Activity?.setTransparentStatusBar(
    isTransparent: Boolean = true,
    @ColorRes idColor: Int = android.R.color.white
) {
    this ?: return
    if (isTransparent) {
        val w: Window = window
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        w.statusBarColor = color(android.R.color.transparent)
        this.setNavigationBarColor(android.R.color.transparent)
        WindowCompat.setDecorFitsSystemWindows(w, false)
        return
    }
    val w: Window = window
    w.statusBarColor = color(idColor)
    this.setNavigationBarColor(idColor)
}

fun Activity?.setNavigationBarColor(@ColorRes idColor: Int = android.R.color.white) {
    this ?: return
    val w: Window = window
    w.navigationBarColor = color(idColor)
}

fun Activity?.hideSystemUI() {
    this?.window?.let { window ->
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

fun Activity?.showSystemUI() {
    this?.window?.let { window ->
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.systemBars())
    }
}


fun Activity?.turnoffNativeToolBar() {
    try {
        this?.actionBar?.hide()
    } catch (e: NullPointerException) {
        e.printStackTrace()
    }
}

fun Activity?.turnoffSystemNotificationStatus() {
    this?.let {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

fun Activity?.turnoffSystemNavigationBar() {
    this?.let {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

fun Activity?.getAppWidthScreen(): Int {
    return this?.resources?.displayMetrics?.widthPixels ?: 0
}

fun Activity?.heightScreen(): Int {
    return this?.resources?.displayMetrics?.heightPixels ?: 0
}

@Suppress("DEPRECATION")
fun Activity?.getHeightScreen(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        heightScreenFromAPI30R()
    } else {
        this ?: return heightScreen()
        val display = this.windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        if (size.y > 0) {
            size.y
        } else {
            heightScreen()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun Activity?.heightScreenFromAPI30R(): Int {
    val windowMetrics: WindowMetrics? = this?.windowManager?.currentWindowMetrics
    return windowMetrics?.bounds?.height() ?: heightScreen()
}

fun Activity.openCHPlay() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.data = Uri.parse("market://details?id=${packageName}")
        })
    } catch (e: ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.data = Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")
        })
    }
}

fun Activity.realPathFromURI(uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val cursor: Cursor = contentResolver.query(uri, projection, null, null, null)
        ?: return uri.path
    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
    cursor.moveToFirst()
    return cursor.getString(columnIndex)
}