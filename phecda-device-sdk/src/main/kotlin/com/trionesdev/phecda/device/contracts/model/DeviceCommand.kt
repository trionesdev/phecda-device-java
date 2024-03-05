package com.trionesdev.phecda.device.contracts.model

class DeviceCommand {
    var name: String? = null
    var isHidden = false
    var readWrite: String? = null
    var resourceOperations: MutableList<ResourceOperation>? = null
    var tags: MutableMap<String, Any>? = null
}