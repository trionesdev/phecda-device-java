package com.trionesdev.phecda.device.contracts.go

import java.util.concurrent.CountDownLatch

class WaitGroup {
    private var latch: CountDownLatch? = null
    private var count = 0

    fun waitGroup() {
        this.latch = CountDownLatch(1)
    }

    fun add(delta: Int) {
        count += delta
    }

    fun done() {
        count -= 1
    }

    fun await() {
        try {
            latch?.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}