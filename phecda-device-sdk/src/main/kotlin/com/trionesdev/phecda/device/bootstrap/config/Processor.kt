package com.trionesdev.phecda.device.bootstrap.config

import cn.hutool.core.io.resource.ResourceUtil
import com.trionesdev.phecda.device.bootstrap.args.CommonArgs
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.bootstrap.environement.Variables
import com.trionesdev.phecda.device.bootstrap.interfaces.Configuration
import com.trionesdev.phecda.device.bootstrap.startup.Timer
import com.trionesdev.phecda.device.contracts.go.WaitGroup
import org.yaml.snakeyaml.Yaml
import java.nio.file.Paths

class Processor {
    companion object {
        fun newProcessor(args: CommonArgs, envVars: Variables, startupTimer: Timer, dic: Container): Processor {
            return Processor().apply {
                this.args = args
                this.envVars = envVars
                this.startupTimer = startupTimer
                this.dic = dic
            }
        }
    }

    var args: CommonArgs? = null
    var envVars: Variables? = null
    var startupTimer: Timer? = null
    var wg: WaitGroup? = null
    var dic: Container? = null

    fun process(serviceKey: String, serviceConfig: Configuration) {

        val filePath = getConfigFileLocation(args!!)
        val configMap = loadConfigYamlFromFile(filePath)

    }

    fun loadCommonConfigFromFile(configFile: String, serviceConfig: Configuration) {
        val commonConfig = loadConfigYamlFromFile(configFile)

    }

    private fun loadConfigYamlFromFile(yamlFile: String?): Map<String, Any>? {
        return Yaml().load(ResourceUtil.getStream(yamlFile))
    }

    fun getConfigFileLocation(args: CommonArgs): String {
        val configFileName = Variables.getConfigFileName(args.configFileName())
        val configDir = Variables.getConfigDir(args.configDirectory())
        val profileDir = Variables.getProfileDir(args.profile())
        return Paths.get(configDir, profileDir, configFileName).toString()
    }
}