package com.trionesdev.phecda.device.contracts.model

import com.trionesdev.phecda.device.contracts.common.CommonConstants.EVENT_TYPE_PROPERTY
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading
import java.time.Instant
import java.util.*

open class Event : Versionable() {
    companion object {
        fun newEvent(
            productKey: String?,
            deviceName: String?,
            identifier: String?,
        ): Event {
            val versionable = newVersionable()
            return Event().apply {
                this.apiVersion = versionable.apiVersion
                this.id = UUID.randomUUID().toString()
                this.type = EVENT_TYPE_PROPERTY
                this.productKey = productKey
                this.deviceName = deviceName
                this.identifier = identifier
                this.origin = Instant.now().toEpochMilli()
            }
        }
    }

    var id: String? = null
    var type: String? = null
    var deviceName: String? = null
    var productKey: String? = null
    var identifier: String? = null
    var origin: Long? = null
    var readings: MutableList<BaseReading>? = null
    var tags: MutableMap<String, String?>? = null
}