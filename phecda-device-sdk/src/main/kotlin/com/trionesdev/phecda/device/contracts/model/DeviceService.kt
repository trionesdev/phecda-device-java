package com.trionesdev.phecda.device.contracts.model

class DeviceService : DBTimestamp() {
    var id: String? = null
    var name: String? = null
    val description: String? = null
    val labels: MutableList<String>? = null
}