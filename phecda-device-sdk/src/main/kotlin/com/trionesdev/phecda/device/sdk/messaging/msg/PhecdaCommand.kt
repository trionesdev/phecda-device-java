package com.trionesdev.phecda.device.sdk.messaging.msg

class PhecdaCommand {
    companion object {
        fun toCommand(command: PhecdaCommand) {

        }
    }

    var id: String? = null
    var sync: Boolean? = null
    var method: String? = null
    var productKey: String? = null
    var deviceName: String? = null
    var identifier: String? = null
    var inputData: MutableMap<String, Any?>? = null
}