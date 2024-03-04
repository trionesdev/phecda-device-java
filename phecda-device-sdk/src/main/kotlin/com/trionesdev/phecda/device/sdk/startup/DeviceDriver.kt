package com.trionesdev.phecda.device.sdk.startup

import com.trionesdev.phecda.device.bootstrap.environement.Variables
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver
import com.trionesdev.phecda.device.sdk.service.DeviceDriverService

class DeviceDriver {
    companion object {
        @JvmStatic
        fun bootstrap(args: Array<String>, serviceKey: String, serviceVersion: String, driver: ProtocolDriver) {
            Variables.args = args
            val deviceService = DeviceDriverService.newDeviceService(serviceKey, serviceVersion, driver)
            deviceService.run()
        }
    }
}