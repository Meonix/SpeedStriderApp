package com.example.extension.worker.coutdown

open class SecondCountdownTimer(intervalMillis: Long = 10000) :
    CoroutineCountdownTimer(intervalMillis) {
    override fun onTicks(remainMillis: Long) {
        val seconds = remainMillis / 1000
        val s = "%02d:%02d".format(seconds / 60, seconds % 60)
    }
}