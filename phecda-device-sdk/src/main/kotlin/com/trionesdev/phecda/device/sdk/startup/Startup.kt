package com.trionesdev.phecda.device.sdk.startup

import com.trionesdev.phecda.device.bootstrap.environement.Variables
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver
import com.trionesdev.phecda.device.sdk.service.DeviceService

class Startup {
    companion object {
        fun bootstrap(args: Array<String>, serviceKey: String, serviceVersion: String, driver: ProtocolDriver) {
            Variables.args = args
            val deviceService = DeviceService.newDeviceService(serviceKey, serviceVersion, driver)
            deviceService.run()
        }
    }
}