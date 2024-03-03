package com.trionesdev.phecda.device.sdk.autoevent

import com.trionesdev.phecda.device.bootstrap.BootstrapHandlerArgs
import com.trionesdev.phecda.device.sdk.interfaces.AutoEventManager

class DeviceAutoEventManager : AutoEventManager {
    companion object {
        fun bootstrapHandler(args: BootstrapHandlerArgs): Boolean {
            return true
        }
    }

    override fun startAutoEvents() {
        TODO("Not yet implemented")
    }

    override fun restartForDevice(name: String?) {
        TODO("Not yet implemented")
    }

    override fun stopForDevice(deviceName: String?) {
        TODO("Not yet implemented")
    }
}