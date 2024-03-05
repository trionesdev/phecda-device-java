package com.trionesdev.phecda.device.contracts.model

class Device : DBTimestamp() {
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var adminState: String? = null
    var operatingState: String? = null
    var protocols: MutableMap<String, Map<String, Any>>? = null
    var labels: MutableList<String>? = null
    var location: Any? = null
    var serviceName: String? = null
    var profileName: String? = null
    var autoEvents: List<AutoEvent>? = null
    var tags: MutableMap<String, Any>? = null
    var properties: MutableMap<String, Any>? = null
}