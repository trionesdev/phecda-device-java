package com.trionesdev.phecda.device.sdk.cache

import com.trionesdev.phecda.device.contracts.model.DeviceCommand
import com.trionesdev.phecda.device.contracts.model.DeviceProfile
import com.trionesdev.phecda.device.contracts.model.DeviceResource
import com.trionesdev.phecda.device.contracts.model.ResourceOperation
import java.util.regex.Pattern

interface ProfileCache {
    companion object {
        @JvmStatic
        var pc: ProfileCache? = null
        fun newProfileCache(profiles: List<DeviceProfile>): ProfileCache {
            val dpMap: MutableMap<String, DeviceProfile> = mutableMapOf()
            val drMap: MutableMap<String, MutableMap<String, DeviceResource>> = mutableMapOf()
            val dcMap: MutableMap<String, MutableMap<String, DeviceCommand>> = mutableMapOf()
            profiles.forEach { it ->
                it.name?.let { deviceName ->
                    dpMap[deviceName] = it
                    it.deviceResources?.associateBy { it.name!! }?.let { deviceResourceMap ->
                        drMap[deviceName] = deviceResourceMap.toMutableMap()
                    }
                    it.deviceCommands?.associateBy { it.name!! }?.let { deviceCommandMap ->
                        dcMap[deviceName] = deviceCommandMap.toMutableMap()
                    }
                }
            }
            pc = ProfileCacheImpl().apply {
                this.deviceProfileMap = dpMap

            }
            return pc!!
        }
    }

    fun forName(name: String): DeviceProfile?
    fun all(): MutableList<DeviceProfile>
    fun add(profile: DeviceProfile)
    fun update(profile: DeviceProfile)
    fun removeByName(name: String)
    fun deviceResource(profileName: String, resourceName: String): DeviceResource?
    fun deviceResourcesByRegex(profileName: String, regex: Pattern): DeviceResource?
    fun deviceCommand(profileName: String, commandName: String): DeviceCommand?
    fun resourceOperation(profileName: String, deviceResource: String): ResourceOperation?
}

class ProfileCacheImpl : ProfileCache {
    var deviceProfileMap: MutableMap<String, DeviceProfile> = mutableMapOf()
    var deviceResourceMap: MutableMap<String, MutableMap<String, DeviceResource>> = mutableMapOf()
    var deviceCommandMap: MutableMap<String, MutableMap<String, DeviceCommand>> = mutableMapOf()
    override fun forName(name: String): DeviceProfile? {
        TODO("Not yet implemented")
    }

    override fun all(): MutableList<DeviceProfile> {
        TODO("Not yet implemented")
    }

    override fun add(profile: DeviceProfile) {
        TODO("Not yet implemented")
    }

    override fun update(profile: DeviceProfile) {
        TODO("Not yet implemented")
    }

    override fun removeByName(name: String) {
        TODO("Not yet implemented")
    }

    override fun deviceResource(profileName: String, resourceName: String): DeviceResource? {
        TODO("Not yet implemented")
    }

    override fun deviceResourcesByRegex(profileName: String, regex: Pattern): DeviceResource? {
        TODO("Not yet implemented")
    }

    override fun deviceCommand(profileName: String, commandName: String): DeviceCommand? {
        TODO("Not yet implemented")
    }

    override fun resourceOperation(profileName: String, deviceResource: String): ResourceOperation? {
        TODO("Not yet implemented")
    }
}