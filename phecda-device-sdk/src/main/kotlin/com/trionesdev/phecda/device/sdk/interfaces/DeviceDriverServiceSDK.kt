package com.trionesdev.phecda.device.sdk.interfaces

import com.lmax.disruptor.dsl.Disruptor
import com.trionesdev.phecda.device.contracts.model.*
import com.trionesdev.phecda.device.sdk.disruptor.AsyncValuesEvent

interface DeviceDriverServiceSDK {
    fun addDevice(device: Device)
    fun devices(): MutableList<Device>
    fun getDeviceByName(name:String)
    fun updateDevice(device: Device)
    fun removeDeviceByName(name: String)
    fun addDeviceProfile(profile: DeviceProfile)
    fun deviceProfiles():MutableList<DeviceProfile>
    fun getProfileByName(name: String): DeviceProfile
    fun updateDeviceProfile(profile: DeviceProfile)
    fun removeDeviceProfileByName(name: String)
    fun deviceResource(deviceName:String,resourceName:String):DeviceResource
    fun deviceCommand(deviceName: String, commandName: String):DeviceCommand
    fun addDeviceAutoEvent(deviceName: String,autoEvent: AutoEvent)
    fun removeDeviceAutoEvent(deviceName: String, autoEventName: String)
    fun run()
    fun name()
    fun asyncReadingsEnabled(): Boolean
    fun asyncValuesChannel(): Disruptor<AsyncValuesEvent>?
}