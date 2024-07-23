package com.trionesdev.phecda.device.contracts.model.reading

import java.time.Instant
import java.util.*

open class BaseReading : Reading {
    companion object {
        fun newBaseReading(
            productKey: String?,
            deviceName: String?,
            identifier: String?,
            valueType: String?
        ): BaseReading {
            return BaseReading().apply {
                this.id = UUID.randomUUID().toString()
                this.origin = Instant.now().toEpochMilli()
                this.deviceName = deviceName
                this.identifier = identifier
                this.productKey = productKey
                this.valueType = valueType
            }
        }
    }

    var id: String? = null
    var origin: Long? = null
    var deviceName: String? = null
    var productKey: String? = null
    var identifier: String? = null
    var valueType: String? = null
    val utils: String? = null
    var tags: MutableMap<String, Any>? = mutableMapOf()
    var binaryValue: ByteArray? = null
    var mediaType: String? = null
    var objectValue: Any? = null
    var value: String? = null
}