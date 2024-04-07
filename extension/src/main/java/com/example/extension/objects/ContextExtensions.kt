package com.example.extension.objects

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TEXT_PLANT_TYPE = "text/plain"
private const val IMAGE_PNG_TYPE = "image/png"

fun Context?.isGps(): Boolean {
    this ?: return false
    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    return manager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
}

fun Context?.showToastShort(msg: String?) {
    this ?: return
    Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show()
}

fun Context?.showToastLong(msg: String?) {
    this ?: return
    Toast.makeText(this, msg.toString(), Toast.LENGTH_LONG).show()
}

fun Context?.copyText(text: String?, onCopyComplete: (() -> Unit)? = null) {
    this ?: return
    val clipboard: ClipboardManager? =
        this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText(packageName, text ?: "")
    clipboard?.let {
        it.setPrimaryClip(clip)
        onCopyComplete?.invoke()
    }
}

/**
 *  @param colorRes [ID resource]
 * */
fun Context.color(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

/**
 *  @param colorRes [ID resource]
 * */
fun Context.colorStateList(@ColorRes colorRes: Int) =
    ContextCompat.getColorStateList(this, colorRes)

/**
 *  @param drawableRes [ID resource]
 * */
fun Context.drawable(@DrawableRes drawableRes: Int) = ContextCompat.getDrawable(this, drawableRes)

/**
 *  @param dimenRes [ID resource]
 * */
fun Context.dimen(@DimenRes dimenRes: Int) = resources.getDimension(dimenRes)

/**
 *  @param attrRes [ID resource]
 * */
fun Context.typedValue(@AttrRes attrRes: Int): TypedValue {
    val outValue = TypedValue()
    theme.resolveAttribute(attrRes, outValue, true)
    return outValue
}

fun Context?.callPhoneNumber(phone: String) {
    this ?: return
    val intentDial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    startActivity(intentDial)
}

/**
 * Usage go to detail setting intent :
 * define these line on top level of the class
private val requestSettingDetail =
registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//handle activity result
}
 *  launch intent:
 *
 *  requestSettingDetail.launch(getSettingDetailIntentByAction(getPermissionAction()))
 */

fun Context?.getSettingDetailIntentByAction(action: String): Intent? {
    this ?: return null
    val intent = Intent()
    intent.action = action
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    } else {
        intent.putExtra("app_package", packageName)
        intent.putExtra("app_uid", applicationInfo?.uid)
    }
    return intent
}

fun Context?.getSettingDetailIntent(): Intent? {
    this ?: return null
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    return intent
}

fun Context?.shareMessage(msg: String) {
    this ?: return
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, msg)
        type = TEXT_PLANT_TYPE
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}

fun Activity?.shareImage(uriToImage: Uri? = null, title: String = "") {
    this ?: return
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uriToImage)
        type = IMAGE_PNG_TYPE
    }
    startActivity(Intent.createChooser(shareIntent, title))
}

fun Context.showPermissionRequiredDialog(
    activity: Activity,
    permissionName: String
) {
    val alertDialog = MaterialAlertDialogBuilder(this)
    alertDialog.setTitle("Permission denied")
    alertDialog.setMessage("The first time you don't have permission to the ${permissionName}, you need permission at Settings")
    alertDialog.setPositiveButton("Ok") { _, _ ->
        startActivity(getSettingDetailIntent())
    }
    alertDialog.show()
}
