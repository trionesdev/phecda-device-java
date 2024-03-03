package com.trionesdev.phecda.device.bootstrap.environement

import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log

@Slf4j
class Variables {
    companion object {
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
    }

    private var variables = mutableMapOf<String, String>()

    fun logEnvironmentOverride(name: String, key: String?, value: String?) {
        log.info("Variables override of '{}' by environment variable: {}={}", name, key, value)
    }

}