package com.example.extension.worker

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  These variables are used to keep track of the ID of the last clicked view,
 *  the ID of the last event, and the time of the last click, respectively.
 */
private var lastClickViewId: Int = 0

private var lastEventId: Int = -2

private var lastClickTime: Long = System.currentTimeMillis()

/**
 * The [delayedInterval] parameter is used to specify a time delay between consecutive clicks
 * on the same view, and the [eventId] parameter is used to specify a unique identifier for the click event.
 */
abstract class ViewClickListener(
    private val delayedInterval: Long = 300,
    private val eventId: Int = 1
) : View.OnClickListener {
    /**
     * a volatile Boolean property called "[onTrigger]" which is used to indicate
     * if the click event is currently being processed
     */
    @Volatile
    var onTrigger: Boolean = false

    /**
     * A getter method called "[isDelayed]" is used to check
     * if the time elapsed since the last click is greater than the delayedInterval.
     */
    private val isDelayed: Boolean get() = System.currentTimeMillis() - lastClickTime > delayedInterval

    private val triggerMap = mutableMapOf<Int, Job>()

    /**
     *  A getter method called "[wasClickedOnDifferenceView]" is used to check if the ID of the view
     *  being clicked is different than the ID of the last clicked view.
     */
    private val View.wasClickedOnDifferenceView: Boolean get() = id != lastClickViewId

    abstract fun onClicks(v: View)

    final override fun onClick(v: View?) {
        v ?: return
        if (eventId > 0 && eventId == lastEventId) return
        if (onTrigger) return
        /** there are two cases where view click event is accepted
         * 1: The view the user clicked on was different from the previous view was clicked
         * 2: The view have been delayed
         */
        if (v.wasClickedOnDifferenceView || isDelayed) {
            onTrigger = true
            lastClickViewId = v.id
            lastClickTime = System.currentTimeMillis()
            lastEventId = eventId
            onClicks(v)
            triggerMap[v.id]?.cancel(null)
            triggerMap[v.id] = CoroutineScope(Dispatchers.IO).launch {
                delay(delayedInterval)
                lastEventId = -2
                onTrigger = false
            }
        }
    }

}

fun View?.addClickListener(
    delayedInterval: Long,
    eventId: Int,
    listener: ((View?) -> Unit)? = null
) {
    this ?: return
    if (listener == null) {
        setOnClickListener(null)
        if (this is EditText) {
            isFocusable = true
            isCursorVisible = true
        }
        return
    }
    setOnClickListener(object : ViewClickListener(delayedInterval, eventId) {
        override fun onClicks(v: View) {
            listener(v)
        }
    })
    if (this is EditText) {
        isFocusable = false
        isCursorVisible = false
    }
}

fun View?.addClickListener(delayedInterval: Long, listener: ((View?) -> Unit)? = null) {
    addClickListener(delayedInterval, 1, listener)
}

fun View?.addClickListener(listener: ((View?) -> Unit)? = null) {
    addClickListener(360, 1, listener)
}

fun addClickListeners(vararg views: View?, block: (View?) -> Unit) {
    val listener = object : ViewClickListener() {
        override fun onClicks(v: View) {
            block(v)
        }
    }
    views.forEach {
        it?.setOnClickListener(listener)
    }
}

fun clearClickListeners(vararg views: View?) {
    views.forEach {
        it?.setOnClickListener(null)
    }
}

abstract class FastClickListener(private val clickCount: Int) : View.OnClickListener {

    private var lastClickTime: Long = 0

    private var currentClickCount: Int = 0

    abstract fun onViewClick(v: View?)

    final override fun onClick(v: View?) {
        if (System.currentTimeMillis() - lastClickTime > 420 || currentClickCount >= clickCount) {
            currentClickCount = 0
        }
        lastClickTime = System.currentTimeMillis()
        currentClickCount++
        if (currentClickCount == clickCount) {
            lastClickTime = 0
            currentClickCount = 0
            onViewClick(v)
        }
    }
}

fun View?.addFastClickListener(clickCount: Int, block: () -> Unit) {
    this?.setOnClickListener(object : FastClickListener(clickCount) {
        override fun onViewClick(v: View?) {
            block()
        }
    })
}

fun View.onClickThrottled(skipDurationMillis: Long = 850, action: () -> Unit) {
    var isEnabled = true
    this.setOnClickListener {
        if (isEnabled) {
            action()
            isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({ isEnabled = true }, skipDurationMillis)
        }
    }
}
