package com.trionesdev.phecda.device.sdk.messaging.msg

import com.trionesdev.phecda.device.contracts.model.Event
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading

open class PhecdaEvent {
    companion object {
        @JvmStatic
        fun newPhecdaEvent(event: Event): PhecdaEvent {
            return PhecdaEvent().apply {
                this.version = event.apiVersion
                this.id = event.id
                this.type = event.type
                this.deviceName = event.deviceName
                this.productKey = event.productKey
                this.identifier = event.identifier
                this.ts = event.origin
                this.readings = Reading.fromBaseReadingsToMap(event.readings)
                this.tags = event.tags
            }
        }
    }

    var version: String? = null
    var id: String? = null
    var type: String? = null
    var deviceName: String? = null
    var productKey: String? = null
    var identifier: String? = null
    var ts: Long? = null
    var readings: MutableMap<String, Reading>? = mutableMapOf()
    var tags: MutableMap<String, String?>? = mutableMapOf()


    class Reading {
        companion object {
            fun fromBaseReading(baseReading: BaseReading): Reading {
                return Reading().apply {
                    this.identifier = baseReading.identifier
                    this.valueType = baseReading.valueType
                    this.utils = baseReading.utils
                    this.binaryValue = baseReading.binaryValue
                    this.mediaType = baseReading.mediaType
                    this.objectValue = baseReading.objectValue
                    this.value = baseReading.value
                    this.ts = baseReading.origin
                }
            }

            fun fromBaseReadings(baseReadings: MutableList<BaseReading>): MutableList<Reading> {
                return baseReadings.map { fromBaseReading(it) }.toMutableList()
            }

            fun fromBaseReadingsToMap(baseReadings: MutableList<BaseReading>?): MutableMap<String, Reading> {
                if (baseReadings.isNullOrEmpty()) {
                    return mutableMapOf()
                }
                return baseReadings.map { fromBaseReading(it) }.associateBy { it.identifier!! }.toMutableMap()
            }

        }

        var identifier: String? = null
        var valueType: String? = null
        var utils: String? = null
        var binaryValue: ByteArray? = null
        var mediaType: String? = null
        var objectValue: Any? = null
        var value: String? = null
        var ts: Long? = null
    }

}