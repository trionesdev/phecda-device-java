package com.trionesdev.phecda.device.sdk.cache

import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.sdk.cache.DeviceCache.Companion.newDeviceCache
import com.trionesdev.phecda.device.sdk.cache.ProfileCache.Companion.newProfileCache

object Cache {
    fun initCache(instanceName: String, baseServiceName: String, dic: Container) {
        newDeviceCache(mutableListOf(), dic)
        newProfileCache(mutableListOf())
    }

    fun devices(): DeviceCache? {
        return DeviceCache.dc
    }

    fun profiles(): ProfileCache? {
        return ProfileCache.pc
    }
}