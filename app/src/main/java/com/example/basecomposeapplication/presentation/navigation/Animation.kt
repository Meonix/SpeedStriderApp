package com.example.basecomposeapplication.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavBackStackEntry

const val SHORT_DURATION = 200
const val MEDIUM_DURATION = 500
const val LONG_DURATION = 800

/**
 * slide transition
 */
fun AnimatedContentTransitionScope<NavBackStackEntry>.enterSlideTransition(): EnterTransition {
    return this.slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(SHORT_DURATION)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.exitSlideTransition(): ExitTransition {
    return this.slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(SHORT_DURATION)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.popInSlideTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
        animationSpec = tween(SHORT_DURATION)
    )
}


fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitSlideTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
        animationSpec = tween(SHORT_DURATION)
    )
}

/**
 * Zoom transition
 */
fun AnimatedContentTransitionScope<NavBackStackEntry>.enterZoomTransition(): EnterTransition {
    return scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(SHORT_DURATION)
    ) + fadeIn(animationSpec = tween(SHORT_DURATION))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.exitZoomTransition(): ExitTransition {
    return scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(SHORT_DURATION)
    ) + fadeOut(animationSpec = tween(SHORT_DURATION))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterZoomTransition(): EnterTransition {
    return scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(SHORT_DURATION)
    ) + fadeIn(animationSpec = tween(SHORT_DURATION))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitZoomTransition(): ExitTransition {
    return scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(SHORT_DURATION)
    ) + fadeOut(animationSpec = tween(SHORT_DURATION))
}

/**
 * Zoom fade and slide from right transition
 */

fun AnimatedContentTransitionScope<NavBackStackEntry>.enterComplexTransition(): EnterTransition {
    return scaleIn(
        initialScale = 0.5f,
        animationSpec = tween(SHORT_DURATION)
    ) + fadeIn(animationSpec = tween(SHORT_DURATION)) + slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(SHORT_DURATION)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.exitComplexTransition(): ExitTransition {
    return scaleOut(
        targetScale = 0.5f,
        animationSpec = tween(SHORT_DURATION)
    ) + fadeOut(animationSpec = tween(SHORT_DURATION)) + slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(SHORT_DURATION)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterComplexTransition(): EnterTransition {
    return scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(SHORT_DURATION)
    ) + fadeIn(animationSpec = tween(SHORT_DURATION)) + slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
        animationSpec = tween(SHORT_DURATION)
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitComplexTransition(): ExitTransition {
    return scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(SHORT_DURATION)
    ) + fadeOut(animationSpec = tween(SHORT_DURATION)) + slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
        animationSpec = tween(SHORT_DURATION)
    )
}

