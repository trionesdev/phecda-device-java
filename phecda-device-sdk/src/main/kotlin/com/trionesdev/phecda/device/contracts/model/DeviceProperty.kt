package com.trionesdev.phecda.device.contracts.model

class DeviceProperty {
    var identifier:String? = null
    var name: String? = null
    var description:String? = null
    var properties: ResourceProperties? = null
    var attributes: MutableMap<String, Any?>? = null
    var tags: MutableMap<String, Any>? = null
}