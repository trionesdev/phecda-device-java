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
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRING
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRING_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRUCT
import com.trionesdev.phecda.device.contracts.errors.CommonPhecdaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_CONTRACT_INVALID
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_ENTITY_DOSE_NOT_EXIST
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_NOT_ALLOWED
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVER_ERROR
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVICE_LOCKED
import com.trionesdev.phecda.device.contracts.model.*
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


@Slf4j
object ApplicationCommand {
    /**
     * 获取类型的指令，如果不存在对应表示的指令，则去寻找对应的属性
     */
    fun getCommand(
        deviceName: String?,
        identifier: String?,
        queryParams: String?,
        dic: Container
    ): Event? {
        if (deviceName.isNullOrBlank()) {
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_CONTRACT_INVALID,
                "device name is empty",
                null
            )
        }
        if (identifier.isNullOrBlank()) {
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_CONTRACT_INVALID,
                "command name is empty",
                null
            )
        }
        var res: Event? = null
        validateServiceAndDeviceState(deviceName, dic)?.let { device ->
            Cache.profiles()?.deviceCommand(device.productKey, identifier)?.let { deviceCommand ->
                res = readDeviceCommand(device, identifier, queryParams, dic)
            } ?: let {
                res =  readDeviceProperty(device, identifier, queryParams, dic)
            }
            log.debug("GET Device Command successfully. Device: {}, Source: {}", deviceName, identifier)
        }
        return res
    }

    fun setCommand(
        deviceName: String?,
        identifier: String?,
        queryParams: String?,
        requests: MutableMap<String, Any?>?,
        dic: Container
    ): Event? {
        if (deviceName.isNullOrBlank()) {
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_CONTRACT_INVALID,
                "device identifier is empty",
                null
            )
        }
        if (identifier.isNullOrBlank()) {
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_CONTRACT_INVALID,
                "command identifier is empty",
                null
            )
        }
        var event: Event? = null
        validateServiceAndDeviceState(deviceName, dic)?.let { device ->
            Cache.profiles()?.deviceCommand(device.productKey, identifier)?.let {
                event = writeDeviceCommand(device, identifier, queryParams, requests, dic)
            } ?: let {
                event = writeDeviceResource(device, identifier, queryParams, requests, dic)
            }
            log.debug(
                "SET Device Command successfully. Device: {}, Identifier: {}",
                deviceName,
                identifier
            )
        }
        return event
    }


    private fun readDeviceProperty(device: Device, propertyIdentifier: String, attributes: String?, dic: Container): Event? {
        Cache.profiles()?.deviceProperty(device.productKey!!, propertyIdentifier)?.let { dr ->
            if (dr.properties?.readWrite.equals(READ_WRITE_W)) {
                throw CommonPhecdaException(
                    KIND_NOT_ALLOWED,
                    String.format("DeviceResource %s is marked as write-only", dr.name)
                )
            }

            val reqs: MutableList<CommandRequest> = mutableListOf()
            val req = CommandRequest().apply {
                this.identifier = dr.identifier
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
                dr.identifier!!,
                configuration?.device?.dataTransform ?: false,
                dic
            )

        } ?: let {
            throw CommonPhecdaException(
                KIND_ENTITY_DOSE_NOT_EXIST,
                String.format("DeviceProperty %s not found", propertyIdentifier)
            )
        }
    }

    fun readDeviceCommand(device: Device, commandName: String, attributes: String?, dic: Container): Event? {
        Cache.profiles()?.deviceCommand(device.productKey, commandName)?.let { dc ->
            if (dc.readWrite.equals(READ_WRITE_W)) {
                throw CommonPhecdaException(
                    KIND_NOT_ALLOWED,
                    String.format("DeviceCommand %s is marked as write-only", dc.name)
                )
            }
            val configuration = dic.getInstance(ConfigurationStruct::class.java)
            dc.inputData?.let {inputData->
                if (inputData.size > configuration!!.device!!.maxCmdOps) {
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
            }


            val reqs = mutableListOf<CommandRequest>()

            dc.inputData?.forEachIndexed { i, inputItem ->
                val req = CommandRequest().apply {
                    this.identifier = inputItem.identifier
                    this.attributes = inputItem.attributes
                    this.type = inputItem.properties?.valueType
                    if (attributes?.isNotEmpty() == true) {
                        if (this.attributes == null) {
                            this.attributes = mutableMapOf()
                        }
                        this.attributes?.set(URLRawQuery, attributes)
                    }
                }
                reqs.add(req)
            }

            val results =
                dic.getInstance(ProtocolDriver::class.java)!!.handleReadCommands(device.name, device.protocols, reqs)
            return Transformer.commandValuesToEvent(
                results,
                device.name,
                dc.identifier!!,
                configuration?.device?.dataTransform ?: false,
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
        identifier: String,
        attributes: String?,
        requests: MutableMap<String, Any?>?,
        dic: Container
    ): Event? {
        Cache.profiles()?.deviceCommand(device.productKey, identifier)?.let { dc ->
            if (dc.readWrite.equals(READ_WRITE_R)) {
                throw CommonPhecdaException(
                    KIND_NOT_ALLOWED,
                    String.format("DeviceCommand %s is marked as read-only", dc.name)
                )
            }
            val configuration = dic.getInstance(ConfigurationStruct::class.java)
            val cvs = mutableListOf<CommandValue>()

            dc.inputData?.forEachIndexed { i, inputItem ->
                val cv = createCommandValue(inputItem.identifier!!,inputItem.properties!!.valueType!!, requests?.get(inputItem.identifier))
                cvs.add(cv)
            }
            val reqs = mutableListOf<CommandRequest>()
            cvs.forEach { cv->
                var req = CommandRequest().apply {
                    this.identifier = cv.identifier
                    this.type = cv.type
                }
                if (configuration?.device?.dataTransform == true) {
//                    TransformParam.transformWriteParameter(cv, dr?.properties)
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
                    identifier,
                    false,
                    dic
                )
            }
            return null
        }?: let {
            throw CommonPhecdaException(
                KIND_ENTITY_DOSE_NOT_EXIST,
                String.format("DeviceCommand %s not found", identifier)
            )
        }
    }

    fun writeDeviceResource(
        device: Device,
        identifier: String,
        attributes: String?,
        requests: MutableMap<String, Any?>?,
        dic: Container
    ): Event? {
        Cache.profiles()?.deviceProperty(device.productKey!!, identifier)?.let { dr ->
            if (dr.properties?.readWrite.equals(READ_WRITE_R)) {
                throw CommonPhecdaException(
                    KIND_NOT_ALLOWED,
                    String.format("DeviceResource %s is marked as read-only", dr.name)
                )
            }
            var v = requests?.get(dr.identifier)
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
                this.identifier = cv.identifier
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
                    identifier,
                    false,
                    dic
                )
            }
            return null
        } ?: let {
            throw CommonPhecdaException(
                KIND_ENTITY_DOSE_NOT_EXIST,
                String.format("DeviceResource %s not found", identifier)
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

    fun createCommandValue(identifier: String,valueType: String, value: Any?): CommandValue {
        val v = StrUtil.join("", value);
        if (!valueType.equals(VALUE_TYPE_STRING) && StrUtil.trim(v) == "") {
            throw CommonPhecdaException(
                KIND_CONTRACT_INVALID,
                String.format("empty string is invalid for %v value type", valueType)
            )
        }
        var result: CommandValue = CommandValue()
        when (valueType) {
            VALUE_TYPE_BOOL -> {
                result = CommandValue.newCommandValue(identifier, VALUE_TYPE_BOOL, v.toBoolean())
            }

            VALUE_TYPE_STRING -> {
                result = CommandValue.newCommandValue(identifier, VALUE_TYPE_STRING, v)
            }

            VALUE_TYPE_INT -> {
                result = CommandValue.newCommandValue(identifier, VALUE_TYPE_INT, v.toInt())
            }

            VALUE_TYPE_LONG -> {
                result = CommandValue.newCommandValue(identifier, VALUE_TYPE_LONG, v.toInt())
            }

            VALUE_TYPE_FLOAT -> {
                result = CommandValue.newCommandValue(identifier, VALUE_TYPE_FLOAT, v.toFloat())
            }

            VALUE_TYPE_DOUBLE -> {
                result = CommandValue.newCommandValue(identifier, VALUE_TYPE_DOUBLE, v.toDouble())
            }

            VALUE_TYPE_STRUCT -> {
                result = CommandValue.newCommandValue(identifier, VALUE_TYPE_STRUCT, v)
            }

            VALUE_TYPE_BOOL_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Boolean::class.java)
                    result = CommandValue.newCommandValue(identifier, VALUE_TYPE_BOOL_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", valueType)
                    )
                }
            }

            VALUE_TYPE_STRING_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, String::class.java)
                    result = CommandValue.newCommandValue(identifier, VALUE_TYPE_STRING_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", valueType)
                    )
                }
            }

            VALUE_TYPE_INT_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Int::class.java)
                    result = CommandValue.newCommandValue(identifier, VALUE_TYPE_INT_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", valueType)
                    )
                }
            }

            VALUE_TYPE_LONG_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Long::class.java)
                    result = CommandValue.newCommandValue(identifier, VALUE_TYPE_LONG_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", valueType)
                    )
                }
            }

            VALUE_TYPE_FLOAT_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Float::class.java)
                    result = CommandValue.newCommandValue(identifier, VALUE_TYPE_FLOAT_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", valueType)
                    )
                }
            }

            VALUE_TYPE_DOUBLE_ARRAY -> {
                try {
                    val array = JSON.parseArray(v, Double::class.java)
                    result = CommandValue.newCommandValue(identifier, VALUE_TYPE_DOUBLE_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", valueType)
                    )
                }
            }

            VALUE_TYPE_STRING_ARRAY -> {
                try {
                    val array = JSON.parseObject(v, Object::class.java)
                    result = CommandValue.newCommandValue(identifier, VALUE_TYPE_STRING_ARRAY, array)
                } catch (e: Exception) {
                    throw CommonPhecdaException(
                        KIND_SERVER_ERROR,
                        String.format("failed to convert set parameter %s to ValueType %s", valueType)
                    )
                }
            }

            else -> {
                throw CommonPhecdaException(
                    KIND_SERVER_ERROR,
                    String.format("failed to convert set parameter %s to ValueType %s",  valueType)
                )
            }
        }
        return result
    }

    fun createCommandValueFromDeviceResource(dr: DeviceProperty, value: Any?): CommandValue {
        return createCommandValue(dr.identifier!!, dr.properties!!.valueType!!, value)
    }

}