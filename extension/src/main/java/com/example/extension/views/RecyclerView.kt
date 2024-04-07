package com.example.extension.views

import androidx.recyclerview.widget.RecyclerView

interface DragListener {

    fun onLeftDrag()

    fun onRightDrag()

    fun onUpDrag()

    fun onDownDrag()
}

interface ScrollListener {

    fun onScrolling()

    fun onStopScroll()
}


fun RecyclerView.addDragListener(listener: DragListener?) {
    if (listener == null) {
        clearOnScrollListeners()
        return
    }
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            when {
                dx < 0 -> {
                    println("Scrolled Left")
                    listener.onLeftDrag()
                }

                dx > 0 -> {
                    println("Scrolled Right")
                    listener.onRightDrag()
                }

                else -> {
                    println("No Horizontal Scrolled")
                }
            }
            when {
                dy < 0 -> {
                    println("Scrolled Upwards")
                    listener.onUpDrag()
                }

                dy > 0 -> {
                    println("Scrolled Downwards")
                    listener.onDownDrag()
                }

                else -> {
                    println("No Vertical Scrolled")
                }
            }
        }
    })
}

fun RecyclerView.addScrollListener(listener: ScrollListener?) {
    if (listener == null) {
        clearOnScrollListeners()
        return
    }
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    println("Not scrolling")
                    listener.onStopScroll()
                }

                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    println("Scrolling now")
                    listener.onScrolling()
                }
            }
        }
    })
}