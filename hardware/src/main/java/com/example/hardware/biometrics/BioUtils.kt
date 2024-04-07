package com.example.hardware.biometrics

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.extension.objects.showToastShort
import java.util.concurrent.Executor
import javax.crypto.Cipher

var biometricCallback: BiometricPrompt.AuthenticationCallback? = null

fun Context.bioAvailable(): Boolean {
    return when (getBiometricStatus()) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false

        else -> false
    }
}

fun Context.getBiometricStatus(): Int {
    val biometricManager = BiometricManager.from(this)
    val authenticationTypes = BIOMETRIC_WEAK or DEVICE_CREDENTIAL
    return biometricManager.canAuthenticate(authenticationTypes)
}

//fun SettingsFragment.showErrorBiometric() {
//    when (com.example.hardware.biometrics.getBiometricStatus()) {
//        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
//            errorToast(message = str(R.string.fingerprint_disable))
//        }
//        else -> {
//            toast(str(R.string.setting_view_something_went_wrong))
//        }
//    }
//}

fun Context.cancelAuthentication(
    fragment: Fragment,
    callBack: BiometricPrompt.AuthenticationCallback
) {
    val executor: Executor = ContextCompat.getMainExecutor(this)
    biometricCallback = null
    BiometricPrompt(fragment, executor, callBack).cancelAuthentication()
}

fun promptInfo(title: String, cancelLbl: String): BiometricPrompt.PromptInfo {
    return BiometricPrompt.PromptInfo.Builder().apply {
        setTitle(title)
        setSubtitle("")
        setConfirmationRequired(false)
        setNegativeButtonText(cancelLbl)
    }.build()
}

fun promptInfoWithPin(title: String, cancelLbl: String): BiometricPrompt.PromptInfo {
    return BiometricPrompt.PromptInfo.Builder().apply {
        setTitle(title)
        setSubtitle("")
        setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        setConfirmationRequired(false)
    }.build()
}

fun Context.authenticationWithFingerprint(
    fragment: Fragment,
    listener: BiometricPrompt.AuthenticationCallback,
    promptInfo: BiometricPrompt.PromptInfo,
    cipher: Cipher
) {
    try {
        val biometricPrompt: BiometricPrompt
        val executor: Executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(fragment, executor, listener)

        biometricCallback = listener
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    } catch (e: Exception) {
        e.printStackTrace()
        showToastShort(e.message)
    }
}

fun Context.authenticationWithFaceId(
    fragment: Fragment,
    listener: BiometricPrompt.AuthenticationCallback
) {
    val biometricPrompt: BiometricPrompt
    val executor: Executor = ContextCompat.getMainExecutor(this)
    biometricPrompt = BiometricPrompt(fragment, executor, listener)

    val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Enable Face ID for application")
        .setSubtitle("Do you want to allow this app to use Face ID to verify your identity?")
        .setNegativeButtonText("Cancel")
        .setConfirmationRequired(true)
        .build()

    biometricPrompt.authenticate(promptInfo)
}
