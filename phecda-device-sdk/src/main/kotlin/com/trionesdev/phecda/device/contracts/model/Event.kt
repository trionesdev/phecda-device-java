package com.trionesdev.phecda.device.contracts.model

import com.trionesdev.phecda.device.contracts.model.reading.BaseReading
import com.trionesdev.phecda.device.contracts.model.reading.Reading
import java.time.Instant
import java.util.*

class Event : Versionable() {
    companion object {
        fun newEvent(
            profileName: String?,
            deviceName: String?,
            sourceName: String?,
        ): Event {
            val versionable = newVersionable()
            return Event().apply {
                this.apiVersion = versionable.apiVersion
                this.id = UUID.randomUUID().toString()
                this.profileName = profileName
                this.deviceName = deviceName
                this.sourceName = sourceName
                this.origin = Instant.now().toEpochMilli()
            }
        }
    }

    var id: String? = null
    var deviceName: String? = null
    var profileName: String? = null
    var sourceName: String? = null
    var origin: Long? = null
    var readings: MutableList<BaseReading>? = null
    var tags: MutableMap<String, Any>? = null
}