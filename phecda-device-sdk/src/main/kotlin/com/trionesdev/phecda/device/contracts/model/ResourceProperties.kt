package com.trionesdev.phecda.device.contracts.model

class ResourceProperties {
    var valueType: String? = null
    var readWrite: String? = null
    var units: String? = null
    var minimum: Double? = null
    var maximum: Double? = null
    var defaultValue: String? = null
    var mask: Long? = null
    var shift: Long? = null
    var scale: Double? = null
    var offset: Double? = null
    var base: Double? = null
    var assertion: String? = null
    var mediaType: String? = null
    var optional: MutableMap<String, Any>? = null
}