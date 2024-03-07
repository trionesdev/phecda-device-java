package com.trionesdev.phecda.device.sdk.model

class CommandRequest {
    var deviceResourceName: String? = null
    var attributes: MutableMap<String, Any?>? = null
    var type: String? = null
}