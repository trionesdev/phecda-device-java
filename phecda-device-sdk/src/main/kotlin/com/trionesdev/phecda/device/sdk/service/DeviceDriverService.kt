package com.trionesdev.phecda.device.sdk.service

import com.trionesdev.phecda.device.bootstrap.Bootstrap
import com.trionesdev.phecda.device.bootstrap.args.CommonArgs
import com.trionesdev.phecda.device.bootstrap.args.DefaultArgs
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.bootstrap.environement.Variables
import com.trionesdev.phecda.device.bootstrap.startup.Timer
import com.trionesdev.phecda.device.contracts.model.DeviceService
import com.trionesdev.phecda.device.sdk.autoevent.DeviceAutoEventManager
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct
import com.trionesdev.phecda.device.sdk.interfaces.DeviceDriverServiceSDK
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver

class DeviceDriverService : DeviceDriverServiceSDK {

    companion object {
        @JvmStatic
        var EnvInstanceName: String = "PHECDA_INSTANCE_NAME"
        fun newDeviceService(
            serviceKey: String?,
            serviceVersion: String?,
            driver: ProtocolDriver
        ): DeviceDriverServiceSDK {
            if (serviceKey.isNullOrBlank()) {
                throw RuntimeException("serviceKey is null")
            }
            return DeviceDriverService().apply {
                this.serviceKey = serviceKey
                this.driver = driver
                this.config = ConfigurationStruct()
            }
        }
    }

    private var serviceKey: String? = null
    private var baseServiceName: String? = null
    private var driver: ProtocolDriver? = null
    private var args: CommonArgs? = null
    private var deviceService: DeviceService? = null
    private var config: ConfigurationStruct? = null
    private var dic: Container? = null

    override fun run() {
        var instanceName = ""
        val startupTimer = Timer()
        val additionalUsage =
            """    
                -i, --instance  Provides a service name suffix which allows unique instance to be created
                If the option is provided, service name will be replaced with "<name>_<instance>"
            """
        this.args = DefaultArgs.withUsage(additionalUsage)
        this.args?.parse(Variables.args)
        Variables.envVars["instance"]?.let { instanceName = it }
        Variables.envVars["i"]?.let { instanceName = it }

        this.setServiceName(instanceName)
        this.config = ConfigurationStruct()
        this.deviceService = DeviceService().apply {
            name = serviceKey
        }
        this.dic = Container.newContainer(listOf(this.config, this.deviceService, this.driver))

        Bootstrap.runAndReturnWaitGroup(
            args,
            serviceKey,
            config,
            startupTimer,
            dic,
            mutableListOf(
                DeviceAutoEventManager::bootstrapHandler
            )
        )

    }

    private fun setServiceName(instanceName: String) {
        var instanceNameInner = instanceName
        val envValue = System.getenv(EnvInstanceName)
        if (envValue.isNullOrBlank()) {
            instanceNameInner = envValue
        }
        this.baseServiceName = this.serviceKey
        if (instanceNameInner.isNotBlank()) {
            this.serviceKey = this.serviceKey + "_" + instanceNameInner
        }
    }

}