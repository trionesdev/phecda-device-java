package com.trionesdev.phecda.device.sdk.autoevent

import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.common.CommonConstants
import com.trionesdev.phecda.device.contracts.common.CommonConstants.COMMAND
import com.trionesdev.phecda.device.contracts.common.CommonConstants.NAME
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
                this.identifier = autoEvent.identifier
                this.onChange = autoEvent.onChange == true
                this.duration = duration
                this.stop = false
            }
        }

        fun readProperty(e: Executor, dic: Container): Event? {
            val vars: MutableMap<String, String?> = HashMap(2)
            vars[NAME] = e.deviceName
            vars[COMMAND] = e.identifier
            return ApplicationCommand.getCommand(e.deviceName!!, e.identifier!!, "", dic)
        }
    }

    private var deviceName: String? = null
    private var identifier: String? = null
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
                    evt = readProperty(executor, dic)
                } catch (e: Exception) {
                    log.error(
                        "AutoEvent - error occurs when reading resource {}: {}",
                        identifier,
                        e.message,
                        e
                    )
                }
                try {
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
                            it.identifier,
                            correlationId
                        )
                    } ?: let {
                        log.debug(
                            "AutoEvent - no event generated when reading resource {}",
                            identifier
                        )
                    }
                }catch (e: Exception){
                    log.error(
                        "AutoEvent - error occurs when reading resource {}: {}",
                        identifier,
                        e.message,
                        e
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
            val lastReading = lastReadings!![reading.identifier!!]
            lastReading?.let {
                if (CommonConstants.VALUE_TYPE_BINARY == reading.valueType) {
                    val checksum = xxHash64.hash(reading.binaryValue, 0, reading.binaryValue!!.size, 0)
                    if (lastReading != checksum) {
                        lastReadings!![reading.identifier!!] = checksum
                        result = false
                    }
                } else {
                    if (lastReading != reading.value!!) {
                        lastReadings!![reading.identifier!!] = reading.value!!
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
            if (CommonConstants.VALUE_TYPE_BINARY == reading.valueType) {
                reading.identifier?.let {
                    lastReadings!!.put(
                        it,
                        xxHash64.hash(reading.binaryValue, 0, reading.binaryValue?.size ?: 0, 0)
                    )
                }
            } else {
                lastReadings!![reading.identifier!!] = reading.value!!
            }
        }
    }
}