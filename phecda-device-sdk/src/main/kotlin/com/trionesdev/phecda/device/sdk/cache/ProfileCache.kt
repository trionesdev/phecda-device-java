package com.trionesdev.phecda.device.sdk.cache

import com.trionesdev.phecda.device.contracts.model.*
import java.util.regex.Pattern

interface ProfileCache {
    companion object {
        @JvmStatic
        var pc: ProfileCache? = null

        @JvmStatic
        fun newProfileCache(profiles: List<DeviceProfile>): ProfileCache {
            val profileMap: MutableMap<String, DeviceProfile> = mutableMapOf()
            val dpMap: MutableMap<String, MutableMap<String, DeviceProperty?>?> = mutableMapOf()
            val dcMap: MutableMap<String, MutableMap<String, DeviceCommand?>?> = mutableMapOf()
            val deMap: MutableMap<String, MutableMap<String, DeviceEvent?>?> = mutableMapOf()
            profiles.forEach { it ->
                it.productKey?.let { productKey ->
                    profileMap[productKey] = it
                    it.deviceProperties?.associateBy { it.identifier!! }?.let { deviceResourceMap ->
                        dpMap[productKey] = deviceResourceMap.toMutableMap()
                    }
                    it.deviceCommands?.associateBy { it.identifier!! }?.let { deviceCommandMap ->
                        dcMap[productKey] = deviceCommandMap.toMutableMap()
                    }
                    it.deviceEvents?.associateBy { it.identifier!! }?.let { deviceEventMap ->
                        deMap[productKey] = deviceEventMap.toMutableMap()
                    }
                }
            }
            pc = ProfileCacheImpl().apply {
                this.deviceProfileMap = profileMap
                this.devicePropertiesMap = dpMap
                this.deviceCommandMap = dcMap
                this.deviceEventMap = deMap
            }
            return pc!!
        }
    }

    fun forProductKey(productKey: String): DeviceProfile?
    fun all(): MutableList<DeviceProfile>
    fun add(profile: DeviceProfile)
    fun update(profile: DeviceProfile)
    fun removeByProductKey(productKey: String)
    fun deviceProperty(productKey: String, identifier: String): DeviceProperty?
//    fun deviceResourcesByRegex(profileName: String, regex: Pattern): MutableList<DeviceResource>?
    fun deviceCommand(productKey: String?, commandIdentifier: String?): DeviceCommand?
//    fun resourceOperation(profileName: String, deviceResource: String): ResourceOperation?
}

class ProfileCacheImpl : ProfileCache {
    var deviceProfileMap: MutableMap<String, DeviceProfile> = mutableMapOf()
    var devicePropertiesMap: MutableMap<String, MutableMap<String, DeviceProperty?>?> = mutableMapOf()
    var deviceCommandMap: MutableMap<String, MutableMap<String, DeviceCommand?>?> = mutableMapOf()
    var deviceEventMap: MutableMap<String, MutableMap<String, DeviceEvent?>?> = mutableMapOf()
    override fun forProductKey(productKey: String): DeviceProfile? {
        return this.deviceProfileMap[productKey]
    }

    override fun all(): MutableList<DeviceProfile> {
        return this.deviceProfileMap.values.toMutableList()
    }

    override fun add(profile: DeviceProfile) {
        profile.productKey?.let { productKey ->
            this.deviceProfileMap[productKey] = profile
            this.devicePropertiesMap[productKey] = profile.deviceProperties?.associateBy { it.identifier!! }?.toMutableMap()
            this.deviceCommandMap[productKey] = profile.deviceCommands?.associateBy { it.identifier!! }?.toMutableMap()
            this.deviceEventMap[productKey] = profile.deviceEvents?.associateBy { it.identifier!! }?.toMutableMap()
        }
    }

    override fun update(profile: DeviceProfile) {
        removeByProductKey(profile.productKey!!)
        add(profile)
    }

    override fun removeByProductKey(productKey: String) {
        deviceProfileMap[productKey]?.let {
            this.deviceProfileMap.remove(productKey)
            this.devicePropertiesMap.remove(productKey)
            this.deviceCommandMap.remove(productKey)
        }
    }

    override fun deviceProperty(productKey: String, identifier: String): DeviceProperty? {
        return devicePropertiesMap[productKey]?.let {
            it[identifier]
        }
    }

//    override fun deviceResourcesByRegex(profileName: String, regex: Pattern): MutableList<DeviceResource>? {
//        return deviceResourceMap[profileName]?.let { drs ->
//            val res = mutableListOf<DeviceResource>()
//            for (dr in drs.values) {
//                if (dr?.name == regex.toString()) {
//                    res.add(dr)
//                    continue
//                }
//                if (regex.matcher(dr?.name!!).find()) {
//                    res.add(dr)
//                }
//            }
//            res
//        }
//    }

    override fun deviceCommand(productKey: String?, commandIdentifier: String?): DeviceCommand? {
        return deviceCommandMap[productKey]?.let { dcs -> dcs[commandIdentifier] }
    }

//    override fun resourceOperation(profileName: String, deviceResource: String): ResourceOperation? {
//        return deviceCommandMap[profileName]?.let { dcMap ->
//            for (dc in dcMap.values) {
//                dc?.resourceOperations?.let { resourceOperations ->
//                    for (ro in resourceOperations) {
//                        if (ro.deviceResource == deviceResource) {
//                            return ro
//                        }
//                    }
//                }
//            }
//            return null
//        }
//    }


    fun verifyProfileExists(profileName: String): Boolean {
        return deviceProfileMap[profileName]?.let { true } ?: let { false }
    }
}