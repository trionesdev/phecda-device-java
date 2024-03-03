package com.trionesdev.phecda.device.sdk.provision

import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.model.DeviceService

class Provision {
    companion object {
        @JvmStatic
        fun loadDevices(path: String, dic: Container) {
            val serviceName = dic.getInstance(DeviceService::class.java)
        }

        fun loadDevicesFromFile(path: String, serviceName: String) {

        }
    }
}