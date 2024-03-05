package com.trionesdev.phecda.device.sdk.transformer

import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeString
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeStruct
import com.trionesdev.phecda.device.contracts.errors.CommonPhedaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KindEntityDoesNotExist
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KindNaNError
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KindOverflowError
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KindServerError
import com.trionesdev.phecda.device.contracts.model.Event
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading
import com.trionesdev.phecda.device.contracts.model.reading.Reading
import com.trionesdev.phecda.device.contracts.model.reading.SimpleReading
import com.trionesdev.phecda.device.contracts.model.reading.StructReading
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.common.SdkCommonUtils
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct
import com.trionesdev.phecda.device.sdk.model.CommandValue
import com.trionesdev.phecda.device.sdk.transformer.TransformResult.NaN
import com.trionesdev.phecda.device.sdk.transformer.TransformResult.Overflow
import java.time.Instant
import java.util.*

object Transformer {
    fun commandValuesToEvent(
        cvs: MutableList<CommandValue>, deviceName: String, sourceName: String, dataTransform: Boolean, dic: Container
    ): Event? {
        if (cvs.isEmpty()) {
            return null
        }
        val device = Cache.devices()?.forName(deviceName) ?: let {
            throw CommonPhedaException.newCommonPhedaException(
                KindEntityDoesNotExist, String.format("failed to find device %s", deviceName), null
            )
        }
        var transformsOK = true
        val origin: Long = uniqueOrigin()
        val tags = mutableMapOf<String, Any>()
        val readings = mutableListOf<BaseReading>()

        cvs.forEachIndexed { index, cv ->
            if (Objects.isNull(cv)) {
                return@forEachIndexed
            }
            val dr = Cache.profiles()?.deviceResource(device.profileName!!, cv.deviceResourceName!!) ?: let {
                val msg = String.format(
                    "failed to find DeviceResource %s in Device %s for CommandValue (%s)",
                    cv.deviceResourceName,
                    deviceName,
                    cv.toString()
                )
                log.error(msg)
                throw CommonPhedaException.newCommonPhedaException(
                    KindEntityDoesNotExist, msg, null
                )
            }
            if (dataTransform) {
                try {
                    TransformResult.transformReadResult(cv, dr.properties!!)
                } catch (e: CommonPhedaException) {
                    when (e.kind) {
                        KindOverflowError -> {
                            cvs[index] =
                                CommandValue.newCommandValue(cv.deviceResourceName!!, ValueTypeString, Overflow)
                        }

                        KindNaNError -> {
                            cvs[index] = CommandValue.newCommandValue(cv.deviceResourceName!!, ValueTypeString, NaN)
                        }

                        else -> {
                            transformsOK = false
                        }
                    }
                }
            }
            if (!cv.tags.isNullOrEmpty()) {
                cv.tags.let { tags.putAll(it) }
            }
            val ro = Cache.profiles()?.resourceOperation(device.profileName!!, cv.deviceResourceName!!)
            ro?.let {
                if (it.mappings.isNullOrEmpty()) {
                    cvs[index] = TransformResult.mapCommandValue(cv, it.mappings)!!
                }
            }
            val reading = commandValueToReading(cv, deviceName, device.profileName!!, dr.properties?.mediaType, origin)
            val config = dic.getInstance(ConfigurationStruct::class.java)
            SdkCommonUtils.addReadingTags(reading)
            readings.add(reading!!)
        }
        if (transformsOK) {
            throw CommonPhedaException.newCommonPhedaException(
                KindServerError, String.format("failed to transform value for %s", deviceName), null
            )
        }
        if (!readings.isNullOrEmpty()) {
            val event = Event.newEvent(device.profileName, device.name, sourceName).apply {
                this.readings = readings
                this.origin = origin
                this.tags = tags
            }
            SdkCommonUtils.addEventTags(event)
            return event
        } else {
            return null
        }
    }

    private fun commandValueToReading(
        cv: CommandValue, deviceName: String, profileName: String, mediaType: String?, eventOrigin: Long
    ): BaseReading {

        val reading = when (cv.type) {
            ValueTypeStruct -> {
                StructReading.newStructReading(profileName, deviceName, cv.deviceResourceName, cv.type)
            }

            else -> {
                SimpleReading.newSimpleReading(profileName, deviceName, cv.deviceResourceName, cv.type, cv.value)
            }
        }
        if (Objects.nonNull(cv.origin) && cv.origin!! > 0) {
            reading.origin = cv.origin
        } else {
            reading.origin = eventOrigin
        }
        return reading
    }

    private fun uniqueOrigin(): Long {
        return Instant.now().toEpochMilli()
    }
}