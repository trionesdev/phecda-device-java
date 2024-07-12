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

        @JvmStatic
        fun newProfileCache(profiles: List<DeviceProfile>): ProfileCache {
            val dpMap: MutableMap<String, DeviceProfile> = mutableMapOf()
            val drMap: MutableMap<String, MutableMap<String, DeviceResource?>?> = mutableMapOf()
            val dcMap: MutableMap<String, MutableMap<String, DeviceCommand?>?> = mutableMapOf()
            profiles.forEach { it ->
                it.name?.let { profileName ->
                    dpMap[profileName] = it
                    it.deviceResources?.associateBy { it.name!! }?.let { deviceResourceMap ->
                        drMap[profileName] = deviceResourceMap.toMutableMap()
                    }
                    it.deviceCommands?.associateBy { it.name!! }?.let { deviceCommandMap ->
                        dcMap[profileName] = deviceCommandMap.toMutableMap()
                    }
                }
            }
            pc = ProfileCacheImpl().apply {
                this.deviceProfileMap = dpMap
                this.deviceResourceMap = drMap
                this.deviceCommandMap = dcMap
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
    fun deviceResourcesByRegex(profileName: String, regex: Pattern): MutableList<DeviceResource>?
    fun deviceCommand(profileName: String?, commandName: String?): DeviceCommand?
    fun resourceOperation(profileName: String, deviceResource: String): ResourceOperation?
}

class ProfileCacheImpl : ProfileCache {
    var deviceProfileMap: MutableMap<String, DeviceProfile> = mutableMapOf()
    var deviceResourceMap: MutableMap<String, MutableMap<String, DeviceResource?>?> = mutableMapOf()
    var deviceCommandMap: MutableMap<String, MutableMap<String, DeviceCommand?>?> = mutableMapOf()
    override fun forName(name: String): DeviceProfile? {
        return this.deviceProfileMap[name]
    }

    override fun all(): MutableList<DeviceProfile> {
        return this.deviceProfileMap.values.toMutableList()
    }

    override fun add(profile: DeviceProfile) {
        profile.name?.let { profileName ->
            this.deviceProfileMap[profileName] = profile
            this.deviceResourceMap[profileName] = profile.deviceResources?.associateBy { it.name!! }?.toMutableMap()
            this.deviceCommandMap[profileName] = profile.deviceCommands?.associateBy { it.name!! }?.toMutableMap()
        }
    }

    override fun update(profile: DeviceProfile) {
        removeByName(profile.name!!)
        add(profile)
    }

    override fun removeByName(name: String) {
        deviceProfileMap[name]?.let {
            this.deviceProfileMap.remove(name)
            this.deviceResourceMap.remove(name)
            this.deviceCommandMap.remove(name)
        }
    }

    override fun deviceResource(profileName: String, resourceName: String): DeviceResource? {
        return deviceResourceMap[profileName]?.let {
            it[resourceName]
        }
    }

    override fun deviceResourcesByRegex(profileName: String, regex: Pattern): MutableList<DeviceResource>? {
        return deviceResourceMap[profileName]?.let { drs ->
            val res = mutableListOf<DeviceResource>()
            for (dr in drs.values) {
                if (dr?.name == regex.toString()) {
                    res.add(dr)
                    continue
                }
                if (regex.matcher(dr?.name!!).find()) {
                    res.add(dr)
                }
            }
            res
        }
    }

    override fun deviceCommand(profileName: String?, commandName: String?): DeviceCommand? {
        return deviceCommandMap[profileName]?.let { dcs -> dcs[commandName] }
    }

    override fun resourceOperation(profileName: String, deviceResource: String): ResourceOperation? {
        return deviceCommandMap[profileName]?.let { dcMap ->
            for (dc in dcMap.values) {
                dc?.resourceOperations?.let { resourceOperations ->
                    for (ro in resourceOperations) {
                        if (ro.deviceResource == deviceResource) {
                            return ro
                        }
                    }
                }
            }
            return null
        }
    }


    fun verifyProfileExists(profileName: String): Boolean {
        return deviceProfileMap[profileName]?.let { true } ?: let { false }
    }
}