package com.trionesdev.phecda.device.sdk.cache

import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.model.Device

interface DeviceCache {
    companion object {
        @JvmStatic
        var dc: DeviceCache? = null
        fun newDeviceCache(devices: List<Device>, dic: Container): DeviceCache {
            val deviceMap = devices.associateBy { it.name!! }.toMutableMap()
            dc = DeviceCacheImpl().apply {
                this.deviceMap = deviceMap
            }
            return dc!!
        }
    }

    fun forName(name: String): Device?
    fun all(): MutableList<Device>
    fun add(device: Device)
    fun update(device: Device)
    fun removeByName(name: String)
}

class DeviceCacheImpl : DeviceCache {


    var deviceMap: MutableMap<String, Device> = mutableMapOf()

    override fun forName(name: String): Device? {
        return this.deviceMap[name]
    }

    override fun all(): MutableList<Device> {
        return deviceMap.values.toMutableList()
    }

    override fun add(device: Device) {
        device.name?.let {
            deviceMap[it] = device
        }
    }

    override fun update(device: Device) {
        device.name?.let {
            deviceMap[it] = device
        }

    }

    override fun removeByName(name: String) {
        deviceMap.remove(name)
    }
}