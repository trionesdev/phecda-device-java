package com.trionesdev.phecda.device.sdk.messaging.msg

class PhecdaCommand {
    companion object {
        fun toCommand(command: PhecdaCommand) {

        }
    }

    var method: String? = null
    var productKey: String? = null
    var deviceName: String? = null
    var commandName: String? = null
    var params: MutableMap<String, String?>? = null
    var body: MutableMap<String, Any?>? = null
}