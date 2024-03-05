package com.trionesdev.phecda.device.sdk.autoevent

import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.common.CommonConstants
import com.trionesdev.phecda.device.contracts.common.CommonConstants.Command
import com.trionesdev.phecda.device.contracts.common.CommonConstants.Name
import com.trionesdev.phecda.device.contracts.go.WaitGroup
import com.trionesdev.phecda.device.contracts.model.AutoEvent
import com.trionesdev.phecda.device.contracts.model.Event
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading
import com.trionesdev.phecda.device.sdk.application.ApplicationCommand
import com.trionesdev.phecda.device.sdk.common.SdkCommonUtils
import net.jpountz.xxhash.XXHash64
import net.jpountz.xxhash.XXHashFactory
import java.util.*
import kotlin.time.Duration

@Slf4j
class Executor {
    companion object {
        fun newExecutor(deviceName: String, autoEvent: AutoEvent): Executor {
            val duration = Duration.parse( autoEvent.interval!!)
            return Executor().apply {
                this.deviceName = deviceName
                this.sourceName = autoEvent.sourceName
                this.onChange = autoEvent.onChange == true
                this.duration = duration
                this.stop = false
            }
        }

        fun readResource(e: Executor, dic: Container): Event? {
            val vars: MutableMap<String, String?> = HashMap(2)
            vars[Name] = e.deviceName
            vars[Command] = e.sourceName
            return ApplicationCommand.getCommand(e.deviceName!!, e.sourceName!!, "", false, dic)
        }
    }

    private var deviceName: String? = null
    private var sourceName: String? = null
    private var onChange: Boolean = false
    private var lastReadings: MutableMap<String, Any>? = null
    private var duration: Duration? = null
    private var stop: Boolean = false
    private var timer: Timer? = null
    private val xxHash64: XXHash64 = XXHashFactory.fastestInstance().hash64()

    fun run(wg: WaitGroup, dic: Container) {
        val executor = this
        timer = Timer()

        val task = object : TimerTask() {
            override fun run() {
                var evt: Event? = null
                try {
                    evt = readResource(executor, dic)
                } catch (e: Exception) {
                    log.error(
                        "AutoEvent - error occurs when reading resource {}: {}",
                        sourceName,
                        e.message,
                        e
                    )
                }
                evt?.let {
                    if (onChange) {
                        if (!compareReadings(it.readings!!)) {
                            log.debug("AutoEvent - readings are the same as previous one")
                        }
                    }
                    val correlationId = UUID.randomUUID().toString()
                    SdkCommonUtils.sendEvent(it, correlationId, dic)
                    log.trace(
                        "AutoEvent - Sent new Event/Reading for '{}' source with Correlation Id '{}'",
                        it.sourceName,
                        correlationId
                    )
                } ?: let {
                    log.debug(
                        "AutoEvent - no event generated when reading resource {}",
                        sourceName
                    )
                }
            }
        }
        timer?.schedule(task, duration!!.inWholeSeconds * 1000, duration!!.inWholeSeconds * 1000)
    }

    fun stop() {
        timer?.let {
            timer?.cancel()
            timer = null
        }
        stop = true
    }

    fun compareReadings(readings: MutableList<BaseReading>): Boolean {
        if (lastReadings?.size != readings.size) {
            renewLastReadings(readings)
            return false
        }
        var result = true
        for (reading in readings) {
            val lastReading = lastReadings!![reading.resourceName!!]
            lastReading?.let {
                if (CommonConstants.ValueTypeBinary == reading.valueType) {
                    val checksum = xxHash64.hash(reading.binaryValue, 0, reading.binaryValue!!.size, 0)
                    if (lastReading != checksum) {
                        lastReadings!![reading.resourceName!!] = checksum
                        result = false
                    }
                } else {
                    if (lastReading != reading.value!!) {
                        lastReadings!![reading.resourceName!!] = reading.value!!
                        result = false
                    }
                }
            } ?: let {
                renewLastReadings(readings)
                return false
            }
        }
        return result
    }

    fun renewLastReadings(readings: MutableList<BaseReading>) {
        lastReadings = mutableMapOf()
        for (reading in readings) {
            if (CommonConstants.ValueTypeBinary == reading.valueType) {
                reading.resourceName?.let {
                    lastReadings!!.put(
                        it,
                        xxHash64.hash(reading.binaryValue, 0, reading.binaryValue?.size ?: 0, 0)
                    )
                }
            } else {
                lastReadings!![reading.resourceName!!] = reading.value!!
            }
        }
    }
}