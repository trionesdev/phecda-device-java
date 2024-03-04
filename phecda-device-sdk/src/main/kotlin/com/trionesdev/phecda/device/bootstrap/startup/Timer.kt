package com.trionesdev.phecda.device.bootstrap.startup

import com.trionesdev.phecda.device.bootstrap.environement.Variables
import java.time.Duration
import java.time.Instant

class Timer {
    companion object{
        fun newStartUpTimer(serviceKey:String): Timer {
            val startup = Variables.getStartupInfo(serviceKey)
            return Timer().apply {
                startTime = Instant.now()
                duration = Duration.ofSeconds(startup.duration.toLong())
                interval = Duration.ofSeconds(startup.interval.toLong())
            }
        }
    }
    private var startTime: Instant? = null
    private var duration: Duration? = null
    private var interval: Duration? = null

    fun sinceAsString(): String {
        return Duration.between(startTime, Instant.now()).toString()
    }
    fun hasNotElapsed(): Boolean {
        return Instant.now().isBefore(startTime!!.plusSeconds(duration!!.seconds))
    }

    fun sleepForInterval() {
        Thread.sleep(interval!!.toMillis())
    }
}