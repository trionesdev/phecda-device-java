package com.trionesdev.phecda.device.bootstrap.args

import com.trionesdev.phecda.device.bootstrap.environement.Variables
import java.text.MessageFormat

class DefaultArgs : CommonArgs {
    companion object {
        var DefaultConfigFile = "configuration.yaml"

        fun withUsage(additionalUsage: String): CommonArgs {
            return DefaultArgs().apply { this.additionalUsage = additionalUsage }
        }
    }

    private var additionalUsage: String? = null
    private var overwriteConfig = false
    private var devMode: Boolean = false
    private var commonConfig = ""
    private var profile = ""
    private var configDir = ""
    private var configFileName: String = DefaultConfigFile

    fun helpCallback() {
        val help = MessageFormat.format(
            "Usage: {0} [options]\n" +
                    "Server Options:\n" +
                    "    -cp, --configProvider        Indicates to use Configuration Provider service at specified URL.\n" +
                    "                                 URL Format: {type}.{protocol}://{host}:{port} ex: consul.http://localhost:8500\n" +
                    "    -cc, --commonConfig          Takes the location where the common configuration is loaded from when\n" +
                    "                                 not using the Configuration Provider\n" +
                    "    -o, --overwrite              Overwrite configuration in provider with local configuration\n" +
                    "                                 *** Use with cation *** Use will clobber existing settings in provider,\n" +
                    "                                 problematic if those settings were edited by hand intentionally\n" +
                    "    -cf, --configFile <name>     Indicates name of the local configuration file. Defaults to configuration.yaml\n" +
                    "    -p, --profile <name>         Indicate configuration profile other than default\n" +
                    "    -cd, --configDir             Specify local configuration directory\n" +
                    "    -r, --registry               Indicates service should use Registry.\n" +
                    "    -rsh, \n" +
                    "     --remoteServiceHosts \n" +
                    "          <host names>           Indicates that the service is running remote from the core EdgeX services and\n" +
                    "                                 to use the listed host names to connect remotely. <host names> contains 3 comma seperated host names seperated by ','.\n" +
                    "                                 1st is the local system host name, 2nd is the remote system host name and 3rd is the WebServer bind host name\n" +
                    "                                 example: -rsh=192.0.1.20,192.0.1.5,localhost\n" +
                    "    -d, --dev                    Indicates service to run in developer mode which causes Host configuration values to be overridden.\n" +
                    "                                 with `localhost`. This is so that it will run with other services running in Docker (aka hybrid mode)\n" +
                    "{1}\n" +
                    "Common Options:\n" +
                    "    -h, --help                   Show this message\n",
            Variables.args[0], additionalUsage
        )
        println(help)
    }

    private fun argsMap(arguments: Array<String>?): MutableMap<String, String> {
        if (arguments.isNullOrEmpty()) {
            return mutableMapOf()
        }
        val res: MutableMap<String, String> = mutableMapOf()
        for (arg in arguments) {
            val argList: List<String> = arg.trim().split("=")
            val key: String = argList[0].replace("-", "", true)
            var value = "true"
            if (argList.size > 1) {
                value = argList[1]
            }
            res[key] = value
        }
        return res
    }

    override fun overwriteConfig(): Boolean {
        return this.overwriteConfig
    }

    override fun profile(): String? {
        return this.profile
    }

    override fun configDirectory(): String? {
        return this.configDir
    }

    override fun configFileName(): String? {
        return this.configFileName
    }

    override fun commonConfig(): String? {
        return this.commonConfig
    }

    override fun parse(arguments: Array<String>?) {
        val configProviderRE = "^--?(cp|configProvider)=?"
        val argsMap = argsMap(arguments)
        Variables.envVars = argsMap

        argsMap["commonConfig"]?.let { this.commonConfig = argsMap["commonConfig"].toString() }
        argsMap["cc"]?.let { this.commonConfig = argsMap["cc"].toString() }
        argsMap["cf"]?.let { this.configFileName = argsMap["cf"].toString() }
        argsMap["configFile"]?.let { this.configFileName = argsMap["configFile"].toString() }
        argsMap["profile"]?.let { this.profile = argsMap["profile"].toString() }
        argsMap["p"]?.let { this.profile = argsMap["p"].toString() }
        argsMap["configDir"]?.let { this.configDir = argsMap["configDir"].toString() }
        argsMap["cd"]?.let { this.configDir = argsMap["cd"].toString() }

    }
}