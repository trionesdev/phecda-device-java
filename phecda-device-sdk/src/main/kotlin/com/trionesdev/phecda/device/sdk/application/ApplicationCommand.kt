package com.trionesdev.phecda.device.sdk.application

import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson2.JSON
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.common.CommonConstants.READ_WRITE_R
import com.trionesdev.phecda.device.contracts.common.CommonConstants.READ_WRITE_W
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_BOOL
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_BOOL_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_DOUBLE
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_DOUBLE_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_LONG
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_LONG_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_OBJECT
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_OBJECT_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRING
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRING_ARRAY
import com.trionesdev.phecda.device.contracts.errors.CommonPhecdaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_CONTRACT_INVALID
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_ENTITY_DOSE_NOT_EXIST
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_NOT_ALLOWED
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVER_ERROR
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVICE_LOCKED
import com.trionesdev.phecda.device.contracts.model.Device
import com.trionesdev.phecda.device.contracts.model.DeviceResource
import com.trionesdev.phecda.device.contracts.model.DeviceService
import com.trionesdev.phecda.device.contracts.model.Event
import com.trionesdev.phecda.device.contracts.model.enums.AdminState
import com.trionesdev.phecda.device.contracts.model.enums.OperatingState
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.common.SdkConstants.URLRawQuery
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver
import com.trionesdev.phecda.device.sdk.model.CommandRequest
import com.trionesdev.phecda.device.sdk.model.CommandValue
import com.trionesdev.phecda.device.sdk.transformer.TransformParam
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
        if (deviceName.isNullOrBlank()) {
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_CONTRACT_INVALID,
                "device name is empty",
                null
            )
        }
        if (commandName.isNullOrBlank()) {
            throw CommonPhecdaException.newCommonPhecdaException(
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

    fun setCommand(
        deviceName: String?,
        commandName: String?,
        queryParams: String?,
        requests: MutableMap<String, Any?>?,
        dic: Container
    ): Event? {
        if (deviceName.isNullOrBlank()) {
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_CONTRACT_INVALID,
                "device name is empty",
                null
            )
        }
        if (commandName.isNullOrBlank()) {
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_CONTRACT_INVALID,
                "command name is empty",
                null
            )
        }
        var event: Event? = null
        validateServiceAndDeviceState(deviceName, dic)?.let { device ->
            Cache.profiles()?.deviceCommand(device.profileName, commandName)?.let {
                event = writeDeviceCommand(device, commandName, queryParams, requests, dic)
            } ?: let {
                event = writeDeviceResource(device, commandName, queryParams, requests, dic)
            }
            log.debug(
                "SET Device Command successfully. Device: {}, Source: {}",
                deviceName,
                commandName
            )
        }
        return event
    }


    private fun readDeviceResource(device: Device, resourceName: String, attributes: String?, dic: Container): Event? {
        Cache.profiles()?.deviceResource(device.profileName!!, resourceName)?.let { dr ->
            if (dr.properties?.readWrite.equals(READ_WRITE_W)) {
                throw CommonPhecdaException(
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

            val results =
                dic.getInstance(ProtocolDriver::class.java)!!.handleReadCommands(device.name, device.protocols, reqs)
            val configuration = dic.getInstance(ConfigurationStruct::class.java)
            return Transformer.commandValuesToEvent(
                results,
                device.name,
                dr.name!!,
                configuration?.device?.dataTransform ?: false,
                dic
            )

        } ?: let {
            throw CommonPhecdaException(
                KIND_ENTITY_DOSE_NOT_EXIST,
                String.format("DeviceResource %s not found", resourceName)
            )
        }
    }

    private fun readDeviceResourcesRegex(
        device: Device,
        regexResourceName: String?,
        attributes: String?,
        dic: Container
    ): Event? {
        val regex = Pattern.compile(regexResourceName!!)
        Cache.profiles()?.deviceResourcesByRegex(device.profileName!!, regex)?.let { deviceResources ->
            if (deviceResources.isEmpty()) {
                val errMsg = String.format("Regex DeviceResource %s not found", regexResourceName)
                throw CommonPhecdaException(KIND_ENTITY_DOSE_NOT_EXIST, errMsg)
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
                throw CommonPhecdaException(KIND_NOT_ALLOWED, errMsg)
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
                throw CommonPhecdaException(
                    KIND_NOT_ALLOWED,
                    String.format("DeviceCommand %s is marked as write-only", dc.name)
                )
            }
            val configuration = dic.getInstance(ConfigurationStruct::class.java)
            if (dc.resourceOperations!!.size > configuration!!.device!!.maxCmdOps) {
                throw CommonPhecdaException(
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
                            if (this.attributes == null) {
                                this.attributes = mutableMapOf()
                            }
                            this.attributes?.set(URLRawQuery, attributes)
                        }
                    }
                    reqs.add(req)
                } ?: let {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format(
                            "DeviceResource %s in GET command %s for %s not defined",
                            drName,
                            dc.name,
                            device.name
                        )
                    )
                }
            }
            val results =
                dic.getInstance(ProtocolDriver::class.java)!!.handleReadCommands(device.name, device.protocols, reqs)
            return Transformer.commandValuesToEvent(
                results,
                device.name,
                dc.name!!,
                configuration.device?.dataTransform ?: false,
                dic
            )
        } ?: let {
            throw CommonPhecdaException(
                KIND_ENTITY_DOSE_NOT_EXIST,
                String.format("DeviceCommand %s not found", commandName)
            )
        }
    }

    fun writeDeviceCommand(
        device: Device,
        commandName: String,
        attributes: String?,
        requests: MutableMap<String, Any?>?,
        dic: Container
    ): Event? {
        Cache.profiles()?.deviceCommand(device.profileName, commandName)?.let { dc ->
            if (dc.readWrite.equals(READ_WRITE_R)) {
                throw CommonPhecdaException(
                    KIND_NOT_ALLOWED,
                    String.format("DeviceCommand %s is marked as read-only", dc.name)
                )
            }
            val configuration = dic.getInstance(ConfigurationStruct::class.java)
            if (dc.resourceOperations!!.size > configuration!!.device!!.maxCmdOps) {
                throw CommonPhecdaException(
                    KIND_SERVER_ERROR,
                    String.format(
                        "POST command %s exceed device %s MaxCmdOps (%d)",
                        dc.name,
                        device.name,
                        configuration.device?.maxCmdOps
                    )
                )
            }
            val cvs = mutableListOf<CommandValue>()
            dc.resourceOperations?.forEachIndexed { i, ro ->
                val drName = ro.deviceResource
                Cache.profiles()?.deviceResource(device.profileName!!, drName!!)?.let { dr ->
                    var value = requests?.get(ro.deviceResource)
                    value ?: let {
                        value = if (ro.defaultValue.isNullOrBlank()) {
                            ro.defaultValue
                        } else if (dr.properties?.defaultValue.isNullOrBlank()) {
                            dr.properties?.defaultValue
                        } else {
                            throw CommonPhecdaException(
                                KIND_SERVER_ERROR,
                                String.format(
                                    "DeviceResource %s not found in request body and no default value defined",
                                    dr.name
                                )
                            )
                        }
                    }

                    if (ro.mappings?.isNotEmpty() == true) {
                        for ((k, v) in ro.mappings!!) {
                            if (v == value) {
                                value = k
                                break
                            }
                        }
                    }
                    val cv = createCommandValueFromDeviceResource(dr, value)
                    cvs.add(cv)


                } ?: let {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format(
                            "DeviceResource %s in SET command %s for %s not defined",
                            drName,
                            dc.name,
                            device.name
                        )
                    )
                }
            }
            val reqs = mutableListOf<CommandRequest>()
            cvs.forEach { cv ->
                val dr = Cache.profiles()?.deviceResource(device.profileName!!, cv.deviceResourceName!!)
                val req = CommandRequest().apply {
                    this.deviceResourceName = cv.deviceResourceName
                    this.attributes = dr?.attributes
                    this.type = cv.type
                    if (!attributes.isNullOrBlank()) {
                        if (this.attributes.isNullOrEmpty()) {
                            this.attributes = mutableMapOf()
                        }
                        this.attributes?.set(URLRawQuery, attributes)
                    }
                }
                if (configuration.device?.dataTransform == true) {
                    TransformParam.transformWriteParameter(cv, dr?.properties)
                }
                reqs.add(req)
            }
            dic.getInstance(ProtocolDriver::class.java)?.let { driver ->
                driver.handleWriteCommands(device.name, device.protocols, reqs, cvs)
            }
            if (!dc.readWrite.equals(READ_WRITE_W)) {
                return Transformer.commandValuesToEvent(
                    cvs,
                    device.name,
                    commandName,
                    false,
                    dic
                )
            }
            return null
        } ?: let {
            throw CommonPhecdaException(
                KIND_ENTITY_DOSE_NOT_EXIST,
                String.format("DeviceCommand %s not found", commandName)
            )
        }
    }

    fun writeDeviceResource(
        device: Device,
        resourceName: String,
        attributes: String?,
        requests: MutableMap<String, Any?>?,
        dic: Container
    ): Event? {
        Cache.profiles()?.deviceResource(device.profileName!!, resourceName)?.let { dr ->
            if (dr.properties?.readWrite.equals(READ_WRITE_R)) {
                throw CommonPhecdaException(
                    KIND_NOT_ALLOWED,
                    String.format("DeviceResource %s is marked as read-only", dr.name)
                )
            }
            var v = requests?.get(dr.name)
            v ?: let {
                if (!dr.properties?.defaultValue.isNullOrBlank()) {
                    v = dr.properties?.defaultValue
                } else {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format(
                            "DeviceResource %s not found in request body and no default value defined",
                            dr.name
                        )
                    )

                }
            }
            val cv = createCommandValueFromDeviceResource(dr, v)
            val req = CommandRequest().apply {
                this.deviceResourceName = cv.deviceResourceName
                this.attributes = dr.attributes
                this.type = cv.type
                if (!attributes.isNullOrBlank()) {
                    if (this.attributes.isNullOrEmpty()) {
                        this.attributes = mutableMapOf()
                    }
                    this.attributes?.set(URLRawQuery, attributes)
                }
            }
            dic.getInstance(ConfigurationStruct::class.java)?.let { configuration ->
                if (configuration.device?.dataTransform == true) {
                    TransformParam.transformWriteParameter(cv, dr.properties)
                }
            }
            dic.getInstance(ProtocolDriver::class.java)?.let { driver ->
                driver.handleWriteCommands(device.name, device.protocols, mutableListOf(req), mutableListOf(cv))
            }
            if (!dr.properties?.readWrite.equals(READ_WRITE_W)) {
                return Transformer.commandValuesToEvent(
                    mutableListOf(cv),
                    device.name,
                    resourceName,
                    false,
                    dic
                )
            }
            return null
        } ?: let {
            throw CommonPhecdaException(
                KIND_ENTITY_DOSE_NOT_EXIST,
                String.format("DeviceResource %s not found", resourceName)
            )
        }
    }

    private fun validateServiceAndDeviceState(deviceName: String?, dic: Container): Device? {
        val ds = dic.getInstance(DeviceService::class.java)
        if (ds?.adminState?.equals(AdminState.LOCKED) == true) {
            throw CommonPhecdaException(KIND_SERVICE_LOCKED, "service locked")
        }
        return Cache.devices()?.forName(deviceName!!)?.let {
            if (it.adminState?.equals(AdminState.LOCKED) == true) {
                throw CommonPhecdaException(KIND_SERVICE_LOCKED, String.format("device %s locked", it.name))
            }
            if (it.operatingState?.equals(OperatingState.DOWN) == true) {
                throw CommonPhecdaException(
                    KIND_SERVICE_LOCKED,
                    String.format("device %s OperatingState is DOWN", it.name)
                )
            }
            it
        }
    }

    fun createCommandValueFromDeviceResource(dr: DeviceResource, value: Any?): CommandValue {
        var result = CommandValue()
        val v = StrUtil.join("", value);

        if (!dr.properties?.valueType.equals(VALUE_TYPE_STRING) && StrUtil.trim(v) == "") {
            throw CommonPhecdaException(
                KIND_CONTRACT_INVALID,
                String.format("empty string is invalid for %v value type", dr.properties?.valueType)
            )
        }
        when (dr.properties?.valueType) {
            VALUE_TYPE_BOOL -> {
                result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_BOOL, v.toBoolean())
            }

            VALUE_TYPE_STRING -> {
                result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_STRING, v)
            }

            VALUE_TYPE_INT -> {
                result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_INT, v.toInt())
            }

            VALUE_TYPE_LONG -> {
                result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_LONG, v.toInt())
            }

            VALUE_TYPE_FLOAT -> {
                result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_FLOAT, v.toFloat())
            }

            VALUE_TYPE_DOUBLE -> {
                result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_DOUBLE, v.toDouble())
            }

            VALUE_TYPE_OBJECT -> {
                result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_OBJECT, v)
            }

            VALUE_TYPE_BOOL_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Boolean::class.java)
                    result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_BOOL_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", dr.properties?.valueType)
                    )
                }
            }

            VALUE_TYPE_STRING_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, String::class.java)
                    result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_STRING_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", dr.properties?.valueType)
                    )
                }
            }

            VALUE_TYPE_INT_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Int::class.java)
                    result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_INT_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", dr.properties?.valueType)
                    )
                }
            }

            VALUE_TYPE_LONG_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Long::class.java)
                    result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_LONG_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", dr.properties?.valueType)
                    )
                }
            }

            VALUE_TYPE_FLOAT_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Float::class.java)
                    result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_FLOAT_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", dr.properties?.valueType)
                    )
                }
            }

            VALUE_TYPE_DOUBLE_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Double::class.java)
                    result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_DOUBLE_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", dr.properties?.valueType)
                    )
                }
            }

            VALUE_TYPE_OBJECT_ARRAY -> {
                try {
                    val array = JSON.parseObject(v, Object::class.java)
                    result = CommandValue.newCommandValue(dr.name, VALUE_TYPE_OBJECT_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", dr.properties?.valueType)
                    )
                }
            }

            else -> {
                throw CommonPhecdaException(
                    KIND_SERVER_ERROR,
                    String.format("failed to convert set parameter %s to ValueType %s", dr.properties?.valueType)
                )
            }
        }
        //TODO
        return result
    }

}