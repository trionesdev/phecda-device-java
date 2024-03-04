package com.trionesdev.phecda.device.bootstrap.environement

import cn.hutool.core.util.StrUtil
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log

@Slf4j
class Variables {
    companion object {
        var envKeyCommonConfig: String = "PHECDA_COMMON_CONFIG"

        @JvmStatic
        var envKeyConfigDir: String = "PHECDA_CONFIG_DIR"

        @JvmStatic
        var envKeyProfile: String = "PHECDA_PROFILE"

        @JvmStatic
        var envKeyConfigFile: String = "PHECDA_CONFIG_FILE"


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

    }
}