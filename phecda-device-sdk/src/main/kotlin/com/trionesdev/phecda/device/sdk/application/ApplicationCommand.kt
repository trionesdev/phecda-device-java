package com.trionesdev.phecda.device.sdk.application

import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.common.CommonConstants.READ_WRITE_W
import com.trionesdev.phecda.device.contracts.errors.CommonPhedaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_NOT_ALLOWED
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVER_ERROR
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_CONTRACT_INVALID
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_ENTITY_DOSE_NOT_EXIST
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVICE_LOCKED
import com.trionesdev.phecda.device.contracts.model.Device
import com.trionesdev.phecda.device.contracts.model.DeviceService
import com.trionesdev.phecda.device.contracts.model.Event
import com.trionesdev.phecda.device.contracts.model.enums.AdminState
import com.trionesdev.phecda.device.contracts.model.enums.OperatingState
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct


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
        val res: Event? = null
        validateServiceAndDeviceState(deviceName, dic)?.let { device ->
            Cache.profiles()?.deviceCommand(device.profileName, commandName)?.let { deviceCommand ->

            } ?: let {

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
        } ?: let {
            throw CommonPhedaException(KIND_ENTITY_DOSE_NOT_EXIST, String.format("DeviceCommand %s not found", commandName))
        }
        return null
    }

}