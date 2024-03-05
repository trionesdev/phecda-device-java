package com.trionesdev.phecda.device.bootstrap.environement

import cn.hutool.core.collection.ListUtil
import cn.hutool.core.map.MapUtil
import cn.hutool.core.util.StrUtil
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import java.util.*

@Slf4j
class Variables {
    companion object {
        private var bootTimeoutSecondsDefault: Int = 60
        private var bootRetrySecondsDefault: Int = 1
        private var envKeyCommonConfig: String = "PHECDA_COMMON_CONFIG"
        private var envKeyStartupDuration: String = "PHECDA_STARTUP_DURATION"
        private var envKeyStartupInterval: String = "PHECDA_STARTUP_INTERVAL"

        @JvmStatic
        var envKeyConfigDir: String = "PHECDA_CONFIG_DIR"

        @JvmStatic
        var envKeyProfile: String = "PHECDA_PROFILE"

        @JvmStatic
        var envKeyConfigFile: String = "PHECDA_CONFIG_FILE"
        var configPathSeparator: String = "/"
        var configNameSeparator: String = "-"
        var envNameSeparator: String = "_"

        @JvmStatic
        var args: Array<String> = arrayOf()

        @JvmStatic
        var envVars: MutableMap<String, String> = mutableMapOf()

        fun newVariables(): Variables {
            return Variables().apply {
                this.variables = System.getenv()
            }
        }

        private fun logEnvironmentOverride(name: String, key: String?, value: String?) {
            log.info("Variables override of '{}' by environment variable: {}={}", name, key, value)
        }

        fun getStartupInfo(serviceKey: String?): StartupInfo {
            val startup: StartupInfo = StartupInfo().apply {
                this.duration = bootTimeoutSecondsDefault
                this.interval = bootRetrySecondsDefault
            }
            val durationValue = System.getenv(envKeyStartupDuration)
            if (durationValue?.isNotEmpty() == true) {
                logEnvironmentOverride("Startup Duration", envKeyStartupDuration, durationValue)
                startup.duration = durationValue.toInt()
            }

            val intervalValue = System.getenv(envKeyStartupInterval)
            if (intervalValue?.isNotEmpty() == true) {
                logEnvironmentOverride("Startup Interval", envKeyStartupInterval, intervalValue)
                startup.interval = intervalValue.toInt()
            }
            return startup
        }

        fun getConfigDir(configDir: String?): String? {
            var configDirScope = configDir
            val envValue = System.getenv(envKeyConfigDir)
            if (StrUtil.length(envValue) > 0) {
                configDirScope = envValue
                logEnvironmentOverride("-cf/--configDir", envKeyConfigDir, envValue)
            }
            return configDirScope
        }

        fun getProfileDir(profileDir: String?): String? {
            var profileDirScope = profileDir
            val envValue = System.getenv(envKeyProfile)
            if (StrUtil.length(envValue) > 0) {
                profileDirScope = envValue
                logEnvironmentOverride("-cf/--profileDir", envKeyProfile, envValue)
            }
            return profileDirScope
        }

        fun getConfigFileName(configFileName: String?): String? {
            var configFileNameScope = configFileName
            val envValue = System.getenv(envKeyConfigFile)
            if (StrUtil.length(envValue) > 0) {
                configFileNameScope = envValue
                logEnvironmentOverride("-cf/--configFile", envKeyConfigFile, envValue)
            }
            return configFileNameScope
        }

        fun getCommonConfigFileName(commonConfigFileName: String?): String? {
            var commonConfigFileNameScope = commonConfigFileName
            val envValue = System.getenv(envKeyCommonConfig)
            if (StrUtil.isNotBlank(envValue)) {
                commonConfigFileNameScope = envValue
                logEnvironmentOverride("-cc/--commonConfig", envKeyCommonConfig, envValue)
            }
            return commonConfigFileNameScope
        }

    }

    private var variables = mutableMapOf<String, String>()

    fun overrideConfigMapValues(configMap: MutableMap<String, Any>): Int {
        var overrideCount = 0
        val paths: MutableList<String> = buildPaths(configMap)
        val overrideNames = buildOverrideNames(paths)
        for ((key, newValue) in variables) {
            val path = overrideNames[key]
            if (Objects.isNull(path)) {
                continue
            }
            val oldValue = getConfigMapValue(path, configMap)
            setConfigMapValue(path!!, newValue, configMap)
            overrideCount++
            logEnvironmentOverride(path, key, newValue)
        }
        return overrideCount
    }

    fun getConfigMapValue(path: String?, configMap: Map<String, Any>): Any? {
        val value = configMap[path]
        if (Objects.nonNull(value)) {
            return value
        }
        var currentMap = configMap
        path?.split(configPathSeparator)?.let { keys ->
            for (key in keys) {
                val item = currentMap[key]
                if (Objects.isNull(item)) {
                    return null
                }

                if (item !is Map<*, *>) {
                    return item
                }
                val iteMap = item as Map<String, Any>
                currentMap = iteMap
                continue
            }
        }

        return null
    }

    fun setConfigMapValue(path: String, value: Any, configMap: MutableMap<String, Any>) {
        if (Objects.nonNull(configMap[path])) {
            configMap[path] = value
            return
        }
        var currentMap = configMap
        path.split(configPathSeparator).let { keys ->
            for (key in keys) {
                val item = currentMap[key]
                if (item !is Map<*, *>) {
                    currentMap[key] = value
                    return
                }
                currentMap = item as MutableMap<String, Any>
                continue
            }
        }

    }

    fun buildPaths(keyMap: Map<String, Any>): MutableList<String> {
        val paths: MutableList<String> = ListUtil.toList()
        for ((key, value) in keyMap) {
            if (Objects.isNull(value) || value !is Map<*, *>) {
                paths.add(key)
                continue
            }
            val subMap = value as Map<String, Any>
            val subPaths = buildPaths(subMap)
            for (path in subPaths) {
                paths.add("$key/$path")
            }
        }
        return paths
    }

    fun buildOverrideNames(paths: MutableList<String>): Map<String, String> {
        val names: MutableMap<String, String> = MapUtil.newHashMap()
        for (path in paths) {
            names[getOverrideNameFor(path)] = path
        }
        return names
    }

    fun getOverrideNameFor(path: String?): String {
        var override = path?.replace(configPathSeparator, envNameSeparator)
        override = override?.replace(configNameSeparator, envNameSeparator)
        override = override?.uppercase()
        return override ?: ""
    }
}