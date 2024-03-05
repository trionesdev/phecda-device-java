package com.trionesdev.phecda.device.sdk.service

import com.lmax.disruptor.EventHandler
import com.lmax.disruptor.dsl.Disruptor
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.Bootstrap
import com.trionesdev.phecda.device.bootstrap.args.CommonArgs
import com.trionesdev.phecda.device.bootstrap.args.DefaultArgs
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.bootstrap.environement.Variables
import com.trionesdev.phecda.device.bootstrap.startup.Timer
import com.trionesdev.phecda.device.contracts.errors.CommonPhedaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_DUPLICATE_NAME
import com.trionesdev.phecda.device.contracts.go.WaitGroup
import com.trionesdev.phecda.device.contracts.model.*
import com.trionesdev.phecda.device.sdk.autoevent.DeviceAutoEventManager
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.common.SdkCommonUtils
import com.trionesdev.phecda.device.sdk.common.SdkConstants
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct
import com.trionesdev.phecda.device.sdk.disruptor.AsyncValuesEvent
import com.trionesdev.phecda.device.sdk.interfaces.AutoEventManager
import com.trionesdev.phecda.device.sdk.interfaces.DeviceServiceSDK
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver
import com.trionesdev.phecda.device.sdk.model.AsyncValues
import com.trionesdev.phecda.device.sdk.service.init.ServiceBootstrap
import com.trionesdev.phecda.device.sdk.transformer.Transformer

@Slf4j
class DeviceServiceSdkImpl : DeviceServiceSDK {

    companion object {
        @JvmStatic
        var EnvInstanceName: String = "PHECDA_INSTANCE_NAME"
        fun newDeviceService(
            serviceKey: String?,
            serviceVersion: String?,
            driver: ProtocolDriver
        ): DeviceServiceSDK {
            if (serviceKey.isNullOrBlank()) {
                throw RuntimeException("please specify device service name")
            }
            if (serviceVersion.isNullOrBlank()) {
                throw RuntimeException("please specify device service version")
            }
            SdkConstants.ServiceVersion = serviceVersion
            return DeviceServiceSdkImpl().apply {
                this.serviceKey = serviceKey
                this.driver = driver
                this.config = ConfigurationStruct()
            }
        }
    }

    var serviceKey: String? = null
    var baseServiceName: String? = null
    var driver: ProtocolDriver? = null
    var autoEventManager: AutoEventManager? = null
    var asyncCh: Disruptor<AsyncValuesEvent>? = null //异步上报数据通道
    private var args: CommonArgs? = null
    private var deviceService: DeviceService? = null
    var config: ConfigurationStruct? = null
    var wg: WaitGroup? = null
    var dic: Container? = null
    override fun addDevice(device: Device): String? {
        Cache.devices()?.forName(device.name!!)?.let {
            throw CommonPhedaException.newCommonPhedaException(
                KIND_DUPLICATE_NAME,
                String.format("name conflicted, Device %s exists", device.name), null
            )
        }
        device.serviceName = serviceKey
        log.debug("Adding managed Device {}", device.name)
        return ""
    }

    override fun devices(): MutableList<Device?> {
        return Cache.devices()?.all() ?: mutableListOf()
    }

    override fun getDeviceByName(name: String): Device? {
        return Cache.devices()?.forName(name)
    }

    override fun updateDevice(device: Device) {
        Cache.devices()?.update(device)
    }

    override fun removeDeviceByName(name: String) {
        Cache.devices()?.removeByName(name)
    }

    override fun addDeviceProfile(profile: DeviceProfile) {
        Cache.profiles()?.add(profile)
    }

    override fun deviceProfiles(): MutableList<DeviceProfile> {
        return Cache.profiles()?.all() ?: mutableListOf()
    }

    override fun getProfileByName(name: String): DeviceProfile? {
        return Cache.profiles()?.forName(name)
    }

    override fun updateDeviceProfile(profile: DeviceProfile) {
        TODO("Not yet implemented")
    }

    override fun removeDeviceProfileByName(name: String) {
        TODO("Not yet implemented")
    }

    override fun deviceResource(deviceName: String, resourceName: String): DeviceResource {
        TODO("Not yet implemented")
    }

    override fun deviceCommand(deviceName: String, commandName: String): DeviceCommand {
        TODO("Not yet implemented")
    }

    override fun addDeviceAutoEvent(deviceName: String, autoEvent: AutoEvent) {
        TODO("Not yet implemented")
    }

    override fun removeDeviceAutoEvent(deviceName: String, autoEventName: String) {
        TODO("Not yet implemented")
    }


    override fun run() {
        var instanceName = ""
        val startupTimer = Timer.newStartUpTimer(serviceKey!!)
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

        val wg = Bootstrap.runAndReturnWaitGroup(
            args,
            serviceKey,
            config,
            startupTimer,
            dic,
            mutableListOf(
                DeviceAutoEventManager::bootstrapHandler,
                ServiceBootstrap.newBootstrap(this)::bootstrapHandler,
            )
        )
        this.driver?.start()
        wg.await()
    }

    override fun name(): String? {
        return this.serviceKey
    }

    override fun asyncReadingsEnabled(): Boolean {
        return config?.device?.enableAsyncReadings ?: false
    }

    override fun asyncValuesChannel(): Disruptor<AsyncValuesEvent>? {
        return asyncCh
    }

    private fun setServiceName(instanceName: String) {
        var instanceNameInner = instanceName
        val envValue = System.getenv(EnvInstanceName)
        if (!envValue.isNullOrBlank()) {
            instanceNameInner = envValue
        }
        this.baseServiceName = this.serviceKey
        if (instanceNameInner.isNotBlank()) {
            this.serviceKey = this.serviceKey + "_" + instanceNameInner
        }
    }

    /**
     * process async results
     * define the event handler and start disruptor instance
     */
    fun processAsyncResults(dic: Container) {
        val handler =
            EventHandler<AsyncValuesEvent> { asyncValuesEvent, l, b ->
                sendAsyncValues(
                    asyncValuesEvent.message,
                    dic
                )
            }
        asyncCh?.handleEventsWith(handler)
        asyncCh?.start()
    }

    /**
     * send async values
     */
    private fun sendAsyncValues(acv: AsyncValues?, dic: Container?) {
        if (acv?.commandValues?.isEmpty() == true) {
            log.error("Skip sending AsyncValues because the CommandValues is empty.")
            return
        }
        if (acv?.commandValues?.size!! > 1 && acv.sourceName.isNullOrBlank()) {
            log.error("Skip sending AsyncValues because the SourceName is empty.")
            return
        }
        if (acv.commandValues.size == 1 && acv.deviceName.isNullOrBlank()) {
            acv.sourceName = acv.commandValues[0].deviceResourceName
        }
        var configuration = dic?.getInstance(ConfigurationStruct::class.java)
        val event = Transformer.commandValuesToEvent(acv.commandValues, acv.deviceName!!, acv.sourceName!!, true, dic!!)
        SdkCommonUtils.sendEvent(event!!, "", dic)
    }

}