package com.trionesdev.phecda.device.sdk.provision

import cn.hutool.core.io.resource.ResourceUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.model.Device
import com.trionesdev.phecda.device.contracts.model.DeviceProfile
import com.trionesdev.phecda.device.contracts.model.DeviceService
import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Paths

object Provision {

    enum class FileType {
        YAML,
        JSON,
        OTHER
    }

    private fun getFileType(path: String): FileType {
        return if (path.endsWith(".yaml") || path.endsWith(".yml")) {
            FileType.YAML
        } else if (path.endsWith(".json")) {
            FileType.JSON
        } else {
            FileType.OTHER
        }
    }

    class DevicesYaml {
        var deviceList: MutableList<Device>? = null
    }

    @JvmStatic
    fun loadDevices(path: String, dic: Container) {
        if (path.isBlank()) {
            return
        }
        val serviceName = dic.getInstance(DeviceService::class.java)?.name
        val devices = loadDevicesFromFile(path, serviceName!!)
    }

    private fun loadDevicesFromFile(path: String, serviceName: String): MutableList<Device> {
        val devices = mutableListOf<Device>()
        ResourceUtil.getResource(path).let { resource ->
            val resourcePath = Paths.get(resource.toURI())
            Files.walk(resourcePath, 1)
                .filter { p -> !p.equals(resourcePath) }
                .forEach { p ->
                    processDevices(p.toString(), p.toString(), serviceName)?.forEach { device ->
                        devices.add(device)
                    }
                }
        }
        return devices
    }

    private fun processDevices(path: String, displayPath: String, serviceName: String): MutableList<Device>? {
        val fileType = getFileType(path)
        val devices: MutableList<Device>?
        when (fileType) {
            FileType.YAML -> {
                val devicesYaml = Yaml().loadAs(ResourceUtil.getStream(path), DevicesYaml::class.java)
                devices = devicesYaml.deviceList
            }

            FileType.JSON -> {
                devices =
                    JSON.parseObject(ResourceUtil.getUtf8Reader(path), object : TypeReference<List<Device>>() {}.type)
            }

            FileType.OTHER -> {
                return null
            }
        }
        return devices
    }

    fun loadProfiles(path: String, dic: Container) {
        if (path.isBlank()) {
            return
        }
        loadProfilesFromFile(path)
    }

    private fun loadProfilesFromFile(path: String): MutableList<DeviceProfile> {
        val profiles = mutableListOf<DeviceProfile>()
        ResourceUtil.getResource(path).let { it ->
            val resourcePath = Paths.get(it.toURI())
            Files.walk(resourcePath, 1)
                .filter { p -> !p.equals(resourcePath) }
                .forEach { p ->
                    processProfiles(p.toString())?.let { profile -> profiles.add(profile) }
                }
        }
        return profiles
    }

    private fun processProfiles(path: String): DeviceProfile? {
        val profile: DeviceProfile
        val fileType = getFileType(path)
        when (fileType) {
            FileType.YAML -> {
                val profileMap: MutableMap<String, Any> = Yaml().load(ResourceUtil.getStream(path))
                profile = JSON.parseObject(JSON.toJSONString(profileMap), DeviceProfile::class.java)
            }

            FileType.JSON -> {
                profile = JSON.parseObject(ResourceUtil.getUtf8Reader(path), DeviceProfile::class.java)
            }

            FileType.OTHER -> {
                return null
            }
        }
        return profile
    }

}