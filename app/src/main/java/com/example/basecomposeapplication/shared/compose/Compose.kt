package com.example.basecomposeapplication.shared.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import timber.log.Timber

class Ref(var value: Int)

@Composable
inline fun LogCompositions(msg: String) {
    val ref = remember { Ref(0) }
    SideEffect { ref.value++ }
    Timber.tag("RecompositionLog").d("Compositions: " + msg + " " + ref.value)
}