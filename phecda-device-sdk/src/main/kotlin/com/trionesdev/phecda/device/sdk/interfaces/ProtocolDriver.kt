package com.trionesdev.phecda.device.sdk.interfaces

import com.trionesdev.phecda.device.sdk.model.CommandRequest
import com.trionesdev.phecda.device.sdk.model.CommandValue

interface ProtocolDriver {
    fun initialize(sdk: DeviceDriverServiceSDK)
    fun start()
    fun stop(force: Boolean)
    fun handleReadCommands(
        deviceName: String,
        protocols: MutableMap<String, MutableMap<String, Any>>,
        reqs: MutableList<CommandRequest>
    ): MutableList<CommandValue>

    fun handleWriteCommands(
        deviceName: String,
        protocols: MutableMap<String, MutableMap<String, Any>>,
        reqs: MutableList<CommandRequest>,
        params: MutableList<CommandValue>
    )

    fun addDevice(
        deviceName: String,
        protocols: MutableMap<String, MutableMap<String, Any>>,
        adminState: String
    )

    fun updateDevice(
        deviceName: String,
        protocols: MutableMap<String, MutableMap<String, Any>>,
        adminState: String
    )

    fun removeDevice(
        deviceName: String,
        protocols: MutableMap<String, MutableMap<String, Any>>
    )

}