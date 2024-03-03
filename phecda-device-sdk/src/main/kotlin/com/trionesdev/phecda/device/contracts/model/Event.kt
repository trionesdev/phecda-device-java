package com.trionesdev.phecda.device.contracts.model

import com.trionesdev.phecda.device.contracts.model.reading.Reading

class Event {
    val id: String? = null
    val deviceName: String? = null
    val profileName: String? = null
    val sourceName: String? = null
    val origin: Long? = null
    val readings: List<Reading>? = null
    val tags: Map<String, Any>? = null
}