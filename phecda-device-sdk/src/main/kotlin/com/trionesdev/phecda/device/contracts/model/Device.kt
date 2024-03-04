package com.trionesdev.phecda.device.contracts.model

class Device : DBTimestamp() {
    val id: String? = null
    val name: String? = null
    val description: String? = null
    val adminState: String? = null
    val operatingState: String? = null
    val protocols: Map<String, Map<String, Any>>? = null
    val labels: List<String>? = null
    val location: Any? = null
    var serviceName: String? = null
    val profileName: String? = null
    val autoEvents: List<AutoEvent>? = null
    val tags: Map<String, Any>? = null
    val properties: Map<String, Any>? = null
}