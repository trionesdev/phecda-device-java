package com.trionesdev.phecda.device.sdk.startup

import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.environement.Variables
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver
import com.trionesdev.phecda.device.sdk.service.DeviceServiceSdkImpl

@Slf4j
class DeviceDriver {
    companion object {
        @JvmStatic
        fun bootstrap(args: Array<String>, serviceKey: String, serviceVersion: String, driver: ProtocolDriver) {
            log.info("Phecda Device Driver Starting......")
            Variables.args = args
            val deviceService = DeviceServiceSdkImpl.newDeviceService(serviceKey, serviceVersion, driver)
            deviceService.run()
            log.info("Phecda Device Driver Started")
        }
    }
}