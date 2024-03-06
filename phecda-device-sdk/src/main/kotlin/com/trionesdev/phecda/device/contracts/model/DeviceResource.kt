package com.trionesdev.phecda.device.contracts.model

class DeviceResource {
    var name: String? = null
    var description: String? = null
    var isHidden: Boolean? = false
    var properties: ResourceProperties? = null
    var attributes: MutableMap<String, Any>? = null
    var tags: MutableMap<String, Any>? = null
}