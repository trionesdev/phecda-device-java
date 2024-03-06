package com.trionesdev.phecda.device.sdk.application

import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.common.CommonConstants.READ_WRITE_W
import com.trionesdev.phecda.device.contracts.errors.CommonPhedaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_CONTRACT_INVALID
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_ENTITY_DOSE_NOT_EXIST
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_NOT_ALLOWED
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVER_ERROR
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVICE_LOCKED
import com.trionesdev.phecda.device.contracts.model.Device
import com.trionesdev.phecda.device.contracts.model.DeviceService
import com.trionesdev.phecda.device.contracts.model.Event
import com.trionesdev.phecda.device.contracts.model.enums.AdminState
import com.trionesdev.phecda.device.contracts.model.enums.OperatingState
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.common.SdkConstants.URLRawQuery
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver
import com.trionesdev.phecda.device.sdk.model.CommandRequest
import com.trionesdev.phecda.device.sdk.transformer.Transformer
import java.util.regex.Pattern


@Slf4j
object ApplicationCommand {
    fun getCommand(
        deviceName: String?,
        commandName: String?,
        queryParams: String?,
        regexCmd: Boolean,
        dic: Container
    ): Event? {
        deviceName ?: let {
            throw CommonPhedaException.newCommonPhedaException(
                KIND_CONTRACT_INVALID,
                "device name is empty",
                null
            )
        }
        commandName ?: let {
            throw CommonPhedaException.newCommonPhedaException(
                KIND_CONTRACT_INVALID,
                "command name is empty",
                null
            )
        }
        var res: Event? = null
        validateServiceAndDeviceState(deviceName, dic)?.let { device ->
            Cache.profiles()?.deviceCommand(device.profileName, commandName)?.let { deviceCommand ->
                res = readDeviceCommand(device, commandName, queryParams, dic)
            } ?: let {
                res = if (regexCmd) {
                    readDeviceResourcesRegex(device, commandName, queryParams, dic)
                } else {
                    readDeviceResource(device, commandName, queryParams, dic)
                }
            }
            log.debug("GET Device Command successfully. Device: {}, Source: {}", deviceName, commandName)
        }
        return res
    }

    fun validateServiceAndDeviceState(deviceName: String?, dic: Container): Device? {
        val ds = dic.getInstance(DeviceService::class.java)
        if (ds?.adminState?.equals(AdminState.LOCKED) == true) {
            throw CommonPhedaException(KIND_SERVICE_LOCKED, "service locked")
        }
        return Cache.devices()?.forName(deviceName!!)?.let {
            if (it.adminState?.equals(AdminState.LOCKED) == true) {
                throw CommonPhedaException(KIND_SERVICE_LOCKED, String.format("device %s locked", it.name))
            }
            if (it.operatingState?.equals(OperatingState.DOWN) == true) {
                throw CommonPhedaException(
                    KIND_SERVICE_LOCKED,
                    String.format("device %s OperatingState is DOWN", it.name)
                )
            }
            it
        }
    }

    fun readDeviceResource(device: Device, resourceName: String, attributes: String?, dic: Container): Event? {
        Cache.profiles()?.deviceResource(device.profileName!!, resourceName)?.let { dr ->
            if (dr.properties?.readWrite.equals(READ_WRITE_W)) {
                throw CommonPhedaException(
                    KIND_NOT_ALLOWED,
                    String.format("DeviceResource %s is marked as write-only", dr.name)
                )
            }

            val reqs: MutableList<CommandRequest> = mutableListOf()
            val req = CommandRequest().apply {
                this.deviceResourceName = dr.name
                this.attributes = dr.attributes
                this.type = dr.properties?.valueType
                if (attributes?.isNotEmpty() == true) {
                    if (this.attributes?.isEmpty() == true) {
                        this.attributes = mutableMapOf()
                    }
                    this.attributes?.set(URLRawQuery, attributes)
                }
            }
            reqs.add(req)

            val results = dic.getInstance(ProtocolDriver::class.java)!!.let { driver ->
                driver.handleReadCommands(device.name, device.protocols, reqs)
            }
            val configuration = dic.getInstance(ConfigurationStruct::class.java)
            return Transformer.commandValuesToEvent(
                results,
                device.name,
                dr.name!!,
                configuration?.device?.dataTransform ?: false,
                dic
            )

        } ?: let {
            throw CommonPhedaException(
                KIND_ENTITY_DOSE_NOT_EXIST,
                String.format("DeviceResource %s not found", resourceName)
            )
        }
    }

