package com.trionesdev.phecda.device.bootstrap.args

interface CommonArgs {
    fun overwriteConfig(): Boolean
    fun profile(): String?

    fun configDirectory(): String?
    fun configFileName(): String?
    fun commonConfig(): String?
    fun parse(arguments: Array<String>?)

}