package com.trionesdev.phecda.device.sdk.messaging.msg

class PhecdaCommand {
    var productKey: String? = null
    var deviceName: String? = null
    var commandName: String? = null
    var params: MutableMap<String, Any?>? = null
}