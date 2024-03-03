package com.trionesdev.phecda.device.contracts.model.reading

open class BaseReading : Reading {
    val id: String? = null
    val origin: Long? = null
    val deviceName: String? = null
    val resourceName: String? = null
    val profileName: String? = null
    val valueType: String? = null
    val utils: String? = null
    val tags: MutableMap<String, Any>? = null
}