    fun readDeviceResourcesRegex(
        device: Device,
        regexResourceName: String?,
        attributes: String?,
        dic: Container
    ): Event? {
        val regex = Pattern.compile(regexResourceName!!)
        Cache.profiles()?.deviceResourcesByRegex(device.profileName!!, regex)?.let { deviceResources ->
            if (deviceResources.isEmpty()) {
                val errMsg = String.format("Regex DeviceResource %s not found", regexResourceName)
                throw CommonPhedaException(KIND_ENTITY_DOSE_NOT_EXIST, errMsg)
            }
            val reqs: MutableList<CommandRequest> = mutableListOf()
            for (dr in deviceResources) {
                if (dr.properties?.readWrite.equals(READ_WRITE_W)) {
                    log.debug("DeviceResource {} is marked as write-only, skipping adding to RegEx Read list", dr.name)
                    continue
                }
                val req = CommandRequest().apply {
                    this.deviceResourceName = dr.name
                    this.attributes = dr.attributes
                    this.type = dr.properties?.valueType
                    if (attributes?.isNotEmpty() == true) {
                        if (this.attributes?.isEmpty() == true) {
                            this.attributes = mutableMapOf()
                        }
                        this.attributes?.set(URLRawQuery, attributes)
                    }
                }
                reqs.add(req)
            }
            if (reqs.isEmpty()) {
                val errMsg = String.format("no readable resources matched with %s", regexResourceName)
                throw CommonPhedaException(KIND_NOT_ALLOWED, errMsg)
            }
            val results = dic.getInstance(ProtocolDriver::class.java)!!.let { driver ->
                driver.handleReadCommands(device.name, device.protocols, reqs)
            }
            val configuration = dic.getInstance(ConfigurationStruct::class.java)
            return Transformer.commandValuesToEvent(
                results,
                device.name,
                regexResourceName,
                configuration?.device?.dataTransform ?: false,
                dic
            )
        } ?: let {
            return null
        }
    }

    fun readDeviceCommand(device: Device, commandName: String, attributes: String?, dic: Container): Event? {
        Cache.profiles()?.deviceCommand(device.profileName, commandName)?.let { dc ->
            if (dc.readWrite.equals(READ_WRITE_W)) {
                throw CommonPhedaException(
                    KIND_NOT_ALLOWED,
                    String.format("DeviceCommand %s is marked as write-only", dc.name)
                )
            }
            val configuration = dic.getInstance(ConfigurationStruct::class.java)
            if (dc.resourceOperations!!.size > configuration!!.maxEventSize) {
                throw CommonPhedaException(
                    KIND_SERVER_ERROR,
                    String.format(
                        "GET command %s exceed device %s MaxCmdOps (%d)",
                        dc.name,
                        device.name,
                        configuration.device?.maxCmdOps
                    )
                )
            }
            val reqs = mutableListOf<CommandRequest>()
            dc.resourceOperations?.forEachIndexed { i, op ->
                val drName = op.deviceResource
                Cache.profiles()?.deviceResource(device.profileName!!, drName!!)?.let { dr ->
                    val req = CommandRequest().apply {
                        this.deviceResourceName = dr.name
                        this.attributes = dr.attributes
                        this.type = dr.properties?.valueType
                        if (attributes?.isNotEmpty() == true) {
                            if (this.attributes?.isEmpty() == true) {
                                this.attributes = mutableMapOf()
                            }
                            this.attributes?.set(URLRawQuery, attributes)
                        }
                    }
                    reqs.add(req)
                } ?: let {
                    throw CommonPhedaException(
                        KIND_SERVER_ERROR,
                        String.format(
                            "DeviceResource %s in GET commnd %s for %s not defined",
                            drName,
                            dc.name,
                            device.name
                        )
                    )
                }
            }
            val results = dic.getInstance(ProtocolDriver::class.java)!!.let { driver ->
                driver.handleReadCommands(device.name, device.protocols, reqs)
            }
            return Transformer.commandValuesToEvent(
                results,
                device.name,
                dc.name!!,
                configuration.device?.dataTransform ?: false,
                dic
            )
        } ?: let {
            throw CommonPhedaException(
                KIND_ENTITY_DOSE_NOT_EXIST,
                String.format("DeviceCommand %s not found", commandName)
            )
        }
    }

}