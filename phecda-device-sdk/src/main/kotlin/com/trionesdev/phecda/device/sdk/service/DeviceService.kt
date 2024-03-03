package com.trionesdev.phecda.device.sdk.service

import com.trionesdev.phecda.device.bootstrap.args.CommonArgs
import com.trionesdev.phecda.device.bootstrap.args.DefaultArgs
import com.trionesdev.phecda.device.bootstrap.environement.Variables
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct
import com.trionesdev.phecda.device.sdk.interfaces.DeviceServiceSDK
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver

class DeviceService : DeviceServiceSDK {

    companion object {
        @JvmStatic
        var EnvInstanceName: String = "PHECDA_INSTANCE_NAME"
        fun newDeviceService(serviceKey: String?, serviceVersion: String?, driver: ProtocolDriver): DeviceServiceSDK {
            if (serviceKey.isNullOrBlank()) {
                throw RuntimeException("serviceKey is null")
            }
            return DeviceService().apply {
                this.serviceKey = serviceKey
                this.driver = driver
                this.config = ConfigurationStruct()
            }
        }
    }

    private var serviceKey: String? = null
    private val baseServiceName: String? = null
    private var driver: ProtocolDriver? = null
    private var args:CommonArgs? = null
    private var config: ConfigurationStruct? = null

    override fun run() {
        val instanceName = ""
        val additionalUsage =
            """    -i, --instance                  Provides a service name suffix which allows unique instance to be created
                                    If the option is provided, service name will be replaced with "<name>_<instance>"
            """
        this.args = DefaultArgs.withUsage(additionalUsage)
        this.args?.parse(Variables.args)
    }
}