package com.example.extension.worker


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

enum class PermissionAction {
    NOTIFICATION,
    ALL_SETTING_DETAIL,
    LOCATION_DEVICE
}

fun getPermissionAction(permissionAction: PermissionAction): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return when (permissionAction) {
            PermissionAction.NOTIFICATION -> {
                Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }

            PermissionAction.LOCATION_DEVICE -> {
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            }

            PermissionAction.ALL_SETTING_DETAIL -> {
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            }

            else -> ""
        }
    } else {
        return when (permissionAction) {
            PermissionAction.NOTIFICATION -> {
                "android.settings.APP_NOTIFICATION_SETTINGS"
            }

            PermissionAction.ALL_SETTING_DETAIL -> {
                "android.settings.APPLICATION_DETAILS_SETTINGS"
            }

            PermissionAction.LOCATION_DEVICE -> {
                "android.settings.LOCATION_SOURCE_SETTINGS"
            }

            else -> ""
        }
    }
}

fun Fragment.canShowPermission(permission: String) =
    !ActivityCompat.shouldShowRequestPermissionRationale(
        requireActivity(),
        permission
    )

fun Fragment.registerSinglePermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): ActivityResultLauncher<String> {
    return registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            onGranted.invoke()
        } else {
            onDenied.invoke()
        }
    }
}

fun Fragment.autoHandleSinglePermission(
    permission: String,
    onGranted: () -> Unit,
    permissionResult: ActivityResultLauncher<String>
) {
    context?.let { context ->
        if (context.isGranted(permission)) {
            onGranted.invoke()
        } else {
            permissionResult.launch(permission)
        }
    }
}

fun Fragment.registerMultiPermission(
    onGrantedList: (Array<String>) -> Unit,
    onDeniedList: (Array<String>) -> Unit,
): ActivityResultLauncher<Array<String>> {
    val grantedList = mutableListOf<String>()
    val deniedList = mutableListOf<String>()
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultList ->
        resultList.entries.forEach {
            if (it.value) {
                grantedList.add(it.key)
            } else {
                deniedList.add(it.key)
            }
        }
        onGrantedList.invoke(grantedList.toTypedArray())
        onDeniedList.invoke(deniedList.toTypedArray())
    }
}


fun Fragment.autoHandleMultiPermission(
    vararg permissions: String,
    onGrantedList: (Array<String>) -> Unit,
    permissionMultiResult: ActivityResultLauncher<Array<String>>
) {
    val grantedList = mutableListOf<String>()
    val notGrantedList = mutableListOf<String>()
    context?.let { context ->
        for (permission in permissions) {
            if (context.isGranted(permission)) {
                grantedList.add(permission)
            } else {
                notGrantedList.add(permission)
            }
        }
        onGrantedList.invoke(grantedList.toTypedArray())
        permissionMultiResult.launch(notGrantedList.toTypedArray())
    }
}


fun Context.isGranted(vararg permissions: String): Boolean {
    permissions.iterator().forEach {
        if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

/**
 *  permission Array available flags
 *  Using:
val permission = permissionMediaStoreArr()
if (context?.isGranted(*permission) == true) {
onGranted()
} else {
permissionHandler?.let {
launchPermission(
permissions = permission,
onGranted = ::onGranted,
onDenied = ::onDenied,
permissionMultiResult = it
)
}
}
 */

fun permissionCameraArr() = arrayOf(Manifest.permission.CAMERA)

fun permissionMediaStoreArr(): Array<String> {
    val permissionList = mutableListOf<String>()
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            permissionList.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissionList.add(Manifest.permission.READ_MEDIA_VIDEO)
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionList.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
        }

        else -> {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
    return permissionList.toTypedArray()
}

fun permissionLocationArr(): Array<String> = arrayOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)

fun permissionLocationBackgroundArr(): Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else {
        arrayOf()
    }

fun permissionNotificationArr(): Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        arrayOf()
    }

